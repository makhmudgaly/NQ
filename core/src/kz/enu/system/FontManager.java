package kz.enu.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
/**
 * Class for working with fonts
 * Created by Meirkhan on 01.09.2018.
 */

public class FontManager {
    private static final String CHAR_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАӘБВГҒДЕЁЖЗИЙКҚЛМНҢОӨПРСТУҰҮФХҺЦЧШЩЪЫІЬЭЮЯаәбвгғдеёжзийкқлмнңоөпрстуұүфхһцчшщъыіьэюя.-?!_[](){}:;, %$+-*=0123456789■◄►|";

    static public BitmapFont getFont(String sFilePath, int iFontSize, Color oFontColor, float fBorderWidth, Color oBorderColor, boolean isFlipped) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(sFilePath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = CHAR_STRING;
        parameter.size = iFontSize;
        parameter.borderWidth = fBorderWidth;
        parameter.borderColor = oBorderColor;
        parameter.flip = isFlipped;
        BitmapFont bitmapFont = generator.generateFont(parameter);
        bitmapFont.setColor(oFontColor);

        // Garbage cleaning
        generator.dispose();

        return bitmapFont;
    }
}
