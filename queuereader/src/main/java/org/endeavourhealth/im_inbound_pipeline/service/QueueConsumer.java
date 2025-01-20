package org.endeavourhealth.im_inbound_pipeline.service;

import org.endeavourhealth.im_inbound_pipeline.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

  @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
  public void listen(String message) {
    System.out.println("Received message: " + message);
    // TODO: Process the message
  }
}
