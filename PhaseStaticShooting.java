import java.awt.image.BufferedImage;

// Bullets the spawn below the boss, and move in a straight line downwards. Very simple pattern.
public class PhaseStaticShooting extends Phase {
    private boolean isShooting;
    private long lastShotTime, lastBurstTime;
    private int shootDelay, burstDelay, burstCount, shotCounter, speed;

    public PhaseStaticShooting(Boss boss){
        super(boss);

        shootDelay = 300; // Delay between bullets
        burstDelay = 2000; // Delay between bursts
        burstCount = 8; // Bullets per burst
        speed = 6; // Bullet speed
        
        shotCounter = 0;
        maxHP = 60;
        hp = maxHP;
        duration = 22000;
    }

    public void setupPhase(){};
    
    public void updatePhaseLogic(long now){
        if(now - lastBurstTime > burstDelay && !isShooting) isShooting = true;

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
        BufferedImage img = ImageManager.loadBufferedImage("images/boss1_bullet.png");
        int x = (int)(boss.getWorldX() + boss.getWidth()/2);
        int y = (int)(boss.getWorldY() + boss.getHeight() + 20);
        int dx = 0;
        int dy = speed;

        soundManager.playClip("boss_shoot", false);
        BossBullet bullet = new BossBullet(img, x, y, dx, dy);
        bulletManager.addBullet(bullet);
    }
}
