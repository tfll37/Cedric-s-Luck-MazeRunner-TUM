package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MazeLoader {
    private final Map<GridPoint2, Integer> tileOverrides;
    private final Properties properties;
    private TiledMap tiledMap;
    private final int mapHeight;
    private final TileEffectMNGR tileEffectMNGR;

    public MazeLoader(String propertiesPath, TiledMap tiledMap, TileEffectMNGR tileEffectMNGR) {
        this.tileEffectMNGR = new TileEffectMNGR();
        this.tileOverrides = new HashMap<>();
        this.properties = loadProperties(propertiesPath);
        this.tiledMap = tiledMap;
        this.mapHeight = tiledMap.getProperties().get("height", Integer.class);
        parseProperties();
    }

    private Properties loadProperties(String path) {
        Properties props = new Properties();
        try {
            FileHandle fileHandle = Gdx.files.internal(path);
            props.load(fileHandle.reader());
        } catch (Exception e) {
            throw new GdxRuntimeException("Failed to load properties file: " + path, e);
        }
        return props;
    }

    private void parseProperties() {
        for (String key : properties.stringPropertyNames()) {
            try {
                String[] coords = key.split(",");
                if (coords.length == 2) {
                    int x = Integer.parseInt(coords[0]);
                    int originalY = Integer.parseInt(coords[1]);
                    int transformedY = mapHeight - 1 - originalY;
                    int tileType = Integer.parseInt(properties.getProperty(key));

                    tileOverrides.put(new GridPoint2(x, transformedY), tileType);


                    if (tileType == TileEffectMNGR.TRAP_MARKER) {
                        tileEffectMNGR.registerTrapLocation(x, transformedY);
                        // Get the actual trap tile ID for rendering
                        int trapTileId = tileEffectMNGR.getTileEffectId(x, transformedY);
                        tileOverrides.put(new GridPoint2(x, transformedY), trapTileId);
                    } else {
                        tileOverrides.put(new GridPoint2(x, transformedY), tileType);
                    }
                }
            } catch (NumberFormatException e) {
                Gdx.app.error("MazeLoader", "Invalid property format: " + key);
            }
        }
    }


    /**
     * Gets the tile type for a specific coordinate.
     * Returns -1 if no override exists for the coordinate.
     */
    public int getTileType(int x, int y) {
        int transformedY = mapHeight - 1 - y;
        return tileOverrides.getOrDefault(new GridPoint2(x, transformedY), -1);
    }

    /**
     * Converts tile type to TMX tile ID
     */
    public int getTileId(int tileType) {
        switch (tileType) {
            case 0:
                return 1295;          // Safe ground
            case 1:
                return WallTiles.HORIZONTAL;
            case 2:
                return TileEffectMNGR.getRandomTrap().getTileId();
            case 3:
                return 27;            // Enemy type 2
            case 4:
                return TileEffectMNGR.getRandomPowerUp().getTileId();
            default:
                return 1295;         // Default to safe ground
        }
    }

    private int adjustY(int y) {
        int height = tiledMap.getProperties().get("height", Integer.class);
        return height - 1 - y;
    }

    private static final class WallTiles {
        static final int TOP_LEFT = 241;
        static final int TOP_RIGHT = 243;
        static final int BOTTOM_LEFT = 281;
        static final int BOTTOM_RIGHT = 283;
        static final int HORIZONTAL = 242;
        static final int VERTICAL = 261;
        static final int DEFAULT = 244;
        static final int CROSS = 265;
        static final int TOP_END = 38;
        static final int BOTTOM_END = 262;
        static final int TOP_HORIZ_T = 245;
        static final int BOTTOM_HORIZ_T = 285;
        static final int LEFT_VERT_T = 264;
        static final int RIGHT_VERT_T = 266;

    }

    private static final int NORTH = 1;  // 0001
    private static final int EAST = 2;  // 0010
    private static final int SOUTH = 4;  // 0100
    private static final int WEST = 8;  // 1000

    public void orientWallTiles(TiledMapTileLayer targetLayer) {
        if (targetLayer == null) {
            throw new IllegalStateException("Layer must not be null");
        }

        int width = targetLayer.getWidth();
        int height = targetLayer.getHeight();

        try {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (TilePropMNGR.isWallTile(targetLayer, x, y)) {
                        int mask = 0;
                        if (TilePropMNGR.isWallTile(targetLayer, x, y + 1)) mask |= NORTH;
                        if (TilePropMNGR.isWallTile(targetLayer, x + 1, y)) mask |= EAST;
                        if (TilePropMNGR.isWallTile(targetLayer, x, y - 1)) mask |= SOUTH;
                        if (TilePropMNGR.isWallTile(targetLayer, x - 1, y)) mask |= WEST;

                        setOrientedWallTile(targetLayer, x, y, getOrientationFromMask(mask));
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("MazeLoader", "Error orienting wall tiles: " + e.getMessage());
        }
    }

    private String getOrientationFromMask(int mask) {
        switch (mask) {
            // Full cross (all directions)
            case NORTH | EAST | SOUTH | WEST:
                return "cross_4directional";

            // T-junctions
            case EAST | SOUTH | WEST:
                return "top_horizontal_3exits";
            case NORTH | EAST | WEST:
                return "bottom_horizontal_3exits";
            case NORTH | SOUTH | WEST:
                return "right_side_vertical_3exits";
            case NORTH | SOUTH | EAST:
                return "left_side_vertical_3exits";

            // Straight pieces
            case NORTH | SOUTH:
                return "straight_vertical";
            case EAST | WEST:
                return "straight_horizontal";

            // Corners
            case SOUTH | EAST:
                return "top_left_edge";
            case SOUTH | WEST:
                return "top_right_edge";
            case NORTH | EAST:
                return "bottom_left_edge";
            case NORTH | WEST:
                return "bottom_right_edge";

            // End pieces
            case NORTH:
                return "bottom_vetical_end";
            case SOUTH:
                return "top_vertical_end";

            // Default case
            default:
                return "default_wall_texture";
        }
    }

    private void setOrientedWallTile(TiledMapTileLayer layer, int x, int y, String orientation) {
        int adjustedY = adjustY(y);
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        if (cell == null) {
            cell = new TiledMapTileLayer.Cell();
            layer.setCell(x, y, cell);
        }

        int tileId = switch (orientation) {
            case "top_left_edge" -> WallTiles.TOP_LEFT;
            case "straight_horizontal" -> WallTiles.HORIZONTAL;
            case "top_right_edge" -> WallTiles.TOP_RIGHT;
            case "cross_4directional" -> WallTiles.CROSS;
            case "straight_vertical" -> WallTiles.VERTICAL;
            case "bottom_vetical_end" -> WallTiles.BOTTOM_END;
            case "top_vertical_end" -> WallTiles.TOP_END;
            case "bottom_left_edge" -> WallTiles.BOTTOM_LEFT;
            case "bottom_right_edge" -> WallTiles.BOTTOM_RIGHT;
            case "left_side_vertical_3exits" -> WallTiles.LEFT_VERT_T;
            case "right_side_vertical_3exits" -> WallTiles.RIGHT_VERT_T;
            case "top_horizontal_3exits" -> WallTiles.TOP_HORIZ_T;
            case "bottom_horizontal_3exits" -> WallTiles.BOTTOM_HORIZ_T;
            default -> WallTiles.HORIZONTAL;
        };

        // Set the tile directly using the ID
        TiledMapTile tile = tiledMap.getTileSets().getTile(tileId);
        if (tile != null) {
            cell.setTile(tile);
        } else {
            cell.setTile(tiledMap.getTileSets().getTile(WallTiles.DEFAULT));
        }
    }

    public TileEffectMNGR getTrapMNGR() {
        return tileEffectMNGR;
    }

    /**
     * Checks if there's a tile override at the specified coordinates
     */
    public boolean hasOverride(int x, int y) {
        int transformedY = mapHeight - 1 - y;
        return tileOverrides.containsKey(new GridPoint2(x, transformedY));
    }

    /**
     * Gets all tile overrides
     */
    public Map<GridPoint2, Integer> getAllOverrides() {
        return new HashMap<>(tileOverrides);
    }

    public void setTiledMap(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
    }
}