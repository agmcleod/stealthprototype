package com.agmcleod.sp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Aaron on 6/16/2015.
 */
public class Bullet extends GameObject {
    private float WIDTH = 16;
    private float HEIGHT = 4;

    private boolean active;
    private Body body;
    private Rectangle bounds;
    private Enemy enemy;
    private World world;
    private float rotation;
    private Rectangle target;
    private Vector2 direction;
    public String name;


    private final float VELOCITY = 10f;
    public Bullet(Game game, float x, float y) {
        super("bullet");
        rotation = 0;
        bounds = new Rectangle(x, y, WIDTH, HEIGHT);

        this.world = game.getWorld();

        target = new Rectangle();
        direction = new Vector2();
        active = false;
    }

    public void dispose() {
        dispose(world);
    }

    public void dispose(World world) {
        if (body != null) {
            world.destroyBody(body);
        }
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    public void renderShape(ShapeRenderer renderer) {
        renderer.setColor(Color.RED);
        renderer.rect(bounds.x, bounds.y, bounds.width / 2, bounds.height / 2, bounds.width, bounds.height, 1, 1, rotation);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setup(float x, float y) {
        bounds.x = x;
        bounds.y = y;
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

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation * MathUtils.radiansToDegrees;
        body.setTransform(body.getTransform().getPosition().x, body.getTransform().getPosition().y, rotation);
    }

    public void setTarget(float x, float y) {
        target.setPosition(x, y);
    }

    public void update() {
        bounds.x = (int) ((body.getPosition().x * Game.BOX_TO_WORLD) - WIDTH / 2);
        bounds.y = (int) ((body.getPosition().y * Game.BOX_TO_WORLD) - HEIGHT / 2);
        direction.set(target.x, target.y).sub(bounds.x, bounds.y).nor();
        body.setLinearVelocity(VELOCITY * direction.x, VELOCITY * direction.y);
    }
}
