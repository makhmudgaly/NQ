package kz.enu.states.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public final class ThemePreview {
    public Texture texture;
    public Rectangle rectangle;
    public float x;
    public float y;
    public ThemePreview(float x, float y, Texture texture){
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.rectangle = new Rectangle(x,y,texture.getWidth(),texture.getHeight());
    }

    public void glow(SpriteBatch sb,Texture glowTexture){
        sb.draw(glowTexture,this.x-7,this.y-7);
    }
}