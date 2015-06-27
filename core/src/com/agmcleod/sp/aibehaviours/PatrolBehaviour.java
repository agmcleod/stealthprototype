package com.agmcleod.sp.aibehaviours;

import com.agmcleod.sp.Enemy;
import com.agmcleod.sp.Game;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by aaronmcleod on 15-06-07.
 */
public class PatrolBehaviour extends Behaviour {
    private final float CORRECTIVE_FACTOR = 13;
    private Vector2 direction;
    private Vector2 enemyPosition;
    private Vector2 closestPatrolPoint;
    private boolean returnToPatrol;

    public PatrolBehaviour(Enemy enemy) {
        super(enemy);
        returnToPatrol = false;
        closestPatrolPoint = new Vector2();
        direction = new Vector2();
        enemyPosition = new Vector2();
    }

    public void changePatrolDirectionIfAtEnd() {
        Vector2 target = enemy.getTarget();
        Vector2 original = enemy.getOriginal();
        float velx = enemy.getVelX();
        float vely = enemy.getVelY();
        Rectangle bounds = enemy.getBounds();
        if (target.y != original.y) {
            if (vely > 0) {
                enemy.setRotation(90);
                if ((target.y > original.y && bounds.y >= target.y) || (original.y > target.y && bounds.y >= original.y)) {
                    vely *= -1;
                }
            }
            else if(vely < 0) {
                enemy.setRotation(270);
                if ((target.y < original.y && bounds.y <= target.y) || (original.y < target.y && bounds.y <= original.y)) {
                    vely *= -1;
                }
            }
        }

        if (target.x != original.x) {
            if (velx > 0) {
                enemy.setRotation(0);
                if ((target.x > original.x && bounds.x >= target.x) || (original.x > target.x && bounds.x >= original.x)) {
                    velx *= -1;
                }
            } else if (velx < 0) {
                enemy.setRotation(180);
                if ((target.x < original.x && bounds.x <= target.x) || (original.x < target.x && bounds.x <= original.x)) {
                    velx *= -1;
                }
            }
        }

        enemy.setVelX(velx);
        enemy.setVelY(vely);
    }

    public boolean isReturnToPatrol() {
        return returnToPatrol;
    }

    public void setReturnToPatrol(boolean returnToPatrol) {
        if (!this.returnToPatrol && returnToPatrol) {
            setClosestPatrolPoint();
        }
        this.returnToPatrol = returnToPatrol;
    }

    @Override
    public void update() {
        Vector2 target = enemy.getTarget();
        Vector2 original = enemy.getOriginal();
        Body body = enemy.getBody();
        if (returnToPatrol) {
            updateEnemyToReturnToPatrol(body);
        }
        else {
            if (target.y != original.y) {
                body.setLinearVelocity(body.getLinearVelocity().x, enemy.getVelY());
            }

            if (target.x != original.x) {
                body.setLinearVelocity(enemy.getVelX(), body.getLinearVelocity().y);
            }
        }
    }

    public void updateEnemyToReturnToPatrol(Body body) {
        direction.set(closestPatrolPoint.x, closestPatrolPoint.y).sub(enemy.getBounds().x, enemy.getBounds().y).nor();
        float xvel = enemy.MOVE_SPEED * direction.x;
        float yvel = enemy.MOVE_SPEED * direction.y;

        enemy.setRotationFromDirection(direction);

        if (xvel != 0 && Math.abs(closestPatrolPoint.x - enemy.getBounds().x) <= enemy.MOVE_SPEED * CORRECTIVE_FACTOR) {
            body.setTransform(closestPatrolPoint.x * Game.WORLD_TO_BOX, body.getTransform().getPosition().y, 0);
            if (xvel > 0) {
                enemy.setVelX(enemy.MOVE_SPEED);
            }
            else {
                enemy.setVelX(-enemy.MOVE_SPEED);
            }
            xvel = 0;
        }

        if (yvel != 0 && Math.abs(closestPatrolPoint.y - enemy.getBounds().y) <= enemy.MOVE_SPEED * CORRECTIVE_FACTOR) {
            body.setTransform(body.getTransform().getPosition().x, closestPatrolPoint.y * Game.WORLD_TO_BOX, 0);
            if (yvel > 0) {
                enemy.setVelY(enemy.MOVE_SPEED);
            }
            else {
                enemy.setVelY(-enemy.MOVE_SPEED);
            }
            yvel = 0;
        }

        if (xvel == 0 && yvel == 0) {
            body.setLinearVelocity(0, 0);
            returnToPatrol = false;
        } else {
            body.setLinearVelocity(xvel, yvel);
        }
    }

    // Private methods

    private void setClosestPatrolPoint() {
        enemy.getBounds().getPosition(enemyPosition);
        Intersector.nearestSegmentPoint(enemy.getOriginal(), enemy.getTarget(), enemyPosition, closestPatrolPoint);
        closestPatrolPoint.add(enemy.WIDTH / 2, enemy.HEIGHT / 2);
    }
}
