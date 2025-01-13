package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class Enemy {
    private Texture texture;

    // Mechanics variables

    private Vector2 position;          // Current position of the enemy
    private Vector2 startPosition;     // Start position for current movement
    private Vector2 targetPosition;    // Target tile position
    private boolean isMoving;          // Whether the enemy is currently moving
    private float timeAccumulation;    // Time accumulator for interpolation
    private float totalMoveTime;       // Time to move one tile
    private List<Vector2> path;        // Path from the enemy to the player
    private float timeSinceLastUpdate; // Time since last pathfinding update
    private int lookingDirection;      // Direction the enemy is looking at
    private float time = 0f;
    private AnimationMNGR animationMNGR = new AnimationMNGR();
    private Vector2 lastKnownPlayerTile = new Vector2(-1, -1); // Initialize to an invalid position
    private float damage = 1f;
    private float health = 100f;
    private SpriteBatch batch;
    private boolean displayHitParticle = false;

    public Enemy(float x, float y) {
        this.position = new Vector2(x, y);
        this.startPosition = new Vector2(x, y);
        this.targetPosition = new Vector2(x, y);
        this.isMoving = false;
        this.timeAccumulation = 0f;
        this.totalMoveTime = 0.1f; // Time to move one tile (adjust for speed)
        this.path = new ArrayList<>();
        this.timeSinceLastUpdate = 0f;
        this.lookingDirection = 0;
        this.animationMNGR.loadAnimations();
    }
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

        time+=delta;
        timeSinceLastUpdate += delta;

        timeSinceLastUpdate += delta;
        this.batch = batch;
        // Track player's tile position
        Vector2 currentPlayerTile = player.getTilePosition(tileWidth, tileHeight);
        boolean overlap = player.getBounds().overlaps(getBounds());
        if (overlap) {

            displayHitParticle = true;
            damage(player);
        }
        else {
            displayHitParticle = false;
        }

        if (!currentPlayerTile.equals(lastKnownPlayerTile)) {
            // Recalculate path if player has moved
            Vector2 enemyTile = new Vector2((int) (position.x / tileWidth), (int) (position.y / tileHeight));
            path = Pathfinding.findPath(maze, enemyTile, currentPlayerTile);
            lastKnownPlayerTile.set(currentPlayerTile);
            timeSinceLastUpdate = 0f; // Reset pathfinding timer
        }

        if (isMoving) {
            timeAccumulation += delta;
            float alpha = timeAccumulation / totalMoveTime;

            if (alpha > 1f) alpha = 1f;

            // Interpolate position
            position.x = startPosition.x + (targetPosition.x - startPosition.x) * alpha;
            position.y = startPosition.y + (targetPosition.y - startPosition.y) * alpha;

            // If movement to the target is complete
            if (alpha >= 1.0f) {
                isMoving = false;
            }

            return; // Wait for the movement to complete before calculating the next step
        }

        // If not moving, calculate the next target
        if (!path.isEmpty() && !isMoving) {
            Vector2 nextTile = path.remove(0); // Get the next tile in the path
            startPosition.set(position);      // Set the start position
            targetPosition.set(nextTile.x * tileWidth, nextTile.y * tileHeight); // Convert tile to world position
            isMoving = true;                 // Start moving
            timeAccumulation = 0f;           // Reset interpolation
            return;
        }

        // Recalculate path if no path is available and not moving
        if (path.isEmpty() && timeSinceLastUpdate >= 1.0f) {
            Vector2 enemyTile = new Vector2((int) (position.x / tileWidth), (int) (position.y / tileHeight));
            Vector2 playerTile = new Vector2((int) (player.getPosition().x / tileWidth), (int) (player.getPosition().y / tileHeight));

            path = Pathfinding.findPath(maze, enemyTile, playerTile);

            timeSinceLastUpdate = 0f;
        }
    }

    public void render(SpriteBatch batch) {
        // Render the enemy
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
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, 16, 16);
    }
    private void damage(Player player) {
        player.takeDamage(damage);

    }
    public boolean isDisplayHitParticle() {
        return displayHitParticle;
    }
}




