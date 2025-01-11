package de.tum.cit.fop.maze;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
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
}