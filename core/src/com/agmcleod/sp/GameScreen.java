package com.agmcleod.sp;

import com.agmcleod.sp.aibehaviours.ChaseBehaviour;
import com.agmcleod.sp.aibehaviours.PatrolBehaviour;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
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
    private ObjectMap<String, String> classByName;
    private Array<GameObject> gameObjects;
    private FollowCamera followCamera;
    private Game game;
    private Rectangle mapBounds;
    private Array<CustomMapRenderer> mapRenderers;
    private Player player;
    private boolean restartNextFrame;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;

    public GameScreen(Game game, World world) {
        this.world = world;
        this.world.setContactListener(new CollisionListener(this));
        this.game = game;
        bodyBuilder = new MapBodyBuilder(game, world);
        classByName = new ObjectMap<String, String>();
        classByName.put("enemy", "com.agmcleod.sp.Enemy");
        gameObjects = new Array<GameObject>();
        restartNextFrame = false;
    }

    @Override
    public void show() {
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraCpy = camera.combined.cpy();
        mapRenderers = new Array<CustomMapRenderer>();
        shapeRenderer = new ShapeRenderer();

        player = new Player(game);
        batch = new SpriteBatch();

        mapBounds = new Rectangle();
        followCamera = new FollowCamera(camera, player.getBounds(), mapBounds);

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
        int mapwidth = properties.get("width", Integer.class);
        int mapheight = properties.get("height", Integer.class);

        int tileWidth = properties.get("tilewidth", Integer.class);
        int tileHeight = properties.get("tileheight", Integer.class);
        mapBounds.merge(new Rectangle(x, y, mapwidth * tileWidth, mapheight * tileHeight));

        MapLayer layer = map.getLayers().get("entities");
        if (layer != null) {
            MapObjects objects = layer.getObjects();

            for (MapObject object : objects) {
                String className = object.getName();
                MapProperties objectProperties = object.getProperties();

                if (className.equals("enemy")) {
                    Enemy enemy = (Enemy) ObjectMapToClass.getInstanceOfObject(classByName, className, this.game);
                    enemy.setInitialBounds(objectProperties.get("x", Float.class) + x, objectProperties.get("y", Float.class) + y, objectProperties.get("width", Float.class), objectProperties.get("height", Float.class));
                    float targetY = (mapheight * tileHeight - tileHeight - (Float.parseFloat(objectProperties.get("target_y", String.class))) + y);
                    float targetX = Float.parseFloat(objectProperties.get("target_x", String.class)) + x;
                    enemy.setTarget(targetX, targetY);

                    if (objectProperties.get("aitype", String.class).equals("chase")) {
                        ChaseBehaviour behaviour = new ChaseBehaviour(enemy);
                        behaviour.setTarget(player.getBounds());
                        enemy.addBehaviour(behaviour);
                    }

                    enemy.addBehaviour(new PatrolBehaviour(enemy));

                    gameObjects.add(enemy);
                } else {
                    MapEntity entity = (MapEntity) ObjectMapToClass.getInstanceOfObject(classByName, className, this.game);
                    entity.setBounds(objectProperties.get("x", Float.class) + x, objectProperties.get("y", Float.class) + y, objectProperties.get("width", Float.class), objectProperties.get("height", Float.class));
                    gameObjects.add(entity);
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1f);

        renderMap();

        batch.begin();
        player.render(batch);

        for (GameObject gameObject : gameObjects) {
            gameObject.render(batch);
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Enemy) {
                ((Enemy) gameObject).renderSight(shapeRenderer);
            }
        }
        shapeRenderer.end();

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

    public void restart() {
        restartNextFrame = true;
    }

    public void update() {
        if (restartNextFrame) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Enemy) {
                    ((Enemy) gameObject).reset();
                }
            }
            player.reset();
            restartNextFrame = false;
        }

        player.update();

        this.world.step(1f / 60f, 6, 2);

        followCamera.update();
        camera.update();
        cameraCpy.set(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        for (CustomMapRenderer renderer : mapRenderers) {
            renderer.setView(camera);
        }

        for (GameObject gameObject : gameObjects) {
            gameObject.update();
            if (gameObject instanceof Enemy) {
                ((Enemy) gameObject).checkSightline(player);
            }
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
        shapeRenderer.dispose();
    }
}
