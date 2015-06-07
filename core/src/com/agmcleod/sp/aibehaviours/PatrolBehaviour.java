package com.agmcleod.sp.aibehaviours;

import com.agmcleod.sp.Enemy;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by aaronmcleod on 15-06-07.
 */
public class PatrolBehaviour extends Behaviour {
    public PatrolBehaviour(Enemy enemy) {
        super(enemy);
    }

    @Override
    public void update() {
        Vector2 target = enemy.getTarget();
        Vector2 original = enemy.getOriginal();
        Body body = enemy.getBody();
        if (target.y != original.y) {
            body.setLinearVelocity(body.getLinearVelocity().x, enemy.getVelY());
        }

        if (target.x != original.x) {
            body.setLinearVelocity(enemy.getVelX(), body.getLinearVelocity().y);
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
