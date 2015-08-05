package com.github.shell88.bddvideoannotator.videorecorder;


/*
 * Idee: Thread 1: macht immer Screenshots und hat immer einen aktuellen Screenshot parat
 *       FrameHandler ist auf die FrameRate getimed und ruft von Thread 1 den letzten aktuellen Screenshot ab
 *       (immer den aktuellesten)
 *       Framehandler fügt diesen sodann in eine Encoding Queue rein die bis zum letzten Frame abgearbeitet
 *       wird (per join synchronisiert)
 *            
 */

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
