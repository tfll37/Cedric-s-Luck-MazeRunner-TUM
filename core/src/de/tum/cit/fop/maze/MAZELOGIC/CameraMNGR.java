package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static de.tum.cit.fop.maze.MAZELOGIC.gameCONFIG.MAX_ZOOM;
import static de.tum.cit.fop.maze.MAZELOGIC.gameCONFIG.MIN_ZOOM;

public class CameraMNGR {
    private static final int BASE_MAP_SIZE = 16; // The reference size for 16x16 maps
    private static final float DEFAULT_ZOOM = 0.4f; // The default zoom level for 16x16 maps

    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final TiledMap tiledMap;
    private final float worldWidth;
    private final float worldHeight;
    private final float tileSize;
    private boolean followPlayer;

    public CameraMNGR(OrthographicCamera camera, FitViewport viewport, TiledMap tiledMap) {
        this.camera = camera;
        this.viewport = viewport;
        this.tiledMap = tiledMap;

        MapProperties props = tiledMap.getProperties();
        int mapWidth = props.get("width", Integer.class);
        int mapHeight = props.get("height", Integer.class);
        this.tileSize = props.get("tilewidth", Integer.class); // Assuming square tiles

        this.worldWidth = mapWidth * tileSize;
        this.worldHeight = mapHeight * tileSize;

        // Determine if we should follow player based on map size
        this.followPlayer = Math.max(mapWidth, mapHeight) > BASE_MAP_SIZE;

        // Initialize camera settings
        setupCamera(mapWidth, mapHeight);
    }

    private void setupCamera(int mapWidth, int mapHeight) {
        // Calculate the visible area we want to maintain (based on 16x16 map view)
        float visibleTilesX = BASE_MAP_SIZE;
        float visibleTilesY = BASE_MAP_SIZE;

        // Set zoom to maintain consistent visible area
        float targetZoom = 0.4f;  // DEFAULT_ZOOM * (BASE_MAP_SIZE / (float)Math.max(mapWidth, mapHeight));
//        if (targetZoom < 0.4f) {
//            targetZoom = 0.4f;
//        }
        camera.zoom = targetZoom;

        if (!followPlayer) {
            // For small maps, center the camera
            camera.position.set(worldWidth / 2f, worldHeight / 2f, 0);
        }

        camera.update();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void update(Vector2 playerPosition) {
        if (!followPlayer) {
            return;
        }

        // Calculate the camera boundaries to prevent showing areas outside the map
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        // Calculate the bounds
        float minX = effectiveViewportWidth / 2f;
        float maxX = worldWidth - (effectiveViewportWidth / 2f);
        float minY = effectiveViewportHeight / 2f;
        float maxY = worldHeight - (effectiveViewportHeight / 2f);

        // Update camera position to follow player
        camera.position.x = MathUtils.clamp(playerPosition.x, minX, maxX);
        camera.position.y = MathUtils.clamp(playerPosition.y, minY, maxY);

        camera.update();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);

        if (!followPlayer) {
            // Recenter camera for small maps
            camera.position.set(worldWidth / 2f, worldHeight / 2f, 0);
            camera.update();
        }
    }

    public void adjustForMapSize(int mapSize) {
        float targetZoom = DEFAULT_ZOOM * (BASE_MAP_SIZE / (float)mapSize);
        camera.zoom = MathUtils.clamp(targetZoom, MIN_ZOOM, MAX_ZOOM);
        camera.update();
    }

    public boolean isFollowingPlayer() {
        return followPlayer;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }
}