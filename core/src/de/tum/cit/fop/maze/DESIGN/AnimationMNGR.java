package de.tum.cit.fop.maze.DESIGN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;

public class AnimationMNGR implements Disposable {
    private static final float FRAME_DURATION = 0.1f;
    private static final float TILE_ANIMATION_INTERVAL = 0.5f;

    private static Animation<TextureRegion> hitAnimation1;

    private static Animation<TextureRegion> heartAnimation;

    private static Animation<TextureRegion> fireBallAnimationLeft;
    private static Animation<TextureRegion> fireBallAnimationRight;
    private static Animation<TextureRegion> fireBallAnimationUp;
    private static Animation<TextureRegion> fireBallAnimationDown;

    private static Animation<TextureRegion> characterDownAnimation;
    private static Animation<TextureRegion> characterUpAnimation;
    private static Animation<TextureRegion> characterLeftAnimation;
    private static Animation<TextureRegion> characterRightAnimation;


    private static Animation<TextureRegion> characterDownHitAnimation;
    private static Animation<TextureRegion> characterUpHitAnimation;
    private static Animation<TextureRegion> characterLeftHitAnimation;
    private static Animation<TextureRegion> characterRightHitAnimation;

    private static Animation<TextureRegion> baldGuyDownAnimation;
    private static Animation<TextureRegion> baldGuyUpAnimation;
    private static Animation<TextureRegion> baldGuyLeftAnimation;
    private static Animation<TextureRegion> baldGuyRightAnimation;

    private static Animation<TextureRegion> diceAnimation;
    private static TextureRegion[] diceFaces;

    private static Animation<TextureRegion> sparkAnimation;
    private final ObjectMap<String, Texture> textures;


    private final SpriteBatch spriteBatch = new SpriteBatch();

    private final ObjectMap<Integer, Array<TiledMapTile>> animatedTiles;
    private final ObjectMap<Integer, Long> lastUpdateTimes;
    private TiledMapTileLayer tileLayer;
    private TiledMap map;
    private TiledMapTileLayer baseLayer;
    private TiledMapTileLayer secondLayer;

    public AnimationMNGR() {
        this.animatedTiles = new ObjectMap<>();
        this.lastUpdateTimes = new ObjectMap<>();

        textures = new ObjectMap<>();
    }

    public void loadAnimations() {
        loadPlayerAnimations();
        loadDiceAnimation();
        loadHitAnimations();

    }

