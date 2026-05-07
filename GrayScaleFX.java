import java.awt.image.BufferedImage;

// Used to grayscale a boss after they die and for victory screen.
public class GrayScaleFX implements ImageFX {
	private boolean originalImage;

	public GrayScaleFX() {}

	private int toGray (int pixel) {
  		int alpha, red, green, blue, gray, newPixel;

		alpha = (pixel >> 24) & 255;
		red = (pixel >> 16) & 255;
		green = (pixel >> 8) & 255;
		blue = pixel & 255;
		gray = (red + green + blue) / 3;
		red = green = blue = gray;

		newPixel = blue | (green << 8) | (red << 16) | (alpha << 24);
		return newPixel;
	}

	public BufferedImage apply(BufferedImage image) {
		if(originalImage) return image;

		BufferedImage copy = ImageManager.copyImage(image);
		int imWidth = copy.getWidth();
		int imHeight = copy.getHeight();
    	int [] pixels = new int[imWidth * imHeight];
    	copy.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

		for (int i=0; i<pixels.length; i++) {
			pixels[i] = toGray(pixels[i]);
		}
  
    	copy.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
		return copy;
	}	

	public void update() {}
}