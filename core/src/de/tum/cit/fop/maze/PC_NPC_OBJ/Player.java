package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;
import de.tum.cit.fop.maze.MAZELOGIC.*;

import static de.tum.cit.fop.maze.MAZELOGIC.gameCONFIG.WALK_MOVE_TIME;

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
    private CameraMNGR cameraMNGR;

    private float health = 100f;
    private float damage = 10f;
    private int dashCount;
    private boolean shootsFireball = false;
    private float fireballCooldown = 0;
    private static final float FIREBALL_COOLDOWN_TIME = 3.0f;
    private FireBall fireBall;
    private boolean isRunning;
    private static final float SPRINT_SPEED_MULTIPLIER = 1.55f;
    private static final float MAX_STAMINA = 100f;
    private static final float STAMINA_DRAIN_RATE = 50f;
    private static final float STAMINA_REGEN_RATE = 20f;
    private static final float STAMINA_REGEN_DELAY = 1.5f;
    private float currentStamina;
    private float staminaRegenTimer;
    private boolean canSprint;

    private int maxDashCharges = 3;
    private int dashCharges = maxDashCharges;
    private float dashCooldown = 0f;
    private static final float DASH_COOLDOWN_TIME = 5f;
    private static final float DASH_DISTANCE = 4f;
    private boolean isDashing = false;
    private float dashInvulnerabilityTimer = 0f;
    private static final float DASH_INVULNERABILITY_DURATION = 0.5f;
    private int temporaryDashes = 0;


    private float damageFlashDuration = 0f;
    private static final float MAX_FLASH_DURATION = 1.0f;
    private static final Color DAMAGE_FLASH_COLOR = new Color(1, 0, 0, 0.5f);
    private Color currentTint = new Color(1, 1, 1, 1);

    public Player(float x, float y, CameraMNGR cameraMNGR) {
        this.cameraMNGR = cameraMNGR;
        this.position = new Vector2(x, y);
        this.startPosition = new Vector2(x, y);
        this.targetPosition = new Vector2(x, y);
        this.isMoving = false;
        this.timeAccumulation = 0f;
        this.baseSpeed = 1.0f;
        this.speedModifier = 1.0f;
        this.baseMoveTime = WALK_MOVE_TIME;
        this.totalMoveTime = baseMoveTime;
        this.bounds = new Rectangle(x, y, 16, 16);
        this.hitting = false;
        this.animationMNGR = new AnimationMNGR();
        this.dashCount = 3;
        this.animationMNGR.loadPlayerAnimations();
        this.fireBall = new FireBall(position.x, position.y);

        this.isRunning = false;
        this.currentStamina = MAX_STAMINA;
        this.staminaRegenTimer = 5f;
        this.canSprint = true;

        this.cameraMNGR = cameraMNGR;

        this.dashCharges = maxDashCharges;

        this.currentTint.set(1, 1, 1, 1);

    }

    public void update(float delta, float labyrinthWidth, float labyrinthHeight,
                       float tileWidth, float tileHeight, Labyrinth labyrinth, Array<Enemy> enemies) {
        time += delta;

        updateStamina(delta);
        updateDash(delta);

        TileEffectMNGR trapManager = labyrinth.getTrapMNGR();
        TileEffectMNGR.TrapType trap = trapManager.checkTrap(position);
        int tileX = (int) (position.x / 16);
        int tileY = (int) (position.y / 16);

        if (trap != null) {
            Gdx.app.log("Player", "Stepped on trap: " + trap.getName());

            trapManager.applyEffect(this);
        }
        TileEffectMNGR.PowerUpType powerUp = trapManager.getPowerUpAtLocation(tileX, tileY);

        if (powerUp != null) {
            trapManager.applyEffect(this);

        }

        if (isMoving) {
            float currentMoveTime = (isRunning && canSprint) ?
                    totalMoveTime / SPRINT_SPEED_MULTIPLIER : totalMoveTime;

            timeAccumulation += delta;
            float alpha = Math.min(timeAccumulation / currentMoveTime, 1.0f);

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

        if (damageFlashDuration > 0) {
            damageFlashDuration -= delta;
            // Calculate flash intensity
            float flashIntensity = damageFlashDuration / MAX_FLASH_DURATION;
            currentTint.set(1, 1 - flashIntensity * 0.5f, 1 - flashIntensity * 0.5f, 1);
        } else {
            currentTint.set(1, 1, 1, 1); // Reset to normal color
        }
    }

    private void updateDash(float delta) {
        if (dashCooldown > 0) {
            dashCooldown -= delta;
            if (dashCooldown <= 0 && dashCharges < maxDashCharges) {
                dashCharges++;
                if (dashCharges < maxDashCharges) {
                    dashCooldown = DASH_COOLDOWN_TIME;
                }
            }
        }

        if (dashInvulnerabilityTimer > 0) {
            dashInvulnerabilityTimer -= delta;
            isDashing = dashInvulnerabilityTimer > 0;
        }
    }

    private void updateStamina(float delta) {
        if (isRunning && canSprint) {
            currentStamina -= STAMINA_DRAIN_RATE * delta;
            staminaRegenTimer = STAMINA_REGEN_DELAY;

            if (currentStamina <= 0) {
                currentStamina = 0;
                canSprint = false;
            }
        } else {
            if (staminaRegenTimer > 0) {
                staminaRegenTimer -= delta;
            } else {
                currentStamina += STAMINA_REGEN_RATE * delta;
                if (currentStamina >= MAX_STAMINA) {
                    currentStamina = MAX_STAMINA;
                    canSprint = true;
                }
            }
        }
    }


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

        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && getTotalDashCharges() > 0) {
            if (temporaryDashes > 0) {
                temporaryDashes--;
            } else {
                dashCharges--;
            }

            if (dashCharges < maxDashCharges && dashCooldown <= 0) {
                dashCooldown = DASH_COOLDOWN_TIME;
            }

            isDashing = true;
            dashInvulnerabilityTimer = DASH_INVULNERABILITY_DURATION;

            // Return dash movement based on looking direction
            switch (lookingDirection) {
                case 0: // Up
                    return new MovementREQ(MovementREQ.MoveType.DASH, 0, (int) DASH_DISTANCE);
                case 1: // Right
                    return new MovementREQ(MovementREQ.MoveType.DASH, (int) DASH_DISTANCE, 0);
                case 2: // Down
                    return new MovementREQ(MovementREQ.MoveType.DASH, 0, (int) -DASH_DISTANCE);
                case 3: // Left
                    return new MovementREQ(MovementREQ.MoveType.DASH, (int) -DASH_DISTANCE, 0);
            }
        }

        return null;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;
        float animationSpeed = time * ((isRunning && canSprint) ? 1.5f : 1.0f);

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

    Color prevColor = batch.getColor().cpy();
    batch.setColor(currentTint);

    float offsetX = -8f; // (32 - 16) / 2 = 8
    float offsetY = -8f; // (32 - 16) / 2 = 8

    batch.draw(currentFrame, position.x + offsetX, position.y + offsetY);
        batch.setColor(prevColor);

    }

    // Getter for stamina (useful for UI)
    public float getCurrentStamina() {
        return currentStamina;
    }

    public float getMaxStamina() {
        return MAX_STAMINA;
    }


    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getTilePosition(float tileWidth, float tileHeight) {
        return new Vector2((int) (position.x / tileWidth), (int) (position.y / tileHeight));
    }

    public float getHealth() {
        return health;
    }

    public void takeDamage(float damage) {
        if (!isDashing) {  // Only take damage if not dashing
            this.health -= damage;
            this.health = Math.max(this.health, 0);

            damageFlashDuration = MAX_FLASH_DURATION;


            if (damage <= 10) {
                cameraMNGR.startLightShake();
            } else if (damage > 10 && damage <= 50) {
                cameraMNGR.startShake();
            } else if (damage > 50) {
                cameraMNGR.startHeavyShake();
            }
        }
    }

    public void addTemporaryDashes(int amount) {
        temporaryDashes += amount;
        System.out.println("Added " + amount + " temporary dashes. Total temporary dashes: " + temporaryDashes);
    }

    public int getBaseDashCharges() {
        return dashCharges;
    }

    public int getTemporaryDashes() {
        return temporaryDashes;
    }

    public int getTotalDashCharges() {
        return dashCharges + temporaryDashes;
    }


    public void increaseHealth(float health) {
        this.health += health;
    }


    public void modifySpeed(float factor) {
        this.speedModifier = factor;
        this.totalMoveTime = baseMoveTime / speedModifier;
    }


    public void setSpeedModifier(float modifier) {
        this.speedModifier = modifier;
        this.totalMoveTime = baseMoveTime / speedModifier;
    }


    public float getSpeedModifier() {
        return speedModifier;
    }


    public void damage(Enemy enemy) {
        enemy.takeDamage(damage);
    }


    public boolean isDead() {
        return health <= 0;
    }


    public void heal(float amount) {
        this.health = Math.min(200, this.health + amount);
    }


    public boolean shootsFireBall() {
        return shootsFireball;
    }


    public int getOrientation() {
        return lookingDirection;
    }


    public void setPosition(Vector2 position) {
        this.position.set(position);
        this.startPosition.set(position);
        this.targetPosition.set(position);
        this.bounds.setPosition(position.x, position.y);
    }


    public boolean hits() {
        return this.hitting;
    }


    public void giveDashes() {
        this.dashCount += 5;
    }


    public FireBall getFireBall() {
        return fireBall;
    }

    public int getDashCharges() {
        return dashCharges;
    }

    public float getDashCooldown() {
        return dashCooldown;
    }

    public int getMaxDashCharges() {
        return maxDashCharges;
    }

    public void addDashCharge() {
        if (dashCharges < maxDashCharges) {
            dashCharges++;
        }
    }

    public void resetDashCooldown() {
        dashCooldown = 0f;
    }

    public boolean isDashing() {
        return isDashing;
    }

    public int getDashCount() {
        return dashCount;
    }
}