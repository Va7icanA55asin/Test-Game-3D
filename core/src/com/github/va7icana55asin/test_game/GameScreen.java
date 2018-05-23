package com.github.va7icana55asin.test_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final Main game;

    private OrthographicCamera camera;
    private Texture playerSprite;
    private Texture projectileSpriteRight;
    private Texture projectileSpriteLeft;
    private Texture projectileSpriteUp;
    private Texture projectileSpriteDown;
    private Music music;
    private Sound hit;
    private Rectangle rectangle;
    private Array<Projectile> projectiles;
    private long lastLaunchTime;
    private int health;
    private float timer;
    //private int difficulty; //will change with difficulty as will speed and timer. This will probably become a argument in the constructor
    //private int speed;
    //private ArrayList<Rectangle> powerUps;

    /*
    TODO Modify title screen to select difficulty (This will take some figuring out) (Will increase spawn amount and time to win and decrease time between spawns for harder difficulties)
    TODO Change backgrounds to something. Maybe an image
    TODO See if gifs can be used. (Use "You Died" from dark souls if possible)(Actually might not be a good idea)(Maybe make a spoof)
    TODO Maybe eventually add power-ups
    TODO Maybe add settings for on the main menu (Would have to be written to file)
     */

    public GameScreen(final Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false,640f,480f);
        playerSprite = new Texture("player.png");
        projectileSpriteRight = new Texture("projectile-right.png");
        projectileSpriteLeft = new Texture("projectile-left.png");
        projectileSpriteUp = new Texture("projectile-up.png");
        projectileSpriteDown = new Texture("projectile-down.png");
        hit = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        music = Gdx.audio.newMusic(Gdx.files.internal("basic-beat.wav"));
        music.setVolume(0.5f);
        music.setLooping(true);
        rectangle = new Rectangle();
        rectangle.x = 320;
        rectangle.y = 240;
        rectangle.width = 32;
        rectangle.height = 32;

        //Have switch for difficulty here
        timer = 90; //Should be in seconds
        health = 5;

        projectiles = new Array<Projectile>();
        spawnProjectiles();
    }

    private void spawnProjectiles(){
        Projectile projectile = new Projectile();
        int side = MathUtils.random(1,4);
        switch(side){
            case 1: //Bottom of the screen
                projectile.direction = Projectile.DIRECTION.UP;
                projectile.x = MathUtils.random(0,640-32);
                projectile.y = 0;
                break;

            case 2: //Left side of the screen
                projectile.direction = Projectile.DIRECTION.RIGHT;
                projectile.x = 0;
                projectile.y = MathUtils.random(0,480-32);
                break;

            case 3: //Right side of the screen
                projectile.direction = Projectile.DIRECTION.LEFT;
                projectile.x = 640;
                projectile.y = MathUtils.random(0,480-32);
                break;

            case 4: //Top of the screen
                projectile.direction = Projectile.DIRECTION.DOWN;
                projectile.x = MathUtils.random(0,640-32);
                projectile.y = 480;
                break;
        }

        projectile.height = 32;
        projectile.width = 32;

        projectiles.add(projectile);
        lastLaunchTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta){
        if(timer <= 0){
            game.setScreen(new GameWinScreen(game));
            dispose();
        }else {
            Gdx.gl.glClearColor(1, 0, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            camera.update();
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            game.font.draw(game.batch, "Health: " + health, 0, 480); //Draw the health counter or meter. Meter would probably require various textures and a switch statement
            game.font.draw(game.batch, "Time Remaining: " + (int) timer + " seconds", 0, 460); //Draw the timer
            game.batch.draw(playerSprite, rectangle.x, rectangle.y);
            for (Projectile projectile : projectiles) {
                Texture projectileTexture = null;
                switch (projectile.direction) {
                    case UP:
                        projectileTexture = projectileSpriteUp;
                        break;

                    case DOWN:
                        projectileTexture = projectileSpriteDown;
                        break;

                    case RIGHT:
                        projectileTexture = projectileSpriteRight;
                        break;

                    case LEFT:
                        projectileTexture = projectileSpriteLeft;
                        break;
                }
                game.batch.draw(projectileTexture, projectile.x, projectile.y);
            }
            game.batch.end();

            if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                rectangle.x = touchPos.x - 64 / 2;
                rectangle.y = touchPos.y - 64 / 2;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                rectangle.x -= 200 * Gdx.graphics.getDeltaTime();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                rectangle.x += 200 * Gdx.graphics.getDeltaTime();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                rectangle.y += 200 * Gdx.graphics.getDeltaTime();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                rectangle.y -= 200 * Gdx.graphics.getDeltaTime();
            }

            if (rectangle.x < 0) {
                rectangle.x = 0;
            }

            if (rectangle.x > 640 - 32) {
                rectangle.x = 640 - 32;
            }

            if (rectangle.y < 0) {
                rectangle.y = 0;
            }

            if (rectangle.y > 480 - 32) {
                rectangle.y = 480 - 32;
            }

            if (TimeUtils.nanoTime() - lastLaunchTime > 1000000000) { //This number will change for varying difficulty
                timer--;
                spawnProjectiles();
            }

            Iterator<Projectile> iter = projectiles.iterator();
            while (iter.hasNext()) {
                Projectile projectile = iter.next();
                switch (projectile.direction) {
                    case UP:
                        projectile.y += 200 * Gdx.graphics.getDeltaTime(); //These speeds will change with difficulty
                        if (projectile.y + 32 > 480) {
                            iter.remove();
                        }
                        break;

                    case DOWN:
                        projectile.y -= 200 * Gdx.graphics.getDeltaTime();
                        if (projectile.y + 32 < 0) {
                            iter.remove();
                        }
                        break;

                    case LEFT:
                        projectile.x -= 200 * Gdx.graphics.getDeltaTime();
                        if (projectile.x + 32 < 0) {
                            iter.remove();
                        }
                        break;

                    case RIGHT:
                        projectile.x += 200 * Gdx.graphics.getDeltaTime();
                        if (projectile.x + 32 > 640) {
                            iter.remove();
                        }
                        break;
                }

                if (projectile.overlaps(rectangle)) {
                    hit.play();
                    iter.remove();
                    if (--health <= 0) {
                        game.setScreen(new GameOverScreen(game));
                        dispose();
                    }
                }
            }
        }
    }

    @Override
    public void show(){
        music.play();
    }

    @Override
    public void dispose(){
        playerSprite.dispose();
        projectileSpriteRight.dispose();
        projectileSpriteDown.dispose();
        projectileSpriteUp.dispose();
        projectileSpriteLeft.dispose();
        hit.dispose();
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
