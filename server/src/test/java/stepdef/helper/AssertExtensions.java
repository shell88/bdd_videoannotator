package stepdef.helper;

import com.github.shell88.bddvideoannotator.annotationfile.exporter.StepAnnotation;
import com.github.shell88.bddvideoannotator.annotationfile.parser.ExpectedResultStep;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Provides extended Assert-Methods for use in stepdefinitions.
 * 
 * @author Hell
 * 
 */

public class AssertExtensions extends Assert {

  /**
   * Asserts if the directory path is valid.
   * 
   * @param directory
   *          Directory to check
   * @throws Throwable
   *           Assertion Error when directory is not present
   */
  public static void assertDirectoryExsits(File directory) throws Throwable {
    assertTrue(directory.getPath() + " does not exist!", directory.exists());
    assertTrue(directory.getPath() + " is not a directory!",
        directory.isDirectory());
  }

  /**
   * Asserts if a time interval (milliseconds_from to milliseconds_to) equals
   * the expected seconds specified.
   * 
   * @param expectedSeconds
   *          - Expected Seconds between millis_from and millis_to
   * @param millisFrom
   *          - time interval start in milliseconds
   * @param millisTo
   *          - time interval end in milliseconds
   * @param msg
   *          - Optional text for the error message
   */

  public static void assertDurationEquals(int expectedSeconds, long millisFrom,
      long millisTo, String... msg) {
    assertDurationEquals(expectedSeconds, millisTo - millisFrom);
  }

  /**
   * Asserts if an expected time duration in seconds matches an duration in
   * milliseconds A deviation of 1 Second will be accepted.
   * 
   * @param expectedSeconds
   *          - Expected duration in seconds
   * @param actualMillis
   *          - Actual duration in milliseconds
   * @param msg
   *          - Optional text for the error message
   */

  public static void assertDurationEquals(int expectedSeconds,
      long actualMillis, String... msg) {
    long deviationMillis = Math.abs(TimeUnit.SECONDS.toMillis(expectedSeconds)
        - actualMillis);
    assertTrue(StringUtils.join(msg, "\n")
        + " Duration between expected Seconds: " + expectedSeconds
        + "and actual _millis " + actualMillis + " > 1 second",
        deviationMillis <= TimeUnit.SECONDS.toMillis(1));
  }

  private static void assertDurationEquals(String msg, ExpectedResultStep exp,
      StepAnnotation actual) {

    if (exp.getExpectedDurationSeconds() == null) {
      return;
    }
    assertDurationEquals(exp.getExpectedDurationSeconds(),
        actual.getMillisecondsFrom(), actual.getMillisecondsTo(), msg);

  }

  /**
   * Compares an expected ResultStep with a ResultStep from an annotation
   * output-file.
   * 
   * @param msg
   *          - Text for error Message
   * @param exp
   *          - expected ResultStep
   * @param actual
   *          - actual ResultStep
   */
  public static void assertActualResultStepEquals(String msg,
      ExpectedResultStep exp, StepAnnotation actual) {
    assertStepTextEquals(msg, exp, actual);
    assertSetUpDataEquals(msg, exp, actual);
    assertResultEquals(msg, exp, actual);
    assertDurationEquals(msg, exp, actual);
  }

  private static void assertStepTextEquals(String msg, ExpectedResultStep exp,
      StepAnnotation actual) {
    assertEquals(msg, exp.getSteptext(), actual.getSteptext());
  }

  private static void assertSetUpDataEquals(String msg, ExpectedResultStep exp,
      StepAnnotation actual) {
    
    assertEquals(msg, exp.getDataTables().size(), actual.getDataTables().size());

    for (int i = 0; i < exp.getDataTables().size(); i++) {
      if (exp.getDataTables().get(i).length != actual.getDataTables().get(i)
          .length) {
        fail(msg + " (Size in line " + i + " does not fit)");
      }
      for (int j = 0; j < exp.getDataTables().get(i).length; j++) {
        if (!exp.getDataTables().get(i)[j]
            .equals(actual.getDataTables().get(i)[j])) {
          fail(msg + " (Values in line " + i + " cell " + j + " differ");
        }

      }

    }

  }

  private static void assertResultEquals(String msg, ExpectedResultStep exp,
      StepAnnotation actual) {
    assertEquals(msg, exp.getStepResult().toString(), actual.getStepResult()
        .toString());

  }

  /**
   * Checks if a fileName is in a File array.
   * 
   * @param files            - file array
   * @param needleFileName   - fileName to search for   
   */
  
  public static void assertFileNameInArray(File[] files, String needleFileName) {
    for (File file : files) {
      if (file.getName().matches(needleFileName)) {
        return;
      }
    }
    fail(needleFileName + " not in fileList");
  }

}
