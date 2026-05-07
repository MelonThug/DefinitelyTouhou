import java.awt.image.BufferedImage;

// Spaced streams of bullets extend outwards from the boss in straight lines, aimed towards the player and accelerating over time.
public class PhaseAimedShotgun extends Phase {
    private boolean isShooting;
    private long lastShotTime, lastBurstTime;
    private int shootDelay, lineCount, burstDelay, bulletsPerLine, shotCounter, startSpeed, maxSpeed, accelTime, spreadRadius, moveChance;
    private double burstAngle;

    public PhaseAimedShotgun(Boss boss){
        super(boss);

        shootDelay = 50; // Delay between bullets
        burstDelay = 600; // Delay between bursts
        lineCount = 3; // Lines in the burst
        bulletsPerLine = 15; // Bullets per line in the burst

        spreadRadius = 18; // Spread of the burst 
        startSpeed = 5; // Bullet start speed
        maxSpeed = 12; // Bullet max speed
        accelTime = 120; // Frames till max speed reached
        moveChance = 360; // Chance for the boss to move. Higher = less probable

        shotCounter = 0;
        maxHP = 70;
        hp = maxHP;
        duration = 25000;
    }

    public void setupPhase(){
        boss.setMoveChance(moveChance);
    };

    public void updatePhaseLogic(long now){
        if(now - lastBurstTime > burstDelay && !isShooting) {
            isShooting = true;
            burstAngle = getAngleToPlayer();
            soundManager.playClip("boss_attack", false);
        }
        
        if(isShooting){
            if(now - lastShotTime > shootDelay){
                lastShotTime = gameClock.getTime();
                shootBurst();
                shotCounter++;

                if(shotCounter >= bulletsPerLine){
                    shotCounter = 0;
                    isShooting = false;
                    lastBurstTime = gameClock.getTime();
                }
            }
        }
    }

    private void shootBurst(){
        BufferedImage rawImg = ImageManager.loadBufferedImage("images/boss2_bullet.png");
        double spawnX = boss.getWorldX() + boss.getWidth() / 2.0;
        double spawnY = boss.getWorldY() + boss.getHeight();

        double spread = Math.toRadians(spreadRadius); // Bullet spread from base burst angle
        double radius = 20;
        double startAngle = burstAngle - spread;
        double endAngle   = burstAngle + spread;

        for(int i = 0; i < lineCount; i++){
            double t = (double)i / (lineCount - 1);
            double bulletAngle = startAngle + t * (endAngle - startAngle); // The angle this particular bullet should be facing
        
            double dirX = Math.cos(bulletAngle); // Direction vectors
            double dirY = Math.sin(bulletAngle);

            double x = spawnX + radius * Math.cos(bulletAngle); // Spawn coordinates
            double y = spawnY + radius * Math.sin(bulletAngle);
        
            BufferedImage img = ImageManager.rotateImage(rawImg, Math.toDegrees(bulletAngle) + 90);
            BossBullet bullet = new BossBullet(img, x, y, dirX, dirY);
            bullet.setAcceleration(startSpeed, maxSpeed, accelTime);
            bullet.setImageEffect(new TintFX(0, 120, 120, 0));
            bulletManager.addBullet(bullet);
        }
    }

    private double getAngleToPlayer(){
        Player player = bulletManager.getPlayer();
        double spawnX = boss.getWorldX() + boss.getWidth() / 2.0;
        double spawnY = boss.getWorldY() + boss.getHeight();

        double targetX = player.getWorldX() + player.getWidth() / 2.0;
        double targetY = player.getWorldY() + player.getHeight() / 2.0;

        // Calculate a vector towards they player's current location
        double dx = targetX - spawnX;
        double dy = targetY - spawnY;
        double distance = Math.sqrt(dx * dx + dy * dy); // Normalize
        if (distance == 0) distance = 1;

        dx = dx / distance; // Unit vectors
        dy = dy / distance;
        double baseAngle = Math.atan2(dy, dx); // Get angle to player

        return baseAngle;
    }
}
