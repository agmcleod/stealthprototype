package com.agmcleod.sp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by aaronmcleod on 15-05-31.
 */
public class Enemy extends MapEntity {
    private final float WIDTH = 32;
    private final float HEIGHT = 32;

    private Body body;
    private Game game;
    private Vector2 original;
    TextureRegion region;
    private float rotation = 0;
    private Vector2 target;
    private int velx = 3;
    private int vely = 3;
    public Enemy(Game game) {
        super("enemy");
        this.game = game;
        region = game.getAtlas().findRegion("enemy");
    }

    @Override
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

    public void setInitialBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        original = new Vector2(x, y);

        World world = game.getWorld();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH / 2 * game.WORLD_TO_BOX, HEIGHT / 2 * game.WORLD_TO_BOX);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set((bounds.x + WIDTH / 2) * game.WORLD_TO_BOX, (bounds.y + HEIGHT / 2) * game.WORLD_TO_BOX);

        body = world.createBody(def);
        body.setFixedRotation(true);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        shape.dispose();
    }

    public void setTarget(float x, float y) {
        target = new Vector2(x, y);
    }

    @Override
    public void update() {
        if (target.y != original.y) {
            body.setLinearVelocity(body.getLinearVelocity().x, vely);
            if (vely > 0) {
                rotation = 270;
                if (target.y > original.y && body.getPosition().y >= target.y) {
                    vely *= -1;
                }
            }
            else {
                rotation = 90;
                if (target.y < original.y && body.getPosition().y <= target.y) {
                    vely *= -1;
                }
            }
        }

        if (target.x != original.x) {
            body.setLinearVelocity(velx, body.getLinearVelocity().y);
            if (velx > 0) {
                rotation = 0;
                if (target.x > original.x && body.getPosition().x >= target.x) {
                    velx *= -1;
                }
            }
            else {
                rotation = 180;
                if (target.x < original.x && body.getPosition().x <= target.x) {
                    velx *= -1;
                }
            }
        }

        bounds.x = (int) (body.getPosition().x * game.BOX_TO_WORLD) - WIDTH / 2;
        bounds.y = (int) (body.getPosition().y * game.BOX_TO_WORLD) - HEIGHT / 2;
    }
}
