package com.github.va7icana55asin.test_game.utility;

import com.badlogic.gdx.graphics.g3d.Model;

public class PowerUp extends UtilityBase {

    public enum POWER_UP {
        SPEED_BOOST, SHIELD, ONE_UP
    }

    public POWER_UP type;

    public PowerUp(Model model, float x, float y, float z, POWER_UP type) {
        super(model, x, y, z);
        this.type = type;
    }
}
