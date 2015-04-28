package com.agmcleod.sp;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game extends com.badlogic.gdx.Game {
    final float WORLD_TO_BOX = 0.01f;
    final float BOX_TO_WORLD = 100f;

    @Override
    public void create () {
        setScreen(new GameScreen());
    }

    public void dispose() {
    }
}
