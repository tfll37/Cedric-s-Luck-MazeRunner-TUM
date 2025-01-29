package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class SpecialAreaMNGR {
    /** Directional constant for north connections in bitmasks */
    private static final int NORTH = 1;
    /** Directional constant for east connections in bitmasks */
    private static final int EAST = 2;
    /** Directional constant for south connections in bitmasks */
    private static final int SOUTH = 4;
    /** Directional constant for west connections in bitmasks */
    private static final int WEST = 8;

    /** Width of special areas in tiles */
    private static final int AREA_WIDTH = 3;
    /** Height of special areas in tiles */
    private static final int AREA_HEIGHT = 3;

    /**
     * Contains tile IDs for spawn area components.
     * These IDs correspond to specific tiles in the tileset used to construct
     * the spawn area's visual appearance.
     */
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

    /**
     * Contains tile IDs for exit area components.
     * These IDs correspond to specific tiles in the tileset used to construct
     * the exit area's visual appearance.
     */
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

    private static SpecialAreaMNGR instance;
    private final TiledMap tiledMap;
    private final MazeLoader mazeLoader;
    private Vector2 spawnPoint;
    private Vector2 exitPoint;
    private boolean areasCreated = false;

    /**
     * Constructs a new SpecialAreaMNGR instance.
     * Should typically not be called directly - use getInstance() instead.
     *
     * @param tiledMap The TiledMap to create special areas in
     * @param mazeLoader The MazeLoader instance to use for tile placement
     */
    public SpecialAreaMNGR(TiledMap tiledMap, MazeLoader mazeLoader) {
        this.tiledMap = tiledMap;
        this.mazeLoader = mazeLoader;
    }


    /**
     * Creates spawn and exit areas in the maze if they haven't been created yet.
     * This method ensures special areas are only created once and handles the complete
     * setup of both spawn and exit zones.
     */
    public void createSpecialAreas() {
        if (areasCreated) {
            return;
        }
        TiledMapTileLayer baseLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        int width = baseLayer.getWidth();
        int height = baseLayer.getHeight();
        float tileWidth = baseLayer.getTileWidth();
        float tileHeight = baseLayer.getTileHeight();

        findSpawnPoint(baseLayer, width, height, tileWidth, tileHeight);

        findExitPoint(baseLayer, width, height, tileWidth, tileHeight);

        createArea(baseLayer, width, height, true);
        createArea(baseLayer, width, height, false);

        areasCreated = true;
    }
    /**
     * Creates either a spawn or exit area at appropriate coordinates.
     *
     * @param layer The tile layer to create the area in
     * @param mapWidth The width of the map in tiles
     * @param mapHeight The height of the map in tiles
     * @param isSpawn true to create a spawn area, false to create an exit area
     */
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
    /**
     * Places a specific tile for a special area zone.
     *
     * @param layer The tile layer to place the tile in
     * @param worldX The x coordinate in the world
     * @param worldY The y coordinate in the world
     * @param relativeX The x coordinate relative to the zone's top-left corner
     * @param relativeY The y coordinate relative to the zone's top-left corner
     * @param isSpawn true for spawn zone tiles, false for exit zone tiles
     */
    private void placeZoneTile(TiledMapTileLayer layer, int worldX, int worldY,
                               int relativeX, int relativeY, boolean isSpawn) {
        if (!isWithinBounds(worldX, worldY, layer.getWidth(), layer.getHeight())) {
            return;
        }

        TiledMapTileLayer.Cell cell = getOrCreateCell(layer, worldX, worldY);

        int tileId;

        if (relativeX == 1) {
            if (isSpawn) {
                if (relativeY == 0) tileId = SpawnTiles.SPAWN_PORTAL;
                else if (relativeY == 1) tileId = SpawnTiles.CENTER;
                else tileId = SpawnTiles.TOP_EDGE;
            } else {
                if (relativeY == 2) tileId = ExitTiles.LOCKED_EXIT;
                else if (relativeY == 1) tileId = ExitTiles.CENTER;
                else tileId = ExitTiles.BOTTOM_EDGE;
            }
        } else {
            int mask = calculateBitmask(relativeX, relativeY, AREA_WIDTH, AREA_HEIGHT);
            tileId = getTileIdFromMask(mask, relativeX == 1 && relativeY == 1, isSpawn);
        }

        TiledMapTile tile = tiledMap.getTileSets().getTile(tileId);
        if (tile != null) {
            cell.setTile(tile);
        }
    }
    /**
     * Calculates a bitmask representing adjacent tile connections.
     *
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @param width The width of the area
     * @param height The height of the area
     * @return A bitmask where each bit represents a connection direction
     */
    private int calculateBitmask(int x, int y, int width, int height) {
        int mask = 0;
        if (y < height - 1) mask |= NORTH;
        if (x < width - 1) mask |= EAST;
        if (y > 0) mask |= SOUTH;
        if (x > 0) mask |= WEST;
        return mask;
    }
    /**
     * Checks if coordinates are within the map bounds.
     *
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @param width The map width
     * @param height The map height
     * @return true if coordinates are valid, false otherwise
     */
    private boolean isWithinBounds(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Gets or creates a cell at the specified coordinates.
     *
     * @param layer The tile layer to get/create the cell in
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The existing or newly created cell
     */
    private TiledMapTileLayer.Cell getOrCreateCell(TiledMapTileLayer layer, int x, int y) {
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null) {
            cell = new TiledMapTileLayer.Cell();
            layer.setCell(x, y, cell);
        }
        return cell;
    }

    /**
     * Gets or creates a SpecialAreaMNGR instance.
     * Implements the singleton pattern to ensure only one instance exists.
     *
     * @param tiledMap The TiledMap to create special areas in
     * @param mazeLoader The MazeLoader instance to use for tile placement
     * @return The singleton instance of SpecialAreaMNGR
     */
    public static SpecialAreaMNGR getInstance(TiledMap tiledMap, MazeLoader mazeLoader) {
        if (instance == null) {
            instance = new SpecialAreaMNGR(tiledMap, mazeLoader);
        }
        return instance;
    }


    /**
     * Finds a suitable spawn point location in the map.
     * Searches for walkable tiles starting from the bottom of the map.
     *
     * @param layer The tile layer to search in
     * @param width The map width in tiles
     * @param height The map height in tiles
     * @param tileWidth The width of a tile in pixels
     * @param tileHeight The height of a tile in pixels
     */
    private void findSpawnPoint(TiledMapTileLayer layer, int width, int height, float tileWidth,
                                float tileHeight) {
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

    /**
     * Finds a suitable exit point location in the map.
     * Searches for walkable tiles starting from the top of the map.
     *
     * @param layer The tile layer to search in
     * @param width The map width in tiles
     * @param height The map height in tiles
     * @param tileWidth The width of a tile in pixels
     * @param tileHeight The height of a tile in pixels
     */
    private void findExitPoint(TiledMapTileLayer layer, int width, int height, float tileWidth, float tileHeight) {
        for (int y = 0; y < height; y++) {
            for (int x = width - 1; x >= 0; x--) {
                if (TilePropMNGR.isTileWalkable(layer, x, y)) {
                    exitPoint = new Vector2(x * tileWidth, y * tileHeight);
                    return;
                }
            }
        }
        exitPoint = new Vector2((width - 2) * tileWidth, (height - 2) * tileHeight);
    }

    /**
     * Gets the appropriate tile ID based on a connection bitmask.
     *
     * @param mask The bitmask representing tile connections
     * @param isCenter Whether this is a center tile of the special area
     * @param isSpawn Whether this is for a spawn area (true) or exit area (false)
     * @return The appropriate tile ID from either SpawnTiles or ExitTiles
     */
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

    public void unlockExit() {
        TiledMapTileLayer baseLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        int exitTileX = (int) (exitPoint.x / baseLayer.getTileWidth());
    int exitTileY = (int) (exitPoint.y / baseLayer.getTileHeight()) + 2; // Add 2 to Y to target the door position

        TiledMapTileLayer.Cell cell = baseLayer.getCell(exitTileX, exitTileY);
    if (cell != null && cell.getTile().getId() == ExitTiles.LOCKED_EXIT) {
            cell.setTile(tiledMap.getTileSets().getTile(ExitTiles.UNLOCKED_EXIT));
        }
    }

    /**
     * Gets the current spawn point location.
     * @return The spawn point coordinates as a Vector2
     */
    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }

    /**
     * Gets the current exit point location.
     * @return The exit point coordinates as a Vector2
     */
    public Vector2 getExitPoint() {
        return exitPoint;
    }

    /**
     * Resets the singleton instance.
     * Should be called when transitioning between different maze configurations.
     */
    public static void reset() {
        instance = null;
    }
}