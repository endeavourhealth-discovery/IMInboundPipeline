package org.endeavourhealth.im_inbound_pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Configuration
public class ImInboundPipelinePollerApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(ImInboundPipelinePollerApplication.class, args);
  }

}
