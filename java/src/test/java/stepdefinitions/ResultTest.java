package stepdefinitions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import gherkin.formatter.model.Result;

import com.github.shell88.bddvideoannotator.javaadapters.CucumberReportingAdapter;
import com.github.shell88.bddvideoannotator.javaadapters.ServerConnector;
import com.github.shell88.bddvideoannotator.stubjava.StepResult;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ResultTest {

  CucumberReportingAdapter adapterWithoutConnection;
  StepResult convertedResult;

  @Given("^i have an instance of the BDD-Adapter for Cucumber-JVM without a server connection$")
  public void i_have_an_instance_of_the_BDD_Adapter_for_Cucumber_JVM_without_a_server_connection()
      throws Throwable {
    ServerConnector mockedServerConnector = mock(ServerConnector.class);
    adapterWithoutConnection = new CucumberReportingAdapter(
        mockedServerConnector);
  }

  @When("^Cucumber reports \"(FAILED#AssertionError)\"$")
  public void cucumber_reports_assertion_error(String status) throws Throwable {

    Result res = new Result("FAILED", 0L, new AssertionError(), null);

    StepResult test = adapterWithoutConnection.toServerStepResult(res);
    convertedResult = test;
  }

  @When("^Cucumber reports \"(FAILED|PASSED|SKIPPED|UNDEFINED|PENDING|NOTACUCUMBERSTATUS)\"$")
  public void cucumber_reports_result_without_assertion_error(String status)
      throws Throwable {
    Result res = new Result(status, 0L, "");
    StepResult test = adapterWithoutConnection.toServerStepResult(res);
    convertedResult = test;
  }

  @Then("^the BDD-Adapter should convert it to \"(.*?)\"$")
  public void the_BDD_Adapter_should_convert_it_to(String target)
      throws Throwable {

    assertEquals(StepResult.valueOf(target), convertedResult);

  }

}
