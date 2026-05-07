import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

// The image is determined by the boss. It can either be an arena (an image meant to be shown once), 
// or it can be a background meant to be repeated. Repeated backgrounds form the transition between bosses.
public class Background {
    private double startY, endY, height;
    private BufferedImage image;
    private Boss owner;
    private boolean isBossArena;

    public Background(double startY, Boss boss, boolean isBossArena){
        this.startY = startY;
        this.isBossArena = isBossArena;
        this.owner = boss;
        if(isBossArena) this.image = boss.getBgImage();
        else this.image = boss.getRepeatBgImage();

        this.height = image.getHeight();
        this.endY = startY + height;
    }

    public void draw(Graphics2D g2, Camera camera){
        int screenY = (int)(startY - camera.getY());
        g2.drawImage(image, 0, screenY, null);
    }
    
    public Boss getBoss(){ return this.owner; }
    public boolean isBossArena(){ return this.isBossArena; }
    public double getHeight(){ return this.height; }
    public double getStartY(){ return this.startY; }
    public double getEndY(){ return this.endY; }
}
