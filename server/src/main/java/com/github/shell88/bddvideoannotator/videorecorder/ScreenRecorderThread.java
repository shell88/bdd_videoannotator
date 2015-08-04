package com.github.shell88.bddvideoannotator.videorecorder;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;


public class ScreenRecorderThread extends Thread {

  private Rectangle screenBounds;
  private Robot robot;
  private ImageEncoder encoder;

  public ScreenRecorderThread(Rectangle screenBounds, ImageEncoder encoder)
      throws AWTException {
    this.screenBounds = screenBounds;
    this.encoder = encoder;
    robot = new Robot();
  }

  @Override
  public void run() {
    robot.createScreenCapture(screenBounds);
    ScreenShotData data = new ScreenShotData();
    data.image = robot.createScreenCapture(screenBounds);
    data.timestamp = System.currentTimeMillis();
    data.mousePointerLocation = MouseInfo.getPointerInfo().getLocation();
    encoder.addScreenShotData(data);
  }

}
