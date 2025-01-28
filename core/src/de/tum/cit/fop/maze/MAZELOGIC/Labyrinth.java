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

public class Labyrinth extends TiledMap {
    private final Array<TextureRegion> objects;
    private final Background background;
    private final MazeLoader mazeLoader;
    private final TileEffectMNGR tileEffectMNGR;

    public Labyrinth(SpriteBatch spriteBatch, String tmxFile, String propertiesFile, TileEffectMNGR tileEffectMNGR) {
        SpecialAreaMNGR.reset();
        this.objects = new Array<>();
        this.background = new Background(spriteBatch);
        this.background.loadTiledMap(tmxFile, propertiesFile);
        this.mazeLoader = new MazeLoader(propertiesFile, background.getTiledMap(), tileEffectMNGR);
        this.tileEffectMNGR = tileEffectMNGR;

    }

    public void render(OrthographicCamera camera) {
        background.renderTiledMap(camera);
    }

    public TiledMapTileLayer.Cell getTile(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) background.getTiledMap().getLayers().get(0);
        int tileX = (int) (x / layer.getTileWidth());
        int tileY = (int) (y / layer.getTileHeight());
        return layer.getCell(tileX, tileY);
    }

    public boolean isBlocked(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) background.getTiledMap().getLayers().get(0);
        int tileX = (int) (x / layer.getTileWidth());
        int tileY = (int) (y / layer.getTileHeight());
        return !TilePropMNGR.isTileWalkable(layer, tileX, tileY);
    }

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

    public Vector2 getSpawnPoint() {
        return SpecialAreaMNGR.getInstance(background.getTiledMap(), mazeLoader).getSpawnPoint();
    }

    public Vector2 getExitPoint() {
        return SpecialAreaMNGR.getInstance(background.getTiledMap(), mazeLoader).getExitPoint();
    }

    public MazeLoader getMazeLoader() {
        return mazeLoader;
    }

    public TileEffectMNGR getTrapMNGR() {
        return tileEffectMNGR;
    }

    public void dispose() {
        background.dispose();
    }

    public Background getBackground() {
        return background;
    }

}