package org.endeavourhealth.pipeline.inbound;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DataReader implements ApplicationRunner {

  @Value("${rabbitmq.dataqueue}")
  private String dataQueue;

	public static void main(String[] args) {
		SpringApplication.run(DataReader.class, args);
	}

  @Override
  public void run(ApplicationArguments args) throws Exception {
    System.out.println("Running FileReader with dataQueue: " + dataQueue);
  }
}
