// A Boss consists of Phases. A Phase contols the attack pattern and has its own HP and duration.
public abstract class Phase {
    private long startTime, lastTimeAudio, timeAudioDelay;
    protected GameClock gameClock;
    protected boolean isActive, failed;
    protected int maxHP, hp, duration, score;
    protected BackgroundManager bgManager;
    protected BulletManager bulletManager;
    protected SoundManager soundManager;
    protected Boss boss;

    public Phase(Boss boss){
        this.boss = boss;
        isActive = failed = false;
        bulletManager = BulletManager.getInstance();
        soundManager = SoundManager.getInstance();
        bgManager = BackgroundManager.getInstance();
        gameClock = GameClock.getInstance();
        timeAudioDelay = 1000; // Delay between the time beeping SFX when time is running out
        score = 500;
    }

    public void startPhase(){
        setupPhase();
        isActive = true;
        startTime = gameClock.getTime();
    }

    public void updatePhase(){
        if(!isActive) return;
        
        long now = gameClock.getTime();
        if(now - startTime >= duration) { // Time limit up
            soundManager.playClip("phase_fail", false);
            failed = true;
            hp = 0;
            endPhase();
        }

        updatePhaseLogic(now);
    };

    public abstract void updatePhaseLogic(long currentTime);
    public abstract void setupPhase();

    public void endPhase(){
        isActive = false;
        bulletManager.clearBullets();
        if(!failed) soundManager.playClip("phase_success", false);
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if(hp <= 0){
            endPhase();
        }
    }

    public int getTimeLeft(){
        long now = gameClock.getTime();
        long elapsed = now - startTime;
        long remaining = duration - elapsed;
        int remTime = (int)remaining/1000;
        if(remTime < 4 && now - lastTimeAudio > timeAudioDelay){
            lastTimeAudio = now;
            soundManager.playClip("time_low", false);
        } 
        return (int)(remaining/1000);
    }

    public boolean isActive(){ return this.isActive; }
    public int getHP(){ return this.hp; }
    public int getMaxHP(){ return this.maxHP; }
    public int getScore(){ return this.score; }
    public boolean isFailed(){ return this.failed; }
}
