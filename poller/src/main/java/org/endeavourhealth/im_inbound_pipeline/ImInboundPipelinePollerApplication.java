package org.endeavourhealth.im_inbound_pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ImInboundPipelinePollerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImInboundPipelinePollerApplication.class, args);
	}

}
