package de.tum.cit.fop.maze.MAZELOGIC;

public class MovementREQ {
    public enum MoveType{
        STEP,
        DASH,
        KNOCKBACK,

    }

    public MoveType moveType;
    public int deltaTileX;
    public int deltaTileY;

    public MovementREQ(MoveType moveType, int deltaTileX, int deltaTileY) {
        this.moveType = moveType;
        this.deltaTileX = deltaTileX;
        this.deltaTileY = deltaTileY;
    }

}
