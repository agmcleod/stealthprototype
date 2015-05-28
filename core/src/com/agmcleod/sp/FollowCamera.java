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
        if (target.x - camera.viewportWidth / 2 < totalViewBounds.x) {
            cam.position.x = totalViewBounds.x + camera.viewportWidth / 2;
        }
        else if (target.x + camera.viewportWidth / 2 > totalViewBounds.getWidth() + totalViewBounds.x) {
            cam.position.x = (totalViewBounds.getWidth() + totalViewBounds.x) - camera.viewportWidth / 2;
        }
        else {
            cam.position.x = target.x;
        }
    }

    public void followV(Vector2 target) {
        Camera cam = this.camera;
        if (target.y - camera.viewportHeight / 2 < totalViewBounds.y) {
            cam.position.y = totalViewBounds.y + camera.viewportHeight / 2;
        }
        else if (target.y + camera.viewportHeight / 2 > totalViewBounds.getHeight() + totalViewBounds.y) {
            cam.position.y = (totalViewBounds.getHeight() + totalViewBounds.y) - camera.viewportHeight / 2;
        }
        else {
            cam.position.y = target.y;
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
