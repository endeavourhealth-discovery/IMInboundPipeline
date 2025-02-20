package org.endeavourhealth.pipeline.inbound.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.sourceQueue}")
  private String sourceQueue;

  private static String SOURCE_QUEUE;

  @PostConstruct
  public void init() {
    SOURCE_QUEUE = sourceQueue;
  }

  public static String getQueue() {
    return SOURCE_QUEUE;
  }

  @Bean
  public Queue queue() {
    return new Queue(SOURCE_QUEUE);
  }

}

