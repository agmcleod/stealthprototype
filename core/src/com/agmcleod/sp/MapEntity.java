package com.agmcleod.sp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aaronmcleod on 15-05-31.
 */
public class MapEntity extends GameObject {
    protected Rectangle bounds;
    public MapEntity(String name) {
        super(name);
        bounds = new Rectangle();
    }

    public void dispose(World world) {
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    public void setBounds(float x, float y, float width, float height) {
        bounds.set(x, y, width, height);
    }

    @Override
    public void update() {

    }
}
