package org.endeavourhealth.im_inbound_pipeline.service;

import org.endeavourhealth.im_inbound_pipeline.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class EmisListener {

  @RabbitListener(queues = RabbitMQConfig.EMIS_QUEUE)
  public void handleEmisMessages(String message) {
    System.out.println("Received EMIS message: " + message);
  }
}
