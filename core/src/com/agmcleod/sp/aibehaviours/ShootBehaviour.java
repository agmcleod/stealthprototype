package com.agmcleod.sp.aibehaviours;

import com.agmcleod.sp.Bullet;
import com.agmcleod.sp.Enemy;
import com.agmcleod.sp.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Aaron on 6/16/2015.
 */
public class ShootBehaviour extends Behaviour {
    private float angle;
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

    public void reset() {
        bullet.setActive(false);
        bullet.dispose();
    }

    public void setTarget(float x, float y) {
        bullet.setTarget(x, y);
    }

    public void start(Vector2 targetPosition, float angle) {
        shootTimeout = 0.15f;
        this.angle = angle;
        setTarget(targetPosition.x, targetPosition.y);
    }

    @Override
    public void update() {
        if (shootTimeout > 0) {
            shootTimeout -= Gdx.graphics.getDeltaTime();
            if (shootTimeout <= 0) {
                bullet.setActive(true);
                Rectangle enemyBounds = enemy.getBounds();
                bullet.setup(enemyBounds.x + enemyBounds.width / 2, enemyBounds.y + enemyBounds.height / 2);
                bullet.setRotation(angle);
            }
        }
        else if (bullet.isActive()) {
            bullet.update();
        }
    }
}
