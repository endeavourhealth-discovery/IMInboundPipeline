package org.endeavourhealth.pipeline.inbound.service;

import org.endeavourhealth.pipeline.inbound.model.FileStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class S3Service {
  private static final String AWS_ACCESS_KEY_ID = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY_ID")).orElseThrow(() -> new IllegalArgumentException("Env var 'AWS_ACCESS_KEY_ID' is not defined"));
  private static final String AWS_SECRET_ACCESS_KEY = Optional.ofNullable(System.getenv("AWS_SECRET_ACCESS_KEY")).orElseThrow(() -> new IllegalArgumentException("Env var 'AWS_SECRET_ACCESS_KEY' is not defined"));
  private static final String REGION = Optional.ofNullable(System.getenv("REGION")).orElseThrow(() -> new IllegalArgumentException("Env var 'REGION' is not defined"));
  private static final String BUCKET_NAME = Optional.ofNullable(System.getenv("BUCKET_NAME")).orElseThrow(() -> new IllegalArgumentException("Env var 'BUCKET_NAME' is not defined"));

  public InputStream getFile(String fileName) {
    S3Client s3 = getS3Client();
    ResponseInputStream<GetObjectResponse> s3Object = s3.getObject(GetObjectRequest.builder().bucket(BUCKET_NAME).key(fileName).build());
    return s3Object;
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
      System.out.println("Failed to move file from " + source + " to " + destination);
      throw new RuntimeException(e);
    }
  }

  public List<String> getExistingFilesInBucket(Optional<String> prefix) {
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

  private S3Client getS3Client() {
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
    return S3Client.builder().region(Region.of(REGION)).credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
  }

  private String getFileName(String path) {
    return Paths.get(path).getFileName().toString();
  }

}
