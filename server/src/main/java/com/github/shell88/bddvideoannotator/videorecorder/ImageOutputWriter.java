package com.github.shell88.bddvideoannotator.videorecorder;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageOutputWriter extends EncodingThread {


  public ImageOutputWriter(File out, int imagesPerSeconds, Dimension capturingArea) {
    super(out, imagesPerSeconds, capturingArea);
  }

  @Override
  public void encodeScreenShot(BufferedImage image) {
    // TODO Auto-generated method stub
    File outputFile = new File(super.getVideoOutputFile().getParentFile(), "img" + super.getCurrentFrameNumber() + ".png");
    try {
      ImageIO.write(image,  "png",  outputFile);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void finish() {
    // TODO Auto-generated method stub
    
  }



}
