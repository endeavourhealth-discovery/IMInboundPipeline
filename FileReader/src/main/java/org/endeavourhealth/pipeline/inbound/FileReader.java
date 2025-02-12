package org.endeavourhealth.pipeline.inbound;

import org.endeavourhealth.pipeline.inbound.listener.FileEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Configuration
public class FileReader extends SpringBootServletInitializer implements ApplicationRunner {

  @Value("${rabbitmq.routingKey}")
  private String routingKey;

  @Value("${rabbitmq.filequeue}")
  private String fileQueue;

  public static void main(String[] args) {
    SpringApplication.run(FileReader.class, args);
    new FileEventListener().test();
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    System.out.println("Running FileReader with routingKey: " + routingKey + ", fileQueue: " + fileQueue);
  }
}

