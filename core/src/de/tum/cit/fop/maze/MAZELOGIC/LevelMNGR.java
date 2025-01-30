package de.tum.cit.fop.maze.MAZELOGIC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The type Level mngr.
 */
public class LevelMNGR {

    /**
     * The type Level info.
     */
    public record LevelInfo(
            String name,
            String description,
            int mapSize,
            String propertiesFile,
            String difficulty,
            int Level,
            int requiredScore
    ) {
    }

    private static final List<LevelInfo> availableLevels = new ArrayList<>();

    static {

        availableLevels.add(new LevelInfo(
                "Entrance",
                "A small beginner maze",
                16,
                "maps/level-1-1.properties",
                "Easy",
                0,
                3
        ));

        availableLevels.add(new LevelInfo(
                "Threshold",
                "First enemies",
                16,
                "maps/level-1-2.properties",
                "Easy",
                1,
                7
        ));

        availableLevels.add(new LevelInfo(
                "Challenger",
                "Larger maze with more complexity",
                32,
                "maps/level-2-1.properties",
                "Med",
                2,
                10
        ));

        availableLevels.add(new LevelInfo(
                " Despair",
                "It's too slimy here. I wonder why?",
                32,
                "maps/level-2-2.properties",
                "Med",
                3,
                10
        ));

        availableLevels.add(new LevelInfo(
                "Hell",
                "It is hot in here",
                64,
                "maps/level-3-1.properties",
                "Hard",
                4,
                10
        ));

        availableLevels.add(new LevelInfo(
                "Expert",
                "Massive maze for experts",
                64,
                "maps/level-3-2.properties",
                "Hard",
                5,
                0
        ));
    }

    /**
     * Gets available levels.
     *
     * @return the available levels
     */
    public static List<LevelInfo> getAvailableLevels() {
        return availableLevels;
    }

    /**
     * Generate score requirement int.
     *
     * @param level the level
     * @return the int
     */
    public static int generateScoreRequirement(LevelInfo level) {
        Random random = new Random();
        int baseScore = level.requiredScore();
        return (int) (baseScore * (0.8 + (random.nextDouble() * 0.4)));
    }

    /**
     * Gets level.
     *
     * @param index the index
     * @return the level
     */
    public static LevelInfo getLevel(int index) {
        if (index >= 0 && index < availableLevels.size()) {
            return availableLevels.get(index);
        }
        return null;
    }

    /**
     * Gets tmx path for size.
     *
     * @param size the size
     * @return the tmx path for size
     */
    public static String getTmxPathForSize(int size) {
        return switch (size) {
            case 16 -> "assets/Tiled/Gamemap16.tmx";
            case 32 -> "assets/Tiled/Gamemap32.tmx";
            case 64 -> "assets/Tiled/Gamemap64.tmx";
            default -> throw new IllegalArgumentException("No TMX template for size: " + size);
        };
    }


}
