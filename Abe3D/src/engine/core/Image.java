package engine.core;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class Image {

	private BufferedImage image;
	private WritableRaster raster;
	private int[] data;
	
	private int width;
	private int height;
	
	public Image(String resource) {
		try {
			InputStream in = getClass().getResourceAsStream(resource);
			if(in == null) {
				throw new RuntimeException("Couldn't load " + resource);
			}
			
			BufferedImage imageProv = ImageIO.read(in);
			image = new BufferedImage(imageProv.getWidth(), imageProv.getHeight(), BufferedImage.TYPE_INT_ARGB);
			image.getGraphics().drawImage(imageProv, 0, 0, null);
		} catch(Exception e) {
			System.err.println("Couldn't load " + resource);
			Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, e);
		}
		
		this.width = image.getWidth();
		this.height = image.getHeight();
		raster = image.getRaster();
		data = ((DataBufferInt) raster.getDataBuffer()).getData();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public BufferedImage getColorBuffer() {
		return image;
	}
	
	public void setPixel(int x, int y, int[] color) {
		data[x + y * width] = color[3] + (color[2] << 8) + (color[1] << 16) + (color[0] << 24);
	}
	
	public int getPixel(int x, int y) {
		return data[x + y * width];
	}
	
	public void getPixel(int tx, int ty, int[] color) {
		int c = data[tx + ty * width];
		color[0] = (c & 0xff000000) >> 24;
		color[1] = (c & 0x00ff0000) >> 16;
		color[2] = (c & 0x0000ff00) >> 8;
		color[3] = (c & 0x000000ff);
	}
}