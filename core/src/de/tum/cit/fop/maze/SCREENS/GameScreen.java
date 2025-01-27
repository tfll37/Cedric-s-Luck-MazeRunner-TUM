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
import de.tum.cit.fop.maze.MAZELOGIC.*;
import de.tum.cit.fop.maze.MazeRunnerGame;
import de.tum.cit.fop.maze.PC_NPC_OBJ.*;

/**
 * The GameScreen class is responsible for managing gameplay.
 */
public class GameScreen implements Screen, InputProcessor, DiceMinigameListener {
    private boolean isPaused = false;
    public LevelMNGR.LevelInfo getLevel() {
        return level;
    }
    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final Labyrinth labyrinth;
    private final Player player;
    private Array<Enemy> enemies;
    int amountOfEnemies = 1;
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
    private int rollsNeededToOpenDoor = 20;



    public GameScreen(MazeRunnerGame game, LevelMNGR.LevelInfo level) {
        this.game = game;
        this.camera = game.getCamera();
        this.level = level;

        Gdx.input.setInputProcessor(this);


        String tmxPath = LevelMNGR.getTmxPathForSize(level.mapSize());
        TileEffectMNGR tileEffectMNGR = new TileEffectMNGR();

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

        this.animationMNGR = new AnimationMNGR();
        initializeTileAnimations();

        Vector2 spawnPoint = labyrinth.getSpawnPoint();
        Vector2 diceSpawnPoint = labyrinth.getValidSpawnPoint();
        Vector2 heartSpawnPoint = labyrinth.getValidSpawnPoint();
        player = new Player(spawnPoint.x, spawnPoint.y);
        enemies = new Array<>();
        for(int i = 0; i < amountOfEnemies; i++){

            Vector2 enemySpawnPoint = labyrinth.getValidSpawnPoint();
            enemies.add(new Enemy(enemySpawnPoint.x, enemySpawnPoint.y));
        }
        dice = new Dice(diceSpawnPoint.x, diceSpawnPoint.y);
        hitParticle1 = new HitParticle(player.getBounds().x, player.getBounds().y);
        diceMinigame = new DiceMinigame(animationMNGR);
        diceMinigame.setListener(this);

        heart = new Heart(heartSpawnPoint.x, heartSpawnPoint.y);
        gameUI = new GameUI(game.getSpriteBatch(), this.game.getSkin());
        exitPointer = new ExitPointer();


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

    private void initializeTileAnimations() {
        TiledMapTileLayer layer = (TiledMapTileLayer) labyrinth.getBackground().getTiledMap().getLayers().get(0);
        TiledMap map = labyrinth.getBackground().getTiledMap();

        animationMNGR.setMapAndLayer(layer, map);

        animationMNGR.registerAnimatedTile(1881, 1929);
    }


    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Updates
        cameraMNGR.update(player.getPosition());
        animationMNGR.updateTileAnimations();

        // Player State Update
        float tileWidth = labyrinth.getBackground().getTiledMap().getProperties().get("tilewidth", Integer.class);
        float tileHeight = labyrinth.getBackground().getTiledMap().getProperties().get("tileheight", Integer.class);
        float labyrinthWidth = labyrinth.getBackground().getTiledMap().getProperties().get("width", Integer.class);
        float labyrinthHeight = labyrinth.getBackground().getTiledMap().getProperties().get("height", Integer.class);
        labyrinth.render(camera);
        float cameraX = camera.position.x;
        float cameraY = camera.position.y;

        Vector2 exitPoint = labyrinth.getExitPoint();
        Vector2 playerTile = player.getTilePosition(16, 16);
        Vector2 exitTile = new Vector2(exitPoint.x / 16, exitPoint.y / 16);

        if (playerTile.x == exitTile.x && playerTile.y == exitTile.y) {
            // Load next level
            int nextLevelIndex = level.Level() + 1;
            LevelMNGR.LevelInfo nextLevel = LevelMNGR.getLevel(nextLevelIndex);
            if (nextLevel != null) {
                game.goToGame(nextLevel);
            } else {
                game.goToMenu();
            }
        }

        player.update(delta, labyrinthWidth, labyrinthHeight, tileWidth, tileHeight, labyrinth, enemies);
        dice.update(delta, player);
        heart.update(delta, player);
        player.getFireBall().update(delta, player, labyrinth, enemies);
        for (int i = 0; i < amountOfEnemies; i++) {
            Enemy enemy = enemies.get(i);
            enemy.update(delta, labyrinthWidth, labyrinthHeight, tileWidth, tileHeight, labyrinth, player, maze);
        hitParticle1.update(delta, player, enemy.isDisplayHitParticle());
        gameUI.update(delta, player, enemy);

        }
        handleInput();
        cameraMNGR.update(player.getPosition());
//        animationMNGR.updateTileAnimations();

        if (dice.isMinigameActive() && !diceMinigame.isActive()) {
            diceMinigame.start();
            if(!dice.isGotcolelcted()){
                gameUI.updateScore(diceMinigame.getDiceResult());
            }
            diceMinigame.setActiveDuration(3.0f);

        }
        if (!diceMinigame.isActive() && dice.isMinigameActive()) {
            System.out.println("Reached post-minigame logic!");

            int diceResult = diceMinigame.getDiceResult();

            if (diceResult > 0) {
                rollsNeededToOpenDoor -= diceResult;
                if (rollsNeededToOpenDoor < 0) {
                    rollsNeededToOpenDoor = 0;
                }

                gameUI.setDoorUnlockProgress(rollsNeededToOpenDoor);

                if (rollsNeededToOpenDoor <= 0) {
                    System.out.println("Door unlocked!");
                }
            }
            dice.deactivateMinigame();
        }

        diceMinigame.update(delta);
        if (!diceMinigame.isActive() && dice.isMinigameActive()) {
            dice.deactivateMinigame();
        }


        // Render game elements
        labyrinth.render(camera);
        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();

        player.render(batch);
        for(int i = 0; i < amountOfEnemies; i++){
            Enemy enemy = enemies.get(i);
            if (enemy.getLifeStatus() == true) {
                enemy.render(batch);
            }
        }
        dice.render(batch);
        heart.render(batch);
        hitParticle1.render(batch);
        if (diceMinigame.isActive()) {
            System.out.println("Dice Minigame is active");
        }
        player.getFireBall().render(batch);

        Vector2 playerPos = player.getPosition();
        Vector2 exitPos = labyrinth.getExitPoint();
        exitPointer.render(batch, playerPos, exitPos);

        batch.end();
        diceMinigame.render(batch, cameraX, cameraY);


        gameUI.render();
    }

