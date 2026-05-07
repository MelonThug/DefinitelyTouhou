import java.awt.image.BufferedImage;

// Used to tint boss bullets and the game over screen red
public class TintFX implements ImageFX {
	private int tintChange, rTint, gTint, bTint;

	public TintFX (int r, int g, int b, int tc) {
		rTint = r;
		gTint = g;
		bTint = b;
		tintChange = tc;
	}

	private int truncate (int colourValue) {
		if (colourValue > 255) return 255;
		if (colourValue < 0) return 0;
		return colourValue;
	}

	private int applyTint (int pixel) {
    	int alpha, red, green, blue, newPixel;
		
		alpha = (pixel >> 24) & 255;
		red = (pixel >> 16) & 255;
		green = (pixel >> 8) & 255;
		blue = pixel & 255;

		red = truncate(red + rTint);
		green = truncate(green + gTint);
		blue = truncate(blue + bTint);
		
		newPixel = blue | (green << 8) | (red << 16) | (alpha << 24);
		return newPixel;
	}


	public BufferedImage apply(BufferedImage image) {
		BufferedImage copy = ImageManager.copyImage(image);		
		int imWidth = copy.getWidth();
		int imHeight = copy.getHeight();
    	int [] pixels = new int[imWidth * imHeight];
    	copy.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

		for (int i=0; i<pixels.length; i++) {
			pixels[i] = applyTint(pixels[i]);
		}

    	copy.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);	
		return copy;
	}

	public void update() {
		rTint += tintChange;
		gTint += tintChange;
		bTint += tintChange;
		if (rTint > 255) rTint = 0;
		if (gTint > 255) gTint = 0;
		if (bTint > 255) bTint = 0;
	}
}