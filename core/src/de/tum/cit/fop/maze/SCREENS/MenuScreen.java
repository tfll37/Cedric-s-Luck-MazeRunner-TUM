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

        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music/button-click-289742.mp3"));

        var camera = new com.badlogic.gdx.graphics.OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());

        Array<Texture> frames = new Array<>();
        for (int i = 1; i <= 181; i += 2) {
            String framePath = String.format("assets/menuvid/ezgif-frame-%03d.jpg", i);
            frames.add(new Texture(Gdx.files.internal(framePath)));
        }
        animation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);

        Texture normalTexture = new Texture(Gdx.files.internal("assets/Buttons/[1] Normal.png"));
        Texture hoverTexture = new Texture(Gdx.files.internal("assets/Buttons/[3] Hover.png"));
        Texture clickedTexture = new Texture(Gdx.files.internal("assets/Buttons/[2] Clicked.png"));

        Drawable normalDrawable = new TextureRegionDrawable(normalTexture);
        Drawable hoverDrawable = new TextureRegionDrawable(hoverTexture);
        Drawable clickedDrawable = new TextureRegionDrawable(clickedTexture);

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
                buttonClickSound.play();

                table.clear();

                table.add(new Label("Select Level", game.getSkin(), "title"))
                        .padTop(50)
                        .padBottom(50)
                        .row();

                for (LevelMNGR.LevelInfo level : LevelMNGR.getAvailableLevels()) {
                    TextButton levelButton = new TextButton(
                            level.name() + " (" + level.difficulty() + ")",
                            game.getSkin()
                    );
                    levelButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            buttonClickSound.play();
                            game.goToGame(level);
                        }
                    });
                    table.add(levelButton).width(300).padBottom(20).row();
                }

                TextButton backButton = new TextButton("Back", game.getSkin());
                backButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        buttonClickSound.play();
                        game.goToMenu();
                    }
                });
                table.add(backButton).width(200).padTop(30);
            }
        });
        table.add(startGameButton).width(600).height(100).padBottom(60).row();
        startGameButton.getLabel().setAlignment(Align.center);

        TextButton.TextButtonStyle quitButtonStyle = new TextButton.TextButtonStyle();
        quitButtonStyle.up = normalDrawable;
        quitButtonStyle.over = hoverDrawable;
        quitButtonStyle.down = clickedDrawable;
        quitButtonStyle.font = game.getSkin().getFont("title");

        TextButton quitButton = new TextButton("Quit Game", quitButtonStyle);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buttonClickSound.play();
                Gdx.app.exit();
            }
        });
        table.add(quitButton).width(600).height(100).padBottom(60).row();
        quitButton.getLabel().setAlignment(Align.center);

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/TEMPLAR'S RESPITE _ Meditative Chant & Crackling Fire Sounds _ ASMR_3 (1).mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);

        startButtonStyle.font.getData().setScale(0.7f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        elapsedTime += delta;

        Texture currentFrame = animation.getKeyFrame(elapsedTime);

        spriteBatch.begin();
        spriteBatch.draw(
                currentFrame,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
        );
        spriteBatch.end();

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
