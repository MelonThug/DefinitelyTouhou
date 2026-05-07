public class Boss1 extends Boss {
    public Boss1(GamePanel p){
        super(p);
        
        name = "Boss1";
        bgImage = ImageManager.loadBufferedImage("images/stage1_bg.png");
        repeatBgImage = ImageManager.loadBufferedImage("images/stage1_repeat_bg.png");
        parallaxImage = ImageManager.loadBufferedImage("images/stage1_parallax.png");
        bgm = "boss1_bgm";
        soundManager.loadClip("sounds/boss1_bgm.wav", bgm, 4);

        Animation idle = new Animation(true, 4, 1, 0, "images/boss1_idle.png", null);
        Animation left = new Animation(false, 4, 1, 0, "images/boss1_moveleft.png", null);
        Animation right = new Animation(false, 4, 1, 0, "images/boss1_moveright.png", null);
        animations.put("idle", idle);
        animations.put("move_left", left);
        animations.put("move_right", right);
        currentAnimation = idle;
        currentAnimation.start();

        width = currentAnimation.getWidth();
        height = currentAnimation.getHeight();
        speed = 2;

        Phase phase1 = new PhaseStaticShooting(this);
        Phase phase2 = new PhaseAimedBursts(this);
        Phase phase3 = new PhaseCircleWaves(this);
        phases.add(phase3);
        phases.add(phase2);
        phases.add(phase1);
    }
}
