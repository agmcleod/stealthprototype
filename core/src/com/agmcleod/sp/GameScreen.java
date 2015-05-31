package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Created by aaronmcleod on 15-04-27.
 */
public class GameScreen implements Screen {
    private World world;

    private MapBodyBuilder bodyBuilder;
    private OrthographicCamera camera;
    private Matrix4 cameraCpy;
    private Box2DDebugRenderer debugRenderer;
    private FollowCamera followCamera;
    private Game game;
    private Rectangle mapBounds;
    private Array<CustomMapRenderer> mapRenderers;
    private Player player;
    private SpriteBatch batch;

    public GameScreen(Game game, World world) {
        this.world = world;
        this.game = game;
        bodyBuilder = new MapBodyBuilder(game, world);
    }

    @Override
    public void show() {
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraCpy = camera.combined.cpy();
        mapRenderers = new Array<CustomMapRenderer>();

        TextureAtlas atlas = game.getAtlas();
        TextureRegion region = atlas.findRegion("player");
        player = new Player(game, region);
        batch = new SpriteBatch();

        mapBounds = new Rectangle();
        followCamera = new FollowCamera(camera, player.getPosition(), mapBounds);

        loadLevel("startroom.tmx", 0, 0);
        loadLevel("lhall.tmx", 832, 96);
        loadLevel("adjacent.tmx", 1344, -925);
    }

    public void loadLevel(String name, float x, float y) {
        TiledMap map = new TmxMapLoader().load(name);
        CustomMapRenderer mapRenderer = new CustomMapRenderer(map, x, y);
        mapRenderers.add(mapRenderer);
        bodyBuilder.buildShapes(mapRenderer, world, x, y);

        MapProperties properties = map.getProperties();
        int width = properties.get("width", Integer.class);
        int height = properties.get("height", Integer.class);

        int tileWidth = properties.get("tilewidth", Integer.class);
        int tileHeight = properties.get("tileheight", Integer.class);
        mapBounds.merge(new Rectangle(x, y, width * tileWidth, height * tileHeight));
    }

    @Override
    public void render(float delta) {
        update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1f);

        renderMap();

        batch.begin();
        player.render(batch);
        batch.end();

        debugRenderer.render(world, cameraCpy.scl(game.BOX_TO_WORLD));
    }

    public void renderMap() {
        for (CustomMapRenderer renderer : mapRenderers) {
            renderer.getBatch().begin();
            TiledMap map = renderer.getMap();
            renderer.setMap(map);
            for (MapLayer layer : map.getLayers()) {
                if (layer.isVisible() && !layer.getName().equals("collision")) {
                    if (layer instanceof TiledMapTileLayer) {
                        renderer.renderTileLayer((TiledMapTileLayer) layer);
                    }
                }
            }
            renderer.getBatch().end();
        }
    }

    public void update() {
        player.update();

        this.world.step(1f / 60f, 6, 2);

        followCamera.update();
        camera.update();
        cameraCpy.set(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        for (CustomMapRenderer renderer : mapRenderers) {
            renderer.setView(camera);
        }

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
        bodyBuilder.disposeBodies();
        batch.dispose();
    }
}
