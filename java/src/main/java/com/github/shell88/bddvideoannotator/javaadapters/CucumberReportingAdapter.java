package com.github.shell88.bddvideoannotator.javaadapters;

import cucumber.runtime.CucumberException;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.DocString;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;

import com.github.shell88.bddvideoannotator.stubjava.AnnotationService;
import com.github.shell88.bddvideoannotator.stubjava.StepResult;
import com.github.shell88.bddvideoannotator.stubjava.StringArray;
import com.github.shell88.bddvideoannotator.stubjava.StringArrayArray;

import java.util.List;

/**
 * BDD-videoannotator-Formatter for Cucumber-JVM. Can be used through the
 * command line option --plugin using the fully qualified Classname of this Class.
 * 
 * @author Hell
 */

public class CucumberReportingAdapter implements Reporter, Formatter {
  private static AnnotationService client;
  private String lastExampleId = null;
  private boolean scenarioStarted = false;

  
  /**
   * @throws Throwable errors while reading propertiesFile or
   *                   starting the serverProcess.
   */
  public CucumberReportingAdapter() throws Throwable {
    ServerConnector connector = new ServerConnector();
    client = connector.startServerProcess();
  }
  
  /**
   * 
   * @param connector  - ServerConnector that should be used to 
   *                    start the server Process (testability).
   * @throws Throwable - When starting the serverProcess failed.
   */
  public CucumberReportingAdapter(ServerConnector connector) 
      throws Throwable {
    client = connector.startServerProcess();
  }

  public void syntaxError(String state, String event, List<String> legalEvents,
      String uri, Integer line) {

  }

  public void uri(String uri) {
  }

  public void feature(Feature feature) {
    client.setFeatureText(feature.getName());
  }

  public void scenarioOutline(ScenarioOutline scenarioOutline) {
  }

  public void examples(Examples examples) {
    this.lastExampleId = examples.getRows().get(examples.getRows().size() - 1).getId();
  }

  /**
   *   Starts a new scenario on the server.
   */
  public void startOfScenarioLifeCycle(Scenario scenario) {
    // consecutive calls to startScenario will be ignored by the server
    client.startScenario(scenario.getName());
    scenarioStarted = true;
  }

  public void background(Background background) {

  }

  public void scenario(Scenario scenario) {

  }
  /**
   *   Adds the step to the stepBuffer on the server.
   */
  public void step(Step step) {
    // Needed in for scenarioOutlines
    if (!this.scenarioStarted) {
      return;
    }

    client.addStepToBuffer(step.getKeyword() + step.getName()
        + convertDocString(step.getDocString()),
        convertDataTableToStringArray(step.getRows()));

  }
  /**
   * Stops the scenario on the server (not scenario outlines).
   */
  public void endOfScenarioLifeCycle(Scenario scenario) {
    // Collect Scenario-Outline to single Scenario
    if (this.lastExampleId != null && this.lastExampleId != scenario.getId()) {
      return;
    }
    client.stopScenario();
    scenarioStarted = false;
    this.lastExampleId = null;
  }

  public void done() {

  }

  public void close() {

  }

  public void eof() {

  }
  /**Sends a step for before-Hooks to the server.*/
  public void before(Match match, Result result) {
    client.addStepWithResult(match.getLocation(), new StringArrayArray(),
        toServerStepResult(result));

  }

  /**
   * Sends the result to the server.
   */
  public void result(Result result) {
    try {
      client.addResultToBufferStep(toServerStepResult(result));
    } catch (Exception e) {
      throw new CucumberException("Could not send result to server: "
          + e.getMessage());
    }
  }
  /**Sends a step for after-Hooks to the server.*/
  public void after(Match match, Result result) {
    client.addStepWithResult(match.getLocation(), new StringArrayArray(),
        toServerStepResult(result));
  }

  public void match(Match match) {

  }

  public void embedding(String mimeType, byte[] data) {

  }

  public void write(String text) {

  }

  /**
   * Converts the Cucumber-JVM result to the server-Format.
   * @param res result from Cucumber-JVM
   * @return the converted result for transmission to the server
   */

  public StepResult toServerStepResult(Result res) {

    if (res.getStatus().equalsIgnoreCase("passed")) {
      return StepResult.SUCCESS;
    } else if (res.getStatus().equalsIgnoreCase("pending")
        || (res.getStatus().equalsIgnoreCase("failed") 
            && res.getError() instanceof AssertionError)) {
      return StepResult.FAILURE;
    } else if (res.getStatus().equalsIgnoreCase("undefined")
        || res.getStatus().equalsIgnoreCase("skipped")) {
      return StepResult.SKIPPED;
    } else {
      return StepResult.ERROR;
    }

  }

  /**
   * @param dataTable datatable from a step
   * @return the converted datatable for transmission to the server.
   */
  public StringArrayArray convertDataTableToStringArray(
      final List<DataTableRow> dataTable) {
    StringArrayArray tableToSend = new StringArrayArray();

    if (dataTable != null && dataTable.size() > 0) {
      StringArray row;

      for (DataTableRow tableRow : dataTable) {
        row = new StringArray();
        row.getItem().addAll(tableRow.getCells());
        tableToSend.getItem().add(row);
      }
    }
    return tableToSend;
  }
  
  /**
   * @param  str DocString object from a step.
   * @return Converted to a single string with appropriate markups
   */
  public String convertDocString(DocString str) {
    if (str == null || str.getValue() == null) {
      return "";
    }
    return " \"\"\"" + str.getValue() + "\"\"\"";
  }

}
