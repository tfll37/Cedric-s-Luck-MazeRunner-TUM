package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * The interface Game obj.
 */
public interface IGameOBJ {
    /**
     * Update.
     *
     * @param delta the delta
     */
    void update(float delta);

    /**
     * Render.
     *
     * @param batch the batch
     */
    void render(SpriteBatch batch);

    /**
     * Gets bounds.
     *
     * @return the bounds
     */
    Rectangle getBounds();
}
