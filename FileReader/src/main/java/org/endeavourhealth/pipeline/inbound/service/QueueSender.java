package org.endeavourhealth.pipeline.inbound.service;

import org.endeavourhealth.pipeline.inbound.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class QueueSender {

  private final RabbitTemplate rabbitTemplate;

  public QueueSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendMessage(String exchange, String routingKey, String message, MessagePostProcessor headers) {
    rabbitTemplate.convertAndSend(exchange, routingKey, message, headers);
    System.out.println("Message sent to exchange with routing key " + routingKey + ": " + message + " and headers: " + headers);
  }

}
