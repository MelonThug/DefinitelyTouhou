// This camera follows and centers the player by default.
// It can also be locked, gradually transitioning to the target Y to lock at.
// Will be locked when entering boss arenas, and unlocked after killing a boss.
public class Camera {
    private double x, y, targetTransitionY, lerpSpeed;
    private BackgroundManager bgManager;
    private GamePanel panel;
    private boolean locked, transitioning;

    public Camera(GamePanel p){
        this.panel = p;
        bgManager = BackgroundManager.getInstance();
        locked = false;
        lerpSpeed = 0.02;
    }

    public void update(Player player){
        if(transitioning){ // Lerp to targetY for a smooth transition
            y += (targetTransitionY - y) * lerpSpeed;

            if(Math.abs(targetTransitionY - y) < 1){
                y = targetTransitionY;
                transitioning = false;
            }
            return;
        }

        if(locked) return;
        follow(player);
    }

    public void follow(Player player){
        double targetX = player.getWorldX() - panel.getWidth()/2;
        double targetY = player.getWorldY() - panel.getHeight()/2;

        double maxX = bgManager.getWorldWidth() - panel.getWidth();
        double maxY = bgManager.getWorldHeight() - panel.getHeight();

        x = Math.max(0, Math.min(targetX, maxX));
        y = Math.max(0, Math.min(targetY, maxY));
    }

    public void lock(Background background){  // Transition to boss arena
        this.locked = true; 
        this.transitioning = true;
        this.targetTransitionY = background.getStartY() - (panel.getHeight() - background.getHeight());
    }

    public void unlock(Player player){ // Transition to player
        this.locked = false;
        this.transitioning = true;
        this.targetTransitionY = player.getWorldY() - panel.getHeight()/2; 
    }

    public boolean isTransitioning(){ return this.transitioning; }
    public boolean isLocked() { return this.locked; }
    public double getX(){ return this.x; }
    public double getY(){ return this.y; }
}
