package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Manages the background and tile-based map system for the maze game.
 * This class handles loading, rendering, and managing the tiled map including special tiles and effects.
 */
public class Background {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private final SpriteBatch spriteBatch;
    private MazeLoader mazeLoader;
    private TileEffectMNGR tileEffectMNGR;
//    private SpecialAreaMNGR specialAreaHandler;

    /**
     * Constructs a new Background instance.
     * Initializes the sprite batch and tile effect manager.
     *
     * @param spriteBatch The SpriteBatch used for rendering
     */
    public Background(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        this.tileEffectMNGR = new TileEffectMNGR();
    }


    /**
     * Loads a tiled map from specified TMX and properties files.
     * Initializes the map renderer and loads maze configuration.
     *
     * @param tmxPath        The file path to the TMX map file
     * @param propertiesPath The file path to the properties configuration file
     */
    public void loadTiledMap(String tmxPath, String propertiesPath) {
        tiledMap = new TmxMapLoader().load(tmxPath);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, gameCONFIG.UNIT_SCALE, spriteBatch);
        mazeLoader = new MazeLoader(propertiesPath, tiledMap, tileEffectMNGR);
//        specialAreaHandler = SpecialAreaMNGR.getInstance(tiledMap, mazeLoader);
        updateMazeLayout();
    }


    /**
     * Updates the maze layout including base tiles, special areas, wall orientations, and trap layers.
     * This is a private helper method called during map loading.
     */

    private void updateMazeLayout() {
        TiledMapTileLayer baseLayer = getBaseLayer();
        mazeLoader.setTiledMap(tiledMap);
        updateBasicTiles(baseLayer);

        SpecialAreaMNGR.getInstance(tiledMap, mazeLoader).createSpecialAreas();
//        Vector2 spawnPoint = specialAreaHandler.getSpawnPoint();
//        Vector2 exitPoint = specialAreaHandler.getExitPoint();


        mazeLoader.orientWallTiles(baseLayer);

        TiledMapTileLayer trapLayer = getSecondLayer();
        if (trapLayer != null) {
            trapLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(),
                    baseLayer.getTileWidth(), baseLayer.getTileHeight());
            tiledMap.getLayers().add(trapLayer);

        }

        updateTrapLayer(trapLayer);
