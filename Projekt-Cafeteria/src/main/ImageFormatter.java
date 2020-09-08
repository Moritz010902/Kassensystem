package main;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class ImageFormatter {

	static ImageIcon pic;
	
	
	public ImageIcon getImage(int width, int height, String path) {
		
		try {
			
			
			BufferedImage i = ImageIO.read(getClass().getResource(path));
			
			pic = toIcon(i);
			pic.setImage(pic.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
			
			return pic;
		
		
		} catch (IOException e) {
			return null;
		}
		
	}
	
	private ImageIcon toIcon(BufferedImage image) throws IOException {
		
		ImageIcon icon = new ImageIcon(toImage(toByteArray(image)));
		return icon;
		
	}
	
	private byte[] toByteArray(BufferedImage image) throws IOException {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", bos);
		byte[] bytes = bos.toByteArray();
		return bytes;
		
	}
	
	private Image toImage(byte[] bytes) {
		
		Image image = Toolkit.getDefaultToolkit().createImage(bytes);
		return image;
		
	}

}
