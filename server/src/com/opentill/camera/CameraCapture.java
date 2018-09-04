package com.opentill.camera;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.opentill.main.Config;

public class CameraCapture {
	Dimension[] nonStandardResolutions = new Dimension[] {
			WebcamResolution.PAL.getSize(),
			WebcamResolution.HD.getSize(),
			new Dimension(2000, 1000),
			new Dimension(1000, 500),
	};
	
    public CameraCapture() throws IOException
    {
    	Webcam webcam = Webcam.getDefault();
    	webcam.setCustomViewSizes(nonStandardResolutions);
    	webcam.setViewSize(WebcamResolution.HD.getSize());
    	webcam.open();
    	BufferedImage image = webcam.getImage();
    	ImageIO.write(image, "JPEG", new File(Config.APP_HOME + File.separatorChar + "hello-world.png"));
    }
    public static void main(String[] args) {
    	try {
			CameraCapture cam = new CameraCapture();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}