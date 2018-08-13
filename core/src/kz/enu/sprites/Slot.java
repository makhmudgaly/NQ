package kz.enu.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import kz.enu.states.PlayState;

/**
 * Created by SLUX on 29.05.2017.
 */

public class Slot {

    public Texture texture;
    public Rectangle rectangle;
    public float x, y;
    public int currentStonesNumber;
    public int oldStonesNumber;
    public int slotNumber;
    public boolean isTuzdyk = false;
    public boolean side;
    public Texture stoneTexture;
    public Sprite[] stoneSprites;
    private static final float DELAY = 1f;
    public float fadeInAlpha = 0;
    public float fadeOutAlpha = 1;
    private static float currentTime;
    private static boolean animationHasEnded;

    public Slot(int slotNumber, float x, float y, Texture texture, Texture stone) {
        currentStonesNumber = 9;
        oldStonesNumber = 9;
        side = (slotNumber <= 8) ? true : false;
        this.x = x;
        this.y = y;
        this.slotNumber = slotNumber;
        this.texture = texture;
        this.stoneTexture = stone;
        animationHasEnded = true;
        rectangle = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        setStoneSprites();
    }

    public Slot(Slot slot) {
        currentStonesNumber = slot.currentStonesNumber;
        oldStonesNumber = slot.oldStonesNumber;
        side = slot.side;
        this.x = slot.x;
        this.y = slot.y;
        this.slotNumber = slot.slotNumber;
        this.texture = slot.texture;
        this.stoneTexture = slot.stoneTexture;
        animationHasEnded = animationHasEnded;
        rectangle = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        setStoneSprites();
    }

    public void resetStonesAlpha() {
        for (int i = 0; i < stoneSprites.length; i++) {
            stoneSprites[i].setAlpha(0f);
        }
        for (int i = 0; i < currentStonesNumber && i < 25; i++) {
            stoneSprites[i].setAlpha(1f);
        }
    }


    public void setStoneSprites() {
        currentTime = 0;
        stoneSprites = new Sprite[25];
        for (int i = 0; i < stoneSprites.length; i++) {
            stoneSprites[i] = new Sprite(stoneTexture);
            stoneSprites[i].setAlpha(0f);
        }
        for (int i = 0; i < currentStonesNumber && i < 25; i++) {
            stoneSprites[i].setAlpha(1f);
        }
        int temp = 25;
        int z = 0;
        float slotHeight = texture.getHeight();


        //First Layer
        float stoneX, stoneY = !side ? (this.y + 106f) : this.y + 2f;
        for (int i = 0; i < 10 && i < temp; i++) {
            stoneX = this.x + 14f + (i % 2) * 28f;
            if (i % 2 == 0 && i != 0) {
                if (!side) stoneY -= 25f;
                else stoneY += 25f;
            }


            stoneSprites[z].setPosition(stoneX, stoneY);
            z++;
        }
        //Second Layer Center
        temp -= 10;
        stoneX = this.x + 28f;
        stoneY = !side ? this.y + 126f : this.y - 20f;
        for (int i = 0; i < 5 && i < temp; i++) {
            if (!side) stoneY -= 25f;
            else stoneY += 25f;

            stoneSprites[z].setPosition(stoneX, stoneY);
            z++;
        }
        //Second Layer Sides
        temp -= 5;
        stoneY = !side ? this.y + 109f : this.y + 2f;
        for (int i = 0; i < 10 && i < temp; i++) {
            stoneX = this.x + 1f + (i % 2) * 53f;
            if (i % 2 == 0 && i != 0) {
                if (!side) stoneY -= 25f;
                else stoneY += 25f;
            }

            stoneSprites[z].setPosition(stoneX, stoneY);
            z++;
        }
    }


    public boolean isToched(OrthographicCamera camera) {
        boolean b = false;
        if (Gdx.input.isTouched()) {
            Vector3 tmp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(tmp);
            if (rectangle.contains(tmp.x, tmp.y)) {
                b = true;
            }
        }
        return b;
    }

    public void drawStones(SpriteBatch sb) {

        for (int i = 0; i < 25; i++) {
            stoneSprites[i].draw(sb);
        }
    }


    public void drawCurrentNumber(SpriteBatch sb, BitmapFont bitmapFont, BitmapFont bitmapFont1) {
        if (this.side) {
            bitmapFont.draw(sb, currentStonesNumber + "", this.x + 38f, this.y + this.texture.getHeight() + 18f);
            bitmapFont1.draw(sb, (slotNumber + 1) + "", this.x + this.texture.getWidth() / 2 - 3f, this.y - 5f);
        } else {
            bitmapFont.draw(sb, currentStonesNumber + "", this.x + 46f, this.y - 16f);
            bitmapFont1.draw(sb, (slotNumber - 8) + "", this.x + this.texture.getWidth() / 2 + 5f, this.y + this.texture.getHeight() + 7f);
        }
    }

    public void glow(SpriteBatch sb, Texture glowTexture) {
        sb.draw(glowTexture, this.x - 7, this.y - 7);
    }

    public void glow(SpriteBatch sb, TextureRegion glowTexture) {
        sb.draw(glowTexture, this.x - 7, this.y - 7);
    }

    public void dispose() {
        this.texture.dispose();
    }

    public void fadeStoneAnimation(float dt) {

        if (currentStonesNumber > oldStonesNumber) {
            fadeInAnimation(dt);
        } else if (currentStonesNumber < oldStonesNumber) {
            fadeOutAnimation(dt);
        }

    }

    private void fadeInAnimation(float dt) {
        fadeInAlpha += dt;

        for (int i = oldStonesNumber; i < currentStonesNumber && i < 25; i++) {
            stoneSprites[i].setAlpha(fadeInAlpha);
        }

    }

    private void fadeOutAnimation(float dt) {
        fadeOutAlpha -= dt;
        if (currentStonesNumber != 0)
            for (int i = oldStonesNumber - 1; i > currentStonesNumber - 1 && i < 25; i--) {
                stoneSprites[i].setAlpha(fadeOutAlpha);

            }
        else {
            for (int i = 0; i < stoneSprites.length; i++) {
                stoneSprites[i].setAlpha(0f);
            }
        }

    }
}
