package testrunner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(plugin = "com.github.shell88.bddvideoannotator.javaadapters.CucumberReportingAdapter", features = { "src/features" }, glue = "stepdefinition")
public class Testrunner {


}
