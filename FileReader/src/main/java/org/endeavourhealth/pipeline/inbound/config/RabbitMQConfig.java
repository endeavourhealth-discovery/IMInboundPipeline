package org.endeavourhealth.pipeline.inbound.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  private static final String EXCHANGE_NAME = System.getenv("EXCHANGE_NAME");
  private static final String QUEUE = System.getenv("QUEUE");
  private static final String ROUTING_KEY = System.getenv("ROUTING_KEY");

  public static String getQueue() {
    return QUEUE;
  }

  public static String getExchangeName() {
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

