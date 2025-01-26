package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;

public class Dice extends Collectable {
    private AnimationMNGR animationMNGR;
    private float time = 0f;
    private TextureRegion currentFrame;
    private boolean minigameActive = false; // Flag for the minigame state
    private Sound diceCollectedSound;

    private boolean gotcolelcted = true;
    public Dice(float x, float y) {
        super(x, y);
        this.animationMNGR = new AnimationMNGR();

        // Load the sound for collecting the dice
        diceCollectedSound = Gdx.audio.newSound(Gdx.files.internal("assets//music//94031__loafdv__dice-roll.mp3"));
    }

    @Override
    public void update(float delta, Player player) {
        time += delta;
        if(collected) gotcolelcted = true;
        if (!collected) {
            currentFrame = animationMNGR.getDiceAnimation().getKeyFrame(time, true);
        }

        // Check collision with player
        if (!collected && player.getTilePosition(16, 16).x == this.getTilePosition(16, 16).x
                && player.getTilePosition(16, 16).y == this.getTilePosition(16, 16).y) {
            collected = true;
            minigameActive = true; // Activate the minigame when the dice is collected
            gotcolelcted = false;
            minigameActive = true; // Activate the minigame

            // Play the dice collection sound
            diceCollectedSound.play();
        }
    }

    @Override
    public void update(float delta) {
        time += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (collected) return;

        float centeredX = x + 4;
        float centeredY = y + 4;

        batch.draw(currentFrame, centeredX, centeredY, 8, 8);
    }

    public boolean isMinigameActive() {
        return minigameActive;
    }

    public void deactivateMinigame() {
        minigameActive = false;
    }

    public Vector2 getTilePosition(float tileWidth, float tileHeight) {
        return new Vector2((int) (x / tileWidth), (int) (y / tileHeight));
    }

    public void dispose() {
        diceCollectedSound.dispose();
    }
    public boolean isGotcolelcted() {
        return gotcolelcted;
    }
}
