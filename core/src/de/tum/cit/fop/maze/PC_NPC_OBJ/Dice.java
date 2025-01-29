package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;

/**
 * Represents a dice collectable object in the game that triggers a minigame
 * upon being collected by the player.
 * <p>
 * A dice object has an associated animation, a sound effect that plays once
 * it is collected, and can toggle the minigame state.
 */
public class Dice extends Collectable {

    /**
     * Manages the dice animation.
     */
    private AnimationMNGR animationMNGR;

    /**
     * Accumulates the total elapsed time since the dice was created, used for
     * advancing the animation.
     */
    private float time = 0f;

    /**
     * The current frame of the dice animation.
     */
    private TextureRegion currentFrame;

    /**
     * Indicates if the minigame has been activated.
     */
    private boolean minigameActive = false;

    /**
     * Sound effect that plays when the dice is collected.
     */
    private Sound diceCollectedSound;

    /**
     * Indicates if the dice has just been collected.
     * <p>
     * This may be used to trigger certain one-time effects or logic at the
     * moment of collection.
     */
    private boolean gotcolelcted = true;

    /**
     * Creates a new dice collectable at the specified coordinates.
     *
     * @param x the x-coordinate of this dice object
     * @param y the y-coordinate of this dice object
     */
    public Dice(float x, float y) {
        super(x, y);
        this.animationMNGR = new AnimationMNGR();
        animationMNGR.loadDiceAnimation();
        // Load the dice collection sound effect
        diceCollectedSound = Gdx.audio.newSound(Gdx.files.internal("assets//music//94031__loafdv__dice-roll.mp3"));
    }

    /**
     * Updates the dice’s state, checking for collection by the player and
     * updating the current animation frame.
     * <p>
     * If the dice has not yet been collected, this method also checks whether
     * the player's tile position matches this dice’s tile position. If they match,
     * the dice is considered collected, triggering a minigame and playing a sound.
     *
     * @param delta  time (in seconds) since the last frame
     * @param player the player interacting with the game
     */
    @Override
    public void update(float delta, Player player) {
        time += delta;

        // Update "gotcolelcted" if the dice was marked as collected in other logic
        if (collected) {
            gotcolelcted = true;
        }

        // Advance the dice animation if it isn't collected
        if (!collected) {
            currentFrame = animationMNGR.getDiceAnimation().getKeyFrame(time, true);
        }

        // Check if the player is colliding with the dice tile
        if (!collected
                && player.getTilePosition(16, 16).x == this.getTilePosition(16, 16).x
                && player.getTilePosition(16, 16).y == this.getTilePosition(16, 16).y) {
            collected = true;
            minigameActive = true; // Activate the minigame
            gotcolelcted = false;

            // Play the dice collection sound
            diceCollectedSound.play();
        }
    }

    /**
     * Updates the dice’s state without reference to a player.
     * <p>
     * Generally used for non-player-specific updates (e.g., advancing animations).
     *
     * @param delta time (in seconds) since the last frame
     */
    @Override
    public void update(float delta) {
        time += delta;
    }

    /**
     * Renders the dice on the screen.
     * <p>
     * Draws the current animation frame to the sprite batch at the dice's coordinates.
     * If the dice has already been collected, no rendering is performed.
     *
     * @param batch the {@link SpriteBatch} used for drawing
     */
    @Override
    public void render(SpriteBatch batch) {
        if (collected) {
            return;
        }

        // Slight offset for positioning
        float centeredX = x + 4;
        float centeredY = y + 4;

        batch.draw(currentFrame, centeredX, centeredY, 8, 8);
    }

    /**
     * Checks whether the minigame is currently active.
     *
     * @return true if the minigame is active; false otherwise
     */
    public boolean isMinigameActive() {
        return minigameActive;
    }

    /**
     * Deactivates the minigame.
     * <p>
     * This method can be called after the player finishes or exits the minigame.
     */
    public void deactivateMinigame() {
        minigameActive = false;
    }

    /**
     * Calculates the tile position of this dice object based on the specified
     * tile width and height.
     * <p>
     * This is commonly used to check collision with other objects by tile coordinates.
     *
     * @param tileWidth  the width of one tile in the game world
     * @param tileHeight the height of one tile in the game world
     * @return a {@link Vector2} representing the tile coordinates of this dice object
     */
    public Vector2 getTilePosition(float tileWidth, float tileHeight) {
        return new Vector2((int) (x / tileWidth), (int) (y / tileHeight));
    }

    /**
     * Disposes resources used by this dice object (e.g., the dice collection sound).
     * <p>
     * Call this method when the dice is no longer needed to free up resources.
     */
    public void dispose() {
        diceCollectedSound.dispose();
    }

    /**
     * Checks whether this dice has just been collected.
     * <p>
     * This flag can be used to determine if any one-time logic
     * should trigger at the moment of collection.
     *
     * @return true if the dice has just been collected; false otherwise
     */
    public boolean isGotcolelcted() {
        return gotcolelcted;
    }
}
