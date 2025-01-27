package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;
import de.tum.cit.fop.maze.SCREENS.DiceMinigameListener;
import de.tum.cit.fop.maze.SCREENS.GameScreen;

/**
 * Represents a dice minigame that simulates the rolling of a dice with an
 * animated sequence and displays the result.
 * <p>
 * The minigame can be started and stopped, with a rolling animation that
 * transitions into a static result display. It supports listener callbacks
 * for dice results and includes sound effects for the rolling action.
 */
public class DiceMinigame {

    /**
     * Manages the dice animation during the rolling sequence.
     */
    private AnimationMNGR animationMNGR;

    /**
     * Indicates whether the dice minigame is currently active.
     */
    private boolean active;

    /**
     * Tracks the elapsed time since the minigame started.
     */
    private float time;

    /**
     * The total duration of the dice rolling animation.
     */
    private float activeDuration;

    /**
     * The final dice result (1 to 6), or -1 if no result is currently shown.
     */
    private int diceResult = -1;

    /**
     * Indicates whether the dice result is currently being displayed.
     */
    private boolean showingResult;

    /**
     * Tracks how long the result has been displayed.
     */
    private float resultTimer;

    /**
     * The duration for which the final dice result is shown.
     */
    private float resultDisplayTime = 1.5f;

    /**
     * The sound effect for rolling the dice.
     */
    private Sound diceRollingSound;

    /**
     * Listener for handling the dice result once the rolling is complete.
     */
    private DiceMinigameListener listener;

    /**
     * Creates a new dice minigame with the specified animation manager.
     *
     * @param animationMNGR the {@link AnimationMNGR} responsible for dice animations
     */
    public DiceMinigame(AnimationMNGR animationMNGR) {
        this.animationMNGR = animationMNGR;
        this.active = false;
        this.time = 0f;
        this.activeDuration = 2.0f;

        // Load the dice rolling sound effect
        diceRollingSound = Gdx.audio.newSound(Gdx.files.internal("assets//music//94031__loafdv__dice-roll.mp3"));
    }

    /**
     * Sets the listener to receive notifications about the dice roll result.
     *
     * @param listener the listener to handle dice roll events
     */
    public void setListener(DiceMinigameListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the duration for the dice rolling animation.
     *
     * @param duration the duration in seconds
     */
    public void setActiveDuration(float duration) {
        this.activeDuration = duration;
    }

    /**
     * Starts the dice minigame, initializing the animation and rolling logic.
     * <p>
     * The rolling sound is played, and the dice result is determined once the
     * animation completes.
     */
    public void start() {
        active = true;
        time = 0f;
        diceResult = -1;
        showingResult = false;

        // Simulate rolling the dice and start the sound
        diceRollingSound.play();
    }

    /**
     * Stops the dice minigame, finalizing the rolling animation and showing the result.
     * <p>
     * Calls the listener to notify about the dice result and starts displaying
     * the result for a fixed duration.
     */
    public void stop() {
        active = false;
        diceRollingSound.stop();

        // Determine the dice result (1 to 6)
        diceResult = (int) (Math.random() * 6) + 1;
        System.out.println("Dice roll result: " + diceResult);

        if (listener != null) {
            listener.onDiceRolled(diceResult);
        }

        showingResult = true;
        resultTimer = 0f;
    }

    /**
     * Checks whether the dice minigame is currently active.
     *
     * @return true if the minigame is active; false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Updates the state of the dice minigame.
     * <p>
     * Handles the progression of the rolling animation and transitions to showing
     * the result once the animation duration is exceeded.
     *
     * @param delta the time in seconds since the last update
     */
    public void update(float delta) {
        if (active) {
            time += delta;
            if (time > activeDuration) {
                stop();
            }
        }

        if (showingResult) {
            resultTimer += delta;
            if (resultTimer > resultDisplayTime) {
                showingResult = false;
                diceResult = -1; // Reset the result after display
            }
        }
    }

    /**
     * Renders the dice animation or the final dice result on the screen.
     *
     * @param batch    the {@link SpriteBatch} used for drawing
     * @param cameraX  the x-coordinate of the camera
     * @param cameraY  the y-coordinate of the camera
     */
    public void render(SpriteBatch batch, float cameraX, float cameraY) {
        float offsetX = cameraX - 32;
        float offsetY = cameraY + 32;
        batch.begin();

        if (active) {
            // Draw the rolling animation frame
            TextureRegion rollingFrame = animationMNGR.getDiceAnimation().getKeyFrame(time, true);
            batch.draw(rollingFrame, offsetX, offsetY, 64, 64);
        } else if (showingResult && diceResult != -1) {
            // Draw the final dice face
            TextureRegion finalFace = AnimationMNGR.getDiceFaces()[diceResult - 1];
            batch.draw(finalFace, offsetX, offsetY, 64, 64);
        }

        batch.end();
    }

    /**
     * Retrieves the result of the dice roll.
     *
     * @return the dice result (1 to 6), or -1 if no result is currently shown
     */
    public int getDiceResult() {
        return diceResult;
    }

    /**
     * Disposes of the resources used by the dice minigame, including sounds.
     * <p>
     * This method should be called when the minigame is no longer needed to
     * free up resources.
     */
    public void dispose() {
        diceRollingSound.dispose();
    }
}
