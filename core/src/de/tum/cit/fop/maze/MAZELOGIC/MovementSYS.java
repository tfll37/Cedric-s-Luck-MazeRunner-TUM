package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
/**
 * Movement system for handling entity movement within the maze game.
 * This class provides static utilities for processing movement requests
 * while respecting tile-based collision detection.
 */
public class MovementSYS {
    /**
     * Processes a movement request and returns the new position.
     * This method handles collision detection and tile-based movement,
     * ensuring entities cannot move through walls or other obstacles.
     *
     * @param currentPixel The current position in pixel coordinates
     * @param labyrinth The labyrinth containing collision data
     * @param tileWidth The width of a single tile in pixels
     * @param tileHeight The height of a single tile in pixels
     * @param movementREQUEST The movement request to process
     * @return A Vector2 containing the new position after movement,
     *         or the original position if movement was blocked
     */
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
