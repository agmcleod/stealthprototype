package com.agmcleod.sp;

import com.agmcleod.sp.aibehaviours.Behaviour;
import com.agmcleod.sp.aibehaviours.ChaseBehaviour;
import com.agmcleod.sp.aibehaviours.PatrolBehaviour;
import com.agmcleod.sp.aibehaviours.SearchBehaviour;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * Created by aaronmcleod on 15-05-31.
 */
public class Enemy extends MapEntity {
    public final float WIDTH = 32;
    public final float HEIGHT = 32;

    private Array<Behaviour> behaviours;
    private Body body;
    private float chaseVelocity = 3.0f;
    private Circle detectArea;
    private Game game;
    private Vector2 lastKnownPlayerPosition;
    private Vector2 original;
    private boolean playerInSight;
    private boolean playerInSightLastFrame;

    private boolean radiusDetectionOn;
    TextureRegion region;
    private float rotation = 0;
    private Polygon sight;
    private Vector2 target;
    public final float MOVE_SPEED = 1.5f;
    private float patrolVelX = MOVE_SPEED;
    private float patrolVelY = MOVE_SPEED;

    private Vector2 raycastOrigin;
    private Vector2 raycastTarget;


    public Enemy(Game game) {
        super("enemy");
        this.game = game;
        region = game.getAtlas().findRegion("enemy");

        sight = new Polygon();
        playerInSight = false;
        behaviours = new Array<Behaviour>();
        raycastTarget = new Vector2();
        raycastOrigin = new Vector2();
        detectArea = new Circle();
        playerInSightLastFrame = false;
        lastKnownPlayerPosition = new Vector2();
    }

    public void addBehaviour(Behaviour b) {
        behaviours.add(b);
    }

    public void checkSightline(Player player) {
        float[] playerBoundsVertices = player.getBoundsVertices();
        if(Intersector.overlapConvexPolygons(sight.getTransformedVertices(), playerBoundsVertices, null)) {
            World world = game.getWorld();
            final Rectangle playerBounds = player.getBounds();
            raycastTarget.set((playerBounds.x + playerBounds.width / 2) * game.WORLD_TO_BOX, (playerBounds.y + playerBounds.height / 2) * game.WORLD_TO_BOX);
            raycastOrigin.set((bounds.x + bounds.width / 2) * game.WORLD_TO_BOX, (bounds.y + bounds.height / 2) * game.WORLD_TO_BOX);
            world.rayCast(new RayCastCallback() {
                @Override
                public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                    if (((GameObject) fixture.getUserData()).name.equals("player")) {
                        playerInSight = true;
                    }
                    else {
                        playerInSight = false;
                    }
                    return fraction;
                }
            }, raycastOrigin, raycastTarget);
        }
        else {
            if (playerInSight) {
                getPatrolBehaviour().setReturnToPatrol(true);
            }
            playerInSight = false;
        }
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

    private ChaseBehaviour getChaseBehaviour() {
        ChaseBehaviour b = null;
        for (Behaviour temp : behaviours) {
            if (temp instanceof ChaseBehaviour) {
                b = (ChaseBehaviour) temp;
            }
        }

        return b;
    }

    private SearchBehaviour getSearchBehaviour() {
        SearchBehaviour sb = null;
        for (Behaviour temp : behaviours) {
            if (temp instanceof SearchBehaviour) {
                sb = (SearchBehaviour) temp;
            }
        }
        return sb;
    }


    public Body getBody() {
        return body;
    }

    public float getChaseVelocity() {
        return chaseVelocity;
    }

    public final Vector2 getLastKnownPlayerPosition() {
        return lastKnownPlayerPosition;
    }

    public final Vector2 getOriginal() {
        return original;
    }

    private PatrolBehaviour getPatrolBehaviour() {
        PatrolBehaviour b = null;
        Iterator<Behaviour> it = behaviours.iterator();
        while (it.hasNext()) {
            Behaviour temp = it.next();
            if (temp instanceof PatrolBehaviour) {
                b = (PatrolBehaviour) temp;
            }
        }

        return b;
    }

    public final Vector2 getTarget() {
        return target;
    }

    public float getVelX() {
        return patrolVelX;
    }

    public float getVelY() {
        return patrolVelY;
    }

    public void moveWithVelocity(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    public void playerIsInSight() {
        playerInSightLastFrame = true;
        Rectangle playerBounds = game.getPlayer().getBounds();
        lastKnownPlayerPosition.set(playerBounds.x, playerBounds.y);
        ChaseBehaviour behaviour = getChaseBehaviour();
        if (behaviour != null) {
            behaviour.update();
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
        if (radiusDetectionOn) {
            renderer.setColor(0.0f, 1.0f, 0.0f, 1.0f);
            renderer.circle(detectArea.x, detectArea.y, detectArea.radius);
        }
    }

    public void reset() {
        playerInSight = false;
        rotation = 0;
        bounds.setPosition(original.x, original.y);
        sight.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        body.setTransform((bounds.x + WIDTH / 2) * game.WORLD_TO_BOX, (bounds.y + HEIGHT / 2) * game.WORLD_TO_BOX, 0);
    }

    public void setInitialBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        sight.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        sight.setVertices(new float[]{
                0, 0,
                180, -100,
                180, 100
        });

        detectArea.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        detectArea.setRadius(150);

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

    public void setRadiusDetectionOn(boolean radiusDetectionOn) {
        this.radiusDetectionOn = radiusDetectionOn;
    }

    public void setRotation(float a) {
        rotation = a;
    }

    public void setTarget(float x, float y) {
        target = new Vector2(x, y);
    }

    public void setVelX(float x) {
        patrolVelX = x;
    }

    public void setVelY(float y) {
        patrolVelY = y;
    }

    @Override
    public void update() {
        if (playerInSight) {
            playerIsInSight();
        }
        else {
            if (playerInSightLastFrame) {
                playerInSightLastFrame = false;
                radiusDetectionOn = true;
                SearchBehaviour sb = getSearchBehaviour();
                if (sb != null) {
                    sb.start();
                }
            }
            if (radiusDetectionOn) {
                SearchBehaviour sb = getSearchBehaviour();
                if (sb != null) {
                    sb.update();
                }
            }
            else {
                getPatrolBehaviour().update();
            }
        }

        bounds.x = (int) (body.getPosition().x * game.BOX_TO_WORLD) - WIDTH / 2;
        bounds.y = (int) (body.getPosition().y * game.BOX_TO_WORLD) - HEIGHT / 2;

        sight.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        detectArea.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);

        if (!playerInSight && !getPatrolBehaviour().isReturnToPatrol()) {
            getPatrolBehaviour().changePatrolDirectionIfAtEnd();
        }

        sight.setRotation(rotation);
    }
}
