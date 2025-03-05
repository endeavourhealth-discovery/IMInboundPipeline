package org.endeavourhealth.pipeline.inbound.validator;

import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.endeavourhealth.pipeline.inbound.CucumberRunner;
import org.endeavourhealth.pipeline.inbound.CucumberSpringConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileValidatorSteps extends CucumberSpringConfiguration {
  private String fileName;
  private List<String> fileHeaders;
  private boolean validationResult;
  private List<String> fileNames;

  @Autowired
  private FileValidator validator;

  @Given("a file named {string}")
  public void a_file_named(String fileName) {
    this.fileName = fileName;
  }

  @And("it contains headers {string}")
  public void it_contains_headers(String headers) {
    this.fileHeaders = Arrays.asList(headers.split(","));
  }

  @When("I validate the file")
  public void i_validate_the_file() {
    this.validationResult = validator.isValidFile(fileName, fileHeaders);
  }

  @Then("the validation should return {word}")
  public void the_validation_should_return(String expectedResult) {
    boolean expected = Boolean.parseBoolean(expectedResult);
    Assertions.assertEquals(expected, validationResult);
  }

  @Given("a list of {string}")
  public void a_list_of(String fileNames) {
    this.fileNames = Arrays.asList(fileNames.split(", "));
  }

  @When("I validate the list")
  public void i_validate_the_list() throws IOException {
    this.validationResult = validator.areAllFilesInBucket("EMIS", fileNames);
  }
}
