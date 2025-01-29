package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import de.tum.cit.fop.maze.PC_NPC_OBJ.Player;

import java.util.Random;

/**
 * Manages tile-based effects in the maze game, including traps and power-ups.
 * This class handles the registration, application, and tracking of various
 * effects that can be triggered when a player moves onto specific tiles.
 */
public class TileEffectMNGR {
    /**
     * Marker value indicating a trap tile in the map data
     */

    public static final int TRAP_MARKER = 2;
    /**
     * Marker value indicating a power-up tile in the map data
     */

    public static final int POWERUP_MARKER = 4;

    private final ObjectMap<Vector2, TrapType> trapLocations;
    private final ObjectMap<Vector2, PowerUpType> powerUpLocations;
    private final Random random;


    /**
     * Enumeration of trap types available in the game.
     * Each trap type has a specific tile ID, name, and damage value,
     * and implements its own effect behavior.
     */
    public enum TrapType {
        /**
         * Poison trap that deals low continuous damage
         */
        POISON(2080, "Poison", 5f) {
            @Override
            public void applyEffect(Player player) {
                player.takeDamage(damage);
                System.out.println("Poison trap applied " + damage + " damage");
            }
        },
        /**
         * Sting trap that deals medium damage
         */
        STING(2082, "Sting", 10) {
            @Override
            public void applyEffect(Player player) {
                player.takeDamage(damage);
                System.out.println("STING");
            }
        },
        /**
         * Heavy blow trap that deals high damage
         */
        HEAVY_BLOW(2085, "Heavy blow", 40) {
            @Override
            public void applyEffect(Player player) {
                player.takeDamage(damage);
                System.out.println("Death trap applied " + damage + " damage");
            }
        };

        /**
         * @return The tile ID used to render this trap type
         */
        public int getTileId() {
            return tileId;
        }

        /**
         * @return The display name of this trap type
         */
        public String getName() {
            return name;
        }

        /**
         * Applies the trap's effect to a player.
         *
         * @param player The player to affect
         */
        public abstract void applyEffect(Player player);

        private final int tileId;
        private final String name;
        protected final float damage;

        TrapType(int tileId, String name, float damage) {
            this.tileId = tileId;
            this.name = name;
            this.damage = damage;
        }

    }


    /**
     * Enumeration of power-up types available in the game.
     * Each power-up type has a specific tile ID and name,
     * and implements its own beneficial effect.
     */
    public enum PowerUpType {
        /**
         * Grants an additional dash ability to the player
         */
        GIVE_DASH(2333, "Give Dashes") {
            @Override
            public void applyEffect(Player player) {
                player.addTemporaryDashes(1);
                System.out.println("GIVEN A DASH");
                System.out.println("Amount of dashes: " + player.getTotalDashCharges());
            }
        },
        /**
         * Restores the player's health
         */
        HEALTH_PACK(2334, "Health Pack") {
            @Override
            public void applyEffect(Player player) {
                player.heal(10f);
                System.out.println("Health pack restored 25 health");
            }
        };

        /**
         * @return The tile ID used to render this power-up type
         */
        public int getTileId() {
            return tileId;
        }

        /**
         * @return The display name of this power-up type
         */
        public String getName() {
            return name;
        }

        /**
         * Applies the power-up's effect to a player.
         *
         * @param player The player to affect
         */
        public abstract void applyEffect(Player player);

        private final int tileId;
        private final String name;

        PowerUpType(int tileId, String name) {
            this.tileId = tileId;
            this.name = name;
        }


    }


    /**
     * Constructs a new TileEffectMNGR with empty trap and power-up maps.
     */
    public TileEffectMNGR() {
        this.trapLocations = new ObjectMap<>();
        this.powerUpLocations = new ObjectMap<>();
        this.random = new Random();
    }

    /**
     * Registers a power-up at the specified coordinates.
     * A random power-up type will be selected if the location isn't already occupied.
     *
     * @param x The x-coordinate of the power-up
     * @param y The y-coordinate of the power-up
     */
    public void registerPowerUp(int x, int y) {
        Vector2 position = new Vector2(x, y);
        if (!powerUpLocations.containsKey(position)) {
            PowerUpType[] powerUps = PowerUpType.values();
            PowerUpType randomPowerUp = powerUps[random.nextInt(powerUps.length)];
            powerUpLocations.put(position, randomPowerUp);
            System.out.println("Registered " + randomPowerUp.getName() + " at (" + x + "," + y + ")");
        }
    }

    /**
     * Gets the power-up type at the specified coordinates.
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return The PowerUpType at the location, or null if none exists
     */
    public PowerUpType getPowerUpAtLocation(int x, int y) {
        return powerUpLocations.get(new Vector2(x, y));
    }


    /**
     * Registers a trap at the specified coordinates.
     * A random trap type will be selected if the location isn't already occupied.
     *
     * @param x The x-coordinate of the trap
     * @param y The y-coordinate of the trap
     */
    public void registerTrapLocation(int x, int y) {
        Vector2 position = new Vector2(x, y);
        if (!trapLocations.containsKey(position)) {
            TrapType randomTrap = getRandomTrap();
            trapLocations.put(position, randomTrap);
            System.out.println("Registered new " + randomTrap.getName() + " trap at " + x + "," + y);
        }
    }

    /**
     * Gets the trap effect at the specified coordinates.
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return The TrapType at the location, or null if none exists
     */
    public TrapType getEffectAtLocation(int x, int y) {
        return trapLocations.get(new Vector2(x, y));
    }

    /**
     * Applies any effects at the player's current position.
     * Effects are consumed and removed after being applied.
     *
     * @param player The player to apply effects to
     */
    public void applyEffect(Player player) {
        int tileX = (int) (player.getPosition().x / 16);
        int tileY = (int) (player.getPosition().y / 16);
        Vector2 tilePos = new Vector2(tileX, tileY);

        TrapType trap = trapLocations.get(tilePos);
        if (trap != null) {
            System.out.println("Applying " + trap.getName() + " trap effect");
            trap.applyEffect(player);
            trapLocations.remove(tilePos); // Remove after triggering
        }

        PowerUpType powerUp = powerUpLocations.get(tilePos);
        if (powerUp != null) {
            System.out.println("Applying " + powerUp.getName() + " powerup effect");
            powerUp.applyEffect(player);
            powerUpLocations.remove(tilePos); // Remove after collecting
        }
    }


    /**
     * Checks for a trap at the specified position.
     *
     * @param position The position to check in world coordinates
     * @return The TrapType at the position, or null if none exists
     */
    public TrapType checkTrap(Vector2 position) {
        int tileX = (int) (position.x / 16);
        int tileY = (int) (position.y / 16);
        return getEffectAtLocation(tileX, tileY);
    }

    /**
     * Gets a random trap type from available trap types.
     *
     * @return A randomly selected TrapType
     */
    public static TrapType getRandomTrap() {
        TrapType[] traps = TrapType.values();
        return traps[new Random().nextInt(traps.length)];
    }

    /**
     * Gets a random power-up type from available power-up types.
     *
     * @return A randomly selected PowerUpType
     */
    public static PowerUpType getRandomPowerUp() {
        PowerUpType[] powerUps = PowerUpType.values();
        return powerUps[new Random().nextInt(powerUps.length)];
    }
}