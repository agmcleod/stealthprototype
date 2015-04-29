package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Created by aaronmcleod on 15-04-27.
 */
public class LevelBuilder {
    private Array<Body> bodies;
    private Game game;
    private World world;

    public LevelBuilder(Game game, World world) {
        bodies = new Array<Body>();
        this.world = world;
        this.game = game;
    }

    public void build() {
        // left side
        PolygonShape shape = new PolygonShape();
        float width = 32;
        float height = 256;
        Vector2 size = new Vector2(
                width * 0.5f * game.WORLD_TO_BOX,
                Gdx.graphics.getHeight() / 2 * game.WORLD_TO_BOX
        );
        shape.setAsBox(width * 0.5f * game.WORLD_TO_BOX, height * 0.5f * game.WORLD_TO_BOX, size, 0f);

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bd);
        Fixture f = body.createFixture(shape, 1);
        f.setUserData(new GameObject("mapCollision"));
        f.setRestitution(0f);

        bodies.add(body);
        shape.dispose();

        // top left angle
        shape = new PolygonShape();
        float startY = (Gdx.graphics.getHeight() / 2 + height / 2);
        shape.set(new float[] {
            0f, startY * game.WORLD_TO_BOX,
                width * game.WORLD_TO_BOX, startY * game.WORLD_TO_BOX,
                200f * game.WORLD_TO_BOX, (startY + 200f) * game.WORLD_TO_BOX,
                (200f - width) * game.WORLD_TO_BOX, (startY + 200f) * game.WORLD_TO_BOX
        });
        bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bd);
        f = body.createFixture(shape, 1);
        f.setUserData(new GameObject("mapCollision"));
        f.setRestitution(0f);
        shape.dispose();
    }


}
