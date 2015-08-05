package com.github.shell88.bddvideoannotator.videorecorder;

import java.util.TimerTask;

//TODO: Handling of EncodingThread/Recorder Thread, especially for error handling purposes
public class FrameSynchronizer extends TimerTask{
  
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
