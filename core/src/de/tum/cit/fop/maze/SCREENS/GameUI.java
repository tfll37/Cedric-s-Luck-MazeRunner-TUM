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
    private int score = 0;

    //UI Bars
    private ProgressBar healthBar;
    private Label doorUnlockLabel;


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

        // Make the health bar smaller
        healthSection.add(healthLabel).left().padRight(5);
        healthSection.add(healthBar).width(80).height(20); // Smaller width & height

        // Score label
        scoreLabel = new Label("Score: 0", skin);

        // Add healthSection to the left and the scoreLabel to the right
        // Use empty cell (expandX) to push the score to the right
        topBar.add(healthSection).left().padLeft(10).padRight(10);
        topBar.add().expandX();                 // This empty cell expands and pushes next cell to the right
        topBar.add(scoreLabel).right().pad(10); // Score label aligned right with some padding
        doorUnlockLabel = new Label("Needed to Unlock: 20", skin);

        mainTable.add(topBar).growX().top();
        mainTable.row();
        mainTable.add(doorUnlockLabel).right().pad(10);
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
        this.score+=score;
        scoreLabel.setText("Score: " + this.score);

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
    public void setDoorUnlockProgress(int remainingRolls) {
        doorUnlockLabel.setText("Needed to Unlock: " + remainingRolls);
    }
    public void dispose() {
        stage.dispose();
    }
}