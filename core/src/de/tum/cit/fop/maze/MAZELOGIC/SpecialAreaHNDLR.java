package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class SpecialAreaHNDLR {
    // Bitmask directions
    private static final int NORTH = 1;
    private static final int EAST = 2;
    private static final int SOUTH = 4;
    private static final int WEST = 8;

    private static final int AREA_WIDTH = 3;
    private static final int AREA_HEIGHT = 3;

    private static final class SpawnTiles {
        static final int CENTER = 1302;
        static final int TOP_EDGE = 1282;
        static final int BOTTOM_EDGE = 1323;
        static final int LEFT_EDGE = 1301;
        static final int RIGHT_EDGE = 1303;
        static final int TOP_LEFT_CORNER = 1281;
        static final int TOP_RIGHT_CORNER = 1283;
        static final int BOTTOM_LEFT_CORNER = 1322;
        static final int BOTTOM_RIGHT_CORNER = 1324;

        static final int SPAWN_PORTAL = 1881;
    }

    private static final class ExitTiles {
        static final int CENTER = 1302;
        static final int TOP_EDGE = 1281;
        static final int BOTTOM_EDGE = 1323;
        static final int LEFT_EDGE = 1301;
        static final int RIGHT_EDGE = 1303;
        static final int TOP_LEFT_CORNER = 1280;
        static final int TOP_RIGHT_CORNER = 1282;
        static final int BOTTOM_LEFT_CORNER = 1322;
        static final int BOTTOM_RIGHT_CORNER = 1324;

        static final int LOCKED_EXIT = 1852;
        static final int UNLOCKED_EXIT = 1848;
    }

    private static SpecialAreaHNDLR instance;
    private final TiledMap tiledMap;
    private final MazeLoader mazeLoader;
    private Vector2 spawnPoint;
    private Vector2 exitPoint;
    private boolean areasCreated = false;

    public SpecialAreaHNDLR(TiledMap tiledMap, MazeLoader mazeLoader) {
        this.tiledMap = tiledMap;
        this.mazeLoader = mazeLoader;
    }


    public void createSpecialAreas() {
        if (areasCreated) {
            return; // Prevent duplicate creation
        }
        TiledMapTileLayer baseLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        int width = baseLayer.getWidth();
        int height = baseLayer.getHeight();
        float tileWidth = baseLayer.getTileWidth();
        float tileHeight = baseLayer.getTileHeight();

        // Find spawn point (first walkable tile from top-left)
        findSpawnPoint(baseLayer, width, height, tileWidth, tileHeight);

        // Find exit point (first walkable tile from bottom-right)
        findExitPoint(baseLayer, width, height, tileWidth, tileHeight);

        // Create the actual areas
        createArea(baseLayer, width, height, true);  // Spawn area
        createArea(baseLayer, width, height, false); // Exit area

        areasCreated = true;
    }

    private void createArea(TiledMapTileLayer layer, int mapWidth, int mapHeight, boolean isSpawn) {
        int startX, startY;

        if (isSpawn) {
            startX = mapWidth / 2 - (AREA_WIDTH / 2);
            startY = 1; // Slightly above bottom
            spawnPoint = new Vector2((startX + 1) * layer.getTileWidth(), (startY + 2) * layer.getTileHeight());
        } else {
            Random random = new Random();
            startX = random.nextInt(mapWidth - AREA_WIDTH);
            startY = mapHeight - AREA_HEIGHT - 1;
            exitPoint = new Vector2((startX + 1) * layer.getTileWidth(), startY * layer.getTileHeight());
        }

        for (int x = 0; x < AREA_WIDTH; x++) {
            for (int y = 0; y < AREA_HEIGHT; y++) {
                placeZoneTile(layer, startX + x, startY + y, x, y, isSpawn);
            }
        }
    }

    private void placeZoneTile(TiledMapTileLayer layer, int worldX, int worldY,
                               int relativeX, int relativeY, boolean isSpawn) {
        if (!isWithinBounds(worldX, worldY, layer.getWidth(), layer.getHeight())) {
            return;
        }

        // Get or create cell
        TiledMapTileLayer.Cell cell = getOrCreateCell(layer, worldX, worldY);

        int tileId;

        // Special handling for portal/door placement in center column
        if (relativeX == 1) { // Center column
            if (isSpawn) {
                if (relativeY == 0) tileId = SpawnTiles.SPAWN_PORTAL;      // Bottom - Portal
                else if (relativeY == 1) tileId = SpawnTiles.CENTER;       // Middle - Spawn point
                else tileId = SpawnTiles.TOP_EDGE;                         // Top
            } else {
                if (relativeY == 2) tileId = ExitTiles.LOCKED_EXIT;        // Top - Door
                else if (relativeY == 1) tileId = ExitTiles.CENTER;        // Middle - Exit point
                else tileId = ExitTiles.BOTTOM_EDGE;                       // Bottom
            }
        } else {
            // Handle edge and corner tiles using bitmask
            int mask = calculateBitmask(relativeX, relativeY, AREA_WIDTH, AREA_HEIGHT);
            tileId = getTileIdFromMask(mask, relativeX == 1 && relativeY == 1, isSpawn);
        }

        // Set the tile and overwrite any existing tile
        TiledMapTile tile = tiledMap.getTileSets().getTile(tileId);
        if (tile != null) {
            cell.setTile(tile);
        }
    }

    private int calculateBitmask(int x, int y, int width, int height) {
        int mask = 0;
        if (y < height - 1) mask |= NORTH;
        if (x < width - 1) mask |= EAST;
        if (y > 0) mask |= SOUTH;
        if (x > 0) mask |= WEST;
        return mask;
    }

    private boolean isWithinBounds(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private TiledMapTileLayer.Cell getOrCreateCell(TiledMapTileLayer layer, int x, int y) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null) {
            cell = new TiledMapTileLayer.Cell();
            layer.setCell(x, y, cell);
        }
        return cell;
    }

    public static SpecialAreaHNDLR getInstance(TiledMap tiledMap, MazeLoader mazeLoader) {
        if (instance == null) {
            instance = new SpecialAreaHNDLR(tiledMap, mazeLoader);
        }
        return instance;
    }


    private void findSpawnPoint(TiledMapTileLayer layer, int width, int height, float tileWidth,
                                float tileHeight) {
        // Start from top-left corner
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                if (TilePropMNGR.isTileWalkable(layer, x, y)) {
                    spawnPoint = new Vector2(x * tileWidth, y * tileHeight);
                    return;
                }
            }
        }
        // Fallback to default if no suitable location found
        spawnPoint = new Vector2(tileWidth, tileHeight);
    }

    private void findExitPoint(TiledMapTileLayer layer, int width, int height, float tileWidth, float tileHeight) {
        // Start from bottom-right corner
        for (int y = 0; y < height; y++) {
            for (int x = width - 1; x >= 0; x--) {
                if (TilePropMNGR.isTileWalkable(layer, x, y)) {
                    exitPoint = new Vector2(x * tileWidth, y * tileHeight);
                    return;
                }
            }
        }
        // Fallback to default if no suitable location found
        exitPoint = new Vector2((width - 2) * tileWidth, (height - 2) * tileHeight);
    }

    public int getTileIdFromMask(int mask, boolean isCenter, boolean isSpawn) {
        if (isCenter) {
            return isSpawn ? SpawnTiles.CENTER : ExitTiles.CENTER;
        }

        switch (mask) {
            case NORTH | EAST | SOUTH | WEST:
                return isSpawn ? SpawnTiles.CENTER : ExitTiles.CENTER;
            case NORTH | EAST:
                return isSpawn ? SpawnTiles.BOTTOM_LEFT_CORNER : ExitTiles.BOTTOM_LEFT_CORNER;
            case NORTH | WEST:
                return isSpawn ? SpawnTiles.BOTTOM_RIGHT_CORNER : ExitTiles.BOTTOM_RIGHT_CORNER;
            case SOUTH | EAST:
                return isSpawn ? SpawnTiles.TOP_LEFT_CORNER : ExitTiles.TOP_LEFT_CORNER;
            case SOUTH | WEST:
                return isSpawn ? SpawnTiles.TOP_RIGHT_CORNER : ExitTiles.TOP_RIGHT_CORNER;
            case NORTH | SOUTH:
                return isSpawn ? SpawnTiles.RIGHT_EDGE : ExitTiles.RIGHT_EDGE;
            case EAST | WEST:
                return isSpawn ? SpawnTiles.BOTTOM_EDGE : ExitTiles.BOTTOM_EDGE;
            case NORTH:
                return isSpawn ? SpawnTiles.BOTTOM_EDGE : ExitTiles.BOTTOM_EDGE;
            case SOUTH:
                return isSpawn ? SpawnTiles.TOP_EDGE : ExitTiles.TOP_EDGE;
            case EAST:
                return isSpawn ? SpawnTiles.LEFT_EDGE : ExitTiles.LEFT_EDGE;
            case WEST:
                return isSpawn ? SpawnTiles.RIGHT_EDGE : ExitTiles.RIGHT_EDGE;

            default:
                return isSpawn ? SpawnTiles.CENTER : ExitTiles.CENTER;
        }
    }

    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }

    public Vector2 getExitPoint() {
        return exitPoint;
    }

    // Reset the singleton instance when loading a new level
    public static void reset() {
        instance = null;
    }
}