package com.java.mina.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

public class ImageUtil {
	
	/**
	 * <p>image compress</p>
	 * <p>InputStream to byte[]</p>
	 * @param image
	 * @param quality
	 * @return
	 * @throws IOException
	 */
	protected static byte[] imageCompress(InputStream in, Double quality,
			Integer width, Integer height, Double ratio) throws IOException {
		BufferedImage inBuffer = ImageIO.read(in);
		if (ratio != null) {
			width = inBuffer.getWidth();
			height = inBuffer.getHeight();
			width = (int) (width * ratio);
			height = (int) (height * ratio);
		}
		// create image buffer
		BufferedImage buffer = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		buffer.getGraphics().drawImage(inBuffer.getScaledInstance(width, height, 
				Image.SCALE_SMOOTH), 0, 0, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		// set image compress MOD and quality
		JPEGImageWriteParam param = new JPEGImageWriteParam(Locale.US);
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality.floatValue());
		
		// write to output stream
		ImageWriter writer = ImageIO.getImageWritersByFormatName("JPEG").next();
		writer.setOutput(ImageIO.createImageOutputStream(out));
		writer.write(null, new IIOImage(buffer, null, null), param);
		out.flush();
		out.close();
		return out.toByteArray();
	}
	
	
	/**
	 * <p>image compress</p>
	 * <p>compress with ratio</p>
	 * @param in
	 * @param quality
	 * @param ratio
	 * @return
	 * @throws Exception
	 */
	public static byte[] imageCompress(InputStream in, Double quality, Double ratio)
			throws Exception{
		return imageCompress(in, quality, null, null, ratio);
	}
	
	/**
	 * <p>image compress</p>
	 * <p>compress with width and height</p>
	 * @param in
	 * @param quality
	 * @param width
	 * @param height
	 * @return
	 * @throws Exception
	 */
	public static byte[] imageCompress(InputStream in, Double quality, 
			Integer width, Integer height) throws Exception{
		return imageCompress(in, quality, width, height, null);
	}
	
	
	public static void main(String[] args) {
		String path = "C:\\Users\\asus\\Desktop\\tmp\\123.png";
		String path2 = "C:\\Users\\asus\\Desktop\\123.png";
		try {
			InputStream in = new FileInputStream(path);
			System.out.println(in.available());
			byte[] img = imageCompress(in, 0.9, 1.0);
			File file = new File(path2);
			if (!file.exists())
				file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(img);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
