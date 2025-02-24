package org.endeavourhealth.pipeline.inbound;

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
@ComponentScan(excludeFilters={@ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, value=DataSourceAutoConfiguration.class)})
@Configuration
public class FileReader extends SpringBootServletInitializer implements ApplicationRunner {

  @Value("${rabbitmq.targetBaseRoutingKey}")
  private String targetBaseRoutingKey;

  @Value("${rabbitmq.sourceQueue}")
  private String sourceQueue;

  @Value("${rabbitmq.targetExchange}")
  private String targetExchange;

  public static void main(String[] args) {
    SpringApplication.run(FileReader.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    System.out.println("Running FileReader with targetBaseRoutingKey: " + targetBaseRoutingKey + ", sourceQueue: " + sourceQueue + ", targetExchange: " + targetExchange);
  }
}

