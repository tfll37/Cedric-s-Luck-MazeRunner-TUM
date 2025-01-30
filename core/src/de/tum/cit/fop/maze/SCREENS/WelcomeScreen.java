package de.tum.cit.fop.maze.SCREENS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.fop.maze.MazeRunnerGame;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Welcome screen.
 */
public class WelcomeScreen implements Screen {
    // Variables
    private final Stage stage;
    private final MazeRunnerGame game;
    private final Label storyLabel;

    private final List<String> storyLines;
    private int currentLineIndex;
    private int clickCount;
    private boolean waitForNextClick;

    /**
     * Instantiates a new Welcome screen.
     *
     * @param game the game
     */
    public WelcomeScreen(MazeRunnerGame game) {
        this.game = game;

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f;

        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());

        // Initialize story text lines
        storyLines = new ArrayList<>();

        storyLines.add("In the shadowed depths of Macao Maze, brave Sir Cedric treads alone,\n");
        storyLines.add("Through winding paths and ancient stones, far from all he's ever known.\n");
        storyLines.add("Haunted by the tortured souls of those who failed to break their chains,\n");
        storyLines.add("He battles onwards through the dark, where only echoed screams remain.\n");
        storyLines.add("Each roll of fate could lead to freedom or seal his doom in these cold halls,\n");
        storyLines.add("As shadows dance and whisper secrets from their age-old castle walls.\n");

        currentLineIndex = 0;
        clickCount = 0;
        waitForNextClick = false;

        storyLabel = new Label(storyLines.get(currentLineIndex), game.getSkin(), "default");
        storyLabel.setFontScale(3.5f);
        storyLabel.setAlignment(Align.center);
        storyLabel.setWrap(true);
        storyLabel.setColor(Color.WHITE);

        Table table = new Table();
        table.setFillParent(true);
        table.add(storyLabel).expand().center().pad(50).width(stage.getViewport().getWorldWidth() * 0.8f);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
            if (currentLineIndex < storyLines.size() - 1) {
                currentLineIndex++;
                storyLabel.setText(storyLines.get(currentLineIndex));
            } else {
                game.goToMenu();
            }
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 5 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
}
