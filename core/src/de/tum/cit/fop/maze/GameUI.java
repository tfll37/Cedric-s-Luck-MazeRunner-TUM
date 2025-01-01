//package de.tum.cit.fop.maze;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.scenes.scene2d.Stage;
//import com.badlogic.gdx.scenes.scene2d.ui.Label;
//import com.badlogic.gdx.scenes.scene2d.ui.Table;
//import com.badlogic.gdx.utils.viewport.ScreenViewport;
//import com.badlogic.gdx.scenes.scene2d.ui.Skin;
//
//
//
//public class GameUI {
//    private Stage stage;
//    private Label healthLabel;
//    private Label scoreLabel;
//
//    Skin skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json"));
//
//
//    public GameUI(SpriteBatch spriteBatch) {
//        stage = new Stage(new ScreenViewport(), spriteBatch);
//
//        // Set up UI layout
//        Table table = new Table();
//        table.setFillParent(true);
//        stage.addActor(table);
//
//        // Initialize UI components
//        BitmapFont font = new BitmapFont(Gdx.files.internal("craft/font-export.fnt"));
//
//        // Create LabelStyle with the font
//        Label.LabelStyle labelStyle = new Label.LabelStyle();
//        labelStyle.font = font;
//
//        healthLabel = new Label("Health: 100", new Label.LabelStyle());
//        scoreLabel = new Label("Score: 0", new Label.LabelStyle());
//
//        // Add components to table
//        table.top();
//        table.add(healthLabel).padTop(10).left().padLeft(10);
//        table.add().expandX(); // Spacer
//        table.add(scoreLabel).padTop(10).right().padRight(10);
//    }
//
//    public void updateHealth(int health) {
//        healthLabel.setText("Health: " + health);
//    }
//
//    public void updateScore(int score) {
//        scoreLabel.setText("Score: " + score);
//    }
//
//    public void render() {
//        stage.act();
//        stage.draw();
//    }
//
//    public void dispose() {
//        stage.dispose();
//    }
//
//    public Stage getStage() {
//        return stage;
//    }
//}
