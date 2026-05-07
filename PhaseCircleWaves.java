import java.awt.image.BufferedImage;

// Multiple streams of bullets that form a circle around the boss and extend outwards, accelerating to a max speed.
public class PhaseCircleWaves extends Phase {
    private boolean isShooting, waitingToMove;
    private long lastShotTime, lastBurstTime;
    private int shootDelay,bulletCount, burstDelay, burstCount, shotCounter, startSpeed, maxSpeed, accelTime, moveDelay;

    public PhaseCircleWaves(Boss boss){
        super(boss);

        moveDelay = 1000; // Delay between boss movement after each burst
        shootDelay = 300; // Delay between bullets
        burstDelay = 1200; // Delay between waves
        burstCount = 8; // Waves per burst
        bulletCount = 23; // Bullets per wave (essentially how dense the wave is)
        startSpeed = 1; // Bullet start speed
        maxSpeed = 12; // Bullet max speed
        accelTime = 120; // Frames till max speed reached
        
        shotCounter = 0;
        maxHP = 70;
        hp = maxHP;
        duration = 25000;
    }

    public void setupPhase(){
        boss.setCanMove(false);
    };

    public void updatePhaseLogic(long now){
        if(now - lastBurstTime > burstDelay && !isShooting && !waitingToMove) {
            isShooting = true;
        }

        if(isShooting && !boss.isMoving()){
            if(now - lastShotTime > shootDelay){
                lastShotTime = now;
                spawnCurve();
                shotCounter++;

                if(shotCounter >= burstCount){
                    shotCounter = 0;
                    isShooting = false;
                    waitingToMove = true;
                    lastBurstTime = now;
                }
            }
        } else if(waitingToMove){
            if(now - lastBurstTime > moveDelay){
                boss.forceMove();

                isShooting = true;
                waitingToMove = false;
                lastShotTime = now;
            }
        }
    }

    private void spawnCurve(){
        BufferedImage rawImg = ImageManager.loadBufferedImage("images/boss1_bullet.png");

        double centerX = boss.getWorldX() + boss.getWidth() / 2.0;
        double centerY = boss.getWorldY() + boss.getHeight() / 2.0;

        double radius = 50;
        double startAngle = Math.toRadians(-45);
        double endAngle = Math.toRadians(225);

        for(int i = 0; i < bulletCount; i++){
            double t = (double)i / (bulletCount - 1);
            double angle = startAngle + t * (endAngle - startAngle);
            double dirX = Math.cos(angle);
            double dirY = Math.sin(angle);

            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            BufferedImage img = ImageManager.rotateImage(rawImg, Math.toDegrees(angle) + 90);
            BossBullet bullet = new BossBullet(img, x, y, dirX, dirY);
            bullet.setAcceleration(startSpeed, maxSpeed, accelTime);
            if(i % 2 == 0) bullet.setImageEffect(new GrayScaleFX()); // Even numbered bullets are black and white
            bulletManager.addBullet(bullet);
        }
    }
}
