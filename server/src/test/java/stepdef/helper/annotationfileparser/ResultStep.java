package stepdef.helper.annotationfileparser;

import com.github.shell88.bddvideoannotator.annotationexport.StepResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the steps that are parsed from an annotation output file. Can be
 * compared with an {@link ExpectedResultStep} over using 
 * {@link stepdef.helper.AssertExtensions#assertActualResultStepEquals
 * (String, ExpectedResultStep, ResultStep)}
 * 
 * @author Hell
 */

public class ResultStep implements Comparable<ResultStep> {

  private StepResult stepResult;
  private long millisecondsTo;
  private long millisecondsFrom;

  private String stepText;
  private List<List<String>> setUpData;

  public String getStepText() {
    return stepText;
  }

  public void setStepText(String stepText) {
    this.stepText = stepText;
  }

  public List<List<String>> getSetUpData() {
    return setUpData;
  }

  /**
   * @param dataRow a row of a datatable corresponding to this step.
   */
  public void addSetUpDataRow(List<String> dataRow) {
    if (setUpData == null) {
      setUpData = new ArrayList<List<String>>();
    }
    setUpData.add(dataRow);
  }

  public StepResult getStepResult() {
    return stepResult;
  }

  public void setStepResult(StepResult stepResult) {
    this.stepResult = stepResult;
  }

  public long getMillisecondsTo() {
    return millisecondsTo;
  }

  public void setMillisecondsTo(long millisecondsTo) {
    this.millisecondsTo = millisecondsTo;
  }

  public long getMillisecondsFrom() {
    return millisecondsFrom;
  }

  public void setMillisecondsFrom(long millisecondsFrom) {
    this.millisecondsFrom = millisecondsFrom;
  }

  /**
   * Used to sort a list of ResultStep in the correct time order.
   * @param other  another ResultStep
   * @return   compareValue
   */

  @Override
  public int compareTo(ResultStep other) {
    return Long.compare(millisecondsTo, other.millisecondsTo);
  }

}
