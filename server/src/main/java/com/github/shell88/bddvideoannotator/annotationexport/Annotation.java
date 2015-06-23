package com.github.shell88.bddvideoannotator.annotationexport;

/**
 * An Annotation is a represented as a time_referenced (milliseconds_from -
 * milliseconds_to) object that belongs to a tier. A tier is identified by a
 * string and represents the kind of annotation.
 * 
 * @author Hell
 */

public abstract class Annotation {
  /** The tier that the annotation should be added to. */
  private String tierIdentifier;
  /**
   * Starting point for the time alignment of the annotation in milliseconds.
   */
  private Long millisecondsFrom;
  /**
   * The duration of the time alignment starting from {@link #millisecondsFrom}
   * in milliseconds.
   */
  private Long durationMillis;

  /**
   * @param tierIdent {@link #tierIdentifier}.
   */
  public Annotation( String tierIdent) {
    this.tierIdentifier = tierIdent;
  }

  /**
   * @return tierIdent {@link #tierIdentifier}.
   */
  public String getTierIdentifier() {
    return tierIdentifier;
  }

  /** 
   * @return {@link #millisecondsFrom}. 
   */
  public Long getMillisecondsFrom() {
    return millisecondsFrom;
  }

  /**
   * @return Ending point for the time alignment of the annotation in
   *         milliseconds.
   */
  public Long getMillisecondsTo() {
    return millisecondsFrom + durationMillis;
  }

  /**
   * @param millisFrom  {@link #millisecondsFrom}.
   */
  public void setMillisecondsFrom( Long millisFrom) {
    this.millisecondsFrom = millisFrom;
  }

  /**
   *  @return {@link #durationMillis}.
  */
  public Long getDurationMillis() {
    return this.durationMillis;
  }

  /**
   * @param millisDuration {@link #durationMillis}.
   */
  public void setDurationMillis( Long millisDuration) {
    this.durationMillis = millisDuration;
  }

}
