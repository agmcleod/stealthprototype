package com.agmcleod.sp;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by aaronmcleod on 15-03-05.
 */
public class FollowCamera {
    private Camera camera;
    private Rectangle target;
    private Rectangle deadzone;
    private Rectangle totalViewBounds;

    public FollowCamera(Camera camera, Rectangle bounds, Rectangle totalViewBounds) {
        deadzone = new Rectangle(0, 0, 0, 0);
        this.target = bounds;
        this.camera = camera;
        this.totalViewBounds = totalViewBounds;
    }

    public void followH() {
        Camera cam = this.camera;
        if (totalViewBounds.getWidth() > camera.viewportWidth) {
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
    }

    public void followV() {
        Camera cam = this.camera;
        if (totalViewBounds.getHeight() > camera.viewportHeight) {
            if (target.y - camera.viewportHeight / 2 < totalViewBounds.y) {
                cam.position.y = totalViewBounds.y + camera.viewportHeight / 2;
            } else if (target.y + camera.viewportHeight / 2 > totalViewBounds.getHeight() + totalViewBounds.y) {
                cam.position.y = (totalViewBounds.getHeight() + totalViewBounds.y) - camera.viewportHeight / 2;
            } else {
                cam.position.y = target.y;
            }
        }
    }

    public void update() {
        updateTarget();
    }

    public void updateTarget() {
        followH();
        followV();
    }
}
