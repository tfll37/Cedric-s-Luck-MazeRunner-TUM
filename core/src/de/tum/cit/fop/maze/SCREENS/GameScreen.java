package de.tum.cit.fop.maze.SCREENS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;
import de.tum.cit.fop.maze.MAZELOGIC.CameraMNGR;
import de.tum.cit.fop.maze.MAZELOGIC.Labyrinth;
import de.tum.cit.fop.maze.MAZELOGIC.LevelMNGR;
import de.tum.cit.fop.maze.MAZELOGIC.TileEffectMNGR;
import de.tum.cit.fop.maze.MAZELOGIC.TilePropMNGR;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.PC_NPC_OBJ.Dice;
import de.tum.cit.fop.maze.PC_NPC_OBJ.DiceMinigame;
import de.tum.cit.fop.maze.PC_NPC_OBJ.Enemy;
import de.tum.cit.fop.maze.PC_NPC_OBJ.ExitPointer;
import de.tum.cit.fop.maze.PC_NPC_OBJ.Heart;
import de.tum.cit.fop.maze.PC_NPC_OBJ.HitParticle;
import de.tum.cit.fop.maze.PC_NPC_OBJ.Player;

/**
 * GameScreen class: controls the main "in-game" logic, rendering, and transitions.
 */
public class GameScreen implements Screen, InputProcessor {

    private boolean isPaused = false;
    private boolean isLostDismissed = false;
    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final Labyrinth labyrinth;
    private final Player player;
    private Array<Enemy> enemies;
    private final Dice dice;
    private AnimationMNGR animationMNGR;
    private GameUI gameUI;
    private Array<Array<Integer>> maze;
    private CameraMNGR cameraMNGR;
    private LevelMNGR.LevelInfo level;
    private HitParticle hitParticle1;
    private DiceMinigame diceMinigame;
    private Heart heart;
    private ExitPointer exitPointer;
    private boolean gameOver = false;
    // Set how many enemies you want in this level:
    private final int amountOfEnemies = 1;

    /**
     * Expose the level info so external code can compare or retrieve it.
     */
    public LevelMNGR.LevelInfo getLevel() {
        return level;
    }

    public GameScreen(MazeRunnerGame game, LevelMNGR.LevelInfo level) {
        this.game = game;
        this.camera = game.getCamera();
        this.level = level;

        // Set this screen as the active input processor
        Gdx.input.setInputProcessor(this);

        // Load the appropriate .tmx tile map for this level
        String tmxPath = LevelMNGR.getTmxPathForSize(level.mapSize());
        TileEffectMNGR tileEffectMNGR = new TileEffectMNGR();

        // Create the labyrinth (contains the tile map & properties)
        this.labyrinth = new Labyrinth(
                game.getSpriteBatch(),
                tmxPath,
                level.propertiesFile(),
                tileEffectMNGR
        );

        this.cameraMNGR = new CameraMNGR(
                camera,
                game.getViewport(),
                labyrinth.getBackground().getTiledMap()
        );

        // Initialize animation manager and register any animated tiles
        this.animationMNGR = new AnimationMNGR();
        initializeTileAnimations();

        // Player and item/enemy spawning
        Vector2 spawnPoint = labyrinth.getSpawnPoint();
        player = new Player(spawnPoint.x, spawnPoint.y);

        enemies = new Array<>();
        for (int i = 0; i < amountOfEnemies; i++) {
            Vector2 enemySpawnPoint = labyrinth.getValidSpawnPoint();
            enemies.add(new Enemy(enemySpawnPoint.x, enemySpawnPoint.y));
        }

        Vector2 diceSpawnPoint = labyrinth.getValidSpawnPoint();
        dice = new Dice(diceSpawnPoint.x, diceSpawnPoint.y);

        Vector2 heartSpawnPoint = labyrinth.getValidSpawnPoint();
        heart = new Heart(heartSpawnPoint.x, heartSpawnPoint.y);

        hitParticle1 = new HitParticle(player.getBounds().x, player.getBounds().y);

        diceMinigame = new DiceMinigame(animationMNGR);
        gameUI = new GameUI(game.getSpriteBatch(), this.game.getSkin());
        exitPointer = new ExitPointer();

        initializeMazeMatrix();
    }

