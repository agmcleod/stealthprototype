package com.agmcleod.sp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aaronmcleod on 15-07-01.
 */
public class Trigger extends GameObject {
    public enum TriggerType {
        DISABLE_HIDDEN, ENABLE_HIDDEN
    }

    private Body body;
    private GameScreen gs;
    private TriggerType type;

    public Trigger(GameScreen gs) {
        super("trigger");
        this.gs = gs;
    }

    public void dispose(World world) {
        world.destroyBody(body);
    }

    public void exec() {
        if (type == TriggerType.ENABLE_HIDDEN) {
            gs.showHidden(true);
        }
        else if (type == TriggerType.DISABLE_HIDDEN) {
            gs.showHidden(false);
        }
    }

    public void render(SpriteBatch batch) {

    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setTypeByString(String typeString) {
        if (typeString.equals("disablehidden")) {
            type = TriggerType.DISABLE_HIDDEN;
        }
        else if (typeString.equals("enablehidden")) {
            type = TriggerType.ENABLE_HIDDEN;
        }
    }

    public void update() {

    }
}
