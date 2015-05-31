package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private TextureRegion region;
    private float rotation;
    public Player(Game game, TextureRegion region) {
        super("player");
        position = new Vector2(WIDTH, Gdx.graphics.getHeight() / 2);
        rotation = 0;
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
        this.region = region;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void render(SpriteBatch batch) {
        float x = position.x;
        float y = position.y;

        if (rotation == 90) {
            x += 32;
        }
        else if (rotation == 180) {
            x += 32;
            y += 32;
        }
        else if (rotation == 270) {
            y += 32;
        }

        batch.draw(region, x, y, 0, 0, 32, 32, 1.0f, 1.0f, rotation);
    }

    public void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-VEL, body.getLinearVelocity().y);
            rotation = 180;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(VEL, body.getLinearVelocity().y);
            rotation = 0;
        }
        else {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.setLinearVelocity(body.getLinearVelocity().x, VEL);
            rotation = 90;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            body.setLinearVelocity(body.getLinearVelocity().x, -VEL);
            rotation = 270;
        }
        else {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
        }

        position.set((int) (body.getPosition().x * game.BOX_TO_WORLD) - 16, (int) (body.getPosition().y * game.BOX_TO_WORLD) - 16);
    }
}
