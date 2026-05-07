import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

public class GamePanel extends JPanel implements Runnable {
	private GameClock gameClock;
	private SoundManager soundManager;
	private BackgroundManager bgManager;
	private FXManager fxManager;
	private BulletManager bulletManager;

	private int sleepTime, score, width, height, frames, fps, hudHeight;
	private int currentBossIndex, numRepeatBgs, lifeScoreVal;
	private long lastFrameTime;
	private boolean isRunning, isPaused, gameOver, victory;
	private String stageText;

	private Thread gameThread;
	private BufferedImage image;
	private Camera camera;
	private Player player;
	private Boss currentBoss;
	private ArrayList<Boss> bosses;

	public GamePanel(int width, int height) {
		this.width = width;
		this.height = height;
		this.setPreferredSize(new Dimension(width, height));

		isRunning = isPaused = gameOver = victory = false;
		sleepTime = 16; // Targetting ~60fps
		fps = 1000/sleepTime; // Just a starting value, real value will be calculated dynamically every second
		numRepeatBgs = 4; // How many times repeatable backgrounds will repeat
		lifeScoreVal = 400;
		hudHeight = 50;

		gameClock = GameClock.getInstance();
		bgManager = BackgroundManager.getInstance();
		soundManager = SoundManager.getInstance();
		fxManager = FXManager.getInstance();
		bulletManager = BulletManager.getInstance();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bosses = new ArrayList<>();
	}

	public void createGameEntities() {
		camera = new Camera(this);
		Boss boss1 = new Boss1(this);
		Boss boss2 = new Boss2(this);
		bosses.add(boss1);
		bosses.add(boss2);

		// "Creating" the world by chaining backgrounds together.
    	int y = 0;
    	for (int i = bosses.size() - 1; i >= 0; i--) {
			Boss boss = bosses.get(i);
			boss.setWorldY(y + 50); 
			boss.setWorldX(getWidth()/2 - boss.getWidth()/2);

			Background arenaBg = new Background(y, boss, true); // Boss arena background, only 1
    	    bgManager.addBackground(arenaBg);
    	    y += arenaBg.getHeight();

    	    for (int j = 0; j < numRepeatBgs; j++) { // Repeat the boss' repeatable background + parallax numRepeatBgs times
				Background repeatBg = new Background(y, boss, false);
    	        bgManager.addBackground(repeatBg);

				BufferedImage parallaxImg = boss.getParallaxImage();
				bgManager.addParallaxLayer(new ParallaxLayer(y, parallaxImg, 1.2));

    	        y += repeatBg.getHeight();
    	    }
    	}

		player = new Player();
		player.setWorldX(bgManager.getWorldWidth()/2 - player.getWidth()/2);
        player.setWorldY(bgManager.getWorldHeight() - 100); 

		currentBossIndex = 0;
		currentBoss = bosses.get(currentBossIndex);
		currentBossIndex++;
		bulletManager.setPlayer(player);
		bulletManager.setCurrentBoss(currentBoss);
	}

	public void run () {
		lastFrameTime = System.currentTimeMillis();
		try {
			isRunning = true;
			while (isRunning) {
				long beginTime = System.currentTimeMillis();

				if (!isPaused) gameUpdate();
				if (!gameOver) gameRender();

				// This sleep timer logic is a bit different than taught in class. I noticed when I ran my program on 2 different machines, the FPS
				// was different. Some research led me to believe it was due to the differences in OS scheduling and delays in waking up the thread,
				// and that calculating a difference rather than a static sleep time would keep the FPS constant.
				long timeTaken = System.currentTimeMillis() - beginTime;
				long timeLeft = sleepTime - timeTaken;
				if(timeLeft > 0) Thread.sleep(timeLeft);	

				// FPS calculation
				frames++;
            	long currentTime = System.currentTimeMillis();
            	if (currentTime - lastFrameTime >= 1000) {
            	    fps = frames;
            	    frames = 0;
            	    lastFrameTime = currentTime;
            	}
			}

		} catch(InterruptedException e) {}
	}


