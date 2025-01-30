package de.tum.cit.fop.maze.PC_NPC_OBJ;

        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.math.Vector2;


/**
 * The type Exit pointer.
 */
public class ExitPointer {

            private final Texture arrowTexture;

            private static final float ARROW_OFFSET_X = 8f;
            private static final float ARROW_OFFSET_Y = 24f;


    /**
     * Instantiates a new Exit pointer.
     */
    public ExitPointer() {
                arrowTexture = new Texture("arrows.png");
            }


    /**
     * Render.
     *
     * @param batch     the batch
     * @param playerPos the player pos
     * @param exitPos   the exit pos
     */
    public void render(SpriteBatch batch, Vector2 playerPos, Vector2 exitPos) {
                Vector2 direction = new Vector2(exitPos).sub(playerPos);

                float angle = direction.angleDeg() - 90f;

                float arrowX = playerPos.x + ARROW_OFFSET_X;
                float arrowY = playerPos.y + ARROW_OFFSET_Y;

                float originX = arrowTexture.getWidth() / 2f;
                float originY = arrowTexture.getHeight() / 2f;

                batch.draw(
                        arrowTexture,
                        arrowX,
                        arrowY,
                        originX,
                        originY,
                        arrowTexture.getWidth(),
                        arrowTexture.getHeight(),
                        1.0f,
                        1.0f,
                        angle,
                        0,
                        0,
                        arrowTexture.getWidth(),
                        arrowTexture.getHeight(),
                        false,
                        false
                );
            }


    /**
     * Dispose.
     */
    public void dispose() {
                arrowTexture.dispose();
            }
        }