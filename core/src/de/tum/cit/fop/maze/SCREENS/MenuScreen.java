package de.tum.cit.fop.maze.SCREENS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
    private Sound buttonClickSound;

    public MenuScreen(MazeRunnerGame game) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();

        // Load button click sound
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music/button-click-289742.mp3"));

        // Set up the camera and viewport
        var camera = new com.badlogic.gdx.graphics.OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());

        // Load animation frames from the menuvid folder
        Array<Texture> frames = new Array<>();
        for (int i = 12; i <= 181; i += 2) { // Assuming 200 frames
            String framePath = String.format("menuvid/ezgif-frame-%03d.jpg", i);
            frames.add(new Texture(Gdx.files.internal(framePath)));
        }
        animation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);

        // Load button textures
        Texture normalTexture = new Texture(Gdx.files.internal("assets/Buttons/[1] Normal.png"));
        Texture hoverTexture = new Texture(Gdx.files.internal("assets/Buttons/[3] Hover.png"));
        Texture clickedTexture = new Texture(Gdx.files.internal("assets/Buttons/[2] Clicked.png"));

        Drawable normalDrawable = new TextureRegionDrawable(normalTexture);
        Drawable hoverDrawable = new TextureRegionDrawable(hoverTexture);
        Drawable clickedDrawable = new TextureRegionDrawable(clickedTexture);

        // Set up the UI
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new Label("Diddy Party Escape", game.getSkin(), "title"))
                .padTop(9)
                .padBottom(200)
                .row();

        // Start Game Button
        TextButton.TextButtonStyle startButtonStyle = new TextButton.TextButtonStyle();
        startButtonStyle.up = normalDrawable;
        startButtonStyle.over = hoverDrawable;
        startButtonStyle.down = clickedDrawable;
        startButtonStyle.font = game.getSkin().getFont("title");

        TextButton startGameButton = new TextButton("Start Game", startButtonStyle);
        startGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Play button click sound
                buttonClickSound.play();

                // Dispose buttons and start game
                disposeButtons();
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


                // Start Game action
                LevelMNGR.LevelInfo tutorialLevel = LevelMNGR.getAvailableLevels().get(0); // Assuming first level is tutorial
                game.goToGame(tutorialLevel);
            }
        });
        table.add(startGameButton).width(600).height(100).padBottom(60).row();
        startGameButton.getLabel().setAlignment(Align.center);

        // Settings Button
        TextButton.TextButtonStyle settingsButtonStyle = new TextButton.TextButtonStyle();
        settingsButtonStyle.up = normalDrawable;
        settingsButtonStyle.over = hoverDrawable;
        settingsButtonStyle.down = clickedDrawable;
        settingsButtonStyle.font = game.getSkin().getFont("title");

        TextButton settingsButton = new TextButton("Settings", settingsButtonStyle);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Play button click sound
                buttonClickSound.play();

                // Dispose buttons and open settings (currently does nothing)
                disposeButtons();
            }
        });
        table.add(settingsButton).width(600).height(100).padBottom(60).row();

        // Load background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets//music//Wallpaper Engine - Batman Arkham Knight - Batman Overlooking Gotham from Wayne Tower_1.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);

        startButtonStyle.font.getData().setScale(0.7f);
        settingsButtonStyle.font.getData().setScale(0.7f);
    }
    private void disposeButtons() {
        stage.clear();
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
        if (buttonClickSound != null) {
            buttonClickSound.dispose();
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
