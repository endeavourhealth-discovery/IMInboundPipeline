package org.endeavourhealth.pipeline.inbound.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.model.FileValidationConfigItem;
import org.endeavourhealth.pipeline.inbound.model.ProcessOrderConfigItem;
import org.endeavourhealth.pipeline.inbound.service.QueueSender;
import org.endeavourhealth.pipeline.inbound.validator.FileValidator;
import org.json.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FileEventListener {

  @Value("classpath:processOrderConfig.json")
  private Resource processOrderConfig;

  @Value("${rabbitmq.targetBaseRoutingKey}")
  private String targetBaseRoutingKey;

  @Value("${rabbitmq.targetExchange}")
  private String targetExchange;

  public FileEventListener(RabbitTemplate rabbitTemplate, FileValidator fileValidator) {
    this.queueSender = new QueueSender(rabbitTemplate);
    this.fileValidator = fileValidator;
  }

  private final QueueSender queueSender;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final FileValidator fileValidator;
  private static final String AWS_ACCESS_KEY_ID = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY_ID")).orElseThrow(() -> new IllegalArgumentException("Env var 'AWS_ACCESS_KEY_ID' is not defined"));
  private static final String AWS_SECRET_ACCESS_KEY = Optional.ofNullable(System.getenv("AWS_SECRET_ACCESS_KEY")).orElseThrow(() -> new IllegalArgumentException("Env var 'AWS_SECRET_ACCESS_KEY' is not defined"));
  private static final String REGION = Optional.ofNullable(System.getenv("REGION")).orElseThrow(() -> new IllegalArgumentException("Env var 'REGION' is not defined"));
  private static final String BUCKET_NAME = Optional.ofNullable(System.getenv("BUCKET_NAME")).orElseThrow(() -> new IllegalArgumentException("Env var 'BUCKET_NAME' is not defined"));

  @RabbitListener(queues = "#{rabbitMQConfig.getSourceQueue()}")
  public void handleFileEvent(Message message) throws IOException {
//    resetFiles(); TODO: Delete when finihsed with testing
    System.out.println("Received file event: " + message);
    List<ProcessOrderConfigItem> processOrderConfigPOJO = getProcessOrderConfig();
    Optional<ProcessOrderConfigItem> found = processOrderConfigPOJO.stream().filter(configItem -> configItem.getOrg().equalsIgnoreCase(targetBaseRoutingKey)).findFirst();
    if (found.isPresent()) {
      List<String> orderedList = found.get().getOrderedList();
      Set<String> filesInBucket = getExistingFilesInBucket(Optional.of(targetBaseRoutingKey));
      int index = 0;
      boolean isValidFile = true;
      while (index < orderedList.size() && filesInBucket.contains(orderedList.get(index)) && isValidFile) {
        String filePath = orderedList.get(index);
        System.out.println("Processing file: " + filePath);
        InputStream stream = getFile(filePath);
        List<String> headers = getHeaders(stream);
        if (fileValidator.isValidFile(filePath, headers)) {
//        moveFileFromTo(filePath, FileStageFolder.UPLOADED, FileStageFolder.QUEUING); TODO: Uncomment when finished with testing
        populateQueue(stream, filePath, message);
        index++;
        System.out.println("Queued all lines successfully");
//        moveFileFromTo(filePath, FileStageFolder.QUEUING, FileStageFolder.FILING); TODO: Uncomment when finished with testing
        } else {
          isValidFile = false;
          System.out.println("Invalid file: " + filePath);
        }
      }
    }
  }

  private void populateQueue(InputStream inputStream, String fileName, Message message) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      String[] headers = null;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        if (headers == null) {
          headers = values;
        } else {
          JSONObject jsonObject = new JSONObject();
          for (int i = 0; i < headers.length; i++) {
            jsonObject.put(headers[i], values[i]);
          }
          System.out.println(line + " -> " + jsonObject.toString());
          queueSender.sendMessage(targetExchange, targetBaseRoutingKey, jsonObject.toString(), fileName, message);
        }
      }
    }
  }

  private Set<String> getExistingFilesInBucket(Optional<String> prefix) {
    Set<String> filesInBucket = new HashSet<>();
    S3Client s3 = getS3Client();

    ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(BUCKET_NAME).prefix(prefix.orElse("")).build();
    ListObjectsV2Iterable response = s3.listObjectsV2Paginator(request);

    for (ListObjectsV2Response page : response) {
      page.contents().forEach((S3Object object) -> {
        filesInBucket.add(object.key());
      });
    }

    return filesInBucket;
  }

  private InputStream getFile(String fileName) {
    S3Client s3 = getS3Client();

    ResponseInputStream<GetObjectResponse> s3Object = s3.getObject(GetObjectRequest.builder()
      .bucket(BUCKET_NAME)
      .key(fileName)
      .build());

    return s3Object;
  }

  private List<ProcessOrderConfigItem> getProcessOrderConfig() throws IOException {
    try {
      return objectMapper.readValue(
        processOrderConfig.getInputStream(),
        new TypeReference<List<ProcessOrderConfigItem>>() {
        }
      );
    } catch (IOException e) {
      e.printStackTrace();
      return List.of();
    }
  }

  private void moveFileFromTo(String filePath, FileStageFolder from, FileStageFolder to) {
    String fileName = getFileName(filePath);
    String source = from == FileStageFolder.UPLOADED ? targetBaseRoutingKey + "/" + fileName : targetBaseRoutingKey + "/" + from + "/" + fileName;
    String destination = to == FileStageFolder.UPLOADED ? targetBaseRoutingKey + "/" + fileName : targetBaseRoutingKey + "/" + to + "/" + fileName;
    try {
      S3Client s3 = getS3Client();
      CopyObjectRequest copyRequest = CopyObjectRequest.builder()
        .sourceBucket(BUCKET_NAME)
        .sourceKey(source)
        .destinationBucket(BUCKET_NAME)
        .destinationKey(destination)
        .build();
      s3.copyObject(copyRequest);

      DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
        .bucket(BUCKET_NAME)
        .key(source)
        .build();

      s3.deleteObject(deleteRequest);
      s3.close();
    } catch (Exception e) {
      System.out.println("Failed to move file from " + source + " to " + destination);
      throw new RuntimeException(e);
    }
  }

  enum FileStageFolder {
    UPLOADED,
    QUEUING,
    TRANSFORMING,
    FILING,
    FILED,
  }

  private S3Client getS3Client() {
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);

    return S3Client.builder()
      .region(Region.of(REGION))
      .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
      .build();
  }

  void resetFiles() {
    Set<String> filingFiles = getExistingFilesInBucket(Optional.of(targetBaseRoutingKey + "/" + FileStageFolder.FILING));
    for (String filingFile : filingFiles) {
      moveFileFromTo(filingFile, FileStageFolder.FILING, FileStageFolder.UPLOADED);
    }

    Set<String> queueingFiles = getExistingFilesInBucket(Optional.of(targetBaseRoutingKey + "/" + FileStageFolder.QUEUING));
    for (String queuingFile : queueingFiles) {
      moveFileFromTo(queuingFile, FileStageFolder.QUEUING, FileStageFolder.UPLOADED);
    }
    System.out.println("Files have been moved to UPLOADED");
  }

  private String getFileName(String path) {
    return Paths.get(path).getFileName().toString();
  }

  private List<String> getHeaders(InputStream inputStream) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line = br.readLine();
      if (line != null) {
        return Arrays.asList(line.split(","));
      }
    }
    return Collections.emptyList();
  }
}
