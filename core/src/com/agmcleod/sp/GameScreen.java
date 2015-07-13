package com.agmcleod.sp;

import com.agmcleod.sp.aibehaviours.ChaseBehaviour;
import com.agmcleod.sp.aibehaviours.PatrolBehaviour;
import com.agmcleod.sp.aibehaviours.SearchBehaviour;
import com.agmcleod.sp.aibehaviours.ShootBehaviour;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by aaronmcleod on 15-04-27.
 */
public class GameScreen implements Screen {

    private class Level {
        public String name;
        public float x;
        public float y;
        public Level(String name, float x, float y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }

    private World world;
    private boolean allowPlayerMovement;
    private SpriteBatch batch;
    private MapBodyBuilder bodyBuilder;
    private OrthographicCamera camera;
    private Matrix4 cameraCpy;
    private Level currentLevel;
    private Box2DDebugRenderer debugRenderer;
    private float fadeTimer;
    private FollowCamera followCamera;
    private Game game;
    private Array<GameObject> gameObjects;
    private boolean isPaused;
    private Rectangle mapBounds;
    private Array<CustomMapRenderer> mapRenderers;
    private Player player;
    private Array<GameObject> objectsToRemove;
    private boolean restartNextFrame;
    private ShapeRenderer shapeRenderer;
    private TransitionCallback resetTransitionCallback;
    private boolean transitioning;

    public GameScreen(Game game, World world) {
        this.world = world;
        this.world.setContactListener(new CollisionListener(this));
        this.game = game;
        bodyBuilder = new MapBodyBuilder(game, world);
        gameObjects = new Array<GameObject>();
        restartNextFrame = false;
        transitioning = false;
        resetTransitionCallback = new TransitionCallback() {
            @Override
            public void callback() {
                restartNextFrame = true;
            }
        };
        allowPlayerMovement = true;
        isPaused = false;
        objectsToRemove = new Array<GameObject>();
    }

    public void allowPlayerMovement(boolean allow) {
        allowPlayerMovement = allow;
        if (!allowPlayerMovement) {
            player.stop();
        }
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public BitmapFont getUiFont() {
        return game.getUiFont();
    }

    public void loadLevel(String name, float x, float y) {
        currentLevel = new Level(name, x, y);
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
                    Enemy enemy = new Enemy(this);
                    enemy.setInitialBounds(objectProperties.get("x", Float.class) + x, objectProperties.get("y", Float.class) + y, objectProperties.get("width", Float.class), objectProperties.get("height", Float.class));
                    float targetY = (mapheight * tileHeight - tileHeight - (Float.parseFloat(objectProperties.get("target_y", String.class))) + y);
                    float targetX = Float.parseFloat(objectProperties.get("target_x", String.class)) + x;
                    enemy.setTarget(targetX, targetY);

                    enemy.addBehaviour(new PatrolBehaviour(enemy));

                    if (objectProperties.get("aitype", String.class).equals("chase")) {
                        ChaseBehaviour behaviour = new ChaseBehaviour(enemy);
                        behaviour.setTarget(player.getBounds());
                        enemy.addBehaviour(behaviour);
                        SearchBehaviour sb = new SearchBehaviour(enemy);
                        enemy.addBehaviour(sb);
                        enemy.setType("chase");
                    }
                    else if (objectProperties.get("aitype", String.class).equals("shoot")) {
                        ShootBehaviour sb = new ShootBehaviour(game, enemy);
                        enemy.addBehaviour(sb);
                        enemy.setType("shoot");
                    }

                    gameObjects.add(enemy);
                }
                else if (className.equals("trigger")) {
                    Trigger trigger = new Trigger(this);
                    trigger.setTypeByString(objectProperties.get("action", String.class));
                    trigger.setBody(bodyBuilder.buildSingleBody(world, object, BodyDef.BodyType.StaticBody, x * Game.WORLD_TO_BOX, y * Game.WORLD_TO_BOX, Game.TRIGGER_MASK, Game.PLAYER_MASK, true, trigger));
                    gameObjects.add(trigger);
                }
                else if (className.equals("hackablecomponent")) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    HackableComponent hackComponent = new HackableComponent(this, rect.x + x, rect.y + y + rect.height / 2);
                    hackComponent.setType(objectProperties.get("type", null, String.class));
                    hackComponent.setBody(bodyBuilder.buildSingleBody(world, object, BodyDef.BodyType.StaticBody, x * Game.WORLD_TO_BOX, y * Game.WORLD_TO_BOX, Game.TRIGGER_MASK, Game.PLAYER_MASK, false, hackComponent));
                    gameObjects.add(hackComponent);
                }
                else {
                    MapEntity entity = new MapEntity("map entity");
                    entity.setBounds(objectProperties.get("x", Float.class) + x, objectProperties.get("y", Float.class) + y, objectProperties.get("width", Float.class), objectProperties.get("height", Float.class));
                    gameObjects.add(entity);
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSLASH)) {
            isPaused = !isPaused;
        }
        if (!isPaused) {
            update();
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1f);

