import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
   The ImageManager class manages the loading and processing of images.
*/

public class ImageManager {
      
   	public ImageManager () {}

	public static Image loadImage (String fileName) {
		return new ImageIcon(fileName).getImage();
	}

	public static BufferedImage loadBufferedImage(String filename) {
		BufferedImage bi = null;
		File file = new File (filename);

		try {
			bi = ImageIO.read(file);
		} catch (IOException ioe) {
			System.out.println ("Error opening file " + filename + ":" + ioe);
		}

		return bi;
	}

	public static BufferedImage copyImage(BufferedImage src) {
		if (src == null) return null;

		int imWidth = src.getWidth();
		int imHeight = src.getHeight();
		BufferedImage copy = new BufferedImage (imWidth, imHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = copy.createGraphics();

		g2d.drawImage(src, 0, 0, null);
		g2d.dispose();
		return copy; 
	}

	// Used for bullet rotations
	public static BufferedImage rotateImage(BufferedImage image, double degrees) {
	    int width = image.getWidth();
	    int height = image.getHeight();
	    double radians = Math.toRadians(degrees);

	    double sin = Math.abs(Math.sin(radians));
	    double cos = Math.abs(Math.cos(radians));
	    int newWidth = (int)Math.floor(width * cos + height * sin);
	    int newHeight = (int)Math.floor(height * cos + width * sin);

	    BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = rotated.createGraphics();

	    g2.translate((newWidth - width) / 2, (newHeight - height) / 2);
	    g2.rotate(radians, width / 2.0, height / 2.0);
		
	    g2.drawImage(image, 0, 0, null);
	    g2.dispose();

	    return rotated;
	}
}


