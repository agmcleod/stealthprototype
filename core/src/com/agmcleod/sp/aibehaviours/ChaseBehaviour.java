package com.agmcleod.sp.aibehaviours;

import com.agmcleod.sp.Enemy;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Aaron on 6/5/2015.
 */
public class ChaseBehaviour extends Behaviour {
    private Rectangle target;

    public ChaseBehaviour(Enemy enemy) {
        super(enemy);
        target = new Rectangle();
    }

    public void setTarget(Rectangle target) {
        this.target = target;
    }

    @Override
    public void update() {
        float angle = MathUtils.atan2(enemy.getBounds().y - target.y, enemy.getBounds().x - target.x);
        enemy.moveWithVelocity((float) Math.cos(angle) * enemy.getChaseVelocity(), (float) Math.sin(angle) * enemy.getChaseVelocity());
    }
}
