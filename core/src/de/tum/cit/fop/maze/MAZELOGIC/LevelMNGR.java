package de.tum.cit.fop.maze.MAZELOGIC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelMNGR {

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
                1
        ));

        availableLevels.add(new LevelInfo(
                "The Threshold",
                "",
                16,
                "maps/level-1-2.properties",
                "Easy",
                1,
                30
        ));

        availableLevels.add(new LevelInfo(
                "Challenger",
                "Larger maze with more complexity",
                32,
                "maps/level-2-1.properties",
                "Medium",
                2,
                50
        ));

        availableLevels.add(new LevelInfo(
                "Diddy's Den",
                "It's too slimey here. I wonder why?",
                32,
                "maps/level-2-2.properties",
                "Medium",
                3,
                70
        ));

        availableLevels.add(new LevelInfo(
                "HELL",
                "It is hot in here",
                64,
                "maps/level-3-1.properties",
                "Hard",
                4,
                100
        ));

        availableLevels.add(new LevelInfo(
                "Expert",
                "Massive maze for experts",
                64,
                "maps/level-3-2.properties",
                "Hard",
                5,
                120
        ));
    }

    public static List<LevelInfo> getAvailableLevels() {
        return availableLevels;
    }

    public static int generateScoreRequirement(LevelInfo level) {
        Random random = new Random();
        int baseScore = level.requiredScore();
        // Generate score between 80% and 120% of base score
        return (int) (baseScore * (0.8 + (random.nextDouble() * 0.4)));
    }

    public static LevelInfo getLevel(int index) {
        if (index >= 0 && index < availableLevels.size()) {
            return availableLevels.get(index);
        }
        return null;
    }

    public static String getTmxPathForSize(int size) {
        return switch (size) {
            case 16 -> "assets/Tiled/Gamemap16.tmx";
            case 32 -> "assets/Tiled/Gamemap32.tmx";
            case 64 -> "assets/Tiled/Gamemap64.tmx";
            default -> throw new IllegalArgumentException("No TMX template for size: " + size);
        };
    }
}
