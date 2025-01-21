package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class MovementSYS {
    Vector2 currentPixel;
    Labyrinth labyrinth;
    float tileWidth;
    float tileHeight;
    MovementREQ movementREQUEST;

    public static Vector2 processMovement(
            Vector2 currentPixel,
            Labyrinth labyrinth,
            float tileWidth, float tileHeight,
            MovementREQ movementREQUEST
    ) {
        TiledMapTileLayer layer = (TiledMapTileLayer) labyrinth.getBackground().getTiledMap().getLayers().get(0);
        int currentTileX = (int) (currentPixel.x / layer.getTileWidth());
        int currentTileY = (int) (currentPixel.y / layer.getTileHeight());

        int targetTileX = currentTileX + movementREQUEST.deltaTileX;
        int targetTileY = currentTileY + movementREQUEST.deltaTileY;

        if (TilePropMNGR.isTileWalkable(layer, targetTileX, targetTileY)) {
            float newX = targetTileX * tileWidth;
            float newY = targetTileY * tileHeight;
            return new Vector2(newX, newY);
        }

        return currentPixel.cpy();

    }

}
