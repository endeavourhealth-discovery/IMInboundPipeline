package org.endeavourhealth.pipeline.inbound.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class RabbitMQConfig {

  public static final String QUEUE = Optional.ofNullable(System.getenv("QUEUE")).orElseThrow(() -> new IllegalArgumentException("Env var 'QUEUE' is not defined"));

  public static String getQueue() {
    return QUEUE;
  }

  @Bean
  public Queue queue() {
    return new Queue(QUEUE);
  }

}

