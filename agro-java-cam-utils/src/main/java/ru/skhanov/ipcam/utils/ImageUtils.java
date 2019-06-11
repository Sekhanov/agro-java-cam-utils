package ru.skhanov.ipcam.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import com.github.sarxos.webcam.Webcam;

public class ImageUtils {

	public static void getImageFromCamWithMetadata(Webcam webcam, String imageName) {
		webcam.open(true);
		byte[] imageInByte = null;
		BufferedImage image = webcam.getImage();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "jpg", byteArrayOutputStream);	
			byteArrayOutputStream.flush();
			imageInByte = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
		} catch (IOException e) {	
			e.printStackTrace();
		}		
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(imageName + ".jpg")));
			TiffOutputSet tiffOutputSet = new TiffOutputSet();
			tiffOutputSet.setGPSInDegrees(41.3477778, 56.8525000);
			
			new ExifRewriter().updateExifMetadataLossless(imageInByte, os, tiffOutputSet);
		} catch (ImageReadException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImageWriteException e) {
			e.printStackTrace();
		}
		webcam.close();		
	}
}
