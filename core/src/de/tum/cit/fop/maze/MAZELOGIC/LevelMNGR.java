package de.tum.cit.fop.maze.MAZELOGIC;

import java.util.ArrayList;
import java.util.List;

public class LevelMNGR {
    // Simple level metadata
    public record LevelInfo(
            String name,
            String description,
            int mapSize,  // 16, 32, 64, etc.
            String propertiesFile,
            int difficulty
    ) {
    }

    private static final List<LevelInfo> availableLevels = new ArrayList<>();

    static {
        // Register available levels
        availableLevels.add(new LevelInfo(
                "Tutorial",
                "A small beginner maze",
                16,  // 16x16
                "maps/level-1.properties",
                1
        ));

        availableLevels.add(new LevelInfo(
                "Challenge",
                "Larger maze with more complexity",
                32,  // 32x32
                "maps/level-2.properties",
                2
        ));

        availableLevels.add(new LevelInfo(
                "Expert",
                "Massive maze for experts",
                64,  // 64x64
                "maps/level-4.properties",
                3
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
