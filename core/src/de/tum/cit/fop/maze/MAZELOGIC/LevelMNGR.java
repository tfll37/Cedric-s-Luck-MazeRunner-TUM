package de.tum.cit.fop.maze.MAZELOGIC;

import java.util.ArrayList;
import java.util.List;

public class LevelMNGR {
    // Simple level metadata
    public record LevelInfo(
            String name,
            String description,
            int mapSize,
            String propertiesFile,
            int Level
    ) {
    }

    private static final List<LevelInfo> availableLevels = new ArrayList<>();

    static {
        // Register available levels
        availableLevels.add(new LevelInfo(
                "Entrance",
                "A small beginner maze",
                16,
                "maps/level-1-1.properties",
                1
        ));

        availableLevels.add(new LevelInfo(
                "The Treshhold",
                "",
                16,
                "maps/level-1-2.properties",
                2
        ));

        availableLevels.add(new LevelInfo(
                "Challenger",
                "Larger maze with more complexity",
                32,
                "maps/level-2-1.properties",
                3
        ));

        availableLevels.add(new LevelInfo(
                "Diddy's Den",
                "It's too slimey here. I wonder why?",
                32,
                "maps/level-2-2.properties",
                4
        ));

        availableLevels.add(new LevelInfo(
                "HELL",
                "It is hot in here",
                64,
                "maps/level-3-1.properties",
                5
        ));

        availableLevels.add(new LevelInfo(
                "Expert",
                "Massive maze for experts",
                64,
                "maps/level-3-2.properties",
                6
        ));
    }

    public static List<LevelInfo> getAvailableLevels() {
        return availableLevels;
    }

    public static LevelInfo getLevel(int index) {
        if (index >= 0 && index < availableLevels.size()) {
            return availableLevels.get(index);
        }
        return null;
    }

    // Add to LevelMNGR.java
    public static String getTmxPathForSize(int size) {
        return switch (size) {
            case 16 -> "assets/Gamemap16.tmx";
            case 32 -> "assets/Gamemap32.tmx";
            case 64 -> "assets/Gamemap64.tmx";
            default -> throw new IllegalArgumentException("No TMX template for size: " + size);
        };
    }
}
