package org.endeavourhealth.im_inbound_pipeline.service;

import org.endeavourhealth.im_inbound_pipeline.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueSender {

  private final RabbitTemplate rabbitTemplate;

  public QueueSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendMessage(String message) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.QUEUE_NAME, message);
    System.out.println("Sent message: " + message);
  }
}
