package com.ufp.demo.operations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class BlurOperation implements Operation {
    private static Logger logger = Logger.getLogger(BlurOperation.class);

    public InputStream process(InputStream inputStream) {
	ByteArrayOutputStream outputStream = null;
	try {
	    BufferedImage bufferedImage = ImageIO.read(inputStream);

	    Kernel kernel = new Kernel(3, 3, 
				       new float[] { 1f/9f, 1f/9f, 1f/9f, 
						     1f/9f, 1f/9f, 1f/9f, 
						     1f/9f, 1f/9f, 1f/9f}); 
	    BufferedImageOp op = new ConvolveOp(kernel); 

	    int width = bufferedImage.getWidth();
	    int height = bufferedImage.getHeight();
	    BufferedImage tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	    for(int i=0; i<width; i++) {
		for(int j = 0; j<height; j++)
		    tmp.setRGB(i, j, bufferedImage.getRGB(i, j));
	    }

	    bufferedImage = op.filter(tmp, null); 
	    outputStream = new ByteArrayOutputStream();
	    ImageIO.write(bufferedImage, "jpeg", outputStream);
	} catch (IOException ioe) {
	    logger.error(ioe.getMessage(), ioe);
	} finally {
	    try {
		inputStream.close();
	    } catch (IOException ioe) {
		logger.error(ioe.getMessage(), ioe);
	    }
	}	
	return ((outputStream != null)?new ByteArrayInputStream(outputStream.toByteArray()):null);
    }
}