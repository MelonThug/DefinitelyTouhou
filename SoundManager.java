// for playing sound clips
import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;

public class SoundManager {
	private HashMap<String, Clip> clips;
	private HashMap<String, Integer> clipPositions; // Keeps track of frame positions per clip, mainly for pausing/resuming.
	private String currentBgm;
	private static SoundManager instance = null;
	
	private SoundManager () {
		clips = new HashMap<String, Clip>();
		clipPositions = new HashMap<>();
		
		loadClip("sounds/boss_attack.wav", "boss_attack", -10);
		loadClip("sounds/boss_shoot.wav", "boss_shoot", -4);
		loadClip("sounds/boss_death.wav", "boss_death", 0);
		loadClip("sounds/boss_hit.wav", "boss_hit", -4);

		loadClip("sounds/game_over.wav", "game_over", 0);
		loadClip("sounds/game_pause.wav", "game_pause", 0);
		loadClip("sounds/game_unpause.wav", "game_unpause", 0);
		loadClip("sounds/game_win.wav", "game_win", 0);

		loadClip("sounds/player_death.wav", "player_death", -6);
		loadClip("sounds/player_shoot.wav", "player_shoot", -10);
		loadClip("sounds/phase_fail.wav", "phase_fail", 0);
		loadClip("sounds/phase_success.wav", "phase_success", 0);

		loadClip("sounds/time_low.wav", "time_low", -1);
		loadClip("sounds/wind.wav", "wind", 0);
		this.currentBgm = null;
	}


	public static SoundManager getInstance() {
		if (instance == null) instance = new SoundManager();
		return instance;
	}		


    public Clip loadClip (String fileName, String clipName, float volume) {
 		AudioInputStream audioIn;
		Clip clip = null;

		try {
    		File file = new File(fileName);
    		audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL()); 
    		clip = AudioSystem.getClip();
    		clip.open(audioIn);
			setVolume(clip, volume);
			clips.put(clipName, clip);
		} catch (Exception e) {
 			System.out.println ("Error opening sound files: " + e);
		}

    	return clip;
	}

    public void playClip(String title, boolean looping) {
		Clip clip = getClip(title);
		if (clip != null) {
			int pos = clipPositions.getOrDefault(title, 0);
			if(pos == 0) clip.stop();
			clip.setFramePosition(pos);
			if (looping) {
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			} else {
				clip.start();
			} 
		}
    }

    public void stopClip(String title) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.stop();
			clip.setFramePosition(0);
			clipPositions.put(title, 0);
		}
    }

	public void setVolume(Clip clip, float gainDB) {
	    try {
	        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
	        control.setValue(gainDB);
	    } catch (IllegalArgumentException e) {
	        System.out.println("Volume control not supported for this clip.");
	    }
	}

	// Added pause functionality, mainly for the BGM if the game is paused.
	public void pauseClip(String title) {
        Clip clip = clips.get(title);
        if (clip != null && clip.isRunning()) {
            int pos = clip.getFramePosition();
            clip.stop();
            clipPositions.put(title, pos);
        }
    }

	public void playBgm(String title) { 
		this.currentBgm = title; 
		playClip(title, true);
	}

	public void stopBGM(){
		stopClip(currentBgm);
	}

	public Clip getClip (String title) { return clips.get(title); }
	public String getCurrentBgm(){ return this.currentBgm; }
}