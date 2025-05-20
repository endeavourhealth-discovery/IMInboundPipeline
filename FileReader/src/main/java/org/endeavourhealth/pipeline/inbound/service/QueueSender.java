package org.endeavourhealth.pipeline.inbound.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.endeavourhealth.pipeline.inbound.utils.Utils;
import org.endeavourhealth.pipeline.inbound.model.Category;
import org.endeavourhealth.pipeline.inbound.validator.FileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class QueueSender {

  private final RabbitTemplate rabbitTemplate;
  private final FileValidator fileValidator;
  private static final Logger LOG = LoggerFactory.getLogger(QueueSender.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final Utils utils = new Utils();

  public QueueSender(RabbitTemplate rabbitTemplate, FileValidator fileValidator) {
    this.rabbitTemplate = rabbitTemplate;
    this.fileValidator = fileValidator;
  }

  public boolean sendMessage(String exchange, String routingKey, String message, MessagePostProcessor headers) {
    try {
      rabbitTemplate.convertAndSend(exchange, routingKey, message, headers);
      LOG.debug("Message sent to exchange with routing key {}: {} and headers: {}", routingKey, message, headers);
      return true;
    } catch (Exception e) {
      LOG.error("Message not sent: {}", e.getMessage());
      throw e;
    }
  }

  public int populateQueue(InputStream inputStream, String filePath, String targetBaseRoutingKey, String targetExchange, Category category, int maxRetries, int retryWait) throws Exception {
    int messageCount = 0;
    char delimiter = Utils.getSeparatorFromFileName(filePath);

    CSVFormat csvFormat = CSVFormat.Builder.create()
      .setDelimiter(delimiter)
      .setHeader()
      .setSkipHeaderRecord(true)
      .build();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)); CSVParser csvParser = new CSVParser(br, csvFormat)) {
      List<String> headers = csvParser.getHeaderNames();
      if (fileValidator.isValidFile(filePath, headers)) {
        LOG.debug("Validated file: {}", filePath);
        String routingKey = "endeavour-inbound." + targetBaseRoutingKey + "." + filePath.substring(targetBaseRoutingKey.length() + 1);
        MessagePostProcessor messageHeaders = getHeaders(targetBaseRoutingKey, filePath, category, messageCount + 1, true);
        for (CSVRecord record : csvParser) {
          LOG.debug("Row: {}", record);
          ObjectNode jsonObject = objectMapper.createObjectNode();
          for (String header : headers) {
            jsonObject.put(header, record.get(header));
          }
          LOG.debug("JSON: {}", jsonObject);
          boolean messageSent = Boolean.TRUE.equals(utils.executeWithRetry(() -> sendMessage(targetExchange, routingKey, jsonObject.toString(), messageHeaders), maxRetries, retryWait));
          if (!messageSent) throw new Exception("Message not sent");
          messageCount++;
        }
      } else {
        throw new Exception("Invalid file: " + filePath);
      }
    } catch (Exception e) {
      LOG.error("Failed to populate queue: {}", e.getMessage());
      throw e;
    }
    return messageCount;
  }

  public MessagePostProcessor getHeaders(String targetBaseRoutingKey, String fileName, Category category, int lineCount, boolean isNotEOF) {
    String[] parts = fileName.split("_");
    if (parts.length >= 4) {
      String domain = parts[2];
      String datatype = parts[3];

      return msg -> {
        MessageProperties props = msg.getMessageProperties();
        props.setHeader("datatype", datatype);
        props.setHeader("domain", domain);
        props.setHeader("publisher", targetBaseRoutingKey);
        props.setHeader("location", lineCount);
        props.setHeader("source", fileName);
        props.setHeader("category", category.toString());
        if (isNotEOF)
          props.setContentType("application/json");
        return msg;
      };
    }
    return null;
  }

  public void sendEOFMessage(String filePath, String targetBaseRoutingKey, String targetExchange, Category category) {
    String routingKey = "endeavour-inbound." + targetBaseRoutingKey + "." + filePath.substring(targetBaseRoutingKey.length() + 1);
    MessagePostProcessor messageHeaders = getHeaders(targetBaseRoutingKey, filePath, category, -1, false);
    sendMessage(targetExchange, routingKey, "EOF", messageHeaders);
  }
}
