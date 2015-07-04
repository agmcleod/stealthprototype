package com.agmcleod.sp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class ToggleTrigger extends GameObject {

    private GameScreen gs;
    private Body body;
    private boolean enabled;
    private ToggleTriggerCallback startCallback;
    private ToggleTriggerCallback endCallback;

    public ToggleTrigger(GameScreen gs) {
        super("toggletrigger");
        this.gs = gs;
        enabled = false;
    }

    @Override
    public void dispose(World world) {
        world.destroyBody(body);
    }

    public void exec() {
        if (enabled) {
            enabled = false;
            endCallback.exec();
        }
        else {
            enabled = true;
            startCallback.exec();
        }
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    public void setEndCallback(ToggleTriggerCallback cb) {
        endCallback = cb;
    }

    public void setStartCallback(ToggleTriggerCallback cb) {
        startCallback = cb;
    }

    @Override
    public void update() {

    }
}
