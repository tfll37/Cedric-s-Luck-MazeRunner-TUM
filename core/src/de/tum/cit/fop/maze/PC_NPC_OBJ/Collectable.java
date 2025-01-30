package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.fop.maze.MAZELOGIC.IGameOBJ;


/**
 * The type Collectable.
 */
public abstract class Collectable implements IGameOBJ {


    /**
     * The X.
     */
    protected float x;


    /**
     * The Y.
     */
    protected float y;

    /**
     * The Counted.
     */
    protected boolean counted = false;


    /**
     * The Collected.
     */
    protected boolean collected = false;


    /**
     * The Bounds.
     */
    protected Rectangle bounds;


    /**
     * Instantiates a new Collectable.
     *
     * @param x the x
     * @param y the y
     */
    public Collectable(float x, float y) {
        this.x = x;
        this.y = y;
        this.collected = false;
        this.bounds = new Rectangle();
    }


    /**
     * Collected.
     */
    public void collected() {
        collected = true;
    }

    /**
     * Counted.
     */
    public void counted() {
        counted = true;
    }


    /**
     * Is collected boolean.
     *
     * @return the boolean
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Is counted boolean.
     *
     * @return the boolean
     */
    public boolean isCounted(){
        return counted;
    }


    @Override
    public abstract void update(float delta);


    /**
     * Update.
     *
     * @param delta  the delta
     * @param player the player
     */
    public abstract void update(float delta, Player player);


    @Override
    public abstract void render(SpriteBatch batch);


    @Override
    public Rectangle getBounds() {
        return bounds;
    }

}
