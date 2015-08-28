package stepdef.subtest;

import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;

import com.github.shell88.bddvideoannotator.javaadapters.CucumberReportingAdapter;

import java.util.List;

/**
 * TestProxy which will be used to execute tests on a mocked 
 * {@link com.github.shell88.bddvideoannotator.javaadapters.CucumberReportingAdapter}
 * 
 * @author Hell
 *
 */

public class CucumberReportingAdapterTestProxy implements Reporter, Formatter {

  private static CucumberReportingAdapter testMock;
  //IDEE: mit MEDIATOR pattern vereinfachen? zuordnung submethode aufrufmethode
  public CucumberReportingAdapterTestProxy() {
    if (testMock == null) {
      throw new RuntimeException("No Adapter instance for proxy set!");
    }
  }

  public static void setUnderlyingInstance(CucumberReportingAdapter instance) {
    testMock = instance;
  }

  public static CucumberReportingAdapter getUnderlyingInstance() {
    return testMock;
  }


  @Override
  public void before(Match match, Result result) {
    testMock.before(match, result);
  }

  @Override
  public void result(Result result) {
    testMock.result(result);
  }

  @Override
  public void after(Match match, Result result) {
    testMock.after(match, result);
  }

  @Override
  public void match(Match match) {
    testMock.match(match);
  }

  @Override
  public void embedding(String mimeType, byte[] data) {
    testMock.embedding(mimeType, data);
  }

  @Override
  public void write(String text) {
    testMock.write(text);
  }



  @Override
  public void syntaxError(String state, String event, List<String> legalEvents, String uri,
      Integer line) {
    testMock.syntaxError(state, event, legalEvents, uri, line);
  }



  @Override
  public void uri(String uri) {
    testMock.uri(uri);
  }



  @Override
  public void feature(Feature feature) {
    testMock.feature(feature);
  }



  @Override
  public void scenarioOutline(ScenarioOutline scenarioOutline) {
    testMock.scenarioOutline(scenarioOutline);
  }



  @Override
  public void examples(Examples examples) {
    testMock.examples(examples);
  }



  @Override
  public void startOfScenarioLifeCycle(Scenario scenario) {
    testMock.startOfScenarioLifeCycle(scenario);
  }



  @Override
  public void background(Background background) {
    testMock.background(background);
  }



  @Override
  public void scenario(Scenario scenario) {
    testMock.scenario(scenario);
  }



  @Override
  public void step(Step step) {
    testMock.step(step);
  }



  @Override
  public void endOfScenarioLifeCycle(Scenario scenario) {
    testMock.endOfScenarioLifeCycle(scenario);
  }



  @Override
  public void done() {
    testMock.done();
  }



  @Override
  public void close() {
    testMock.close();
  }



  @Override
  public void eof() {
    testMock.eof();
  }

}
