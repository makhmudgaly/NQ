package kz.enu.states.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import kz.enu.TheTogyzQumalaq;
import kz.enu.ai.AI;
import kz.enu.system.Registry;
import kz.enu.sprites.Board;
import kz.enu.sprites.Slot;
import kz.enu.sprites.StoneBank;
import kz.enu.states.view.GameOver;
import kz.enu.states.model.GameStateManager;
import kz.enu.states.model.State;
import kz.enu.states.view.MenuState;
import kz.enu.system.FontManager;
import kz.enu.system.Util;

import static kz.enu.TheTogyzQumalaq.bPlaySound;


/**
 * Main play state
 * Created by SLUX on 17.05.2017.
 */

public class PlayState extends State implements InputProcessor, Input.TextInputListener {

    private boolean flag;
    private static String opponentID = "";
    private static String myID = "";

    private static final String FONT_PATH = "arial.ttf";
    private static final int FONT_SIZE = 15;

    private static Socket socket;
    private final static float UPDATE_TIME = 1 / 30f;
    private float timer = 0;

    private static int GAME_MODE;
    private static boolean bAnimationStarted;
    private static boolean moveHasFinished;
    private static boolean amIFirst;
    private static int frameCounter = 0;
    private final static int slowness = 20;

    private final static float SPASING = 87f;
    private final static float Y_OFFSET = 5f;

    private static Slot[] slots = new Slot[18];
    private static StoneBank[] stoneBanks = new StoneBank[2];

    private static String sSaveFile;
    private static FileHandle fileHandle;

    private static boolean turn;
    private static boolean isThinking;

    private static ArrayList<Board> shots = new ArrayList<Board>();

    private static Sound effective;
    private Sound empty;
    private Sound tuzdykSound;
    private Sound jackpot;
    private static Sound error;

    private Texture bg;
    private Texture slotTexture;
    private float alpha;

    private static String sThinkingBar = "";
    private static float fThinkingDuration = 0;

    private static Texture tuzdykTexture;
    private static Texture glowTexture;
    private static Texture turnUpTexture;
    private static Texture turnDownTexture;
    private static Texture stoneTexture;

    private Texture stoneBankTexture;
    private Texture undoTexture;
    private Texture homeTexture;
    private Texture soundTexture;
    private Texture soundOnTexture;
    private Texture soundOffTexture;

    private static Sprite blackSprite;

    private Rectangle undoRectangle;
    private Rectangle homeRectangle;
    private Rectangle soundRectangle;

    public static int move = 0;
    public static boolean IS_NEW_GAME = false;
    public static int prevMove = 0;

    private BitmapFont oMainFont;
    private BitmapFont oMainFontFlipped;
    private BitmapFont oIceCreamFont;
    private BitmapFont oIceCreamFontFlipped;

    private boolean isMoveTuzdykMaker = false;
    private boolean isAIMoveTuzdykMaker = false;
    private boolean bAIHasTuzdyk = false;
    private boolean bAIMadeMove = false;
    private boolean bPlayerMadeMove = false;

    private boolean isEffective = false;

    private static final float DELAY = 1.2f;
    private static float currentTime;
    private static float deltaTime;

    private boolean sinteticMove;
    private static float wWaiting, wYourId;

    public PlayState(GameStateManager gsm, int mode, boolean isNewGame) {
        super(gsm);

        PlayState.GAME_MODE = mode;
        PlayState.IS_NEW_GAME = isNewGame;

        initVariables();
        initResource();
        initSlots(slotTexture);
        initStoneBanks(stoneBankTexture);

        if (IS_NEW_GAME)
            try {
                sartFile();
            } catch (FileNotFoundException fnfe) {}
        else {
            turn = true;
        }
        amIFirst = true;
        flag = true;
        if (GAME_MODE == Registry.INTERNET) {
            connectSocket();
            configSocketEvents();
        }

        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        frameCounter = 0;

        moveHasFinished = false;
        shots.clear();

        if (opponentID.equals("") && TheTogyzQumalaq.getCreateConnect() == Registry.CONNECT && GAME_MODE == Registry.INTERNET) {
            Gdx.input.getTextInput(this, TheTogyzQumalaq.LOCALE[23], "", "");
        }
    }

