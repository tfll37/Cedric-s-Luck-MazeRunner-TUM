package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * The GameScreen class is responsible for managing gameplay.
 */
public class GameScreen implements Screen {
    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final Labyrinth labyrinth;
    private final Player player;

//    private GameUI gameUI;


    /**
     * Constructor for GameScreen. Initializes game elements.
     *
     * @param game The main game instance.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        this.camera = game.getCamera();

        // Initialize labyrinth with a tiled map
        labyrinth = new Labyrinth(game.getSpriteBatch());
        labyrinth.getBackground().centerTiledMap(camera);

        player = new Player(100, 100);

        // Initialize GameUI for health, score, etc.
//        gameUI = new GameUI(game.getSpriteBatch());

        // Set input processor to the GameUI stage
//        Gdx.input.setInputProcessor(gameUI.getStage());
    }


    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Updates
        camera.update();
        handleInput();
        player.update(delta);

        // Render game elements
        labyrinth.render(camera);

        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();
        player.render(batch);
        batch.end();


//        gameUI.render();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.moveLeft();
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.moveRight();
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.moveUp();
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.moveDown();
        } else {
            player.stop();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();

        }

        // Example: Adjust game UI updates (placeholder logic for demonstration)
//        if (Gdx.input.isKeyPressed(Input.Keys.H)) gameUI.updateHealth(90); // Example health update
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) gameUI.updateScore(100); // Example score update
    }


    @Override
    public void resize(int width, int height) {
        game.getViewport().update(width, height);
    }

    @Override
    public void show() {
        // Called when this screen becomes the current screen.
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        labyrinth.dispose();
//        gameUI.dispose();
    }


}
