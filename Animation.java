import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {
    private ArrayList<AnimFrame> frames;
    private int currFrameIndex, imgWidth, imgHeight;
    private long animTime, startTime, totalDuration;
    private boolean loop, isActive;
    private ImageFX effect; // Can store an effect to apply to the animation

    public Animation(boolean loop, int width, int height, int row, String path, ImageFX effect) {
        frames = new ArrayList<AnimFrame>();
        totalDuration = 0;
        isActive = false;
	    this.loop = loop;
        this.effect = effect;

        // Was getting some rounding issues due to my spritesheets not being perfectly divisible, so
        // I had to use doubles for calculations then recast to int.
        Image image = ImageManager.loadImage(path);
        double frameWidth = (double) image.getWidth(null) / width;
        double frameHeight = (double) image.getHeight(null) / height;
        imgWidth = (int)frameWidth;
        imgHeight = (int)frameHeight;

        for (int j = 0; j < width; j++) {
            BufferedImage frameImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) frameImage.getGraphics();

            int sx1 = (int)Math.round(j * frameWidth);
            int sx2 = (int)Math.round((j + 1) * frameWidth);
            int sy1 = (int)Math.round(row * frameHeight);
            int sy2 = (int)Math.round((row + 1) * frameHeight);

            g.drawImage(image,
                0, 0, imgWidth, imgHeight,
                sx1, sy1, sx2, sy2,
                null
            );
        
            addFrame(frameImage, 100);
        }
    }

    public synchronized void addFrame(Image image, long duration) {
        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }

    public synchronized void start() {
	    isActive = true;
        animTime = 0;
        currFrameIndex = 0;
	    startTime = System.currentTimeMillis();
    }

    public synchronized void stop() {
	    isActive = false;
    }

    public synchronized void update() {
        if (!isActive) return;

        long currTime = System.currentTimeMillis();
	    long elapsedTime = currTime - startTime;
	    startTime = currTime;

        if (frames.size() > 1) {
            animTime += elapsedTime;
            if (animTime >= totalDuration) {
		        if (loop) {
                    animTime = animTime % totalDuration;
                    currFrameIndex = 0;
		        } else { 
	                isActive = false;
		        }
            }

	        if (!isActive) return;

            while (animTime > getFrame(currFrameIndex).endTime) {
                currFrameIndex++;
            }
        }
    }

    public synchronized Image getImage() {
        if (frames.size() == 0) {
            return null;
        } else {
            Image img = getFrame(currFrameIndex).image;
            if(effect != null){
                img = effect.apply((BufferedImage)img); // Effect is applied to the image is one was given
            }
            return img;
        }
    }

    public int getNumFrames() { return frames.size(); }
    private AnimFrame getFrame(int i) {return frames.get(i); }
    public boolean isStillActive () { return isActive; }
    public int getWidth() { return this.imgWidth; }
    public int getHeight() { return this.imgHeight; }
    public ImageFX getEffect() { return this.effect; }
    
    private class AnimFrame {
        Image image;
        long endTime;

        public AnimFrame(Image image, long endTime) {
            this.image = image;
            this.endTime = endTime;
        }
    }

}
