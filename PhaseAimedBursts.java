import java.awt.image.BufferedImage;

// A stream of bullets aimed at the player's location
public class PhaseAimedBursts extends Phase {
    private boolean isShooting;
    private long lastShotTime, lastBurstTime;
    private int shootDelay, burstDelay, burstCount, shotCounter, speed;

    public PhaseAimedBursts(Boss boss){
        super(boss);

        shootDelay = 300; // Delay between shots
        burstDelay = 1000; // Delay between burts
        burstCount = 6; // Bullets per burst
        speed = 6; // Bullet speed

        shotCounter = 0;
        maxHP = 50;
        hp = maxHP;
        duration = 25000;
    }

    public void setupPhase(){};
    
    public void updatePhaseLogic(long now){
        if(now - lastBurstTime > burstDelay && !isShooting) {
            soundManager.playClip("boss_attack", false);
            isShooting = true;
        }

        if(isShooting){
            if(now - lastShotTime > shootDelay){
                lastShotTime = gameClock.getTime();
                shootBullet();
                shotCounter++;

                if(shotCounter >= burstCount){
                    shotCounter = 0;
                    isShooting = false;
                    lastBurstTime = gameClock.getTime();
                }
            }
        }
    }

    private void shootBullet(){
        Player player = bulletManager.getPlayer();
        double spawnX = boss.getWorldX() + boss.getWidth() / 2.0;
        double spawnY = boss.getWorldY() + boss.getHeight();

        double targetX = player.getWorldX() + player.getWidth() / 2.0;
        double targetY = player.getWorldY() + player.getHeight() / 2.0;

        // Calculate a vector towards they player's current location
        double dx = targetX - spawnX;
        double dy = targetY - spawnY;

        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) distance = 1;

        dx = dx / distance;
        dy = dy / distance;

        double vx = dx * speed;
        double vy = dy * speed;
        double angle = Math.atan2(vy, vx);
    
        BufferedImage rawImg = ImageManager.loadBufferedImage("images/boss1_bullet.png");
        BufferedImage img = ImageManager.rotateImage(rawImg, Math.toDegrees(angle) + 90);
        BossBullet bullet = new BossBullet(img, spawnX, spawnY, vx, vy);
        bulletManager.addBullet(bullet);
    }
}
