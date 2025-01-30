package de.tum.cit.fop.maze.SCREENS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

/**
 * The type Pause menu screen.
 */
public class PauseMenuScreen {
    private final Stage stage;
    private final MazeRunnerGame game;
    private final GameScreen gameScreen;
    private boolean visible;

    /**
     * Instantiates a new Pause menu screen.
     *
     * @param game       the game
     * @param gameScreen the game screen
     */
    public PauseMenuScreen(MazeRunnerGame game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        this.visible = false;

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Create buttons
        TextButton resumeButton = new TextButton("Resume", game.getSkin());
        TextButton menuButton = new TextButton("Menu", game.getSkin());

        // Add listeners
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });

        // Add buttons to table with spacing
        table.add(resumeButton).width(200).pad(10);
        table.row();
        table.add(menuButton).width(200).pad(10);

        stage.addActor(table);
    }

    /**
     * Show.
     */
    public void show() {
        visible = true;
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Hide.
     */
    public void hide() {
        visible = false;
        // Reset input processor back to game screen
        gameScreen.setIsPaused(false);
        Gdx.input.setInputProcessor(gameScreen);
    }

    /**
     * Render.
     */
    public void render() {
        if (!visible) return;

        // Darken the background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.graphics.getGL20().glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw semi-transparent overlay
        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();
        // Draw a semi-transparent black overlay
        batch.setColor(0, 0, 0, 0.5f);
        // Draw a rectangle covering the entire screen
        batch.draw(game.getBackgroundTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(1, 1, 1, 1); // Reset color
        batch.end();

        stage.act();
        stage.draw();
    }

    /**
     * Is visible boolean.
     *
     * @return the boolean
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Resize.
     *
     * @param width  the width
     * @param height the height
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Gets stage.
     *
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Dispose.
     */
    public void dispose() {
        stage.dispose();
    }
}