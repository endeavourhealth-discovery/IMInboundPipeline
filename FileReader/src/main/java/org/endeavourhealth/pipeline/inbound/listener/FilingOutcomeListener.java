package org.endeavourhealth.pipeline.inbound.listener;

import org.endeavourhealth.pipeline.inbound.model.FileStatus;
import org.endeavourhealth.pipeline.inbound.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class FilingOutcomeListener {

  @Value("${rabbitmq.targetBaseRoutingKey}")
  private String targetBaseRoutingKey;

  private static final Logger LOG = LoggerFactory.getLogger(FilingOutcomeListener.class);
  private final S3Service s3Service;

  public FilingOutcomeListener(S3Service s3Service) {
    this.s3Service = s3Service;
  }

  @RabbitListener(queues = "#{rabbitMQConfig.getFilingOutcomeQueue()}")
  public void handleEvent(Message message) {
    LOG.debug("Received filing outcome message: {}", message);
    String filePath = message.getMessageProperties().getHeaders().get("source").toString();
    s3Service.moveFileFromTo(filePath, FileStatus.FILING, FileStatus.FILED, targetBaseRoutingKey);
  }
}
