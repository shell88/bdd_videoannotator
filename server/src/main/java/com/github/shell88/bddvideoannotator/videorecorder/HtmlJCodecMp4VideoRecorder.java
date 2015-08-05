package com.github.shell88.bddvideoannotator.videorecorder;

import static java.lang.Math.max;

import com.github.shell88.bddvideoannotator.service.Helper;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//TODO: Mauszeiger funktioniert noch nicht
//TODO: length of video images PerSeconds

public class HtmlJCodecMp4VideoRecorder implements VideoRecorder {

  private File outputFile;
  private Dimension capturingArea;
  
  private ScheduledThreadPoolExecutor scheduledExecutor;
  private FrameSynchronizer frameSynchronizer; 
  private ScreenRecorderThread recorderThread;
  private EncodingThread encoderThread;

  private long startRecordingTimestamp;
  private long endRecordingTimestamp;

  private final int imagesPerSeconds = 10;

  public HtmlJCodecMp4VideoRecorder(File outputDirectory,
      String fileNamePrefix, Dimension capturingArea) throws IOException,
      AWTException, InterruptedException {
    outputFile = Helper.createNewOutputFile(outputDirectory, fileNamePrefix,
        "mp4");
    this.capturingArea = capturingArea;
      
  }

  @Override
  public void startVideoRecording() throws Exception {
    double frameRateMillis = 1000 / imagesPerSeconds;
    int screenGrabRateMillis = max(1, (int) frameRateMillis);
    // encoderThread = new EncodingThreadJCodec(outputFile, imagesPerSeconds);
    recorderThread = new ScreenRecorderThread(new Rectangle(capturingArea),
        encoderThread);
    scheduledExecutor = new ScheduledThreadPoolExecutor(1);
    encoderThread = new EncodingHumbleVideo(outputFile, imagesPerSeconds,
        capturingArea);
    frameSynchronizer = new FrameSynchronizer(recorderThread, encoderThread);
    scheduledExecutor.scheduleAtFixedRate(frameSynchronizer, 0,
        screenGrabRateMillis, TimeUnit.MILLISECONDS);
    recorderThread.start();
    encoderThread.start();
    startRecordingTimestamp = System.currentTimeMillis();

  }

  @Override
  public void stopVideoRecording() throws Exception {
    scheduledExecutor.shutdown();
    recorderThread.interrupt();
    endRecordingTimestamp = System.currentTimeMillis();
    encoderThread.recordingFinalized();
    encoderThread.join();
    System.out.println("Duration of recording: " + (this.getEndTimestamp() - this.getStartTimestamp()));
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
