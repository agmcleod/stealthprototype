package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

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
    private Array<TiledMap> maps;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Player player;

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
        maps = new Array<TiledMap>();
        player = new Player(game);

        loadLevel("startroom.tmx");
    }

    public void loadLevel(String name) {
        TiledMap map = new TmxMapLoader().load(name);
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        bodyBuilder.buildShapes(map, world);

        MapProperties properties = map.getProperties();
        int width = properties.get("width", Integer.class);
        int height = properties.get("height", Integer.class);

        int tileWidth = properties.get("tilewidth", Integer.class);
        int tileHeight = properties.get("tileheight", Integer.class);

        // Rectangle mapBounds = new Rectangle(0, 0, width * tileWidth, height * tileHeight);
        // followCamera = new FollowCamera(camera, player.getPosition(), mapBounds);
        maps.add(map);
    }

    @Override
    public void render(float delta) {
        update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1f);

        renderMap();

        debugRenderer.render(world, cameraCpy.scl(game.BOX_TO_WORLD));
    }

    public void renderMap() {
        mapRenderer.getBatch().begin();
        for (TiledMap map : maps) {
            mapRenderer.setMap(map);
            for (MapLayer layer : map.getLayers()) {
                if (layer.isVisible() && !layer.getName().equals("collision")) {
                    if (layer instanceof TiledMapTileLayer) {
                        mapRenderer.renderTileLayer((TiledMapTileLayer) layer);
                    } else {
                        mapRenderer.renderObjects(layer);
                    }
                }
            }
        }
        mapRenderer.getBatch().end();
    }

    public void update() {
        cameraCpy.set(camera.combined);
        player.update();
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
