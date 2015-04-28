package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by aaronmcleod on 15-04-27.
 */
public class GameScreen implements Screen {
    private World world;
    private OrthographicCamera camera;
    private Matrix4 cameraCpy;
    private Box2DDebugRenderer debugRenderer;
    private Game game;

    public GameScreen(Game game, World world) {
        this.world = world;
        this.game = game;
    }

    @Override
    public void show() {
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraCpy = camera.combined.cpy();

        LevelBuilder builder = new LevelBuilder(this.game, this.world);
        builder.build();
    }

    @Override
    public void render(float delta) {
        update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1f);

        debugRenderer.render(world, cameraCpy.scl(game.BOX_TO_WORLD));
    }

    public void update() {
        cameraCpy.set(camera.combined);

        this.world.step(1f / 60f, 6, 2);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
    }
}
