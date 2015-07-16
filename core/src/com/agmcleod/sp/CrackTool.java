package com.agmcleod.sp;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Aaron on 7/15/2015.
 */
public class CrackTool {
    private GameScreen gs;
    private Vector2 position;
    private Texture texture;
    public CrackTool(GameScreen gs) {
        this.gs = gs;
        texture = new Texture("cracktool.png");
        position = new Vector2(0, 0);
    }

    public void dispose() {
        texture.dispose();
    }

    public void render(SpriteBatch batch) {
        Camera camera = gs.getCamera();
        float x = camera.position.x + position.x - camera.viewportWidth / 2;
        float y = camera.position.y + position.y - camera.viewportHeight / 2;
        batch.draw(texture, x, y);
    }
}
