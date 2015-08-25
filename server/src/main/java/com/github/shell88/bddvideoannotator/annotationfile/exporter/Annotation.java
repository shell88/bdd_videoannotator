package com.github.shell88.bddvideoannotator.annotationfile.exporter;


/**
 * An Annotation is a represented as a time_referenced (milliseconds_from -
 * milliseconds_to) object that belongs to a tier. A tier is identified by a
 * string and represents the kind of annotation.
 * 
 * @author Hell
 */

public abstract class Annotation implements Comparable<Annotation> {

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

  
  public void setMillisecondsTo(Long millisTo) {
    setDurationMillis(millisTo - getMillisecondsFrom());
  }
  
  /**
   * Used to sort a list of ResultStep in the correct time order.
   * @param other  another ResultStep
   * @return   compareValue
   */

  @Override
  public int compareTo(Annotation other) {
    return Long.compare(getMillisecondsTo(), other.getMillisecondsTo());
  }
  
  
}
