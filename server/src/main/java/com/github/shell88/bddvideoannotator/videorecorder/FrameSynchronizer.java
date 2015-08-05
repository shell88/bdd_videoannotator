package com.github.shell88.bddvideoannotator.videorecorder;

//TODO: Handling of EncodingThread/Recorder Thread, especially for error handling purposes
//TODO: Test TimerTask (eventually it will guarantee screenshots!)
public class FrameSynchronizer implements Runnable{
  
  private ScreenShotBuffer targetQueue;
  private ScreenShotProvider source;

  public FrameSynchronizer(ScreenShotProvider source,
      ScreenShotBuffer targetQueue) {
    this.targetQueue = targetQueue;
    this.source = source;
  }

  @Override
  public void run() {
    targetQueue.addScreenShotData(source.getLatestScreenShot());
  }

}
