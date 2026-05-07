import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

// Overlays onto the repeatable backgrounds only.
public class ParallaxLayer {
    private double startY, height, factor;
    private BufferedImage image;

    public ParallaxLayer(double startY, BufferedImage img, double parallaxFactor){
        this.startY = startY;
        this.height = img.getHeight();
        this.image = img;
        this.factor = parallaxFactor;
    }

    public void draw(Graphics2D g2, Camera camera){
        double cameraY = camera.getY();
        if(cameraY + height < startY || cameraY >= startY + height) return; // Don't render past intended visible area (eg. in a boss arena)

        int screenY = (int)((startY - cameraY) * factor);
        g2.drawImage(image, 0, screenY, null);
    }

    public double getStartY(){ return this.startY; }
    public double getEndY(){ return startY + height; }
}
