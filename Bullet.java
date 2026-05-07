import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class Bullet {
    protected int width, height;
    protected double worldX, worldY;
    protected boolean isOffWorld;
    protected BufferedImage image;
    protected ImageFX effect;
    protected BackgroundManager bgManager;

    public abstract void move();
    public abstract void update();
    public abstract void draw(Graphics2D g2, Camera camera);    
    public abstract Rectangle2D.Double getBoundingRectangle();

    public void setImageEffect(ImageFX effect){ 
        this.effect = effect; 
        FXManager.getInstance().addEffect(effect);
    }
    
    public boolean isOffWorld(){ return this.isOffWorld; }
    public ImageFX getImageEffect(){ return this.effect; }
}
