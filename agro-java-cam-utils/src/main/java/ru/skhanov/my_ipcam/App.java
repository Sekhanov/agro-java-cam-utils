package ru.skhanov.my_ipcam;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.ds.ipcam.IpCamAuth;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

public class App {
	
	public static final String STREAM_CAM = "streamCam";
	public static final String IMAGE_CAM = "imageCam";
	public static final String USER = "admin";
	public static final String PASSWORD = "";
	public static final String STREAM_URL = "http://192.168.100.188/video2.mjpg";
	public static final String IMAGE_URL = "http://192.168.100.188/dms?nowprofileid=1";
	public static Webcam streamCam = null;
	public static Webcam imageCam = null;
	
	static {
		Webcam.setDriver(new IpCamDriver());
	}
	
	public static void main(String[] args) {

		
		try {
			IpCamDeviceRegistry.register(new IpCamDevice(STREAM_CAM, STREAM_URL, IpCamMode.PUSH, new IpCamAuth(USER, PASSWORD)));
			streamCam = Webcam.getWebcamByName(STREAM_CAM);
			IpCamDeviceRegistry.register(new IpCamDevice(IMAGE_CAM, IMAGE_URL, IpCamMode.PULL, new IpCamAuth(USER, PASSWORD)));
			imageCam = Webcam.getWebcamByName(IMAGE_CAM);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
		
		WebcamPanel panel = new WebcamPanel(streamCam);		
		panel.setFPSLimit(10);	
		panel.setImageSizeDisplayed(true);
		JFrame f = new JFrame();		
		f.add(panel);
		f.pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		f.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				streamCam.getDevice().dispose();
				imageCam.getDevice().dispose();
				System.out.println("closed");
				e.getWindow().dispose();
			}
		});
		
		getImageFromCam(imageCam);
	}

	private static void getImageFromCam(Webcam webcamPush) {
		webcamPush.open(true);
		BufferedImage image = webcamPush.getImage();
		try {
			ImageIO.write(image, "jpg", new File("test.jpg"));
		} catch (IOException e) {	
			e.printStackTrace();
		}		
		try {
			File inputFile = new File("test.jpg");			
			OutputStream os = new BufferedOutputStream(new FileOutputStream(new File("test1.jpg")));
//			ImageMetadata imageMetadata = Imaging.getMetadata(inputFile);
//			JpegImageMetadata jpegImageMetadata = (JpegImageMetadata) imageMetadata;
			TiffOutputSet tiffOutputSet = new TiffOutputSet();
			tiffOutputSet.setGPSInDegrees(41.3477778, 56.8525000);
//			new ExifRewriter().updateExifMetadataLossless(inputFile, os, tiffOutputSet);
			new ExifRewriter().updateExifMetadataLossless(inputFile, os, tiffOutputSet);
		} catch (ImageReadException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImageWriteException e) {
			e.printStackTrace();
		}
		webcamPush.close();		
	}

	
}
