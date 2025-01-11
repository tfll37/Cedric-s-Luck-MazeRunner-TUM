package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.math.GridPoint2;

public class MazeLoader {
    private final Map<GridPoint2, Integer> tileOverrides;
    private final Properties properties;

    public MazeLoader(String propertiesPath) {
        this.tileOverrides = new HashMap<>();
        this.properties = loadProperties(propertiesPath);
        parseProperties();
    }

    private Properties loadProperties(String path) {
        path = "maps/level-1.properties";
        Properties props = new Properties();
        try {
            FileHandle fileHandle = Gdx.files.internal(path);
            props.load(fileHandle.reader());
        } catch (Exception e) {
            throw new GdxRuntimeException("Failed to load properties file: " + path, e);
        }
        return props;
    }

    private void parseProperties() {
        for (String key : properties.stringPropertyNames()) {
            try {
                // Split the coordinate string (e.g., "3,0")
                String[] coords = key.split(",");
                if (coords.length == 2) {
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    int tileType = Integer.parseInt(properties.getProperty(key));

                    tileOverrides.put(new GridPoint2(x, y), tileType);
                }
            } catch (NumberFormatException e) {
                Gdx.app.error("MazeLoader", "Invalid property format: " + key);
            }
        }
    }

    /**
     * Gets the tile type for a specific coordinate.
     * Returns -1 if no override exists for the coordinate.
     */
    public int getTileType(int x, int y) {
        return tileOverrides.getOrDefault(new GridPoint2(x, y), -1);
    }

    /**
     * Converts tile type to TMX tile ID
     */
    public int getTileId(int tileType) {
        // Map the tile types to actual tile IDs in your tileset
        switch (tileType) {
            case 0: return 22; // Safe ground (ID 21 + 1 due to TMX indexing)
            case 1: return 1;  // Wall
            case 2: return 25; // Enemy type 1
            case 3: return 26; // Enemy type 2
            case 4: return 27; // Collectible type 1
            case 5: return 28; // Collectible type 2
            default: return 22; // Default to safe ground
        }
    }

    /**
     * Checks if there's a tile override at the specified coordinates
     */
    public boolean hasOverride(int x, int y) {
        return tileOverrides.containsKey(new GridPoint2(x, y));
    }

    /**
     * Gets all tile overrides
     */
    public Map<GridPoint2, Integer> getAllOverrides() {
        return new HashMap<>(tileOverrides);
    }
}