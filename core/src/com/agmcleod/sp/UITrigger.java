package com.agmcleod.sp;

import com.agmcleod.sp.hooks.StartHackHook;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class UITrigger extends GameObject {

    private GameScreen gs;
    private Body body;
    private boolean enabled;
    private Array<Hook> hooks;
    private int interactionId;
    private String message;
    private Vector2 position;

    public UITrigger(GameScreen gs, int interactionId) {
        super("uitrigger");
        this.gs = gs;
        enabled = false;
        position = new Vector2();
        hooks = new Array<Hook>();
        this.interactionId = interactionId;
    }

    public void addHook(Hook hook) {
        hooks.add(hook);
    }

    @Override
    public void dispose(World world) {
        world.destroyBody(body);
    }

    public void exec() {
        if (!enabled) {
            for (Hook hook : hooks) {
                hook.exec();
            }
        }
        else {
            for (Hook hook : hooks) {
                hook.undo();
            }
        }
        enabled = !enabled;
    }

    public final int getInteractionId() {
        return interactionId;
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

    public void setType(String type) {

        if (type.equals("hack")) {
            setMessage("Press [E] to hack");
            addHook(new StartHackHook(gs, interactionId));
        }
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    @Override
    public void update() {

    }
}
