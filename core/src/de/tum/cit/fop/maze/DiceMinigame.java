package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DiceMinigame {
    private AnimationMNGR animationMNGR;
    private boolean active;
    private float time;
    private float activeDuration; // How long the minigame should stay active

    public DiceMinigame(AnimationMNGR animationMNGR) {
        this.animationMNGR = animationMNGR;
        this.active = false;
        this.time = 0f;
        this.activeDuration = 2.0f; // Default to 2 seconds
    }

    public void setActiveDuration(float duration) {
        this.activeDuration = duration;
    }

    public void start() {
        active = true;
        time = 0f;
    }

    public void stop() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void update(float delta) {
        if (active) {
            time += delta;
            if (time > activeDuration) {
                stop(); // Stop the minigame after the specified duration
            }
        }
    }

    public void render(SpriteBatch batch, float cameraX, float cameraY) {
        if (active) {
            float minigameX = cameraX - 32; // Adjust X position
            float minigameY = cameraY + 32; // Adjust Y position

            batch.begin();
            TextureRegion diceFrame = animationMNGR.getDiceAnimation().getKeyFrame(time, true);
            batch.draw(diceFrame, minigameX, minigameY, 64, 64); // Adjust size as needed
            batch.end();
        }
    }
}
