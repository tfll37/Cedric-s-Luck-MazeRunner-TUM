package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {
    //variables
    private final Stage stage;
    private final MazeRunnerGame game;


    /**
     * CONSTRUCTOR.
     *
     * Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        this.game = game;

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view


        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("Diddy party escape", game.getSkin(), "title")).padBottom(80).row();

        // Create and add buttons
        TextButton goToGameButton = new TextButton("Go To Game", game.getSkin());
        TextButton testingButton = new TextButton("Testing", game.getSkin());

        table.add(goToGameButton).width(300).row();
        table.add(testingButton).width(350).row();

        //Set up the button listeners
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame(); // Change to the game screen when button is pressed
            }
        });

        testingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });
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

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }




    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
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

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
