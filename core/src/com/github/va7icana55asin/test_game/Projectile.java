package com.github.va7icana55asin.test_game;

import com.badlogic.gdx.math.Rectangle;

public class Projectile extends Rectangle {
    public enum DIRECTION{
        UP,DOWN,LEFT,RIGHT,NONE
    }

    public DIRECTION direction;

    //Constructs rectangle with all values 0 and a none direction
    public Projectile(){
        super();
        this.direction = DIRECTION.NONE;
    }

    public Projectile(DIRECTION direction, float x, float y, float width, float height){
        super(x,y,width,height);
        this.direction = direction;
    }
}
