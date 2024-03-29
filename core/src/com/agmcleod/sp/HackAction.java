package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aaronmcleod on 15-07-05.
 */
public class HackAction extends GameObject {
    private final float TIME_TO_HACK = 3.0f;
    private final float WIDTH = 60;
    private GameScreen gs;
    private HackableComponent hackableComponent;
    private Rectangle bounds;
    private float timeout;
    private String type;

    public HackAction(GameScreen gs, HackableComponent hackableComponent) {
        super("hack");
        this.hackableComponent = hackableComponent;
        timeout = TIME_TO_HACK;
        this.gs = gs;
        bounds = new Rectangle(hackableComponent.getBounds());
    }

    @Override
    public void dispose(World world) {}

    @Override
    public void render(SpriteBatch batch) {

    }

    public void renderShape(ShapeRenderer renderer) {
        renderer.setColor(Color.GREEN);
        renderer.rect(bounds.x - (WIDTH / 2), bounds.y + bounds.height / 2, WIDTH * ((TIME_TO_HACK - timeout) / TIME_TO_HACK), 4);
    }

    public boolean requiresACrack() {
        return type.equals("crack");
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void update() {
        if (type.equals("hack")) {
            timeout -= Gdx.graphics.getDeltaTime();
            if (timeout <= 0) {
                hackableComponent.removeFromGame();
            }
        }
    }
}
