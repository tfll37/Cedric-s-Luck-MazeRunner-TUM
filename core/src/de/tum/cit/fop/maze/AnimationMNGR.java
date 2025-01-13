package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class AnimationMNGR implements Disposable {
    private static final float FRAME_DURATION = 0.1f;

    private static Animation<TextureRegion> hitAnimation1;

    private static Animation<TextureRegion> characterDownAnimation;
    private static Animation<TextureRegion> characterUpAnimation;
    private static Animation<TextureRegion> characterLeftAnimation;
    private static Animation<TextureRegion> characterRightAnimation;
    private static Animation<TextureRegion> characterDownHitAnimation;

    private static Animation<TextureRegion> baldGuyDownAnimation;
    private static Animation<TextureRegion> baldGuyUpAnimation;
    private static Animation<TextureRegion> baldGuyLeftAnimation;
    private static Animation<TextureRegion> baldGuyRightAnimation;

    private static Animation<TextureRegion> diceAnimation;
    private static Animation<TextureRegion> sparkAnimation;
    private final ObjectMap<String, Texture> textures;
    private final SpriteBatch spriteBatch = new SpriteBatch();

    public AnimationMNGR() {

        textures = new ObjectMap<>();

    }

    public void loadAnimations() {
        loadPlayerAnimations();
        loadDiceAnimation();
        loadHitAnimations();

    }

    public void loadPlayerAnimations() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));
        Texture baldWalkSheet = new Texture(Gdx.files.internal("mobs.png"));
        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;
        int hitFrameWidth = 32;
        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkDownFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> hitFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkUpFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkLeftFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkRightFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < animationFrames; col++) {
            walkDownFrames.add(new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            walkUpFrames.add(new TextureRegion(walkSheet, col * frameWidth, frameHeight*2, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            walkRightFrames.add(new TextureRegion(walkSheet, col * frameWidth, frameHeight, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            walkLeftFrames.add(new TextureRegion(walkSheet, col * frameWidth, frameHeight*3, frameWidth, frameHeight));
        }
        for(int col = 0; col < animationFrames; col++) {
            hitFrames.add(new TextureRegion(walkSheet, col * hitFrameWidth, 4*frameHeight, hitFrameWidth, frameHeight));
        }
        characterDownAnimation = new Animation<>(0.1f, walkDownFrames);
        characterUpAnimation = new Animation<>(0.1f, walkUpFrames);
        characterLeftAnimation = new Animation<>(0.1f, walkLeftFrames);
        characterRightAnimation = new Animation<>(0.1f, walkRightFrames);
        characterDownHitAnimation = new Animation<>(0.1f, hitFrames);

        frameWidth = 16;
        frameHeight = 16;
        animationFrames = 3;


        Array<TextureRegion> baldWalkDownFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> baldWalkUpFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> baldWalkLeftFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> baldWalkRightFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            baldWalkDownFrames.add(new TextureRegion(baldWalkSheet, col * frameWidth, 0*frameWidth , frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            baldWalkLeftFrames.add(new TextureRegion(baldWalkSheet, col * frameWidth, 1*frameWidth, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            baldWalkRightFrames.add(new TextureRegion(baldWalkSheet, col * frameWidth, 2*frameWidth, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            baldWalkUpFrames.add(new TextureRegion(baldWalkSheet, col * frameWidth, 3*frameWidth, frameWidth, frameHeight));
        }
        baldGuyDownAnimation = new Animation<>(0.1f, baldWalkDownFrames);
        baldGuyUpAnimation = new Animation<>(0.1f, baldWalkUpFrames);
        baldGuyLeftAnimation = new Animation<>(0.1f, baldWalkLeftFrames);
        baldGuyRightAnimation = new Animation<>(0.1f, baldWalkRightFrames);


    }

    public void loadDiceAnimation() {
        Texture diceSheet = new Texture(Gdx.files.internal("dice.png"));
        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 6;
        Array<TextureRegion> diceFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < animationFrames; col++) {
            diceFrames.add(new TextureRegion(diceSheet, col * frameWidth, 14*frameHeight, frameWidth, frameHeight));
        }
        diceAnimation = new Animation<>(0.1f, diceFrames);
    }

    public void loadHitAnimations(){
        Texture hit1_1 = new Texture(Gdx.files.internal("assets\\hit1\\hit1_1.png"));
        Texture hit1_2 = new Texture(Gdx.files.internal("assets\\hit1\\hit1_2.png"));
        Texture hit1_3 = new Texture(Gdx.files.internal("assets\\hit1\\hit1_3.png"));
        Texture hit1_4 = new Texture(Gdx.files.internal("assets\\hit1\\hit1_4.png"));
        Texture hit1_5 = new Texture(Gdx.files.internal("assets\\hit1\\hit1_5.png"));

        int frameWidth = 16;
        int frameHeight = 16;

        Array<TextureRegion> hitFrames1 = new Array<>(TextureRegion.class);
        hitFrames1.add(new TextureRegion(hit1_1, 0, 0, frameWidth, frameHeight));
        hitFrames1.add(new TextureRegion(hit1_2, 0, 0, frameWidth, frameHeight));
        hitFrames1.add(new TextureRegion(hit1_3, 0, 0, frameWidth, frameHeight));
        hitFrames1.add(new TextureRegion(hit1_4, 0, 0, frameWidth, frameHeight));
        hitFrames1.add(new TextureRegion(hit1_5, 0, 0, frameWidth, frameHeight));

        hitAnimation1 = new Animation<>(0.1f, hitFrames1);
    }

    public Animation<TextureRegion> createAnimation(TextureRegion[] frames, int frameCount) {
        TextureRegion[] animationFrames = new TextureRegion[frameCount];
        System.arraycopy(frames, 0, animationFrames, 0, frameCount);
        return new Animation<>(FRAME_DURATION, animationFrames);
    }

    public static Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }
    public static Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }
    public static Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }
    public static Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }
    public static Animation<TextureRegion> getCharacterDownHitAnimation() {
        return characterDownHitAnimation;
    }
    public static Animation<TextureRegion> getDiceAnimation() {
        return diceAnimation;
    }
    public static Animation<TextureRegion> getBaldGuyDownAnimation() {
        return baldGuyDownAnimation;
    }
    public static Animation<TextureRegion> getBaldGuyUpAnimation() {
        return baldGuyUpAnimation;
    }
    public static Animation<TextureRegion> getBaldGuyLeftAnimation() {
        return baldGuyLeftAnimation;
    }
    public static Animation<TextureRegion> getBaldGuyRightAnimation() {
        return baldGuyRightAnimation;
    }
    public static Animation<TextureRegion> getHitAnimation1() {
        return hitAnimation1;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public void renderPlayer(float delta, float x, float y, Animation<TextureRegion> animation) {
        spriteBatch.begin();
        spriteBatch.draw(animation.getKeyFrame(delta, true), x, y);
        spriteBatch.end();
    }
    public void update(float delta) {
        // Update animations
        spriteBatch.begin();

        spriteBatch.end();

    }
    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();

    }
}