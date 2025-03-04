package org.endeavourhealth.pipeline.inbound;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.*;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
//@SelectPackages("org.endeavourhealth.pipeline.inbound")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.endeavourhealth.pipeline.inbound")
//@CucumberContextConfiguration
//@SpringBootTest
//@SpringBootConfiguration
public class CucumberRunner {

}
