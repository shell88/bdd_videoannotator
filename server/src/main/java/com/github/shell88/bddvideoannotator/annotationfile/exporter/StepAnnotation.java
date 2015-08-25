package com.github.shell88.bddvideoannotator.annotationfile.exporter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an annotation for an executed Gherkin-step.
 * 
 * @author Hell
 */

public class StepAnnotation extends Annotation {

  /**
   * The Gherkin-Steptext (e.g. "Given i execute a step") defined in the
   * featureFile.
   */
  private String steptext;
  /** The result of executing this step. */
  private StepResult stepResult;
  /** An optional datatable to this step defined in the featureFile. */
  private List<String[]> dataTable;

  public StepAnnotation() {
    dataTable = new ArrayList<String[]>();
  }
  
  /**
   * Creates a singleString representation
   * for the StepData.
   */
  public String toString() { 
    String toSingleString = getSteptext();
    toSingleString += stringifyDatatable();
    toSingleString += " " + stepResult.toString();
    return toSingleString;
  }
  
  /**
   * Converts a datatable to a flat string representation. Each line will be
   * seperated by "||". Cells are seperated by a "|". Example: Line1Value1,
   * Line1Value2 Line2Value1, Line2Value2 will be formatted as follows:
   * ||Line1Value1|Line1Value2||Line2Value1|Line2Value2||
   * 
   * @return the stringified datatable
   */
  private String stringifyDatatable() {
    String stringified = "";
    
    if (dataTable != null && dataTable.size() > 0) {
      ArrayList<String> rowsStringified = new ArrayList<String>(1);
      for (String[] row : dataTable) {
        rowsStringified.add(StringUtils.join(row, "|"));
      }

      stringified += "||" + StringUtils.join(rowsStringified, "||") + "||";
    }

    return stringified;
  }
  
  
  
  /**
   * @return {@link #steptext}.
   */
  public String getSteptext() {
    return steptext;
  }
  

  /**
   * @param stepText
   *          {@link #steptext}.
   */
  public void setSteptext(String stepText) {
    this.steptext = stepText;
  }

  /**
   * @return {@link #stepResult}.
   */
  public StepResult getStepResult() {
    return stepResult;
  }

  /**
   * @param stepRes
   *          {@link #stepResult}.
   */
  public void setStepResult(StepResult stepRes) {
    this.stepResult = stepRes;
  }

  /**
   * @return dataTable {@link #dataTable}.
   */
  public List<String[]> getDataTables() {
    return this.dataTable;
  }
/**
 * @return The dataTable.
 */
  public String[][] getDataTable() {
    List<String[]> dataTables = this.getDataTables();
    String[][] ret = new String[dataTables.size()][];
    for (int i = 0; i < dataTables.size(); i++) {
      ret[i] = dataTables.get(i);
    }
    return ret;
  }

  /**
   * @param dataTab
   *          {@link #dataTable}.
   */
  public void setDataTables(String[][] dataTab) {
    if (dataTab == null) {
      return;
    }
    for (String[] dataRow : dataTab) {
      addDataTableRow(dataRow);
    }
  }

  /**
   * Adds a single tableRow to the dataTable.
   * @param tableRow  the tableRow to add.
   */
  
  public void addDataTableRow(String[] tableRow) {

    if (!dataTable.isEmpty() && dataTable.get(0).length != tableRow.length) {
      throw new ArrayIndexOutOfBoundsException(
          "Length of tableRow does not fit to columns of previous added lines");
    }
    dataTable.add(tableRow);
  }

}
