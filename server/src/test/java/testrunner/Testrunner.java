package testrunner;

/**
 * TestRunner for jUnit.
 */


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import stepdef.helper.TestUtils;

import java.io.IOException;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty" }, 
                 features = { "src/test/java/features" },
                 glue = "stepdefinitions", monochrome = true)
public class Testrunner {

  @BeforeClass
  public static void emptyAnnotationsFolder() throws IOException {
    TestUtils.cleanTestOutputFolders();
  }

}