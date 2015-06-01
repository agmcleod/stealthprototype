package com.agmcleod.sp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by aaronmcleod on 15-05-31.
 */
public class Enemy extends MapEntity {
    private Game game;
    private Vector2 original;
    TextureRegion region;
    private Vector2 target;
    public Enemy(Game game) {
        super("enemy");
        this.game = game;
        region = game.getAtlas().findRegion("enemy");
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(region, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        original = new Vector2(x, y);
    }

    public void setTarget(float x, float y) {
        target = new Vector2(x, y);
    }

    @Override
    public void update() {

    }
}
