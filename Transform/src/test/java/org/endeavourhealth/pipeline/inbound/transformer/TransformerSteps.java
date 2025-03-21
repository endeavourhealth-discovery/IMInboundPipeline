package org.endeavourhealth.pipeline.inbound.transformer;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.endeavourhealth.pipeline.inbound.CucumberSpringConfiguration;
import org.endeavourhealth.pipeline.inbound.Transformer;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TransformerSteps extends CucumberSpringConfiguration {
  private List<String> headers;
  private String jslt;

  private final Transformer transformer = new Transformer();

  @Given("a message with header values {string} {string}")
  public void aMessageWithHeaders(String arg0, String arg1) {
    this.headers = new ArrayList<>();
    this.headers.add(arg0);
    this.headers.add(arg1);
  }

  @When("I look for the correct transform file")
  public void iLookForTheCorrectTransformFile() throws URISyntaxException, IOException {
    //this.jslt = transformer.loadTransformation(this.headers.get(0), this.headers.get(1));
  }

  @Then("a file is found with content")
  public void aFileIsFoundWithContent(String arg0) {
    Assertions.assertEquals(arg0.replaceAll("\\s+", ""), this.jslt.replaceAll("\\s+", ""));
  }
}
