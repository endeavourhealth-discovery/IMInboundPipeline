package org.endeavourhealth.pipeline.inbound;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
  properties = {
    "rabbitmq.targetBaseRoutingKey=EMIS",
    "rabbitmq.sourceQueue=File-EMIS",
    "rabbitmq.targetExchange=Data"
  })
public class CucumberSpringConfiguration {

}
