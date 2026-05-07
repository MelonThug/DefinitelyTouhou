import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

// The player has 2 speeds they can shift between, and can have their movement locked during
// stage transitions. The player loses a life if hit by an enemy bullet, then respawns at the bottom
// center of the screen, with an immunity period, during which they will flash. The player's hitbox
// is slightly smaller than their sprite for "less frustrating" dodging.
public class Player {
    private double worldX, worldY;
    private long lastShotTime, immuneStartTime;
    private int width, height, shootDelay, hp, immuneTime, hitboxLenience;
    private int speed, slowedSpeed, currentSpeed;
    private boolean left, right, up, down;
    private boolean isShooting, isSlowed, isDead, isImmune, isControlLocked;

    private BufferedImage leftImg, rightImg, neutralImg, currentImage;
    private GameClock gameClock;
    private BackgroundManager bgManager;
    private BulletManager bulletManager;
    private SoundManager soundManager;
    private FXManager fxManager;
    private ImageFX deathFx;

    public Player(){
        left = right = up = down = isShooting = isSlowed = isDead = isImmune = false;

        gameClock = GameClock.getInstance();
        bgManager = BackgroundManager.getInstance();
        soundManager = SoundManager.getInstance();
        bulletManager = BulletManager.getInstance();
        fxManager = FXManager.getInstance();

        neutralImg = ImageManager.loadBufferedImage("images/player_neutral.png");
        leftImg = ImageManager.loadBufferedImage("images/player_left.png");
        rightImg = ImageManager.loadBufferedImage("images/player_right.png");
        currentImage = neutralImg;

        width = rightImg.getWidth(null);
        height = rightImg.getHeight(null);

        shootDelay = 150; // Delay between shots
        hitboxLenience = 10; // How much smaller the player hitbox is relative to sprite size
        speed = 5; // Move speed
        slowedSpeed = speed/2; // Slowed speed
        immuneTime = 3000; // Immunity duration after being hit
        hp = 5; // Max/Starting HP
        currentSpeed = speed;
    }

    public void draw(Graphics2D g2, Camera camera){
        if(isDead) return;

        BufferedImage img = currentImage;
        if(deathFx != null){
            if(isImmune){
                img = deathFx.apply(currentImage);
            } else {
                fxManager.removeEffect(deathFx);
            }   
        }

        int screenX = (int)(worldX - camera.getX());
        int screenY = (int)(worldY - camera.getY());

        g2.drawImage(img, screenX, screenY, width, height, null);
    }

    public void update(){
        if(isControlLocked) return;

        if(left) { // Change player sprite depending on current move direction
            currentImage = leftImg;
        } else if(right){
            currentImage = rightImg;
        } else {
            currentImage = neutralImg;
        }

        if(isSlowed){ // Toggle between speeds
            currentSpeed = slowedSpeed;
        } else {
            currentSpeed = speed;
        }

        if(isShooting){
            long now = gameClock.getTime();
            if(now - lastShotTime >= shootDelay){
                lastShotTime = now;
                shootBullet();
            }
        }

        if(isImmune){
            long now = gameClock.getTime();
            if(now - immuneStartTime >= immuneTime){
                isImmune = false;
            }
        }
    }

    public void move() {
        if(isControlLocked) return;
        Background background = bgManager.getBackgroundContaining(worldY);
        
        if (left) worldX -= currentSpeed;
        if (right) worldX += currentSpeed;
        if (up) worldY -= currentSpeed;
        if (down) worldY += currentSpeed;
        
        if(background != null && background.isBossArena() && !background.getBoss().isDead()) { // Lock player Y movement to arena area
            if(worldY < background.getStartY()) {
                worldY = background.getStartY();
            }
            if(worldY > background.getEndY() - height) {
                worldY = background.getEndY() - height;
            }
        } else { // Let player move freely about Y
            if(worldY < 0) {
                worldY = 0;
            }
            if(worldY > bgManager.getWorldHeight() - height) {
                worldY = bgManager.getWorldHeight() - height;
            }
        }

        // Player X movement never gets locked (since this game's world is 500 width always), so this clamp applies regardless
        double rightBoundary = bgManager.getWorldWidth() - width;
        if(worldX < 0) worldX = 0;
        if(worldX > rightBoundary) worldX = rightBoundary;
    }

    public void shootBullet(){
        if(!isShooting) return;
        bulletManager.addBullet(new PlayerBullet(worldX, worldY, width));
        soundManager.playClip("player_shoot", false);
    }

    public void takeDamage() {
        if(isDead || isImmune ) return;

        doDeath();
        hp = hp - 1;
        if(hp <= 0){
            isDead = true;
        }
    }

    public void doDeath(){
        soundManager.playClip("player_death", false);
        
        isImmune = true;
        Background background = bgManager.getBackgroundContaining(worldY);
        if(background != null) { // Death should only ever occur in a boss arena, so respawn at the bottom center of that background
            worldX = (bgManager.getWorldWidth()/2) - width/2;
            worldY = background.getEndY() - 100;
        }

        immuneStartTime = gameClock.getTime();
        deathFx = new DisappearFX(50); // Flashing effect
        fxManager.addEffect(deathFx);
    }

    // Handling movement like this instead of immediately reacting on relative key presses 
    // gives much smoother movement.
    public void setMoveDirection(int direction){
        if(direction == 1) left = true;
        if(direction == 2) right = true;
        if(direction == 3) up = true;
        if(direction == 4) down = true;

        if(direction == -1) left = false;
        if(direction == -2) right = false;
        if(direction == -3) up = false;
        if(direction == -4) down = false;
    }

    public Rectangle2D.Double getBoundingRectangle(){
        double x = worldX + hitboxLenience/2;
        double y = worldY + hitboxLenience/2;
        double w = width - hitboxLenience;
        double h = height - hitboxLenience;
        return new Rectangle2D.Double(x, y, w, h);
    }

    public void lockControl(){ this.isControlLocked = true; }
    public void unlockControl(){ this.isControlLocked = false; }

    public void setIsShooting(boolean isShooting) { this.isShooting = isShooting; }
    public void setIsSlowed(boolean isSlowed) { this.isSlowed = isSlowed; }
    public double setWorldX(double worldX){ return this.worldX = worldX; }
    public double setWorldY(double worldY){ return this.worldY = worldY; }

    public double getWorldX(){ return this.worldX; }
    public double getWorldY(){ return this.worldY; }
    public int getHP(){ return this.hp; }
    public int getWidth(){ return this.width; }
    public int getHeight(){ return this.height; }
    public boolean isDead(){ return this.isDead; }
    public boolean isImmune(){ return this.isImmune; }
}
