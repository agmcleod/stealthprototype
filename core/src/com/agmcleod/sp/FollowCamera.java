package com.agmcleod.sp;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by aaronmcleod on 15-03-05.
 */
public class FollowCamera {
    private Camera camera;
    private Vector2 target;
    private Rectangle deadzone;
    private Rectangle totalViewBounds;

    public FollowCamera(Camera camera, Vector2 target, Rectangle totalViewBounds) {
        deadzone = new Rectangle(0, 0, 0, 0);
        this.target = target;
        this.camera = camera;
        this.totalViewBounds = totalViewBounds;
        setDeadzone(this.camera.viewportWidth / 6, this.camera.viewportHeight / 6);
    }

    public void followH(Vector2 target) {
        Camera cam = this.camera;
        if ((target.x - cam.position.x - camera.viewportWidth / 2) > (deadzone.x + deadzone.width)) {
            cam.position.x = (float) Math.floor(Math.min(target.x - (deadzone.x + deadzone.width), totalViewBounds.width - camera.viewportWidth));
        }
        else if ((target.x - cam.position.x - camera.viewportWidth / 2) < (deadzone.x)) {
            cam.position.x = (float) Math.floor(Math.max(target.x - deadzone.x, totalViewBounds.x + camera.viewportWidth / 2));
        }
    }

    public void followV(Vector2 target) {
        Camera cam = this.camera;
        if ((target.y - cam.position.y - cam.viewportHeight / 2) > (deadzone.y + deadzone.height)) {
            cam.position.y = (float) Math.floor(Math.min(target.y - (deadzone.y + deadzone.height), totalViewBounds.height - camera.viewportHeight));
        }
        else if ((target.y - cam.position.y - cam.viewportHeight / 2) < (deadzone.y)) {
            cam.position.y = (float) Math.floor(Math.max(target.y - this.deadzone.y, totalViewBounds.y + camera.viewportHeight / 2));
        }
    }

    public void setDeadzone(float w, float h) {
        this.deadzone.setPosition(
                (float) Math.floor((this.camera.viewportWidth - w) / 2),
                (float) Math.floor((this.camera.viewportHeight - h) / 2 - h * 0.25)
        );
        this.deadzone.setSize(w, h);

        updateTarget();
    }

    public void update() {
        updateTarget();
    }

    public void updateTarget() {
        followH(this.target);
        followV(this.target);
    }
}
