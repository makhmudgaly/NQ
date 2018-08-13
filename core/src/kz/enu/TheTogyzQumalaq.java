package kz.enu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;


import java.util.Scanner;

import kz.enu.states.GameStateManager;
import kz.enu.states.MenuState;
import kz.enu.states.PlayState;

public class TheTogyzQumalaq extends ApplicationAdapter {


    public static final int WIDTH = 900;
    public static final int HEIGHT = 540;
    public static final String TITLE = "Nine";
    private static final String CHAR_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАӘБВГҒДЕЁЖЗИЙКҚЛМНҢОӨПРСТУҰҮФХҺЦЧШЩЪЫІЬЭЮЯаәбвгғдеёжзийкқлмнңоөпрстуұүфхһцчшщъыіьэюя.-?!_[](){}:;, %$+-*=0123456789■◄►|";
    public static String POSTFIX;
    public static int LANGUAGE;
    private static BitmapFont bitmapFont;
    private static BitmapFont bitmapFontLil;
    private static final String FILE_PATH = "segoeui.ttf";
    private static final String FILE_PATH2 = "arial.ttf";
    private static GameStateManager gsm;
    private static SpriteBatch batch;
    public static String[] WORDS;
    public static int botLevel = 0;
    private static int createConnect = ResID.CREATE;

    public static int getCreateConnect() {
        return createConnect;
    }

    public static void setCreateConnect(int createConnect) {
        TheTogyzQumalaq.createConnect = createConnect;
    }


    private static Music music;
    private static Sound button;

    public static boolean sound;
    private static float volume;

    public static Music getMusic() {
        return music;
    }

    public static Sound getButtonSound() {
        return button;
    }

    public static BitmapFont getBitmapFont() {
        return bitmapFont;
    }

    public static BitmapFont getBitmapFontLil() {
        return bitmapFontLil;
    }

    @Override
    public void create() {
        setParams();
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(bitmapFontLil, "►");


    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
        music.dispose();
        button.dispose();
        bitmapFont.dispose();
        bitmapFontLil.dispose();
    }

    public static FileHandle fileHandle;

    public static void loadProfile() {
        fileHandle = Gdx.files.local("profile.txt");
        try {
            Scanner in = new Scanner(fileHandle.file());
            POSTFIX = in.nextLine();
            sound = in.nextBoolean();
            LANGUAGE = in.nextInt();
            WORDS = ResID.WORDS[LANGUAGE];
            botLevel = in.nextInt();
            volume = in.nextInt() / 32f;
            in.close();
        } catch (Exception ex) {
            POSTFIX = "taha";
            sound = true;
            WORDS = ResID.WORDS[0];
            botLevel = 0;
            volume = 0.1f;
        }
    }

    public static void setParams() {
        batch = new SpriteBatch();
        gsm = new GameStateManager();
        bitmapFont = new BitmapFont();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FILE_PATH));
        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal(FILE_PATH2));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = CHAR_STRING;
        parameter2.characters = CHAR_STRING;
        parameter.size = 50;
        parameter2.size = 50;
        loadProfile();
        if (getIndexOfTheme() != 8 && getIndexOfTheme() != 9) {
            parameter.borderWidth = 1.5f;
            parameter2.borderWidth = 1.5f;
        }
        parameter.borderColor = new Color(0f, 0f, 0f, 1f);
        parameter2.borderColor = new Color(0f, 0f, 0f, 1f);
        bitmapFont = generator.generateFont(parameter);
        parameter.size = 20;
        bitmapFontLil = generator2.generateFont(parameter2);

        generator.dispose();
        generator2.dispose();

        bitmapFont.setColor(ResID.COLORS[getIndexOfTheme()]);
        bitmapFontLil.setColor(ResID.COLORS[getIndexOfTheme()]);
        System.out.println("MAIN!" + POSTFIX);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        music = Gdx.audio.newMusic(Gdx.files.internal(ResID.MUSIC + POSTFIX + ".mp3"));
        button = Gdx.audio.newSound(Gdx.files.internal(ResID.BUTTON));
        music.setVolume(volume);
        music.setLooping(true);
        if (sound) music.play();
        gsm.push(new MenuState(gsm, POSTFIX));
    }

    public void reboot() {
        this.dispose();
        this.create();
    }

    public static int getIndexOfTheme() {
        for (int i = 0; i < ResID.skinsList.length; i++) {
            if (POSTFIX.equals(ResID.skinsList[i])) return i;
        }
        return -1;
    }

    public static int getIndexOfLanguage() {
        return LANGUAGE;
    }


}
