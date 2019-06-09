package ru.skhanov.my_ipcam;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.ds.ipcam.IpCamAuth;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

/**
 * Hello world!
 *
 */
public class App {
	
	static {
		Webcam.setDriver(new IpCamDriver());
	}
	
	public static void main(String[] args) {
		final String name = "myCam";
		final String user = "admin"; // change to your own username
		final String pass = ""; // change to your own password
//		final String user = "tt"; // change to your own username
//		final String pass = "samsung"; // change to your own password
		final String url = "http://192.168.100.188/video1.mjpg"; // camera's IP address or domain
//		final String url = "http://192.168.100.41:8080/photo.jpg"; // camera's IP address or domain
		
		try {
			IpCamDeviceRegistry.register(new IpCamDevice(name, url, IpCamMode.PUSH, new IpCamAuth(user, pass)));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Webcam webcam = Webcam.getWebcams().get(0);
		webcam.open();
		BufferedImage image = webcam.getImage();
		try {
			ImageIO.write(image, "jpg", new File("test.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		try {
			File inputFile = new File("test.jpg");
			OutputStream os = new BufferedOutputStream(new FileOutputStream(new File("test1.jpg")));
			
			ImageMetadata imageMetadata = Imaging.getMetadata(inputFile);
			JpegImageMetadata jpegImageMetadata = (JpegImageMetadata) imageMetadata;

			TiffOutputSet tiffOutputSet = new TiffOutputSet();
			tiffOutputSet.setGPSInDegrees(-74.0, 40 + 43 / 60.0);
			new ExifRewriter().updateExifMetadataLossless(inputFile, os, tiffOutputSet);
		} catch (ImageReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImageWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webcam.close();
		
		
		
		WebcamPanel panel = new WebcamPanel(webcam);		
		JFrame f = new JFrame(name);
		f.add(panel);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
	}	

}
