package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Manages tile properties and provides utility methods for querying tile characteristics
 * in a tile-based maze game. This class provides static methods to check various
 * properties of tiles such as walkability, type, and orientation.
 */
public class TilePropMNGR {
    /**
     * Determines if a tile at the specified coordinates is walkable.
     * A tile is considered walkable if it has a "Movement" property set to "true".
     * This method includes bounds checking to prevent access outside the layer dimensions.
     *
     * @param layer The tile layer to check
     * @param tileX The x-coordinate of the tile
     * @param tileY The y-coordinate of the tile
     * @return true if the tile exists and is walkable, false otherwise or if coordinates are out of bounds
     */
    public static boolean isTileWalkable(TiledMapTileLayer layer, int tileX, int tileY) {
        if (tileX < 0 || tileX >= layer.getWidth() || tileY < 0 || tileY >= layer.getHeight()) {
            return false;
        }

        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);
        if (cell != null && cell.getTile() != null) {
            Object property = cell.getTile().getProperties().get("Movement");
            return "true".equals(property);
        }
        return false;
    }

    /**
     * Checks if a tile at the specified coordinates is a wall tile.
     * A tile is considered a wall if its "TileDescrip" property equals "wall".
     *
     * @param layer The tile layer to check
     * @param tileX The x-coordinate of the tile
     * @param tileY The y-coordinate of the tile
     * @return true if the tile exists and is a wall, false otherwise or if coordinates are out of bounds
     */
    public static boolean isWallTile(TiledMapTileLayer layer, int tileX, int tileY) {
        if (tileX < 0 || tileX >= layer.getWidth() || tileY < 0 || tileY >= layer.getHeight()) {
            return false;
        }

        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);
        if (cell == null || cell.getTile() == null) {
            return false;
        }

        Object property = cell.getTile().getProperties().get("TileDescrip");
        return property != null && property.equals("wall");
    }


}
