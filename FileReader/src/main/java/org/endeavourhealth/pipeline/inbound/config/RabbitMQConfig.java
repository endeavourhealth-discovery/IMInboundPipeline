package org.endeavourhealth.pipeline.inbound.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  private static final String ROUTING_KEY = System.getenv("ROUTING_KEY");

  public static String getRoutingKey() {
    return ROUTING_KEY;
  }
}

