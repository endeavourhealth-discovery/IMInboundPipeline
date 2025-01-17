package org.endeavourhealth.im_inbound_pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Configuration
public class ImInboundPipelineAPIApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ImInboundPipelineAPIApplication.class, args);
	}

}
