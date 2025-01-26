package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Background {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private final SpriteBatch spriteBatch;
    private MazeLoader mazeLoader;
    private TileEffectMNGR tileEffectMNGR;

    public Background(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        this.tileEffectMNGR = new TileEffectMNGR();
    }

    public void loadTiledMap(String tmxPath, String propertiesPath) {
        // Load the base TMX map
        tiledMap = new TmxMapLoader().load(tmxPath);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, gameCONFIG.UNIT_SCALE, spriteBatch);

        // Load maze configuration
        mazeLoader = new MazeLoader(propertiesPath, tiledMap, tileEffectMNGR);

        // Update maze layout in proper order
        updateMazeLayout();
    }

    private void updateMazeLayout() {
        // 1. First set up base layer with walls and floor
        TiledMapTileLayer baseLayer = getBaseLayer();
        mazeLoader.setTiledMap(tiledMap);
        updateBasicTiles(baseLayer);

        // 2. Create special areas on base layer (will overwrite existing tiles)
        SpecialAreaHNDLR.getInstance(tiledMap, mazeLoader).createSpecialAreas();

        // 3. Orient all walls after everything is placed on base layer
        mazeLoader.orientWallTiles(baseLayer);

        // 4. Finally, handle traps on second layer
        TiledMapTileLayer trapLayer = getSecondLayer();
        if (trapLayer != null) {
            trapLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(),
                    (int) baseLayer.getTileWidth(), (int) baseLayer.getTileHeight());
            tiledMap.getLayers().add(trapLayer);

        }

        updateTrapLayer(trapLayer);


    }

    private void updateBasicTiles(TiledMapTileLayer layer) {
        // Get all overrides except traps
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

//    private boolean isSpecialAreaTile(int tileType) {
//        // Add your special area tile types here
//        return tileType == SPAWN_MARKER || tileType == EXIT_MARKER;
//    }

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

    private void placeTile(TiledMapTileLayer layer, int x, int y, int tileType) {
        int tileId = mazeLoader.getTileId(tileType);
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null) {
            cell = new TiledMapTileLayer.Cell();
            layer.setCell(x, y, cell);
        }
        cell.setTile(tiledMap.getTileSets().getTile(tileId));
    }

    private void updateLayer(TiledMapTileLayer layer) {
        // Get all overrides from the properties file
        var overrides = mazeLoader.getAllOverrides();

        // Apply overrides to the map
        for (var entry : overrides.entrySet()) {
            var pos = entry.getKey();
            int tileType = entry.getValue();

            // Convert tile type to actual tile ID
            int tileId = mazeLoader.getTileId(tileType);

            // Update the cell in the layer
            TiledMapTileLayer.Cell cell = layer.getCell(pos.x, pos.y);
            if (cell == null) {
                cell = new TiledMapTileLayer.Cell();
                layer.setCell(pos.x, pos.y, cell);
            }
            cell.setTile(tiledMap.getTileSets().getTile(tileId));
        }

//        mazeLoader.orientWallTiles(layer);
    }

    public TiledMapTileLayer getBaseLayer() {
        return (TiledMapTileLayer) tiledMap.getLayers().get(0);
    }

    public TiledMapTileLayer getSecondLayer() {
        return (TiledMapTileLayer) tiledMap.getLayers().get(1);
    }


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

    public void renderTiledMap(OrthographicCamera camera) {
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (tiledMapRenderer != null) {
            tiledMapRenderer.dispose();
        }
    }
}