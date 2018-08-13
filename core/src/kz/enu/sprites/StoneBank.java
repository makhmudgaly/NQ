package kz.enu.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import kz.enu.TheTogyzQumalaq;

/**
 * Created by SLUX on 29.05.2017.
 */

public class StoneBank {
    public int currentStonesNumber;
    public int oldStonesNumber;
    public float fadeInAlpha = 1;
    public Texture texture;
    public Rectangle rectangle;
    public Sprite[] stoneSprites;
    public float x, y;

    public StoneBank(float x, float y, Texture texture, Texture stone) {
        currentStonesNumber = oldStonesNumber = 0;
        this.texture = texture;
        this.rectangle = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        this.x = x;
        this.y = y;
        setStoneSprites(stone);
    }

    public void resetStonesAlpha() {
        for (int i = 0; i < stoneSprites.length; i++) {
            stoneSprites[i].setAlpha(0f);
        }
        for (int i = 0; i < currentStonesNumber && i < 88; i++) {
            stoneSprites[i].setAlpha(1f);
        }
    }

    public void setStoneSprites(Texture stoneTexture) {
        int z = 0;
        stoneSprites = new Sprite[88];
        for (int i = 0; i < stoneSprites.length; i++) {
            stoneSprites[i] = new Sprite(stoneTexture);
            stoneSprites[i].setAlpha(0f);
        }
        for (int i = 0; i < currentStonesNumber && i < 25; i++) {
            stoneSprites[i].setAlpha(1f);
        }
        int temp = 88;
        //Fistr Layer
        float stoneX = this.x + 14f, stoneY;
        for (int i = 0; i < 60 && i < temp; i++) {
            stoneY = this.y + 14f + (i % 2) * 28f;
            if (i % 2 == 0 && i != 0) {
                stoneX += 25f;
            }
            stoneSprites[z].setPosition(stoneX, stoneY);
            z++;
        }
        //Second Layer Center
        temp -= 60;
        stoneX = this.x + 26f;
        stoneY = this.y + 28f;
        for (int i = 0; i < 29 && i < temp; i++) {
            stoneSprites[z].setPosition(stoneX, stoneY);
            z++;
            stoneX += 25f;
        }
    }

    public void drawStones(SpriteBatch sb) {

        for (int i = 0; i < 88; i++) {
            stoneSprites[i].draw(sb);
        }
    }

    public void drawCurrentNumber(SpriteBatch sb, BitmapFont bitmapFont, int side) {
        if (side == 0) {
            bitmapFont.draw(sb, currentStonesNumber + "", this.x + this.texture.getWidth() + 15f, TheTogyzQumalaq.HEIGHT / 2);
            bitmapFont.draw(sb, ":", this.x + this.texture.getWidth() + 35f, TheTogyzQumalaq.HEIGHT / 2);
        } else {
            bitmapFont.draw(sb, currentStonesNumber + "", this.x + this.texture.getWidth() + 43f, TheTogyzQumalaq.HEIGHT / 2);
        }
    }

    public void fadeStoneAnimation(float dt) {

        if (currentStonesNumber > oldStonesNumber) {
            fadeInAnimation(dt);
        }
    }

    private void fadeInAnimation(float dt) {
        fadeInAlpha += dt;

        for (int i = oldStonesNumber; i < currentStonesNumber && i < 88; i++) {
            stoneSprites[i].setAlpha(fadeInAlpha);
        }

    }

    public void dispose() {
        this.texture.dispose();
    }
}
