package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Rectangle;
import java.awt.*;

public class FireBall {
    private AnimationMNGR animationMNGR;
    private float time = 0f;
    private TextureRegion currentFrame;
    public float x, y;
    public boolean appear = false;
    public int direction = 0;

    public FireBall(float x, float y) {
        this.animationMNGR = new AnimationMNGR();
        this.x = x;
        this.y = y;
    }
    private final float width = 16f;
    private final float height = 16f;

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, (int)width, (int)height);
    }

    public void update(float delta, Player player, Labyrinth labyrinth, Enemy enemy) {
        if (!appear) return;

        // Move the fireball first
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

        // 1) Check collision with labyrinth walls
        if (labyrinth.isBlocked(x, y)) {
            appear = false;
            return;
        }

        // 2) Check collision with the enemy (only if it's still alive)
        if (enemy.getLifeStatus() && getBounds().overlaps(enemy.getBounds())) {
            // Damage the enemy, then disappear
            enemy.takeDamage(70f);   // or any damage value you want
            appear = false;
        }
    }

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

