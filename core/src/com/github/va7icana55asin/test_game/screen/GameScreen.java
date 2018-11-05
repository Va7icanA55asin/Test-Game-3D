package com.github.va7icana55asin.test_game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.github.va7icana55asin.test_game.utility.PowerUp;
import com.github.va7icana55asin.test_game.utility.Projectile;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final Main game;

    private OrthographicCamera camera;
    private int screenWidth;
    private int screenHeight;
    private boolean paused;

    private boolean leftMove;
    private boolean rightMove;
    private boolean upMove;
    private boolean downMove;

    private Texture playerSprite;
    private Texture shieldedPlayerSprite;
    private Texture projectileSpriteRight;
    private Texture projectileSpriteLeft;
    private Texture projectileSpriteUp;
    private Texture projectileSpriteDown;
    private Texture speedBoostSprite;
    private Texture shieldSprite;
    private Texture oneUpSprite;

    private Music music;
    private Sound hit;
    private Sound shieldHit;

    private Rectangle player;
    private int playerBaseSpeed;
    private int playerSpeed;
    private int health;
    private int timer;

    private Array<Projectile> projectiles;
    private long lastLaunchTime;
    private long lastTime;
    private int speed;
    private int launchFrequency;

    private Array<PowerUp> powerUps;
    private long speedBoostActivatedTime;
    private boolean shieldActive;
    private long shieldActivatedTime;
    private long lastSpawnTime;
    private long powerUpFrequency;

    /*
    TODO Modify title screen to select difficulty (This will take some figuring out) (These two will need scene2D for buttons)
    TODO Maybe add settings for on the main menu (Would have to be written to file)
    TODO Replace all hard screen size reference numbers to a variable when settings are determined
     */

    public GameScreen(final Main game, DIFFICULTY difficulty) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false,640f,480f);
        screenWidth = 640; //These will change based on settings passed in
        screenHeight = 480;
        paused = false;
        Gdx.input.setInputProcessor(new GameInputProcessor(this));
        playerSprite = new Texture("player.png");
        shieldedPlayerSprite = new Texture("shieldedPlayer.png");
        projectileSpriteRight = new Texture("projectile-right.png");
        projectileSpriteLeft = new Texture("projectile-left.png");
        projectileSpriteUp = new Texture("projectile-up.png");
        projectileSpriteDown = new Texture("projectile-down.png");
        speedBoostSprite = new Texture("speed-boost.png");
        shieldSprite = new Texture("shield.png");
        oneUpSprite = new Texture("life.png");
        hit = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        shieldHit = Gdx.audio.newSound(Gdx.files.internal("shield-hit.wav"));
        music = Gdx.audio.newMusic(Gdx.files.internal("basic-beat.wav"));
        music.setVolume(0.5f);
        music.setLooping(true);
        player = new Rectangle();
        player.x = 320;
        player.y = 240;
        player.width = 32;
        player.height = 32;

        switch(difficulty){
            case BABY:
                timer = 90;
                health = 5;
                speed = 200;
                launchFrequency = 1000000000;
                powerUpFrequency = 5000000000L;
                break;

            case EASY:
                timer = 120;
                health = 4;
                speed = 225;
                launchFrequency = 750000000;
                powerUpFrequency = 6000000000L;
                break;

            case MEDIUM:
                timer = 150;
                health = 3;
                speed = 250;
                launchFrequency = 500000000;
                powerUpFrequency = 7000000000L;
                break;

            case HARD:
                timer = 180;
                health = 2;
                speed = 275;
                launchFrequency = 250000000;
                powerUpFrequency = 8000000000L;
                break;

            case EXTREME:
                timer = 210;
                health =1;
                speed = 300;
                launchFrequency = 10000000;
                powerUpFrequency = 9000000000L;
                break;
        }

        playerBaseSpeed = playerSpeed = 200;
        shieldActive = false;
        lastTime = TimeUtils.nanoTime();
        lastSpawnTime = TimeUtils.nanoTime();
        projectiles = new Array<Projectile>();
        powerUps = new Array<PowerUp>();
        spawnProjectile();
    }

    private void spawnProjectile(){
        Projectile projectile = new Projectile();
        int side = MathUtils.random(1,4);
        projectile.height = 32;
        projectile.width = 32;
        switch(side){
            case 1: //Bottom of the screen
                projectile.direction = Projectile.DIRECTION.UP;
                projectile.texture = projectileSpriteUp;
                projectile.x = MathUtils.random(0,screenWidth-projectile.width);
                projectile.y = 0;
                break;

            case 2: //Left side of the screen
                projectile.direction = Projectile.DIRECTION.RIGHT;
                projectile.texture = projectileSpriteRight;
                projectile.x = 0;
                projectile.y = MathUtils.random(0,screenHeight-projectile.height);
                break;

            case 3: //Right side of the screen
                projectile.direction = Projectile.DIRECTION.LEFT;
                projectile.texture = projectileSpriteLeft;
                projectile.x = screenWidth;
                projectile.y = MathUtils.random(0,screenHeight-projectile.height);
                break;

            case 4: //Top of the screen
                projectile.direction = Projectile.DIRECTION.DOWN;
                projectile.texture = projectileSpriteDown;
                projectile.x = MathUtils.random(0,screenWidth-projectile.width);
                projectile.y = screenHeight;
                break;
        }

        projectiles.add(projectile);
        lastLaunchTime = TimeUtils.nanoTime();
    }

    private void spawnPowerUp(){
        PowerUp powerUp = new PowerUp();
        int type = MathUtils.random(1,3);
        powerUp.height = 16;
        powerUp.width = 16;
        switch (type){
            case 1:
                powerUp.type = PowerUp.POWER_UP.SPEEDBOOST;
                powerUp.texture = speedBoostSprite;
                break;

            case 2:
                powerUp.type = PowerUp.POWER_UP.SHIELD;
                powerUp.texture = shieldSprite;
                break;

            case 3:
                powerUp.type = PowerUp.POWER_UP.ONEUP;
                powerUp.texture = oneUpSprite;
                break;
        }
        powerUp.x = MathUtils.random(0,screenWidth-powerUp.width);
        powerUp.y = MathUtils.random(0,screenHeight-powerUp.height);
        powerUps.add(powerUp);
        lastSpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta){
        if(!paused){
            if(timer <= 0){
                game.setScreen(new GameWinScreen(game));
                dispose();
            }else {
                Gdx.gl.glClearColor(1, 0, 1, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                camera.update();
                game.batch.setProjectionMatrix(camera.combined);
                game.batch.begin();
                game.font.draw(game.batch, "Health: " + health, 0, screenHeight); //Draw the health counter or meter. Meter would probably require various textures and a switch statement
                game.font.draw(game.batch, "Time Remaining: " + timer + " seconds", 0, screenHeight - 20); //Draw the timer
                if (shieldActive) {
                    game.batch.draw(shieldedPlayerSprite, player.x, player.y);
                } else {
                    game.batch.draw(playerSprite, player.x, player.y);
                }

                for (Projectile projectile : projectiles) {
                    game.batch.draw(projectile.texture, projectile.x, projectile.y);
                }
                for (PowerUp powerUp : powerUps) {
                    game.batch.draw(powerUp.texture, powerUp.x, powerUp.y);
                }
                game.batch.end();

                updateMotion();

                if (player.x < 0) {
                    player.x = 0;
                }

                if (player.x > screenWidth - player.width) {
                    player.x = screenWidth - player.width;
                }

                if (player.y < 0) {
                    player.y = 0;
                }

                if (player.y > screenHeight - player.height) {
                    player.y = screenHeight - player.height;
                }

                if (TimeUtils.nanoTime() - lastLaunchTime > launchFrequency) {
                    spawnProjectile();
                }

                if (TimeUtils.nanoTime() - lastSpawnTime > powerUpFrequency) {
                    spawnPowerUp();
                }

                if (powerUps.size > 5) {
                    powerUps.removeIndex(0);
                }

                moveProjectiles();
                checkForPowerUpCollision();

                if (shieldActive) {
                    if (TimeUtils.nanoTime() - shieldActivatedTime > 10000000000L) {
                        shieldActive = false;
                    }
                }

                if (playerSpeed != playerBaseSpeed) {
                    if (TimeUtils.nanoTime() - speedBoostActivatedTime > 10000000000L) {
                        playerSpeed = playerBaseSpeed;
                    }
                }

                if (TimeUtils.nanoTime() - lastTime > 1000000000) {
                    timer--;
                    lastTime = TimeUtils.nanoTime();
                }
        }
        }
    }

    private void updateMotion(){
        if(leftMove){
            player.x -= playerSpeed * Gdx.graphics.getDeltaTime();
        }
        if (rightMove){
            player.x += playerSpeed * Gdx.graphics.getDeltaTime();
        }
        if (upMove){
            player.y += playerSpeed * Gdx.graphics.getDeltaTime();
        }
        if (downMove){
            player.y -= playerSpeed * Gdx.graphics.getDeltaTime();
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

    private void moveProjectiles(){
        Iterator<Projectile> iter = projectiles.iterator();
        while (iter.hasNext()) {
            Projectile projectile = iter.next();
            switch (projectile.direction) {
                case UP:
                    projectile.y += speed * Gdx.graphics.getDeltaTime();
                    if (projectile.y + projectile.height > screenHeight) {
                        iter.remove();
                    }
                    break;

                case DOWN:
                    projectile.y -= speed * Gdx.graphics.getDeltaTime();
                    if (projectile.y + projectile.height < 0) {
                        iter.remove();
                    }
                    break;

                case LEFT:
                    projectile.x -= speed * Gdx.graphics.getDeltaTime();
                    if (projectile.x + projectile.width < 0) {
                        iter.remove();
                    }
                    break;

                case RIGHT:
                    projectile.x += speed * Gdx.graphics.getDeltaTime();
                    if (projectile.x + projectile.width> screenWidth) {
                        iter.remove();
                    }
                    break;
            }

            if (projectile.overlaps(player)) {
                iter.remove();
                if(!shieldActive){
                    hit.play();
                    if (--health <= 0) {
                        game.setScreen(new GameOverScreen(game));
                        dispose();
                    }
                }else {
                    shieldHit.play();
                    shieldActive = false;
                }
            }
        }
    }

    private void checkForPowerUpCollision(){
        for (PowerUp powerUp : powerUps) {
            if (powerUp.overlaps(player)) {
                switch (powerUp.type) {
                    case SPEEDBOOST:
                        playerSpeed = playerBaseSpeed * 3 / 2;
                        speedBoostActivatedTime = TimeUtils.nanoTime();
                        break;

                    case SHIELD:
                        shieldActive = true;
                        shieldActivatedTime = TimeUtils.nanoTime();
                        break;

                    case ONEUP:
                        health++;
                        break;
                }

                powerUps.removeValue(powerUp, true);
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
        paused = true;
    }

    public boolean isPaused() {
        music.pause();
        return paused;
    }

    @Override
    public void resume() {
        music.play();
        paused = false;
    }
}
