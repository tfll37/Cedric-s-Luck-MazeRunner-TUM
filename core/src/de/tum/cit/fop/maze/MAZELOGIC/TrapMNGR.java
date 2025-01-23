package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import de.tum.cit.fop.maze.PC_NPC_OBJ.Player;

import java.util.Random;

public class TrapMNGR {
    public static final int TRAP_MARKER = 3;

    // Define trap types and their corresponding tile IDs
    public enum TrapType {
        POISON(2057, "Poison", 5f, 1.0f),      // damage over time
        SLOWDOWN(2060, "Slowdown", 0f, 0.5f),  // slows player by 50%
        DEATH(2075, "Death", 100f, 1.0f);      // instant kill trap

        private final int tileId;
        private final String name;
        private final float damage;
        private final float speedModifier;

        TrapType(int tileId, String name, float damage, float speedModifier) {
            this.tileId = tileId;
            this.name = name;
            this.damage = damage;
            this.speedModifier = speedModifier;
    }

        public int getTileId() { return tileId; }
        public String getName() { return name; }
        public float getDamage() { return damage; }
        public float getSpeedModifier() { return speedModifier; }
        public static TrapType getByTileId(int tileId) {
            for (TrapType type : values()) {
                if (type.tileId == tileId) return type;
            }
            return null;
    }
    }

    private final ObjectMap<Vector2, TrapType> trapLocations;
    private final Random random;

    public TrapMNGR() {
        this.trapLocations = new ObjectMap<>();
        this.random = new Random();
    }

    public void registerTrapLocation(int x, int y) {
        Vector2 location = new Vector2(x, y);
        TrapType[] trapTypes = TrapType.values();
        TrapType randomTrap = trapTypes[random.nextInt(trapTypes.length)];
        trapLocations.put(location, randomTrap);
    }
    public int getTrapTileId(int x, int y) {
        Vector2 location = new Vector2(x, y);
        TrapType trap = trapLocations.get(location);
        return trap != null ? trap.getTileId() : -1;
}

    public TrapType getTrapAtLocation(int x, int y) {
        return trapLocations.get(new Vector2(x, y));
    }

    public void applyTrapEffect(Player player, TrapType trap) {
        if (trap == null) return;

        // Apply damage
        if (trap.getDamage() > 0) {
            player.takeDamage(trap.getDamage());
        }

        // Apply speed modification
        if (trap.getSpeedModifier() != 1.0f) {
            player.setSpeedModifier(trap.getSpeedModifier());
        }
    }
    public TrapType checkTrap(Vector2 position) {
        int tileX = (int) (position.x / 16);
        int tileY = (int) (position.y / 16);
        return getTrapAtLocation(tileX, tileY);
    }

    public static TrapType getRandomTrap() {
        TrapType[] traps = TrapType.values();
        int randomIndex = (int)(Math.random() * traps.length);
        return traps[randomIndex];
    }
    public void clearTrap(int x, int y) {
        trapLocations.remove(new Vector2(x, y));
    }
}