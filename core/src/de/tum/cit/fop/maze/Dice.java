package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Dice extends Collectable {
    private AnimationMNGR animationMNGR;
    private float time = 0f;
    private TextureRegion currentFrame;
    private boolean minigameActive = false; // Flag for the minigame state

    public Dice(float x, float y) {
        super(x, y);
        this.animationMNGR = new AnimationMNGR();
    }

    @Override
    public void update(float delta, Player player) {
        time+=delta;
        if(!collected) {currentFrame = animationMNGR.getDiceAnimation().getKeyFrame(time, true);}
        //System.out.println("Player x: " + player.getTilePosition(16,16).x + " Player y: " + player.getTilePosition(16,16).y);
        //System.out.println("Dice x: " + x + " Dice y: " + y);
        if(!collected &&  player.getTilePosition(16,16).x == this.getTilePosition(16,16).x  && player.getTilePosition(16,16).y == this.getTilePosition(16,16).y){
            collected = true;
            minigameActive = true; // Activate the minigame when the dice is collected

        }
    }
    @Override
    public void update(float delta) {
        time+=delta;
    }
    @Override
    public void render(SpriteBatch batch) {
        if (collected) return;
        batch.draw(currentFrame, x, y, 16, 16);
    }
    public boolean isMinigameActive() {
        return minigameActive;
    }

    public void deactivateMinigame() {
        minigameActive = false;
    }
    public Vector2 getTilePosition(float tileWidth, float tileHeight) {
        return new Vector2((int) (x / tileWidth), (int) (y / tileHeight));
    }
}
