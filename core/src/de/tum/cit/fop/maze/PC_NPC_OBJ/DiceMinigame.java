package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;

public class DiceMinigame {
    private AnimationMNGR animationMNGR;
    private boolean active;
    private float time;
    private float activeDuration; // how long the "rolling" lasts

    private int diceResult = -1;   // the final dice side
    private boolean showingResult; // true while final face is visible
    private float resultTimer;     // how long we’ve been showing the face
    private float resultDisplayTime = 1.5f; // how long to show final face

    private Sound diceRollingSound;

    public DiceMinigame(AnimationMNGR animationMNGR) {
        this.animationMNGR = animationMNGR;
        this.active = false;
        this.time = 0f;
        this.activeDuration = 2.0f;

        // Load the sound for rolling the dice
        diceRollingSound = Gdx.audio.newSound(Gdx.files.internal("assets//music//94031__loafdv__dice-roll.mp3"));
    }

    public void setActiveDuration(float duration) {
        this.activeDuration = duration;
    }

    public void start() {
        active = true;
        time = 0f;
        diceResult = -1;
        time   = 0f;
        diceResult    =         (int) (Math.random() * 6) + 1;

        showingResult = false;

        // Play dice rolling sound
        diceRollingSound.play();
    }


    public void stop() {
        active = false;

        // Stop the dice rolling sound (optional, depending on duration)
        diceRollingSound.stop();

        // Roll the dice
        diceResult = (int) (Math.random() * 6) + 1;
        // roll the dice
        System.out.println("Dice roll result: " + diceResult);

        // Start showing the final face
        showingResult = true;
        resultTimer = 0f;
    }

    public boolean isActive() {
        return active;
    }

    public void update(float delta) {
        // (1) If still "rolling", increment time
        if (active) {
            time += delta;
            if (time > activeDuration) {
                stop();
            }
        }
        // (2) If showing final face, time it
        if (showingResult) {
            resultTimer += delta;
            if (resultTimer > resultDisplayTime) {
                // Hide the face after 1.5s
                showingResult = false;
                diceResult = -1;
            }
        }
    }

    public void render(SpriteBatch batch, float cameraX, float cameraY) {
        float offsetX = cameraX - 32;
        float offsetY = cameraY + 32;
        batch.begin();

        // If still rolling, show the rolling animation
        if (active) {
            TextureRegion rollingFrame = animationMNGR.getDiceAnimation().getKeyFrame(time, true);
            batch.draw(rollingFrame, offsetX, offsetY, 64, 64);

            // If we have a final face to show
        } else if (showingResult && diceResult != -1) {
            // Retrieve the correct face from diceFaces
            TextureRegion finalFace = AnimationMNGR.getDiceFaces()[diceResult - 1];
            batch.draw(finalFace, offsetX, offsetY, 64, 64);
        }
        batch.end();
    }

    public int getDiceResult() {
        return diceResult;
    }

    public void dispose() {
        diceRollingSound.dispose();
    }
}
