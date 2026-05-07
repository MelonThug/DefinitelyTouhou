import java.awt.image.BufferedImage;

public interface ImageFX {
	public void update();
	public BufferedImage apply(BufferedImage image);
}