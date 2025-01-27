package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;

/**
 * Represents a hit particle effect that appears when the player or an enemy is hit.
 */
public class HitParticle {
    private AnimationMNGR animationMNGR;
    private float time = 0f;
    private TextureRegion currentFrame;
    private float x, y;
    private boolean appear = false;

    /**
     * Creates a new HitParticle at the specified position.
     *
     * @param x the x-coordinate of the hit particle's initial position
     * @param y the y-coordinate of the hit particle's initial position
     */
    public HitParticle(float x, float y) {
        this.animationMNGR = new AnimationMNGR();
        this.x = x;
        this.y = y;
    }

    /**
     * Updates the hit particle's state, including its position and appearance.
     *
     * @param delta  the time in seconds since the last update
     * @param player the player object
     * @param appear whether the hit particle should appear
     */
    public void update(float delta, Player player, boolean appear) {
        time += delta;
        this.currentFrame = animationMNGR.getHitAnimation1().getKeyFrame(time, true);
        x = player.getBounds().x;
        y = player.getBounds().y;
        this.appear = appear;
    }

    /**
     * Renders the hit particle on the screen.
     *
     * @param batch the SpriteBatch used for drawing
     */
    public void render(SpriteBatch batch) {
        if (!appear) return;
        batch.draw(currentFrame, x, y, 16, 16);
    }
}