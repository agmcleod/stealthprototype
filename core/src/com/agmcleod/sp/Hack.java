package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aaronmcleod on 15-07-05.
 */
public class Hack extends GameObject {
    private GameScreen gs;
    private int id;
    private float timeout;
    private UITrigger trigger;
    public Hack(GameScreen gs, UITrigger trigger) {
        super("hack");
        this.id = trigger.getInteractionId();
        this.trigger = trigger;
        timeout = 3.0f; // temp for now
        this.gs = gs;
    }

    @Override
    public void dispose(World world) {}

    public int getId() {
        return id;
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public void update() {
        timeout -= Gdx.graphics.getDeltaTime();
        if (timeout <= 0) {
            gs.removeObject(trigger);
            gs.removeObject(this);
        }
    }
}
