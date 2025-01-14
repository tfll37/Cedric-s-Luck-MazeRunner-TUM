package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
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
    private final Dice dice;
    private AnimationMNGR animationMNGR;
    private GameUI gameUI;
    private Array<Array<Integer>> maze;
    private HitParticle hitParticle1;
    private DiceMinigame diceMinigame;


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
        Vector2 diceSpawnPoint = labyrinth.getValidSpawnPoint();
        player = new Player(spawnPoint.x, spawnPoint.y);
        enemy = new Enemy(enemySpawnPoint.x, enemySpawnPoint.y);
        dice = new Dice(diceSpawnPoint.x, diceSpawnPoint.y);
        hitParticle1 = new HitParticle(player.getBounds().x, player.getBounds().y);
        diceMinigame = new DiceMinigame(animationMNGR);
        // Initialize GameUI for health, score, etc.
        gameUI = new GameUI(game.getSpriteBatch(),this.game.getSkin());

        // Set input processor to the GameUI stage
//        Gdx.input.setInputProcessor(gameUI.getStage());
        initializeMazeMatrix();
    }
    private void initializeMazeMatrix() {
        TiledMapTileLayer layer = (TiledMapTileLayer) labyrinth.getBackground().getTiledMap().getLayers().get(0);
        int width = layer.getWidth();
        int height = layer.getHeight();
        maze = new Array<>(width);

        for (int x = 0; x < width; x++) {
            Array<Integer> column = new Array<>(height);
            for (int y = 0; y < height; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    boolean walkable = TilePropMNGR.isTileWalkable(layer, x, y);
                    column.add(walkable ? 0 : 1); // 0 for walkable, 1 for obstacles
                } else {
                    column.add(1); // Treat empty tiles as obstacles
                }
            }
            maze.add(column);
        }
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
        labyrinth.render(camera);
        float cameraX = camera.position.x;
        float cameraY = camera.position.y;

        player.update(delta, labyrinthWidth, labyrinthHeight, tileWidth, tileHeight, labyrinth, enemy);
        enemy.update(delta, labyrinthWidth, labyrinthHeight, tileWidth, tileHeight, labyrinth, player, maze);
        dice.update(delta, player);
        hitParticle1.update(delta, player, enemy.isDisplayHitParticle());
        System.out.println(Gdx.input.getX() + " " + Gdx.input.getY());
        gameUI.update(delta, player, enemy);
        handleInput();
        if (dice.isMinigameActive() && !diceMinigame.isActive()) {
            diceMinigame.start();
            diceMinigame.setActiveDuration(3.0f); // Set the minigame to stay active for 3 seconds

        }
        if (!diceMinigame.isActive() && dice.isMinigameActive()) {
            dice.deactivateMinigame();
        }

        diceMinigame.update(delta);
        if (!diceMinigame.isActive() && dice.isMinigameActive()) {
            dice.deactivateMinigame(); // Disable the flag in the Dice class
        }


        // Render game elements



        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();
        player.render(batch);
        if(enemy.getLifeStatus() == true){
            enemy.render(batch);
        }
        dice.render(batch);
        hitParticle1.render(batch);
        if (diceMinigame.isActive())
        {
            System.out.println("Dice Minigame is active");
        }

        batch.end();
        diceMinigame.render(batch, cameraX, cameraY);



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
