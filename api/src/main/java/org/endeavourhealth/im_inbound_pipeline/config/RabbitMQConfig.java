package org.endeavourhealth.im_inbound_pipeline.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  public static final String EXCHANGE_NAME = "my_topic_exchange";

  @Bean
  public TopicExchange topicExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }
}

