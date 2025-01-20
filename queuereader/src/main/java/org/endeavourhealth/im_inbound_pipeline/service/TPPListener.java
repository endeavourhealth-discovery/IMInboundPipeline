package org.endeavourhealth.im_inbound_pipeline.service;

import org.endeavourhealth.im_inbound_pipeline.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class TPPListener {

  @RabbitListener(queues = RabbitMQConfig.TPP_QUEUE)
  public void handleTppMessages(String message) {
    System.out.println("Received TPP message: " + message);
  }
}