    public static void setSlots(int[] slotCounter) {
        for (int i = 0; i < slots.length; i++) {
            slots[i].currentStonesNumber = slotCounter[i];
        }
    }

    public static void setStoneBanks(int[] stoneBankCounter) {
        for (int i = 0; i < stoneBanks.length; i++) {
            stoneBanks[i].currentStonesNumber = stoneBankCounter[i];
        }
    }

    public static void setTurn(boolean tur) {
        turn = tur;
    }

    public void checkTheVictory() {
        if (stoneBanks[turn ? 0 : 1].currentStonesNumber > 81) {

            System.out.println("U WON with " + stoneBanks[turn ? 0 : 1].currentStonesNumber + " stones");
            try {
                refreshGame();
            } catch (FileNotFoundException fnfe) {
            }
            if (GAME_MODE == Registry.INTERNET) socket.emit("playerMoved", move);
            moveHasFinished = false;
            gsm.set(new GameOver(gsm));

        } else if (stoneBanks[0].currentStonesNumber == 81 && stoneBanks[1].currentStonesNumber == 81) {
            try {
                refreshGame();
            } catch (FileNotFoundException fnfe) {
            }
            if (GAME_MODE == Registry.INTERNET) socket.emit("playerMoved", move);
            moveHasFinished = false;
            gsm.set(new GameOver(gsm));
        }
    }

    public static String getResult() {
        if (isAtsyrau()) {
            if (!turn)
                return (162 - stoneBanks[1].currentStonesNumber) + ":" + stoneBanks[1].currentStonesNumber;
            else
                return (stoneBanks[0].currentStonesNumber) + ":" + (162 - stoneBanks[1].currentStonesNumber);
        } else if (stoneBanks[0].currentStonesNumber == 81 && stoneBanks[1].currentStonesNumber == 81) {
            return stoneBanks[0].currentStonesNumber + ":" + stoneBanks[1].currentStonesNumber + " ";
        } else {
            return stoneBanks[0].currentStonesNumber + ":" + stoneBanks[1].currentStonesNumber + " ";
        }
    }

    public static String getGameOverWords() {
        if (GAME_MODE == Registry.SINGLE_PLAYER) {
            if (isAtsyrau()) {
                if (!turn) return TheTogyzQumalaq.LOCALE[9];
                else return TheTogyzQumalaq.LOCALE[10];
            } else if (stoneBanks[0].currentStonesNumber == 81 && stoneBanks[1].currentStonesNumber == 81) {
                return TheTogyzQumalaq.LOCALE[11];
            } else if (stoneBanks[0].currentStonesNumber > stoneBanks[1].currentStonesNumber) {
                return TheTogyzQumalaq.LOCALE[9];
            } else {
                return TheTogyzQumalaq.LOCALE[10];
            }
        } else if (GAME_MODE == Registry.MULTIPLAYER || GAME_MODE == Registry.INTERNET) {
            if (isAtsyrau()) {
                if (!turn) return TheTogyzQumalaq.LOCALE[12];
                else return TheTogyzQumalaq.LOCALE[13];
            } else if (stoneBanks[0].currentStonesNumber == 81 && stoneBanks[1].currentStonesNumber == 81) {
                return TheTogyzQumalaq.LOCALE[11];
            } else if (stoneBanks[0].currentStonesNumber > stoneBanks[1].currentStonesNumber) {
                return TheTogyzQumalaq.LOCALE[12];
            } else {
                return TheTogyzQumalaq.LOCALE[13];
            }
        } else {
            return "";
        }
    }

    private void initVariables()
    {
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
        sThinkingBar = "";
        fThinkingDuration = 0;
        bAnimationStarted = false;
        isThinking = false;

        if (GAME_MODE == 0) {
            sSaveFile = "saveAI.txt";
        } else if (GAME_MODE == 1) {
            sSaveFile = "save.txt";
        }
    }

