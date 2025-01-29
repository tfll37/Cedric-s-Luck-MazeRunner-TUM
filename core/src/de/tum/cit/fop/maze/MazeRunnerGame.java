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


public class MazeRunnerGame extends Game {

    private WelcomeScreen welcomeScreen;
    private MenuScreen menuScreen;
    private GameScreen currentGameScreen;

    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private FitViewport viewport;

    private Skin skin;

    private Texture backgroundTexture;
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterDownHitAnimation;

    private Music backgroundMusic;


    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }


    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.zoom = 0.4f;


        viewport = new FitViewport(800, 600, camera);
        camera.update();

        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));


        backgroundMusic = Gdx.audio.newMusic(
                Gdx.files.internal("assets/music/455516__ispeakwaves__the-plan-upbeat-loop-no-voice-edit-mono-track.ogg")
        );
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.3f);

        loadCharacterAnimation();

        goToWelcomeScreen();

        backgroundTexture = new Texture(Gdx.files.internal("assets/DEV_grid.png"));
    }


    public void goToWelcomeScreen() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
        if (currentGameScreen != null) {
            currentGameScreen.dispose();
            currentGameScreen = null;
        }
        welcomeScreen = new WelcomeScreen(this);
        this.setScreen(welcomeScreen);
    }


    public void goToMenu() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }

        if (currentGameScreen != null) {
            currentGameScreen.dispose();
            currentGameScreen = null;
        }

        menuScreen = new MenuScreen(this);
        this.setScreen(menuScreen);
    }

    public void goToGame(LevelMNGR.LevelInfo level) {
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }

        currentGameScreen = new GameScreen(this, level);
        setScreen(currentGameScreen);
    }

    private void loadCharacterAnimation() {

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

        if (getScreen() != null) {
            getScreen().hide();
            getScreen().dispose();
        }
        spriteBatch.dispose();
        skin.dispose();
        backgroundMusic.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    public Skin getSkin() {
        return skin;
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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
    }
}
