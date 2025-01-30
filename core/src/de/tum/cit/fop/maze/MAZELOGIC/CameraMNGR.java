package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static de.tum.cit.fop.maze.MAZELOGIC.gameCONFIG.*;

/**
 * The type Camera mngr.
 */
public class CameraMNGR {

    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final float worldWidth;
    private final float worldHeight;
    private final float tileSize;
    private float targetZoom;

    private float shakeTime;
    private float shakeDuration;
    private float shakeIntensity;
    private final Vector3 originalPosition;
    private boolean isShaking;

    /**
     * Instantiates a new Camera mngr.
     *
     * @param camera   the camera
     * @param viewport the viewport
     * @param tiledMap the tiled map
     */
    public CameraMNGR(OrthographicCamera camera, FitViewport viewport, TiledMap tiledMap) {
        this.camera = camera;
        this.viewport = viewport;
        this.originalPosition = new Vector3();

        MapProperties props = tiledMap.getProperties();
        int mapWidth = props.get("width", Integer.class);
        int mapHeight = props.get("height", Integer.class);
        this.tileSize = props.get("tilewidth", Integer.class);

        this.worldWidth = mapWidth * tileSize;
        this.worldHeight = mapHeight * tileSize;


        this.targetZoom = DEFAULT_ZOOM;
        camera.zoom = DEFAULT_ZOOM;

        setupCamera();
    }

    private void setupCamera() {
        camera.position.set(worldWidth / 2f, worldHeight / 2f, 0);

        camera.update();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Update.
     *
     * @param playerPosition the player position
     */
    public void update(Vector2 playerPosition) {
        if (camera.zoom != targetZoom) {
            camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, CAMERA_LERP_SPEED);
        }

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        float minX = effectiveViewportWidth / 2f;
        float maxX = worldWidth - (effectiveViewportWidth / 2f);
        float minY = effectiveViewportHeight / 2f;
        float maxY = worldHeight - (effectiveViewportHeight / 2f);

        float targetX = MathUtils.clamp(playerPosition.x, minX, maxX);
        float targetY = MathUtils.clamp(playerPosition.y, minY, maxY);

        camera.position.x = MathUtils.lerp(camera.position.x, targetX, CAMERA_LERP_SPEED);
        camera.position.y = MathUtils.lerp(camera.position.y, targetY, CAMERA_LERP_SPEED);

        originalPosition.set(camera.position);

        updateScreenShake(Gdx.graphics.getDeltaTime());
        camera.update();
    }

    /**
     * Handle scroll.
     *
     * @param amount the amount
     */
    public void handleScroll(float amount) {
        float zoomDelta = -amount * gameCONFIG.ZOOM_SPEED;
        targetZoom = MathUtils.clamp(targetZoom + zoomDelta, gameCONFIG.MIN_ZOOM, gameCONFIG.MAX_ZOOM);
    }

    /**
     * Start shake.
     */
    public void startShake() {
        startShake(SHAKE_DEFAULT_DURATION, SHAKE_DEFAULT_INTENSITY);
    }

    /**
     * Start light shake.
     */
    public void startLightShake() {
        startShake(LIGHT_SHAKE_DURATION, LIGHT_SHAKE_INTENSITY);
    }

    /**
     * Start heavy shake.
     */
    public void startHeavyShake() {
        startShake(HEAVY_SHAKE_DURATION, HEAVY_SHAKE_INTENSITY);
    }

    /**
     * Start shake.
     *
     * @param duration  the duration
     * @param intensity the intensity
     */
    public void startShake(float duration, float intensity) {
        this.shakeTime = 0;
        this.shakeDuration = duration;
        this.shakeIntensity = intensity;
        this.isShaking = true;
        this.originalPosition.set(camera.position);
    }

    private void updateScreenShake(float deltaTime) {
        if (!isShaking) return;

        shakeTime += deltaTime;

        if (shakeTime < shakeDuration) {
            float currentIntensity = shakeIntensity * (1 - (shakeTime / shakeDuration));
            float offsetX = MathUtils.random(-1f, 1f) * currentIntensity;
            float offsetY = MathUtils.random(-1f, 1f) * currentIntensity;

            camera.position.x = originalPosition.x + offsetX;
            camera.position.y = originalPosition.y + offsetY;
        } else {
            isShaking = false;
            camera.position.x = originalPosition.x;
            camera.position.y = originalPosition.y;
        }
    }


    /**
     * Resize.
     *
     * @param width  the width
     * @param height the height
     */
    public void resize(int width, int height) {
        viewport.update(width, height);

    }

    /**
     * Is shaking boolean.
     *
     * @return the boolean
     */
    public boolean isShaking() {
        return isShaking;
    }

    /**
     * Gets world width.
     *
     * @return the world width
     */
    public float getWorldWidth() {
        return worldWidth;
    }

    /**
     * Gets world height.
     *
     * @return the world height
     */
    public float getWorldHeight() {
        return worldHeight;
    }
}