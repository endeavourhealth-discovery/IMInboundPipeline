package org.endeavourhealth.im_inbound_pipeline.service;

import org.endeavourhealth.im_inbound_pipeline.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BulkListener {

  @RabbitListener(queues = RabbitMQConfig.BULK_QUEUE)
  public void handleBulkMessages(String message) {
    System.out.println("Received BULK message: " + message);
  }
}