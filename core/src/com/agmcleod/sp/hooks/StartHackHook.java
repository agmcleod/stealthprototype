package com.agmcleod.sp.hooks;

import com.agmcleod.sp.GameScreen;
import com.agmcleod.sp.Hook;

/**
 * Created by aaronmcleod on 15-07-05.
 */
public class StartHackHook extends Hook {
    private int id;
    public StartHackHook(GameScreen gs, int id) {
        super(gs);
        this.id = id;
    }

    @Override
    public void exec() {
        gs.enableHack(id);
    }

    @Override
    public void undo() {
        gs.enableHack(0);
    }
}