	public void gameUpdate() {
		gameClock.update();
		player.update();
		player.move();
		currentBoss.update();
		currentBoss.move();
		bulletManager.update();
		bulletManager.moveBullets();
		fxManager.updateEffects();

		Background background = bgManager.getBackgroundContaining(player.getWorldY() + player.getHeight()*2); // Height*2 for a small buffer
		boolean inAliveBossArena = background != null && background.isBossArena() && !background.getBoss().isDead();

		if(inAliveBossArena && !camera.isLocked() ) { 
			soundManager.stopBGM();
		    camera.lock(background);
			player.lockControl();
		}

		if(!camera.isTransitioning()){
			player.unlockControl();
		}

		if(camera.isLocked()&& !camera.isTransitioning() && !currentBoss.isActive()){
			currentBoss.activate();
		}

		if(player.isDead() || currentBoss.hasFailedPhase()){
			soundManager.stopBGM();
			soundManager.playClip("game_over", false);
			victory = false;
			endGame();
			return;
		}

		if(currentBoss.isDead()){
			camera.unlock(player);
			player.lockControl(); // For the camera transition back to player-centered

			if(currentBossIndex >= bosses.size()){ // No bosses left
				victory = true;
				score += player.getHP() * lifeScoreVal;
				endGame();

			} else {
				soundManager.playBgm("wind");
				bulletManager.clearBullets();
				Boss nextBoss = bosses.get(currentBossIndex);
				currentBossIndex++;
				currentBoss = nextBoss;
				bulletManager.setCurrentBoss(currentBoss);
			}
		}

		camera.update(player);
	}

	public void gameRender() {
		Graphics2D imageContext = (Graphics2D) image.getGraphics(); // Back buffer
		// Draw everything onto the back buffer by supplying its Graphics2D object to the draw methods
		bgManager.draw(imageContext, camera);
		bulletManager.drawBullets(imageContext, camera);
		if(player != null){
			player.draw(imageContext, camera);
		}

		for(Boss boss : bosses){
			boss.draw(imageContext, camera);
		}

		drawGameInfo(imageContext);
		Graphics2D g2 = (Graphics2D) getGraphics();	// Get the graphics context for the game panel
		if(gameOver){
			if(victory) drawVictory(g2);
			else drawGameOver(g2);
			
		} else {
			
			if(isPaused) drawPause(imageContext);
			g2.drawImage(image, 0, 0, width, height, null); // Blit the back buffer onto the game panel		
		}
		
		imageContext.dispose();
		g2.dispose();
	}


	public void startGame() {
		if (isRunning) return;
		isPaused = false;
		gameOver = false;
		victory = false;
		isRunning = true;
		currentBoss = null;
		player = null;
		score = 0;
		bosses.clear();
		bgManager.clearBackgrounds();
		fxManager.clearEffects();
		bulletManager.reset();
		gameClock.reset();

		createGameEntities();
		gameThread = new Thread (this);			
		gameThread.start();
		soundManager.playBgm("wind");
	}

	public void updateDirections(int direction){
		if (player != null && !isPaused) {
			player.setMoveDirection(direction);
		}
	}

	public void pauseGame() {
		if (isRunning) {
			if (isPaused) {
				isPaused = false;
				soundManager.playClip("game_unpause", false);
				soundManager.playBgm(soundManager.getCurrentBgm());
			} else {
				isPaused = true;
				soundManager.playClip("game_pause", false);
				soundManager.pauseClip(soundManager.getCurrentBgm());
			}
		}
		gameClock.setPaused(isPaused);
	}

	public void endGame() {
		if(gameOver || !isRunning) return;
		gameOver = true;
		isRunning = false;

		soundManager.stopBGM();
		if(victory) soundManager.playClip("game_win", false);
		else soundManager.playClip("game_over", false);
		gameRender(); 
	}



	public void drawCenteredText(Graphics2D g2, String text, Color color, int size, int y){
		g2.setColor(color);
		g2.setFont(new Font("Arial", Font.BOLD, size));

		FontMetrics fm = g2.getFontMetrics();
		int x = (getWidth() - fm.stringWidth(text)) / 2;
		g2.drawString(text, x, y);
	}	

	public void drawTimer(Graphics2D g2){ // Boss phase timer
		String text = String.valueOf(currentBoss.getPhaseTimeLeft());
		drawCenteredText(g2, text, Color.white, 15, hudHeight + 40);
	}

	public void drawGameOver(Graphics2D g2){
		ImageFX gameOverTint = new TintFX(70, 0, 0, 0);
		fxManager.addEffect(gameOverTint);
		BufferedImage grayImage = gameOverTint.apply(image); // Apply grayscale to the finished back buffer

		int y = getHeight()/2;
		g2.drawImage(grayImage, 0, 0, width, height, null);
		drawCenteredText(g2, "GAME OVER", Color.WHITE, 40, y + 10);
		drawCenteredText(g2, "Score: " + score, Color.WHITE, 20, y + 40);
	}

