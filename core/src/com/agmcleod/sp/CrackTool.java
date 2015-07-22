package com.agmcleod.sp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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

        public int getNumber() {
            return number;
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

    private final int CODE_LENGTH = 8;
    private final float CODE_TIMEOUT = 1.5f;

    private float decoding;
    private BitmapFont crackFont;
    private GameScreen gs;
    private HighlightRect[] highlightAreas;
    private TextureRegion highlightTexture;
    private Array<Integer> passcode;
    private Vector2 position;
    private float scanNumberCountdown;
    private int[] scanNumber;
    private Array<Integer> selectedNumbers;
    private boolean showCode;
    private HackableComponent target;
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
        selectedNumbers = new Array<Integer>();
        passcode = new Array<Integer>();
        crackFont = new BitmapFont(Gdx.files.internal("cracktoolfont.fnt"), Gdx.files.internal("cracktoolfont.png"), false);
        scanNumber = new int[CODE_LENGTH];
    }

    public void clickActiveKey() {
        int number = 0;
        for (HighlightRect rect : highlightAreas) {
            if (rect.isEnabled()) {
                number = rect.getNumber();
            }
        }
        if (number > 0) {
            selectedNumbers.add(number);
            if (selectedNumbers.size >= CODE_LENGTH) {
                boolean numberIsEqual = true;
                for (int i = 0; i < CODE_LENGTH; i++) {
                    numberIsEqual &= selectedNumbers.get(i) == passcode.get(i);
                }

                if (numberIsEqual) {
                    target.removeFromGame();
                    setTarget(null);
                    gs.getPlayer().setShowCrackTool(false);
                }
                else {
                    // alarm or something
                }
            }
        }
    }

    public void dispose() {
        texture.dispose();
        crackFont.dispose();
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

        if (showCode) {
            for (int i = 0; i < passcode.size; i++) {
                crackFont.draw(batch, "" + passcode.get(i), x + 673 + (30 * i), y + 330);
            }

            for (int i = 0; i < selectedNumbers.size; i++) {
                crackFont.draw(batch, "" + selectedNumbers.get(i), x + 673 + (30 * i), y + 600);
            }
        }
        else {
            for (int i = 0; i < scanNumber.length; i++) {
                crackFont.draw(batch, "" + scanNumber[i], x + 673 + (30 * i), y + 330);
            }
        }
    }

    public void setTarget(HackableComponent component) {
        target = component;
    }

    public void setup() {
        selectedNumbers.clear();
        passcode.clear();
        for (int i = 0; i < CODE_LENGTH; i++) {
            passcode.add(MathUtils.random(1, 9));
        }
        decoding = CODE_TIMEOUT;
        showCode = false;
        scanNumberCountdown = 0.1f;
    }

    public void update() {
        if (!showCode) {
            scanNumberCountdown -= Gdx.graphics.getDeltaTime();
            if (scanNumberCountdown <= 0) {
                scanNumberCountdown = 0.1f;
                for (int i = 0; i < scanNumber.length; i++) {
                    scanNumber[i] = MathUtils.random(1, 9);
                }
            }
            decoding -= Gdx.graphics.getDeltaTime();
            if (decoding <= 0) {
                showCode = true;
            }
        }
    }
}
