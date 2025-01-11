package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
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

    public Enemy(float x, float y) {
        texture = new Texture("bush.png");
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
        MovementREQ request = null;
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
    public void render(SpriteBatch batch) {
        // used to rescale the texture of the player if needed(old approach --> KEEP JUST IN CASE)
//        float scaleWidth = texture.getWidth() * gameCONFIG.UNIT_SCALE;
//        float scaleHeight = texture.getHeight() * gameCONFIG.UNIT_SCALE;

        TextureRegion currentFrame;
        if (lookingDirection == 0) {
            currentFrame = animationMNGR.getBaldGuyDownAnimation().getKeyFrame(time, true);
        } else if (lookingDirection == 1) {
            currentFrame = animationMNGR.getBaldGuyRightAnimation().getKeyFrame(time, true);
        } else if (lookingDirection == 2) {
            currentFrame = animationMNGR.getBaldGuyUpAnimation().getKeyFrame(time, true);
        } else {
            currentFrame = animationMNGR.getBaldGuyLeftAnimation().getKeyFrame(time, true);
        }
        batch.draw(currentFrame, position.x, position.y);
    }


}
