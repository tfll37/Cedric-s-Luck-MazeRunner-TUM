package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Random;

/**
 * The type Labyrinth.
 */
public class Labyrinth extends TiledMap {
    private final Array<TextureRegion> objects;
    private final Background background;
    private final MazeLoader mazeLoader;
    private final TileEffectMNGR tileEffectMNGR;

    /**
     * Instantiates a new Labyrinth.
     *
     * @param spriteBatch    the sprite batch
     * @param tmxFile        the tmx file
     * @param propertiesFile the properties file
     * @param tileEffectMNGR the tile effect mngr
     */
    public Labyrinth(SpriteBatch spriteBatch, String tmxFile, String propertiesFile, TileEffectMNGR tileEffectMNGR) {
        SpecialAreaMNGR.reset();
        this.objects = new Array<>();
        this.background = new Background(spriteBatch);
        this.background.loadTiledMap(tmxFile, propertiesFile);
        this.mazeLoader = new MazeLoader(propertiesFile, background.getTiledMap(), tileEffectMNGR);
        this.tileEffectMNGR = tileEffectMNGR;

    }

    /**
     * Render.
     *
     * @param camera the camera
     */
    public void render(OrthographicCamera camera) {
        background.renderTiledMap(camera);
    }

    /**
     * Gets tile.
     *
     * @param x the x
     * @param y the y
     * @return the tile
     */
    public TiledMapTileLayer.Cell getTile(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) background.getTiledMap().getLayers().get(0);
        int tileX = (int) (x / layer.getTileWidth());
        int tileY = (int) (y / layer.getTileHeight());
        return layer.getCell(tileX, tileY);
    }

    /**
     * Is blocked boolean.
     *
     * @param x the x
     * @param y the y
     * @return the boolean
     */
    public boolean isBlocked(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) background.getTiledMap().getLayers().get(0);
        int tileX = (int) (x / layer.getTileWidth());
        int tileY = (int) (y / layer.getTileHeight());
        return !TilePropMNGR.isTileWalkable(layer, tileX, tileY);
    }

    /**
     * Gets valid spawn point.
     *
     * @return the valid spawn point
     */
    public Vector2 getValidSpawnPoint() {
        TiledMapTileLayer layer = (TiledMapTileLayer) background.getTiledMap().getLayers().get(0);
        ArrayList<Vector2> openTiles = new ArrayList<>();

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (TilePropMNGR.isTileWalkable(layer, x, y)) {
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
     * Gets spawn point.
     *
     * @return the spawn point
     */
    public Vector2 getSpawnPoint() {
        return SpecialAreaMNGR.getInstance(background.getTiledMap(), mazeLoader).getSpawnPoint();
    }

    /**
     * Gets exit point.
     *
     * @return the exit point
     */
    public Vector2 getExitPoint() {
        return SpecialAreaMNGR.getInstance(background.getTiledMap(), mazeLoader).getExitPoint();
    }

    /**
     * Gets maze loader.
     *
     * @return the maze loader
     */
    public MazeLoader getMazeLoader() {
        return mazeLoader;
    }

    /**
     * Gets trap mngr.
     *
     * @return the trap mngr
     */
    public TileEffectMNGR getTrapMNGR() {
        return tileEffectMNGR;
    }

    public void dispose() {
        background.dispose();
    }

    /**
     * Gets background.
     *
     * @return the background
     */
    public Background getBackground() {
        return background;
    }

}