//        updateTrapLayerWithSpecialAreas(trapLayer, spawnPoint, exitPoint);


    }

    /**
     * Updates the basic floor and wall tiles in the specified layer based on tile overrides.
     *
     * @param layer The TiledMapTileLayer to update
     */
    private void updateBasicTiles(TiledMapTileLayer layer) {
        var overrides = mazeLoader.getAllOverrides();

        for (var entry : overrides.entrySet()) {
            var pos = entry.getKey();
            int tileType = entry.getValue();

            if (tileType == TileEffectMNGR.TRAP_MARKER || tileType == TileEffectMNGR.POWERUP_MARKER) {
                placeTile(layer, pos.x, pos.y, 0);  // Place floor tile
            } else {
                placeTile(layer, pos.x, pos.y, tileType);
            }

        }
    }

    private boolean isInSpecialArea(int x, int y, Vector2 spawnPoint, Vector2 exitPoint) {
        // Convert spawn and exit points to tile coordinates
        int spawnTileX = (int) (spawnPoint.x / 16);
        int spawnTileY = (int) (spawnPoint.y / 16);
        int exitTileX = (int) (exitPoint.x / 16);
        int exitTileY = (int) (exitPoint.y / 16);

        // Check if the position is within 2 tiles of spawn or exit points
        boolean nearSpawn = Math.abs(x - spawnTileX) <= 2 && Math.abs(y - spawnTileY) <= 2;
        boolean nearExit = Math.abs(x - exitTileX) <= 2 && Math.abs(y - exitTileY) <= 2;

        return nearSpawn || nearExit;
    }

    private void updateTrapLayerWithSpecialAreas(TiledMapTileLayer layer, Vector2 spawnPoint, Vector2 exitPoint) {
        var overrides = mazeLoader.getAllOverrides();

        for (var entry : overrides.entrySet()) {
            var pos = entry.getKey();
            int tileType = entry.getValue();

            // Skip if position is in special area
            if (isInSpecialArea(pos.x, pos.y, spawnPoint, exitPoint)) {
                continue;
            }

            if (tileType == TileEffectMNGR.TRAP_MARKER || tileType == TileEffectMNGR.POWERUP_MARKER) {
                TiledMapTileLayer.Cell cell = layer.getCell(pos.x, pos.y);
                if (cell == null) {
                    cell = new TiledMapTileLayer.Cell();
                    layer.setCell(pos.x, pos.y, cell);
                }

                int tileId;
                if (tileType == TileEffectMNGR.TRAP_MARKER) {
                    TileEffectMNGR.TrapType trap = TileEffectMNGR.getRandomTrap();
                    tileId = trap.getTileId();
                    tileEffectMNGR.registerTrapLocation(pos.x, pos.y);
                } else {
                    TileEffectMNGR.PowerUpType powerUp = TileEffectMNGR.getRandomPowerUp();
                    tileId = powerUp.getTileId();
                    tileEffectMNGR.registerPowerUp(pos.x, pos.y);
                }

                cell.setTile(tiledMap.getTileSets().getTile(tileId));
            }
        }
    }


    /**
     * Updates the trap layer with trap and power-up tiles.
     * Randomly assigns trap and power-up types to marked positions.
     *
     * @param layer The TiledMapTileLayer for traps and power-ups
     */
    private void updateTrapLayer(TiledMapTileLayer layer) {
        var overrides = mazeLoader.getAllOverrides();

        for (var entry : overrides.entrySet()) {
            var pos = entry.getKey();
            int tileType = entry.getValue();

            if (tileType == TileEffectMNGR.TRAP_MARKER || tileType == TileEffectMNGR.POWERUP_MARKER) {
                TiledMapTileLayer.Cell cell = layer.getCell(pos.x, pos.y);
                if (cell == null) {
                    cell = new TiledMapTileLayer.Cell();
                    layer.setCell(pos.x, pos.y, cell);
                }

                // Get the correct tile ID based on type
                int tileId;
                if (tileType == TileEffectMNGR.TRAP_MARKER) {
                    TileEffectMNGR.TrapType trap = TileEffectMNGR.getRandomTrap();
                    tileId = trap.getTileId();
                    System.out.println("Placing trap tile with ID " + tileId + " at position " + pos.x + "," + pos.y);
                } else {
                    TileEffectMNGR.PowerUpType powerUp = TileEffectMNGR.getRandomPowerUp();
                    tileId = powerUp.getTileId();
                    System.out.println("Placing powerup tile with ID " + tileId + " at position " + pos.x + "," + pos.y);
                }

                cell.setTile(tiledMap.getTileSets().getTile(tileId));
            }
        }
    }

    /**
     * Places a specific tile type at the given coordinates in the specified layer.
     *
     * @param layer    The TiledMapTileLayer to modify
     * @param x        The x-coordinate for the tile
     * @param y        The y-coordinate for the tile
     * @param tileType The type of tile to place
     */
    private void placeTile(TiledMapTileLayer layer, int x, int y, int tileType) {
        int tileId = mazeLoader.getTileId(tileType);
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null) {
            cell = new TiledMapTileLayer.Cell();
            layer.setCell(x, y, cell);
        }
        cell.setTile(tiledMap.getTileSets().getTile(tileId));
    }

    /**
     * Updates a layer with tiles based on the current override settings.
     *
     * @param layer The TiledMapTileLayer to update
     */
    private void updateLayer(TiledMapTileLayer layer) {
        var overrides = mazeLoader.getAllOverrides();

        for (var entry : overrides.entrySet()) {
            var pos = entry.getKey();
            int tileType = entry.getValue();

            int tileId = mazeLoader.getTileId(tileType);

            TiledMapTileLayer.Cell cell = layer.getCell(pos.x, pos.y);
            if (cell == null) {
                cell = new TiledMapTileLayer.Cell();
                layer.setCell(pos.x, pos.y, cell);
            }
            cell.setTile(tiledMap.getTileSets().getTile(tileId));
        }

    }

    /**
     * Gets the base layer of the tiled map.
     *
     * @return The base TiledMapTileLayer
     */
    public TiledMapTileLayer getBaseLayer() {
        return (TiledMapTileLayer) tiledMap.getLayers().get(0);
    }

    /**
     * Gets the second layer of the tiled map (typically used for traps and power-ups).
     *
     * @return The second TiledMapTileLayer, or null if it doesn't exist
     */
    public TiledMapTileLayer getSecondLayer() {
        return (TiledMapTileLayer) tiledMap.getLayers().get(1);
    }

    /**
     * Centers the camera on the tiled map.
     * Calculates the center position based on map dimensions.
     *
     * @param camera The OrthographicCamera to center
     */
    public void centerTiledMap(OrthographicCamera camera) {
        if (tiledMap != null) {
            MapProperties props = tiledMap.getProperties();
            int mapWidth = props.get("width", Integer.class);
            int mapHeight = props.get("height", Integer.class);
            int tileWidth = props.get("tilewidth", Integer.class);
            int tileHeight = props.get("tileheight", Integer.class);

            float mapPixelWidth = mapWidth * tileWidth;
            float mapPixelHeight = mapHeight * tileHeight;

            camera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
            camera.update();
        }
    }

    /**
     * Renders the tiled map using the provided camera view.
     *
     * @param camera The OrthographicCamera defining the view to render
     */
    public void renderTiledMap(OrthographicCamera camera) {
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }
    }

    /**
     * Gets the current TiledMap instance.
     *
     * @return The current TiledMap
     */
    public TiledMap getTiledMap() {
        return tiledMap;
    }

    /**
     * Disposes of resources used by the Background instance.
     * Should be called when the Background is no longer needed.
     */
    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (tiledMapRenderer != null) {
            tiledMapRenderer.dispose();
        }
    }
}