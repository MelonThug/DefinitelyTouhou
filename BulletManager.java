import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Iterator;

public class BulletManager {
    private static BulletManager instance = null;
    private boolean pendingClear;
    private HashSet<Bullet> bullets;
    private Boss currentBoss;
    private Player player;
    private FXManager fxManager;

    public BulletManager(){
        bullets = new HashSet<>();
        fxManager = FXManager.getInstance();
    };

    public static BulletManager getInstance(){
        if(instance == null) instance = new BulletManager();
        return instance;
    }

    public void addBullet(Bullet bullet){
        bullets.add(bullet);
    }

    public void update(){
        if(currentBoss.getBoundingRectangle().intersects(player.getBoundingRectangle()) && !player.isImmune()){
            player.takeDamage();
            bullets.clear();
        }
        updateBullets();
    }

    public void updateBullets(){
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.update();
        
            if(bullet instanceof PlayerBullet){
                if(bullet.getBoundingRectangle().intersects(currentBoss.getBoundingRectangle())){
                    PlayerBullet b = (PlayerBullet)bullet;
                    currentBoss.takeDamage(b.getDamage());
                    it.remove();
                    continue;
                }

            } else if (bullet instanceof BossBullet){
                if(bullet.getBoundingRectangle().intersects(player.getBoundingRectangle()) && !player.isImmune()){
                    player.takeDamage();
                    clearBullets();
                }
            }   

            if (bullet.isOffWorld()) {
                fxManager.removeEffect(bullet.getImageEffect());
                it.remove();
            }
        }

        if(pendingClear){
            bullets.clear();
            pendingClear = false;
        }
    }

    public void moveBullets(){
        for(Bullet b : bullets){
            b.move();
        }
    }

    public void drawBullets(Graphics2D g2, Camera camera){
        for(Bullet b : bullets){
            b.draw(g2, camera);
        }
    }

    public void reset(){
        bullets.clear();
        currentBoss = null;
        player = null;
    }

    public void clearBullets(){ this.pendingClear = true; } // bullets.clear() caused ConcurrentModificationExceptions
    public void setCurrentBoss(Boss boss) { this.currentBoss = boss; }
    public void setPlayer(Player player) { this.player = player; }
    public Boss getCurrentBoss() { return this.currentBoss; }
    public Player getPlayer() { return this.player; }
}