	public void drawVictory(Graphics2D g2){
		ImageFX gameOverGrayscale = new GrayScaleFX();
		fxManager.addEffect(gameOverGrayscale);
		BufferedImage grayImage = gameOverGrayscale.apply(image);

		String lifeBonusScore = Integer.toString(player.getHP() * lifeScoreVal);
		int y = getHeight()/2;
		g2.drawImage(grayImage, 0, 0, width, height, null);
		drawCenteredText(g2, "VICTORY", Color.CYAN, 40, y + 10);
		drawCenteredText(g2, "Life Bonus: " + 
							 Integer.toString(player.getHP()) +  " x " + Integer.toString(lifeScoreVal) + 
							 " = " + lifeBonusScore, Color.CYAN, 15, y + 40);

		drawCenteredText(g2, "Score: " + score, Color.CYAN, 20, y + 70);
	}

	public void drawPause(Graphics2D g2){
		int y = getHeight()/2;
		drawCenteredText(g2, "PAUSED", Color.WHITE, 40, y + 10);
	}

	// Originally, I was just using the standard InfoPanel from class to handle the information display.
	// With that implementation, I had 4 calls to setText() for the various text fields. I noticed the
	// game's performance began to struggle the more calls to setText() per update, so I tried limiting 
	// info refreshes to once per second instead of every update. Even that still caused some delay, making
	// movement feel very janky. In the end, I decided to forego the info panel, and manually draw the information
	// on the GamePanel's image, to bypass needing to use setText().
	public void drawGameInfo(Graphics2D g2) {
		// These make the text look a bit nicer, they were very sharp and jagged otherwise
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	    g2.setColor(new Color(200, 200, 200));
	    g2.fill(new Rectangle2D.Double(0, 0, width, hudHeight - 1));

		int hpBarHeight = 22;
		g2.setColor(new Color(230, 230, 230));
		g2.fill(new Rectangle2D.Double(0, hudHeight - 1, width, hpBarHeight));

	    Font labelFont = new Font("Arial", Font.BOLD, 14);
	    Font statFont = new Font("Arial", Font.PLAIN, 14);
	    FontMetrics labelMetrics = g2.getFontMetrics(labelFont);
	    FontMetrics statMetrics = g2.getFontMetrics(statFont);

	    String[] labels = {"Lives", "Score", "Stage", "FPS"};
	    String[] values = {Integer.toString(getPlayerHP()), Integer.toString(score), stageText, Integer.toString(fps)};

	    int numColumns = 4;
	    int columnWidth = width / numColumns;

	    for (int i = 0; i < numColumns; i++) {
	        int columnCenterX = (i * columnWidth) + (columnWidth / 2);

	        g2.setFont(labelFont);
	        g2.setColor(Color.BLACK);
	        int labelX = columnCenterX - (labelMetrics.stringWidth(labels[i]) / 2);
	        g2.drawString(labels[i], labelX, hudHeight/2 - 5);

	        g2.setFont(statFont);
	        g2.setColor(Color.BLACK);
	        int valueX = columnCenterX - (statMetrics.stringWidth(values[i]) / 2);
	        g2.drawString(values[i], valueX, hudHeight/2 + 15);
	    }

		drawHPBars(g2, hpBarHeight);
		if(currentBoss.isActive()) drawTimer(g2);
	}

	
    public void drawHPBars(Graphics2D g2, int barHeight){
        if(!currentBoss.isActive()) return;

        int phaseCount = currentBoss.getPhases().size();
        if(phaseCount == 0) return;

        int height = hudHeight - 1;
        int width = getWidth();
        int separatorWidth = 2;
        int totalSeparatorSpace = (phaseCount - 1) * separatorWidth;
        int usableWidth = width - totalSeparatorSpace;

        double phaseWidth = (double)usableWidth / phaseCount; // Width of each phase HP bar
        int x = 0;
        for(int i = 0; i < phaseCount; i++){
            int barX = x;

            int barW;
            if(i == phaseCount - 1) barW = width - barX; // If last bar, fill remaining space
            else barW = (int)Math.round(phaseWidth);
            
            Phase phase = currentBoss.getPhases().get(i);
            double hpPercent = (double)phase.getHP() / phase.getMaxHP();
            g2.setColor(Color.red);
            g2.fillRect(barX, height, (int)(hpPercent * barW), barHeight); // Red HP part of the bar

            if(i > 0){
                g2.setColor(Color.black);
                g2.fillRect(barX - separatorWidth, height, separatorWidth, barHeight); // Black separator
            }

            x += barW + separatorWidth;
        }
    }

	public int getPlayerHP(){ 
		if(player != null) return this.player.getHP();
		return -1;
	}

	public void setStageText(String s) { this.stageText = s; }
	public void addScore(int score) { this.score += score; }
	public Player getPlayer() { return this.player; }
	public Boss getCurrentBoss() { return this.currentBoss; }
	public String getStage(){ return this.stageText; }
	public int getFPS(){ return this.fps; }
	public int getScore(){ return this.score; }
	public int getHUDHeight(){ return this.hudHeight; }
}