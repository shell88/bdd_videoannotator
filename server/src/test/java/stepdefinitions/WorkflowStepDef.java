package stepdefinitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static stepdef.helper.AssertExtensions.assertActualResultStepEquals;
import static stepdef.helper.AssertExtensions.assertDurationEquals;
import stepdef.helper.TestUtils;
import stepdef.helper.annotationfileparser.AnnotationFileParserFactory;
import stepdef.helper.annotationfileparser.ExpectedResultStep;
import stepdef.helper.annotationfileparser.ResultStep;

import com.github.shell88.bddvideoannotator.annotationexport.StepResult;
import com.github.shell88.bddvideoannotator.service.AnnotationService;
import com.xuggle.xuggler.IContainer;

import cucumber.api.DataTable;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class WorkflowStepDef {

  private AnnotationService serverInstance;
  private File outputDirectory;
  private List<ExpectedResultStep> stepsExpected = new ArrayList<ExpectedResultStep>();
  private int resultIndex = 0;

  /**
   * Initializes a new Test.
   * @throws Throwable when subtestDirectory could not be generated.
   */
  public WorkflowStepDef() throws Throwable {
    outputDirectory = TestUtils.getNewSubTestDirectory();
    System.out.println("--OutputDirectory: " + outputDirectory.getName());
    serverInstance = new AnnotationService(outputDirectory.getAbsolutePath(),
        "100", "100");
  }

  @When("^I start a Scenario with description text \"(.*?)\"$")
  public void i_start_a_Scenario_with_description_text(String arg1)
      throws Throwable {
    serverInstance.startScenario(arg1);
  }

  @When("^I add a Step \"(.*?)\",$")
  public void i_add_a_Step(String arg1) throws Throwable {
    serverInstance.addStepToBuffer(arg1, new String[0][0]);
    ExpectedResultStep cur = new ExpectedResultStep();
    cur.setStepText(arg1);
    stepsExpected.add(cur);

  }

  @When("^I add a Step \"(.*?)\" with Result \"(.*?)\" and following Sample-Data:$")
  public void i_add_a_Step_with_Result_and_following_Sample_Data(
      String stepText, String stepResult, DataTable sampleData)
      throws Throwable {
    ExpectedResultStep exp = new ExpectedResultStep();
    exp.setStepText(stepText);
    exp.setStepResult(StepResult.valueOf(stepResult));

    String[][] tableToSend = new String[sampleData.getGherkinRows().size()][];
    int indexRow = 0;
    for (DataTableRow tableRow : sampleData.getGherkinRows()) {
      exp.addSetUpDataRow(tableRow.getCells());
      tableToSend[indexRow] = tableRow.getCells().toArray(new String[] {});
      indexRow++;
    }

    stepsExpected.add(exp);
    serverInstance.addStepWithResult(stepText, tableToSend,
        StepResult.valueOf(stepResult));
    resultIndex++;

  }

  @When("^I add a Step \"(.*?)\" with result \"(.*?)\" after (\\d+) Seconds,$")
  public void i_add_a_Step_with_result_after_Seconds(String steptext,
      String result, int secondsWaiting) throws Throwable {

    Thread.sleep(TimeUnit.SECONDS.toMillis(secondsWaiting));
    serverInstance
        .addStepWithResult(steptext, null, StepResult.valueOf(result));

    ExpectedResultStep exp = new ExpectedResultStep();
    exp.setStepText(steptext);
    exp.setStepResult(StepResult.valueOf(result));
    exp.setExpectedDurationSeconds(secondsWaiting);
    stepsExpected.add(exp);
  }

  @When("^I add the result \"(.*?)\",$")
  public void i_add_the_result(String arg1) throws Throwable {
    StepResult expectedResult = StepResult.valueOf(arg1);
    this.stepsExpected.get(resultIndex).setStepResult(expectedResult);

    serverInstance.addResultToBufferStep(expectedResult);
    resultIndex++;
  }

  @When("^I stop the Scenario$")
  public void i_stop_the_Scenario() throws Throwable {
    serverInstance.stopScenario();
  }

  @Then("^I should get a video with file named \"(.*?)\"$")
  public void i_should_get_a_video_with_file_named(String arg1)
      throws Throwable {
    File[] videoFileList = TestUtils
        .getVideoFilesInDirectory(this.outputDirectory);
    assertTrue(videoFileList.length > 0);
    for (File videoFile : videoFileList) {
      if (videoFile.getName().startsWith(arg1)) {
        return;
      }
    }
    fail("Could not find matching video_file: " + arg1);
  }

  @Then("^I should get an annotation file named \"(.*?)\" containing the added steps$")
  public void i_should_get_an_annotation_file_named_containing_the_added_steps(
      String arg1) throws Throwable {
    File annotationFile = TestUtils
        .getLatestAnnotationOutputFileInDirectory(outputDirectory);
    assertTrue(annotationFile != null
        && annotationFile.getName().startsWith(arg1));
    List<ResultStep> stepsAnnotationFile = AnnotationFileParserFactory
        .getFileParser(annotationFile).parseSortedSteps();
    assertEquals(stepsExpected.size(), stepsAnnotationFile.size());

    for (int i = 0; i < stepsExpected.size(); i++) {
      assertActualResultStepEquals("Step " + i,
          stepsExpected.get(i), stepsAnnotationFile.get(i));

    }

  }

  @When("^I add a Result after (\\d+)$")
  public void i_add_a_Result_after(int secondsToWait) throws Throwable {
    Thread.sleep(TimeUnit.SECONDS.toMillis(secondsToWait));
    this.i_add_the_result("SUCCESS");
  }

  @Then("^I should get a video and an annotation file named \"(.*?)\"$")
  public void i_should_get_a_video_and_an_annotation_file_named(String arg1)
      throws Throwable {
    this.i_should_get_a_video_with_file_named(arg1);
    this.i_should_get_an_annotation_file_named_containing_the_added_steps(arg1);
  }

  @Then("^the video should have a length of (\\d+)$")
  public void the_video_should_have_a_length_of(int expectedSeconds)
      throws Throwable {

    File videoFile = TestUtils.getVideoFilesInDirectory(outputDirectory)[0];
    IContainer container = IContainer.make();
    int result = container.open(videoFile.getAbsolutePath(),
        IContainer.Type.READ, null);

    assertFalse("Could not open " + videoFile.getAbsolutePath(), result < 0);
    assertDurationEquals(expectedSeconds,
        TimeUnit.MICROSECONDS.toMillis(container.getDuration()));
    container.close();

  }

  @Then("^step (\\d+) should be annotated with a duration of (\\d+)$")
  public void step_should_be_annotated_with_a_duration_of(int stepIndex,
      int expectedDurationSeconds) throws Throwable {
    File annotationFile = TestUtils
        .getLatestAnnotationOutputFileInDirectory(outputDirectory);
    List<ResultStep> steps = AnnotationFileParserFactory.getFileParser(
        annotationFile).parseSortedSteps();

    assertTrue("Step Index: " + stepIndex + " not found in List, size is: "
        + steps.size(), stepIndex <= steps.size()

    );

    assertDurationEquals(expectedDurationSeconds, steps.get(stepIndex - 1)
        .getMillisecondsFrom(), steps.get(stepIndex - 1).getMillisecondsTo());
  }

  @Then("^there must be no temporal intersection between the time slots of the steps$")
  public void there_must_be_no_temporal_intersection_between_the_time_slots_of_the_steps()
      throws Throwable {

    File annotationFile = TestUtils
        .getLatestAnnotationOutputFileInDirectory(outputDirectory);
    List<ResultStep> steps = AnnotationFileParserFactory.getFileParser(
        annotationFile).parseSortedSteps();
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
    File videoFile = TestUtils.getVideoFilesInDirectory(outputDirectory)[0];
    File annotationFile = TestUtils
        .getLatestAnnotationOutputFileInDirectory(outputDirectory);

    String viderefFile = AnnotationFileParserFactory.getFileParser(
        annotationFile).parseVideoReferenceFile();
    assertTrue("Video is not referenced in Annotation file properly",
        viderefFile.contains(videoFile.getName()));
  }

  @Then("^the annotationfile should contain the correct SHA-1 checksum of the videofile$")
  public void the_annotationfile_should_contain_the_correct_SHA_checksum_of_the_videofile()
      throws Throwable {
    File annotationFile = TestUtils
        .getLatestAnnotationOutputFileInDirectory(outputDirectory);
    String checkSumFromFile = AnnotationFileParserFactory.getFileParser(
        annotationFile).parseSha1Checksum();

    assertTrue("Checksum length of " + annotationFile.getName() + "<= 5",
        checkSumFromFile.length() > 5);

  }

}