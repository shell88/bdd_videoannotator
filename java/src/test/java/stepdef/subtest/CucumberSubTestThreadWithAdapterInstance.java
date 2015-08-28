package stepdef.subtest;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.ArrayUtils;

import com.github.shell88.bddvideoannotator.javaadapters.CucumberReportingAdapter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Represents a Cucumber-JVM-testrun that will be started in an own thread. It
 * will use an instance of
 * {@link com.github.shell88.bddvideoannotator.javaadapters.CucumberReportingAdapter} 
 * that is specified in the constructor. So this class enables mocking of
 * {@link com.github.shell88.bddvideoannotator.javaadapters.CucumberReportingAdapter} 
 * in a Cucumber-JVM-testrun.
 * 
 * @author Hell
 *
 */

public class CucumberSubTestThreadWithAdapterInstance extends Thread {
  
  private final String pathOfTestProxyClass =
      stepdef.subtest.CucumberReportingAdapterTestProxy.class.getCanonicalName();
  private CucumberReportingAdapter mockedAdapter;
  private File dirContents;
  private ExceptionCollector exceptionCollector;

  /**
   * @param mockedAdapter
   *          - the instance that should be used for the testrun
   * @param dirContents
   *          - The directory containing the feature file(s) and the
   *          stepdefinitions. Feature-files must be located in dirContents, for
   *          the glue-Path the stepdefinition-Classes must be located in a
   *          package named like dirContents Example: +dir_content *test.feature
   *          *dir_content - StepDef.class
   */
  public CucumberSubTestThreadWithAdapterInstance(
      CucumberReportingAdapter mockedAdapter, File dirContents) {
    this.mockedAdapter = mockedAdapter;
    this.dirContents = dirContents;
    exceptionCollector = new ExceptionCollector();
    this.setUncaughtExceptionHandler(exceptionCollector);

  }

  public List<Throwable> getThrownExceptions() {
    return this.exceptionCollector.getThrownExceptions();
  }

  @Override
  public void run() {

    // For better orientation
    System.out.println("--STARTING SUBTEST IN "
        + this.dirContents.getAbsolutePath());
    
    CucumberReportingAdapterTestProxy.setUnderlyingInstance(mockedAdapter);
    
    String[] argsCucumber = new String[] {
        dirContents.getAbsolutePath(), 
        "--glue", dirContents.getName(),
        "-p", pathOfTestProxyClass
    };
    
    //Stepdefinition must be detectable by Cucumber-JVM
    URL[] urlstoLoad = ((URLClassLoader) (ClassLoader.getSystemClassLoader()))
        .getURLs();

    try {
      urlstoLoad = ArrayUtils.add(urlstoLoad, dirContents.getAbsoluteFile()
          .toURI().toURL());
    } catch (MalformedURLException e1) {
      this.getUncaughtExceptionHandler().uncaughtException(this, e1);
      return;
    }
    URLClassLoader classLoader = new URLClassLoader(urlstoLoad);
    this.setContextClassLoader(classLoader);
    
    
    byte retValue = 1;
    try {
      //CucumberJVM will alway use Class Loader of current thread, and not that one
      //specified in run Method (see https://github.com/cucumber/cucumber-jvm/issues/880)
      retValue =  cucumber.api.cli.Main.run(argsCucumber,
                  Thread.currentThread().getContextClassLoader());
    } catch (IOException e) {
      this.getUncaughtExceptionHandler().uncaughtException(this, e);
      return;
    }

    assertTrue("Return Code of Cucumber-Thread != 0", retValue == 0);
    System.out.println("---END OF SUBTEST");

  }

}
