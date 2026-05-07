import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class PlayerBullet extends Bullet {
    private int speed, damage;

    public PlayerBullet(double worldX, double worldY, int playerWidth){
        image = ImageManager.loadBufferedImage("images/player_bullet.png");
        width = image.getWidth();
        height = image.getHeight();
        isOffWorld = false;

        speed = 10; // Bullet speed
        damage = 2; // Bullet damage
        int offsetY = 20; // Offset above the player to spawn bullet

        this.bgManager = BackgroundManager.getInstance();
        this.worldX = worldX + (playerWidth/2) - (width/2);
        this.worldY = worldY - offsetY;
    }

    public void update(){
        if((worldX < 0) || (worldX + width > bgManager.getWorldWidth()) || 
           (worldY < 0) || (worldY + height > bgManager.getWorldHeight())){
            
            isOffWorld = true;
        }
    }

    public void move(){
        worldY -= speed;
    }

    public void draw(Graphics2D g2, Camera camera){
        int screenX = (int)(worldX - camera.getX());
        int screenY = (int)(worldY - camera.getY());

        g2.drawImage(image, screenX, screenY, width, height, null);
    }

    public Rectangle2D.Double getBoundingRectangle(){
        return new Rectangle2D.Double(worldX, worldY, width, height);
    }

    public int getDamage() { return this.damage; }
}
