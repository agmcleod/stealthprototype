package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class HackableComponent extends GameObject {

    private GameScreen gs;
    private Body body;
    private boolean enabled;
    private HackAction hackAction;
    private boolean hacking;
    private String message;
    private Texture texture;
    private Rectangle bounds;

    public HackableComponent(GameScreen gs, float x, float y, float width, float height, String imageName) {
        super("hackablecomponent");
        this.gs = gs;
        enabled = false;
        bounds = new Rectangle(x, y, width, height);
        hackAction = new HackAction(gs, this);
        hacking = false;
        if (imageName != null) {
            texture = new Texture(imageName + ".png");
            texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        }
        message = "Press [E] to hack";
    }

    public void disable() {
        enabled = false;
    }

    @Override
    public void dispose(World world) {
        world.destroyBody(body);
        hackAction.dispose(world);
        if (texture != null) {
            texture.dispose();
        }
    }

    public void enable() {
        enabled = true;
    }

    public HackAction getHackAction() {
        return hackAction;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isHacking() {
        return hacking;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
        if (enabled && !hacking) {
            gs.getUiFont().draw(batch, message, bounds.x - 150, bounds.y + bounds.height);
        }
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setType(String type) {
        hackAction.setType(type);
    }

    @Override
    public void update() {
        if (enabled && Gdx.input.isKeyJustPressed(Input.Keys.E) && !hacking) {
            hacking = true;
            gs.allowPlayerMovement(false);
            if (hackAction.requiresACrack()) {
                gs.setPlayerShowCrackTool(true);
            }
        }

        if (hacking) {
            hackAction.update();
        }
    }
}
