package de.tum.cit.fop.maze.MAZELOGIC;

import java.util.ArrayList;
import java.util.List;

public class LevelMNGR {

    public record LevelInfo(
            String name,
            String description,
            int mapSize,
            String propertiesFile,
            String difficulty,
            int Level
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
                0
        ));

        availableLevels.add(new LevelInfo(
                "The Threshold",
                "",
                16,
                "maps/level-1-2.properties",
                "Easy",
                1
        ));

        availableLevels.add(new LevelInfo(
                "Challenger",
                "Larger maze with more complexity",
                32,
                "maps/level-2-1.properties",
                "Medium",
                2
        ));

        availableLevels.add(new LevelInfo(
                "Diddy's Den",
                "It's too slimey here. I wonder why?",
                32,
                "maps/level-2-2.properties",
                "Medium",
                3
        ));

        availableLevels.add(new LevelInfo(
                "HELL",
                "It is hot in here",
                64,
                "maps/level-3-1.properties",
                "Hard",
                4
        ));

        availableLevels.add(new LevelInfo(
                "Expert",
                "Massive maze for experts",
                64,
                "maps/level-3-2.properties",
                "Hard",
                5
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

    public static String getTmxPathForSize(int size) {
        return switch (size) {
            case 16 -> "assets/Tiled/Gamemap16.tmx";
            case 32 -> "assets/Tiled/Gamemap32.tmx";
            case 64 -> "assets/Tiled/Gamemap64.tmx";
            default -> throw new IllegalArgumentException("No TMX template for size: " + size);
        };
    }
}
