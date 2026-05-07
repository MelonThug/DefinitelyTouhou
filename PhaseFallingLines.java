import java.awt.image.BufferedImage;
import java.util.Random;

// Horizontal lines of bullets falling from the top of the screen, with a
// gap the player must move through
public class PhaseFallingLines extends Phase {
    private long lastLine;
    private int verticalOffset, lineDelay, lineGap, bulletGap, prevGapLocation, maxGapLocationDiff, speed;

    public PhaseFallingLines(Boss boss){
        super(boss);
        maxHP = 60;
        hp = maxHP;
        duration = 30000;

        verticalOffset = 10; // Offset from top of screen to start spawning
        lineDelay = 800; // Delay between lines
        lineGap = 90; // X gap in a line
        bulletGap = 10; // Gap between individual bullets on each line
        maxGapLocationDiff = 300; // Max X difference between 2 consecutive gaps. 
        speed = 3; // Line fall speed
    }

    public void setupPhase(){};
    
    public void updatePhaseLogic(long now){
        if(now - lastLine > lineDelay){
            lastLine = now;
            soundManager.playClip("boss_attack", false);
            spawnLine();
        }
    }

    private void spawnLine(){
        BufferedImage rawImg = ImageManager.loadBufferedImage("images/boss2_bullet.png");
        BufferedImage img = ImageManager.rotateImage(rawImg, 180);
        Random random = new Random();
        
        int gapStartPos = 999;  // Just a big value for the first iteration
        while(Math.abs(gapStartPos - prevGapLocation) > maxGapLocationDiff){
            gapStartPos = random.nextInt((int)bgManager.getWorldWidth() - lineGap);
        }   

        prevGapLocation = gapStartPos;
        int dx = 0;
        int dy = speed;

        for(int i = 0; i < bgManager.getWorldWidth(); i += bulletGap){
            if(i >= gapStartPos && i <= (gapStartPos + lineGap)){ // Gap logic
                continue;
            }

            Background bg = bgManager.getBackgroundContaining(boss.getWorldY());
            BossBullet bullet = new BossBullet(img, i, bg.getStartY() + verticalOffset, dx, dy);
            bulletManager.addBullet(bullet);
        }
    }
}