    public void loadPlayerAnimations() {
        Texture walkSheet = new Texture(Gdx.files.internal("assets/AxeKnight_Blue.png"));
        Texture baldWalkSheet = new Texture(Gdx.files.internal("mobs.png"));

        int frameWidth = 32;
        int frameHeight = 32;
        int animationFrames = 4;
        int hitFrameWidth = 32;
        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkDownFrames = new Array<>(TextureRegion.class);

        Array<TextureRegion> walkUpFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkLeftFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> walkRightFrames = new Array<>(TextureRegion.class);

        Array<TextureRegion> hitDownFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> hitUpFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> hitLeftFrames = new Array<>(TextureRegion.class);
        Array<TextureRegion> hitRightFrames = new Array<>(TextureRegion.class);




        for (int col = 0; col < animationFrames; col++) {
            walkDownFrames.add(new TextureRegion(walkSheet, col * frameWidth, 6*frameHeight, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            walkUpFrames.add(new TextureRegion(walkSheet, col * frameWidth, frameHeight*11, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            walkRightFrames.add(new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight));
        }
        for (int col = 0; col < animationFrames; col++) {
            TextureRegion leftFrame = new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight);
            leftFrame.flip(true, false);
            walkLeftFrames.add(leftFrame);

        }
        for(int col = 0; col < animationFrames; col++) {
            hitDownFrames.add(new TextureRegion(walkSheet, col * hitFrameWidth, 7*frameHeight, hitFrameWidth, frameHeight));
        }
        for(int col = 0; col < animationFrames; col++) {
            hitUpFrames.add(new TextureRegion(walkSheet, col * hitFrameWidth, 12*frameHeight, hitFrameWidth, frameHeight));
        }
        for(int col = 0; col < animationFrames; col++) {
            hitRightFrames.add(new TextureRegion(walkSheet, col * hitFrameWidth, 2*frameHeight, hitFrameWidth, frameHeight));
        }
        for(int col = 0; col < animationFrames; col++) {
            TextureRegion leftFrame = new TextureRegion(walkSheet, col * frameWidth, 2*frameHeight, frameWidth, frameHeight);
            leftFrame.flip(true, false);
            hitLeftFrames.add(leftFrame);
        }
        characterDownAnimation = new Animation<>(0.1f, walkDownFrames);
        characterUpAnimation = new Animation<>(0.1f, walkUpFrames);
        characterLeftAnimation = new Animation<>(0.1f, walkLeftFrames);
        characterRightAnimation = new Animation<>(0.1f, walkRightFrames);

        characterDownHitAnimation = new Animation<>(0.1f, hitDownFrames);
        characterUpHitAnimation = new Animation<>(0.1f, hitUpFrames);
        characterLeftHitAnimation = new Animation<>(0.1f, hitLeftFrames);
        characterRightHitAnimation = new Animation<>(0.1f, hitRightFrames);

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
        Texture heartSheet = new Texture(Gdx.files.internal("objects.png"));
        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 6;
        Array<TextureRegion> diceFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < animationFrames; col++) {
            diceFrames.add(new TextureRegion(diceSheet, col * frameWidth, 14*frameHeight, frameWidth, frameHeight));
        }
        diceAnimation = new Animation<>(0.2f, diceFrames);

        // 6 final faces at row 1
        diceFaces = new TextureRegion[6];
        for (int col = 0; col < 6; col++) {
            diceFaces[col] = new TextureRegion(
                    diceSheet,
                    col * frameWidth,    // X
                    1 * frameHeight,     // Y
                    frameWidth,
                    frameHeight
            );
        }

        Array<TextureRegion> heartFrames = new Array<>(TextureRegion.class);
        animationFrames = 4;
        for (int col = 0; col < animationFrames; col++) {
            heartFrames.add(new TextureRegion(heartSheet, col * frameWidth, 3*frameHeight, frameWidth, frameHeight));
        }
        heartAnimation = new Animation<>(0.1f, heartFrames);
    }

    public void loadHitAnimations(){
        Texture hit1_1 = new Texture(Gdx.files.internal("assets\\hit1\\hit1_1.png"));
        Texture hit1_2 = new Texture(Gdx.files.internal("assets\\hit1\\hit1_2.png"));
        Texture hit1_3 = new Texture(Gdx.files.internal("assets\\hit1\\hit1_3.png"));
        Texture hit1_4 = new Texture(Gdx.files.internal("assets\\hit1\\hit1_4.png"));
        Texture hit1_5 = new Texture(Gdx.files.internal("assets\\hit1\\hit1_5.png"));

        Texture fireBall1_1 = new Texture(Gdx.files.internal("assets\\bolt\\bolt1.png")); ///left
        Texture fireBall1_2 = new Texture(Gdx.files.internal("assets\\bolt\\bolt2.png"));
        Texture fireBall1_3 = new Texture(Gdx.files.internal("assets\\bolt\\bolt3.png"));
        Texture fireBall1_4 = new Texture(Gdx.files.internal("assets\\bolt\\bolt4.png"));

        Texture fireBall2_1 = new Texture(Gdx.files.internal("assets\\bolt\\bolt1_right.png")); ///right
        Texture fireBall2_2 = new Texture(Gdx.files.internal("assets\\bolt\\bolt2_right.png"));
        Texture fireBall2_3 = new Texture(Gdx.files.internal("assets\\bolt\\bolt3_right.png"));
        Texture fireBall2_4 = new Texture(Gdx.files.internal("assets\\bolt\\bolt4_right.png"));

        Texture fireBall3_1 = new Texture(Gdx.files.internal("assets\\bolt\\bolt1_up.png")); ///up
        Texture fireBall3_2 = new Texture(Gdx.files.internal("assets\\bolt\\bolt2_up.png"));
        Texture fireBall3_3 = new Texture(Gdx.files.internal("assets\\bolt\\bolt3_up.png"));
        Texture fireBall3_4 = new Texture(Gdx.files.internal("assets\\bolt\\bolt4_up.png"));

        Texture fireBall4_1 = new Texture(Gdx.files.internal("assets\\bolt\\bolt1_down.png")); ///down
        Texture fireBall4_2 = new Texture(Gdx.files.internal("assets\\bolt\\bolt2_down.png"));
        Texture fireBall4_3 = new Texture(Gdx.files.internal("assets\\bolt\\bolt3_down.png"));
        Texture fireBall4_4 = new Texture(Gdx.files.internal("assets\\bolt\\bolt4_down.png"));


        int frameWidth = 16;
        int frameHeight = 16;

        Array<TextureRegion> hitFrames1 = new Array<>(TextureRegion.class);
        hitFrames1.add(new TextureRegion(hit1_1, 0, 0, frameWidth, frameHeight));
        hitFrames1.add(new TextureRegion(hit1_2, 0, 0, frameWidth, frameHeight));
        hitFrames1.add(new TextureRegion(hit1_3, 0, 0, frameWidth, frameHeight));
        hitFrames1.add(new TextureRegion(hit1_4, 0, 0, frameWidth, frameHeight));
        hitFrames1.add(new TextureRegion(hit1_5, 0, 0, frameWidth, frameHeight));

        frameHeight = 16;
        frameWidth = 32;

        Array<TextureRegion> fireBallFramesLeft = new Array<>(TextureRegion.class);
        fireBallFramesLeft.add(new TextureRegion(fireBall1_1, 0, 0, frameWidth, frameHeight));
        fireBallFramesLeft.add(new TextureRegion(fireBall1_2, 0, 0, frameWidth, frameHeight));
        fireBallFramesLeft.add(new TextureRegion(fireBall1_3, 0, 0, frameWidth, frameHeight));
        fireBallFramesLeft.add(new TextureRegion(fireBall1_4, 0, 0, frameWidth, frameHeight));

        Array<TextureRegion> fireBallFramesRight = new Array<>(TextureRegion.class);
        fireBallFramesRight.add(new TextureRegion(fireBall2_1, 0, 0, frameWidth, frameHeight));
        fireBallFramesRight.add(new TextureRegion(fireBall2_2, 0, 0, frameWidth, frameHeight));
        fireBallFramesRight.add(new TextureRegion(fireBall2_3, 0, 0, frameWidth, frameHeight));
        fireBallFramesRight.add(new TextureRegion(fireBall2_4, 0, 0, frameWidth, frameHeight));

        Array<TextureRegion> fireBallFramesUp = new Array<>(TextureRegion.class);
        fireBallFramesUp.add(new TextureRegion(fireBall3_1, 0, 0, frameWidth, frameHeight));
        fireBallFramesUp.add(new TextureRegion(fireBall3_2, 0, 0, frameWidth, frameHeight));
        fireBallFramesUp.add(new TextureRegion(fireBall3_3, 0, 0, frameWidth, frameHeight));
        fireBallFramesUp.add(new TextureRegion(fireBall3_4, 0, 0, frameWidth, frameHeight));

        Array<TextureRegion> fireBallFramesDown = new Array<>(TextureRegion.class);
        fireBallFramesDown.add(new TextureRegion(fireBall4_1, 0, 0, frameWidth, frameHeight));
        fireBallFramesDown.add(new TextureRegion(fireBall4_2, 0, 0, frameWidth, frameHeight));
        fireBallFramesDown.add(new TextureRegion(fireBall4_3, 0, 0, frameWidth, frameHeight));
        fireBallFramesDown.add(new TextureRegion(fireBall4_4, 0, 0, frameWidth, frameHeight));


        hitAnimation1 = new Animation<>(0.1f, hitFrames1);
        fireBallAnimationLeft = new Animation<>(0.1f, fireBallFramesLeft);
        fireBallAnimationRight = new Animation<>(0.1f, fireBallFramesRight);
        fireBallAnimationUp = new Animation<>(0.1f, fireBallFramesUp);
        fireBallAnimationDown = new Animation<>(0.1f, fireBallFramesDown);
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
    public static Animation<TextureRegion> getCharacterUpHitAnimation() {return characterUpHitAnimation;}
    public static Animation<TextureRegion> getCharacterLeftHitAnimation() {return characterLeftHitAnimation;}
    public static Animation<TextureRegion> getCharacterRightHitAnimation() {return characterRightHitAnimation;}

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
    public static Animation<TextureRegion> getFireBallAnimationLeft() {return fireBallAnimationLeft;}
    public static Animation<TextureRegion> getFireBallAnimationRight() {return fireBallAnimationRight;}
    public static Animation<TextureRegion> getFireBallAnimationUp() {return fireBallAnimationUp;}
    public static Animation<TextureRegion> getFireBallAnimationDown() {return fireBallAnimationDown;}
    public static Animation<TextureRegion> getHeartAnimation() {
        return heartAnimation;
    }
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public void registerAnimatedTile(int baseTileID, int variationTileID){
        Array<TiledMapTile> frames = new Array<>();
        TiledMapTile baseTile = map.getTileSets().getTile(baseTileID);
        TiledMapTile variationTile = map.getTileSets().getTile(variationTileID);

        frames.addAll(baseTile, variationTile);
        animatedTiles.put(baseTileID, frames);
        lastUpdateTimes.put(baseTileID, System.currentTimeMillis());
    }

    public void updateTileAnimations() {
        long currentTime = TimeUtils.millis();
        float deltaTime = TILE_ANIMATION_INTERVAL * 1000;

        updateLayerAnimations(baseLayer, currentTime, deltaTime);
        updateLayerAnimations(secondLayer, currentTime, deltaTime);

    }

    private void updateLayerAnimations(TiledMapTileLayer layer, long currentTime, float deltaTime) {
        ObjectMap<Integer, Boolean> updatedThisFrame = new ObjectMap<>();

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell == null || cell.getTile() == null) continue;

                int tileID = cell.getTile().getId();

                if (animatedTiles.containsKey(tileID) || isVariationTile(tileID)) {
                    int baseTileId = getBaseTileId(tileID);

                    if (updatedThisFrame.containsKey(baseTileId)) continue;

                    Long lastUpdate = lastUpdateTimes.get(baseTileId);
                    if (lastUpdate == null) {
                        lastUpdate = currentTime;
                        lastUpdateTimes.put(baseTileId, lastUpdate);
                    }

                    if (currentTime - lastUpdate > deltaTime) {
                        updateTilesOfType(baseTileId, layer);
                        lastUpdateTimes.put(baseTileId, currentTime);
                        updatedThisFrame.put(baseTileId, true);
                    }
                }
            }
        }
    }

    private boolean isVariationTile(int tileId) {
        for (Array<TiledMapTile> frames : animatedTiles.values()) {
            for (TiledMapTile tile : frames) {
                if (tile.getId() == tileId) return true;
            }
        }
        return false;
    }

    private int getBaseTileId(int tileId) {
        if (animatedTiles.containsKey(tileId)) return tileId;

        for (int baseId : animatedTiles.keys()) {
            Array<TiledMapTile> frames = animatedTiles.get(baseId);
            for (TiledMapTile tile : frames) {
                if (tile.getId() == tileId) return baseId;
            }
        }
        return tileId;
    }

    private void updateTilesOfType(int baseTileId, TiledMapTileLayer layer) {
        Array<TiledMapTile> frames = animatedTiles.get(baseTileId);
        if (frames == null || frames.size < 2) return;

        // Find all tiles of this type and update them
        for (int x = 0; x < tileLayer.getWidth(); x++) {
            for (int y = 0; y < tileLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                    int currentTileId = cell.getTile().getId();
                    if (currentTileId == baseTileId || isVariationOfBaseTile(currentTileId, baseTileId)) {
                        // Find current frame index and calculate next
                        int currentIndex = getCurrentFrameIndex(currentTileId, frames);
                        int nextIndex = (currentIndex + 1) % frames.size;
                        cell.setTile(frames.get(nextIndex));
                    }

            }
        }
    }

    private boolean isVariationOfBaseTile(int tileId, int baseTileId) {
        Array<TiledMapTile> frames = animatedTiles.get(baseTileId);
        for (TiledMapTile frame : frames) {
            if (frame.getId() == tileId) return true;
        }
        return false;
    }

    private int getCurrentFrameIndex(int tileId, Array<TiledMapTile> frames) {
        for (int i = 0; i < frames.size; i++) {
            if (frames.get(i).getId() == tileId) return i;
        }
        return 0;
    }

    public void setMapAndLayer(TiledMapTileLayer tileLayer, TiledMap map) {
        this.map = map;
        this.baseLayer = (TiledMapTileLayer) map.getLayers().get(0);
        this.secondLayer = (TiledMapTileLayer) map.getLayers().get(1);
        this.tileLayer = baseLayer;
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
    public static TextureRegion[] getDiceFaces() {
        return diceFaces;
    }
}