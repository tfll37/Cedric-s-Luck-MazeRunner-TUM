package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public interface IGameOBJ {
    void update(float delta);

    void render(SpriteBatch batch);

    Rectangle getBounds();
}
