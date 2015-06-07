package com.agmcleod.sp.aibehaviours;

import com.agmcleod.sp.Enemy;
import com.agmcleod.sp.Game;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by aaronmcleod on 15-06-07.
 */
public class PatrolBehaviour extends Behaviour {
    private Vector2 direction;
    private Vector2 lastPatrolPoint;
    private boolean returnToPatrol;
    public PatrolBehaviour(Enemy enemy) {
        super(enemy);
        returnToPatrol = false;
        lastPatrolPoint = new Vector2();
        direction = new Vector2();
    }

    public void setLastPatrolPoint(float x, float y) {
        this.lastPatrolPoint.set(x, y);
    }

    public void setReturnToPatrol(boolean returnToPatrol) {
        this.returnToPatrol = returnToPatrol;
    }

    @Override
    public void update() {
        Vector2 target = enemy.getTarget();
        Vector2 original = enemy.getOriginal();
        Body body = enemy.getBody();
        if (returnToPatrol) {
            direction.set(lastPatrolPoint.x, lastPatrolPoint.y).sub(enemy.getBounds().x, enemy.getBounds().y).nor();
            float xvel = 0;
            float yvel = 0;
            if (Math.abs(direction.x) > Math.abs(direction.y)) {
                xvel = enemy.MOVE_SPEED;
                if (direction.x < 0) {
                    xvel *= -1;
                }
            }
            else if (Math.abs(direction.y) > Math.abs(direction.x)) {
                yvel = enemy.MOVE_SPEED;
                if (direction.y < 0) {
                    yvel *= -1;
                }
            }
            else {
                xvel = enemy.MOVE_SPEED;
                yvel = enemy.MOVE_SPEED;

                if (direction.x < 0) {
                    xvel *= -1;
                }
                if (direction.y < 0) {
                    yvel *= -1;
                }
            }

            if (xvel != 0 && Math.abs(xvel) > Math.abs(direction.x)) {
                body.setTransform(lastPatrolPoint.x * Game.WORLD_TO_BOX, body.getTransform().getPosition().y, 0);
                xvel = 0;
            }

            if (yvel != 0 && Math.abs(yvel) > Math.abs(direction.y)) {
                body.setTransform(body.getTransform().getPosition().x, lastPatrolPoint.y * Game.WORLD_TO_BOX, 0);
                yvel = 0;
            }

            if (direction.x == 0 && direction.y == 0) {
                returnToPatrol = false;
            }
            else {
                body.setLinearVelocity(xvel, yvel);
            }
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

    public void updateEnemyVelocity() {
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
}
