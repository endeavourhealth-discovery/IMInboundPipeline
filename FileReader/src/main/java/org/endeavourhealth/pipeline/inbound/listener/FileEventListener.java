package org.endeavourhealth.pipeline.inbound.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.model.Category;
import org.endeavourhealth.pipeline.inbound.model.FileStatus;
import org.endeavourhealth.pipeline.inbound.model.ProcessOrderConfig;
import org.endeavourhealth.pipeline.inbound.model.ProcessOrderFileItem;
import org.endeavourhealth.pipeline.inbound.service.QueueSender;
import org.endeavourhealth.pipeline.inbound.service.S3Service;
import org.endeavourhealth.pipeline.inbound.validator.FileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class FileEventListener {

  @Value("classpath:processOrderConfig.json")
  private Resource processOrderConfig;

  @Value("${rabbitmq.targetBaseRoutingKey}")
  private String targetBaseRoutingKey;

  @Value("${rabbitmq.targetExchange}")
  private String targetExchange;

  @Value("${queueSender.maxRetries:3}")
  private int maxRetries;

  @Value("${queueSender.retryWait:3}")
  private int retryWait;

  public FileEventListener(RabbitTemplate rabbitTemplate, FileValidator fileValidator, S3Service s3Service) {
    this.queueSender = new QueueSender(rabbitTemplate, fileValidator);
    this.fileValidator = fileValidator;
    this.s3Service = s3Service;
  }

  private final QueueSender queueSender;
  private final S3Service s3Service;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final FileValidator fileValidator;
  private static final Logger LOG = LoggerFactory.getLogger(FileEventListener.class);

  @RabbitListener(queues = "#{rabbitMQConfig.getSourceQueue()}")
  public void handleFileEvent(Message message) throws Exception {
    LOG.info("Received file event: {}", message);
    List<String> filesInBucket = s3Service.getExistingFilesInBucket(Optional.of(targetBaseRoutingKey));
    if (fileValidator.areAllFilesInBucket(targetBaseRoutingKey, filesInBucket)) {
      for (ProcessOrderFileItem fileItem : getOrderedList()) {
        Optional<String> filePath = filesInBucket.stream().filter(file -> file.matches(fileItem.getNamePattern())).findFirst();
        if (filePath.isPresent()) {
          processFile(filePath.get(), fileItem.getCategory());
        } else {
          LOG.warn("No matching file in bucket: {}", fileItem.getNamePattern());
        }
      }
    } else {
      LOG.warn("Files are missing in bucket");
    }
  }

  private boolean processFile(String filePath, Category category) throws Exception {
    LOG.info("Processing file: {}", filePath);
    int lineCount = s3Service.getFileLineCount(filePath);
    InputStream stream = s3Service.getFile(filePath);
    s3Service.moveFileFromTo(filePath, FileStatus.UPLOADED, FileStatus.QUEUING, targetBaseRoutingKey);
    try {
      int messageCount = queueSender.populateQueue(stream, filePath, targetBaseRoutingKey, targetExchange, category, maxRetries, retryWait);
      if (lineCount != messageCount) {
        throw new Exception("Line count mismatch");
      }
      LOG.info("Queued {} out of {} lines", messageCount, lineCount);
      s3Service.moveFileFromTo(filePath, FileStatus.QUEUING, FileStatus.FILING, targetBaseRoutingKey);
    } catch (Exception e) {
      s3Service.moveFileFromTo(filePath, FileStatus.QUEUING, FileStatus.FAILED, targetBaseRoutingKey);
      System.exit(1);
    }

    return true;
  }

  public List<ProcessOrderFileItem> getOrderedList() throws IOException {
    List<ProcessOrderConfig> processOrderConfigPOJO = objectMapper.readValue(processOrderConfig.getInputStream(), new TypeReference<>() {
    });
    Optional<ProcessOrderConfig> found = processOrderConfigPOJO.stream().filter(configItem -> configItem.getOrg().equalsIgnoreCase(targetBaseRoutingKey)).findFirst();
    if (found.isEmpty()) {
      throw new IllegalStateException("No process order config found");
    }
    return found.get().getOrderedList();
  }
}
