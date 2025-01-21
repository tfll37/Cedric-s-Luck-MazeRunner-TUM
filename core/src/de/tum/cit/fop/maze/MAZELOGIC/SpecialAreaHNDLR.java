package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.maps.tiled.TiledMap;
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

    private final TiledMap tiledMap;
    private Vector2 spawnPoint;
    private Vector2 exitPoint;
    private final MazeLoader mazeLoader;

    public SpecialAreaHNDLR(TiledMap tiledMap, MazeLoader mazeLoader) {
        this.tiledMap = tiledMap;
        this.mazeLoader = mazeLoader;
    }

    private int getTileIdFromMask(int mask, boolean isCenter, boolean isSpawn) {
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

    public void createSpecialAreas(TiledMapTileLayer targetLayer) {
        int mapWidth = targetLayer.getWidth();
        int mapHeight = targetLayer.getHeight();

        createArea(targetLayer, mapWidth, mapHeight, true);  // Spawn area
        createArea(targetLayer, mapWidth, mapHeight, false); // Exit area

        mazeLoader.orientWallTiles(targetLayer);
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
                int worldX = startX + x;
                int worldY = startY + y;

                if (isWithinBounds(worldX, worldY, mapWidth, mapHeight)) {
                    TiledMapTileLayer.Cell cell = getOrCreateCell(layer, worldX, worldY);
                    int tileId;

                    // Special handling for portal/door placement
                    if (x == 1) { // Center column
                        if (isSpawn) {
                            if (y == 0) tileId = SpawnTiles.SPAWN_PORTAL;      // Bottom - Portal
                            else if (y == 1) tileId = SpawnTiles.CENTER;       // Middle - Spawn point
                            else tileId = SpawnTiles.TOP_EDGE;                 // Top
                        } else {
                            if (y == 2) tileId = ExitTiles.LOCKED_EXIT;         // Top - Door
                            else if (y == 1) tileId = ExitTiles.CENTER;       // Middle - Exit point
                            else tileId = ExitTiles.BOTTOM_EDGE;              // Bottom
                        }
                    } else {
                        // Handle edge and corner tiles as before
                        int mask = calculateBitmask(x, y, AREA_WIDTH, AREA_HEIGHT);
                        tileId = getTileIdFromMask(mask, false, isSpawn);
                    }

                    cell.setTile(tiledMap.getTileSets().getTile(tileId));
                }
            }
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

    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }

    public Vector2 getExitPoint() {
        return exitPoint;
    }
}