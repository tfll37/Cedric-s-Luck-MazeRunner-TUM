package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;
import de.tum.cit.fop.maze.MAZELOGIC.Labyrinth;
import de.tum.cit.fop.maze.MAZELOGIC.MovementREQ;
import de.tum.cit.fop.maze.MAZELOGIC.MovementSYS;
import de.tum.cit.fop.maze.MAZELOGIC.TileEffectMNGR;


public class Player extends Actor {
    // Visualisation variables
    private final Texture texture;

    // Mechanics variables
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
    private int lookingDirection = 0;// 0 = up, 1 = right, 2 = down, 3 = left
    private boolean hitting;
    private final AnimationMNGR animationMNGR;
    private float health = 100f;
    private float damage = 10f;
    int dash_count = 5;
    private SpriteBatch batch;
    private final int dashCount;
    private boolean shootsFireball = false;
    private float fireballCooldown = 0; // Time remaining before next fireball can be shot
    private static final float FIREBALL_COOLDOWN_TIME = 3.0f;// Cooldown duration in
    private FireBall fireBall;


    public Player(float x, float y) {
        this.texture = new Texture("bush.png");
        this.position = new Vector2(x, y);
        this.startPosition = new Vector2(x, y);
        this.targetPosition = new Vector2(x, y);
        this.isMoving = false;
        this.timeAccumulation = 0f;
        this.baseSpeed = 1.0f;
        this.speedModifier = 1.0f;
        this.baseMoveTime = 0.07f;
        this.totalMoveTime = baseMoveTime;
        this.bounds = new Rectangle(x, y, 16, 16);
        this.lookingDirection = 0;
        this.hitting = false;
        this.animationMNGR = new AnimationMNGR();
        this.health = 100f;
        this.damage = 10f;
        this.dashCount = 100;

        animationMNGR.loadPlayerAnimations();
        this.fireBall = new FireBall(position.x, position.y);
    }

    public void update(float delta, float labyrinthWidth, float labyrinthHeight,
                       float tileWidth, float tileHeight, Labyrinth labyrinth, Enemy enemy) {

        time += delta;

        TileEffectMNGR trapManager = labyrinth.getTrapMNGR();
        TileEffectMNGR.TrapType trap = trapManager.checkTrap(position);
//        if (trap != null) {
//            trapManager.applyTrapEffect(this, trap);
//        }

        if (isMoving) {
        // Speed up the movement by reducing totalMoveTime
        totalMoveTime = 0.15f; // Adjust this value to control movement speed
            timeAccumulation += delta;
        float alpha = Math.min(timeAccumulation / totalMoveTime, 1.0f);

//            if (alpha > 1f) {
//                alpha = 1f;
//            }

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
        boolean overlaps = bounds.overlaps(enemy.getBounds());
        if (overlaps && this.hitting) {
            damage(enemy);
        }
        fireballCooldown -= delta;
        if (fireballCooldown < 0) {
            fireballCooldown = 0;
        }

    }

    private void updateMovement(float delta) {
        timeAccumulation += delta;
        float alpha = Math.min(timeAccumulation / totalMoveTime, 1f);

        position.x = startPosition.x + (targetPosition.x - startPosition.x) * alpha;
        position.y = startPosition.y + (targetPosition.y - startPosition.y) * alpha;

        if (alpha >= 1.0f) {
            isMoving = false;
        }
    }

    private void handleNewMovement(float delta, Labyrinth labyrinth, float tileWidth, float tileHeight) {
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
    }

    private MovementREQ handleInput() {
        var LEFT = Gdx.input.isKeyPressed(Input.Keys.A);
        var RIGHT = Gdx.input.isKeyPressed(Input.Keys.D);
        var DOWN = Gdx.input.isKeyPressed(Input.Keys.S);
        var UP = Gdx.input.isKeyPressed(Input.Keys.W);
        var DASH = Gdx.input.isKeyPressed(Input.Keys.X);
        var HIT = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        var ENTER = Gdx.input.isKeyPressed(Input.Keys.ENTER);


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

        if (DASH && lookingDirection == 0 && dash_count >= 0) {
            dash_count -= 1;
            return new MovementREQ(MovementREQ.MoveType.DASH, 0, 3);
        } else if (DASH && lookingDirection == 2 && dash_count > 0) {
            lookingDirection = 2;
            dash_count -= 1;
            return new MovementREQ(MovementREQ.MoveType.DASH, 0, -3);
        } else if (DASH && lookingDirection == 3 && dash_count > 0) {
            lookingDirection = 3;
            dash_count -= 1;
            return new MovementREQ(MovementREQ.MoveType.DASH, -3, 0);
        } else if (DASH && lookingDirection == 1 && dash_count > 0) {
            lookingDirection = 1;
            dash_count -= 1;
            return new MovementREQ(MovementREQ.MoveType.DASH, 3, 0);
        }
        if (HIT) {
            this.hitting = true;
        } else {
            this.hitting = false;
        }


        if (ENTER && fireballCooldown == 0) {
            System.out.println("Fireball activated");
            fireBall.appear = true;
            fireBall.direction = lookingDirection; // Set direction
            fireBall.x = position.x; // Initialize position
            fireBall.y = position.y;
            fireballCooldown = FIREBALL_COOLDOWN_TIME; // Reset cooldown
        }



        return null;

        // Example: Adjust game UI updates (placeholder logic for demonstration)
//        if (Gdx.input.isKeyPressed(Input.Keys.H)) gameUI.updateHealth(90); // Example health update
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) gameUI.updateScore(100); // Example score update
    }

    public void render(SpriteBatch batch) {
        // used to rescale the texture of the player if needed(old approach --> KEEP JUST IN CASE)
//        float scaleWidth = texture.getWidth() * gameCONFIG.UNIT_SCALE;
//        float scaleHeight = texture.getHeight() * gameCONFIG.UNIT_SCALE;

        TextureRegion currentFrame;
        if (hitting) {
            if (lookingDirection == 0) {
                currentFrame = animationMNGR.getCharacterUpHitAnimation().getKeyFrame(time, true);
            } else if (lookingDirection == 1) {
                currentFrame = animationMNGR.getCharacterRightHitAnimation().getKeyFrame(time, true);
            } else if (lookingDirection == 2) {
                currentFrame = animationMNGR.getCharacterDownHitAnimation().getKeyFrame(time, true);
            } else {
                currentFrame = animationMNGR.getCharacterLeftHitAnimation().getKeyFrame(time, true);
            }
        } else if (lookingDirection == 0) {
            currentFrame = animationMNGR.getCharacterUpAnimation().getKeyFrame(time, true);
        } else if (lookingDirection == 1) {
            currentFrame = animationMNGR.getCharacterRightAnimation().getKeyFrame(time, true);
        } else if (lookingDirection == 2) {
            currentFrame = animationMNGR.getCharacterDownAnimation().getKeyFrame(time, true);
        } else {
            currentFrame = animationMNGR.getCharacterLeftAnimation().getKeyFrame(time, true);
        }
        batch.draw(currentFrame, position.x, position.y);
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
        this.health -= damage;
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

    public void heal(float amount) {
        this.health += Math.min(100f, this.health + amount);
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

    public void dispose() {
        texture.dispose();
    }

    public boolean hits() {
        return this.hitting;
    }
    public FireBall getFireBall(){
        return fireBall;
    }
}
