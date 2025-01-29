package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import de.tum.cit.fop.maze.PC_NPC_OBJ.Player;

import java.util.Random;

public class TileEffectMNGR {
    public static final int TRAP_MARKER = 2;
    public static final int POWERUP_MARKER = 4;

    private final ObjectMap<Vector2, TrapType> trapLocations;
    private final ObjectMap<Vector2, PowerUpType> powerUpLocations;
    private final Random random;


    public interface TileEffect {
        String getName();

    }

    public enum TrapType {
        POISON(2080, "Poison", 5f) {
            @Override
            public void applyEffect(Player player) {
                player.takeDamage(damage);
                System.out.println("Poison trap applied " + damage + " damage");
            }
        },
        STING(2082, "Sting", 10) {
            @Override
            public void applyEffect(Player player) {
                player.takeDamage(damage);
                System.out.println("STING");
            }
        },
        HEAVY_BLOW(2085, "Heavy blow", 40) {
            @Override
            public void applyEffect(Player player) {
                player.takeDamage(damage);
                System.out.println("Death trap applied " + damage + " damage");
            }
        };

        private final int tileId;
        private final String name;
        protected final float damage;

        TrapType(int tileId, String name, float damage) {
            this.tileId = tileId;
            this.name = name;
            this.damage = damage;
        }

        public int getTileId() { return tileId; }
        public String getName() { return name; }
        public abstract void applyEffect(Player player);
    }


    public enum PowerUpType {
        GIVE_DASH(2333, "Give Dashes") {
            @Override
            public void applyEffect(Player player) {
                player.addTemporaryDashes(1);
                System.out.println("GIVEN A DASH");
                System.out.println("Amount of dashes: " + player.getTotalDashCharges());
            }
        },
        HEALTH_PACK(2334, "Health Pack") {
            @Override
            public void applyEffect(Player player) {
                player.heal(10f);
                System.out.println("Health pack restored 25 health");
            }
        };

        private final int tileId;
        private final String name;

        PowerUpType(int tileId, String name) {
            this.tileId = tileId;
            this.name = name;
        }

        public int getTileId() { return tileId; }
        public String getName() { return name; }
        public abstract void applyEffect(Player player);


    }



    public TileEffectMNGR() {
        this.trapLocations = new ObjectMap<>();
        this.powerUpLocations = new ObjectMap<>();
        this.random = new Random();
    }

    public void registerPowerUp(int x, int y) {
        Vector2 position = new Vector2(x, y);
        if (!powerUpLocations.containsKey(position)) {
            PowerUpType[] powerUps = PowerUpType.values();
            PowerUpType randomPowerUp = powerUps[random.nextInt(powerUps.length)];
            powerUpLocations.put(position, randomPowerUp);
            System.out.println("Registered " + randomPowerUp.getName() + " at (" + x + "," + y + ")");
        }
    }

    public PowerUpType getPowerUpAtLocation(int x, int y) {
        return powerUpLocations.get(new Vector2(x, y));
    }



    public void registerTrapLocation(int x, int y) {
        Vector2 position = new Vector2(x, y);
        if (!trapLocations.containsKey(position)) {
            TrapType randomTrap = getRandomTrap();
            trapLocations.put(position, randomTrap);
            System.out.println("Registered new " + randomTrap.getName() + " trap at " + x + "," + y);
        }
    }


    public TrapType getEffectAtLocation(int x, int y) {
        return trapLocations.get(new Vector2(x, y));
    }

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



    public TrapType checkTrap(Vector2 position) {
        int tileX = (int) (position.x / 16);
        int tileY = (int) (position.y / 16);
        return getEffectAtLocation(tileX, tileY);
    }

    public static TrapType getRandomTrap() {
        TrapType[] traps = TrapType.values();
        return traps[new Random().nextInt(traps.length)];
    }

    public static PowerUpType getRandomPowerUp() {
        PowerUpType[] powerUps = PowerUpType.values();
        return powerUps[new Random().nextInt(powerUps.length)];
    }

    public TrapType getTrapAt(int x, int y) {
        return trapLocations.get(new Vector2(x, y));
    }
    public PowerUpType getPowerUpAt(int x, int y) {
        return powerUpLocations.get(new Vector2(x, y));
}
}