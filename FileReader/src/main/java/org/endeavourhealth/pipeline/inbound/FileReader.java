package org.endeavourhealth.pipeline.inbound;

import org.endeavourhealth.pipeline.inbound.service.QueueSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication()
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = DataSourceAutoConfiguration.class)})
@Configuration
public class FileReader extends SpringBootServletInitializer implements ApplicationRunner {

  @Value("${rabbitmq.targetBaseRoutingKey:test}")
  private String targetBaseRoutingKey;

  @Value("${rabbitmq.sourceQueue:test}")
  private String sourceQueue;

  @Value("${rabbitmq.filingOutcomeQueue:test}")
  private String filingOutcomeQueue;

  @Value("${rabbitmq.targetExchange:test}")
  private String targetExchange;

  @Value("${queueSender.maxRetries:3}")
  private int maxRetries;

  @Value("${queueSender.retryWait:3}")
  private int retryWait;

  private static final Logger LOG = LoggerFactory.getLogger(FileReader.class);

  public static void main(String[] args) {
    SpringApplication.run(FileReader.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {
    LOG.info("Running FileReader with targetBaseRoutingKey: {}, sourceQueue: {}, filingOutcomeQueue: {}, targetExchange: {}, maxRetries: {}, retryWait: {}", targetBaseRoutingKey, sourceQueue, filingOutcomeQueue, targetExchange, maxRetries, retryWait);
  }
}

