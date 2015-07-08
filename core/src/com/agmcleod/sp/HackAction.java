package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aaronmcleod on 15-07-05.
 */
public class HackAction extends GameObject {
    private GameScreen gs;
    private float timeout;
    private HackableComponent hackableComponent;
    public HackAction(GameScreen gs, HackableComponent hackableComponent) {
        super("hack");
        this.hackableComponent = hackableComponent;
        timeout = 3.0f; // temp for now
        this.gs = gs;
    }

    @Override
    public void dispose(World world) {}

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public void update() {
        timeout -= Gdx.graphics.getDeltaTime();
        if (timeout <= 0) {
            gs.removeObject(hackableComponent);
            gs.removeObject(this);
        }
    }
}
