package testrunner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import stepdef.helper.TestUtils;

import java.io.IOException;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = { "pretty" },
    features = { "src/test/java/features" }, 
    glue = "stepdefinitions", monochrome = true
    )   
public class TestrunnerAdapter {

  @BeforeClass
  public static void cleanTempDirectory() throws IOException {
    FileUtils.deleteDirectory(TestUtils.testOutputDirectory);
  }

}