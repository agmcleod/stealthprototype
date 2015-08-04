package com.agmcleod.sp;

import com.agmcleod.sp.aibehaviours.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.Path;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
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
    private Behaviour currentBehaviour;
    private Circle detectArea;
    private GameScreen gs;
    private Vector2 lastKnownPlayerPosition;
    private Vector2 original;
    private boolean playerInSight;

    private boolean radiusDetectionOn;
    TextureRegion region;
    private float rotation = 0;
    private Polygon sight;
    private Box2dSteeringEntity steeringEntity;
    private Vector2 target;
    private String type;
    public final float MOVE_SPEED = 1.5f;
    private float patrolVelX = MOVE_SPEED;
    private float patrolVelY = MOVE_SPEED;

    private Vector2 raycastOrigin;
    private Vector2 raycastTarget;


    public Enemy(GameScreen gs, MapProperties properties) {
        super("enemy");
        this.gs = gs;
        region = gs.getGame().getAtlas().findRegion("enemy");

        sight = new Polygon();
        playerInSight = false;
        behaviours = new Array<Behaviour>();
        raycastTarget = new Vector2();
        raycastOrigin = new Vector2();
        detectArea = new Circle();
        lastKnownPlayerPosition = new Vector2();
        radiusDetectionOn = false;
        setupBasedOnType(properties);
    }

    public void addBehaviour(Behaviour b) {
        behaviours.add(b);
        if (behaviours.size == 1) {
            currentBehaviour = behaviours.get(0);
        }
    }

    public void checkSightline(Player player) {
        float[] playerBoundsVertices = player.getBoundsVertices();
        if(Intersector.overlapConvexPolygons(sight.getTransformedVertices(), playerBoundsVertices, null)) {
            World world = gs.getGame().getWorld();
            final Rectangle playerBounds = player.getBounds();
            raycastTarget.set((playerBounds.x + playerBounds.width / 2) * Game.WORLD_TO_BOX, (playerBounds.y + playerBounds.height / 2) * Game.WORLD_TO_BOX);
            raycastOrigin.set((bounds.x + bounds.width / 2) * Game.WORLD_TO_BOX, (bounds.y + bounds.height / 2) * Game.WORLD_TO_BOX);
            world.rayCast(new RayCastCallback() {
                @Override
                public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                    if (((GameObject) fixture.getUserData()).name.equals("player")) {
                        playerInSight = true;
                        return fraction;
                    }
                    else if (((GameObject) fixture.getUserData()).name.equals("bullet")) {
                        return 1;
                    }
                    else {
                        playerInSight = false;
                        return fraction;
                    }
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

    public void checkWithinDetectArea(Rectangle playerBounds) {
        if (currentBehaviour == getSearchBehaviour() && MyMath.rectOverlapsCircle(playerBounds, detectArea)) {
            lastKnownPlayerPosition.set(playerBounds.x + playerBounds.width / 2, playerBounds.y + playerBounds.height / 2);
            getSearchBehaviour().start();
        }
    }

    public void dispose(World world) {
        world.destroyBody(body);
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

    public float getRotation() {
        return rotation;
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

    private ShootBehaviour getShootBehaviour() {
        ShootBehaviour sb = null;
        for (Behaviour temp : behaviours) {
            if (temp instanceof ShootBehaviour) {
                sb = (ShootBehaviour) temp;
            }
        }
        return sb;
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

    public void huntDownPlayer() {
        setRadiusDetectionOn(true);
        updateLastKnownPlayerPosition();
        SearchBehaviour sb = getSearchBehaviour();
        sb.start();
        currentBehaviour = sb;
    }

    public void moveWithVelocity(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    public void playerIsInSight() {
        updateLastKnownPlayerPosition();
        if (type.equals("chase")) {
            if (currentBehaviour != getChaseBehaviour()) {
                currentBehaviour = getChaseBehaviour();
            }
            else {
                currentBehaviour.update();
            }
        }
        else if (type.equals("shoot")) {
            if (currentBehaviour != getShootBehaviour()) {
                ShootBehaviour sb = getShootBehaviour();
                sb.start(lastKnownPlayerPosition, MathUtils.atan2(lastKnownPlayerPosition.y - bounds.y, lastKnownPlayerPosition.x - bounds.x));
                gs.allowPlayerMovement(false);
                currentBehaviour = sb;
                body.setLinearVelocity(0, 0);
            }
            else {
                currentBehaviour.update();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        bounds.x = (int) (body.getPosition().x * Game.BOX_TO_WORLD) - WIDTH / 2;
        bounds.y = (int) (body.getPosition().y * Game.BOX_TO_WORLD) - HEIGHT / 2;

        sight.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        detectArea.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
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

    public void renderBullet(ShapeRenderer renderer) {
        ShootBehaviour sb = getShootBehaviour();
        if (sb != null && sb.hasShot()) {
            sb.getBullet().renderShape(renderer);
        }
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
            renderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
            renderer.circle(lastKnownPlayerPosition.x, lastKnownPlayerPosition.y, 30);
        }
    }

    public void setRadiusDetectionOn(boolean radiusDetectionOn) {
        this.radiusDetectionOn = radiusDetectionOn;
    }

    public void setRotation(float a) {
        rotation = a;
        if (rotation >= 360) {
            rotation = 0;
        }
    }

    public void setRotationFromDirection(Vector2 direction) {
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            if (direction.x > 0) {
                setRotation(0);
            }
            else {
                setRotation(180);
            }
        } else {
            if (direction.y > 0) {
                setRotation(90);
            }
            else {
                setRotation(270);
            }
        }
    }

    public void setTarget(float x, float y) {
        target = new Vector2(x, y);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVelX(float x) {
        patrolVelX = x;
    }

    public void setVelY(float y) {
        patrolVelY = y;
    }

    @Override
    public void update() {
        steeringEntity.update(Gdx.graphics.getDeltaTime());
        /* if (playerInSight) {
            playerIsInSight();
        }
        else {
            if (currentBehaviour == getChaseBehaviour() && type.equals("chase")) {
                radiusDetectionOn = true;
                SearchBehaviour sb = getSearchBehaviour();
                sb.start();
                currentBehaviour = sb;
            }
            else if (radiusDetectionOn) {
                currentBehaviour.update();
            }
            else {
                currentBehaviour = getPatrolBehaviour();
                currentBehaviour.update();
            }
        }

        if (currentBehaviour == getPatrolBehaviour() && !getPatrolBehaviour().isReturnToPatrol()) {
            getPatrolBehaviour().changePatrolDirectionIfAtEnd();
        }

        sight.setRotation(rotation); */
    }

    private void setInitialBounds(float x, float y, float width, float height) {
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

        World world = gs.getGame().getWorld();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH / 2 * Game.WORLD_TO_BOX, HEIGHT / 2 * Game.WORLD_TO_BOX);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set((bounds.x + WIDTH / 2) * Game.WORLD_TO_BOX, (bounds.y + HEIGHT / 2) * Game.WORLD_TO_BOX);

        body = world.createBody(def);
        body.setFixedRotation(true);


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = Game.ENEMY_MASK;
        fixtureDef.filter.maskBits = Game.PLAYER_MASK | Game.WORLD_MASK;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        shape.dispose();
    }

    private void setupBasedOnType(MapProperties objectProperties) {
        this.type = objectProperties.get("aitype", String.class);
        setInitialBounds(objectProperties.get("x", Float.class), objectProperties.get("y", Float.class), objectProperties.get("width", Float.class), objectProperties.get("height", Float.class));
        steeringEntity = new Box2dSteeringEntity(body, true, ((bounds.width + bounds.height) / 4f) * Game.WORLD_TO_BOX);
        Array<Vector2> wayPoints = new Array<Vector2>();
        wayPoints.add(new Vector2(objectProperties.get("target_x", Float.class), objectProperties.get("target_y", Float.class)).scl(Game.WORLD_TO_BOX));
        wayPoints.add(new Vector2(bounds.x, bounds.y).scl(Game.WORLD_TO_BOX));

        FollowPath followPathBehaviour = new FollowPath<Vector2, LinePath.LinePathParam>(steeringEntity, new LinePath<Vector2>(wayPoints))
            .setTimeToTarget(5f) //
            .setArrivalTolerance(0.001f) //
            .setDecelerationRadius(80);

        steeringEntity.setSteeringBehavior(followPathBehaviour);

        setTarget(objectProperties.get("target_x", Float.class), objectProperties.get("target_y", Float.class));

        addBehaviour(new PatrolBehaviour(this));
        addBehaviour(new SearchBehaviour(this));

        if (type.equals("chase")) {
            ChaseBehaviour behaviour = new ChaseBehaviour(this);
            behaviour.setTarget(gs.getPlayer().getBounds());
            addBehaviour(behaviour);
        }
        else if (type.equals("shoot")) {
            ShootBehaviour sb = new ShootBehaviour(gs.getGame(), this);
            addBehaviour(sb);
        }
    }

    private void updateLastKnownPlayerPosition() {
        Rectangle playerBounds = gs.getPlayer().getBounds();
        lastKnownPlayerPosition.set(playerBounds.x, playerBounds.y);
    }
}
