package com.github.va7icana55asin.test_game.utility;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class UtilityBase extends ModelInstance {

    public Vector3 dimensions;

    public UtilityBase(Model model) {
        super(model);
        this.dimensions = this.calculateBoundingBox(new BoundingBox()).getDimensions(new Vector3());
    }

    public UtilityBase(Model model, Vector3 position) {
        super(model, position);
        this.dimensions = this.calculateBoundingBox(new BoundingBox()).getDimensions(new Vector3());
    }

    public UtilityBase(Model model, float x, float y, float z) {
        super(model, x, y, z);
        this.dimensions = this.calculateBoundingBox(new BoundingBox()).getDimensions(new Vector3());
    }

    // Based on the bounding box collision method but using model transforms and dimensions
    public boolean collidedWith(UtilityBase other) {
        Vector3 thisCenter = this.transform.getTranslation(new Vector3());
        Vector3 otherCenter = other.transform.getTranslation(new Vector3());

        float lx = Math.abs(thisCenter.x - otherCenter.x);
        float sumX = (this.dimensions.x / 2.0f) + (other.dimensions.x / 2.0f);

        float ly = Math.abs(thisCenter.y - otherCenter.y);
        float sumY = (this.dimensions.y / 2.0f) + (other.dimensions.y / 2.0f);

        float lz = Math.abs(thisCenter.z - otherCenter.z);
        float sumZ = (this.dimensions.z / 2.0f) + (other.dimensions.z / 2.0f);

        return (lx <= sumX && ly <= sumY && lz <= sumZ);
    }
}
