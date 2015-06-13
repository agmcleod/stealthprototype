package com.agmcleod.sp;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;

public class Game extends com.badlogic.gdx.Game {
    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 100f;

    private World world;

    private TextureAtlas atlas;

    @Override
    public void create () {
        atlas = new TextureAtlas("atlas.txt");
        setScreen(new GameScreen(this));
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
