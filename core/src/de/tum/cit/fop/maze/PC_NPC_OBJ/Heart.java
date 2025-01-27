package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;

/**
 * Represents a heart collectible that the player can pick up to heal.
 */
public class Heart extends Collectable {
    private AnimationMNGR animationMNGR;
    private float time = 0f;
    private TextureRegion currentFrame;

    /**
     * Creates a new Heart at the specified position.
     *
     * @param x the x-coordinate of the heart's initial position
     * @param y the y-coordinate of the heart's initial position
     */
    public Heart(float x, float y) {
        super(x, y);
        this.animationMNGR = new AnimationMNGR();
    }

    /**
     * Updates the heart's state.
     *
     * @param delta the time in seconds since the last update
     */
    public void update(float delta) {
        // No implementation needed for this method
    }

    /**
     * Updates the heart's state, including checking for collision with the player.
     *
     * @param delta  the time in seconds since the last update
     * @param player the player object
     */
    @Override
    public void update(float delta, Player player) {
        time += delta;
        if (!collected) {
            currentFrame = animationMNGR.getHeartAnimation().getKeyFrame(time, true);
        }
        if (!collected && player.getTilePosition(16, 16).x == this.getTilePosition(16, 16).x && player.getTilePosition(16, 16).y == this.getTilePosition(16, 16).y) {
            collected = true;
            player.heal(50);
        }
    }

    /**
     * Renders the heart on the screen.
     *
     * @param batch the SpriteBatch used for drawing
     */
    @Override
    public void render(SpriteBatch batch) {
        if (collected) return;

        float centeredX = x + 4;
        float centeredY = y + 4;

        batch.draw(currentFrame, centeredX, centeredY, 8, 8);
    }

    /**
     * Retrieves the tile position of the heart.
     *
     * @param tileWidth  the width of a single tile
     * @param tileHeight the height of a single tile
     * @return the tile position as a Vector2
     */
    public Vector2 getTilePosition(float tileWidth, float tileHeight) {
        return new Vector2((int) (x / tileWidth), (int) (y / tileHeight));
    }
}