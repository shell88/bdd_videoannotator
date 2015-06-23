package com.github.shell88.bddvideoannotator.annotationexport;

/**
 * BDD-Frameworks represent results in different ways. Annotation-Service will
 * unify these to one of 4 values (see also "BDD in Action" by John F. Smart):
 * @author Hell
 */
public enum StepResult {
  /** Step was executed successfully.*/
  SUCCESS,
  /** Method not implemented or was skipped due to a previous.*/
  SKIPPED,
  /** Some assertions failed. */
  FAILURE, 
  /** Unexpected errors while executing the step. */
  ERROR
}
