package com.github.shell88.bddvideoannotator.videorecorder;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;

public class ScreenRecorderThread extends Thread implements ScreenShotProvider{
  
  private ScreenShotData latestScreenShotData;
  
  private Rectangle screenBounds;
  private Robot robot;


  public ScreenRecorderThread(Rectangle screenBounds, ScreenShotBuffer encoder)
      throws AWTException {
    this.screenBounds = screenBounds;
    robot = new Robot();
    //initializing Screenshot
    acutalizeScreenShot();
  }

  @Override
  public void run() {
    while (!this.isInterrupted()) {
      acutalizeScreenShot();
    }
  }

  private void acutalizeScreenShot() {
    ScreenShotData data = new ScreenShotData();
    data.image = robot.createScreenCapture(screenBounds);
    data.timestamp = System.currentTimeMillis();
    data.mousePointerLocation = MouseInfo.getPointerInfo().getLocation();
    this.latestScreenShotData = data;
  }

  @Override
  public ScreenShotData getLatestScreenShot() {
    return this.latestScreenShotData;
  }

}
