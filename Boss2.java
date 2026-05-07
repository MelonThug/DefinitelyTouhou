public class Boss2 extends Boss {
    public Boss2(GamePanel p){
        super(p);
        
        name = "Boss2";
        bgImage = ImageManager.loadBufferedImage("images/stage2_bg.png");
        repeatBgImage = ImageManager.loadBufferedImage("images/stage2_repeat_bg.png");
        parallaxImage = ImageManager.loadBufferedImage("images/stage2_parallax.png");
        bgm = "boss2_bgm";
        soundManager.loadClip("sounds/boss2_bgm.wav", bgm, 4);

        Animation idle = new Animation(true, 4, 1, 0, "images/boss2_idle.png", null);
        Animation left = new Animation(false, 4, 1, 0, "images/boss2_moveleft.png", null);
        Animation right = new Animation(false, 4, 1, 0, "images/boss2_moveright.png", null);
        animations.put("idle", idle);
        animations.put("move_left", left);
        animations.put("move_right", right);
        currentAnimation = idle;
        currentAnimation.start();

        width = currentAnimation.getWidth();
        height = currentAnimation.getHeight();
        speed = 2;

        Phase phase1 = new PhaseAimedShotgun(this);
        Phase phase2 = new PhaseSideBullets(this);
        Phase phase3 = new PhaseFallingLines(this);
        phases.add(phase3);
        phases.add(phase2);
        phases.add(phase1);
    }
}
