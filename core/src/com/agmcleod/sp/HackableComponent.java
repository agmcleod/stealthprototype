package com.agmcleod.sp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class HackableComponent extends GameObject {

    private GameScreen gs;
    private Body body;
    private boolean enabled;
    private HackAction hackAction;
    private String message;
    private Vector2 position;

    public HackableComponent(GameScreen gs) {
        super("hackablecomponent");
        this.gs = gs;
        enabled = false;
        position = new Vector2();
        hackAction = new HackAction(gs, this);
    }

    public void disable() {
        enabled = false;
    }

    @Override
    public void dispose(World world) {
        world.destroyBody(body);
    }

    public void enable() {
        enabled = true;
    }

    public HackAction getHack() {
        return hackAction;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (enabled) {
            gs.getUiFont().draw(batch, message, position.x, position.y);
        }
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setType(String type) {
        setMessage("Press [E] to hack");
    }

    @Override
    public void update() {

    }
}
