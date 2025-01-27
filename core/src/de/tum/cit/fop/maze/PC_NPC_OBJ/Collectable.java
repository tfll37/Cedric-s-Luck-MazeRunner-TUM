package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.fop.maze.MAZELOGIC.IGameOBJ;

/**
 * An abstract base class for collectible items within the game.
 * <p>
 * A collectable has a position defined by its x and y coordinates, a bounding
 * rectangle for collision or pickup detection, and a flag indicating whether
 * it has been collected.
 */
public abstract class Collectable implements IGameOBJ {

    /**
     * The x-coordinate of this collectable.
     */
    protected float x;

    /**
     * The y-coordinate of this collectable.
     */
    protected float y;

    /**
     * Indicates whether this collectable has been collected.
     */
    protected boolean collected = false;

    /**
     * The bounding rectangle of this collectable, used for collision or pickup detection.
     */
    protected Rectangle bounds;

    /**
     * Creates a new collectable at the specified coordinates.
     *
     * @param x the x-coordinate of this collectable
     * @param y the y-coordinate of this collectable
     */
    public Collectable(float x, float y) {
        this.x = x;
        this.y = y;
        this.collected = false;
        this.bounds = new Rectangle();
    }

    /**
     * Marks this collectable as collected.
     */
    public void collected() {
        collected = true;
    }

    /**
     * Checks whether this collectable has been collected.
     *
     * @return true if it has been collected; false otherwise
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Updates the state of this collectable.
     * <p>
     * This method is declared abstract because different collectables may
     * have different behaviors when updated.
     *
     * @param delta the time in seconds since the last update
     */
    @Override
    public abstract void update(float delta);

    /**
     * Updates the state of this collectable, optionally using the provided player data.
     * <p>
     * Some collectables may require information about the player
     * (e.g., position) to determine if the item has been picked up.
     *
     * @param delta  the time in seconds since the last update
     * @param player the player object interacting with this collectable
     */
    public abstract void update(float delta, Player player);

    /**
     * Renders this collectable using the provided SpriteBatch.
     * <p>
     * Must be implemented by subclasses to define how the collectable should be drawn.
     *
     * @param batch the SpriteBatch used for drawing
     */
    @Override
    public abstract void render(SpriteBatch batch);

    /**
     * Retrieves the bounding rectangle of this collectable, useful for collision or
     * pickup detection.
     *
     * @return the {@link Rectangle} representing this collectable's bounds
     */
    @Override
    public Rectangle getBounds() {
        return bounds;
    }

}
