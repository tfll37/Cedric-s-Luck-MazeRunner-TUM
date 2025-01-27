package de.tum.cit.fop.maze.PC_NPC_OBJ;

        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.math.Vector2;

        /**
         * A small arrow that hovers around the player and always points to the exit.
         */
        public class ExitPointer {

            private final Texture arrowTexture;

            // Offsets to position arrow relative to the player (so it’s not drawn under the player)
            private static final float ARROW_OFFSET_X = 8f;
            private static final float ARROW_OFFSET_Y = 24f;

            /**
             * Creates a new ExitPointer and loads the arrow texture.
             */
            public ExitPointer() {
                // Load your arrow texture, for example an arrow pointing “up” by default
                arrowTexture = new Texture("arrows.png");
            }

            /**
             * Renders the arrow pointing towards the exit.
             *
             * @param batch     the SpriteBatch used for drawing
             * @param playerPos the current position of the player
             * @param exitPos   the position of the exit
             */
            public void render(SpriteBatch batch, Vector2 playerPos, Vector2 exitPos) {
                // 1. Calculate direction from player to exit
                Vector2 direction = new Vector2(exitPos).sub(playerPos);

                // 2. Get angle in degrees.
                //    By default, direction.angleDeg() returns angle with the x-axis going counterclockwise.
                //    If your arrow texture is oriented “upwards” by default, you may need to subtract 90 degrees.
                float angle = direction.angleDeg() - 90f;

                // 3. Position the arrow so it’s near the player, offset a bit
                float arrowX = playerPos.x + ARROW_OFFSET_X;
                float arrowY = playerPos.y + ARROW_OFFSET_Y;

                // 4. Draw with rotation around the center of the arrow
                float originX = arrowTexture.getWidth() / 2f;
                float originY = arrowTexture.getHeight() / 2f;

                batch.draw(
                        arrowTexture,
                        arrowX,
                        arrowY,
                        originX,                // rotation origin X
                        originY,                // rotation origin Y
                        arrowTexture.getWidth(),
                        arrowTexture.getHeight(),
                        1.0f,                   // scale X
                        1.0f,                   // scale Y
                        angle,                  // rotation
                        0,                      // srcX
                        0,                      // srcY
                        arrowTexture.getWidth(),
                        arrowTexture.getHeight(),
                        false,                  // flipX
                        false                   // flipY
                );
            }

            /**
             * Disposes of the arrow texture to free up resources.
             */
            public void dispose() {
                arrowTexture.dispose();
            }
        }