    private void handleInput() {
        boolean escapedPressed = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
        if(escapedPressed){
            isPaused = !isPaused;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            cameraMNGR.startShake();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            cameraMNGR.startLightShake();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            cameraMNGR.startHeavyShake();
        }
    }

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
    public void onDiceRolled(int diceResult) {

        rollsNeededToOpenDoor -= diceResult;
        if (rollsNeededToOpenDoor < 0) {
            rollsNeededToOpenDoor = 0;
        }
        gameUI.setDoorUnlockProgress(rollsNeededToOpenDoor);

        if (rollsNeededToOpenDoor <= 0) {
            System.out.println("Door unlocked!");
        }

        dice.deactivateMinigame();
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

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        cameraMNGR.resize(width, height);
    }


    @Override
    public void show() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
    public void handleDiceResult(int diceResult) {
        rollsNeededToOpenDoor -= diceResult;
        if (rollsNeededToOpenDoor < 0) rollsNeededToOpenDoor = 0;
        gameUI.setDoorUnlockProgress(rollsNeededToOpenDoor);

        if (rollsNeededToOpenDoor <= 0) {
            System.out.println("Door unlocked!");
        }

        dice.deactivateMinigame();
    }
    @Override
    public void dispose() {
        labyrinth.dispose();
    }
}
