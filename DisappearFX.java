import java.awt.image.BufferedImage;

// Used for the player flashing effect during their immunity period after getting hit.
public class DisappearFX implements ImageFX {
	private int alpha, alphaChange;

	public DisappearFX (int rate) {
		alpha = 255;
		alphaChange = rate;
	}

	public BufferedImage apply(BufferedImage image) {
		BufferedImage copy = ImageManager.copyImage(image);
		int imWidth = copy.getWidth();
		int imHeight = copy.getHeight();
    	int [] pixels = new int[imWidth * imHeight];
    	int a, red, green, blue, newValue;
		copy.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

		for (int i=0; i<pixels.length; i++) {
			a = (pixels[i] >> 24);
			red = (pixels[i] >> 16) & 255;
			green = (pixels[i] >> 8) & 255;
			blue = pixels[i] & 255;

			if (a != 0) {
				newValue = blue | (green << 8) | (red << 16) | (alpha << 24);
				pixels[i] = newValue;
			}
		}

    	copy.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
		return copy;
	}

	public void update() {
		alpha = alpha - alphaChange;
		if(alpha <= 50 || alpha >= 255){
		    alphaChange = -alphaChange;
		}
	}
}