package com.agmcleod.sp;

import com.agmcleod.sp.aibehaviours.Behaviour;
import com.agmcleod.sp.aibehaviours.ChaseBehaviour;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * Created by aaronmcleod on 15-05-31.
 */
public class Enemy extends MapEntity {
    private final float WIDTH = 32;
    private final float HEIGHT = 32;

    private Array<Behaviour> behaviours;
    private Body body;
    private float chaseVelocity = 3.0f;
    private Game game;
    private Vector2 original;
    private boolean playerInSight;
    TextureRegion region;
    private float rotation = 0;
    private Polygon sight;
    private Vector2 target;
    private float velx = 1.5f;
    private float vely = 1.5f;


    public Enemy(Game game) {
        super("enemy");
        this.game = game;
        region = game.getAtlas().findRegion("enemy");

        sight = new Polygon();
        playerInSight = false;
        behaviours = new Array<Behaviour>();
    }

    public void addBehaviour(Behaviour b) {
        behaviours.add(b);
    }

    public void checkSightline(Player player) {
        float[] playerBoundsVertices = player.getBoundsVertices();
        if(Intersector.overlapConvexPolygons(sight.getTransformedVertices(), playerBoundsVertices, null)) {
            playerInSight = true;
        }
        else {
            playerInSight = false;
        }
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

    private ChaseBehaviour getChaseBehaviour() {
        ChaseBehaviour b = null;
        Iterator<Behaviour> it = behaviours.iterator();
        while (it.hasNext()) {
            Behaviour temp = it.next();
            if (temp instanceof ChaseBehaviour) {
                b = (ChaseBehaviour) temp;
            }
        }

        return b;
    }

    public float getChaseVelocity() {
        return chaseVelocity;
    }

    public void moveWithVelocity(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    public void patrolMovement() {
        if (target.y != original.y) {
            body.setLinearVelocity(body.getLinearVelocity().x, vely);
        }

        if (target.x != original.x) {
            body.setLinearVelocity(velx, body.getLinearVelocity().y);
        }
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

    public void renderSight(ShapeRenderer renderer) {
        if (playerInSight) {
            renderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
        }
        else {
            renderer.setColor(1.0f, 1.0f, 0.0f, 1.0f);
        }

        renderer.polygon(sight.getTransformedVertices());
    }

    public void setInitialBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        sight.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        sight.setVertices(new float[] {
                0, 0,
                150, -70,
                150, 70
        });

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
        if (playerInSight) {
            getChaseBehaviour().update();
        }
        else {
            patrolMovement();
        }


        bounds.x = (int) (body.getPosition().x * game.BOX_TO_WORLD) - WIDTH / 2;
        bounds.y = (int) (body.getPosition().y * game.BOX_TO_WORLD) - HEIGHT / 2;

        sight.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);

        if (!playerInSight) {
            if (target.y != original.y) {
                if (vely > 0) {
                    rotation = 90;
                    if ((target.y > original.y && bounds.y >= target.y) || (original.y > target.y && bounds.y >= original.y)) {
                        vely *= -1;
                    }
                }
                else if(vely < 0) {
                    rotation = 270;
                    if ((target.y < original.y && bounds.y <= target.y) || (original.y < target.y && bounds.y <= original.y)) {
                        vely *= -1;
                    }
                }
            }

            if (target.x != original.x) {
                if (velx > 0) {
                    rotation = 0;
                    if ((target.x > original.x && bounds.x >= target.x) || (original.x > target.x && bounds.x >= original.x)) {
                        velx *= -1;
                    }
                } else if (velx < 0) {
                    rotation = 180;
                    if ((target.x < original.x && bounds.x <= target.x) || (original.x < target.x && bounds.x <= original.x)) {
                        velx *= -1;
                    }
                }
            }
        }


        sight.setRotation(rotation);
    }
}
