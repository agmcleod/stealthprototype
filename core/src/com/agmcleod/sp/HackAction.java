package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aaronmcleod on 15-07-05.
 */
public class HackAction extends GameObject {
    private final float TIME_TO_HACK = 3.0f;
    private GameScreen gs;
    private HackableComponent hackableComponent;
    private Vector2 position;
    private float timeout;

    public HackAction(GameScreen gs, HackableComponent hackableComponent) {
        super("hack");
        this.hackableComponent = hackableComponent;
        timeout = TIME_TO_HACK;
        this.gs = gs;
        position = new Vector2(hackableComponent.getBounds().x, hackableComponent.getBounds().y);
    }

    @Override
    public void dispose(World world) {}

    @Override
    public void render(SpriteBatch batch) {

    }

    public void renderShape(ShapeRenderer renderer) {
        renderer.setColor(Color.GREEN);
        renderer.rect(position.x, position.y, 60 * ((TIME_TO_HACK - timeout) / TIME_TO_HACK), 4);
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
