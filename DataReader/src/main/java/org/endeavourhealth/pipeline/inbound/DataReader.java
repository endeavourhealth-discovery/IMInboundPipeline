package org.endeavourhealth.pipeline.inbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = SpringDataWebAutoConfiguration.class)
public class DataReader implements ApplicationRunner {

  @Value("${rabbitmq.sourceQueue}")
  private String sourceQueue;

  @Value("${rabbitmq.targetBaseRoutingKey}")
  private String targetBaseRoutingKey;

  @Value("${rabbitmq.targetExchange}")
  private String targetExchange;

  private static final Logger LOG = LoggerFactory.getLogger(DataReader.class);

  public static void main(String[] args) {
    SpringApplication.run(DataReader.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    LOG.info("Running FileReader with sourceQueue: {}, targetBaseRoutingKey: {}, targetExchange: {}", sourceQueue, targetBaseRoutingKey, targetExchange);
  }
}
