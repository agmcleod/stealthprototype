package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aaronmcleod on 15-07-22.
 */
public class Alarm extends GameObject {
    private float alpha;
    private Rectangle bounds;
    private GameScreen gs;
    private float increment;
    private TextureRegion textureRegion;

    public Alarm(GameScreen gs) {
        super("alarm");
        this.gs = gs;
        bounds = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        textureRegion = gs.getGame().getAtlas().findRegion("alarm");
        alpha = 1.0f;
        increment = -4f;
    }

    @Override
    public void dispose(World world) {

    }

    @Override
    public void render(SpriteBatch batch) {
        Camera camera = gs.getCamera();
        float xOffset = camera.position.x - camera.viewportWidth / 2;
        float yOffset = camera.position.y - camera.viewportHeight / 2;
        float x = xOffset + bounds.x;
        float y = yOffset + bounds.y;
        batch.setColor(1, 1, 1, alpha);
        batch.draw(textureRegion, x, y);
        batch.setColor(1, 1, 1, 1.0f);
    }

    public void update() {
        alpha += increment * Gdx.graphics.getDeltaTime();
        if (alpha < 0 || alpha > 1.0) {
            increment *= 1;
        }
    }
}
