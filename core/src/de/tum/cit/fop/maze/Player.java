package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class Player extends Actor {
    // Visualisation variables
    private Texture texture;

    // Mechanics variables
    private Vector2 position;
    private Vector2 startPosition;
    private Vector2 targetPosition;
    private boolean isMoving;
    private float timeAccumulation;
    private float time;
    private float totalMoveTime;
    private Rectangle bounds;
    private int lookingDirection = 0;  // 0 = up, 1 = right, 2 = down, 3 = left
    private AnimationMNGR animationMNGR;
    private float health = 100f;
    private float damage = 10f;
    private SpriteBatch batch;

    public Player(float x, float y) {
        this.texture = new Texture("bush.png");
        this.position = new Vector2(x, y);
        this.startPosition = new Vector2(x, y);
        this.targetPosition = new Vector2(x, y);
        this.isMoving = false;
        this.timeAccumulation = 0f;
        this.totalMoveTime = 0.07f;

        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        this.animationMNGR = new AnimationMNGR();
        animationMNGR.loadPlayerAnimations();
    }

    public void update(
            float delta,
            float labyrinthWidth,
            float labyrinthHeight,
            float tileWidth,
            float tileHeight,
            Labyrinth labyrinth
    ) {
        time += delta;
        if (isMoving) {
            timeAccumulation += delta;
            float alpha = timeAccumulation / totalMoveTime;

            if (alpha > 1f) {
                alpha = 1f;
            }

            position.x = startPosition.x + (targetPosition.x - startPosition.x) * alpha;
            position.y = startPosition.y + (targetPosition.y - startPosition.y) * alpha;

            if (alpha >= 1.0f) {
                isMoving = false;
            }

            bounds.setPosition(position.x, position.y);
            return;
        }
        MovementREQ request = handleInput();
        if (request != null) {
            Vector2 newPixelPos = MovementSYS.processMovement(
                    position,      // current pixel pos
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
    }

    private MovementREQ handleInput() {
        var LEFT = Gdx.input.isKeyPressed(Input.Keys.A);
        var RIGHT = Gdx.input.isKeyPressed(Input.Keys.D);
        var DOWN = Gdx.input.isKeyPressed(Input.Keys.S);
        var UP = Gdx.input.isKeyPressed(Input.Keys.W);

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
        if (lookingDirection == 0) {
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
        health -= damage;
        //System.out.println("Player pos" + position);
        //System.out.println("Player bounds" + this.getBounds());

        //batch.draw(animationMNGR.getHitAnimation1().getKeyFrame(time, true), position.x +703, position.y + 278);
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
}
