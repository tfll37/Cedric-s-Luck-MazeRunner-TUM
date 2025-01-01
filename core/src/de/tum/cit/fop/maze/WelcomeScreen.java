package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WelcomeScreen implements Screen {
    //variables
    private final Stage stage;
    private final MazeRunnerGame game;
    private final Label pressKeyLabel;


    public WelcomeScreen(MazeRunnerGame game) {
        this.game = game;

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f;

        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());

        pressKeyLabel = new Label("Press Key", game.getSkin(), "default");
        pressKeyLabel.setFontScale(5.5f);
        pressKeyLabel.setAlignment(Align.center);
        pressKeyLabel.setColor(Color.RED);

        Table table = new Table();
        table.setFillParent(true);
        table.add(pressKeyLabel).expandX().center();
        stage.addActor(table);

//        TextButton startButton = new TextButton("NNNN", game.getSkin());
//        startButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                game.goToMenu();
//            }
//        });
//
//        table.add(startButton).width(500).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Ensure backgroundTexture is not null
        if (game.getBackgroundTexture() != null) {
            game.getSpriteBatch().begin();
            game.getSpriteBatch().draw(
                    game.getBackgroundTexture(),
                    0, 0,
                    stage.getViewport().getWorldWidth(),
                    stage.getViewport().getWorldHeight()
            );
            game.getSpriteBatch().end();
        }

        // Pulsing effect for the label
        float alpha = Math.abs(MathUtils.sinDeg((System.currentTimeMillis() / 10) % 360));
        pressKeyLabel.getColor().a = alpha;// Adjust transparency dynamically


        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Check for key press or touch input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isTouched()) {
            game.goToMenu(); // Transition to MenuScreen
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }

}
