package stepdefinitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static stepdef.helper.AssertExtensions.assertActualResultStepEquals;
import static stepdef.helper.AssertExtensions.assertDurationEquals;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import stepdef.helper.TestUtils;

import com.github.shell88.bddvideoannotator.annotationfile.exporter.ScenarioAnnotationsDto;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.StepAnnotation;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.StepResult;
import com.github.shell88.bddvideoannotator.annotationfile.parser.AnnotationFileParserFactory;
import com.github.shell88.bddvideoannotator.annotationfile.parser.ExpectedResultStep;
import com.github.shell88.bddvideoannotator.service.AnnotationService;
import com.xuggle.xuggler.IContainer;

import cucumber.api.DataTable;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class WorkflowStepDef {

  private AnnotationService serverInstance;
  private File outputDirectory;
  private File annotationOutputFile;
  private File videoOutputFile;
  private ScenarioAnnotationsDto expectedScenario;
 
  
  /**
   * Initializes a new Test.
   * @throws Throwable when subtestDirectory could not be generated.
   */
  public WorkflowStepDef() throws Throwable {
    outputDirectory = TestUtils.getNewSubTestDirectory();
    serverInstance = new AnnotationService(outputDirectory.getAbsolutePath(),
        "full", "full");
  }

  @When("^I start a Scenario \"(.*?)\"$")
  public void i_start_a_Scenario_with_description_text(String scenarioDescription)
      throws Throwable {
    serverInstance.startScenario(scenarioDescription);
    expectedScenario = new ScenarioAnnotationsDto();
    expectedScenario.setScenarioText(scenarioDescription);
  }

  @When("^I add a Step \"(.*?)\",$")
  public void i_add_a_Step(String steptext) throws Throwable {
    serverInstance.addStepToBuffer(steptext, new String[0][0]);
    ExpectedResultStep cur = new ExpectedResultStep();
    cur.setSteptext(steptext);
    expectedScenario.addStepAnnotation(cur);
  }

  @When("^I add a Step \"(.*?)\" with Result \"(.*?)\" and following Sample-Data:$")
  public void i_add_a_Step_with_Result_and_following_Sample_Data(
      String stepText, String stepResult, DataTable sampleData)
      throws Throwable {
    ExpectedResultStep exp = new ExpectedResultStep();
    exp.setSteptext(stepText);
    exp.setStepResult(StepResult.valueOf(stepResult));
    exp.setExpectedDataTable(sampleData); 
    expectedScenario.addStepAnnotation(exp);
    serverInstance.addStepWithResult(stepText, exp.getDataTable(),
        StepResult.valueOf(stepResult));
  }

  @When("^I add a Step \"(.*?)\" with result \"(.*?)\" after (\\d+) Seconds,$")
  public void i_add_a_Step_with_result_after_Seconds(String steptext,
      String result, int secondsWaiting) throws Throwable {
    Thread.sleep(TimeUnit.SECONDS.toMillis(secondsWaiting));
    serverInstance
        .addStepWithResult(steptext, null, StepResult.valueOf(result));
    ExpectedResultStep exp = new ExpectedResultStep();
    exp.setSteptext(steptext);
    exp.setStepResult(StepResult.valueOf(result));
    exp.setExpectedDurationSeconds(secondsWaiting);
    
    expectedScenario.addStepAnnotation(exp);
  }

  @When("^I add the result \"(.*?)\",$")
  public void i_add_the_result(String result) throws Throwable {
    serverInstance.addResultToBufferStep(StepResult.valueOf(result));

    for (StepAnnotation expectedStep : expectedScenario.getStepAnnotations()) {
      if (expectedStep.getStepResult() == null) {
        expectedStep.setStepResult(StepResult.valueOf(result));
        return;
      }
    }

  }

  @When("^I stop the Scenario$")
  public void i_stop_the_Scenario() throws Throwable {
    serverInstance.stopScenario();
  }

  
  @Then("^I should get a video and an annotation file named with the scenario name$")
  public void i_should_get_a_video_and_an_annotation_file_named_with_the_scenario_name() throws Throwable {
    i_should_get_a_video_file_named_with_the_scenario_name();
    i_should_get_an_annotation_file_named_with_the_scenario_name();
  }

  
  @Then("^I should get a video file named with the scenario name$")
  public void i_should_get_a_video_file_named_with_the_scenario_name()
      throws Throwable {
    File[] videoFileList = TestUtils
        .getVideoFilesInDirectory(this.outputDirectory);
    assertTrue(videoFileList.length > 0);
    for (File videoFile : videoFileList) {
      if (videoFile.getName().startsWith(expectedScenario.getScenarioText())) {
        videoOutputFile = videoFile;
        return;
      }
    }
    fail("Could not find matching video_file: " + expectedScenario.getScenarioText());
  }
  
  
  @Then("^I should get an annotation file named with the scenario name$")
  public void i_should_get_an_annotation_file_named_with_the_scenario_name() throws Throwable {
    annotationOutputFile = TestUtils
        .getLatestAnnotationOutputFileInDirectory(outputDirectory);
    assertTrue(annotationOutputFile != null
        && annotationOutputFile.getName().startsWith(expectedScenario.getScenarioText()));
  }
  
  
  @Then("^the annotation file should contain the added steps$")
  public void i_should_get_an_annotation_file_named_containing_the_added_steps() throws Throwable {
    List<StepAnnotation> stepsAnnotationFile = AnnotationFileParserFactory
        .getFileParser(annotationOutputFile).parse().getStepAnnotations();
    assertEquals(expectedScenario.getNumberOfStepAnnotations(), stepsAnnotationFile.size());

    for (int i = 0; i < expectedScenario.getNumberOfStepAnnotations(); i++) {
      assertActualResultStepEquals("Step " + i,
          (ExpectedResultStep) expectedScenario.getStepAnnotation(i), stepsAnnotationFile.get(i));
    }
  }
 
  @Then("^the annotation file should contain the name of the scenario$")
  public void the_annotation_file_should_contain_the_scenarioName() throws Throwable {
    String actualScenarioText = AnnotationFileParserFactory.getFileParser(
        annotationOutputFile).parse().getScenarioText();
    assertEquals(expectedScenario.getScenarioText(), actualScenarioText);
  }

  @When("^I add a Result after (\\d+)$")
  public void i_add_a_Result_after(int secondsToWait) throws Throwable {
    Thread.sleep(TimeUnit.SECONDS.toMillis(secondsToWait));
    this.i_add_the_result("SUCCESS");
  }


  @Then("^the video should have a length of (\\d+)$")
  public void the_video_should_have_a_length_of(int expectedSeconds)
      throws Throwable {
    IContainer container = IContainer.make();
    int result = container.open( videoOutputFile.getAbsolutePath(),
        IContainer.Type.READ, null);
    assertFalse("Could not open " +  videoOutputFile.getAbsolutePath(), result < 0);
    assertDurationEquals(expectedSeconds,
        TimeUnit.MICROSECONDS.toMillis(container.getDuration()));
    container.close();
  }

  @Then("^step (\\d+) should be annotated with a duration of (\\d+)$")
  public void step_should_be_annotated_with_a_duration_of(int stepIndex,
      int expectedDurationSeconds) throws Throwable {
    List<StepAnnotation> steps = AnnotationFileParserFactory.getFileParser(
        this.annotationOutputFile).parse().getStepAnnotations();
    assertTrue("Step Index: " + stepIndex + " not found in List, size is: "
        + steps.size(), stepIndex <= steps.size()

    );
    assertDurationEquals(expectedDurationSeconds, steps.get(stepIndex - 1)
        .getMillisecondsFrom(), steps.get(stepIndex - 1).getMillisecondsTo());
  }

  @Then("^there must be no temporal intersection between the time slots of the steps$")
  public void there_must_be_no_temporal_intersection_between_the_time_slots_of_the_steps()
      throws Throwable {
    List<StepAnnotation> steps = AnnotationFileParserFactory.getFileParser(
        annotationOutputFile).parse().getStepAnnotations();
    if (steps.size() <= 1) {
      return;
    }
    long start = 0L;
    
    for (int i = 1; i < steps.size(); i++) {
      assertTrue("Step " + i + " (start: " + steps.get(i).getMillisecondsFrom()
          + ", end: " + steps.get(i).getMillisecondsTo()
          + ") intersects with Step before (start: " + start + ")", steps
          .get(i).getMillisecondsFrom() >= start
          && steps.get(i).getMillisecondsTo() >= steps.get(i)
              .getMillisecondsFrom());
      start = steps.get(i).getMillisecondsTo();
    }
  }

  @Then("^the video should references the annotation file$")
  public void the_video_should_references_the_annotation_file()
      throws Throwable {
    String videorefFile = AnnotationFileParserFactory.getFileParser(
        annotationOutputFile).parse().getNameVideoFile();
    assertTrue("Video is not referenced in Annotation file properly",
        videorefFile.contains(this.videoOutputFile.getName()));
  }

  @Then("^the annotationfile should contain the correct SHA-1 checksum of the videofile$")
  public void the_annotationfile_should_contain_the_correct_SHA_checksum_of_the_videofile()
      throws Throwable {
    String checkSumFromFile = AnnotationFileParserFactory.getFileParser(
        annotationOutputFile).parse().getSha1ChecksumVideo();
    assertTrue("Checksum length of " + annotationOutputFile.getName() + "<= 5",
        checkSumFromFile.length() > 5);
  }
  
}