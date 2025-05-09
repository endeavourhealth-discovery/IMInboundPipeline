package org.endeavourhealth.pipeline.inbound.service;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.endeavourhealth.pipeline.inbound.CucumberSpringConfiguration;
import org.junit.jupiter.api.Assertions;

public class SystemServiceSteps extends CucumberSpringConfiguration {
  private String envVarValue;

  @When("getProperty is called with {string}")
  public void getEnvironmentVariable(String name) {
    envVarValue = SystemService.getProperty(name);
  }

  @Then("the result should be {string}")
  public void checkEnvVarValue(String expectedResult) {
    Assertions.assertEquals(expectedResult, envVarValue);
  }
}
