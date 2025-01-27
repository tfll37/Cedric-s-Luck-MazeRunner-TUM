package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;
import de.tum.cit.fop.maze.MAZELOGIC.*;

import static de.tum.cit.fop.maze.MAZELOGIC.gameCONFIG.RUN_MOVE_TIME;
import static de.tum.cit.fop.maze.MAZELOGIC.gameCONFIG.WALK_MOVE_TIME;

/**
 * Represents the player character in the maze game.
 * The player can move, interact with the environment, and attack enemies.
 */
public class Player extends Actor {
    private final Vector2 position;
    private final Vector2 startPosition;
    private final Vector2 targetPosition;
    private boolean isMoving;
    private float timeAccumulation;
    private float time;
    private float baseSpeed;
    private float speedModifier;
    private float baseMoveTime;
    private float totalMoveTime;
    private final Rectangle bounds;
    private int lookingDirection = 0; // 0 = up, 1 = right, 2 = down, 3 = left
    private boolean hitting;
    private final AnimationMNGR animationMNGR;
    private float health = 100f;
    private float damage = 10f;
    private int dashCount;
    private boolean shootsFireball = false;
    private float fireballCooldown = 0; // Time remaining before next fireball can be shot
    private static final float FIREBALL_COOLDOWN_TIME = 3.0f; // Cooldown duration in seconds
    private FireBall fireBall;
    private boolean isRunning;

    /**
     * Creates a new player at the specified position.
     *
     * @param x the x-coordinate of the player's initial position
     * @param y the y-coordinate of the player's initial position
     */
    public Player(float x, float y) {
        this.position = new Vector2(x, y);
        this.startPosition = new Vector2(x, y);
        this.targetPosition = new Vector2(x, y);
        this.isMoving = false;
        this.timeAccumulation = 0f;
        this.baseSpeed = 1.0f;
        this.speedModifier = 2.0f;
        this.baseMoveTime = 0.07f;
        this.totalMoveTime = baseMoveTime;
        this.bounds = new Rectangle(x, y, 16, 16);
        this.hitting = false;
        this.animationMNGR = new AnimationMNGR();
        this.dashCount = 10;
        this.animationMNGR.loadPlayerAnimations();
        this.fireBall = new FireBall(position.x, position.y);
    }

    /**
     * Updates the player's state, including movement, interaction with traps and power-ups, and attacking enemies.
     *
     * @param delta            the time in seconds since the last update
     * @param labyrinthWidth   the width of the labyrinth
     * @param labyrinthHeight  the height of the labyrinth
     * @param tileWidth        the width of a single tile
     * @param tileHeight       the height of a single tile
     * @param labyrinth        the labyrinth object
     * @param enemies          the array of enemies
     */
    public void update(float delta, float labyrinthWidth, float labyrinthHeight,
                       float tileWidth, float tileHeight, Labyrinth labyrinth, Array<Enemy> enemies) {
        time += delta;

        TileEffectMNGR trapManager = labyrinth.getTrapMNGR();
        TileEffectMNGR.TrapType trap = trapManager.checkTrap(position);
        int tileX = (int) (position.x / 16); // or tileWidth
        int tileY = (int) (position.y / 16); // or tileHeight

        if (trap != null) {
            Gdx.app.log("Player", "Stepped on trap: " + trap.getName());

            trapManager.applyEffect(this);
        }
        TileEffectMNGR.PowerUpType powerUp = trapManager.getPowerUpAtLocation(tileX, tileY);

        if (powerUp != null) {
            // Actually apply the effect:
            trapManager.applyEffect(this);

            // If you only want to pick it up once, remove it:
            // trapManager.removePowerUp(tileX, tileY);
        }

        if (isMoving) {
            totalMoveTime = 0.15f;
            timeAccumulation += delta;
            float alpha = Math.min(timeAccumulation / totalMoveTime, 1.0f);

            position.x = startPosition.x + (targetPosition.x - startPosition.x) * alpha;
            position.y = startPosition.y + (targetPosition.y - startPosition.y) * alpha;

            if (alpha >= 1.0f) {
                isMoving = false;
                position.set(targetPosition);
            }

            bounds.setPosition(position.x, position.y);
            return;
        }
        MovementREQ request = handleInput();
        if (request != null) {
            Vector2 newPixelPos = MovementSYS.processMovement(
                    position,
                    labyrinth,
                    tileWidth,
                    tileHeight,
                    request
            );

            if (!newPixelPos.epsilonEquals(position, 0.0001f)) {
                startPosition.set(position);
                targetPosition.set(newPixelPos);
                isMoving = true;
                timeAccumulation = 0f;
            }
        }
        bounds.setPosition(position.x, position.y);
        for (int i = 0; i < enemies.size; i++) {
            Enemy enemy = enemies.get(i);
            boolean overlaps = bounds.overlaps(enemy.getBounds());
            if (overlaps && this.hitting) {
                damage(enemy);
            }
        }
        fireballCooldown -= delta;
        if (fireballCooldown < 0) {
            fireballCooldown = 0;
        }
    }

