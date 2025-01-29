package de.tum.cit.fop.maze.SCREENS;

import com.badlogic.gdx.*;
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
    private boolean isLostDismissed = false;
    private final OrthographicCamera camera;
    private final Labyrinth labyrinth;
    private final Player player;
    private Array<Enemy> enemies;
    private Array<Dice> dices = new Array<>();
    private AnimationMNGR animationMNGR;
    private GameUI gameUI;
    private Array<Array<Integer>> maze;
    private CameraMNGR cameraMNGR;
    private LevelMNGR.LevelInfo level;
    private HitParticle hitParticle1;
    private DiceMinigame diceMinigame;
    private Array<DiceMinigame> minigames = new Array<>();
    private Array<Heart> hearts = new Array<>();
    private ExitPointer exitPointer;
    private boolean gameOver = false;
    // Set how many enemies you want in this level:
    private int amountOfEnemies = 1;
    private int amountOfDice = 1;
    private int amountOfHearts = 1;
    private int rollsNeededToOpenDoor;

    private int currentLevelScore = 0;
    private int requiredScore;
    private boolean levelComplete = false;

    private PauseMenuScreen pauseMenu;
    private VictoryScreen victoryScreen;

    private InputMultiplexer inputMultiplexer;

    public GameScreen(MazeRunnerGame game, LevelMNGR.LevelInfo level) {
        this.game = game;
        this.camera = game.getCamera();
        this.level = level;

        this.pauseMenu = new PauseMenuScreen(game, this);
        this.victoryScreen = new VictoryScreen(game, this, level);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        if (pauseMenu != null && pauseMenu.getStage() != null) {
            inputMultiplexer.addProcessor(pauseMenu.getStage());
        }
        if (victoryScreen != null && victoryScreen.getStage() != null) {
            inputMultiplexer.addProcessor(victoryScreen.getStage());
        }
        Gdx.input.setInputProcessor(inputMultiplexer);


        String tmxPath = LevelMNGR.getTmxPathForSize(level.mapSize());
        TileEffectMNGR tileEffectMNGR = new TileEffectMNGR();

        this.gameUI = new GameUI(game.getSpriteBatch(), this.game.getSkin());


        this.requiredScore = LevelMNGR.generateScoreRequirement(level);
        this.rollsNeededToOpenDoor = level.Level()*3 + 6; // Set door requirement to match score requirement
        this.gameUI.setScoreRequirement(requiredScore);
        this.gameUI.setDoorUnlockProgress(rollsNeededToOpenDoor);

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

        player = new Player(spawnPoint.x, spawnPoint.y, cameraMNGR);
        enemies = new Array<>();
        amountOfEnemies = level.Level() * 2;
        amountOfDice = level.Level() * 3;
        amountOfHearts = level.Level() * 3;

        for (int i = 0; i < amountOfEnemies; i++) {

            Vector2 enemySpawnPoint = labyrinth.getValidSpawnPoint();
            enemies.add(new Enemy(enemySpawnPoint.x, enemySpawnPoint.y));
        }

        for (int i = 0; i <= amountOfHearts; i++) {
            Vector2 heartSpawnPoint = labyrinth.getValidSpawnPoint();
            hearts.add(new Heart(heartSpawnPoint.x, heartSpawnPoint.y));
        }
        for (int i = 0; i <= amountOfDice; i++) {
            Vector2 diceSpawnPoint = labyrinth.getValidSpawnPoint();
            dices.add(new Dice(diceSpawnPoint.x, diceSpawnPoint.y));
            DiceMinigame mini = new DiceMinigame(animationMNGR);
            mini.setListener(this);
            minigames.add(mini);
        }


        hitParticle1 = new HitParticle(player.getBounds().x, player.getBounds().y);
        //diceMinigame = new DiceMinigame(animationMNGR);
        //diceMinigame.setListener(this);

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

            // Reset the font scale back to normal
            game.getSkin().getFont("font").getData().setScale(1.0f);
            batch.end();

            handleInput();
            return;
        }

        // Update camera following the player, handle tile animations
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

        if (playerTile.x == exitTile.x && playerTile.y == exitTile.y && rollsNeededToOpenDoor ==0 ) {

            if (playerTile.x == exitTile.x && playerTile.y == exitTile.y && rollsNeededToOpenDoor ==0) {
                if (currentLevelScore >= requiredScore) {
                    // Show victory screen instead of immediately loading next level
                    setIsPaused(true);
                    victoryScreen.show();
                } else {
                    // Display message that more score is needed
                    gameUI.showScoreRequirementMessage(requiredScore - currentLevelScore);
                }
            }
        }
        if(!isPaused){
        player.update(delta, labyrinthWidth, labyrinthHeight, tileWidth, tileHeight, labyrinth, enemies);
        gameUI.setDashCount(player.getDashCount());
        for (int i = 0; i <= amountOfDice; i++) {
            Dice dice = dices.get(i);
            dice.update(delta, player);

        }
        for (int i = 0; i <= amountOfHearts; i++) {
            Heart heart1 = hearts.get(i);
            heart1.update(delta, player);
        }
        player.getFireBall().update(delta, player, labyrinth, enemies);
        for (int i = 0; i < amountOfEnemies; i++) {
            Enemy enemy = enemies.get(i);
            enemy.update(delta, labyrinthWidth, labyrinthHeight, tileWidth, tileHeight, labyrinth, player, maze);
            hitParticle1.update(delta, player, enemy.isDisplayHitParticle());
            gameUI.update(delta, player, enemy);

        }
        gameUI.update(delta, player);
        }

        handleInput();
        cameraMNGR.update(player.getPosition());
