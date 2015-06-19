package stepdef.helper;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import stepdef.helper.annotationfileparser.ExpectedResultStep;
import stepdef.helper.annotationfileparser.ResultStep;

import java.io.File;
import java.util.concurrent.TimeUnit;


/**
 * Provides extended Assert-Methods for use in stepdefinitions.
 * @author Hell
 *
 */

public class AssertExtensions extends Assert {

  /**
   * Asserts if the directory path is valid.
   * @param directory   Directory to check
   * @throws Throwable  Assertion Error when directory is not present
   */
  public static void assertDirectoryExsits(File directory)
      throws Throwable {
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

  public static void assertDurationEquals(int expectedSeconds,
      long millisFrom, long millisTo, String... msg) {
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
    long deviationMillis = Math.abs(TimeUnit.SECONDS
        .toMillis(expectedSeconds) - actualMillis);
    assertTrue(StringUtils.join(msg, "\n")
        + " Duration between expected Seconds: " + expectedSeconds
        + "and actual _millis " + actualMillis + " > 1 second",
        deviationMillis <= TimeUnit.SECONDS.toMillis(1));
  }

  private static void assertDurationEquals(String msg, ExpectedResultStep exp,
      ResultStep actual) {

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
      ExpectedResultStep exp, ResultStep actual) {
    assertStepTextEquals(msg, exp, actual);
    assertSetUpDataEquals(msg, exp, actual);
    assertResultEquals(msg, exp, actual);
    assertDurationEquals(msg, exp, actual);
  }

  private static void assertStepTextEquals(String msg, ExpectedResultStep exp,
      ResultStep actual) {
    assertEquals(msg, exp.getStepText(), actual.getStepText());
  }

  private static void assertSetUpDataEquals(String msg, ExpectedResultStep exp,
      ResultStep actual) {
    if (exp.getSetUpData() == null && actual.getSetUpData() == null) {
      return;
    }
    assertEquals(msg, exp.getSetUpData().size(), actual.getSetUpData().size());

    for (int i = 0; i < exp.getSetUpData().size(); i++) {
      if (exp.getSetUpData().get(i).size() != actual.getSetUpData().get(i)
          .size()) {
        fail(msg + " (Size in line " + i + " does not fit)");
      }
      for (int j = 0; j < exp.getSetUpData().get(i).size(); j++) {
        if (!exp.getSetUpData().get(i).get(j)
            .equals(actual.getSetUpData().get(i).get(j))) {
          fail(msg + " (Values in line " + i + " cell " + j + " differ");
        }

      }

    }

  }

  private static void assertResultEquals(String msg, ExpectedResultStep exp,
      ResultStep actual) {
    assertEquals(msg, exp.getStepResult().toString(), actual.getStepResult()
        .toString());

  }


}
