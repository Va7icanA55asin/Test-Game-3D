package com.github.va7icana55asin.test_game.event;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.github.va7icana55asin.test_game.screen.GameScreen;

public class GameInputProcessor implements InputProcessor {
    
    private GameScreen screen;

    public GameInputProcessor(GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode){
            case Keys.LEFT:
                screen.setLeftMove(true);
                break;
            case Keys.RIGHT:
                screen.setRightMove(true);
                break;
            case Keys.UP:
                screen.setUpMove(true);
                break;
            case Keys.DOWN:
                screen.setDownMove(true);
                break;
            case Keys.P:
                if(screen.isPaused()){
                    screen.resume();
                }else {
                    screen.pause();
                }
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode){
            case Keys.LEFT:
                screen.setLeftMove(false);
                break;
            case Keys.RIGHT:
                screen.setRightMove(false);
                break;
            case Keys.UP:
                screen.setUpMove(false);
                break;
            case Keys.DOWN:
                screen.setDownMove(false);
                break;
        }
        return true;
    }

    
    //All unused
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