//        animationMNGR.updateTileAnimations();
        for (int i = 0; i <= amountOfDice; i++) {
            Dice dice = dices.get(i);
            DiceMinigame diceMinigame = minigames.get(i);
            if (dice.isMinigameActive() && !diceMinigame.isActive()) {
                diceMinigame.start();
                if (!dice.isGotcolelcted()) {
                    gameUI.updateScore(diceMinigame.getDiceResult());
                }
                diceMinigame.setActiveDuration(3.0f); // Set the minigame to stay active for 3 seconds

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
        }


        // Render game elements
        labyrinth.render(camera);
        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();

        // Make sure our UI font is back to smaller scale
        game.getSkin().getFont("font").getData().setScale(1.0f);

        player.render(batch);
        for (int i = 0; i < amountOfEnemies; i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.getLifeStatus() == true) {
                enemy.render(batch);
            }
        }
        for (int i = 0; i <= amountOfDice; i++) {
            Dice dice = dices.get(i);
            dice.render(batch);

        }
        for (int i = 0; i <= amountOfHearts; i++) {
            Heart heart = hearts.get(i);
            heart.render(batch);
        }
        hitParticle1.render(batch);
        for(int i = 0; i <=amountOfDice;i++){
            DiceMinigame diceMinigame = minigames.get(i);
            if (diceMinigame.isActive()) {
            System.out.println("Dice Minigame is active");
        }}
        // In your GameScreen render or update method
        player.getFireBall().render(batch);

        Vector2 playerPos = player.getPosition();
        Vector2 exitPos = labyrinth.getExitPoint();
        exitPointer.render(batch, playerPos, exitPos);

        batch.end();
        for(int i = 0; i <=amountOfDice;i++){
            DiceMinigame diceMinigame = minigames.get(i);
            diceMinigame.render(batch, cameraX, cameraY);
        }

        if (pauseMenu.isVisible()) {
            pauseMenu.render();
        }

        if (victoryScreen.isVisible()) {
            victoryScreen.render();
        }

        gameUI.render();
    }

    private void handleInput() {

        if (gameOver && !isLostDismissed) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isTouched() || Gdx.input.justTouched()) {
                System.out.println("Input detected! Navigating to the menu...");
                isLostDismissed = true;
                game.goToMenu();
            }
        }

    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        cameraMNGR.handleScroll(amountY);
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            if (!pauseMenu.isVisible()) {
                pauseMenu.show();
                this.isPaused = true;
            } else {
                pauseMenu.hide();
                this.isPaused = false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDiceRolled(int diceResult) {
        System.out.println(diceResult);
        rollsNeededToOpenDoor -= diceResult;
        if (rollsNeededToOpenDoor < 0) {
            rollsNeededToOpenDoor = 0;
        }
        gameUI.setDoorUnlockProgress(rollsNeededToOpenDoor);

        if (rollsNeededToOpenDoor <= 0) {
            System.out.println("Door unlocked!");
        }
        currentLevelScore += diceResult;

        if (rollsNeededToOpenDoor < 0) {
            rollsNeededToOpenDoor = 0;
        }

        // Update UI
        gameUI.setDoorUnlockProgress(rollsNeededToOpenDoor);
        gameUI.updateScore(currentLevelScore);

        // Check if we've met level requirements
        if (currentLevelScore >= requiredScore && rollsNeededToOpenDoor <= 0) {
            levelComplete = true;
            System.out.println("Level requirements met! Find the exit!");
            gameUI.showLevelCompleteMessage();
        }
        for (int i = 0; i <= amountOfDice; i++) {
            dices.get(i).deactivateMinigame();
        }
    }

    public void addInputProcessor(InputProcessor processor) {
        if (inputMultiplexer != null && !inputMultiplexer.getProcessors().contains(processor, true)) {
            inputMultiplexer.addProcessor(0, processor);
        }
    }

    public void removeInputProcessor(InputProcessor processor) {
        if (inputMultiplexer != null) {
            inputMultiplexer.removeProcessor(processor);
        }
    }

    public InputMultiplexer getInputMultiplexer() {
        return inputMultiplexer;
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
        if (inputMultiplexer != null) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void resize(int width, int height) {
        cameraMNGR.resize(width, height);
        if (pauseMenu != null) {
            pauseMenu.resize(width, height);
        }
        if (victoryScreen != null) {
            victoryScreen.resize(width, height);
        }
        game.getViewport().update(width, height, true);
        camera.update();
    }


    @Override
    public void show() {
        // Reset input processor to handle both the game screen and scroll input
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);  // 'this' handles the scroll input
        if (pauseMenu != null && pauseMenu.getStage() != null) {
            inputMultiplexer.addProcessor(pauseMenu.getStage());
        }
        if (victoryScreen != null && victoryScreen.getStage() != null) {
            inputMultiplexer.addProcessor(victoryScreen.getStage());
        }
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
    public void setIsPaused(boolean value){
        isPaused = value;
    }

    @Override
    public void dispose() {
        if (inputMultiplexer != null) {
            inputMultiplexer.clear();
        }
        labyrinth.dispose();
        if (pauseMenu != null) pauseMenu.dispose();
        if (victoryScreen != null) victoryScreen.dispose();
    }

}
