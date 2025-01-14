package de.tum.cit.fop.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private WelcomeScreen welcomeScreen;
    private MenuScreen menuScreen;
    private GameScreen gameScreen;

    // Core rendering components
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private FitViewport viewport;

    // UI Skin
    private Skin skin;

    // Textures & Animations
    private Texture backgroundTexture;
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterDownHitAnimation;
    private LevelMNGR.LevelInfo currentLevel;

    /**
     * CONSTRUCTOR
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        camera = new OrthographicCamera();
        camera.zoom = 0.4f;
        // Use a FitViewport for handling resizing (for example, 800x600)
        viewport = new FitViewport(800, 600, camera);
        camera.update();

        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        this.loadCharacterAnimation(); // Load character animation

        //                              MUSIC
//        Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
//        backgroundMusic.setLooping(true);
//        backgroundMusic.play();

        goToWelcomeScreen();
        backgroundTexture = new Texture(Gdx.files.internal("assets/DEV_grid.png"));
    }

    public void goToWelcomeScreen() {
        this.setScreen(new WelcomeScreen(this));
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame(LevelMNGR.LevelInfo level) {
        // Create a new GameScreen if needed
        gameScreen = new GameScreen(this, level);
        setScreen(gameScreen);
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }

    /**
     * Loads the character animation from the character.png file.
     */
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));

        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

        int hitFrameWidth = 32;

        // Create arrays of frames
        Array<TextureRegion> walkFrames = new Array<>();
        Array<TextureRegion> hitFrames = new Array<>();

        characterDownAnimation = new Animation<>(0.1f, walkFrames);
        characterDownHitAnimation = new Animation<>(0.1f, hitFrames);
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }


    /**
     *
     * Getters and Setters for the TEXTURES
     */
    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(Texture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }


    /**
     *
     * GETTERS AND SETTERS for Character Animation
     */
    public void setCharacterDownAnimation(Animation<TextureRegion> characterDownAnimation) {
        this.characterDownAnimation = characterDownAnimation;
    }

    public Animation<TextureRegion> getCharacterDownHitAnimation() {
        return characterDownHitAnimation;
    }

    public void setCharacterDownHitAnimation(Animation<TextureRegion> characterDownHitAnimation) {
        this.characterDownHitAnimation = characterDownHitAnimation;
    }

    public FitViewport getViewport() {
        return viewport;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
