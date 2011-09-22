package com.ufp.demo.operations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class RawOperation implements Operation {
    private static Logger logger = Logger.getLogger(RawOperation.class);

    public InputStream process(InputStream inputStream) {
	ByteArrayOutputStream outputStream = null;
	try {
	    BufferedImage bufferedImage = ImageIO.read(inputStream);

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