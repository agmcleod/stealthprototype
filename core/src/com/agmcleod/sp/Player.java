package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by aaronmcleod on 15-04-30.
 */
public class Player extends GameObject {
    private Body body;
    private Game game;
    private Vector2 position;
    private final float WIDTH = 32;
    private final float HEIGHT = 32;
    private final int VEL = 3;
    public Player(Game game) {
        super("player");
        position = new Vector2(WIDTH, Gdx.graphics.getHeight() / 2);
        this.game = game;
        World world = game.getWorld();

        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(WIDTH / 2 * game.WORLD_TO_BOX, HEIGHT / 2 * game.WORLD_TO_BOX);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set((position.x + WIDTH / 2) * game.WORLD_TO_BOX, (position.y + HEIGHT / 2) * game.WORLD_TO_BOX);

        body = world.createBody(def);
        body.setFixedRotation(true);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = playerShape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        playerShape.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }

    public void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-VEL, body.getLinearVelocity().y);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(VEL, body.getLinearVelocity().y);
        }
        else {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.setLinearVelocity(body.getLinearVelocity().x, VEL);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            body.setLinearVelocity(body.getLinearVelocity().x, -VEL);
        }
        else {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
        }

        position.set((int) (body.getPosition().x * game.BOX_TO_WORLD) + 16, (int) (body.getPosition().y * game.BOX_TO_WORLD) - 16);
    }
}
