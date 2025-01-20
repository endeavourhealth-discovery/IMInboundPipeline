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

  public static final String EMIS_QUEUE = "emis_queue";
  public static final String TPP_QUEUE = "tpp_queue";
  public static final String BULK_QUEUE = "bulk_queue";

  public static final String EMIS_ROUTING_KEY = "emis.*";
  public static final String TPP_ROUTING_KEY = "tpp.*";
  public static final String BULK_ROUTING_KEY = "*.bulk";

  @Bean
  public TopicExchange topicExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Queue emisQueue() {
    return new Queue(EMIS_QUEUE);
  }

  @Bean
  public Queue tppQueue() {
    return new Queue(TPP_QUEUE);
  }

  @Bean
  public Queue bulkQueue() {
    return new Queue(BULK_QUEUE);
  }

  @Bean
  public Binding emisBinding(Queue emisQueue, TopicExchange topicExchange) {
    return BindingBuilder.bind(emisQueue).to(topicExchange).with(EMIS_ROUTING_KEY);
  }

  @Bean
  public Binding tppBinding(Queue tppQueue, TopicExchange topicExchange) {
    return BindingBuilder.bind(tppQueue).to(topicExchange).with(TPP_ROUTING_KEY);
  }

  @Bean
  public Binding bulkBinding(Queue bulkQueue, TopicExchange topicExchange) {
    return BindingBuilder.bind(bulkQueue).to(topicExchange).with(BULK_ROUTING_KEY);
  }
}

