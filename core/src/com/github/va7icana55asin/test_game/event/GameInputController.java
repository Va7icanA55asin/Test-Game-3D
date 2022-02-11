package com.github.va7icana55asin.test_game.event;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.github.va7icana55asin.test_game.screen.GameScreen;
import com.github.va7icana55asin.test_game.utility.Player;

public class GameInputController extends InputAdapter {

    private final GameScreen screen;
    private final Camera camera;
    public final Player player;
    private final IntIntMap movementKeys = new IntIntMap();
    // Movement, no up and down
    private final int STRAFE_LEFT = Keys.A;
    private final int STRAFE_RIGHT = Keys.D;
    private final int FORWARD = Keys.W;
    private final int BACKWARD = Keys.S;
    private final int L_SHIFT = Keys.SHIFT_LEFT;
    private final int R_SHIFT = Keys.SHIFT_RIGHT;
    // Couple options for pausing
    private final IntIntMap pauseKeys = new IntIntMap();
    private final int PAUSE_P = Keys.P;
    private final int PAUSE_ESC = Keys.ESCAPE;
    // Speeds
    private float movementMultiplier = 1;
    private float walkingVelocity = 1.25f;
    private float runningVelocity = 5;
    private float degreesPerPixel = 0.5f;
    private final Vector3 tmp = new Vector3();
    private final float xLimit;
    private final float zLimit;

    public GameInputController(GameScreen screen, Camera camera, Player player, float xLimit, float zLimit) {
        this.screen = screen;
        this.camera = camera;
        this.player = player;
        this.xLimit = xLimit;
        this.zLimit = zLimit;
        // Adjust height since the model will be centered and the rotation so the model is in the same direction as the camera
        this.player.transform
                .setToTranslation(camera.position.x, camera.position.y / 2, camera.position.z)
                .rotate(Vector3.Y, (float) Math.acos(Vector3.X.dot(camera.direction.nor())) * MathUtils.radiansToDegrees);
        pauseKeys.put(PAUSE_P, PAUSE_P);
        pauseKeys.put(PAUSE_ESC, PAUSE_ESC);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (pauseKeys.containsKey(keycode)) {
            screen.togglePause();
        } else {
            movementKeys.put(keycode, keycode);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        movementKeys.remove(keycode, 0);
        return true;
    }

    public void setMovementMultiplier(float movementMultiplier) {
        this.movementMultiplier = movementMultiplier;
    }

    public void setVelocities(float walkingVelocity, float runningVelocity) {
        this.walkingVelocity = walkingVelocity;
        this.runningVelocity = runningVelocity;
    }

    public void setDegreesPerPixel(float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
        float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
        camera.direction.rotate(camera.up, deltaX);
        player.rotate(deltaX);
        tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, deltaY);
        return true;
    }

    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float deltaTime) {
        float speed;

        if (movementKeys.containsKey(L_SHIFT) || movementKeys.containsKey(R_SHIFT)) {
            speed = runningVelocity;
        } else {
            speed = walkingVelocity;
        }

        boolean forward = movementKeys.containsKey(FORWARD);
        boolean backward = movementKeys.containsKey(BACKWARD);
        boolean left = movementKeys.containsKey(STRAFE_LEFT);
        boolean right = movementKeys.containsKey(STRAFE_RIGHT);

        // Adjust the speed if moving in two directions
        if ((forward ^ backward) && (left ^ right)) {
            speed /= Math.sqrt(2);
        }

        float distance = deltaTime * speed * movementMultiplier;

        // Forward and back will cancel each other out
        if (forward ^ backward) {
            float direction = forward ? -1 : 1;
            tmp.set(camera.direction).crs(camera.up).crs(Vector3.Y).nor().scl(direction * distance);
            Vector3 movement = checkLimits();
            camera.translate(movement);
            player.move(movement);
        }

        // Left and right will cancel each other out
        if (left ^ right) {
            float direction = left ? -1 : 1;
            tmp.set(camera.direction).crs(camera.up).nor().scl(direction * distance);
            Vector3 movement = checkLimits();
            camera.translate(movement);
            player.move(movement);
        }

        camera.update(true);
        player.calculateAllTransformations();
    }

    private Vector3 checkLimits() {
        Vector3 moveVector = tmp.cpy();
        Vector3 currentVector = this.camera.position.cpy();
        if (moveVector.x + currentVector.x < 0 || moveVector.x + currentVector.x > xLimit) {
            moveVector.x = 0;
        }
        if (moveVector.z + currentVector.z < 0 || moveVector.z + currentVector.z > zLimit) {
            moveVector.z = 0;
        }
        return moveVector;
    }
}
