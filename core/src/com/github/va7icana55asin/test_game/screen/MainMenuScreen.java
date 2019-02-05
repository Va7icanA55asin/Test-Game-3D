package com.github.va7icana55asin.test_game.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.va7icana55asin.test_game.utility.DIFFICULTY;

public class MainMenuScreen implements Screen {
    private final Main game;

    private OrthographicCamera camera;
    private Stage stage;
    private SelectBox<DIFFICULTY> selectBox;

    public MainMenuScreen(final Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false,640f,480f);

        stage = new Stage();

        selectBox = new SelectBox<DIFFICULTY>(new Skin(Gdx.files.internal("skin/uiskin.json")));
        selectBox.setItems(DIFFICULTY.values());
        selectBox.setPosition(100,275);
        selectBox.setWidth(100);
        selectBox.setHeight(25);

        stage.addActor(selectBox);

        setInputProcessor();
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Test Game!!! ", 100, 400);
        game.font.draw(game.batch, "Select a difficulty below then press \'Enter\' to begin!", 100, 350);
        game.batch.end();
    }

    private void setInputProcessor(){
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter(){
            @Override
            public boolean keyDown(int keycode){
                if(keycode == Input.Keys.ENTER){
                    game.setScreen(new GameScreen(game, selectBox.getSelected()));
                    dispose();
                }
                return true;
            }
        }));
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

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
}
