package org.endeavourhealth.pipeline.inbound.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class FilingOutcomeSender {

  @Value("${rabbitmq.targetBaseRoutingKey}")
  private String targetBaseRoutingKey;

  @Value("${rabbitmq.targetExchange}")
  private String targetExchange;

  private final RabbitTemplate rabbitTemplate;
  private static final Logger LOG = LoggerFactory.getLogger(FilingOutcomeSender.class);

  public FilingOutcomeSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendMessage(Message message) {
    Map<String, Object> headers = message.getMessageProperties().getHeaders();
    String filePath = headers.get("source").toString();
    String routingKey = "endeavour-inbound." + targetBaseRoutingKey + "." + filePath.substring(targetBaseRoutingKey.length() + 1);

    try {
      rabbitTemplate.convertAndSend(targetExchange, routingKey, message, getMessagePostProcessorFromHeaders(headers));
      LOG.debug("Message sent to exchange with routing key {}: {} and headers: {}", routingKey, "Filed without error", headers);
    } catch (Exception e) {
      LOG.error("Message not sent: {}", e.getMessage());
      throw e;
    }
  }

  public MessagePostProcessor getMessagePostProcessorFromHeaders(Map<String, Object> headers) {
    return msg -> {
      MessageProperties props = msg.getMessageProperties();
      props.setHeader("datatype", headers.get("datatype"));
      props.setHeader("domain", headers.get("domain"));
      props.setHeader("publisher", headers.get("publisher"));
      props.setHeader("location", headers.get("location"));
      props.setHeader("source", headers.get("source"));
      props.setHeader("category", headers.get("category"));
      props.setContentType("application/json");
      return msg;
    };
  }
}

