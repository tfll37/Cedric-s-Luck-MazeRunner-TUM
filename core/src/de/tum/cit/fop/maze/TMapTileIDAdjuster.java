package de.tum.cit.fop.maze;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

public class TMapTileIDAdjuster {
    private TiledMap tiledMap;

    public TMapTileIDAdjuster(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
    }

    public void adjustTileId(){
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;

                for (int x = 0; x < tileLayer.getWidth(); x++) {
                    for (int y = 0; y < tileLayer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);

                        if (cell != null && cell.getTile() != null) {
                            TiledMapTile tile = cell.getTile();
                            int newId = tile.getId() - 1;

                            TiledMapTile correctedTile = findTileById(newId);
                            if (correctedTile != null) {
                                cell.setTile(correctedTile);
                            }
                        }
                    }
                }
            }
        }
    }

    private TiledMapTile findTileById(int id){
        for (TiledMapTileSet tileSet : tiledMap.getTileSets()) {
            TiledMapTile tile = tileSet.getTile(id);
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }
}
