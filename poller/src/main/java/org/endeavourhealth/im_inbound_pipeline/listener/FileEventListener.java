package org.endeavourhealth.im_inbound_pipeline.listener;

import org.endeavourhealth.im_inbound_pipeline.config.RabbitMQConfig;
import org.endeavourhealth.im_inbound_pipeline.service.QueueSender;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FileEventListener {

  private final RabbitTemplate rabbitTemplate = new RabbitTemplate();
  private final QueueSender queueSender = new QueueSender(rabbitTemplate);

  private static final String AWS_ACCESS_KEY_ID = System.getenv("AWS_ACCESS_KEY_ID");
  private static final String AWS_SECRET_ACCESS_KEY = System.getenv("AWS_SECRET_ACCESS_KEY");
//  private static final String DIRECTORY_URL = System.getenv("DIRECTORY_URL");
  private static final String REGION = System.getenv("REGION");
  private static final String BUCKET_NAME = System.getenv("BUCKET_NAME");
//  private static final String OBJECT_KEY = System.getenv("OBJECT_KEY");
  private static final String PREFIX = System.getenv("PREFIX");

  @RabbitListener(queues = "#{rabbitMQConfig.getQueue()}")
  public void handleFileEvent(String message) throws IOException {
    System.out.println("Received file event: " + message);
    List<String> orderedList = new ArrayList<>(); // ordered list in config
    Set<String> filesInBucket = getExistingFilesInBucket();

    int index = 0;

    while (index < orderedList.size() && filesInBucket.contains(orderedList.get(index))) {
      String fileName = orderedList.get(index);
      InputStream stream = getFile(fileName);
      populateQueue(stream, fileName);
      index++;
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
          queueSender.sendMessage(line, fileName);
        }
      }
    }
  }

  private Set<String> getExistingFilesInBucket() {
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);

    Set<String> filesInBucket = new HashSet<>();
    S3Client s3 = S3Client.builder()
      .region(Region.of(REGION))
      .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
      .build();

    ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(BUCKET_NAME).build();
    ListObjectsV2Iterable response = s3.listObjectsV2Paginator(request);

    for (ListObjectsV2Response page : response) {
      page.contents().forEach((S3Object object) -> {
        filesInBucket.add(object.key());
        System.out.println(object.key());
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

  public void test() {
    System.out.println("Starting test");
    System.out.println(AWS_ACCESS_KEY_ID);
    System.out.println(AWS_SECRET_ACCESS_KEY);
    getExistingFilesInBucket();
  }
}
