package com.github.shell88.bddvideoannotator.javaadapters;

/**
 * Exception indicating Problems with the serverProcess.
 * @author Hell
 *
 */

public class ServerConnectorException extends RuntimeException {

  /**
   * Default serail UID.
   */
  private static final long serialVersionUID = 1L;

  public ServerConnectorException(String message) {
    super(message);
  }

  public ServerConnectorException(String message, Throwable error) {
    super(message, error);
  }

}
