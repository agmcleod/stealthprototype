package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class HackableComponent extends GameObject {

    private GameScreen gs;
    private Body body;
    private boolean enabled;
    private HackAction hackAction;
    private boolean hacking;
    private String message;
    private Vector2 position;

    public HackableComponent(GameScreen gs, float x, float y) {
        super("hackablecomponent");
        this.gs = gs;
        enabled = false;
        position = new Vector2();
        hackAction = new HackAction(gs, this);
        hacking = false;
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

    public HackAction getHackAction() {
        return hackAction;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (enabled) {
            if (!hacking) {
                gs.getUiFont().draw(batch, message, position.x, position.y);
            }
        }
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public void setType(String type) {
        setMessage("Press [E] to hack");
    }

    @Override
    public void update() {
        if (enabled && Gdx.input.isKeyJustPressed(Input.Keys.E) && !hacking) {
            hacking = true;
        }

        if (hacking) {
            hackAction.update();
        }
    }
}
