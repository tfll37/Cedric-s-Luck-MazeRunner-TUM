package de.tum.cit.fop.maze.SCREENS;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;
import de.tum.cit.fop.maze.PC_NPC_OBJ.Enemy;
import de.tum.cit.fop.maze.PC_NPC_OBJ.Player;

public class GameUI {
    private Stage stage;
    private Table mainTable;

    // UI Labels
    private Label healthLabel;
    private Label scoreLabel;

    //UI Bars
    private ProgressBar healthBar;

    private final Skin skin;
    private boolean isPaused;

    public GameUI(SpriteBatch spriteBatch, Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport(), spriteBatch);
        this.isPaused = false;

        initializeUI();
    }

    private void initializeUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Create top bar for game stats
        Table topBar = new Table();
        topBar.top().pad(10);

        // Health section
        Table healthSection = new Table();
        healthLabel = new Label("Health: 100", skin);
        healthBar = new ProgressBar(0, 100, 1, false, skin);
        healthBar.setValue(100);
        healthSection.add(healthLabel).left().padRight(5);
        healthSection.add(healthBar).width(150);

        // Score and timer
        scoreLabel = new Label("Score: 0", skin);

        // Add all elements to top bar
        topBar.add(healthSection).left().expandX();
        topBar.add(scoreLabel).center().expandX();

        mainTable.add(topBar).growX().top();
    }

    public void update(float delta, Player player, Enemy enemy) {
        if (!isPaused) {
        }
        stage.act(delta);
        this.updateHealth(player.getHealth());
    }

    public void updateHealth(float health) {
        healthLabel.setText("Health: " + health);
        healthBar.setValue(health);

        // Visual feedback for low health
        if (health <= 20) {
            healthLabel.setColor(Color.RED);
        } else if (health <= 50) {
            healthLabel.setColor(Color.ORANGE);
        } else {
            healthLabel.setColor(Color.WHITE);
        }
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    public void render() {
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }
}