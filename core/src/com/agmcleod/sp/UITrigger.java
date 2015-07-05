package com.agmcleod.sp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class UITrigger extends GameObject {

    private GameScreen gs;
    private Body body;
    private boolean enabled;
    private String message;
    private Vector2 position;

    public UITrigger(GameScreen gs) {
        super("uitrigger");
        this.gs = gs;
        enabled = false;
        position = new Vector2();
    }

    @Override
    public void dispose(World world) {
        world.destroyBody(body);
    }

    public void exec() {
        enabled = !enabled;
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

    public void setMessage(String type) {
        if (type.equals("hack")) {
            this.message = "Press [E] to hack";
        }
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    @Override
    public void update() {

    }
}
