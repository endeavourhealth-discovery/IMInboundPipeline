package org.endeavourhealth.pipeline.inbound.service;

import org.endeavourhealth.pipeline.inbound.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class QueueSender {

  private final RabbitTemplate rabbitTemplate;

  public QueueSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendMessage(String exchange, String baseRoutingKey, String message, String fileName, Message fileMessage) {
    String routingKey = "endeavour-inbound." + baseRoutingKey + "." + fileName;
    rabbitTemplate.convertAndSend(exchange, routingKey, message, getHeaders(fileMessage));
    System.out.println("Message sent to exchange with routing key " + routingKey + ": " + message);
  }

  public MessagePostProcessor getHeaders(Message message) {
    Map<String, Object> headers = message.getMessageProperties().getHeaders();
    return msg -> {
      MessageProperties props = msg.getMessageProperties();
      props.setHeader("datatype", headers.get("datatype"));
      props.setHeader("domain", headers.get("domain"));
      props.setHeader("publisher", headers.get("publisher"));
      props.setHeader("location", headers.get("location"));
      props.setHeader("source", headers.get("source"));
      return msg;
    };
  }
}
