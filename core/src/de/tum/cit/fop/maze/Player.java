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
        texture = new Texture("assets/coma2.png");
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        speed = 500f;
    }

    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
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
