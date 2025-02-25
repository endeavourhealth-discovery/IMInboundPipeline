package org.endeavourhealth.pipeline.inbound.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.model.ProcessOrderConfigItem;
import org.endeavourhealth.pipeline.inbound.service.QueueSender;
import org.endeavourhealth.pipeline.inbound.validator.FileValidator;
import org.json.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.*;
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
    System.out.println("Received file event: " + message);
    List<String> filesInBucket = getExistingFilesInBucket(Optional.of(targetBaseRoutingKey));
    boolean areAllFilesInBucket = fileValidator.areAllFilesInBucket(targetBaseRoutingKey, filesInBucket);

    if (areAllFilesInBucket) {
      List<ProcessOrderConfigItem> processOrderConfigPOJO = getProcessOrderConfig();
      Optional<ProcessOrderConfigItem> found = processOrderConfigPOJO.stream().filter(configItem -> configItem.getOrg().equalsIgnoreCase(targetBaseRoutingKey)).findFirst();
      if (found.isPresent()) for (String pattern : found.get().getOrderedList()) {
        Optional<String> filePath = filesInBucket.stream().filter(file -> file.matches(pattern)).findFirst();
        if (filePath.isPresent()) {
          System.out.println("Processing file: " + filePath.get());
          InputStream stream = getFile(filePath.get());
          moveFileFromTo(filePath.get(), FileStageFolder.UPLOADED, FileStageFolder.QUEUING);
          populateQueue(stream, filePath.get());
          moveFileFromTo(filePath.get(), FileStageFolder.QUEUING, FileStageFolder.FILING);
        }
      }
    }
  }

  private void populateQueue(InputStream inputStream, String filePath) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line = br.readLine();
      List<String> headers = Arrays.asList(line.split(","));

      if (fileValidator.isValidFile(filePath, headers)) {
        System.out.println("Validated file: " + filePath);
        String routingKey = "endeavour-inbound." + targetBaseRoutingKey + "." + filePath.substring(targetBaseRoutingKey.length() + 1);
        MessagePostProcessor messageHeaders = getHeaders(targetBaseRoutingKey, filePath);

        while ((line = br.readLine()) != null) {
          String[] values = line.split(",");
          JSONObject jsonObject = new JSONObject();
          for (int i = 0; i < headers.size(); i++) {
            jsonObject.put(headers.get(i), values[i]);
          }
          queueSender.sendMessage(targetExchange, routingKey, jsonObject.toString(), messageHeaders);
        }
        System.out.println("Queued all lines successfully");
      } else {
        System.out.println("Invalid file: " + filePath);
      }
    }
  }

  private List<String> getExistingFilesInBucket(Optional<String> prefix) {
    List<String> filesInBucket = new ArrayList<>();
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

    ResponseInputStream<GetObjectResponse> s3Object = s3.getObject(GetObjectRequest.builder().bucket(BUCKET_NAME).key(fileName).build());

    return s3Object;
  }

  private List<ProcessOrderConfigItem> getProcessOrderConfig() throws IOException {
    try {
      return objectMapper.readValue(processOrderConfig.getInputStream(), new TypeReference<List<ProcessOrderConfigItem>>() {
      });
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
      CopyObjectRequest copyRequest = CopyObjectRequest.builder().sourceBucket(BUCKET_NAME).sourceKey(source).destinationBucket(BUCKET_NAME).destinationKey(destination).build();
      s3.copyObject(copyRequest);

      DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(source).build();

      s3.deleteObject(deleteRequest);
      s3.close();
    } catch (Exception e) {
      System.out.println("Failed to move file from " + source + " to " + destination);
      throw new RuntimeException(e);
    }
  }

  enum FileStageFolder {
    UPLOADED, QUEUING, TRANSFORMING, FILING, FILED,
  }

  private S3Client getS3Client() {
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);

    return S3Client.builder().region(Region.of(REGION)).credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
  }

  void resetFiles() {
    List<String> filingFiles = getExistingFilesInBucket(Optional.of(targetBaseRoutingKey + "/" + FileStageFolder.FILING));
    for (String filingFile : filingFiles) {
      moveFileFromTo(filingFile, FileStageFolder.FILING, FileStageFolder.UPLOADED);
    }

    List<String> queueingFiles = getExistingFilesInBucket(Optional.of(targetBaseRoutingKey + "/" + FileStageFolder.QUEUING));
    for (String queuingFile : queueingFiles) {
      moveFileFromTo(queuingFile, FileStageFolder.QUEUING, FileStageFolder.UPLOADED);
    }
    System.out.println("Files have been moved to UPLOADED");
  }

  private String getFileName(String path) {
    return Paths.get(path).getFileName().toString();
  }

  public MessagePostProcessor getHeaders(String targetBaseRoutingKey, String fileName) {
    String[] parts = fileName.split("_");
    if (parts.length >= 4) {
      String domain = parts[2];
      String datatype = parts[3];

      return msg -> {
        MessageProperties props = msg.getMessageProperties();
        props.setHeader("datatype", datatype);
        props.setHeader("domain", domain);
        props.setHeader("publisher", targetBaseRoutingKey);
        props.setHeader("location", "S3");
        props.setHeader("source", fileName);
        return msg;
      };
    }
    return null;
  }
}
