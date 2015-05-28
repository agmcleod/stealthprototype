package com.agmcleod.sp;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * Created by aaronmcleod on 15-03-04.
 */
public class MapBodyBuilder {

    private Array<Body> bodies;
    private Game game;
    private World world;
    public MapBodyBuilder(Game game, World world) {
        this.game = game;
        this.world = world;
        bodies = new Array<Body>();
    }

    public Array<Body> buildShapes(CustomMapRenderer renderer, World world, float x, float y) {
        TiledMap map = renderer.getMap();
        MapObjects objects = map.getLayers().get("collision").getObjects();

        x *= game.WORLD_TO_BOX;
        y *= game.WORLD_TO_BOX;

        for (MapObject object : objects) {
            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;

            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject)object, x, y);
            }
            else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject)object, x, y);
            }
            else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject)object, x, y);
            }
            else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject)object, x, y);
            }
            else {
                continue;
            }

            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bd);
            Fixture f = body.createFixture(shape, 1);
            f.setUserData(new GameObject("mapCollision"));
            f.setRestitution(0f);

            bodies.add(body);

            shape.dispose();
        }
        return bodies;
    }

    public void disposeBodies() {
        if (bodies.size > 0) {
            Iterator<Body> it = bodies.iterator();
            while (it.hasNext()) {
                Body body = it.next();
                world.destroyBody(body);
            }
        }
    }

    private PolygonShape getRectangle(RectangleMapObject rectangleObject, float x, float y) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) * game.WORLD_TO_BOX + x,
                (rectangle.y + rectangle.height * 0.5f ) * game.WORLD_TO_BOX + y);
        polygon.setAsBox((rectangle.width * 0.5f) * game.WORLD_TO_BOX,
                (rectangle.height * 0.5f) * game.WORLD_TO_BOX,
                size,
                0.0f);
        return polygon;
    }

    private CircleShape getCircle(CircleMapObject circleObject, float x, float y) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius * game.WORLD_TO_BOX);
        circleShape.setPosition(new Vector2(circle.x * game.WORLD_TO_BOX + x, circle.y * game.WORLD_TO_BOX + y));
        return circleShape;
    }

    private PolygonShape getPolygon(PolygonMapObject polygonObject, float x, float y) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] * game.WORLD_TO_BOX;
            if (i % 2 == 0) {
                worldVertices[i] += x;
            }
            else {
                worldVertices[i] += y;
            }
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private ChainShape getPolyline(PolylineMapObject polylineObject, float x, float y) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] * game.WORLD_TO_BOX + x;
            worldVertices[i].y = vertices[i * 2 + 1] * game.WORLD_TO_BOX + y;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }
}
