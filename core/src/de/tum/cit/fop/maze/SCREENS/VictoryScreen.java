package de.tum.cit.fop.maze.SCREENS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.fop.maze.MAZELOGIC.LevelMNGR;
import de.tum.cit.fop.maze.MazeRunnerGame;

public class VictoryScreen {
    private final Stage stage;
    private final MazeRunnerGame game;
    private final GameScreen gameScreen;
    private boolean visible;
    private final LevelMNGR.LevelInfo currentLevel;

    public VictoryScreen(MazeRunnerGame game, GameScreen gameScreen, LevelMNGR.LevelInfo level) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        this.visible = false;
        this.currentLevel = level;

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Victory title
        Label victoryLabel = new Label("Level Complete!", game.getSkin(), "title");
        victoryLabel.setAlignment(Align.center);

        // Score display (if you want to show final score)
        Label scoreLabel = new Label("Level: " + currentLevel.name(), game.getSkin());
        scoreLabel.setAlignment(Align.center);

        // Create buttons
        TextButton nextLevelButton = new TextButton("Next Level", game.getSkin());
        TextButton menuButton = new TextButton("Return to Menu", game.getSkin());

        nextLevelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int nextLevelIndex = currentLevel.Level() + 1;
                LevelMNGR.LevelInfo nextLevel = LevelMNGR.getLevel(nextLevelIndex);
                if (nextLevel != null) {
                    Gdx.input.setInputProcessor(null);
                    game.goToGame(nextLevel);
                } else {
                    game.goToMenu();
                }
            }
        });

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });

        table.add(victoryLabel).padBottom(50).row();
        table.add(scoreLabel).padBottom(30).row();
        table.add(nextLevelButton).width(200).pad(10).row();
        table.add(menuButton).width(200).pad(10);

        stage.addActor(table);
    }

    public void show() {
        visible = true;
        Gdx.input.setInputProcessor(stage);
    }

    public void hide() {
        visible = false;
        Gdx.input.setInputProcessor(gameScreen);
    }

    public void render() {
        if (!visible) return;

        // Create celebratory background effect
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.graphics.getGL20().glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        SpriteBatch batch = game.getSpriteBatch();
        batch.begin();
        // Draw a semi-transparent golden overlay for victory feel
        batch.setColor(1, 0.8f, 0, 0.3f);
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