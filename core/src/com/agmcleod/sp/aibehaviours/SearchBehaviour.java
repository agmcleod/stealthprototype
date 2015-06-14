package com.agmcleod.sp.aibehaviours;

import com.agmcleod.sp.Enemy;
import com.agmcleod.sp.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by aaronmcleod on 15-06-14.
 */
public class SearchBehaviour extends Behaviour {
    private boolean waitToReturn;
    private float time;
    private final float CORRECTIVE_FACTOR = 13;
    private Vector2 direction;
    public SearchBehaviour(Enemy enemy) {
        super(enemy);
        direction = new Vector2();
        waitToReturn = false;
    }

    public void moveTowardsLastPosition() {
        final Vector2 lastKnownPosition = enemy.getLastKnownPlayerPosition();
        direction.set(lastKnownPosition.x, lastKnownPosition.y).sub(enemy.getBounds().x, enemy.getBounds().y).nor();
        float xvel = 0;
        float yvel = 0;
        if (direction.x != 0) {
            xvel = enemy.MOVE_SPEED;
        }
        if (direction.y != 0) {
            yvel = enemy.MOVE_SPEED;
        }

        if (direction.x < 0) {
            xvel *= -1;
        }
        if (direction.y < 0) {
            yvel *= -1;
        }

        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            if (xvel > 0) {
                enemy.setRotation(0);
            }
            else {
                enemy.setRotation(180);
            }
        } else {
            if (yvel > 0) {
                enemy.setRotation(90);
            }
            else {
                enemy.setRotation(270);
            }
        }

        Body body = enemy.getBody();

        if (xvel != 0 && Math.abs(lastKnownPosition.x - enemy.getBounds().x) <= enemy.MOVE_SPEED * CORRECTIVE_FACTOR) {
            body.setTransform(lastKnownPosition.x * Game.WORLD_TO_BOX, body.getTransform().getPosition().y, 0);
            xvel = 0;
        }

        if (yvel != 0 && Math.abs(lastKnownPosition.y - enemy.getBounds().y) <= enemy.MOVE_SPEED * CORRECTIVE_FACTOR) {
            body.setTransform(body.getTransform().getPosition().x, lastKnownPosition.y * Game.WORLD_TO_BOX, 0);
            yvel = 0;
        }

        if (xvel == 0 && yvel == 0) {
            body.setLinearVelocity(0, 0);
            resetTime();
            waitToReturn = true;
        } else {
            body.setLinearVelocity(xvel, yvel);
        }
    }

    public void resetTime() {
        time = 0.5f;
    }

    public void start() {
        resetTime();
        waitToReturn = false;
    }

    @Override
    public void update() {
        if (time <= 0) {
            if (waitToReturn) {
                enemy.setRadiusDetectionOn(false);
            }
            else {
                moveTowardsLastPosition();
            }

        }
        else {
            time -= Gdx.graphics.getDeltaTime();
        }
    }
}
