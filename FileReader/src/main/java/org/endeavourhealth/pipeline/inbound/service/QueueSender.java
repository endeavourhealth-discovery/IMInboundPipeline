package org.endeavourhealth.pipeline.inbound.service;

import org.endeavourhealth.pipeline.inbound.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueSender {

  private final RabbitTemplate rabbitTemplate;

  public QueueSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendMessage(String exchange, String baseRoutingKey, String message, String fileName) {
    String routingKey = "endeavour-inbound." + baseRoutingKey + "." + fileName;
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
    System.out.println("Message sent to exchange with routing key " + routingKey + ": " + message);
  }
}
