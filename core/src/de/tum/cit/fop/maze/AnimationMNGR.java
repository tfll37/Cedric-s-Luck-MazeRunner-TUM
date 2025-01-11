package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class AnimationMNGR implements Disposable {
    private static final float FRAME_DURATION = 0.1f;
    private final ObjectMap<String, Animation<TextureRegion>> animations;
    private final ObjectMap<String, Texture> textures;

    public AnimationMNGR() {
        animations = new ObjectMap<>();
        textures = new ObjectMap<>();
        loadAnimations();
    }

    private void loadAnimations() {
        loadPlayerAnimations();
        loadCollectibleAnimations();
    }

    private void loadPlayerAnimations() {
        Texture playerSheet = new Texture(Gdx.files.internal("character.png"));
        textures.put("player", playerSheet);

        int frameWidth = 16;
        int frameHeight = 32;

        // Create sprite sheet regions
        TextureRegion[][] tmp = TextureRegion.split(playerSheet, frameWidth, frameHeight);

        // Walking animations for each direction
        animations.put("player_down", createAnimation(tmp[0], 4));
        animations.put("player_left", createAnimation(tmp[1], 4));
        animations.put("player_right", createAnimation(tmp[2], 4));
        animations.put("player_up", createAnimation(tmp[3], 4));

        // Attack animations
        animations.put("player_attack_down", createAnimation(tmp[4], 4));
        animations.put("player_attack_left", createAnimation(tmp[5], 4));
        animations.put("player_attack_right", createAnimation(tmp[6], 4));
        animations.put("player_attack_up", createAnimation(tmp[7], 4));
    }

    private void loadCollectibleAnimations() {
        // Load dice animation
        Texture diceSheet = new Texture(Gdx.files.internal("dice.png"));
        textures.put("dice", diceSheet);

        TextureRegion[][] diceFrames = TextureRegion.split(diceSheet, 16, 16);
        animations.put("dice_spin", createAnimation(diceFrames[0], 6));

        // Add other collectible animations here as needed
    }

    private Animation<TextureRegion> createAnimation(TextureRegion[] frames, int frameCount) {
        TextureRegion[] animationFrames = new TextureRegion[frameCount];
        System.arraycopy(frames, 0, animationFrames, 0, frameCount);
        return new Animation<>(FRAME_DURATION, animationFrames);
    }

    public Animation<TextureRegion> getAnimation(String key) {
        Animation<TextureRegion> animation = animations.get(key);
        if (animation == null) {
            throw new IllegalArgumentException("Animation not found: " + key);
        }
        return animation;
    }

    public TextureRegion getKeyFrame(String animationKey, float stateTime, boolean looping) {
        return getAnimation(animationKey).getKeyFrame(stateTime, looping);
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();
        animations.clear();
    }
}