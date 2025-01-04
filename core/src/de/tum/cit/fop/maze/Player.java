package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class Player extends Actor {
    // Visualisation variables
    private Texture texture;

    // Mechanics variables
    private Vector2 position;
    private Vector2 velocity;
    private float speed;


    public Player(float x, float y) {
        texture = new Texture("bush.png");
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        speed = 50f;
    }

    public void update(
            float delta,
            float labyrinthWidth,
            float labyrinthHeight,
            float tileWidth,
            float tileHeight,
            Labyrinth labyrinth
    ) {
        float newX = position.x + velocity.x * delta;
        float newY = position.y + velocity.y * delta;

        // Convert tile counts to total pixel dimensions:
        float maxWidth = labyrinthWidth * tileWidth;
        float maxHeight = labyrinthHeight * tileHeight;

        // 1. Check if new position is within labyrinth boundaries
        boolean withinBounds =
                (newX >= 0 && newX + texture.getWidth() <= maxWidth) &&
                        (newY >= 0 && newY + texture.getHeight() <= maxHeight);

        if (withinBounds) {
            // 2. Check if the tile is walkable (not blocked)
            if (!labyrinth.isBlocked(newX, newY)) {
                position.x = newX;
                position.y = newY;
            }
            // else do nothing (blocked tile)
        }
        // else out of bounds, do nothing
    }

//        position.add(velocity.x * delta, velocity.y * delta);


    public void render(SpriteBatch batch) {
        // used to rescale the texture of the player if needed(old approach --> KEEP JUST IN CASE)
//        float scaleWidth = texture.getWidth() * gameCONFIG.UNIT_SCALE;
//        float scaleHeight = texture.getHeight() * gameCONFIG.UNIT_SCALE;

        batch.draw(
                texture,
                position.x,
                position.y
        );
    }

    public void moveLeft() {
        velocity.x -= speed;
        velocity.y = 0;
    }
    public void moveRight() {
        velocity.x += speed;
        velocity.y = 0;
    }
    public void moveUp() {
        velocity.y += speed;
        velocity.x = 0;
    }
    public void moveDown() {
        velocity.y -= speed;
        velocity.x = 0;
    }
    public void stop() {
        velocity.setZero();
    }


    public void dispose() {
        texture.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
