package kz.enu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


import java.util.Scanner;

import kz.enu.states.GameStateManager;
import kz.enu.states.MenuState;
import kz.enu.system.FontManager;
import kz.enu.system.Util;

public class TheTogyzQumalaq extends ApplicationAdapter {


    public static final int WIDTH = 900;
    public static final int HEIGHT = 540;
    public static final String TITLE = "Nine";
    public static String POSTFIX;
    public static int LANGUAGE;
    private static BitmapFont oMainFont;
    private static BitmapFont oSecondaryFont;
    private static GameStateManager gsm;
    private static SpriteBatch batch;
    public static String[] LOCALE;
    public static int botLevel = 0;
    private static int createConnect = Registry.CREATE;

    public static int getCreateConnect() {
        return createConnect;
    }

    public static void setCreateConnect(int createConnect) {
        TheTogyzQumalaq.createConnect = createConnect;
    }


    private static Music oBackgroundMusic;
    private static Sound oButtonSound;

    public static boolean bPlaySound;
    private static float fMusicVolume;

    public static Music getBackgroundMusic() {
        return oBackgroundMusic;
    }

    public static Sound getButtonSound() {
        return oButtonSound;
    }

    public static BitmapFont getMainFont() {
        return oMainFont;
    }

    public static BitmapFont getSecondaryFont() {
        return oSecondaryFont;
    }

    @Override
    public void create() {
        setParams();
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
        oBackgroundMusic.dispose();
        oButtonSound.dispose();
        oMainFont.dispose();
        oSecondaryFont.dispose();
    }

    public static FileHandle fileHandle;

    public static void loadProfile() {
        fileHandle = Gdx.files.local("profile.txt");
        try {
            Scanner in = new Scanner(fileHandle.file());
            POSTFIX = in.nextLine();
            bPlaySound = in.nextBoolean();
            LANGUAGE = in.nextInt();
            LOCALE = Registry.DICTIONARY[LANGUAGE];
            botLevel = in.nextInt();
            fMusicVolume = in.nextInt() / 32f;
            in.close();
        } catch (Exception ex) {
            POSTFIX = "taha";
            bPlaySound = true;
            LOCALE = Registry.DICTIONARY[0];
            botLevel = 0;
            fMusicVolume = 0.1f;
        }
    }

    public static void setParams() {
        batch = new SpriteBatch();
        gsm = new GameStateManager();

        // Profile settings fetch
        loadProfile();

        // Font settings
        float fBorderWidth = 0;
        if (getIndexOfTheme() != 8 && getIndexOfTheme() != 9) {
            fBorderWidth = 1.5f;
        }
        Color oFontColor = Registry.COLORS[getIndexOfTheme()];
        Color oBorderColor = new Color(0f, 0f, 0f, 1f);
        oMainFont = FontManager.getFont("segoeui.ttf", 50, oFontColor, fBorderWidth, oBorderColor, false);
        oSecondaryFont = FontManager.getFont("arial.ttf", 50, oFontColor, fBorderWidth, oBorderColor, false);

        // Cleaning canvas
        Gdx.gl.glClearColor(1, 0, 0, 1);

        // Music settings
        loadMusic();

        // Scene generation
        gsm.push(new MenuState(gsm, POSTFIX));
    }

    private static void loadMusic() {
        oBackgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(Registry.MUSIC + POSTFIX + ".mp3"));
        oButtonSound = Util.getSound(Registry.BUTTON);
        oBackgroundMusic.setVolume(fMusicVolume);
        oBackgroundMusic.setLooping(true);
        if (bPlaySound) oBackgroundMusic.play();
    }

    public void reboot() {
        this.dispose();
        this.create();
    }

    public static int getIndexOfTheme() {
        for (int i = 0; i < Registry.skinsList.length; i++) {
            if (POSTFIX.equals(Registry.skinsList[i])) return i;
        }
        return -1;
    }

    public static int getIndexOfLanguage() {
        return LANGUAGE;
    }


}
