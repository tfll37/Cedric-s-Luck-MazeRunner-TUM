package de.tum.cit.fop.maze.SCREENS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.MAZELOGIC.LevelMNGR;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It includes an animated background using frames from the menuvid folder and plays background music.
 */
public class MenuScreen implements Screen {
    private final Stage stage;
    private final MazeRunnerGame game;
    private final SpriteBatch spriteBatch;

    private Animation<Texture> animation;
    private float elapsedTime = 0;
    private Music backgroundMusic;

    public MenuScreen(MazeRunnerGame game) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();

        // Set up the camera and viewport
        var camera = new com.badlogic.gdx.graphics.OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());

        // Load animation frames from the menuvid folder
        Array<Texture> frames = new Array<>();
        for (int i = 1; i <= 200; i += 5) { // Assuming 200 frames
            String framePath = String.format("menuvid/ezgif-frame-%03d.jpg", i);
            frames.add(new Texture(Gdx.files.internal(framePath)));
        }
        animation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);

        // Set up the UI
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new Label("Diddy Party Escape", game.getSkin(), "title")).padBottom(80).row();

        TextButton goToGameButton = new TextButton("Go To Game", game.getSkin());
        TextButton testingButton = new TextButton("Testing", game.getSkin());
        table.add(goToGameButton).width(300).row();
        table.add(testingButton).width(350).row();

        // Set up button listeners
        // Create level selection buttons
        for (LevelMNGR.LevelInfo level : LevelMNGR.getAvailableLevels()) {
            TextButton levelButton = new TextButton(
                    "Level " + level.name() + " (" + level.mapSize() + "x" + level.mapSize() + ")",
                    game.getSkin()
            );levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // If we had a paused game for that level, we’ll resume it
                    // Otherwise we’ll create a new GameScreen
                    game.goToGame(level);
                }
            });
            table.add(levelButton).width(300).padBottom(20).row();
        }

        table.add(testingButton).width(350).row();

        // Load background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/i must rest here a moment - spiritual brother.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update elapsed time
        elapsedTime += delta;

        // Get the current frame of the animation
        Texture currentFrame = animation.getKeyFrame(elapsedTime);

        // Draw the animated background
        spriteBatch.begin();
        spriteBatch.draw(
                currentFrame,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
        );
        spriteBatch.end();

        // Draw the UI
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        for (Texture frame : animation.getKeyFrames()) {
            frame.dispose();
        }
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
}
