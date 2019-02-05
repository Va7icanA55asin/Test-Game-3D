package com.github.va7icana55asin.test_game.utility;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Projectile extends Rectangle {

    public enum DIRECTION{
        UP,DOWN,LEFT,RIGHT,NONE
    }

    public DIRECTION direction;
    public Texture texture;

    //Constructs rectangle with all values 0 and a none direction
    public Projectile(){
        super();
        this.direction = DIRECTION.NONE;
    }

    public Projectile(DIRECTION direction, Texture texture, float x, float y, float width, float height){
        super(x,y,width,height);
        this.direction = direction;
        this.texture = texture;
    }
}
