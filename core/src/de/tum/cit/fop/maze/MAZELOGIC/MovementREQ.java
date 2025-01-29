package de.tum.cit.fop.maze.MAZELOGIC;

/**
 * Represents a movement request in the maze game.
 * This class encapsulates all necessary information for processing
 * an entity's movement, including the type of movement and the
 * desired change in position.
 */
public class MovementREQ {

    /**
     * Defines the different types of movement available in the game.
     */
    public enum MoveType{
        /** Regular step movement, moving one tile at normal speed */
        STEP,
        /** Quick dash movement, potentially moving multiple tiles quickly */
        DASH,
    }

    /** The type of movement being requested */
    public MoveType moveType;
    /** The desired change in x position, measured in tiles */
    public int deltaTileX;
    /** The desired change in y position, measured in tiles */
    public int deltaTileY;

    /**
     * Creates a new movement request with specified parameters.
     *
     * @param moveType The type of movement to perform (STEP or DASH)
     * @param deltaTileX The number of tiles to move in the x direction
     *                   (positive for right, negative for left)
     * @param deltaTileY The number of tiles to move in the y direction
     *                   (positive for up, negative for down)
     */
    public MovementREQ(MoveType moveType, int deltaTileX, int deltaTileY) {
        this.moveType = moveType;
        this.deltaTileX = deltaTileX;
        this.deltaTileY = deltaTileY;
    }

}