    /**
     * Create a 2D array (Array<Array<Integer>>) that marks each tile
     * as 0 (walkable) or 1 (obstacle).
     */
    private void initializeMazeMatrix() {
        TiledMapTileLayer layer =
                (TiledMapTileLayer) labyrinth.getBackground().getTiledMap().getLayers().get(0);
        int width = layer.getWidth();
        int height = layer.getHeight();
        maze = new Array<>(width);

        for (int x = 0; x < width; x++) {
            Array<Integer> column = new Array<>(height);
            for (int y = 0; y < height; y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    boolean walkable = TilePropMNGR.isTileWalkable(layer, x, y);
                    column.add(walkable ? 0 : 1);
                } else {
                    // If there's no tile, treat it as obstacle
                    column.add(1);
                }
            }
            maze.add(column);
        }
    }

    private void initializeTileAnimations() {
        TiledMapTileLayer layer =
                (TiledMapTileLayer) labyrinth.getBackground().getTiledMap().getLayers().get(0);
        TiledMap map = labyrinth.getBackground().getTiledMap();

        animationMNGR.setMapAndLayer(layer, map);

        // Example usage: animate a tile range
        animationMNGR.registerAnimatedTile(1881, 1929);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);

        // If player is dead and gameOver wasn't yet triggered, set gameOver
        if (player.isDead() && !gameOver) {
            gameOver = true;
        }

        // If gameOver is triggered but user hasn't dismissed the "You Lost" prompt
        // Show "YOU LOST" text (large) and wait for input
        if (gameOver && !isLostDismissed) {
            SpriteBatch batch = game.getSpriteBatch();
            batch.begin();

            // Enlarge font only for the "YOU LOST" message
            game.getSkin().getFont("font").getData().setScale(3.0f);
            game.getSkin().getFont("font").draw(batch, "YOU LOST",
                    camera.position.x - 100, camera.position.y);

            batch.end();

            handleInput(); // Check if user presses a key/touch to go to menu
            return;        // Skip the rest of game logic/rendering
        }

        // Update camera following the player, handle tile animations
        cameraMNGR.update(player.getPosition());
        animationMNGR.updateTileAnimations();

        // Tile / labyrinth dimensions from TiledMap properties
        float tileWidth = labyrinth.getBackground().getTiledMap()
                .getProperties().get("tilewidth", Integer.class);
        float tileHeight = labyrinth.getBackground().getTiledMap()
                .getProperties().get("tileheight", Integer.class);
        float labyrinthWidth = labyrinth.getBackground().getTiledMap()
                .getProperties().get("width", Integer.class);
        float labyrinthHeight = labyrinth.getBackground().getTiledMap()
                .getProperties().get("height", Integer.class);

        // Render the labyrinth (the tile map)
        labyrinth.render(camera);

        // Check if the player is on the exit tile
        Vector2 exitPoint = labyrinth.getExitPoint();
        Vector2 playerTile = player.getTilePosition(16, 16);
        Vector2 exitTile = new Vector2(exitPoint.x / 16, exitPoint.y / 16);

        if (playerTile.x == exitTile.x && playerTile.y == exitTile.y) {
            int nextLevelIndex = level.Level() + 1;
            LevelMNGR.LevelInfo nextLevel = LevelMNGR.getLevel(nextLevelIndex);
            if (nextLevel != null) {
                game.goToGame(nextLevel);
            } else {
                // If no more levels, go to main menu
                game.goToMenu();
            }
        }

        // Update logic
        player.update(delta, labyrinthWidth, labyrinthHeight, tileWidth, tileHeight, labyrinth, enemies);
        dice.update(delta, player);
        heart.update(delta, player);
        player.getFireBall().update(delta, player, labyrinth, enemies);

        for (int i = 0; i < amountOfEnemies; i++) {
            Enemy enemy = enemies.get(i);
            enemy.update(delta, labyrinthWidth, labyrinthHeight,
                    tileWidth, tileHeight, labyrinth, player, maze);

            // Update the hit particle effect
            hitParticle1.update(delta, player, enemy.isDisplayHitParticle());

            // Update UI with enemy/player info
            gameUI.update(delta, player, enemy);
        }

        // Handle dice minigame activation
        if (dice.isMinigameActive() && !diceMinigame.isActive()) {
            diceMinigame.start();
            if (!dice.isGotcolelcted()) {
                // Add dice result to score
                gameUI.updateScore(diceMinigame.getDiceResult());
            }
            diceMinigame.setActiveDuration(3.0f);
        }
        if (!diceMinigame.isActive() && dice.isMinigameActive()) {
            dice.deactivateMinigame();
        }
        diceMinigame.update(delta);

        // Render labyrinth again if needed for layering
        labyrinth.render(camera);

        // Begin batch rendering for characters, pickups, etc.
        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();

        // Make sure our UI font is back to smaller scale
        game.getSkin().getFont("font").getData().setScale(1.0f);

        player.render(batch);
        for (int i = 0; i < amountOfEnemies; i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.getLifeStatus()) {
                enemy.render(batch);
            }
        }
        dice.render(batch);
        heart.render(batch);
        hitParticle1.render(batch);
        player.getFireBall().render(batch);

        // Show an arrow pointing to exit
        Vector2 playerPos = player.getPosition();
        Vector2 exitPos = labyrinth.getExitPoint();
        exitPointer.render(batch, playerPos, exitPos);

        batch.end();

        // Render dice minigame overlay
        diceMinigame.render(batch, camera.position.x, camera.position.y);

        // Render game UI (health bars, score, etc.) – it's small again
        gameUI.render();
    }

    /**
     * Called during "YOU LOST" display. If user presses any key or touches, go to menu.
     */
    private void handleInput() {
        if (gameOver && !isLostDismissed) {
            // Check if any key was just pressed or touch was detected
            if (isAnyKeyJustPressed() || Gdx.input.justTouched()) {
                System.out.println("Input detected! Navigating to the menu...");
                isLostDismissed = true;
                game.goToMenu();
            }
        }
    }

    /**
     * Helper method to detect if any key was just pressed.
     * This avoids issues with Input.Keys.ANY_KEY in some environments.
     */
    private boolean isAnyKeyJustPressed() {
        for (int keyCode = 0; keyCode < 256; keyCode++) {
            if (Gdx.input.isKeyJustPressed(keyCode)) {
                return true;
            }
        }
        return false;
    }

    // InputProcessor methods below:

    @Override
    public boolean scrolled(float amountX, float amountY) {
        cameraMNGR.handleScroll(amountY);
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    // Screen lifecycle methods:

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        cameraMNGR.resize(width, height);
    }

    @Override
    public void show() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        labyrinth.dispose();
    }
}
