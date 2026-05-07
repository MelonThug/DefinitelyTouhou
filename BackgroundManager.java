import java.awt.Graphics2D;
import java.util.ArrayList;

// Background are segments that can join sequentially to form the world.
// The amount of backgrounds, and hence the height of the world, depends on the number of bosses in the game.
// Each boss has their own set of background images they use to build the world.
// BackgroundManager manages the various backgrounds, and knows the dimensions of the world
public class BackgroundManager {
    private ArrayList<Background> backgrounds;
    private ArrayList<ParallaxLayer> parallaxLayers;
    private static BackgroundManager instance = null;
    private double worldWidth;

    public BackgroundManager(){
        backgrounds = new ArrayList<>();
        parallaxLayers = new ArrayList<>();
        worldWidth = 500.0; // The world width is predetermined to be 500 in this game, which is why I hardcoded it here.
    }

    public static BackgroundManager getInstance(){
        if(instance == null) instance = new BackgroundManager();
        return instance;
    }

    public void addBackground(Background background){
        backgrounds.add(background);
    }

    public void addParallaxLayer(ParallaxLayer layer){
        parallaxLayers.add(layer);
    }

    public void draw(Graphics2D g2, Camera camera){
        for(Background background : backgrounds){
            background.draw(g2, camera);
        }

        for(ParallaxLayer layer : parallaxLayers){
            layer.draw(g2, camera);
        }
    }

    // World height varies depending on how many bosses, and in turn backgrounds, exist.
    public double getWorldHeight(){
        if(backgrounds.isEmpty()) return 0;
        
        int lastPos = backgrounds.size() - 1;
        return backgrounds.get(lastPos).getEndY();
    }

    public Background getBackgroundContaining(double y){
        for(Background background : backgrounds){
            if(y >= background.getStartY() && y < background.getEndY()) {
                return background;
            }
        }
        return null;
    }

    public double getWorldWidth(){ return this.worldWidth; }
    public void clearBackgrounds(){ backgrounds.clear(); }
}
