package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;
import de.tum.cit.fop.maze.MAZELOGIC.Labyrinth;

/**
 * Represents a fireball that can be shot by the player.
 * The fireball moves in a specified direction and can damage enemies.
 */
public class FireBall {
    private AnimationMNGR animationMNGR;
    private float time = 0f;
    private TextureRegion currentFrame;
    public float x, y;
    public boolean appear = false;
    public int direction = 0;
    private final float width = 16f;
    private final float height = 16f;

    /**
     * Creates a new FireBall at the specified position.
     *
     * @param x the x-coordinate of the fireball's initial position
     * @param y the y-coordinate of the fireball's initial position
     */
    public FireBall(float x, float y) {
        this.animationMNGR = new AnimationMNGR();
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the bounding rectangle of the fireball.
     *
     * @return the bounding rectangle
     */
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, (int)width, (int)height);
    }

    /**
     * Updates the fireball's state, including movement and collision detection.
     *
     * @param delta     the time in seconds since the last update
     * @param player    the player object
     * @param labyrinth the labyrinth object
     * @param enemies   the array of enemies
     */
    public void update(float delta, Player player, Labyrinth labyrinth, Array<Enemy> enemies) {
        if (!appear) return;

        // Move the fireball
        time += delta;
        float speed = 200 * delta; // Adjust speed as needed

        switch (direction) {
            case 0: // Up
                y += speed;
                currentFrame = animationMNGR.getFireBallAnimationUp().getKeyFrame(time, true);
                break;
            case 1: // Right
                x += speed;
                currentFrame = animationMNGR.getFireBallAnimationRight().getKeyFrame(time, true);
                break;
            case 2: // Down
                y -= speed;
                currentFrame = animationMNGR.getFireBallAnimationDown().getKeyFrame(time, true);
                break;
            case 3: // Left
                x -= speed;
                currentFrame = animationMNGR.getFireBallAnimationLeft().getKeyFrame(time, true);
                break;
        }

        // Check collision with labyrinth walls
        if (labyrinth.isBlocked(x, y)) {
            appear = false;
            return;
        }

        // Check collision with enemies
        for (int i = 0; i < enemies.size; i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.getLifeStatus() && getBounds().overlaps(enemy.getBounds())) {
                // Damage the enemy, then disappear
                enemy.takeDamage(70f); // or any damage value you want
                appear = false;
            }
        }
    }

    /**
     * Renders the fireball on the screen.
     *
     * @param batch the SpriteBatch used for drawing
     */
    public void render(SpriteBatch batch) {
        if (!appear) return;

        float rotation = 0;
        switch (direction) {
            case 0: rotation = 0; break;   // Up
            case 1: rotation = 90; break;  // Right
            case 2: rotation = 180; break; // Down
            case 3: rotation = 270; break; // Left
        }

        batch.draw(
                currentFrame,
                x, y,
                width / 2, height / 2,  // originX, originY (center)
                width, height,          // drawn width & height
                1, 1,                   // scaleX, scaleY
                rotation                // rotation
        );
    }
}