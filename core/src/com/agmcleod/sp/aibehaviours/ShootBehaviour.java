package com.agmcleod.sp.aibehaviours;

import com.agmcleod.sp.Bullet;
import com.agmcleod.sp.Enemy;
import com.agmcleod.sp.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Aaron on 6/16/2015.
 */
public class ShootBehaviour extends Behaviour {
    private Bullet bullet;
    private float shootTimeout;
    public ShootBehaviour(Game game, Enemy enemy) {
        super(enemy);
        bullet = new Bullet(game, enemy.getBounds().x, enemy.getBounds().y);
        bullet.setEnemy(enemy);
    }

    public Bullet getBullet() {
        return bullet;
    }

    public boolean hasShot() {
        return bullet.isActive();
    }

    public void setTargetAngle(float a) {
        bullet.setRotation(a);
    }

    public void setTarget(float x, float y) {
        bullet.setTarget(x, y);
    }

    public void start() {
        shootTimeout = 0.15f;
        Rectangle enemyBounds = enemy.getBounds();
        bullet.setPosition(enemyBounds.x + enemyBounds.width / 2, enemyBounds.y + enemyBounds.height / 2);
    }

    @Override
    public void update() {
        if (shootTimeout > 0) {
            shootTimeout -= Gdx.graphics.getDeltaTime();
            if (shootTimeout <= 0) {
                bullet.setActive(true);
            }
        }
        else if (bullet.isActive()) {
            bullet.update();
        }
    }
}
