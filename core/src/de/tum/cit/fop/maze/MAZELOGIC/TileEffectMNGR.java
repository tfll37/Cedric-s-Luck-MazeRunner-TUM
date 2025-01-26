package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import de.tum.cit.fop.maze.PC_NPC_OBJ.Player;

import java.util.ArrayList;
import java.util.Random;

public class TileEffectMNGR {
    public static final int TRAP_MARKER = 2;
    public static final int POWERUP_MARKER = 4;

    // Base interface for all effects
    public interface TileEffect {
        int getTileId();

        String getName();

        void applyEffect(Player player);
    }

    // Define trap types and their corresponding tile IDs
    public enum TrapType implements TileEffect {
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

        @Override
        public int getTileId() {
            return tileId;
        }
        @Override
        public String getName() {
            return name;
        }
        @Override
        public void applyEffect(Player player) {
            if (damage > 0) {
                player.takeDamage(damage);
            }
            if (speedModifier != 1.0f) {
                player.setSpeedModifier(speedModifier);
            }
        }
        public static TrapType getByTileId(int tileId) {
            for (TrapType type : values()) {
                if (type.tileId == tileId) return type;
            }
            return null;
        }
        public float getDamage() {
            return damage;
        }
        public float getSpeedModifier() {
            return speedModifier;
        }

    }

    public enum PowerUpType implements TileEffect {
        SPEED_BOOST(2327, "Speed Boost", 2.0f, 0f, 5f),    // 2x speed for 5 seconds
        HEALTH_PACK(2324, "Health Pack", 1.0f, 25f, 0f);

        private final int tileId;
        private final String name;
        private final float speedModifier;
        private final float healing;
        private final float duration;


        PowerUpType(int tileId, String name, float speedModifier, float healing, float duration) {
            this.tileId = tileId;
            this.name = name;
            this.speedModifier = speedModifier;
            this.healing = healing;
            this.duration = duration;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getTileId() {
            return tileId;
        }

        @Override
        public void applyEffect(Player player) {
            if (healing > 0) {
                player.heal(healing);
            }
            if (speedModifier != 1.0f) {
                player.setSpeedModifier(speedModifier);
                // TODO: Add duration handling for temporary effects
            }
        }

        public static PowerUpType getByTileId(int tileId) {
            for (PowerUpType type : values()) {
                if (type.tileId == tileId) return type;
            }
            return null;
        }


    }

    private final ObjectMap<Vector2, TrapType> trapLocations;
    private final ObjectMap<Vector2, TileEffect> tileEffects;
    private final ObjectMap<Vector2, PowerUpType> powerUpLocations;
    private final Random random;

    public TileEffectMNGR() {
        this.trapLocations = new ObjectMap<>();
        this.tileEffects = new ObjectMap<>();
        this.powerUpLocations = new ObjectMap<>();
        this.random = new Random();
    }

    public void registerPowerUp(int x, int y, PowerUpType powerUp) {
        Vector2 location = new Vector2(x, y);
        powerUpLocations.put(location, powerUp);
    }

    public PowerUpType getPowerUpAtLocation(int x, int y) {
        return powerUpLocations.get(new Vector2(x, y));
    }

    public void registerTileEffect(int x, int y, boolean isTrap) {
        Vector2 location = new Vector2(x, y);
        TileEffect effect;

        if (isTrap) {
            TrapType[] trapTypes = TrapType.values();
            effect = trapTypes[random.nextInt(trapTypes.length)];
        } else {
            PowerUpType[] powerUpTypes = PowerUpType.values();
            effect = powerUpTypes[random.nextInt(powerUpTypes.length)];
        }

        tileEffects.put(location, effect);
    }

    public void registerTrapLocation(int x, int y) {
        Vector2 location = new Vector2(x, y);
        TrapType[] trapTypes = TrapType.values();
        TrapType randomTrap = trapTypes[random.nextInt(trapTypes.length)];
        trapLocations.put(location, randomTrap);
    }

    public int getTileEffectId(int x, int y) {
        Vector2 location = new Vector2(x, y);
        TileEffect effect = tileEffects.get(location);
        return effect != null ? effect.getTileId() : -1;
    }

    public TrapType getEffectAtLocation(int x, int y) {
        return trapLocations.get(new Vector2(x, y));
    }


    public void applyEffect(Player player, TileEffect effect) {
        if (effect != null) {
            effect.applyEffect(player);
        }
    }

    public TrapType checkTrap(Vector2 position) {
        int tileX = (int) (position.x / 16);
        int tileY = (int) (position.y / 16);
        return getEffectAtLocation(tileX, tileY);
    }

    public static TrapType getRandomTrap() {
        TrapType[] traps = TrapType.values();
        int randomIndex = (int) (Math.random() * traps.length);
        return traps[randomIndex];
    }

    public static PowerUpType getRandomPowerUp() {
        PowerUpType[] powerUps = PowerUpType.values();
        int randomIndex = (int) (Math.random() * powerUps.length);
        return powerUps[randomIndex];
    }

    public void clearTrap(int x, int y) {
        trapLocations.remove(new Vector2(x, y));
    }
}