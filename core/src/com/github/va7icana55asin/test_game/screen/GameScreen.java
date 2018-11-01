package com.github.va7icana55asin.test_game.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.va7icana55asin.test_game.event.GameInputProcessor;
import com.github.va7icana55asin.test_game.utility.DIFFICULTY;
import com.github.va7icana55asin.test_game.utility.Projectile;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final Main game;

    private OrthographicCamera camera;
    private boolean leftMove;
    private boolean rightMove;
    private boolean upMove;
    private boolean downMove;
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
    private int timer;
    private long lastTime;
    private int speed;
    private int launchFrequency;
    //private ArrayList<Rectangle> powerUps;

    /*
    TODO Maybe eventually add power-ups
    TODO Modify title screen to select difficulty (This will take some figuring out) (These two will need scene2D for buttons)
    TODO Maybe add settings for on the main menu (Would have to be written to file)
    TODO Replace all hard screen size reference numbers to a variable when settings are determined
     */

    public GameScreen(final Main game, DIFFICULTY difficulty) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false,640f,480f);
        Gdx.input.setInputProcessor(new GameInputProcessor(this));
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

        switch(difficulty){
            case BABY:
                timer = 90;
                health = 5;
                speed = 200;
                launchFrequency = 1000000000;
                break;

            case EASY:
                timer = 120;
                health = 4;
                speed = 225;
                launchFrequency = 750000000;
                break;

            case MEDIUM:
                timer = 150;
                health = 3;
                speed = 250;
                launchFrequency = 500000000;
                break;

            case HARD:
                timer = 180;
                health = 2;
                speed = 275;
                launchFrequency = 250000000;
                break;

            case EXTREME:
                timer = 210;
                health =1;
                speed = 300;
                launchFrequency = 10000000;
                break;
        }

        lastTime = TimeUtils.nanoTime();
        projectiles = new Array<Projectile>();
        spawnProjectile();
    }

    private void spawnProjectile(){
        Projectile projectile = new Projectile();
        int side = MathUtils.random(1,4);
        switch(side){
            case 1: //Bottom of the screen
                projectile.direction = Projectile.DIRECTION.UP;
                projectile.texture = projectileSpriteUp;
                projectile.x = MathUtils.random(0,640-32);
                projectile.y = 0;
                break;

            case 2: //Left side of the screen
                projectile.direction = Projectile.DIRECTION.RIGHT;
                projectile.texture = projectileSpriteRight;
                projectile.x = 0;
                projectile.y = MathUtils.random(0,480-32);
                break;

            case 3: //Right side of the screen
                projectile.direction = Projectile.DIRECTION.LEFT;
                projectile.texture = projectileSpriteLeft;
                projectile.x = 640;
                projectile.y = MathUtils.random(0,480-32);
                break;

            case 4: //Top of the screen
                projectile.direction = Projectile.DIRECTION.DOWN;
                projectile.texture = projectileSpriteDown;
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
            game.font.draw(game.batch, "Time Remaining: " + timer + " seconds", 0, 460); //Draw the timer
            game.batch.draw(playerSprite, rectangle.x, rectangle.y);
            for (Projectile projectile : projectiles) {
                game.batch.draw(projectile.texture, projectile.x, projectile.y);
            }
            game.batch.end();

            updateMotion();

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

            if(TimeUtils.nanoTime() - lastLaunchTime > launchFrequency){
                spawnProjectile();
            }

            moveProjectiles(projectiles);
        }
    }

    private void updateMotion(){
        if(leftMove){
            rectangle.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (rightMove){
            rectangle.x += 200 * Gdx.graphics.getDeltaTime();
        }
        if (upMove){
            rectangle.y += 200 * Gdx.graphics.getDeltaTime();
        }
        if (downMove){
            rectangle.y -= 200 * Gdx.graphics.getDeltaTime();
        }
    }

    public void setLeftMove(boolean bool) {
        if(rightMove && bool){
            rightMove = false;
        }
        leftMove = bool;
    }

    public void setRightMove(boolean bool) {
        if(leftMove && bool){
            leftMove = false;
        }
        rightMove = bool;
    }

    public void setUpMove(boolean bool) {
        if(downMove && bool){
            downMove = false;
        }
        upMove = bool;
    }

    public void setDownMove(boolean bool) {
        if(upMove && bool){
            upMove = false;
        }
        downMove = bool;
    }

    private void moveProjectiles(Array<Projectile> projectiles){
        Iterator<Projectile> iter = projectiles.iterator();
        while (iter.hasNext()) {
            Projectile projectile = iter.next();
            switch (projectile.direction) {
                case UP:
                    projectile.y += speed * Gdx.graphics.getDeltaTime();
                    if (projectile.y + 32 > 480) {
                        iter.remove();
                    }
                    break;

                case DOWN:
                    projectile.y -= speed * Gdx.graphics.getDeltaTime();
                    if (projectile.y + 32 < 0) {
                        iter.remove();
                    }
                    break;

                case LEFT:
                    projectile.x -= speed * Gdx.graphics.getDeltaTime();
                    if (projectile.x + 32 < 0) {
                        iter.remove();
                    }
                    break;

                case RIGHT:
                    projectile.x += speed * Gdx.graphics.getDeltaTime();
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

            if(TimeUtils.nanoTime() - lastTime > 1000000000){
                timer--;
                lastTime = TimeUtils.nanoTime();
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
