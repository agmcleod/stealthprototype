package com.agmcleod.sp;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Game extends com.badlogic.gdx.Game {
    final float WORLD_TO_BOX = 0.01f;
    final float BOX_TO_WORLD = 100f;

    private World world;

    private TextureAtlas atlas;

    @Override
    public void create () {
        atlas = new TextureAtlas("atlas.txt");
        world = new World(new Vector2(0, 0), true);
        setScreen(new GameScreen(this, world));
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
