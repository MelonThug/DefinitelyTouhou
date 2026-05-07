import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// Bosses are enemies the player must defeat. A boss consists of multiple phases, each with their own attack
// patterns, HP and timer. A boss can randomly move between 3 positions on the screen, and is defeated if all their
// phases are defeated. If the player fails to complete any of the boss' phases in time, the boss is considered
// failed, which leads to a Game Over. Bosses also have their own respective arena backgrounds, repeatable
// backgrounds, and parallax backgrounds (set in subclass constructors).
public abstract class Boss {
    protected String name, bgm;
    protected int width, height, speed;
    protected BufferedImage bgImage, repeatBgImage, parallaxImage;
    protected ArrayList<Phase> phases;
    protected HashMap<String, Animation> animations;
    protected Animation currentAnimation;
    protected SoundManager soundManager;
    
    private GamePanel panel;
    private int position, baseMoveChance, moveChance, positionDistance, phaseIndex;
    private double worldX, worldY, targetX;
    private boolean isMoving, isDead, isActive, hasFailedPhase, canMove, forceMove;

    private FXManager fxManager;
    private ArrayList<Integer> positions;
    private Phase currentPhase;
    private Random random;
    private ImageFX deathEffect;
    
    public Boss(GamePanel p){
        panel = p;
        baseMoveChance = 180;
        moveChance = baseMoveChance;
        phaseIndex = 0;
        isMoving = isDead = isActive = forceMove = false;
        canMove = true;
        deathEffect = null;

        fxManager = FXManager.getInstance();
        soundManager = SoundManager.getInstance();
        random = new Random();
        animations = new HashMap<>();
        phases = new ArrayList<>();
        positions = new ArrayList<>();

        position = 1; // Screen positions: 0 = Left, 1 = Middle, 2 = Right
        positionDistance = 170;
        int basePos = panel.getWidth()/2 - width/2;
        positions.add(0, basePos - positionDistance);
        positions.add(1, basePos);
        positions.add(2, basePos + positionDistance);
    }

    public void draw(Graphics2D g2, Camera camera){
        int screenX = (int)(worldX - camera.getX());
        int screenY = (int)(worldY - camera.getY());

        BufferedImage img = (BufferedImage)currentAnimation.getImage();
        if(deathEffect != null) {
            img = deathEffect.apply(img);
        }

        g2.drawImage(img, screenX, screenY, currentAnimation.getWidth(), currentAnimation.getHeight(), null);
    }

    public void update(){
        String stageText = name;
        if(panel.getCurrentBoss() == this){
            if(currentPhase != null){
                stageText += ":Phase " + phaseIndex;
            }
        }
        panel.setStageText(stageText);

        if(!isActive) return;
        determineNextAnimation();
        determineNewPosition();

        if(currentPhase.isFailed()){
            hasFailedPhase = true; // GamePanel will read this and immediately end the game
        }

        if(currentPhase.isActive()){
            currentPhase.updatePhase();
        }
    }

    private void determineNextAnimation(){
        Animation nextAnimation = null;
        if(!isMoving){
            nextAnimation = animations.get("idle");
        } else if (isMoving && targetX > worldX){
            nextAnimation = animations.get("move_right");
        } else if (isMoving && targetX < worldX){
            nextAnimation = animations.get("move_left");
        }

        if(nextAnimation != null) {
            if(currentAnimation != nextAnimation) {
                if(currentAnimation != null) currentAnimation.stop();
                currentAnimation = nextAnimation;
                currentAnimation.start();
            }
            currentAnimation.update();
        }
    }

    public void determineNewPosition(){
        if(isMoving) return;

        int shouldMove = random.nextInt(moveChance);
        if(!forceMove && (shouldMove != 1 || !canMove)) return;

        forceMove = false;
        isMoving = true;
        int newPosition = position;
        if(position == 0 || position == 2){ // If left or right, then move to middle
            newPosition = 1;

        } else if(position == 1) { // If middle, randomly move left/right
            boolean left = random.nextBoolean();
            if(left) newPosition = 0;
            else newPosition = 2;
        } 

        targetX = positions.get(newPosition);
        if(targetX > worldX){
            speed = Math.abs(speed); // Moves right
        } else {
            speed = -Math.abs(speed); // Moves left
        }
    }

    public void move(){
        if(!isMoving || !isActive) return;
        worldX += speed;

        if((speed > 0 && worldX >= targetX) || (speed < 0 && worldX <= targetX)){
            worldX = targetX;
            if(targetX < positions.get(1)) position = 0; // Left
            else if(targetX == positions.get(1)) position = 1; // Middle
            else position = 2; // Right

            isMoving = false;
        }
    }

    public void takeDamage(int damage) {
        if(isDead || !isActive) return;

        soundManager.playClip("boss_hit", false);
        currentPhase.takeDamage(damage);
        if(currentPhase.getHP() <= 0){
            panel.addScore(currentPhase.getScore());
            doNextPhase();
        }
    }

    public void doDeath(){
        soundManager.stopBGM();
        soundManager.playClip("boss_death", false);
        isDead = true;
        isActive = false;
        currentAnimation = animations.get("idle");
        deathEffect = new GrayScaleFX();
        fxManager.addEffect(deathEffect);
    }

    public void doNextPhase(){
        moveChance = baseMoveChance;
        canMove = true;
        if(phaseIndex >= phases.size()) { // No more phases
            doDeath();
            return;
        };

        Phase phase = phases.get(phases.size() - 1 - phaseIndex);
        phaseIndex++;
        currentPhase = phase;
        currentPhase.startPhase();
    }

    public Rectangle2D.Double getBoundingRectangle(){
        return new Rectangle2D.Double(worldX, worldY, width, height);
    }

    public void activate(){
        isActive = true;
        soundManager.playBgm(bgm);
        doNextPhase();
    }

    public ArrayList<Phase> getPhases() { return this.phases; }
    public int getPhaseTimeLeft() { return this.currentPhase.getTimeLeft(); }
    public boolean hasFailedPhase() { return this.hasFailedPhase; }
    public boolean isDead(){ return this.isDead; }
    public boolean isActive(){ return this.isActive; }
    public boolean isMoving(){ return this.isMoving; }

    public double getWorldX(){ return this.worldX; }
    public double getWorldY(){ return this.worldY; }
    public void setWorldX(double worldX){ this.worldX = worldX; }
    public void setWorldY(double worldY){ this.worldY = worldY; }
    public void setMoveChance(int moveChance){ this.moveChance = moveChance; }
    public void setCanMove(boolean canMove){ this.canMove = canMove; }
    public void forceMove(){ this.forceMove = true; }

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public String getBgm() { return this.bgm; }
    public BufferedImage getBgImage(){ return this.bgImage; }
    public BufferedImage getRepeatBgImage(){ return this.repeatBgImage; }
    public BufferedImage getParallaxImage(){ return this.parallaxImage; }
}
