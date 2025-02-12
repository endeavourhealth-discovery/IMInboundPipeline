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

  @Value("${rabbitmq.routingKey}")
  private String routingKey;

  @Value("${rabbitmq.filequeue}")
  private String fileQueue;

  private static String STATIC_ROUTING_KEY;

  private static String FILE_QUEUE;

  @PostConstruct
  public void init() {
    STATIC_ROUTING_KEY = routingKey;
    FILE_QUEUE = fileQueue;
  }

  public static String getRoutingKey() {
    return STATIC_ROUTING_KEY;
  }

  public static String getFileQueue() {
    return FILE_QUEUE;
  }
}

