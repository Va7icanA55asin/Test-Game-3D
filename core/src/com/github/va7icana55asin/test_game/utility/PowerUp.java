package com.github.va7icana55asin.test_game.utility;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class PowerUp extends Rectangle {

    public enum POWER_UP {
        SPEEDBOOST,SHIELD,ONEUP,NONE
    }

    public POWER_UP type;
    public Texture texture;

    public PowerUp() {
        super();
        type = POWER_UP.NONE;
    }
}
