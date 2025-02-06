package org.endeavourhealth.im_inbound_pipeline.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  public static final String EXCHANGE_NAME = "my_topic_exchange"; // from config

  public static final String ORG_QUEUE = "emis_queue"; // from config

  public static final String ORG_ROUTING_KEY = "emis.*"; // from config

  @Bean
  public TopicExchange topicExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Queue emisQueue() {
    return new Queue(ORG_QUEUE);
  }

  @Bean
  public Binding emisBinding(Queue emisQueue, TopicExchange topicExchange) {
    return BindingBuilder.bind(emisQueue).to(topicExchange).with(ORG_ROUTING_KEY);
  }
}

