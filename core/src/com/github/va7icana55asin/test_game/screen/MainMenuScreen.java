package com.github.va7icana55asin.test_game.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.github.va7icana55asin.test_game.utility.DIFFICULTY;

public class MainMenuScreen implements Screen {
    private final Main game;

    private OrthographicCamera camera;

    public MainMenuScreen(final Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false,640f,480f);
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Test Game!!! ", 100, 150);
        game.font.draw(game.batch, "Press \'enter\' to begin!", 100, 100);
        game.batch.end();

        setInputProcessor();
    }

    private void setInputProcessor(){
        Gdx.input.setInputProcessor(new InputAdapter(){
            @Override
            public boolean keyDown(int keycode){
                if(keycode == Input.Keys.ENTER){
                    game.setScreen(new GameScreen(game, DIFFICULTY.EASY));
                    dispose();
                }
                return true;
            }
        });
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

    }
}
