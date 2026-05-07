import java.util.ArrayList;

// A singleton effect manager to make managing them a bit easier.
public class FXManager {
    private static FXManager instance = null;
    private ArrayList<ImageFX> effects;

    public FXManager(){
        effects = new ArrayList<ImageFX>();
    };

    public static FXManager getInstance(){
        if(instance == null){
            instance = new FXManager();
        }
        return instance;
    }

    public void updateEffects(){
        for(ImageFX effect : effects){
            effect.update();
        }
    }

    public void addEffect(ImageFX effect){
        effects.add(effect);
    }

    public void removeEffect(ImageFX effect){
        effects.remove(effect);
    }

    public void clearEffects() { effects.clear(); }
}