    private static void initSlots(Texture texture) {
        float x = 10f;
        float y = 17f + Y_OFFSET;
        int j = 0;
        for (int i = 0; i < slots.length / 2; i++) {
            slots[i] = new Slot(i, x + (i) * SPASING, y, texture, stoneTexture);
        }

        y = 377f;

        for (int i = 9; i < slots.length; i++) {
            slots[i] = new Slot(i, x + (slots.length - i - 1) * SPASING, y, texture, stoneTexture);
        }
    }

    private static void initStoneBanks(Texture texture) {
        float x = 10f;
        float y = 265f + Y_OFFSET;
        for (int i = 1; i >= 0; i--) {
            stoneBanks[i] = new StoneBank(x, y - i * 90f, texture, stoneTexture);
        }
    }

    private void initResource() {
        // Texture initialization
        initTextures();

        // Font initialization
        initFonts();

        // Sound refactor
        initSound();

        // fileHandle
        if (GAME_MODE == Registry.SINGLE_PLAYER || GAME_MODE == Registry.MULTIPLAYER)
            fileHandle = Gdx.files.local(sSaveFile);

        // Transition animation
        alpha = 1f;
        blackSprite = new Sprite(new Texture(Registry.BLACK_BG));
        blackSprite.setAlpha(alpha);
        blackSprite.setPosition(0, 0);
    }

    private void initSound() {
        effective = Util.getSound(Registry.EFFECTIVE_MOVE);
        error = Util.getSound(Registry.ERROR);
        empty = Util.getSound(Registry.EMPTY_MOVE + TheTogyzQumalaq.POSTFIX + ".mp3");
        tuzdykSound = Util.getSound(Registry.TUZDYK_SOUND + TheTogyzQumalaq.POSTFIX + ".mp3");
        jackpot = Util.getSound(Registry.JACKPOT);
    }

