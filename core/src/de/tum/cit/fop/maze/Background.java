package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Background {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private final SpriteBatch spriteBatch;

    /**
     * CONSTRUCTOR
     *
     * @param spriteBatch The SpriteBatch for rendering.
     */
    public Background(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
    }

    // Load a tiled map as the background
    public void loadTiledMap(String mapPath) {;
        tiledMap = new TmxMapLoader().load(mapPath);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, gameCONFIG.UNIT_SCALE, spriteBatch);

        adjustTileIds();
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    // Center the tiled map in the camera's view
    public void centerTiledMap(OrthographicCamera camera) {
        if (tiledMap != null) {
            MapProperties mapProperties = tiledMap.getProperties();

            int tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
            int tilePixelHeight = mapProperties.get("tileheight", Integer.class);
            int mapWidth = mapProperties.get("width", Integer.class);
            int mapHeight = mapProperties.get("height", Integer.class);

            // Calculate map dimensions in pixels
            float mapPixelWidth = mapWidth * tilePixelWidth;
            float mapPixelHeight = mapHeight * tilePixelHeight;

            // Center the camera on the map
            camera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
            camera.update();
        }
    }
    // Render method for tiled map
    public void renderTiledMap(OrthographicCamera camera) {
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }
    }

//                                      TEXTURE
//    /**
//     * Sets the background texture.
//     *
//     * @param texture The new background texture.
//     */
//    public void setBackground(Texture texture) {
//        // Dispose of the previous texture if necessary
//        if (currentBackgroundTexture != null && currentBackgroundTexture != texture) {
//            currentBackgroundTexture.dispose();
//        }
//        currentBackgroundTexture = texture;
//    }

//    /**
//     * Render the background texture centered on the screen.
//     *
//     * @param screenWidth The width of the screen.
//     * @param screenHeight The height of the screen.
//     */
//    public void renderTexture(int screenWidth, int screenHeight) {
//        if (currentBackgroundTexture != null) {
//            spriteBatch.begin();
//
//            // Calculate center position
//            int textureWidth = currentBackgroundTexture.getWidth();
//            int textureHeight = currentBackgroundTexture.getHeight();
//
//            float x = (screenWidth - textureWidth) / 2f;
//            float y = (screenHeight - textureHeight) / 2f;
//
//            // Draw the background texture
//            spriteBatch.draw(currentBackgroundTexture, x, y, textureWidth, textureHeight);
//            spriteBatch.end();
//        }
//    }

    private void adjustTileIds() {
        TMapTileIDAdjuster adjuster = new TMapTileIDAdjuster(tiledMap);
        adjuster.adjustTileId();
    }



    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
    }
}
