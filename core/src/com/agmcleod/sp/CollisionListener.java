package com.agmcleod.sp;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by aaronmcleod on 15-06-12.
 */
public class CollisionListener implements ContactListener {
    private GameScreen gs;
    public CollisionListener(GameScreen gs) {
        this.gs = gs;
    }
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        handlePlayerEnemyCollision(fixtureA, fixtureB);
        handlePlayerBulletCollision(fixtureA, fixtureB);
        handleTriggerCollision(fixtureA, fixtureB);
        handleUITriggerCollision(fixtureA, fixtureB, true);
    }

    @Override
    public void endContact(Contact contact) {
        handleUITriggerCollision(contact.getFixtureA(), contact.getFixtureB(), false);
    }

    public void handlePlayerBulletCollision(Fixture fixtureA, Fixture fixtureB) {
        Player player = null;
        Bullet bullet = null;
        if (((GameObject) fixtureA.getUserData()).name.equals("player") && ((GameObject) fixtureB.getUserData()).name.equals("bullet")) {
            player = (Player) fixtureA.getUserData();
            bullet = (Bullet) fixtureB.getUserData();
        }
        else if (((GameObject) fixtureA.getUserData()).name.equals("bullet") && ((GameObject) fixtureB.getUserData()).name.equals("player")) {
            player = (Player) fixtureB.getUserData();
            bullet = (Bullet) fixtureA.getUserData();
        }

        if (player != null && bullet != null) {
            bullet.setActive(false);
            gs.restart();
        }
    }

    public void handlePlayerEnemyCollision(Fixture fixtureA, Fixture fixtureB) {
        Player player = null;
        Enemy enemy = null;
        if (((GameObject) fixtureA.getUserData()).name.equals("player") && ((GameObject) fixtureB.getUserData()).name.equals("enemy")) {
            player = (Player) fixtureA.getUserData();
            enemy = (Enemy) fixtureB.getUserData();
        }
        else if (((GameObject) fixtureA.getUserData()).name.equals("enemy") && ((GameObject) fixtureB.getUserData()).name.equals("player")) {
            player = (Player) fixtureB.getUserData();
            enemy = (Enemy) fixtureA.getUserData();
        }

        if (player != null && enemy != null) {
            gs.restart();
        }
    }

    public void handleTriggerCollision(Fixture fixtureA, Fixture fixtureB) {
        String aName = ((GameObject) fixtureA.getUserData()).name;
        String bName = ((GameObject) fixtureB.getUserData()).name;

        if (aName.equals("trigger") && bName.equals("player")) {
            ((Trigger) fixtureA.getUserData()).exec();
        }
        else if (bName.equals("trigger") && aName.equals("player")) {
            ((Trigger) fixtureB.getUserData()).exec();
        }
    }

    public void handleUITriggerCollision(Fixture fixtureA, Fixture fixtureB, boolean enable) {
        String aName = ((GameObject) fixtureA.getUserData()).name;
        String bName = ((GameObject) fixtureB.getUserData()).name;

        HackableComponent comp = null;

        if (aName.equals("hackablecomponent") && bName.equals("player")) {
            comp = (HackableComponent) fixtureA.getUserData();
        }
        else if (bName.equals("hackablecomponent") && aName.equals("player")) {
            comp = (HackableComponent) fixtureB.getUserData();
        }

        if (comp != null) {
            if (enable) {
                comp.enable();
            }
            else {
                comp.disable();
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
