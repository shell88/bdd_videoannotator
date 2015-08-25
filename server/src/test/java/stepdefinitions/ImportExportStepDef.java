package stepdefinitions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import stepdef.helper.TestUtils;
import static stepdef.helper.AssertExtensions.assertActualResultStepEquals;
import static org.junit.Assert.assertEquals;

import com.github.shell88.bddvideoannotator.annotationfile.exporter.EafAnnotationExporter;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.ScenarioAnnotationsDto;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.StepResult;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

import com.github.shell88.bddvideoannotator.annotationfile.exporter.StepAnnotation;
import com.github.shell88.bddvideoannotator.annotationfile.parser.AnnotationFileParser;
import com.github.shell88.bddvideoannotator.annotationfile.parser.AnnotationFileParserFactory;
import com.github.shell88.bddvideoannotator.annotationfile.parser.ExpectedResultStep;

public class ImportExportStepDef {

  private File outputDirectory;

  private String featureTextExpected;
  private String scenarioTextExpected;
  private List<ExpectedResultStep> stepsExpected;

  @Given("^I have an empty outputFile,$")
  public void i_have_an_empty_outputFile() throws Throwable {
    outputDirectory = TestUtils.getNewSubTestDirectory();
  }

  @When("^I export a feature with one scenario and a few steps using the EAF Exporter,$")
  public void i_export_a_feature_with_one_scenario_and_a_few_steps_using_the_EAF_Exporter()
      throws Throwable {

    ScenarioAnnotationsDto exporterDto = new ScenarioAnnotationsDto();

    featureTextExpected = "This is a sample feature";
    exporterDto.setFeatureText(featureTextExpected);

    scenarioTextExpected = "This is a sample scenario";
    exporterDto.setScenarioText(scenarioTextExpected);

    stepsExpected = new ArrayList<ExpectedResultStep>();

    ExpectedResultStep annot1 = new ExpectedResultStep();
    annot1.setSteptext("This is the first step");
    annot1.setMillisecondsFrom((long) 0);
    annot1.setDurationMillis((long) 1000);
    annot1.setStepResult(StepResult.SUCCESS);

    stepsExpected.add(annot1);

    ExpectedResultStep annot2 = new ExpectedResultStep();
    annot2.setSteptext("There is also a second step");
    annot2.setMillisecondsFrom((long) 3000);
    annot2.setDurationMillis((long) 5000);
    annot2.setStepResult(StepResult.ERROR);
    annot2
        .setDataTables(new String[][] { { "Col1", "Col2" }, { "2.4", "Val2" } });

    stepsExpected.add(annot2);

    for (StepAnnotation stepAnnotation : stepsExpected) {
      exporterDto.addStepAnnotation(stepAnnotation);
    }
    
    EafAnnotationExporter exporter = new EafAnnotationExporter(outputDirectory);
    exporter.write(exporterDto);
  }

  @When("^i reimport it the previous exported data should be contained\\.$")
  public void i_reimport_it_the_previous_exported_data_should_be_contained()
      throws Throwable {

    File exportedAnnotationFile = TestUtils
        .getLatestAnnotationOutputFileInDirectory(outputDirectory);
    ScenarioAnnotationsDto dto = AnnotationFileParserFactory.getFileParser(
        exportedAnnotationFile).parse();
    assertEquals(featureTextExpected, dto.getFeatureText());
    assertEquals(scenarioTextExpected, dto.getScenarioText());

    List<StepAnnotation> parsedSteps = dto.getStepAnnotations();

    for (int i = 0; i < stepsExpected.size(); i++) {
      assertActualResultStepEquals("", stepsExpected.get(i), parsedSteps.get(i));
    }

  }

}
