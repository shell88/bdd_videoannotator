package com.github.shell88.bddvideoannotator.videorecorder;

/**
 * AdapterInterface so that different videorecorder could be used by 
 * {@link com.github.shell88.bddvideoannotator.service.AnnotationService}.
 * 
 * @author Hell
 */

public interface VideoRecorder {

  /**
   * Starts a new screencast.
   * 
   * @throws Exception
   *           - when screencast could not be started (e.g. Fileoutput-Errors)
   */
  void startVideoRecording() throws Exception;

  /**
   * Stops the screencast.
   * 
   * @throws Exception
   *           - when it is not possible to close the screencast-stream.
   */
  void stopVideoRecording() throws Exception;

  /**
   * @return - The Unix-timestamp when the screencast was started.
   */

  long getStartTimestamp();
  
  //TODO: getLatestVideoFile.... multiple Start/Stops will produce another video file
  /**
   * @return - Filepath where the video is stored.
   */
  String getPathToOutputFile();
    
}
