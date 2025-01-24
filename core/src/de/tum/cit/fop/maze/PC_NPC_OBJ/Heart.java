package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;

public class Heart extends Collectable{
    private AnimationMNGR animationMNGR;
    private float time = 0f;
    private TextureRegion currentFrame;
    public Heart(float x, float y) {
        super(x, y);
        this.animationMNGR = new AnimationMNGR();
    }

    public void update(float delta) {
    }
    @Override
    public void update(float delta, Player player) {
        time += delta;
        if(!collected) {
            currentFrame = animationMNGR.getHeartAnimation().getKeyFrame(time, true);
        }
        if (!collected && player.getTilePosition(16, 16).x == this.getTilePosition(16, 16).x && player.getTilePosition(16, 16).y == this.getTilePosition(16, 16).y) {
            collected = true;
            player.increaseHealth(50);

        }

    }
    @Override
    public void render(SpriteBatch batch) {
        if (collected) return;

        float centeredX = x + 4;
        float centeredY = y + 4;

        batch.draw(currentFrame, centeredX, centeredY, 8, 8);
    }
    public Vector2 getTilePosition(float tileWidth, float tileHeight) {
        return new Vector2((int) (x / tileWidth), (int) (y / tileHeight));
    }
}
