package com.github.va7icana55asin.test_game.utility;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Player extends UtilityBase {

    private final float playerDepth;
    private final float playerHeight;
    private final float playerWidth;

    private UtilityBase shield;

    public Player(float playerDepth, float playerHeight, float playerWidth) {
        super(new ModelBuilder().createBox(playerDepth, playerHeight, playerWidth, new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        this.playerDepth = playerDepth;
        this.playerHeight = playerHeight;
        this.playerWidth = playerWidth;
    }

    public UtilityBase getShield() {
        return shield;
    }

    public void setShield(Model shieldModel) {
        this.shield = new UtilityBase(shieldModel, this.transform.getTranslation(new Vector3()));
    }

    public void rotate(float amount) {
        this.transform.rotate(Vector3.Y, amount);
        this.shield.transform.rotate(Vector3.Y, amount);
    }

    public void move(Vector3 vector) {
        this.transform.trn(vector);
        this.shield.transform.trn(vector);
    }

    public void calculateAllTransformations() {
        this.calculateTransforms();
        this.shield.calculateTransforms();
    }
}
