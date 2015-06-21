package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by aaronmcleod on 15-04-30.
 */
public class Player extends GameObject {
    private Body body;
    private Rectangle bounds;
    private float[] boundsVertices;
    private boolean dirtyVertices;
    private GameScreen gs;
    final float WIDTH = 32;
    final float HEIGHT = 32;
    private final int VEL = 3;
    private TextureRegion region;
    private float rotation;
    public Player(GameScreen gs) {
        super("player");
        rotation = 0;
        this.gs = gs;
        this.region = gs.getGame().getAtlas().findRegion("player");
        World world = gs.getGame().getWorld();

        bounds = new Rectangle();

        resetBoundsToOriginal();

        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(WIDTH / 2 * Game.WORLD_TO_BOX, HEIGHT / 2 * Game.WORLD_TO_BOX);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set((bounds.x + WIDTH) * Game.WORLD_TO_BOX, (bounds.y + HEIGHT / 2) * Game.WORLD_TO_BOX);

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

        dirtyVertices = true;
        boundsVertices = new float[8];
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float[] getBoundsVertices() {
        if (!dirtyVertices) {
            return boundsVertices;
        }

        boundsVertices[0] = bounds.x;
        boundsVertices[1] = bounds.y;
        boundsVertices[2] = bounds.x;
        boundsVertices[3] = bounds.y + bounds.height;
        boundsVertices[4] = bounds.x + bounds.width;
        boundsVertices[5] = bounds.y + bounds.height;
        boundsVertices[6] = bounds.x + bounds.width;
        boundsVertices[7] = bounds.y;

        dirtyVertices = false;

        return boundsVertices;
    }

    public void render(SpriteBatch batch) {
        float x = bounds.x;
        float y = bounds.y;

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

        batch.draw(region, x, y, 0, 0, WIDTH, HEIGHT, 1.0f, 1.0f, rotation);
    }

    public void reset() {
        resetBoundsToOriginal();
        rotation = 0;
        body.setTransform(bounds.x * Game.WORLD_TO_BOX, bounds.y * Game.WORLD_TO_BOX, 0);
    }

    public void resetBoundsToOriginal() {
        bounds.set(WIDTH, Gdx.graphics.getHeight() / 2, WIDTH, HEIGHT);
    }

    public void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-VEL, body.getLinearVelocity().y);
            rotation = 180;
            dirtyVertices = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(VEL, body.getLinearVelocity().y);
            rotation = 0;
            dirtyVertices = true;
        }
        else {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.setLinearVelocity(body.getLinearVelocity().x, VEL);
            rotation = 90;
            dirtyVertices = true;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            body.setLinearVelocity(body.getLinearVelocity().x, -VEL);
            rotation = 270;
            dirtyVertices = true;
        }
        else {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
        }

        bounds.x = (int) ((body.getPosition().x * Game.BOX_TO_WORLD) - WIDTH / 2);
        bounds.y = (int) ((body.getPosition().y * Game.BOX_TO_WORLD) - HEIGHT / 2);
    }
}
