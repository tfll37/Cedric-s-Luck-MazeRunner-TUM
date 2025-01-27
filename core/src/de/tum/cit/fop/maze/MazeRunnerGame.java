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
import de.tum.cit.fop.maze.MAZELOGIC.LevelMNGR;
import de.tum.cit.fop.maze.SCREENS.GameScreen;
import de.tum.cit.fop.maze.SCREENS.MenuScreen;
import de.tum.cit.fop.maze.SCREENS.WelcomeScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {

    // Screens
    private WelcomeScreen welcomeScreen;
    private MenuScreen menuScreen;
    private GameScreen currentGameScreen;

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

    // Background music
    private Music backgroundMusic;

    /**
     * CONSTRUCTOR
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch, Skin, and Music.
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

        // Initialize and play background music
        backgroundMusic = Gdx.audio.newMusic(
                Gdx.files.internal("assets/music/455516__ispeakwaves__the-plan-upbeat-loop-no-voice-edit-mono-track.ogg")
        );
        backgroundMusic.setLooping(true); // Loop the music
        backgroundMusic.setVolume(0.3f);  // Set the volume
        backgroundMusic.play();          // Start playing the music

        // Load character animation frames
        loadCharacterAnimation();

        // Go to the welcome screen first
        goToWelcomeScreen();

        // Some default background texture (for dev/test)
        backgroundTexture = new Texture(Gdx.files.internal("assets/DEV_grid.png"));
    }

    /**
     * Switch to the Welcome Screen.
     */
    public void goToWelcomeScreen() {
        // Dispose old game screen if it exists
        if (currentGameScreen != null) {
            currentGameScreen.dispose();
            currentGameScreen = null;
        }
        // Create new WelcomeScreen
        welcomeScreen = new WelcomeScreen(this);
        this.setScreen(welcomeScreen);
    }

    /**
     * Switches to the menu screen. Disposes any current game screen so that
     * we always start fresh when returning from a "YOU LOST" state.
     */
    public void goToMenu() {
        // Dispose current game screen to reset everything
        if (currentGameScreen != null) {
            currentGameScreen.dispose();
            currentGameScreen = null;
        }
        // Create a new MenuScreen each time
        menuScreen = new MenuScreen(this);
        this.setScreen(menuScreen);
    }

    /**
     * Switches to a new GameScreen for the given level, ensuring a clean start.
     */
    public void goToGame(LevelMNGR.LevelInfo level) {
        // Always create a *new* GameScreen so we start fresh
        currentGameScreen = new GameScreen(this, level);
        setScreen(currentGameScreen);
    }

    /**
     * Loads the character animation from the character.png file.
     * (For demonstration, we just create empty arrays here.)
     */
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));
        int frameWidth = 16;
        int frameHeight = 32;

        // Number of frames in the sprite sheet (example only)
        Array<TextureRegion> walkFrames = new Array<>();
        Array<TextureRegion> hitFrames = new Array<>();

        // Create placeholder animations
        characterDownAnimation = new Animation<>(0.1f, walkFrames);
        characterDownHitAnimation = new Animation<>(0.1f, hitFrames);

        // Dispose walkSheet after extracting frames (if needed)
        // walkSheet.dispose(); // Uncomment if you truly no longer need it in memory
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        // Dispose of the current screen (if any) to avoid leaks
        if (getScreen() != null) {
            getScreen().hide();
            getScreen().dispose();
        }
        // Dispose other resources
        spriteBatch.dispose();
        skin.dispose();
        backgroundMusic.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    // ------------------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------------------
    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public Animation<TextureRegion> getCharacterDownHitAnimation() {
        return characterDownHitAnimation;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    public FitViewport getViewport() {
        return viewport;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    // ------------------------------------------------------------------------
    // SETTERS
    // ------------------------------------------------------------------------
    public void setBackgroundTexture(Texture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    public void setCharacterDownAnimation(Animation<TextureRegion> characterDownAnimation) {
        this.characterDownAnimation = characterDownAnimation;
    }

    public void setCharacterDownHitAnimation(Animation<TextureRegion> characterDownHitAnimation) {
        this.characterDownHitAnimation = characterDownHitAnimation;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
