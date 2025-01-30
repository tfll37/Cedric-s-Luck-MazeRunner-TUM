package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;
import de.tum.cit.fop.maze.MAZELOGIC.Labyrinth;
import de.tum.cit.fop.maze.MAZELOGIC.Pathfinding;

import java.util.ArrayList;
import java.util.List;

import static de.tum.cit.fop.maze.MAZELOGIC.gameCONFIG.WALK_MOVE_TIME;


/**
 * The type Enemy.
 */
public class Enemy {
    private Texture texture;
    private Vector2 position;
    private Vector2 startPosition;
    private Vector2 targetPosition;
    private boolean isMoving;
    private float timeAccumulation;
    private float baseMoveTime;        // Base time to move one tile
    private float totalMoveTime;       // Actual time to move one tile (affected by modifiers)
    private float baseSpeed;           // Base movement speed
    private float speedModifier;       // Speed modification factor
    private List<Vector2> path;
    private float timeSinceLastUpdate;
    private int lookingDirection;
    private float time = 0f;
    private AnimationMNGR animationMNGR = new AnimationMNGR();
    private Vector2 lastKnownPlayerTile = new Vector2(-1, -1);
    private float damage = 1f;
    private float health = 100f;
    private SpriteBatch batch;
    private boolean displayHitParticle = false;
    private boolean alive = true;
    private boolean counted = false;


    /**
     * Instantiates a new Enemy.
     *
     * @param x the x
     * @param y the y
     */
    public Enemy(float x, float y) {
        this.position = new Vector2(x, y);
        this.startPosition = new Vector2(x, y);
        this.targetPosition = new Vector2(x, y);
        this.isMoving = false;
        this.timeAccumulation = 0f;
        this.baseSpeed = 1.0f;
        this.speedModifier = 1.0f;
        this.baseMoveTime = WALK_MOVE_TIME;  // Use same base move time as player
        this.totalMoveTime = baseMoveTime;
        this.path = new ArrayList<>();
        this.timeSinceLastUpdate = 0f;
        this.lookingDirection = 0;
        this.animationMNGR.loadAnimations();
    }


    /**
     * Update.
     *
     * @param delta           the delta
     * @param labyrinthWidth  the labyrinth width
     * @param labyrinthHeight the labyrinth height
     * @param tileWidth       the tile width
     * @param tileHeight      the tile height
     * @param labyrinth       the labyrinth
     * @param player          the player
     * @param maze            the maze
     */
    public void update(
            float delta,
            float labyrinthWidth,
            float labyrinthHeight,
            float tileWidth,
            float tileHeight,
            Labyrinth labyrinth,
            Player player,
            Array<Array<Integer>> maze
    ) {
        if (this.alive) {
            time += delta;
            timeSinceLastUpdate += delta;

            // Track player's tile position
            Vector2 currentPlayerTile = player.getTilePosition(tileWidth, tileHeight);
            boolean overlap = player.getBounds().overlaps(getBounds());
            if (overlap) {
                displayHitParticle = true;
                damage(player);
            } else {
                displayHitParticle = false;
            }

            if ((!isMoving || path.isEmpty()) && !currentPlayerTile.equals(lastKnownPlayerTile)) {
                Vector2 enemyTile = new Vector2((int) (position.x / tileWidth), (int) (position.y / tileHeight));
                List<Vector2> newPath = Pathfinding.findPath(maze, enemyTile, currentPlayerTile);
                // Only update path if we're not moving or new path is significantly better
                if (!isMoving || (newPath.size() < path.size() - 1)) {
                    path = newPath;
                lastKnownPlayerTile.set(currentPlayerTile);
                timeSinceLastUpdate = 0f;
            }
            }

            if (isMoving) {
                timeAccumulation += delta;
                float alpha = Math.min(timeAccumulation / totalMoveTime, 1.0f);

                position.x = startPosition.x + (targetPosition.x - startPosition.x) * alpha;
                position.y = startPosition.y + (targetPosition.y - startPosition.y) * alpha;

                if (alpha >= 1.0f) {
                    isMoving = false;
                    position.set(targetPosition);

                    if (!path.isEmpty()) {
                        Vector2 nextTile = path.remove(0);
                        startPosition.set(position);
                        targetPosition.set(nextTile.x * tileWidth, nextTile.y * tileHeight);
                        isMoving = true;
                        timeAccumulation = 0f;
                        updateLookingDirection();
                }

                }
                return;
            }

            if (!path.isEmpty() && !isMoving) {
                Vector2 nextTile = path.remove(0);
                startPosition.set(position);
                targetPosition.set(nextTile.x * tileWidth, nextTile.y * tileHeight);
                isMoving = true;
                timeAccumulation = 0f;
                updateLookingDirection();
                return;
            }

            if (path.isEmpty() && timeSinceLastUpdate >= 1.0f) {
                Vector2 enemyTile = new Vector2((int) (position.x / tileWidth), (int) (position.y / tileHeight));
                Vector2 playerTile = new Vector2((int) (player.getPosition().x / tileWidth), (int) (player.getPosition().y / tileHeight));

                path = Pathfinding.findPath(maze, enemyTile, playerTile);

                timeSinceLastUpdate = 0f;
            }
        }
    }

    /**
     * Modify speed.
     *
     * @param factor the factor
     */
    public void modifySpeed(float factor) {
        this.speedModifier = factor;
        this.totalMoveTime = baseMoveTime / speedModifier;
    }

    private void updateLookingDirection() {
        if (targetPosition.x > startPosition.x) lookingDirection = 1;      // Right
        else if (targetPosition.x < startPosition.x) lookingDirection = 3; // Left
        else if (targetPosition.y > startPosition.y) lookingDirection = 0; // Up
        else lookingDirection = 2;                                         // Down
    }


    /**
     * Render.
     *
     * @param batch the batch
     */
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentAnimationFrame();
        batch.draw(currentFrame, position.x, position.y, 16, 16);
    }


    private TextureRegion getCurrentAnimationFrame() {
        if (targetPosition.x > startPosition.x) {
            return animationMNGR.getBaldGuyRightAnimation().getKeyFrame(timeAccumulation, true); // Moving Right
        } else if (targetPosition.x < startPosition.x) {
            return animationMNGR.getBaldGuyLeftAnimation().getKeyFrame(timeAccumulation, true); // Moving Left
        } else if (targetPosition.y > startPosition.y) {
            return animationMNGR.getBaldGuyUpAnimation().getKeyFrame(timeAccumulation, true); // Moving Up
        } else {
            return animationMNGR.getBaldGuyDownAnimation().getKeyFrame(timeAccumulation, true); // Moving Down
        }
    }


    /**
     * Gets bounds.
     *
     * @return the bounds
     */
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, 16, 16);
    }

    /**
     * Count.
     */
    public void count(){
        counted = true;
    }

    /**
     * Is counted boolean.
     *
     * @return the boolean
     */
    public boolean isCounted()
    {
        return counted;
    }

    private void damage(Player player) {
        player.takeDamage(damage);
    }


    /**
     * Take damage.
     *
     * @param damage the damage
     */
    public void takeDamage(float damage) {
        this.health -= damage;
    }


    /**
     * Gets life status.
     *
     * @return the life status
     */
    public boolean getLifeStatus() {
        if (this.health <= 0) {
            this.alive = false;
        }
        return this.alive;
    }


    /**
     * Is display hit particle boolean.
     *
     * @return the boolean
     */
    public boolean isDisplayHitParticle() {
        if (!this.alive) {
            displayHitParticle = false;
        }
        return displayHitParticle;
    }
}