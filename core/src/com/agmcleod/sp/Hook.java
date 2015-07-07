package com.agmcleod.sp;

/**
 * Created by aaronmcleod on 15-07-05.
 */
public abstract class Hook {
    protected GameScreen gs;
    public Hook(GameScreen gs) {
        this.gs = gs;
    }


    public abstract void exec();
    public abstract void undo();
}