        renderMap();

        batch.begin();
        player.render(batch);

        for (GameObject gameObject : gameObjects) {
            gameObject.render(batch);
        }

        batch.end();

        // draw enemy sight information
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Enemy) {
                ((Enemy) gameObject).renderSight(shapeRenderer);
            }
        }
        shapeRenderer.end();

        // render objects that use shapes instead of images
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Enemy) {
                ((Enemy) gameObject).renderBullet(shapeRenderer);
            }
            else if (gameObject instanceof HackableComponent) {
                HackableComponent hc = (HackableComponent) gameObject;
                if (hc.isHacking()) {
                    hc.getHackAction().renderShape(shapeRenderer);
                }
            }
        }
        shapeRenderer.end();

        //debugRenderer.render(world, cameraCpy.scl(game.BOX_TO_WORLD));

        if (transitioning) {
            game.drawBlackTransparentSquare(camera, shapeRenderer, fadeTimer / 0.5f, resetTransitionCallback);
            fadeTimer += Gdx.graphics.getDeltaTime();
        }
    }

    public void removeObject(GameObject object) {
        objectsToRemove.add(object);
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
        fadeTimer = 0;
        transitioning = true;
    }

    @Override
    public void show() {
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraCpy = camera.combined.cpy();
        mapRenderers = new Array<CustomMapRenderer>();
        shapeRenderer = new ShapeRenderer();

        player = new Player(this);
        batch = new SpriteBatch();

        mapBounds = new Rectangle();
        followCamera = new FollowCamera(camera, player.getBounds(), mapBounds);

        loadLevel("adjacent.tmx", 0, -928);
    }

    public void showHidden(boolean value) {
        CustomMapRenderer mapRenderer = mapRenderers.get(0);
        mapRenderer.setShowHidden(value);
    }

    public void update() {
        if (restartNextFrame) {
            for (GameObject object : gameObjects) {
                object.dispose(world);
            }
            gameObjects.clear();
            player.reset();
            followCamera.update();
            camera.update();
            loadLevel(currentLevel.name, currentLevel.x, currentLevel.y);
            world.step(1f / 60f, 6, 2);
            transitioning = false;
            restartNextFrame = false;
            allowPlayerMovement = true;
        }
        else if (!transitioning) {
            if (allowPlayerMovement) {
                player.update();
            }

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
                    Enemy enemy = (Enemy) gameObject;
                    enemy.checkSightline(player);
                    if (!player.isCrouching()) {
                        enemy.checkWithinDetectArea(player.getBounds());
                    }
                }
                else if (gameObject instanceof HackableComponent) {
                    HackableComponent component = (HackableComponent) gameObject;
                    component.update();
                }
            }

            world.step(1f / 60f, 6, 2);

            if (objectsToRemove.size > 0) {
                for (GameObject object : objectsToRemove) {
                    int idx = 0;
                    int removeIndex = -1;
                    for (GameObject gameObject : gameObjects) {
                        if (object.equals(gameObject)) {
                            removeIndex = idx;
                            break;
                        }
                        idx++;
                    }

                    if (removeIndex >= 0) {
                        object.dispose(world);
                        gameObjects.removeIndex(removeIndex);
                    }
                }
                objectsToRemove.clear();
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
        for (GameObject object : gameObjects) {
            object.dispose(world);
        }
        player.dispose(world);

        batch.dispose();
        shapeRenderer.dispose();
        world.dispose();
    }
}
