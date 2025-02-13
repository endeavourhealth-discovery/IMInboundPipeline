package org.endeavourhealth.pipeline.inbound.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.dataqueue}")
  private String dataQueue;

  private static String DATA_QUEUE;

  @PostConstruct
  public void init() {
    DATA_QUEUE = dataQueue;
  }

  public static String getQueue() {
    return DATA_QUEUE;
  }

  @Bean
  public Queue queue() {
    return new Queue(DATA_QUEUE);
  }

}

