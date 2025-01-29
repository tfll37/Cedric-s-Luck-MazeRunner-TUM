package de.tum.cit.fop.maze.SCREENS;

/**
 * Listener interface for the dice minigame.
 * <p>
 * Implement this interface to receive callbacks when the dice is rolled in the minigame.
 * </p>
 */
public interface DiceMinigameListener {

    /**
     * Called when the dice has been rolled.
     *
     * @param diceResult the result of the dice roll, typically a value between 1 and 6
     */
    void onDiceRolled(int diceResult);
}