    /**
     * Handles the player's input and returns the corresponding movement request.
     *
     * @return the movement request based on the player's input
     */
    private MovementREQ handleInput() {
        var LEFT = Gdx.input.isKeyPressed(Input.Keys.A);
        var RIGHT = Gdx.input.isKeyPressed(Input.Keys.D);
        var DOWN = Gdx.input.isKeyPressed(Input.Keys.S);
        var UP = Gdx.input.isKeyPressed(Input.Keys.W);
        var DASH = Gdx.input.isKeyPressed(Input.Keys.X);
        var HIT = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        var ENTER = Gdx.input.isKeyPressed(Input.Keys.ENTER);
        var RUN = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        isRunning = RUN;
        totalMoveTime = isRunning ? RUN_MOVE_TIME : WALK_MOVE_TIME;

        if (UP) {
            lookingDirection = 0;
            return new MovementREQ(MovementREQ.MoveType.STEP, 0, 1);
        } else if (DOWN) {
            lookingDirection = 2;
            return new MovementREQ(MovementREQ.MoveType.STEP, 0, -1);
        } else if (LEFT) {
            lookingDirection = 3;
            return new MovementREQ(MovementREQ.MoveType.STEP, -1, 0);
        } else if (RIGHT) {
            lookingDirection = 1;
            return new MovementREQ(MovementREQ.MoveType.STEP, 1, 0);
        }

        if (DASH && lookingDirection == 0 && dashCount >= 0) {
            dashCount -= 1;
            return new MovementREQ(MovementREQ.MoveType.DASH, 0, 3);
        } else if (DASH && lookingDirection == 2 && dashCount > 0) {
            dashCount -= 1;
            return new MovementREQ(MovementREQ.MoveType.DASH, 0, -3);
        } else if (DASH && lookingDirection == 3 && dashCount > 0) {
            dashCount -= 1;
            return new MovementREQ(MovementREQ.MoveType.DASH, -3, 0);
        } else if (DASH && lookingDirection == 1 && dashCount > 0) {
            dashCount -= 1;
            return new MovementREQ(MovementREQ.MoveType.DASH, 3, 0);
        }
        if (HIT) {
            this.hitting = true;
        } else {
            this.hitting = false;
        }

        if (ENTER && fireballCooldown == 0) {
            fireBall.appear = true;
            fireBall.direction = lookingDirection; // Set direction
            fireBall.x = position.x; // Initialize position
            fireBall.y = position.y;
            fireballCooldown = FIREBALL_COOLDOWN_TIME; // Reset cooldown
        }

        return null;
    }

    /**
     * Renders the player on the screen.
     *
     * @param batch the SpriteBatch used for drawing
     */
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;
        float animationSpeed = time * (isRunning ? 1.5f : 1.0f);

