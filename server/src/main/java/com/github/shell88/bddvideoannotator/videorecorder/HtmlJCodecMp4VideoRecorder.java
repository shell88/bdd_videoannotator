package com.github.shell88.bddvideoannotator.videorecorder;

import com.github.shell88.bddvideoannotator.service.Helper;





import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jcodec.api.SequenceEncoder;


//TODO: Mauszeiger funktioniert noch nicht

public class HtmlJCodecMp4VideoRecorder implements VideoRecorder {

  private File outputFile;
  private ScreenRecorderThread recorderThread;
  private final int imagesPerSeconds = 30;
  
  
  public HtmlJCodecMp4VideoRecorder(File outputDirectory,
      String fileNamePrefix, Dimension capturingArea) throws IOException,
      AWTException {

    outputFile = Helper.createNewOutputFile(outputDirectory, fileNamePrefix,
        "mp4");
    SequenceEncoder encoder = new SequenceEncoder(outputFile);
    
    recorderThread = new ScreenRecorderThread(encoder, imagesPerSeconds,
        new Rectangle(capturingArea));
  }
  
  @Override
  public void startVideoRecording() throws Exception {
    // TODO Auto-generated method stub
    recorderThread.start();  
  }

  @Override
  public void stopVideoRecording() throws Exception {
    recorderThread.stopRecording();
    recorderThread.join();
    
  }

  @Override
  public long getStartTimestamp() {
    return recorderThread.getStartTimestamp();
  }

  @Override
  public String getPathToOutputFile() {
    // TODO Auto-generated method stub
    return this.outputFile.getAbsolutePath();
  }

}




class ScreenRecorderThread extends Thread {
  private SequenceEncoder encoder;
  private int imagesPerSecond;
  private Rectangle screenBounds;
  
  private Robot robot;
  private long startTime;
  private long endTime; 
  
  private boolean isRecording = false;
  
  public ScreenRecorderThread(SequenceEncoder encoder, int imagesPerSecond,
      Rectangle screenBounds) throws AWTException {
    this.encoder = encoder;
    this.screenBounds = screenBounds;
    this.imagesPerSecond = imagesPerSecond;
    robot = new Robot();
  }

  public long getStartTimestamp() {
    return this.startTime;
  }

  public long getEndTimestamp() {
    return this.endTime;
  }
  
  public void stopRecording() {
    this.isRecording = false;
  }
   
  @Override
  public void run() {
  
    isRecording = true;
    List<BufferedImage> images = new ArrayList<BufferedImage>();
    this.startTime = System.currentTimeMillis(); 

    
    long waitTimeMillis = 40; //TimeUnit.SECONDS.toMillis(imagesPerSecond);
    long calculatedSleep;
    while (isRecording) {
      endTime = System.currentTimeMillis();
      images.add(robot.createScreenCapture(screenBounds));
      try {
        calculatedSleep = (waitTimeMillis - (System.currentTimeMillis() - endTime));
        calculatedSleep = (calculatedSleep < 0) ? 0 : calculatedSleep;
        sleep(calculatedSleep);
      } catch (InterruptedException e) {
        break;
      }
    }  
    System.out.println("Recorded images: " + images.size());
    
    //encode all images
    for (BufferedImage image : images) {
      try {
        encoder.encodeImage(image);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        throw new RuntimeException("Could not add Images to Video");
      }
    }
    try {
      encoder.finish();
    } catch (IOException e) {
      throw new RuntimeException("Could not finish file");
    }
    

  }
  
  

}
