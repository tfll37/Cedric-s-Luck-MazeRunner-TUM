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

public class PauseMenuScreen {
    private final Stage stage;
    private final MazeRunnerGame game;
    private final GameScreen gameScreen;
    private boolean visible;

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
        TextButton menuButton = new TextButton("Return to Menu", game.getSkin());

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

    public void show() {
        visible = true;
        Gdx.input.setInputProcessor(stage);
    }

    public void hide() {
        visible = false;
        // Reset input processor back to game screen
        gameScreen.setIsPaused(false);
        Gdx.input.setInputProcessor(gameScreen);
    }

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

    public boolean isVisible() {
        return visible;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
    }
}