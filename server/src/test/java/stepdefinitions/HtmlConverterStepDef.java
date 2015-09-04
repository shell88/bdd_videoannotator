package stepdefinitions;

import static org.junit.Assert.assertTrue;

import com.github.shell88.bddvideoannotator.annotationfile.converter.HtmlConverter;
import stepdef.helper.AssertExtensions;
import stepdef.helper.TestUtils;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.File;

public class HtmlConverterStepDef {

  private final File testResourcesFolder = new File(
      "src/test/resources/htmlconvertertest");
  private final File outputDirectory;

  public HtmlConverterStepDef() {
    outputDirectory = TestUtils.getNewSubTestDirectory();
  }

  private int cntResourceFiles = 0;

  @Given("^I have a folder with annotation files and videos from the annotation server$")
  public void i_have_a_folder_with_annotation_files_and_videos_from_the_annotation_server()
      throws Throwable {
    assertTrue(testResourcesFolder.exists());
    cntResourceFiles = testResourcesFolder.listFiles().length;
    assertTrue("No files in testresourceFolder", cntResourceFiles > 0);
  }

  @When("^I convert these to HTML$")
  public void i_convert_these_to_HTML() throws Throwable {
    new HtmlConverter(testResourcesFolder, outputDirectory).convert();
  }

  @Then("^I should get an index\\.html including all stepdata and html5-compatible videos\\.$")
  public void i_should_get_an_index_html_including_all_stepdata_and_html_compatible_videos()
      throws Throwable {
    File[] filesInOutputFolder = outputDirectory.listFiles();
    AssertExtensions.assertFileNameInArray(filesInOutputFolder, "index.html");
    AssertExtensions.assertFileNameInArray(filesInOutputFolder, ".*\\.mp4");
  }

}
