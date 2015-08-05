package com.github.shell88.bddvideoannotator.videorecorder;

import static java.lang.Math.max;

import com.github.shell88.bddvideoannotator.service.Helper;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Timer;


public class H264VideoRecorder implements VideoRecorder {

  private File outputFile;
  private Dimension capturingArea;
  
  private Timer frameTimer;
  private FrameSynchronizer frameSynchronizer; 
  private ScreenRecorderThread recorderThread;
  private EncodingThread encoderThread;
  

  private long startRecordingTimestamp;
  private long endRecordingTimestamp;

  private final int imagesPerSeconds = 15;

  public H264VideoRecorder(File outputDirectory,
      String fileNamePrefix, Dimension capturingArea) throws IOException,
      AWTException, InterruptedException {
    outputFile = Helper.createNewOutputFile(outputDirectory, fileNamePrefix,
        "mp4");
    this.capturingArea = capturingArea;
      
  }

  @Override
  public void startVideoRecording() throws Exception {   
    recorderThread = new ScreenRecorderThread(new Rectangle(capturingArea),
        encoderThread);
    encoderThread = new H264EncodingHumbleVideo(outputFile, imagesPerSeconds,
        capturingArea);

    double frameRateMillis = 1000 / imagesPerSeconds;
    int screenGrabRateMillis = max(1, (int) frameRateMillis);
       
    frameTimer = new Timer("FrameSynchronizer", false);
    frameSynchronizer = new FrameSynchronizer(recorderThread, encoderThread);
    frameTimer.scheduleAtFixedRate(frameSynchronizer, 0, screenGrabRateMillis);
    
    recorderThread.start();
    encoderThread.start();
    startRecordingTimestamp = System.currentTimeMillis();
  }

  @Override
  public void stopVideoRecording() throws Exception {
    frameTimer.cancel();
    recorderThread.interrupt();
    endRecordingTimestamp = System.currentTimeMillis();
    encoderThread.recordingFinalized();
    encoderThread.join();
    
  }

  @Override
  public String getPathToOutputFile() {
    // TODO Auto-generated method stub
    return this.outputFile.getAbsolutePath();
  }

  @Override
  public long getStartTimestamp() {
    return startRecordingTimestamp;
  }

  @Override
  public long getEndTimestamp() {
    return endRecordingTimestamp;
  }

}
