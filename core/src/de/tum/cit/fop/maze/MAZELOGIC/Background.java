package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

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
        tiledMap = new TmxMapLoader().load(tmxPath);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, gameCONFIG.UNIT_SCALE, spriteBatch);
        mazeLoader = new MazeLoader(propertiesPath, tiledMap, tileEffectMNGR);
        updateMazeLayout();
    }

    private void updateMazeLayout() {
        TiledMapTileLayer baseLayer = getBaseLayer();
        mazeLoader.setTiledMap(tiledMap);
        updateBasicTiles(baseLayer);

        SpecialAreaMNGR.getInstance(tiledMap, mazeLoader).createSpecialAreas();


        mazeLoader.orientWallTiles(baseLayer);

        TiledMapTileLayer trapLayer = getSecondLayer();
        if (trapLayer != null) {
            trapLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(),
                    baseLayer.getTileWidth(), baseLayer.getTileHeight());
            tiledMap.getLayers().add(trapLayer);

        }

        updateTrapLayer(trapLayer);


    }


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
        int spawnTileX = (int) (spawnPoint.x / 16);
        int spawnTileY = (int) (spawnPoint.y / 16);
        int exitTileX = (int) (exitPoint.x / 16);
        int exitTileY = (int) (exitPoint.y / 16);

        boolean nearSpawn = Math.abs(x - spawnTileX) <= 2 && Math.abs(y - spawnTileY) <= 2;
        boolean nearExit = Math.abs(x - exitTileX) <= 2 && Math.abs(y - exitTileY) <= 2;

        return nearSpawn || nearExit;
    }

    private void updateTrapLayerWithSpecialAreas(TiledMapTileLayer layer, Vector2 spawnPoint, Vector2 exitPoint) {
        var overrides = mazeLoader.getAllOverrides();

        for (var entry : overrides.entrySet()) {
            var pos = entry.getKey();
            int tileType = entry.getValue();

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