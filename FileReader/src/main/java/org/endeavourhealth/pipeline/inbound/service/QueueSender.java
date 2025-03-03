package org.endeavourhealth.pipeline.inbound.service;

import org.endeavourhealth.pipeline.inbound.config.RabbitMQConfig;
import org.endeavourhealth.pipeline.inbound.model.FileStatus;
import org.endeavourhealth.pipeline.inbound.validator.FileValidator;
import org.json.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class QueueSender {

  private final RabbitTemplate rabbitTemplate;
  private final FileValidator fileValidator;

  public QueueSender(RabbitTemplate rabbitTemplate, FileValidator fileValidator) {
    this.rabbitTemplate = rabbitTemplate;
    this.fileValidator = fileValidator;
  }

  public void sendMessage(String exchange, String routingKey, String message, MessagePostProcessor headers) {
    rabbitTemplate.convertAndSend(exchange, routingKey, message, headers);
    System.out.println("Message sent to exchange with routing key " + routingKey + ": " + message + " and headers: " + headers);
  }

  public void populateQueue(InputStream inputStream, String filePath, String targetBaseRoutingKey, String targetExchange) throws Exception {
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
          sendMessage(targetExchange, routingKey, jsonObject.toString(), messageHeaders);
        }
        System.out.println("Queued all lines successfully");
      } else {
        throw new Exception("Invalid file: " + filePath);
      }
    }
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
