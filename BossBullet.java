import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class BossBullet extends Bullet {
    private int hitboxLenience;
    private double dx, dy;
    private boolean useAcceleration;

    private double dirX, dirY;
    private double maxSpeed, startSpeed;
    private double t;
    private double tStep;

    public BossBullet(BufferedImage image, double worldX, double worldY, double dx, double dy){
        width = image.getWidth();
        height = image.getHeight();
        isOffWorld = false;

        this.bgManager = BackgroundManager.getInstance();
        this.hitboxLenience = 2; // Multipler on how much smaller the hitbox should be relative to the sprite
        this.image = image;
        this.worldX = worldX - width / 2.0;
        this.worldY = worldY - height / 2.0;
        this.dx = dx;
        this.dy = dy;
        this.useAcceleration = false;
    }

    public void update(){
        if((worldX < 0) || (worldX + width > bgManager.getWorldWidth()) || 
           (worldY < 0) || (worldY + height > bgManager.getWorldHeight())){

            isOffWorld = true;
        }
    }

    public void move(){
        if(!useAcceleration){
            worldX += dx;
            worldY += dy;
            return;
        }

        if (t < 1.0) {
            t += tStep;
            if (t > 1.0) t = 1.0;
        }

        double progress = t * t;  
        double speed = startSpeed + (maxSpeed - startSpeed) * progress;

        double vx = dirX * speed;
        double vy = dirY * speed;
        worldX += vx;
        worldY += vy;
    }

    public void draw(Graphics2D g2, Camera camera){
        int screenX = (int)(worldX - camera.getX());
        int screenY = (int)(worldY - camera.getY());
        
        BufferedImage img = image;
        if(effect != null) {
            img = effect.apply(image);
        }
        
        g2.drawImage(img, screenX, screenY, width, height, null);
    }

    public void setAcceleration(double startSpeed, double maxSpeed, int framesToMax){
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length == 0) length = 1;

        this.dirX = dx / length;
        this.dirY = dy / length;

        this.startSpeed = startSpeed;
        this.maxSpeed = maxSpeed;

        this.t = 0;
        this.tStep = 1.0 / framesToMax;
        this.useAcceleration = true;
    }

    public Rectangle2D.Double getBoundingRectangle(){
        double hitWidth = width/hitboxLenience;
        double hitHeight = height/hitboxLenience;
        double offsetX = (width - hitWidth) / 2.0;
        double offsetY = (height - hitHeight) / 2.0;
        return new Rectangle2D.Double(worldX + offsetX, worldY + offsetY, hitWidth, hitHeight);
    }
}