        if (hitting) {
            if (lookingDirection == 0) {
                currentFrame = animationMNGR.getCharacterUpHitAnimation().getKeyFrame(animationSpeed, true);
            } else if (lookingDirection == 1) {
                currentFrame = animationMNGR.getCharacterRightHitAnimation().getKeyFrame(animationSpeed, true);
            } else if (lookingDirection == 2) {
                currentFrame = animationMNGR.getCharacterDownHitAnimation().getKeyFrame(animationSpeed, true);
            } else {
                currentFrame = animationMNGR.getCharacterLeftHitAnimation().getKeyFrame(animationSpeed, true);
            }
        } else if (lookingDirection == 0) {
            currentFrame = animationMNGR.getCharacterUpAnimation().getKeyFrame(animationSpeed, true);
        } else if (lookingDirection == 1) {
            currentFrame = animationMNGR.getCharacterRightAnimation().getKeyFrame(animationSpeed, true);
        } else if (lookingDirection == 2) {
            currentFrame = animationMNGR.getCharacterDownAnimation().getKeyFrame(animationSpeed, true);
        } else {
            currentFrame = animationMNGR.getCharacterLeftAnimation().getKeyFrame(animationSpeed, true);
        }

        batch.draw(currentFrame, position.x, position.y);
    }

    /**
     * Retrieves the player's bounding rectangle.
     *
     * @return the bounding rectangle
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Retrieves the player's position.
     *
     * @return the player's position as a Vector2
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Retrieves the player's tile position.
     *
     * @param tileWidth  the width of a single tile
     * @param tileHeight the height of a single tile
     * @return the tile position as a Vector2
     */
    public Vector2 getTilePosition(float tileWidth, float tileHeight) {
        return new Vector2((int) (position.x / tileWidth), (int) (position.y / tileHeight));
    }

    /**
     * Retrieves the player's health.
     *
     * @return the player's health
     */
    public float getHealth() {
        return health;
    }

    /**
     * Reduces the player's health by the specified damage amount.
     *
     * @param damage the amount of damage to inflict
     */
    public void takeDamage(float damage) {
        this.health -= damage;
        this.health = Math.max(this.health, 0);

    }

    /**
     * Increases the player's health by the specified amount.
     *
     * @param health the amount of health to add
     */
    public void increaseHealth(float health) {
        this.health += health;
    }

    /**
     * Modifies the player's speed by the specified factor.
     *
     * @param factor the speed modification factor
     */
    public void modifySpeed(float factor) {
        this.speedModifier = factor;
        this.totalMoveTime = baseMoveTime / speedModifier;
    }

    /**
     * Sets the player's speed modifier.
     *
     * @param modifier the speed modifier
     */
    public void setSpeedModifier(float modifier) {
        this.speedModifier = modifier;
        this.totalMoveTime = baseMoveTime / speedModifier;
    }

    /**
     * Retrieves the player's speed modifier.
     *
     * @return the speed modifier
     */
    public float getSpeedModifier() {
        return speedModifier;
    }

    /**
     * Inflicts damage on the specified enemy.
     *
     * @param enemy the enemy to damage
     */
    public void damage(Enemy enemy) {
        enemy.takeDamage(damage);
    }

    /**
     * Checks whether the player is dead.
     *
     * @return true if the player is dead; false otherwise
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Heals the player by the specified amount.
     *
     * @param amount the amount of health to restore
     */
    public void heal(float amount) {
        this.health = Math.min(200, this.health + amount);
    }

    /**
     * Checks whether the player can shoot a fireball.
     *
     * @return true if the player can shoot a fireball; false otherwise
     */
    public boolean shootsFireBall() {
        return shootsFireball;
    }

    /**
     * Retrieves the player's current orientation.
     *
     * @return the orientation as an integer (0 = up, 1 = right, 2 = down, 3 = left)
     */
    public int getOrientation() {
        return lookingDirection;
    }

    /**
     * Sets the player's position.
     *
     * @param position the new position as a Vector2
     */
    public void setPosition(Vector2 position) {
        this.position.set(position);
        this.startPosition.set(position);
        this.targetPosition.set(position);
        this.bounds.setPosition(position.x, position.y);
    }

    /**
     * Checks whether the player is hitting.
     *
     * @return true if the player is hitting; false otherwise
     */
    public boolean hits() {
        return this.hitting;
    }


    public void giveDashes(){
        this.dashCount += 5;
    }

    /**
     * Retrieves the player's fireball.
     *
     * @return the fireball object
     */
    public FireBall getFireBall() {
        return fireBall;
    }
    public int getDashCount(){
        return dashCount;
    }
}