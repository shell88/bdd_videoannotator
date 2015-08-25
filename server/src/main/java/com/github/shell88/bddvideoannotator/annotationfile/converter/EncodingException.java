package com.github.shell88.bddvideoannotator.annotationfile.converter;


/**
 * Exception that was thrown during {@link FfmpegCommandLineH264Encoder#call()}.
 * @author Hell
 *
 */

public class EncodingException extends Exception {

  private static final long serialVersionUID = 1L;

  public EncodingException(String message) {
      super(message);
  }
}
