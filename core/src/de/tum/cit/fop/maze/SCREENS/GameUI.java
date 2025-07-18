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

    private Label healthLabel;
    private Label scoreLabel;
    private int currentScore = 0;

    private ProgressBar healthBar;
    private Label doorUnlockLabel;
    private Label dashCountLabel;

    private final Skin skin;
    private boolean isPaused;

    private int requiredScore;
    private Label messageLabel;
    private float messageTimer = 0;
    private static final float MESSAGE_DURATION = 3.0f;

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

        Table topBar = new Table();
        topBar.top().pad(10);

        Table healthSection = new Table();
        healthLabel = new Label("Health: 100", skin);
        healthBar = new ProgressBar(0, 100, 1, false, skin);
        healthBar.setValue(100);

        healthSection.add(healthLabel).left().padRight(5);
        healthSection.add(healthBar).width(80).height(20); // Smaller width & height

        scoreLabel = new Label("Score: 0 / 0", skin);
        messageLabel = new Label("", skin);
        messageLabel.setVisible(false);


        topBar.add(healthSection).left().padLeft(10).padRight(10);
        topBar.add().expandX();                 // This empty cell expands and pushes next cell to the right
        topBar.add(scoreLabel).right().pad(10); // Score label aligned right with some padding
        doorUnlockLabel = new Label("Needed to Unlock: TBD" , skin);
        dashCountLabel = new Label("Dashcount: TBD", skin);
        mainTable.add(topBar).growX().top();
        mainTable.row();
        mainTable.add(doorUnlockLabel).right().pad(10);
        mainTable.row();
        mainTable.add(dashCountLabel).right().pad(10);
    }

    public void update(float delta, Player player, Enemy enemy) {
        if (!isPaused) {
        }
        stage.act(delta);
        this.updateHealth(player.getHealth());

        if (messageTimer > 0) {
            messageTimer -= delta;
            if (messageTimer <= 0) {
                messageLabel.setVisible(false);
            }
        }

    }
    public void update(float delta, Player player) {
        if (!isPaused) {
        }
        stage.act(delta);
        this.updateHealth(player.getHealth());

        if (messageTimer > 0) {
            messageTimer -= delta;
            if (messageTimer <= 0) {
                messageLabel.setVisible(false);
            }
        }

    }


    public void updateHealth(float health) {
        healthLabel.setText("Health: " + health);
        healthBar.setValue(health);

        if (health <= 20) {
            healthLabel.setColor(Color.RED);
        } else if (health <= 50) {
            healthLabel.setColor(Color.ORANGE);
        } else {
            healthLabel.setColor(Color.WHITE);
        }
    }


    public void setScoreRequirement(int required) {
        this.requiredScore = required;
        updateScoreDisplay();
    }

    public void updateScore(int currentScore) {
        this.currentScore = this.currentScore + currentScore;
        updateScoreDisplay();
    }

    private void updateScoreDisplay() {
        scoreLabel.setText(String.format("Score: %d", currentScore));
    }

    public void showScoreRequirementMessage(int remaining) {
        messageLabel.setText("Need " + remaining + " more points to complete level!");
        messageLabel.setVisible(true);
        messageTimer = MESSAGE_DURATION;
    }

    public void showLevelCompleteMessage() {
        messageLabel.setText("Level requirements met! Find the exit!");
        messageLabel.setVisible(true);
        messageTimer = MESSAGE_DURATION;
    }

    public void render() {
        stage.draw();
    }

    public void setDoorUnlockProgress(int remainingRolls) {
        doorUnlockLabel.setText("Needed to Unlock: " + remainingRolls);
    }
    public void setDashCount(int dashCount){
        dashCountLabel.setText("Dashcount: " + dashCount);
    }
    public void dispose() {
        stage.dispose();
    }
}