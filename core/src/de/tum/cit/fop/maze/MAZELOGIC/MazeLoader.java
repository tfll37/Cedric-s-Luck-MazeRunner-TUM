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

/**
 * The type Maze loader.
 */
public class MazeLoader {
    private final Map<GridPoint2, Integer> tileOverrides;
    private final Properties properties;
    private TiledMap tiledMap;
    private final int mapHeight;
    private final TileEffectMNGR tileEffectMNGR;

    /**
     * Instantiates a new Maze loader.
     *
     * @param propertiesPath the properties path
     * @param tiledMap       the tiled map
     * @param tileEffectMNGR the tile effect mngr
     */
    public MazeLoader(String propertiesPath, TiledMap tiledMap, TileEffectMNGR tileEffectMNGR) {
        this.tileOverrides = new HashMap<>();
        this.properties = loadProperties(propertiesPath);
        this.tiledMap = tiledMap;
        this.mapHeight = tiledMap.getProperties().get("height", Integer.class);
        this.tileEffectMNGR = tileEffectMNGR;
        parseProperties();
    }

    private Properties loadProperties(String path) {
        Gdx.app.log("MazeLoader", "Loading properties from: " + path);

        Properties props = new Properties();
        try {
            FileHandle fileHandle = Gdx.files.internal(path);
            props.load(fileHandle.reader());
            Gdx.app.log("MazeLoader", "Loaded " + props.stringPropertyNames().size() + " keys from " + path);

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
                    int y = Integer.parseInt(coords[1]);
                    int transformedY = mapHeight - 1 - y;
                    int tileType = Integer.parseInt(properties.getProperty(key));

                    tileOverrides.put(new GridPoint2(x, transformedY), tileType);

                if (tileType == TileEffectMNGR.TRAP_MARKER) {
                    // Register trap marker position and let TileEffectMNGR handle random type assignment
                    tileOverrides.put(new GridPoint2(x, transformedY), TileEffectMNGR.TRAP_MARKER);

                    tileEffectMNGR.registerTrapLocation(x, transformedY);

                    System.out.println("Registered trap marker at " + x + "," + y);
                }
                else if (tileType == TileEffectMNGR.POWERUP_MARKER) {
                    // Register powerup marker position and let TileEffectMNGR handle random type assignment
                    tileOverrides.put(new GridPoint2(x, transformedY), TileEffectMNGR.POWERUP_MARKER);
                    tileEffectMNGR.registerPowerUp(x, transformedY);
                    System.out.println("Registered powerup marker at " + x + "," + y);
                }
                else {
                    tileOverrides.put(new GridPoint2(x, transformedY), tileType);
                    }
                }
            } catch (NumberFormatException e) {
                Gdx.app.error("MazeLoader", "Invalid property format: " + key, e);
            }
        }
    }

    /**
     * Converts tile type to TMX tile ID
     *
     * @param tileType the tile type
     * @return the tile id
     */
    public int getTileId(int tileType) {
        switch (tileType) {
            case 0:
                return 1295;          // Safe ground
            case 1:
                return WallTiles.HORIZONTAL;
            case 2:
                return 23;
            case 3:
                return TileEffectMNGR.getRandomTrap().getTileId();        // Enemy type 2
            case 4:
                return TileEffectMNGR.getRandomPowerUp().getTileId();
            default:
                return 1295; //
        }
    }

    private int adjustY(int y) {
        int height = tiledMap.getProperties().get("height", Integer.class);
        return height - 1 - y;
    }

    private static final class WallTiles {
        /**
         * The Top left.
         */
        static final int TOP_LEFT = 241;
        /**
         * The Top right.
         */
        static final int TOP_RIGHT = 243;
        /**
         * The Bottom left.
         */
        static final int BOTTOM_LEFT = 281;
        /**
         * The Bottom right.
         */
        static final int BOTTOM_RIGHT = 283;
        /**
         * The Horizontal.
         */
        static final int HORIZONTAL = 242;
        /**
         * The Vertical.
         */
        static final int VERTICAL = 261;
        /**
         * The Default.
         */
        static final int DEFAULT = 244;
        /**
         * The Cross.
         */
        static final int CROSS = 265;
        /**
         * The Top end.
         */
        static final int TOP_END = 38;
        /**
         * The Bottom end.
         */
        static final int BOTTOM_END = 262;
        /**
         * The Top horiz t.
         */
        static final int TOP_HORIZ_T = 245;
        /**
         * The Bottom horiz t.
         */
        static final int BOTTOM_HORIZ_T = 285;
        /**
         * The Left vert t.
         */
        static final int LEFT_VERT_T = 264;
        /**
         * The Right vert t.
         */
        static final int RIGHT_VERT_T = 266;

    }

    private static final int NORTH = 1;  // 0001
    private static final int EAST = 2;  // 0010
    private static final int SOUTH = 4;  // 0100
    private static final int WEST = 8;  // 1000

    /**
     * Orient wall tiles.
     *
     * @param targetLayer the target layer
     */
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
        return switch (mask) {
            case NORTH | EAST | SOUTH | WEST -> "cross_4directional";
            case EAST | SOUTH | WEST -> "top_horizontal_3exits";
            case NORTH | EAST | WEST -> "bottom_horizontal_3exits";
            case NORTH | SOUTH | WEST -> "right_side_vertical_3exits";
            case NORTH | SOUTH | EAST -> "left_side_vertical_3exits";
            case NORTH | SOUTH -> "straight_vertical";
            case EAST | WEST -> "straight_horizontal";
            case SOUTH | EAST -> "top_left_edge";
            case SOUTH | WEST -> "top_right_edge";
            case NORTH | EAST -> "bottom_left_edge";
            case NORTH | WEST -> "bottom_right_edge";
            case NORTH -> "bottom_vetical_end";
            case SOUTH -> "top_vertical_end";
            default -> "default_wall_texture";
        };
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

        TiledMapTile tile = tiledMap.getTileSets().getTile(tileId);
        if (tile != null) {
            cell.setTile(tile);
        } else {
            cell.setTile(tiledMap.getTileSets().getTile(WallTiles.DEFAULT));
        }
    }

    /**
     * Gets trap mngr.
     *
     * @return the trap mngr
     */
    public TileEffectMNGR getTrapMNGR() {
        return tileEffectMNGR;
    }


    /**
     * Has override boolean.
     *
     * @param x the x
     * @param y the y
     * @return the boolean
     */
    public boolean hasOverride(int x, int y) {
        int transformedY = mapHeight - 1 - y;
        return tileOverrides.containsKey(new GridPoint2(x, transformedY));
    }


    /**
     * Gets all overrides.
     *
     * @return the all overrides
     */
    public Map<GridPoint2, Integer> getAllOverrides() {
        return new HashMap<>(tileOverrides);
    }

    /**
     * Sets tiled map.
     *
     * @param tiledMap the tiled map
     */
    public void setTiledMap(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
    }
}