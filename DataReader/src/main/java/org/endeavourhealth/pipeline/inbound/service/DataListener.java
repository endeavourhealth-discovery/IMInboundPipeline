package org.endeavourhealth.pipeline.inbound.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class DataListener {

  @RabbitListener(queues = "#{rabbitMQConfig.getQueue()}")
  public void handleDataMessages(String message) {
    System.out.println("Received data message: " + message);
//    TODO parse JSON
//    TODO get JSLT mapping file based on file name - stored in config
//    TODO convert to storage structure
//    TODO store in db
  }
}
