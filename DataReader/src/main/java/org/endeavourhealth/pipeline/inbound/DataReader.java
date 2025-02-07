package org.endeavourhealth.pipeline.inbound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DataReader {

	public static void main(String[] args) {
		SpringApplication.run(DataReader.class, args);
	}

}
