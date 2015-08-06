package com.github.shell88.bddvideoannotator.videorecorder;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class EncodingThread extends Thread implements ScreenShotBuffer {

  private Dimension capturingArea;
  private File outputVideo;
  private int screensPerSeconds;
  
  private boolean recordingFinalized = false;
  private Queue<ScreenShotData> encodingQueue;
  private int frameNo = 0;
  private Image mousePointerImage;
  
  public EncodingThread(File outputVideo, int screensPerSeconds, Dimension capturingArea) {
    this.outputVideo = outputVideo;
    this.screensPerSeconds = screensPerSeconds;
    this.capturingArea = capturingArea;
    this.encodingQueue = new ConcurrentLinkedQueue<ScreenShotData>();
    initializeMousePointerImage();
    // Because Encoding is joined to test Execution it should be performed just
    // in time
    setPriority(Thread.NORM_PRIORITY + 2);
  }
  
  private void initializeMousePointerImage() {
    URL resource = ClassLoader.getSystemResource("Cursor.png");    
    if (resource == null) {
      System.err.println("Could not find mousePointerImage!");
    }
    mousePointerImage =  Toolkit.getDefaultToolkit().createImage(resource);
  }

  protected File getVideoOutputFile() {
    return this.outputVideo;
  }

  protected int getScreensPerSeconds() {
    return this.screensPerSeconds;
  }

  protected int getCurrentFrameNumber() {
    return frameNo;
  }
  
  @Override
  public void addScreenShotData(ScreenShotData data) {
    this.encodingQueue.add(data);
  }
  
  @Override
  public void run() {
    ScreenShotData data;
    while (!recordingFinalized || encodingQueue.size() > 0) {
      data = encodingQueue.poll();
      if (data != null) {
        long startEncoding = System.currentTimeMillis();
        addMousePointerToScreenShot(data);
        encodeScreenShot(data.image);
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

  public void addMousePointerToScreenShot(ScreenShotData data) {
    if (data.mousePointerLocation.x > capturingArea.width
        || data.mousePointerLocation.y > capturingArea.height) {
      return;
    }
    
    Graphics2D videoGraphics = data.image.createGraphics();
    videoGraphics.setRenderingHint(RenderingHints.KEY_DITHERING,
        RenderingHints.VALUE_DITHER_DISABLE);
    videoGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
        RenderingHints.VALUE_COLOR_RENDER_SPEED);
    videoGraphics.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_SPEED);

    videoGraphics.drawImage(data.image, 0, 0, null);
    videoGraphics.drawImage(mousePointerImage, data.mousePointerLocation.x,
        data.mousePointerLocation.y, null);
  }

  public abstract void encodeScreenShot(BufferedImage data);

  public abstract void finish();
}
