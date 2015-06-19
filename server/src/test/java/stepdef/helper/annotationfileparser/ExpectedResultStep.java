package stepdef.helper.annotationfileparser;

/**
 * Represents an epxpected Step {@link ResultStep}.
 * @author Hell
 *
 */

public class ExpectedResultStep extends ResultStep {

  private Integer expectedDurationSeconds;

  public void setExpectedDurationSeconds(int secondsexpected) {
    this.expectedDurationSeconds = secondsexpected;
  }

  public Integer getExpectedDurationSeconds() {
    return this.expectedDurationSeconds;
  }

}
