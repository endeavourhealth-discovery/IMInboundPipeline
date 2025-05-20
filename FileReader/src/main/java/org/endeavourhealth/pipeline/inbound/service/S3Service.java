package org.endeavourhealth.pipeline.inbound.service;

import org.endeavourhealth.pipeline.inbound.model.FileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class S3Service {

  private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
  private static final String AWS_ACCESS_KEY_ID = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY_ID")).orElse("test");
  private static final String AWS_SECRET_ACCESS_KEY = Optional.ofNullable(System.getenv("AWS_SECRET_ACCESS_KEY")).orElse("test");
  private static final String REGION = Optional.ofNullable(System.getenv("REGION")).orElse("test");
  private static final String BUCKET_NAME = Optional.ofNullable(System.getenv("BUCKET_NAME")).orElse("test");

  public InputStream getFile(String fileName) {
    S3Client s3 = getS3Client();
    return s3.getObject(GetObjectRequest.builder().bucket(BUCKET_NAME).key(fileName).build());
  }

  public int getFileLineCount(String path) throws IOException {
    InputStream fileStream = getFile(path);
    int lines = 0;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(fileStream))) {
      while (br.readLine() != null) {
        lines++;
      }
    } catch (IOException e) {
      LOG.error(e.getMessage());
      throw e;
    }
    return lines - 1;
  }

  public void moveFileFromTo(String filePath, FileStatus from, FileStatus to, String targetBaseRoutingKey) {
    String fileName = getFileName(filePath);
    String source = from == FileStatus.UPLOADED ? targetBaseRoutingKey + "/" + fileName : targetBaseRoutingKey + "/" + from + "/" + fileName;
    String destination = to == FileStatus.UPLOADED ? targetBaseRoutingKey + "/" + fileName : targetBaseRoutingKey + "/" + to + "/" + fileName;
    try {
      S3Client s3 = getS3Client();
      CopyObjectRequest copyRequest = CopyObjectRequest.builder().sourceBucket(BUCKET_NAME).sourceKey(source).destinationBucket(BUCKET_NAME).destinationKey(destination).build();
      s3.copyObject(copyRequest);

      DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(source).build();
      s3.deleteObject(deleteRequest);
      s3.close();
    } catch (Exception e) {
      LOG.error("Failed to move file from {} to {}", source, destination);
      LOG.error(e.getMessage());
      System.exit(1);
    }
  }

  public List<String> getExistingFilesInBucket(Optional<String> prefix) {
    List<String> filesInBucket = new ArrayList<>();
    S3Client s3 = getS3Client();

    ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(BUCKET_NAME).prefix(prefix.orElse("")).build();
    ListObjectsV2Iterable response = s3.listObjectsV2Paginator(request);

    for (ListObjectsV2Response page : response) {
      page.contents().forEach((S3Object object) ->
        filesInBucket.add(object.key()));
    }

    return filesInBucket;
  }


  public boolean hasFilesInFailed(Optional<String> prefix) {
    S3Client s3 = getS3Client();
    ListObjectsV2Request request = ListObjectsV2Request.builder()
      .bucket(BUCKET_NAME)
      .prefix(prefix.orElse(""))
      .maxKeys(1)
      .build();

    ListObjectsV2Response response = s3.listObjectsV2(request);
    return response.hasContents();
  }

  private String getFileName(String path) {
    return Paths.get(path).getFileName().toString();
  }

  private S3Client getS3Client() {
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
    return S3Client.builder().region(Region.of(REGION)).credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
  }
}
