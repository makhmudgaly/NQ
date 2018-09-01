package kz.enu.system;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import kz.enu.TheTogyzQumalaq;
/**
 * System util tools
 * Created by Meirkhan on 02.09.2018.
 */

final public class Util {
    static public Texture getTexture(String sFileName) {
        String sExtension = "png";
        return new Texture(sFileName + TheTogyzQumalaq.POSTFIX + "." + sExtension);
    }

    static public Texture getTexture(String sFileName, String sExtension) {
        return new Texture(sFileName + TheTogyzQumalaq.POSTFIX + "." + sExtension);
    }

    static public Sound getSound(String sSoundName) {
        return Gdx.audio.newSound(Gdx.files.internal(sSoundName));
    }
}
