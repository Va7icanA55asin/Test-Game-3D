package com.github.va7icana55asin.test_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen {
    final Main game;

    OrthographicCamera camera;
    Texture img;
    Music music;
    Rectangle rectangle;

    /*
    TODO Create array of projectiles (These will spawn from all sides, use rand with only 2 options for x and y each. IE projectiles will come from top or bottom and left or right)
    Will have to fine tune above with if statements
    TODO Create health bar/counter
    TODO Create overlap that reduces health when hit
    TODO Create timer for how long to survive
    TODO Create game over screen
    TODO Create win screen
    TODO Find new sprite for player
    TODO Modify title screen to select difficulty (This will take some figuring out) (Will increase spawn amount and time to win and decrease time between spawns for harder difficulties)
    TODO Change backgrounds to something. Maybe an image
    TODO See if gifs can be used. (Use "You Died" from dark souls if possible)(Actually might not be a good idea)(Maybe make a spoof)
    TODO Maybe eventually add power-ups
    TODO Maybe add settings for on the main menu
     */

    public GameScreen(final Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false,640f,480f);
        img = new Texture("badlogic.jpg");
        music = Gdx.audio.newMusic(Gdx.files.internal("basic-beat.wav"));
        music.setVolume(0.5f);
        music.setLooping(true);
        rectangle = new Rectangle();
        rectangle.x = 320;
        rectangle.y = 20;
        rectangle.width = 32;
        rectangle.height = 32;
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(img, rectangle.x, rectangle.y);
        game.batch.end();

        if(Gdx.input.isTouched()){
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            rectangle.x = touchPos.x - 64 / 2;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            rectangle.x -=200 * Gdx.graphics.getDeltaTime();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            rectangle.x += 200 * Gdx.graphics.getDeltaTime();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            rectangle.y += 200 * Gdx.graphics.getDeltaTime();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            rectangle.y -= 200 * Gdx.graphics.getDeltaTime();
        }

        if(rectangle.x < 0){
            rectangle.x = 0;
        }

        if(rectangle.x > 640 - 32){
            rectangle.x = 640-32;
        }

        if(rectangle.y < 0){
            rectangle.y = 0;
        }

        if(rectangle.y > 480 - 32){
            rectangle.y = 480 - 32;
        }
    }

    @Override
    public void show(){
        music.play();
    }

    @Override
    public void dispose(){
        img.dispose();
        music.dispose();
    }

    @Override
    public void hide(){
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
}
