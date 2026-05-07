// The only reason I needed this class was because my game used lots of timers that depending on System.currentTimeMillis().
// Pausing the game would break all of those timers, so I needed pausing the game to actually pause "game" time. Hence,
// this game clock.
public class GameClock {
    private static GameClock instance = null;
    private long gameTime, lastUpdate;
    private boolean paused;

    public GameClock(){
        lastUpdate = System.currentTimeMillis();
        gameTime = 0;
        paused = false;
    }

    public static GameClock getInstance(){
        if(instance == null) instance = new GameClock();
        return instance;
    }

    public void update(){
        long now = System.currentTimeMillis();
        if(!paused){
            gameTime += (now - lastUpdate);
        }
        lastUpdate = now;
    }

    public void setPaused(boolean paused){ 
        if (this.paused && !paused) {
            lastUpdate = System.currentTimeMillis();
        }
        this.paused = paused; 
    }

    public void reset(){
        gameTime = 0;
        paused = false;
        lastUpdate = System.currentTimeMillis();
    }

    public long getTime(){ return this.gameTime; }
}
