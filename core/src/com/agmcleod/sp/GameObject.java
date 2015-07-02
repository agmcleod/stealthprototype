package com.agmcleod.sp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aaronmcleod on 15-04-27.
 */
public abstract class GameObject {
    public String name;
    public GameObject (String name) {
        this.name = name;
    }

    public abstract void dispose(World world);
    public abstract void render(SpriteBatch batch);
    public abstract void update();
}
