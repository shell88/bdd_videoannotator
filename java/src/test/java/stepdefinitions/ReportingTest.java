package stepdefinitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import stepdef.helper.TestUtils;
import stepdef.subtest.CucumberSubTestThreadWithAdapterInstance;

import com.github.shell88.bddvideoannotator.javaadapters.CucumberReportingAdapter;
import com.github.shell88.bddvideoannotator.javaadapters.ServerConnector;
import com.github.shell88.bddvideoannotator.stubjava.AnnotationService;
import com.github.shell88.bddvideoannotator.stubjava.StepResult;
import com.github.shell88.bddvideoannotator.stubjava.StringArray;
import com.github.shell88.bddvideoannotator.stubjava.StringArrayArray;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ReportingTest {

  private AnnotationService mockedClient;
  private CucumberReportingAdapter mockedAdapter;
  private String[] expectedSteps;
  private File subTestDirectory;

  @Given("^i have an instance of the BDD-Adapter for Cucumber-JVM with a mocked server connection$")
  public void i_have_an_instance_of_the_BDD_Adapter_for_Cucumber_JVM_with_a_mocked_server_connection()
      throws Throwable {
    mockedClient = mock(AnnotationService.class);
    ServerConnector mockedServerConnector = mock(ServerConnector.class);
    when(mockedServerConnector.startServerProcess()).thenReturn(mockedClient);
    when(mockedServerConnector.getJavaClient()).thenReturn(mockedClient);
    mockedAdapter = new CucumberReportingAdapter(mockedServerConnector);
   
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

  @When("^I run Cucumber-JVM$")
  public void i_run_Cucumber_JVM() throws Throwable {
    CucumberSubTestThreadWithAdapterInstance subTest = new CucumberSubTestThreadWithAdapterInstance(
        mockedAdapter, this.subTestDirectory);
    subTest.start();
    subTest.join();
    assertTrue(StringUtils.join(subTest.getThrownExceptions(), "\n"), subTest
        .getThrownExceptions().size() == 0);
  }

  @Then("^the Adapter should send following steps for the scenario \"(.*?)\":$")
  public void the_Adapter_should_send_following_steps_for_the_scenario(
      String scenarioName, String stepList) throws Throwable {
    InOrder orderedVerifier = inOrder(mockedClient);
    orderedVerifier.verify(mockedClient).startScenario(
        scenarioName);
    
    expectedSteps = stepList.split("\n");
    
    for (int i = 0; i < expectedSteps.length; i++) {
      expectedSteps[i] = expectedSteps[i].trim();
      orderedVerifier.verify(mockedClient).addStepToBuffer(eq(expectedSteps[i]),
              any(StringArrayArray.class));
    }
    orderedVerifier.verify(mockedClient).stopScenario();
  }

  @Then("^the Adapter should report the scenario \"(.*?)\"$")
  public void the_Adapter_should_report_the_scenario(String scenarioName)
      throws Throwable {
    /*
     * atLeastOnce(): for ScenarioOutlines the scenario will transmitted twice
     * but ignored the second time from the server (desired behaviour)
     */
    verify(mockedClient, atLeastOnce()).startScenario(scenarioName);
    verify(mockedClient, times(1)).stopScenario();
  }
  

  @Then("^the Adapter should send the steptext: \"(.*?)\" with the datatable at position (\\d+)$")
  public void the_Adapter_should_send_the_steptext_with_the_datatable(
      String steptext, int position, String datatable) throws Throwable {

    position = position - 1;
    ArgumentCaptor<String> capturedStep = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<StringArrayArray> capturedDatatable = ArgumentCaptor
        .forClass(StringArrayArray.class);
    // Cannot verify concrete steptext AND using ArgumentCaptor
    // Datatable cannot be verified directly because it is a complex data
    // structure
    verify(mockedClient, atLeastOnce()).addStepToBuffer(capturedStep.capture(),
        capturedDatatable.capture());

    assertEquals(steptext, capturedStep.getAllValues().get(position));
      
    String[] lines = datatable.split("\n");
    List<StringArray> deliveredDatatable = capturedDatatable.getAllValues()
        .get(position).getItem();
    assertEquals(lines.length, deliveredDatatable.size());

    for (int i = 0; i < lines.length; i++) {
      List<String> expected = Arrays.asList(lines[i].trim().split("\\|"));
      List<String> rowEntriesCaptured = deliveredDatatable.get(i).getItem();
      // First entry of expected will be empty because of the split
      assertEquals(expected.size() - 1, rowEntriesCaptured.size());
      for (int j = 1; j < expected.size(); j++) {
        assertEquals(expected.get(j).trim(), rowEntriesCaptured.get(j - 1)
            .trim());
      }
    }   
  }

  @Then("^the Adapter should send \"(.*?)\" for all steps to the server$")
  public void the_Adapter_should_send_for_all_steps_to_the_server(
      String expectedResult) throws Throwable {
    ArgumentCaptor<StepResult> resultsCaptured = ArgumentCaptor
        .forClass(StepResult.class);
    verify(mockedClient, times(expectedSteps.length)).addResultToBufferStep(
        resultsCaptured.capture());
    for (StepResult stepReported : resultsCaptured.getAllValues()) {
      assertEquals(StepResult.valueOf(expectedResult), stepReported);
    }
  }

  @Then("^the Adapter should send the step \"(.*?)\" with Result \"(.*?)\"$")
  public void the_Adapter_should_send_the_step_with_Result(String steptext,
      String result) throws Throwable {
    ArgumentCaptor<StepResult> captureResult = ArgumentCaptor
        .forClass(StepResult.class);
    ArgumentCaptor<String> captureStepText = ArgumentCaptor
        .forClass(String.class);
    verify(mockedClient, atLeastOnce()).addStepWithResult(
        captureStepText.capture(), any(StringArrayArray.class),
        captureResult.capture());

  }

}
