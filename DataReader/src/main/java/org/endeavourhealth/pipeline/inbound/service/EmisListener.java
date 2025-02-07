package org.endeavourhealth.pipeline.inbound.service;

import org.endeavourhealth.pipeline.inbound.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EmisListener {

  @RabbitListener(queues = RabbitMQConfig.ORG_QUEUE)
  public void handleEmisMessages(String message) {
    System.out.println("Received EMIS message: " + message);
//    TODO parse JSON
//    TODO get JSLT mapping file based on file name - stored in config
//    TODO convert to storage structure
//    TODO store in db
  }
}
