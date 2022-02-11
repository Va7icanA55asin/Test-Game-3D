package com.github.va7icana55asin.test_game.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

public class Projectile extends UtilityBase {

    public enum DIRECTION {
        POSITIVE_Z, NEGATIVE_Z, POSITIVE_X, NEGATIVE_X
    }

    public DIRECTION direction;
    public float speed;

    public Projectile(Model model, float x, float y, float z, DIRECTION direction, float speed) {
        super(model, x, y, z);
        this.direction = direction;
        this.speed = speed;
        float degrees = 0;
        // Values are based on the model orientation
        switch (direction) {
            case POSITIVE_Z:
                degrees = 180;
                break;
            case NEGATIVE_Z:
                degrees = 0;
                break;
            case POSITIVE_X:
                degrees = -90;
                break;
            case NEGATIVE_X:
                degrees = 90;
                break;
        }
        this.transform.rotate(Vector3.Y, degrees);
        this.calculateTransforms();
    }

    public void move() {
        // Due to model orientation, backwards is forwards for all models
        this.transform.translate(0, 0, -(this.speed * Gdx.graphics.getDeltaTime()));
        this.calculateTransforms();
    }
}
