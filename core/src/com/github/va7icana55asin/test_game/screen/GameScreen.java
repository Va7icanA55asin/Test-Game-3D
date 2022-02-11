package com.github.va7icana55asin.test_game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.va7icana55asin.test_game.event.GameInputController;
import com.github.va7icana55asin.test_game.utility.DIFFICULTY;
import com.github.va7icana55asin.test_game.utility.Player;
import com.github.va7icana55asin.test_game.utility.PowerUp;
import com.github.va7icana55asin.test_game.utility.Projectile;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final Main game;

    // Renderer settings
    private final PerspectiveCamera camera;
    private final GameInputController cameraController;
    private final Environment environment;

    // Room settings
    private final float roomX = 50; // X dimension of the room; set in the room model; 50m(units)
    private final float roomZ = 50; // Z dimension of the room; set in the room model; 50m(units)
    private final float roomHeight = 5; // And 5m(units) tall

    // Game settings
    private boolean paused;
    private boolean loading;
    private long lastTime;

    // Asset settings
    private ModelBatch modelBatch;
    private AssetManager assets;
    private ModelInstance room;
    private final String roomFile = "Room.g3db";
    private final String projectileFile = "Projectile.g3db";
    private final String projectileAnimation = "Bullet|Rotate"; // Defined in the model
    private final String speedBoostFile = "Speed Boost.g3db";
    private final String speedBoostAnimation = "Arrows|Bounce"; // Defined in the model
    private final String heartFile = "Heart.g3db";
    private final String heartAnimation = "Heart|Bounce"; // Defined in the model
    private final String shieldFile = "Shield.g3db";
    private final String shieldAnimation = "Shield|Bounce"; // Defined in the model
    private final String shieldedFile = "Shielded.g3db";
    private final Array<AnimationController> animationControllers = new Array<AnimationController>();

    // Sound settings
    private final String musicFile = "basic-beat.wav";
    private final String hitFile = "hit.wav";
    private final String shieldHitFile = "shield-hit.wav";
    private final Music music;
    private final Sound hit;
    private final Sound shieldHit;

    // Health and timer counter settings
    private Stage stage;
    private Label label;
    private StringBuilder stringBuilder;

    // Player settings
    private final Player player;
    private final float playerWidth = 0.5f; // Define player shoulder width to be 0.5m(units); based on avg shoulder width
    private final float playerDepth = 0.25f; // Define player front to back depth to be 0.25m(units); based on avg front to back depth
    private final float playerHeight = 1.8f; // Define player height to be 1.8m(units); based loosely on avg height
    private final float playerWalkingSpeed = 1.25f; // Set player base walking speed to 1.25m(units)/s; based on avg human walking speed
    private final float playerRunningSpeed = 5; // Set player base running speed to 5m(units)/s; based on avg human running speed
    private float playerSpeedMultiplier = 1; // Multiplier for the players speed
    private int maxHealth;
    private int health;
    private int timer;

    // Projectile settings
    private final Array<Projectile> projectiles;
    private final float projectileRadius = 0.25f; // Defined in the model; 0.25m(units); diameter is 0.5m
    private long lastLaunchTime;
    private float projectileSpeed;
    private int launchFrequency;

    // Power up settings
    private final Array<PowerUp> powerUps;
    private final float powerUpSpawnHeight = 0.75f; // Arbitrary spawn height that looked good
    private final float powerUpOffset = 0.5f; // Based on the models; mainly a spawn offset to prevent clipping
    private final long speedBoostTimeLimit = 10000000000L; // Arbitrary time limit for how long speed boost can last; currently 10s
    private final float speedBoostMultiplier = 1.5f; // The multiplier for how speed boost affects player speed
    private long speedBoostActivatedTime;
    private boolean shieldActive;
    private final long shieldTimeLimit = 10000000000L; // Arbitrary time limit for how long shield can last; currently 10s
    private long shieldActivatedTime;
    private long lastSpawnTime;
    private long powerUpFrequency;

    public GameScreen(final Main game, DIFFICULTY difficulty) {
        this.game = game;
        // Set up environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Set up the camera and player
        player = new Player(playerDepth, playerHeight, playerWidth);
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(roomX / 2, playerHeight, roomZ / 2);
        camera.lookAt(1, playerHeight, 1);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();
        cameraController = new GameInputController(this, camera, player, roomX, roomZ);
        cameraController.setVelocities(playerWalkingSpeed, playerRunningSpeed);
        cameraController.setMovementMultiplier(playerSpeedMultiplier);
        Gdx.input.setInputProcessor(cameraController);

        // Set up asset loading
        modelBatch = new ModelBatch();
        assets = new AssetManager();
        assets.load(roomFile, Model.class);
        assets.load(projectileFile, Model.class);
        assets.load(speedBoostFile, Model.class);
        assets.load(heartFile, Model.class);
        assets.load(shieldFile, Model.class);
        assets.load(shieldedFile, Model.class);

        // Set up sounds and music
        hit = Gdx.audio.newSound(Gdx.files.internal(hitFile));
        shieldHit = Gdx.audio.newSound(Gdx.files.internal(shieldHitFile));
        music = Gdx.audio.newMusic(Gdx.files.internal(musicFile));
        music.setVolume(0.5f);
        music.setLooping(true);

        // Set up life count and timer printing
        stage = new Stage();
        label = new Label(" ", new Label.LabelStyle(new BitmapFont(), Color.RED));
        label.setY(stage.getHeight() - 25); // Completely arbitrary value that makes it fit
        stage.addActor(label);
        stringBuilder = new StringBuilder();

        switch (difficulty) {
            case BABY:
                timer = 90;
                maxHealth = 7;
                health = 5;
                projectileSpeed = 5;
                launchFrequency = 1000000000;
                powerUpFrequency = 5000000000L;
                break;

            case EASY:
                timer = 120;
                maxHealth = 6;
                health = 4;
                projectileSpeed = 5.625f;
                launchFrequency = 750000000;
                powerUpFrequency = 6000000000L;
                break;

            case MEDIUM:
                timer = 150;
                maxHealth = 5;
                health = 3;
                projectileSpeed = 6.25f;
                launchFrequency = 500000000;
                powerUpFrequency = 7000000000L;
                break;

            case HARD:
                timer = 180;
                maxHealth = 4;
                health = 2;
                projectileSpeed = 6.875f;
                launchFrequency = 250000000;
                powerUpFrequency = 8000000000L;
                break;

            case EXTREME:
                timer = 210;
                maxHealth = 3;
                health = 1;
                projectileSpeed = 7.5f;
                launchFrequency = 10000000;
                powerUpFrequency = 9000000000L;
                break;
        }

        shieldActive = false;
        projectiles = new Array<Projectile>();
        powerUps = new Array<PowerUp>();
        pause();
        loading = true;
    }

    private void doneLoading() {
        room = new ModelInstance(assets.get(roomFile, Model.class));
        room.transform.setToTranslation(roomX / 2, roomHeight / 2, roomZ / 2); // Since the root should be the center of the model
        player.setShield(assets.get(shieldedFile, Model.class));
        lastTime = TimeUtils.nanoTime();
        lastSpawnTime = TimeUtils.nanoTime();
        resume();
        loading = false;
        spawnProjectile();
    }

    private void spawnProjectile() {
        Projectile projectile = null;
        Model projectileModel = assets.get(projectileFile, Model.class);
        int side = MathUtils.random(1, 4);
        float projectileDiameter = 2 * projectileRadius;
        // Spawn from diameter (to prevent projectile clipping through floor) to player height minus radius (so there aren't useless projectiles)
        float spawnHeight = MathUtils.random(projectileDiameter, playerHeight - projectileRadius);
        // Spawn from diameter (to prevent clipping in the wall) to the room size minus diameter
        float spawnLocation;
        switch (side) {
            case 1:
                spawnLocation = MathUtils.random(projectileDiameter, roomX - projectileDiameter);
                projectile = new Projectile(projectileModel, spawnLocation, spawnHeight, 0, Projectile.DIRECTION.POSITIVE_Z, projectileSpeed);
                break;
            case 2:
                spawnLocation = MathUtils.random(projectileDiameter, roomX - projectileDiameter);
                projectile = new Projectile(projectileModel, spawnLocation, spawnHeight, roomZ, Projectile.DIRECTION.NEGATIVE_Z, projectileSpeed);
                break;
            case 3:
                spawnLocation = MathUtils.random(projectileDiameter, roomZ - projectileDiameter);
                projectile = new Projectile(projectileModel, 0, spawnHeight, spawnLocation, Projectile.DIRECTION.POSITIVE_X, projectileSpeed);
                break;
            case 4:
                spawnLocation = MathUtils.random(projectileDiameter, roomZ - projectileDiameter);
                projectile = new Projectile(projectileModel, roomX, spawnHeight, spawnLocation, Projectile.DIRECTION.NEGATIVE_X, projectileSpeed);
                break;
        }

        projectiles.add(projectile);
        AnimationController animationController = new AnimationController(projectile);
        animationController.setAnimation(projectileAnimation, -1);
        animationControllers.add(animationController);
        lastLaunchTime = TimeUtils.nanoTime();
    }

    private void spawnPowerUp() {
        PowerUp powerUp = null;
        String animation = null;
        float x = MathUtils.random(powerUpOffset, roomX - powerUpOffset);
        float y = powerUpSpawnHeight;
        float z = MathUtils.random(powerUpOffset, roomZ - powerUpOffset);
        int type = MathUtils.random(1, 3);
        switch (type) {
            case 1:
                powerUp = new PowerUp(assets.get(speedBoostFile, Model.class), x, y, z, PowerUp.POWER_UP.SPEED_BOOST);
                animation = speedBoostAnimation;
                break;
            case 2:
                powerUp = new PowerUp(assets.get(shieldFile, Model.class), x, y, z, PowerUp.POWER_UP.SHIELD);
                animation = shieldAnimation;
                break;
            case 3:
                powerUp = new PowerUp(assets.get(heartFile, Model.class), x, y, z, PowerUp.POWER_UP.ONE_UP);
                animation = heartAnimation;
                break;
        }

        powerUps.add(powerUp);
        AnimationController animationController = new AnimationController(powerUp);
        animationController.setAnimation(animation, -1);
        animationControllers.add(animationController);
        lastSpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        if (loading && assets.update()) {
            doneLoading();
        }
        if (!paused && !loading) {
            if (timer <= 0) {
                game.setScreen(new GameWinScreen(game));
                dispose();
            } else {
                cameraController.update();
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
                modelBatch.begin(camera);

                if (room != null) {
                    modelBatch.render(room);
                }

                boolean gameOver = moveProjectiles();
                if (gameOver) { // Probably not ideal to do this here, but I don't see a better option for the moment
                    modelBatch.end();
                    game.setScreen(new GameOverScreen(game));
                    dispose();
                    return;
                }
                checkForPowerUpCollision();

                for (AnimationController controller : animationControllers) {
                    controller.update(Gdx.graphics.getDeltaTime());
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

                if (shieldActive) {
                    if (TimeUtils.nanoTime() - shieldActivatedTime > shieldTimeLimit) {
                        shieldActive = false;
                    } else {
                        modelBatch.render(player.getShield());
                    }
                }

                if (playerSpeedMultiplier != 1) {
                    if (TimeUtils.nanoTime() - speedBoostActivatedTime > speedBoostTimeLimit) {
                        playerSpeedMultiplier = 1;
                        cameraController.setMovementMultiplier(playerSpeedMultiplier);
                    }
                }

                if (TimeUtils.nanoTime() - lastTime > 1000000000) { // Keeps timer counting down once per second
                    timer--;
                    lastTime = TimeUtils.nanoTime();
                }

                modelBatch.render(projectiles, environment);
                modelBatch.render(powerUps, environment);
                modelBatch.end();

                // Draw the health and timer counters
                stringBuilder.setLength(0);
                stringBuilder.append("Health: ").append(health)
                        .append("\n")
                        .append("Time Remaining: ").append(timer).append(" seconds");
                label.setText(stringBuilder);
                stage.draw();
            }
        }
    }

    // Will return true if game over was reached
    private boolean moveProjectiles() {
        Iterator<Projectile> iter = projectiles.iterator();
        while (iter.hasNext()) {
            Projectile projectile = iter.next();
            projectile.move();
            Vector3 vector = projectile.transform.getTranslation(new Vector3());
            switch (projectile.direction) {
                case POSITIVE_Z:
                    if (vector.z + projectileRadius > roomZ) {
                        iter.remove();
                    }
                    break;
                case NEGATIVE_Z:
                    if (vector.z + projectileRadius < 0) {
                        iter.remove();
                    }
                    break;
                case POSITIVE_X:
                    if (vector.x + projectileRadius > roomX) {
                        iter.remove();
                    }
                    break;
                case NEGATIVE_X:
                    if (vector.x + projectileRadius < 0) {
                        iter.remove();
                    }
                    break;
            }

            // Shield should be just a bit larger than the player
            if (shieldActive && projectile.collidedWith(player.getShield())) {
                iter.remove();
                shieldHit.play();
                shieldActive = false;
            } else if (!shieldActive && projectile.collidedWith(player)) {
                iter.remove();
                hit.play();
                if (--health <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkForPowerUpCollision(){
        for (PowerUp powerUp : powerUps) {
            if (player.collidedWith(powerUp)) {
                switch (powerUp.type) {
                    case SPEED_BOOST:
                        playerSpeedMultiplier = speedBoostMultiplier;
                        cameraController.setMovementMultiplier(playerSpeedMultiplier);
                        speedBoostActivatedTime = TimeUtils.nanoTime();
                        break;
                    case SHIELD:
                        shieldActive = true;
                        shieldActivatedTime = TimeUtils.nanoTime();
                        break;
                    case ONE_UP:
                        if (health != maxHealth) {
                            health++;
                        }
                        break;
                }
                powerUps.removeValue(powerUp, true);
            }
        }
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        stage.dispose();
        assets.dispose();
        projectiles.clear();
        powerUps.clear();
        hit.dispose();
        shieldHit.dispose();
        music.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = Gdx.graphics.getHeight();
        camera.viewportWidth = Gdx.graphics.getWidth();
        stage.getViewport().update(width, height, true);
    }

    public void togglePause() {
        if (paused) {
            resume();
        } else {
            pause();
        }
    }

    @Override
    public void pause() {
        Gdx.input.setCursorCatched(false);
        music.pause();
        paused = true;
    }

    @Override
    public void resume() {
        Gdx.input.setCursorCatched(true);
        music.play();
        paused = false;
    }
}
