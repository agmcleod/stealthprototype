package com.agmcleod.sp;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Game extends com.badlogic.gdx.Game {
    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 100f;
    public static final short ENEMY_MASK = 0x0004;
    public static final short PLAYER_MASK = 0x0002;
    public static final short TRIGGER_MASK = 0x0008;
    public static final short WORLD_MASK = 0x0001;

    private GameScreen gameScreen;
    private World world;

    private TextureAtlas atlas;

    @Override
    public void create () {
        atlas = new TextureAtlas("atlas.txt");
        world = new World(new Vector2(0, 0), true);
        gameScreen = new GameScreen(this, world);
        setScreen(gameScreen);
    }

    public void drawBlackTransparentSquare(Camera camera, ShapeRenderer shapeRenderer, float percent, TransitionCallback callback) {
        Gdx.gl.glEnable(GL20.GL_BLEND);

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(1, 1, 1, percent));
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        shapeRenderer.rect(camera.position.x - w / 2, camera.position.y - h / 2, w, h);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (percent > 1.0f || percent < 0f) {
            callback.callback();
        }
    }

    public void dispose() {
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public World getWorld() {
        return world;
    }
}
