package org.endeavourhealth.pipeline.inbound.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class RabbitMQConfig {

  public static final String EXCHANGE_NAME = Optional.ofNullable(System.getenv("EXCHANGE_NAME")).orElseThrow(() -> new IllegalArgumentException("Env var 'EXCHANGE_NAME' is not defined")); // from config
  public static final String QUEUE = Optional.ofNullable(System.getenv("QUEUE")).orElseThrow(() -> new IllegalArgumentException("Env var 'QUEUE_NAME' is not defined")); // "emis_queue"; // from config
  public static final String ROUTING_KEY = Optional.ofNullable(System.getenv("ROUTING_KEY")).orElseThrow(() -> new IllegalArgumentException("Env var 'ROUTING_KEY' is not defined")); // "emis.*"; // from config

  public static String getQueue() {
    return QUEUE;
  }

  public static String getExchange() {
    return EXCHANGE_NAME;
  }

  public static String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Bean
  public TopicExchange topicExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Queue queue() {
    return new Queue(QUEUE);
  }

  @Bean
  public Binding binding(Queue emisQueue, TopicExchange topicExchange) {
    return BindingBuilder.bind(emisQueue).to(topicExchange).with(ROUTING_KEY);
  }
}

