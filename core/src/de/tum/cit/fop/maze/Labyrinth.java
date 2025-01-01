package de.tum.cit.fop.maze;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;


public class Labyrinth extends TiledMap {
    private final Array<TextureRegion> objects; // Represents different objects in the labyrinth
    private Background background;
    private final TiledMap tiledMap;

    /**
     * CONSTRUCTOR for Labyrinth.
     *
     * @param spriteBatch The SpriteBatch for rendering.
     */
    public Labyrinth(SpriteBatch spriteBatch) {
        this.background = new Background(spriteBatch);
        this.objects = new Array<>();

        background.loadTiledMap("assets/FOP_test.tmx");
        this.tiledMap = background.getTiledMap();
    }


    /**
     * Add an object to the labyrinth.
     *
     * @param object TextureRegion representing the object.
     */
    public void addObject(TextureRegion object) {
        objects.add(object);
    }


    /**
     * Render the labyrinth, including the background.
     */
    public void render(OrthographicCamera camera) {
        background.renderTiledMap(camera);

        // Render other labyrinth objects, if any
        // e.g., spriteBatch.begin(); spriteBatch.draw(...); spriteBatch.end();

    }

    /**
     * Update the labyrinth mechanics (e.g., object movement).
     */
    public void update(float delta) {
        // Update mechanics if needed, e.g., object positions
    }

    /**
     * Dispose of resources.
     */
    public void dispose() {
        background.dispose();
    }

    public Background getBackground() {
        return background;
    }
}
