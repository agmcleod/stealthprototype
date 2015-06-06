package com.agmcleod.sp.aibehaviours;

import com.agmcleod.sp.Enemy;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Aaron on 6/5/2015.
 */
public class ChaseBehaviour {
    private Enemy enemy;
    private Vector2 target;

    public ChaseBehaviour(Enemy enemy) {
        this.enemy = enemy;
        target = new Vector2();
    }


}
