package com.agmcleod.sp.aibehaviours;

import com.agmcleod.sp.Enemy;

/**
 * Created by Aaron on 6/6/2015.
 */
public class Behaviour {
    protected Enemy enemy;

    public Behaviour(Enemy enemy) {
        this.enemy = enemy;
    }

    public void update() {}
}
