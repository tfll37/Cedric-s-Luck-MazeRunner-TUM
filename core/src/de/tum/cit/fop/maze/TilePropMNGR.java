package de.tum.cit.fop.maze;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class TilePropMNGR {

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

    public static String getTileDescription(TiledMapTileLayer layer, int tileX, int tileY) {
        if (tileX < 0 || tileX >= layer.getWidth() || tileY < 0 || tileY >= layer.getHeight()) {
            return null;
        }

        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);
        if (cell != null && cell.getTile() != null) {
            Object property = cell.getTile().getProperties().get("TileDescrip");
            return property != null ? property.toString() : null;
        }
        return null;
    }

    public static int getTileType(TiledMapTileLayer layer, int tileX, int tileY) {
        if (tileX < 0 || tileX >= layer.getWidth() || tileY < 0 || tileY >= layer.getHeight()) {
            return -1;
        }

        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);
        if (cell != null && cell.getTile() != null) {
            return cell.getTile().getId();
        }
        return -1;
    }


    public static boolean isWallTile(TiledMapTileLayer layer, int tileX, int tileY) {
        String description = getTileDescription(layer, tileX, tileY);
        return description != null && description.equals("wall");
    }

    public static boolean isTileTrap(TiledMapTileLayer layer, int tileX, int tileY) {
        String description = getTileDescription(layer, tileX, tileY);
        return description != null && description.equals("trap");
    }

    //not used right now
//    public static String getWallOrientation(TiledMapTileLayer layer, boolean[] neighbors) {
//        boolean north = neighbors[0];
//        boolean east = neighbors[1];
//        boolean south = neighbors[2];
//        boolean west = neighbors[3];
//
//        // Corner cases
//        if (!north && !east && south && west) return "top_right_edge";
//        if (!north && east && south && !west) return "top_leftss_edge";
//        if (north && !east && !south && west) return "bottom_right_edge";
//        if (north && east && !south && !west) return "bottom_left_edge";
//
//        // Straight pieces
//        if (north && !east && south && !west) return "straight_vertical";
//        if (!north && east && !south && west) return "straight_horizontal";
//
//        // End piece
//        if (north && !east && !south && !west) return "bottom_vetical_end";
//
//        // All sides
//        if (north && east && south && west) return "cross_4directional";
//
//        // Default case
//        return "default_wall_texture";
//    }

    public static String getWallOrientationFromTile(TiledMapTileLayer layer, int tileX, int tileY) {
        if (tileX < 0 || tileX >= layer.getWidth() || tileY < 0 || tileY >= layer.getHeight()) {
            return null;
        }

        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);
        if (cell != null && cell.getTile() != null) {
            Object property = cell.getTile().getProperties().get("Wall_Orientation");
            return property != null ? property.toString() : null;
        }
        return null;
    }
}
