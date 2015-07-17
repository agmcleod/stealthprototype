package com.agmcleod.sp;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Aaron on 7/15/2015.
 */
public class CrackTool {

    class HighlightRect {
        private boolean enabled;
        private int number;
        private Rectangle rect;
        public HighlightRect(float x, float y, float w, float h, int number) {
            rect = new Rectangle(x, y, w, h);
            enabled = false;
            this.number = number;

        }

        public Rectangle getRect() {
            return rect;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private GameScreen gs;
    private HighlightRect[] highlightAreas;
    private TextureRegion highlightTexture;
    private Vector2 position;
    private Texture texture;
    public CrackTool(GameScreen gs) {
        this.gs = gs;
        texture = new Texture("cracktool.png");
        position = new Vector2(0, 0);

        float w = 103;
        float h = 102;
        highlightAreas = new HighlightRect[] {
                new HighlightRect(64, 600, w, h, 1), new HighlightRect(202, 600, w, h, 2), new HighlightRect(336, 600, w, h, 3),
                new HighlightRect(64, 464, w, h, 4), new HighlightRect(202, 464, w, h, 5), new HighlightRect(336, 464, w, h, 6),
                new HighlightRect(64, 330, w, h, 7), new HighlightRect(202, 330, w, h, 8), new HighlightRect(336, 330, w, h, 9)
        };
        highlightTexture = gs.getGame().getAtlas().findRegion("highlight");
    }

    public void dispose() {
        texture.dispose();
    }

    public void highlightKey(float x, float y) {
        Camera camera = gs.getCamera();
        x = x - camera.position.x + camera.viewportWidth / 2;
        y = y - camera.position.y + camera.viewportHeight / 2;
        for (HighlightRect rect : highlightAreas) {
            if (rect.getRect().contains(x, y)) {
                rect.setEnabled(true);
            }
            else {
                rect.setEnabled(false);
            }
        }
    }

    public void render(SpriteBatch batch) {
        Camera camera = gs.getCamera();
        float xOffset = camera.position.x - camera.viewportWidth / 2;
        float yOffset = camera.position.y - camera.viewportHeight / 2;
        float x = xOffset + position.x;
        float y = yOffset + position.y;
        batch.draw(texture, x, y);

        for (HighlightRect rect : highlightAreas) {
            if (rect.isEnabled()) {
                Rectangle bounds = rect.getRect();
                batch.draw(highlightTexture, bounds.x + xOffset, bounds.y + yOffset);
            }
        }
    }
}
