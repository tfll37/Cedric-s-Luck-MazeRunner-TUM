package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * The GameScreen class is responsible for managing gameplay.
 */
public class GameScreen implements Screen {
    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final Labyrinth labyrinth;
    private final Player player;
    private final Enemy enemy;
    private AnimationMNGR animationMNGR;
    private GameUI gameUI;


    /**
     * Constructor for GameScreen. Initializes game elements.
     *
     * @param game The main game instance.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        this.camera = game.getCamera();

//        camera.zoom = 0.5f;

        // Initialize labyrinth with a tiled map
        labyrinth = new Labyrinth(
                game.getSpriteBatch(),
                "assets/Gamemap.tmx",
                "maps/level-1.properties"
        );
        labyrinth.getBackground().centerTiledMap(camera);

        Vector2 spawnPoint = labyrinth.getValidSpawnPoint();
        Vector2 enemySpawnPoint = labyrinth.getValidSpawnPoint();
        player = new Player(spawnPoint.x, spawnPoint.y);
        enemy = new Enemy(enemySpawnPoint.x, enemySpawnPoint.y);

        // Initialize GameUI for health, score, etc.
        gameUI = new GameUI(game.getSpriteBatch(),this.game.getSkin());

        // Set input processor to the GameUI stage
//        Gdx.input.setInputProcessor(gameUI.getStage());
    }


    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Updates
        camera.update();

        // Player State Update
        float tileWidth = labyrinth.getBackground().getTiledMap().getProperties().get("tilewidth", Integer.class);
        float tileHeight = labyrinth.getBackground().getTiledMap().getProperties().get("tileheight", Integer.class);
        float labyrinthWidth = labyrinth.getBackground().getTiledMap().getProperties().get("width", Integer.class);
        float labyrinthHeight = labyrinth.getBackground().getTiledMap().getProperties().get("height", Integer.class);
        player.update(delta, labyrinthWidth, labyrinthHeight, tileWidth, tileHeight, labyrinth);
        enemy.update(delta, labyrinthWidth, labyrinthHeight, tileWidth, tileHeight, labyrinth);

        handleInput();


        // Render game elements
        labyrinth.render(camera);
        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();

        player.render(batch);
        enemy.render(batch);

        batch.end();


        gameUI.render();
    }

    private void handleInput() {
        var LEFT = Gdx.input.isKeyPressed(Input.Keys.A);
        var RIGHT = Gdx.input.isKeyPressed(Input.Keys.D);
        var DOWN = Gdx.input.isKeyPressed(Input.Keys.S);
        var UP = Gdx.input.isKeyPressed(Input.Keys.W);
        var ESCAPE = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
        var SHIFT = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        var COMMA = Gdx.input.isKeyPressed(Input.Keys.COMMA);
        var PERIOD = Gdx.input.isKeyPressed(Input.Keys.PERIOD);

        if (ESCAPE) {
            game.goToMenu();
        }

        if (SHIFT && COMMA ) {
            camera.zoom -= 0.05f;
        }else if (SHIFT && PERIOD) {
            camera.zoom += 0.05f;
        }

        // Example: Adjust game UI updates (placeholder logic for demonstration)
//        if (Gdx.input.isKeyPressed(Input.Keys.H)) gameUI.updateHealth(90); // Example health update
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) gameUI.updateScore(100); // Example score update
    }
    //                          OLD INPUT HANDLING
//    private void handleInput() {
//        var LEFT = Gdx.input.isKeyPressed(Input.Keys.A);
//        var RIGHT = Gdx.input.isKeyPressed(Input.Keys.D);
//        var DOWN = Gdx.input.isKeyPressed(Input.Keys.S);
//        var UP = Gdx.input.isKeyPressed(Input.Keys.W);
//        var ESCAPE = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
//        var SHIFT = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
//        var COMMA = Gdx.input.isKeyPressed(Input.Keys.COMMA);
//        var PERIOD = Gdx.input.isKeyPressed(Input.Keys.PERIOD);
//
//        if (UP) {
//            player.moveUp();
//        } else if (DOWN) {
//            player.moveDown();
//        } else if (LEFT) {
//            player.moveLeft();
//        } else if (RIGHT) {
//            player.moveRight();
//        } else {
//            player.stop();
//        }
//
//        if (ESCAPE) {
//            game.goToMenu();
//        }
//
//        if (SHIFT && COMMA ) {
//            camera.zoom -= 0.1f;
//        }else if (SHIFT && PERIOD) {
//            camera.zoom += 0.1f;
//        }
//
//        // Example: Adjust game UI updates (placeholder logic for demonstration)
    ////        if (Gdx.input.isKeyPressed(Input.Keys.H)) gameUI.updateHealth(90); // Example health update
    ////        if (Gdx.input.isKeyPressed(Input.Keys.S)) gameUI.updateScore(100); // Example score update
//    }


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
