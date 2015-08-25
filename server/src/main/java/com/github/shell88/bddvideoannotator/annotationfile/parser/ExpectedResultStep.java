package com.github.shell88.bddvideoannotator.annotationfile.parser;

import com.github.shell88.bddvideoannotator.annotationfile.exporter.StepAnnotation;
import cucumber.api.DataTable;

import gherkin.formatter.model.DataTableRow;

/**
 * Represents an expected Step in a Subtest.
 * @author Hell
 *
 */
public class ExpectedResultStep extends StepAnnotation {

  private Integer expectedDurationSeconds;

  public void setExpectedDurationSeconds(int secondsexpected) {
    this.expectedDurationSeconds = secondsexpected;
  }

  public Integer getExpectedDurationSeconds() {
    return this.expectedDurationSeconds;
  }
  
  /**
   * @param dataTable DataTable element From a CucumberJVM-Feature-File.
   */
  public void setExpectedDataTable(DataTable dataTable) {
    for (DataTableRow tableRow : dataTable.getGherkinRows()) {
      this.addDataTableRow(tableRow.getCells().toArray(
          new String[tableRow.getCells().size()]));
    }

  }

}
