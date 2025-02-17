package org.endeavourhealth.pipeline.inbound.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.model.FileValidationConfigItem;
import org.endeavourhealth.pipeline.inbound.model.ProcessOrderConfigItem;
import org.endeavourhealth.pipeline.inbound.service.QueueSender;
import org.json.JSONObject;
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
import java.util.*;

@Service
public class FileEventListener {

  @Value("classpath:processOrderConfig.json")
  private Resource processOrderConfig;

  @Value("${rabbitmq.targetBaseRoutingKey}")
  private String targetBaseRoutingKey;

  @Value("${rabbitmq.targetExchange}")
  private String targetExchange;

  public FileEventListener(RabbitTemplate rabbitTemplate) {
    this.queueSender = new QueueSender(rabbitTemplate);
  }

  private final QueueSender queueSender;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String AWS_ACCESS_KEY_ID = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY_ID")).orElseThrow(() -> new IllegalArgumentException("Env var 'AWS_ACCESS_KEY_ID' is not defined"));
  private static final String AWS_SECRET_ACCESS_KEY = Optional.ofNullable(System.getenv("AWS_SECRET_ACCESS_KEY")).orElseThrow(() -> new IllegalArgumentException("Env var 'AWS_SECRET_ACCESS_KEY' is not defined"));
  private static final String REGION = Optional.ofNullable(System.getenv("REGION")).orElseThrow(() -> new IllegalArgumentException("Env var 'REGION' is not defined"));
  private static final String BUCKET_NAME = Optional.ofNullable(System.getenv("BUCKET_NAME")).orElseThrow(() -> new IllegalArgumentException("Env var 'BUCKET_NAME' is not defined"));

  @RabbitListener(queues = "#{rabbitMQConfig.getSourceQueue()}")
  public void handleFileEvent(String message) throws IOException {
    System.out.println("Received file event: " + message);
    List<ProcessOrderConfigItem> processOrderConfigPOJO = getProcessOrderConfig();
    Optional<ProcessOrderConfigItem> found = processOrderConfigPOJO.stream().filter(configItem -> configItem.getOrg().equalsIgnoreCase(targetBaseRoutingKey)).findFirst();
    if (found.isPresent()) {
      List<String> orderedList = found.get().getOrderedList(); // ordered list in config
      Set<String> filesInBucket = getExistingFilesInBucket(Optional.of(targetBaseRoutingKey));

      int index = 0;

      while (index < orderedList.size() && filesInBucket.contains(orderedList.get(index))) {
        System.out.println("Processing file: " + orderedList.get(index));
        String fileName = orderedList.get(index);
        InputStream stream = getFile(fileName);
        populateQueue(stream, fileName);
        index++;
        System.out.println("Processed file: " + fileName);
        System.out.println("\n");
      }
    }

  }

  private void populateQueue(InputStream inputStream, String fileName) throws IOException {
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
          queueSender.sendMessage(targetExchange, targetBaseRoutingKey, line, fileName);
        }
      }
    }
  }

  private Set<String> getExistingFilesInBucket(Optional<String> prefix) {
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);

    Set<String> filesInBucket = new HashSet<>();
    S3Client s3 = S3Client.builder()
      .region(Region.of(REGION))
      .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
      .build();

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
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);

    S3Client s3 = S3Client.builder()
      .region(Region.of(REGION))
      .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
      .build();

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
}
