package stepdefinitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.InOrder;

import stepdef.helper.IsSameStringArrayArray;
import stepdef.helper.TestUtils;
import stepdef.subtest.CucumberSubTestThreadWithAdapterInstance;

import com.github.shell88.bddvideoannotator.javaadapters.CucumberReportingAdapter;
import com.github.shell88.bddvideoannotator.javaadapters.ServerConnector;
import com.github.shell88.bddvideoannotator.stubjava.AnnotationService;
import com.github.shell88.bddvideoannotator.stubjava.StepResult;
import com.github.shell88.bddvideoannotator.stubjava.StringArray;
import com.github.shell88.bddvideoannotator.stubjava.StringArrayArray;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;

import java.io.File;
import java.util.List;

public class ReportingTest {

  private AnnotationService mockedClient;
  private CucumberReportingAdapter mockedAdapter;
  private String[] expectedSteps;
  private File subTestDirectory;
  
  private int scenariosToStop = 0;
  private InOrder orderedVerifier;

  @Given("^i have an instance of the BDD-Adapter for Cucumber-JVM with a mocked server connection$")
  public void i_have_an_instance_of_the_BDD_Adapter_for_Cucumber_JVM_with_a_mocked_server_connection()
      throws Throwable {
    mockedClient = mock(AnnotationService.class);
    ServerConnector mockedServerConnector = mock(ServerConnector.class);
    when(mockedServerConnector.startServerProcess()).thenReturn(mockedClient);
    when(mockedServerConnector.getServerClient()).thenReturn(mockedClient);
    mockedAdapter = new CucumberReportingAdapter(mockedServerConnector);
    orderedVerifier = inOrder(mockedClient);
   
  }

  @Given("^I have a feature file:$")
  public void i_have_a_feature_file(String contentFeatureFile) throws Throwable {
    if (subTestDirectory == null) {
      subTestDirectory = TestUtils.getNewSubTestDirectory();
    }
    File featureFileTemp = new File(subTestDirectory, "test.feature");
    FileUtils.write(featureFileTemp, contentFeatureFile);

  }

  @Given("^i have a step definition file with following methods:$")
  public void i_have_a_step_definition_file_with_following_methods(
      String methodsString) throws Throwable {
    if (subTestDirectory == null) {
      subTestDirectory = TestUtils.getNewSubTestDirectory();
    }
    File stepDefFile = new File(subTestDirectory, "StepDef.java");
    FileUtils.write(stepDefFile, "package " + subTestDirectory.getName()
        + ";\n");
    FileUtils.write(stepDefFile, "import cucumber.api.java.en.*;\n", true);
    FileUtils.write(stepDefFile, "import cucumber.api.java.After;\n", true);
    FileUtils.write(stepDefFile, "import cucumber.api.java.Before;\n", true);
    FileUtils.write(stepDefFile, "public class StepDef{\n\n", true);
    FileUtils.write(stepDefFile, methodsString, true);
    FileUtils.write(stepDefFile, "\n}", true);

    List<String> messages = TestUtils.compileJavaFile(stepDefFile);
    assertEquals(StringUtils.join(messages, "\n"), 0, messages.size());

  }

  @Given("^I have a feature file with a step \"(.*?)\" and a docstring \"(.*?)\"$")
  public void i_have_a_feature_file_with_a_step_and_a_docstring(
      String steptext, String docstring) throws Throwable {
    String contentsFeatureFile = "Feature: test";
    contentsFeatureFile += "\nScenario: test";
    contentsFeatureFile += "\n" + steptext;
    contentsFeatureFile += "\n\"\"\"\n" + docstring + "\n\"\"\"";
    i_have_a_feature_file(contentsFeatureFile);
  }

