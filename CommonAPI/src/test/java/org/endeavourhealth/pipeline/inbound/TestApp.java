package org.endeavourhealth.pipeline.inbound;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication()
@Configuration
public class TestApp extends SpringBootServletInitializer implements ApplicationRunner {

  @Override
  public void run(ApplicationArguments args) {
    // Test application configuration
  }
}

