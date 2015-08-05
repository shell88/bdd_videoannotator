package com.github.shell88.bddvideoannotator.videorecorder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class EncodingThread extends Thread implements ScreenShotBuffer {

  private boolean recordingFinalized = false;
  private Queue<ScreenShotData> imageBuffer;
  private int frameNo = 0;
  
  public EncodingThread() {
    this.imageBuffer = new ConcurrentLinkedQueue<ScreenShotData>();
  }

  public int getCurrentFrameNumber() {
    return frameNo;
  }
  
  @Override
  public void addScreenShotData(ScreenShotData data) {
    this.imageBuffer.add(data);
  }
  
  @Override
  public void run() {
    ScreenShotData data;
    while (!recordingFinalized || imageBuffer.size() > 0) {
      data = imageBuffer.poll();
      if (data != null) {
        long startEncoding = System.currentTimeMillis();
        encodeScreenShotData(data);
        frameNo++;
        System.out.println("Encoding frame " + frameNo + " took: "
            + (System.currentTimeMillis() - startEncoding));
      }
    }
    finish();
  }

  public void recordingFinalized() {
    this.recordingFinalized = true;
  }

  public abstract void encodeScreenShotData(ScreenShotData data);

  public abstract void finish();
}
