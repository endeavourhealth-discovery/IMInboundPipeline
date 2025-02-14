package org.endeavourhealth.pipeline.inbound.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.targetBaseRoutingKey}")
  private String targetBaseRoutingKey;

  @Value("${rabbitmq.sourceQueue}")
  private String sourceQueue;

  private static String TARGET_BASE_ROUTING_KEY;

  private static String SOURCE_QUEUE;

  @PostConstruct
  public void init() {
    TARGET_BASE_ROUTING_KEY = targetBaseRoutingKey;
    SOURCE_QUEUE = sourceQueue;
  }

  public static String getRoutingKey() {
    return TARGET_BASE_ROUTING_KEY;
  }

  public static String getSourceQueue() {
    return SOURCE_QUEUE;
  }
}

