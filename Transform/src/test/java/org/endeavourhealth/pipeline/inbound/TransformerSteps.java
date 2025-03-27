package org.endeavourhealth.pipeline.inbound;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class TransformerSteps extends CucumberSpringConfiguration {
  private String date;
  private String dateFomat;
  private String dateResult;

  @Given("a date of {string}")
  public void aDateOf(String date) {
    this.date = date;
  }

  @Given("a format of {string}")
  public void aFormatOf(String dateFomat) {
    this.dateFomat = dateFomat;
  }

  @When("I call formatDate")
  public void iCallFormatDate() {
    this.dateResult = Transformer.formatDate(dateFomat, date);
  }

  @When("I call formatDateTime")
  public void iCallFormatDateTime() {
    this.dateResult = Transformer.formatDateTime(dateFomat, date);
  }

  @Then("the date returned should be {string}")
  public void theDateReturnedShouldBe(String expectedResult) {
    Assertions.assertEquals(expectedResult, dateResult);
  }
}