  @Then("^the Adapter should report the step \"(.*?)\" with the docstring \"(.*?)\"$")
  public void the_Adapter_should_report_the_step_with_the_docstring(String steptext,
      String docstring) throws Throwable {
    String verificationText = steptext + " \"\"\"" + docstring + "\"\"\"";
    orderedVerifier.verify(mockedClient).addStepToBuffer(eq(verificationText),
        any(StringArrayArray.class));
  }

  @When("^I run Cucumber-JVM$")
  public void i_run_Cucumber_JVM() throws Throwable {
    CucumberSubTestThreadWithAdapterInstance subTest = new CucumberSubTestThreadWithAdapterInstance(
        mockedAdapter, this.subTestDirectory);
    subTest.start();
    subTest.join();
    assertTrue(StringUtils.join(subTest.getThrownExceptions(), "\n"), subTest
        .getThrownExceptions().size() == 0);
  }

  @Then("^the Adapter should report the feature \"([^\"]*)\"$")
  public void the_Adapter_should_report_the_feature(String featureTextExpected) throws Throwable {
      // Write code here that turns the phrase above into concrete actions
    orderedVerifier.verify(mockedClient).setFeatureText(featureTextExpected);
  }

  @Then("^the Adapter should report following steps:$")
  public void the_Adapter_should_report_following_steps(String stepListExpected) throws Throwable {
    // Write code here that turns the phrase above into concrete actions
    expectedSteps = stepListExpected.split("\n");    
    for (int i = 0; i < expectedSteps.length; i++) {
      expectedSteps[i] = expectedSteps[i].trim();
      orderedVerifier.verify(mockedClient).addStepToBuffer(eq(expectedSteps[i]),
              any(StringArrayArray.class));
    } 
  }
  
  @Then("^the Adapter should report the scenario \"(.*?)\"$")
  public void the_Adapter_should_report_the_scenario(String scenarioName)
      throws Throwable {
    /*
     * atLeastOnce(): for ScenarioOutlines the scenario will transmitted twice
     * but ignored the second time from the server (desired behaviour)
     */
    verify(mockedClient, atLeastOnce()).startScenario(scenarioName);
    scenariosToStop++;
    verify(mockedClient, atLeast(scenariosToStop)).stopScenario();
  }

  @Then("^the Adapter should send the steptext \"([^\"]*)\" with the datatable:$")
  public void the_Adapter_should_send_the_steptext_with_the_datatable(String expectedStepText,
      DataTable expectedDataTable) throws Throwable {

    StringArrayArray expectedDataArray = convertDataTable2StringArrayArray(expectedDataTable);

    orderedVerifier.verify(mockedClient, atLeastOnce()).addStepToBuffer(eq(expectedStepText),
        argThat(new IsSameStringArrayArray(expectedDataArray)));
  }

  private StringArrayArray convertDataTable2StringArrayArray(DataTable table) {
    StringArrayArray datatable = new StringArrayArray();

    for (DataTableRow row : table.getGherkinRows()) {
      StringArray rowElement = new StringArray();
      rowElement.getItem().addAll(row.getCells());
      datatable.getItem().add(rowElement);
    }
    
    return datatable;
  }
  

  @Then("^the Adapter should send \"(.*?)\" for all steps to the server$")
  public void the_Adapter_should_send_for_all_steps_to_the_server(
      String expectedResult) throws Throwable {

    verify(mockedClient, times(expectedSteps.length)).addResultToBufferStep(
        refEq(StepResult.valueOf(expectedResult)));
    
  }

  @Then("^the Adapter should send the step \"(.*?)\" with Result \"(.*?)\"$")
  public void the_Adapter_should_send_the_step_with_Result(String steptext,
      String result) throws Throwable {
    //Contains steptext, as CucumberJVM will also Add name of the StepdefinitionClass
    // to the Steptext
    orderedVerifier.verify(mockedClient, atLeastOnce()).addStepWithResult(
        contains(steptext),
        any(StringArrayArray.class), 
        refEq(StepResult.valueOf(result)));
    
  }

}