    private void initFonts() {
        float fBorderWidth = 0;
        Color oBorderColor = new Color(0f, 0f, 0f, 1f);
        if (TheTogyzQumalaq.getIndexOfTheme() != 8 && TheTogyzQumalaq.getIndexOfTheme() != 6 && TheTogyzQumalaq.getIndexOfTheme() != 9) {
            fBorderWidth = 1f;
        }
        // Main fonts
        Color oMainColor = Registry.NUMBER_COLORS[TheTogyzQumalaq.getIndexOfTheme()];
        oMainFont = FontManager.getFont(FONT_PATH, FONT_SIZE, oMainColor, fBorderWidth, oBorderColor, false);
        oMainFontFlipped = FontManager.getFont(FONT_PATH, FONT_SIZE, oMainColor, fBorderWidth, oBorderColor, true);
        oMainFontFlipped.getData().setScale(-1, 1);

        // Ice cream fonts
        Color oIceCreamColor = Registry.INDEX_COLORS[TheTogyzQumalaq.getIndexOfTheme()];
        oIceCreamFont = FontManager.getFont(FONT_PATH, FONT_SIZE, oIceCreamColor,fBorderWidth, oBorderColor, false);
        oIceCreamFontFlipped = FontManager.getFont(FONT_PATH, FONT_SIZE, oIceCreamColor, fBorderWidth, oBorderColor, true);
        oIceCreamFontFlipped.getData().setScale(-1, 1);

        // Text width adjustments
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[21]);
        wWaiting = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[22] + "000000000");
        wYourId = glyphLayout.width;
        glyphLayout.reset();
    }

    private void initTextures() {
        bg = Util.getTexture(Registry.BACKGROUND);
        slotTexture = Util.getTexture(Registry.SLOT);
        stoneBankTexture = Util.getTexture(Registry.STONE_BANK);
        stoneTexture = Util.getTexture(Registry.STONE);
        tuzdykTexture = Util.getTexture(Registry.TUZDYK);
        glowTexture = new Texture(Registry.GLOW);
        turnUpTexture = Util.getTexture(Registry.TURN_UP);
        turnDownTexture = Util.getTexture(Registry.TURN_DOWN);
        undoTexture = Util.getTexture(Registry.UNDO);
        homeTexture = Util.getTexture(Registry.HOME);
        soundOnTexture = Util.getTexture(Registry.SOUND);
        soundOffTexture = Util.getTexture(Registry.SOUND_OFF);
        soundTexture = bPlaySound ? soundOnTexture : soundOffTexture;
        undoRectangle = new Rectangle(812f, 465f, undoTexture.getWidth() + 20, undoTexture.getHeight() + 20);
        homeRectangle = new Rectangle(812f, 41f, homeTexture.getWidth() + 20, homeTexture.getHeight() + 20);
        soundRectangle = new Rectangle(812f, 96f, soundTexture.getWidth() + 20, soundTexture.getHeight() + 20);
    }

    @Override
    protected void handleInput() {
        Vector3 tmp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(tmp);
        if (Gdx.input.justTouched()) {
            if (undoRectangle.contains(tmp.x, tmp.y)) {

                undo();
            } else if (homeRectangle.contains(tmp.x, tmp.y)) {
                if (TheTogyzQumalaq.bPlaySound) TheTogyzQumalaq.getButtonSound().play();
                if (GAME_MODE == Registry.INTERNET) socket.disconnect();
                gsm.set(new MenuState(gsm, TheTogyzQumalaq.POSTFIX));
            } else if (soundRectangle.contains(tmp.x, tmp.y)) {

                if (bPlaySound) {
                    bPlaySound = false;
                    TheTogyzQumalaq.getBackgroundMusic().pause();
                    soundTexture = soundOffTexture;
                } else {
                    bPlaySound = true;
                    TheTogyzQumalaq.getBackgroundMusic().play();
                    soundTexture = soundOnTexture;
                }
            } else if (GAME_MODE == Registry.INTERNET) {
                if (Gdx.input.justTouched()) {
                    for (int i = 0; i < 18; i++) {
                        if (slots[i].isToched(camera)) {
                            sinteticMove = false;
                            move = slots[i].slotNumber;
                            bAnimationStarted = true;
                            if (slots[i].side == turn && slots[i].currentStonesNumber != 0)
                                regShot();
                            logic();
                            printState();
                        }
                    }
                }
            } else if (GAME_MODE == Registry.MULTIPLAYER) {
                if (Gdx.input.justTouched()) {
                    for (int i = 0; i < slots.length; i++) {
                        if (slots[i].isToched(camera)) {
                            move = slots[i].slotNumber;
                            bAnimationStarted = true;
                            if (slots[i].side == turn && slots[i].currentStonesNumber != 0)
                                regShot();
                            logic();
                            printState();
                        }
                    }
                }
            }
        }
        if (GAME_MODE == Registry.SINGLE_PLAYER) {
            if (turn) {
                if (Gdx.input.justTouched()) {
                    for (int i = 0; i < slots.length; i++) {
                        if (slots[i].isToched(camera)) {

                            move = slots[i].slotNumber;
                            bAnimationStarted = true;
                            bPlayerMadeMove = true;
                            if (slots[i].side == turn && slots[i].currentStonesNumber != 0)
                                regShot();
                            logic();
                            printState();
                        }
                    }
                }
            } else {
                currentTime += deltaTime;
                isThinking = true;
                if (currentTime > DELAY) {
                    switch (TheTogyzQumalaq.botLevel) {
                        case AI.EASY_AI:
                            move = AI.makeMoveEasyAI(slots);
                            break;
                        case AI.NORMAL_AI:
                            move = AI.makeMoveNormalAI(slots);
                            break;
                        case AI.HARD_AI:
                            move = AI.makeMoveHardAI(slots);
                            break;
                        case AI.EFFECTIVE_AI:
                            move = AI.makeMoveEffectiveAI(slots);
                            break;
                        default:
                            move = AI.makeMoveNormalAI(slots);
                    }
                    bAIMadeMove = true;
                    bAnimationStarted = true;
                    logic();
                    currentTime = 0;
                    printState();
                    if (!bAIHasTuzdyk) for (int i = 0; i < 9; i++) {
                        if (slots[i].isTuzdyk) {
                            isAIMoveTuzdykMaker = true;
                            bAIHasTuzdyk = true;
                        }
                    }
                }
            }
        }
    }

    public void updateServer(float dt) {
        timer += dt;
        if (timer >= UPDATE_TIME && moveHasFinished && !sinteticMove) {
            socket.emit("playerMoved", move, opponentID);
            moveHasFinished = false;
        }
    }

    @Override
    public void update(float dt) {
        deltaTime = dt;
        if (isThinking) fThinkingDuration += dt;
        if (alpha > 0) {
            alpha -= 0.02f;
            blackSprite.setAlpha(alpha);
        }
        if (sThinkingBar.length() > 2) {
            sThinkingBar = "";
        } else if (fThinkingDuration > 0.3f) {
            sThinkingBar += ".";
            fThinkingDuration = 0;
        }
        if (GAME_MODE == Registry.SINGLE_PLAYER || GAME_MODE == Registry.MULTIPLAYER) try {
            saveGame();
        } catch (FileNotFoundException fnfe) {
        }
        if (((!opponentID.equals("")) && GAME_MODE == Registry.INTERNET) || GAME_MODE == Registry.SINGLE_PLAYER || GAME_MODE == Registry.MULTIPLAYER)
            handleInput();

        if (bAnimationStarted) {
            for (int i = 0; i < slots.length; i++) {
                slots[i].fadeStoneAnimation(1 / (float) slowness);
            }
            for (int i = 0; i < stoneBanks.length; i++) {
                stoneBanks[i].fadeStoneAnimation(1 / (float) slowness);
            }
            frameCounter++;
        }
        if (frameCounter == slowness) {
            bAnimationStarted = false;
            for (int i = 0; i < slots.length; i++) {
                slots[i].fadeInAlpha = 0;
                slots[i].fadeOutAlpha = 1;
                slots[i].oldStonesNumber = slots[i].currentStonesNumber;
                slots[i].resetStonesAlpha();
            }
            for (int i = 0; i < stoneBanks.length; i++) {
                stoneBanks[i].fadeInAlpha = 0;
                stoneBanks[i].oldStonesNumber = stoneBanks[i].currentStonesNumber;
                stoneBanks[i].resetStonesAlpha();
            }
            frameCounter = 0;
        }
        if (GAME_MODE == Registry.INTERNET) updateServer(dt);
        checkTheVictory();
        camera.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        sb.draw(bg, 0, 0);
        if (((!opponentID.equals("")) && GAME_MODE == Registry.INTERNET) || GAME_MODE == Registry.SINGLE_PLAYER || GAME_MODE == Registry.MULTIPLAYER) {
            for (int i = 0; i < slots.length; i++) {
                //slots[i].resetStonesAlpha();
                if (!slots[i].isTuzdyk)
                    sb.draw(slots[i].texture, slots[i].x, slots[i].y);
                else {
                    sb.draw(tuzdykTexture, slots[i].x, slots[i].y);
                }


                slots[i].drawStones(sb);

                if (slots[i].side) {
                    slots[i].drawCurrentNumber(sb, oMainFont, oIceCreamFont);
                } else {
                    slots[i].drawCurrentNumber(sb, oMainFontFlipped, oIceCreamFontFlipped);

                }

            }
            for (int i = 0; i < stoneBanks.length; i++) {
                sb.draw(stoneBanks[i].texture, stoneBanks[i].x, stoneBanks[i].y);
                stoneBanks[i].drawStones(sb);
                stoneBanks[i].drawCurrentNumber(sb, oMainFont, i);
            }
            slots[move].glow(sb, glowTexture);
            sb.draw(undoTexture, 822f, 475f);
            sb.draw(homeTexture, 822f, 42f);
            sb.draw(soundTexture, 822f, 106f);
            if (isThinking) TheTogyzQumalaq.getMainFont().draw(sb, sThinkingBar, 822f, 440f);


            if (turn) {
                sb.draw(turnDownTexture, 5, 5);
            } else {
                sb.draw(turnUpTexture, 5, 265);
            }
        } else {
            if (opponentID.equals("") && TheTogyzQumalaq.getCreateConnect() == Registry.CREATE && GAME_MODE == Registry.INTERNET) {
                TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[21], (TheTogyzQumalaq.WIDTH - wWaiting) / 2, TheTogyzQumalaq.HEIGHT / 2);
                TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[22] + myID, (TheTogyzQumalaq.WIDTH - wYourId) / 2, TheTogyzQumalaq.HEIGHT / 2 + 100f);
            }
        }
        if (alpha > 0) {
            blackSprite.draw(sb);
        }
        sb.end();

    }

    @Override
    public void dispose() {
        bg.dispose();
        slotTexture.dispose();
        stoneBankTexture.dispose();
        tuzdykTexture.dispose();
        stoneTexture.dispose();
        undoTexture.dispose();
        homeTexture.dispose();
        soundTexture.dispose();
        soundOnTexture.dispose();
        soundOffTexture.dispose();
        glowTexture.dispose();
        empty.dispose();
        effective.dispose();
        error.dispose();
        jackpot.dispose();
        tuzdykSound.dispose();
        for (int i = 0; i < slots.length; i++) {
            slots[i].dispose();
        }
        for (int i = 0; i < stoneBanks.length; i++) {
            stoneBanks[i].dispose();
        }
        //tuzdykAnimationTexture.dispose();
        oIceCreamFont.dispose();
        oMainFont.dispose();
        oMainFontFlipped.dispose();
        oIceCreamFontFlipped.dispose();
        turnDownTexture.dispose();
        turnUpTexture.dispose();
    }

    public static void printState() {
        if (turn) System.out.println("Turn of First Player!");
        else System.out.println("Turn of Second Player!");

        System.out.println("[1]:" + stoneBanks[0].currentStonesNumber
                + "=======================CURRENT_STATE======================="
                + "[2]:" + stoneBanks[1].currentStonesNumber);

        for (int i = 0; i < slots.length; i++) {

            if (i <= 8) {
                if (slots[17 - i].isTuzdyk) {
                    System.out.print((slots[17 - i].slotNumber) + ":[" + "T" + "]\t");
                } else {
                    System.out.print((slots[17 - i].slotNumber) + ":[" + slots[17 - i].currentStonesNumber + "]\t");
                }

            } else {
                if (slots[i - 9].isTuzdyk) {
                    System.out.print((slots[i - 9].slotNumber) + ":[" + "T" + "]\t");
                } else {
                    System.out.print((slots[i - 9].slotNumber) + ":[" + slots[i - 9].currentStonesNumber + "]\t");
                }
            }

            if (i == 8 || i == 17) System.out.println("\n\n");

        }

    }

    private static boolean isAtsyrau() {
        boolean b = true;
        for (int i = 0; i < slots.length; i++) {
            if (turn == slots[i].side && slots[i].currentStonesNumber != 0) {
                b = false;
            }
        }
        return b;
    }

    private static int makeMove(int slotIndex) {
        Slot currentSlot = slots[slotIndex];

        int currentSlotStoneNumber = currentSlot.currentStonesNumber;

        int lastSlotIndex;

        if (currentSlot.side == turn && currentSlotStoneNumber != 0) {
            currentSlot.currentStonesNumber = 0;

            if (currentSlotStoneNumber == 1) {
                if (slots[(slotIndex + 1) % 18].isTuzdyk) {
                    stoneBanks[!(slots[(slotIndex + 1) % 18].side) ? 0 : 1].currentStonesNumber++;
                } else {

                    slots[(slotIndex + 1) % 18].currentStonesNumber++;
                }
                lastSlotIndex = (slotIndex + 1) % 18;
            } else {
                lastSlotIndex = (slotIndex + currentSlotStoneNumber - 1) % 18;
                for (int i = 0; i < currentSlotStoneNumber; i++) {
                    if (slots[(slotIndex + i) % 18].isTuzdyk) {
                        stoneBanks[!(slots[(slotIndex + i) % 18].side) ? 0 : 1].currentStonesNumber++;
                    } else {
                        slots[(slotIndex + i) % 18].currentStonesNumber++;
                    }
                }
            }

        } else {
            turn ^= true;
            if (bPlaySound && (GAME_MODE == Registry.SINGLE_PLAYER && !turn || (GAME_MODE == Registry.MULTIPLAYER || GAME_MODE == Registry.INTERNET)))
                error.play();
            return -1;
        }

        return lastSlotIndex;

    }

    private static int getOpposite(int Index) {
        return (Index + 9) % 18;
    }

    private void logic() {

        int lastSlotIndex;
        if (isAtsyrau()) {
            System.out.println("Atsyrau! U Lose");
            gsm.set(new GameOver(gsm));
        }

        lastSlotIndex = makeMove(move);

        if (lastSlotIndex != -1) {
            if (GAME_MODE == Registry.MULTIPLAYER || GAME_MODE == Registry.INTERNET) isMoveTuzdykMaker = false;
            if (GAME_MODE == Registry.SINGLE_PLAYER && bPlayerMadeMove && isMoveTuzdykMaker)
                isMoveTuzdykMaker = false;
            if (GAME_MODE == Registry.SINGLE_PLAYER && bAIMadeMove && isAIMoveTuzdykMaker)
                isAIMoveTuzdykMaker = false;
            if (GAME_MODE == Registry.SINGLE_PLAYER && !turn) {
                isThinking = false;
            }
            if (turn == !slots[lastSlotIndex].side) {

                if (slots[lastSlotIndex].currentStonesNumber % 2 == 0) {

                    if (slots[lastSlotIndex].currentStonesNumber >= 16 && bPlaySound) jackpot.play();

                    stoneBanks[turn ? 0 : 1].currentStonesNumber += slots[lastSlotIndex].currentStonesNumber;

                    slots[lastSlotIndex].currentStonesNumber = 0;

                    if (bPlaySound) {
                        effective.play();
                    }
                    isEffective = true;
                }


                if (slots[lastSlotIndex].currentStonesNumber == 3) {

                    if ((lastSlotIndex != 17 && lastSlotIndex != 8)
                            && !slots[getOpposite(lastSlotIndex)].isTuzdyk && isTuzdykAvailable()) {
                        slots[lastSlotIndex].isTuzdyk = true;
                        slots[lastSlotIndex].texture = tuzdykTexture;
                        stoneBanks[turn ? 0 : 1].currentStonesNumber += slots[lastSlotIndex].currentStonesNumber;
                        slots[lastSlotIndex].currentStonesNumber = 0;
                        if (bPlaySound) tuzdykSound.play();
                        isEffective = true;
                        isMoveTuzdykMaker = true;
                    }
                }
                prevMove = move;
            }
            if (!isEffective)
                if (bPlaySound) {
                    empty.play();
                }
            isEffective = false;
            moveHasFinished = true;
            flag = false;
        }

        if (bAIMadeMove) bAIMadeMove = false;
        if (bPlayerMadeMove) bPlayerMadeMove = false;
        turn ^= true;
    }

    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.BACK) {

            if (GAME_MODE == Registry.INTERNET) socket.disconnect();
            gsm.set(new MenuState(gsm, TheTogyzQumalaq.POSTFIX));

        } else if (keycode == Input.Keys.SPACE) {

        } else if (keycode == Input.Keys.ENTER) {

        } else if (keycode == Input.Keys.Q) {
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            // Optional back button handling (e.g. ask for confirmation)
            Gdx.app.exit();
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    //Artificial
    private static void loadGame() throws FileNotFoundException {
        Scanner in = new Scanner(fileHandle.file());
        int tmpi;
        for (int i = 0; i < slots.length; i++) {
            tmpi = in.nextInt();
            if (tmpi == -1) {
                slots[i].currentStonesNumber = 0;
                slots[i].isTuzdyk = true;
                slots[i].texture = tuzdykTexture;
            } else {
                slots[i].currentStonesNumber = tmpi;
            }
        }
        for (int i = 0; i < stoneBanks.length; i++) {
            stoneBanks[i].currentStonesNumber = in.nextInt();
        }
        String tmp = in.nextLine();

        if (tmp.equals("true")) turn = true;
        else if (tmp.equals("false")) turn = false;
        bAnimationStarted = true;
        in.close();
    }

    private static void saveGame() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(fileHandle.file());
        for (int i = 0; i < slots.length; i++) {
            if (!slots[i].isTuzdyk) pw.println(slots[i].currentStonesNumber);
            else pw.println(-1);
            pw.flush();
        }
        for (int i = 0; i < stoneBanks.length; i++) {
            pw.println(stoneBanks[i].currentStonesNumber);
            pw.flush();
        }
        pw.println(turn);
        pw.close();

    }

    private static void sartFile() throws FileNotFoundException {
        if (fileHandle.file().exists()) {
            loadGame();
        }
    }

    private static void refreshGame() throws FileNotFoundException {
        if (GAME_MODE == Registry.MULTIPLAYER || GAME_MODE == Registry.MULTIPLAYER) {
            PrintWriter pw = new PrintWriter(fileHandle.file());
            for (int i = 0; i < slots.length; i++) {
                pw.println(9);
            }
            for (int i = 0; i < stoneBanks.length; i++) {
                pw.println(0);
            }
            pw.println(0);

            pw.close();
        }
    }

    private static void regShot() {
        shots.clear();
        shots.add(new Board(slots, stoneBanks, turn));
    }

    private void undo() {


        System.out.println("IsTuzdykMaker" + isMoveTuzdykMaker);
        System.out.println("IsAiTuzdykMaker" + isAIMoveTuzdykMaker);
        if (isMoveTuzdykMaker) {
            for (int i = 0; i < slots.length; i++) {
                if (GAME_MODE == Registry.SINGLE_PLAYER) {
                    if (slots[i].side == !turn) {
                        slots[i].texture = slotTexture;
                        slots[i].isTuzdyk = false;

                    }
                } else if (GAME_MODE == Registry.MULTIPLAYER || GAME_MODE == Registry.INTERNET) {
                    if (slots[i].side == turn) {
                        slots[i].texture = slotTexture;
                        slots[i].isTuzdyk = false;
                    }
                }
            }
            isMoveTuzdykMaker = false;
        }
        if (isAIMoveTuzdykMaker) {
            for (int i = 0; i < slots.length; i++) {
                if (slots[i].side == turn) {
                    slots[i].texture = slotTexture;
                    slots[i].isTuzdyk = false;
                    isAIMoveTuzdykMaker = false;
                    bAIHasTuzdyk = false;
                }
            }
        }
        try {
            shots.get(shots.size() - 1).getShot(this);
            shots.remove(shots.size() - 1);
            bAnimationStarted = true;

        } catch (ArrayIndexOutOfBoundsException aiEx) {
            if (bPlaySound) error.play();
        }

    }


    public static boolean isTuzdykAvailable() {
        boolean b = true;
        if (!turn) {
            for (int i = 0; i < 9; i++) {
                if (slots[i].isTuzdyk == true) {
                    b = false;
                }
            }
        } else {
            for (int i = 9; i < 18; i++) {
                if (slots[i].isTuzdyk == true) {
                    b = false;
                }
            }
        }
        return b;
    }

   public static void connectSocket() {
        try {
            socket = IO.socket("https://togyz.herokuapp.com");
            //socket = IO.socket("http://localhost:3000");
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connected");
            }
        }).on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String id = args[0].toString();
                myID = id;
                Gdx.app.log("SocketIO", "My ID: " + id);
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String id = args[0].toString();
                Gdx.app.log("SocketIO", "New Player Connected: " + id);
                if (flag) {
                    amIFirst = false;
                }
            }
        }).on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sinteticMove = true;
                move = (Integer) args[0];
                bAnimationStarted = true;
                logic();
                printState();
            }
        }).on("opponentConnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                opponentID = args[0].toString();

            }
        });
    }

    @Override
    public void input(String text) {
        opponentID = text;
        socket.emit("opponentConnected", myID, opponentID);
    }

    @Override
    public void canceled() {
        Gdx.app.log("X", "Cc");
    }
}
