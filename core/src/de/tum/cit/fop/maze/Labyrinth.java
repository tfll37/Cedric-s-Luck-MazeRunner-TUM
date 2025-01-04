package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Random;


public class Labyrinth extends TiledMap {
    private final Array<TextureRegion> objects; // Represents different objects in the labyrinth
    private Background background;
    private final TiledMap tiledMap;

    /**
     * CONSTRUCTOR for Labyrinth.
     *
     * @param spriteBatch The SpriteBatch for rendering.
     */
    public Labyrinth(SpriteBatch spriteBatch) {
        this.background = new Background(spriteBatch);
        this.objects = new Array<>();

        background.loadTiledMap("assets/Gamemap.tmx");
        this.tiledMap = background.getTiledMap();
    }


    /**
     * Add an object to the labyrinth.
     *
     * @param object TextureRegion representing the object.
     */
    public void addObject(TextureRegion object) {
        objects.add(object);
    }


    /**
     * Render the labyrinth, including the background.
     */
    public void render(OrthographicCamera camera) {
        background.renderTiledMap(camera);

        // Render other labyrinth objects, if any
        // e.g., spriteBatch.begin(); spriteBatch.draw(...); spriteBatch.end();

    }

    // Retrieve a specific Tiled tile location
    public TiledMapTileLayer.Cell getTile(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        int tileX = (int) (x / layer.getTileWidth());
        int tileY = (int) (y / layer.getTileHeight());
        return layer.getCell(tileX, tileY);
    }

    public boolean isBlocked(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

        int tileX = (int) (x / layer.getTileWidth());
        int tileY = (int) (y / layer.getTileHeight());

        return !TilePropMngr.isTileWalkable(layer, tileX, tileY);
    }

    public Vector2 getValidSpawnPoint() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        ArrayList<Vector2> openTiles = new ArrayList<>();


        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (TilePropMngr.isTileWalkable(layer, x, y)) {
                    float pixelX = x * layer.getTileWidth();
                    float pixelY = y * layer.getTileHeight();
                    openTiles.add(new Vector2(pixelX, pixelY));
                }
            }
        }
        if (!openTiles.isEmpty()) {
            Random random = new Random();
            return openTiles.get(random.nextInt(openTiles.size()));
        }

        return new Vector2(0, 0);
    }

    /**
     * Update the labyrinth mechanics (e.g., object movement).
     */
    public void update(float delta) {
        // Update mechanics if needed, e.g., object positions
    }

    /**
     * Dispose of resources.
     */
    public void dispose() {
        background.dispose();
    }

    public Background getBackground() {
        return background;
    }
}
