package com.github.shell88.bddvideoannotator.annotationexport;

/**
 * Represents an annotation for an executed Gherkin-step.
 * @author Hell
 */

public class StepAnnotation extends Annotation {

  /** The Gherkin-Steptext (e.g. "Given i execute a step") defined in the
   * featureFile.*/
  private String steptext;
  /** The result of executing this step.*/
  private StepResult stepResult;
  /** An optional datatable to this step defined in the featureFile.*/
  private String[][] dataTable;
  /** Initializes a new StepAnnotation using the tierIdentifier "Steps".
   * {@link Annotation#getTierIdentifier()} */
  public StepAnnotation() {
    super("Steps");
  }
  
  /** 
   * @return {@link #steptext}.
  */
  public  String getSteptext() {
    return steptext;
  }

  /**
   * @param stepText {@link #steptext}.
   */
  public  void setSteptext( String stepText) {
    this.steptext = stepText;
  }

  /**
   *  @return {@link #stepResult}.
   */
  public  StepResult getStepResult() {
    return stepResult;
  }
  
  /** 
   * @param stepRes {@link #stepResult}. 
   */
  public  void setStepResult( StepResult stepRes) {
    this.stepResult = stepRes;
  }

  /**
   * @return dataTable {@link #dataTable}.
   */
  public  String[][] getDataTables() {
    return dataTable;
  }

  /**
   * @param dataTab {@link #dataTable}.
   */
  public  void setDataTables( String[][] dataTab) {
    this.dataTable = dataTab;
  }

}
