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
     * <p>
     * Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        this.game = game;

        var camera = new OrthographicCamera();
    camera.zoom = 1.5f;

        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());


        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Add a label as a title
        table.add(new Label("Diddy party escape", game.getSkin(), "title")).padBottom(80).row();

        // Create level selection buttons
        for (LevelMNGR.LevelInfo level : LevelMNGR.getAvailableLevels()) {
            TextButton levelButton = new TextButton(
                    "Level " + level.name() + " (" + level.mapSize() + "x" + level.mapSize() + ")",
                    game.getSkin()
            );
            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.goToGame(level);
                }
            });
            table.add(levelButton).width(300).padBottom(20).row();
        }
        TextButton testingButton = new TextButton("Testing", game.getSkin());

        table.add(testingButton).width(350).row();

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

    private void createLevelButtons() {
        Table levelTable = new Table();
        for (LevelMNGR.LevelInfo level : LevelMNGR.getAvailableLevels()) {
            TextButton levelButton = new TextButton(level.name(), game.getSkin());
            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.goToGame(level);  // Need to modify MazeRunnerGame
                }
            });
            levelTable.add(levelButton).pad(10).row();
        }
        levelTable.add(levelTable);
    }

    private void addLevelSelectButtons(Table table) {
        for (LevelMNGR.LevelInfo level : LevelMNGR.getAvailableLevels()) {
            TextButton levelBtn = new TextButton(
                    level.name() + " (" + level.mapSize() + "x" + level.mapSize() + ")",
                    game.getSkin()
            );
            levelBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.goToGame(level);
                }
            });
            table.add(levelBtn).width(300).pad(10).row();
        }
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
