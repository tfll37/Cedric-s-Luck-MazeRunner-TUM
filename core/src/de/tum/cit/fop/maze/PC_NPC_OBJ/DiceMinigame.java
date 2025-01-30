package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;
import de.tum.cit.fop.maze.SCREENS.DiceMinigameListener;
import de.tum.cit.fop.maze.SCREENS.GameScreen;


/**
 * The type Dice minigame.
 */
public class DiceMinigame {


    private AnimationMNGR animationMNGR;


    private boolean active;


    private float time;


    private float activeDuration;


    private int diceResult = -1;


    private boolean showingResult;


    private float resultTimer;


    private float resultDisplayTime = 1.5f;


    private Sound diceRollingSound;


    private DiceMinigameListener listener;

    /**
     * Instantiates a new Dice minigame.
     *
     * @param animationMNGR the animation mngr
     */
    public DiceMinigame(AnimationMNGR animationMNGR) {
        this.animationMNGR = animationMNGR;
        this.active = false;
        this.time = 0f;
        this.activeDuration = 2.0f;

        diceRollingSound = Gdx.audio.newSound(Gdx.files.internal("assets//music//94031__loafdv__dice-roll.mp3"));
    }

    /**
     * Sets listener.
     *
     * @param listener the listener
     */
    public void setListener(DiceMinigameListener listener) {
        this.listener = listener;
    }

    /**
     * Sets active duration.
     *
     * @param duration the duration
     */
    public void setActiveDuration(float duration) {
        this.activeDuration = duration;
    }


    /**
     * Start.
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
     * Stop.
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
     * Is active boolean.
     *
     * @return the boolean
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Update.
     *
     * @param delta the delta
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
                diceResult = -1;
            }
        }
    }


    /**
     * Render.
     *
     * @param batch   the batch
     * @param cameraX the camera x
     * @param cameraY the camera y
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
     * Gets dice result.
     *
     * @return the dice result
     */
    public int getDiceResult() {
        return diceResult;
    }


    /**
     * Dispose.
     */
    public void dispose() {
        diceRollingSound.dispose();
    }
}
