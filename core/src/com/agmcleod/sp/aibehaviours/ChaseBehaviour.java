package com.agmcleod.sp.aibehaviours;

import com.agmcleod.sp.Enemy;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Aaron on 6/5/2015.
 */
public class ChaseBehaviour extends Behaviour {
    private Rectangle target;
    private Vector2 direction;

    public ChaseBehaviour(Enemy enemy) {
        super(enemy);
        target = new Rectangle();
        direction = new Vector2();
    }

    public void setTarget(Rectangle target) {
        this.target = target;
    }

    @Override
    public void update() {
        direction.set(target.x, target.y).sub(enemy.getBounds().x, enemy.getBounds().y).nor();
        direction.scl(enemy.getChaseVelocity());
        enemy.moveWithVelocity(direction.x, direction.y);
    }
}
