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

public class WelcomeScreen implements Screen {
    // Variables
    private final Stage stage;
    private final MazeRunnerGame game;
    private final Label storyLabel;

    private final List<String> storyLines;
    private int currentLineIndex;
    private int clickCount;
    private boolean waitForNextClick;

    public WelcomeScreen(MazeRunnerGame game) {
        this.game = game;

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f;

        Viewport viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, game.getSpriteBatch());

        // Initialize story text lines
        storyLines = new ArrayList<>();
        storyLines.add("In the shadowed depths of Macao Maze, brave Sir Cedric roams,\n");
        storyLines.add("Seeking the Sacred Crystal, through the darkened catacombs.\n");
        storyLines.add("Ghosts of fallen warriors whisper secrets of the past,\n");
        storyLines.add("As he faces lurking dangers, hoping freedom's within grasp and a roll„.\n");
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
        Gdx.gl.glClearColor(0, 0, 0, 1); // Set background to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Handle input to advance the story or transition to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isTouched()) {
            if (waitForNextClick) {
                waitForNextClick = false;
                return;
            }

            clickCount++;
            if (clickCount < 3) {
                if (currentLineIndex < storyLines.size() - 1) {
                    currentLineIndex++;
                    storyLabel.setText(storyLines.get(currentLineIndex));
                    if (currentLineIndex == 1) {
                        waitForNextClick = true; // Require another click from the second text
                    }
                }
            } else {
                game.goToMenu(); // Transition to MenuScreen
            }
        }
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
