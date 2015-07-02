package com.agmcleod.sp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aaronmcleod on 15-05-31.
 */
public class MapCollision extends GameObject {
    public MapCollision() {
        super("mapCollision");
    }

    public MapCollision(String name) {
        super(name);
    }

    public void dispose(World world) {
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public void update() {

    }
}
