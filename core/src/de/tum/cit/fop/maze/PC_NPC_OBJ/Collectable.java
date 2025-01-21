package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.fop.maze.MAZELOGIC.IGameOBJ;

public abstract class Collectable implements IGameOBJ {
    protected float x, y;
    protected boolean collected = false;
    protected Rectangle bounds;


    /**
     * CONSTRUCTOR
     */
    public Collectable(float x, float y) {
        this.x = x;
        this.y = y;
        this.collected = false;
        this.bounds = new Rectangle();
    }

    public void collected(){
        collected = true;
    }

    public boolean isCollected(){
        return collected;
    }

    @Override
    public abstract void update(float delta);


    public abstract void update(float delta, Player player);

    @Override
    public abstract void render(SpriteBatch batch);

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

}