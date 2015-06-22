package stepdef.subtest;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.formatter.PluginFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;

import org.apache.commons.lang3.ArrayUtils;

import com.github.shell88.bddvideoannotator.javaadapters.CucumberReportingAdapter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
  private final String pathOfReportingAdapterClass = com.github.shell88
      .bddvideoannotator.javaadapters.CucumberReportingAdapter.class.getCanonicalName(); 
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

    PluginFactory spyedCucumberPluginFactory = spy(PluginFactory.class);
    doReturn(mockedAdapter).when(spyedCucumberPluginFactory).create(
        pathOfReportingAdapterClass);

    /*
     * CLASS is not mockable by Mockito (final Class). As Cucumber-JVM will
     * create a new Instance of the adapter using the create()-Method in Class
     * Plugin-Factory, this method will be mocked. Unfortunately there is no way
     * in cucumber.api.cli.Main to provide a Mocked Plugin-Factory directly
     */
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
    
    String[] cucumberArgs = new String[] { dirContents.getAbsolutePath(),
        "--glue", dirContents.getName(), "-p", pathOfReportingAdapterClass };
    
    RuntimeOptions runtimeOptions = new RuntimeOptions(
        spyedCucumberPluginFactory, new ArrayList<String>(asList(cucumberArgs)));
    ResourceLoader resourceLoader = new MultiLoader(classLoader);
    ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader,
        classLoader);
    Runtime runtime = new Runtime(resourceLoader, classFinder, classLoader,
        runtimeOptions);
    try {
      runtime.run();
    } catch (IOException e) {
      this.getUncaughtExceptionHandler().uncaughtException(this, e);
      return;
    }
    assertTrue("Return Code of Cucumber-Thread != 0", runtime.exitStatus() == 0);
    verify(spyedCucumberPluginFactory).create(pathOfReportingAdapterClass);
    System.out.println("---END OF SUBTEST");
  }

}
