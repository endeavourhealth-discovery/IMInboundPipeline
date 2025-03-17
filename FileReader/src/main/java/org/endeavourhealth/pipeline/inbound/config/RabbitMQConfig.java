package org.endeavourhealth.pipeline.inbound.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class RabbitMQConfig {

  @Value("${spring.rabbitmq.host:test}")
  private String rabbitMqHost;

  @Value("${spring.rabbitmq.port:1111}")
  private int rabbitMqPort;

  @Value("${spring.rabbitmq.username:test}")
  private String rabbitMqUsername;

  @Value("${spring.rabbitmq.password:test}")
  private String rabbitMqPassword;

  @Value("${rabbitmq.sourceQueue:test}")
  private String sourceQueue;

  @Value("${spring.rabbitmq.virtual-host:test}")
  private String rabbitMqVHost;

  @Value("${rabbitmq.filingOutcomeQueue:test22}")
  private String filingOutcomeQueue;

  private static String SOURCE_QUEUE;
  private static String FILING_OUTCOME_QUEUE;

  @PostConstruct
  public void init() {
    SOURCE_QUEUE = sourceQueue;
    FILING_OUTCOME_QUEUE = filingOutcomeQueue;
  }

  public static String getSourceQueue() {
    return SOURCE_QUEUE;
  }

  public static String getFilingOutcomeQueue() {
    return FILING_OUTCOME_QUEUE;
  }


  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitMqHost, rabbitMqPort);
    connectionFactory.setUsername(rabbitMqUsername);
    connectionFactory.setPassword(rabbitMqPassword);
    connectionFactory.setVirtualHost(rabbitMqVHost);
    return connectionFactory;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    return new RabbitTemplate(connectionFactory);
  }
}

