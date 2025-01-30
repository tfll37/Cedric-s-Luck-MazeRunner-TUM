package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;


/**
 * The type Dice.
 */
public class Dice extends Collectable {

    private AnimationMNGR animationMNGR;

    private float time = 0f;

    private TextureRegion currentFrame;

    private boolean minigameActive = false;

    private Sound diceCollectedSound;

    private boolean gotcolelcted = true;

    /**
     * Instantiates a new Dice.
     *
     * @param x the x
     * @param y the y
     */
    public Dice(float x, float y) {
        super(x, y);
        this.animationMNGR = new AnimationMNGR();
        animationMNGR.loadDiceAnimation();
        diceCollectedSound = Gdx.audio.newSound(Gdx.files.internal("assets//music//94031__loafdv__dice-roll.mp3"));
    }

    @Override
    public void update(float delta, Player player) {
        time += delta;

        if (collected) {
            gotcolelcted = true;
        }

        if (!collected) {
            currentFrame = animationMNGR.getDiceAnimation().getKeyFrame(time, true);
        }

        if (!collected
                && player.getTilePosition(16, 16).x == this.getTilePosition(16, 16).x
                && player.getTilePosition(16, 16).y == this.getTilePosition(16, 16).y) {
            collected = true;
            minigameActive = true; // Activate the minigame
            gotcolelcted = false;

            diceCollectedSound.play();
        }
    }


    @Override
    public void update(float delta) {
        time += delta;
    }


    @Override
    public void render(SpriteBatch batch) {
        if (collected) {
            return;
        }

        float centeredX = x + 4;
        float centeredY = y + 4;

        batch.draw(currentFrame, centeredX, centeredY, 8, 8);
    }

    /**
     * Is minigame active boolean.
     *
     * @return the boolean
     */
    public boolean isMinigameActive() {
        return minigameActive;
    }


    /**
     * Deactivate minigame.
     */
    public void deactivateMinigame() {
        minigameActive = false;
    }


    /**
     * Gets tile position.
     *
     * @param tileWidth  the tile width
     * @param tileHeight the tile height
     * @return the tile position
     */
    public Vector2 getTilePosition(float tileWidth, float tileHeight) {
        return new Vector2((int) (x / tileWidth), (int) (y / tileHeight));
    }

    /**
     * Dispose.
     */
    public void dispose() {
        diceCollectedSound.dispose();
    }

    /**
     * Is gotcolelcted boolean.
     *
     * @return the boolean
     */
    public boolean isGotcolelcted() {
        return gotcolelcted;
    }
}
