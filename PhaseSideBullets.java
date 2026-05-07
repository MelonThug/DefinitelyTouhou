import java.awt.image.BufferedImage;
import java.util.Random;

// Bullets that spawn on both the left/right sides of the screen, randomly tilted,
// and moving in a straight line in their facing direction.
public class PhaseSideBullets extends Phase {
    private long lastBulletTime;
    private int verticalPadding, bulletInterval, bulletSpawnOffset, minAngle, maxAngle, moveChance;
    private double minSpeed, maxSpeed;

    public PhaseSideBullets(Boss boss){
        super(boss);
        maxHP = 80;
        hp = maxHP;
        duration = 30000;

        moveChance = 360; // Chance for the boss to move. Higher = less probable
        minSpeed = 1.0; // Minimum random bullet speed
        maxSpeed = 3.0; // Maximum random bullet speed
        verticalPadding = 100; // Offset from the top and bottom of the screen for the spawnable area for bullets
        bulletInterval = 200; // Delay between each bullet spawn
        bulletSpawnOffset = 10; // Offset from the left/right of the screen for bullet spawns
        minAngle = -50; // Min tilt angle
        maxAngle = 51; // Max tilt angle
    }

    public void setupPhase(){
        boss.setMoveChance(moveChance);
    };

    public void updatePhaseLogic(long now){
        if(now - lastBulletTime > bulletInterval){
            lastBulletTime = now;
            soundManager.playClip("boss_shoot", false);
            spawnBullet(false);
            spawnBullet(true);
        }
    }

    public void spawnBullet(boolean right){
        Random random = new Random();
        BufferedImage rawImg = ImageManager.loadBufferedImage("images/boss2_bullet.png");

        int x;
        double baseAngle;
        if(right){
            x = (int)bgManager.getWorldWidth() - bulletSpawnOffset;
            baseAngle = Math.PI;
        } else {
            x = bulletSpawnOffset;
            baseAngle = 0;
        }
        
        double angle = baseAngle + Math.toRadians(random.nextInt(maxAngle - minAngle) + minAngle);
        Background bg = bgManager.getBackgroundContaining(boss.getWorldY());
        double minY = verticalPadding + bg.getStartY();
        double maxY = bg.getEndY() - verticalPadding;

        int y = (int)(random.nextDouble() * (maxY - minY) + minY);
        double speed = random.nextDouble() * (maxSpeed - minSpeed) + minSpeed;

        double dx = Math.cos(angle) * speed;
        double dy = Math.sin(angle) * speed;

        BufferedImage img = ImageManager.rotateImage(rawImg, Math.toDegrees(angle) + 90); // +90 because the image faces upwards by default
        ImageFX effect;

        if(right) effect = new TintFX(0, 0, 255, 0);
        else effect = new TintFX(0, 120, 120, 0);

        BossBullet bullet = new BossBullet(img, x, y, dx, dy);
        bullet.setImageEffect(effect);
        bulletManager.addBullet(bullet);
    }
}
