package kz.enu.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import kz.enu.AI;
import kz.enu.ResID;
import kz.enu.TheTogyzQumalaq;
import kz.enu.sprites.Board;
import kz.enu.sprites.Slot;
import kz.enu.sprites.StoneBank;

import static kz.enu.TheTogyzQumalaq.sound;


/**
 * Created by SLUX on 17.05.2017.
 */

public class PlayState extends State implements InputProcessor, Input.TextInputListener {

    private boolean flag;
    private static final String FILE_PATH = "arial.ttf";
    private static final String CHAR_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАӘБВГҒДЕЁЖЗИЙКҚЛМНҢОӨПРСТУҰҮФХҺЦЧШЩЪЫІЬЭЮЯаәбвгғдеёжзийкқлмнңоөпрстуұүфхһцчшщъыіьэюя.-?!_[](){}:;, %$+-*=0123456789■◄►|";
    private static String opponentID = "";
    private static String myID = "";


    public static Socket getSocket() {
        return socket;
    }

    private static Socket socket;
    private final static float UPDATE_TIME = 1 / 30f;
    float timer = 0;

    public static int getMode() {
        return mode;
    }

    private static int mode;
    private static boolean animationStarted;
    private static boolean moveHasFinished;
    private static boolean amIFirst;
    private static int frameCounter = 0;
    private final static int slowness = 20;

    private final static float SPASING = 87f;
    private final static float Y_OFFSET = 5f;

    private static Slot[] slots = new Slot[18];
    private static StoneBank[] stoneBanks = new StoneBank[2];

    private static String saver;
    private static FileHandle fileHandle;

    private static boolean turn;
    private static boolean think;

    public static int destination;
    public static int source;

    private static ArrayList<Board> shots = new ArrayList<Board>();

    private static Sound effective;
    private Sound empty;
    private Sound tuzdykSound;
    private Sound jackpot;
    private static Sound error;

    //private Animation tuzdykAnimation;

    private Texture bg;
    private Texture slotTexture;
    private float alpha;

    private static String thinkingBar = "";
    private static float thinkCounter = 0;

    private static Texture tuzdykTexture;
    private static Texture glowTexture;
    private static Texture turnUpTexture;
    private static Texture turnDownTexture;
    private static Texture stoneTexture;
    //private static Texture tuzdykAnimationTexture;

    private Texture stoneBankTexture;
    private Texture undoTexture;
    private Texture homeTexture;
    private Texture soundTexture;
    private Texture soundOnTexture;
    private Texture soundOffTexture;

    private static Sprite blackSprite;
    private static Texture blackBackground;

    private Rectangle undoRectangle;
    private Rectangle homeRectangle;
    private Rectangle soundRectangle;

    public static int move = 0;
    public static int newCon = 0;
    public static int prevMove = 0;

    private BitmapFont bitmapFont;
    private BitmapFont bitmapFontIceCream;
    private BitmapFont bitmapFontFlipped;
    private BitmapFont bitmapFontIceCreamFlipped;
    private boolean isMoveTuzdykMaker = false;
    private boolean isAIMoveTuzdykMaker = false;
    private boolean wasNoTuzdykAlready = true;
    private boolean hasAIMakedMove = false;
    private boolean hasIMakedMove = false;

    private boolean isEffective = false;

    private static final float DELAY = 1.2f;
    private static float currentTime;
    private static float deltaTime;

    static Map possibleMoves = new HashMap<Integer, Integer>();
    private boolean sinteticMove;
    private static float wWaiting, wYourId;

    public PlayState(GameStateManager gsm, int mode, int newCon) {
        super(gsm);
        //generateRandomNumber();

        this.mode = mode;
        this.newCon = newCon;
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
        thinkingBar = "";
        thinkCounter = 0;
        animationStarted = false;
        think = false;

        if (mode == 0) {
            saver = "saveAI.txt";
        } else if (mode == 1) {
            saver = "save.txt";
        }

        initResource();
        initSlots(slotTexture);
        initStoneBanks(stoneBankTexture);

        if (newCon == 1)
            try {
                sartFile();
            } catch (FileNotFoundException fnfe) {
            }
        else {
            turn = true;
        }
        amIFirst = true;
        flag = true;
        if (mode == ResID.INTERNET) {
            connectSocket();
            configSocketEvents();
        }

        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        frameCounter = 0;

        moveHasFinished = false;
        shots.clear();

        if (opponentID.equals("") && TheTogyzQumalaq.getCreateConnect() == ResID.CONNECT && mode == ResID.INTERNET) {
            Gdx.input.getTextInput(this, TheTogyzQumalaq.WORDS[23], "", "");

        }
    }

    public static boolean isTurn() {
        return turn;
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
            if (mode == ResID.INTERNET) socket.emit("playerMoved", move);
            moveHasFinished = false;
            gsm.set(new GameOver(gsm));

        } else if (stoneBanks[0].currentStonesNumber == 81 && stoneBanks[1].currentStonesNumber == 81) {
            try {
                refreshGame();
            } catch (FileNotFoundException fnfe) {
            }
            if (mode == ResID.INTERNET) socket.emit("playerMoved", move);
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
        if (mode == ResID.SINGLE_PLAYER) {
            if (isAtsyrau()) {
                if (!turn) return TheTogyzQumalaq.WORDS[9];
                else return TheTogyzQumalaq.WORDS[10];
            } else if (stoneBanks[0].currentStonesNumber == 81 && stoneBanks[1].currentStonesNumber == 81) {
                return TheTogyzQumalaq.WORDS[11];
            } else if (stoneBanks[0].currentStonesNumber > stoneBanks[1].currentStonesNumber) {
                return TheTogyzQumalaq.WORDS[9];
            } else {
                return TheTogyzQumalaq.WORDS[10];
            }
        } else if (mode == ResID.MULTIPLAYER || mode == ResID.INTERNET) {
            if (isAtsyrau()) {
                if (!turn) return TheTogyzQumalaq.WORDS[12];
                else return TheTogyzQumalaq.WORDS[13];
            } else if (stoneBanks[0].currentStonesNumber == 81 && stoneBanks[1].currentStonesNumber == 81) {
                return TheTogyzQumalaq.WORDS[11];
            } else if (stoneBanks[0].currentStonesNumber > stoneBanks[1].currentStonesNumber) {
                return TheTogyzQumalaq.WORDS[12];
            } else {
                return TheTogyzQumalaq.WORDS[13];
            }
        } else {
            return "";
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

    public BitmapFont getBitmapFontFlipped() {
        return bitmapFontFlipped;
    }

    private void initResource() {
        bg = new Texture(ResID.BACKGROUND + TheTogyzQumalaq.POSTFIX + ".png");
        slotTexture = new Texture(ResID.SLOT + TheTogyzQumalaq.POSTFIX + ".png");
        stoneBankTexture = new Texture(ResID.STONE_BANK + TheTogyzQumalaq.POSTFIX + ".png");
        stoneTexture = new Texture(ResID.STONE + TheTogyzQumalaq.POSTFIX + ".png");
        tuzdykTexture = new Texture(ResID.TUZDYK + TheTogyzQumalaq.POSTFIX + ".png");
        glowTexture = new Texture(ResID.GLOW);
        turnUpTexture = new Texture(ResID.TURN_UP + TheTogyzQumalaq.POSTFIX + ".png");
        turnDownTexture = new Texture(ResID.TURN_DOWN + TheTogyzQumalaq.POSTFIX + ".png");
        undoTexture = new Texture(ResID.UNDO + TheTogyzQumalaq.POSTFIX + ".png");
        homeTexture = new Texture(ResID.HOME + TheTogyzQumalaq.POSTFIX + ".png");
        soundOnTexture = new Texture(ResID.SOUND + TheTogyzQumalaq.POSTFIX + ".png");
        soundOffTexture = new Texture(ResID.SOUND_OFF + TheTogyzQumalaq.POSTFIX + ".png");
        soundTexture = sound ? soundOnTexture : soundOffTexture;
        undoRectangle = new Rectangle(812f, 465f, undoTexture.getWidth() + 20, undoTexture.getHeight() + 20);
        homeRectangle = new Rectangle(812f, 41f, homeTexture.getWidth() + 20, homeTexture.getHeight() + 20);
        soundRectangle = new Rectangle(812f, 96f, soundTexture.getWidth() + 20, soundTexture.getHeight() + 20);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FILE_PATH));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = CHAR_STRING;
        parameter.size = 15;
        if (TheTogyzQumalaq.getIndexOfTheme() != 8 && TheTogyzQumalaq.getIndexOfTheme() != 6 && TheTogyzQumalaq.getIndexOfTheme() != 9) {
            parameter.borderWidth = 1f;
        }
        GlyphLayout glyphLayout = new GlyphLayout();
        bitmapFont = generator.generateFont(parameter);
        bitmapFontIceCream = generator.generateFont(parameter);
        parameter.flip = true;
        bitmapFontFlipped = generator.generateFont(parameter);
        bitmapFontIceCreamFlipped = generator.generateFont(parameter);
        bitmapFont.setColor(ResID.NUMBER_COLORS[TheTogyzQumalaq.getIndexOfTheme()]);
        bitmapFontIceCream.setColor(ResID.INDEX_COLORS[TheTogyzQumalaq.getIndexOfTheme()]);
        bitmapFontFlipped.setColor(ResID.NUMBER_COLORS[TheTogyzQumalaq.getIndexOfTheme()]);
        bitmapFontIceCreamFlipped.setColor(ResID.INDEX_COLORS[TheTogyzQumalaq.getIndexOfTheme()]);
        bitmapFontFlipped.getData().setScale(-1, 1);
        bitmapFontIceCreamFlipped.getData().setScale(-1, 1);
        generator.dispose();
        glyphLayout.setText(TheTogyzQumalaq.getBitmapFont(), TheTogyzQumalaq.WORDS[21]);
        wWaiting = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getBitmapFont(), TheTogyzQumalaq.WORDS[22] + "000000000");
        wYourId = glyphLayout.width;
        glyphLayout.reset();
        effective = Gdx.audio.newSound(Gdx.files.internal(ResID.EFFECTIVE_MOVE));
        error = Gdx.audio.newSound(Gdx.files.internal(ResID.ERROR));
        empty = Gdx.audio.newSound(Gdx.files.internal(ResID.EMPTY_MOVE + TheTogyzQumalaq.POSTFIX + ".mp3"));
        tuzdykSound = Gdx.audio.newSound(Gdx.files.internal(ResID.TUZDYK_SOUND + TheTogyzQumalaq.POSTFIX + ".mp3"));
        jackpot = Gdx.audio.newSound(Gdx.files.internal(ResID.JACKPOT));
        if (mode == ResID.SINGLE_PLAYER || mode == ResID.MULTIPLAYER)
            fileHandle = Gdx.files.local(saver);
        //tuzdykAnimationTexture = new Texture(ResID.TUZDYK_ANIMATION + TheTogyzQumalaq.POSTFIX + ".png");
        //tuzdykAnimation = new Animation(new TextureRegion(tuzdykAnimationTexture), 5, 0.4f);

        blackBackground = new Texture(ResID.BLACK_BG);
        blackSprite = new Sprite(blackBackground);
        alpha = 1f;
        blackSprite.setAlpha(alpha);
        blackSprite.setPosition(0, 0);
    }

    @Override
    protected void handleInput() {
        Vector3 tmp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(tmp);
        if (Gdx.input.justTouched()) {
            if (undoRectangle.contains(tmp.x, tmp.y)) {

                undo();
            } else if (homeRectangle.contains(tmp.x, tmp.y)) {
                if (TheTogyzQumalaq.sound) TheTogyzQumalaq.getButtonSound().play();
                if (mode == ResID.INTERNET) socket.disconnect();
                gsm.set(new MenuState(gsm, TheTogyzQumalaq.POSTFIX));
            } else if (soundRectangle.contains(tmp.x, tmp.y)) {

                if (sound) {
                    sound = false;
                    TheTogyzQumalaq.getMusic().pause();
                    soundTexture = soundOffTexture;
                } else {
                    sound = true;
                    TheTogyzQumalaq.getMusic().play();
                    soundTexture = soundOnTexture;
                }
            } else if (mode == ResID.INTERNET) {
                if (Gdx.input.justTouched()) {
                    for (int i = 0; i < 18; i++) {
                        if (slots[i].isToched(camera)) {
                            sinteticMove = false;
                            move = slots[i].slotNumber;
                            animationStarted = true;
                            if (slots[i].side == turn && slots[i].currentStonesNumber != 0)
                                regShot();
                            logic();
                            printState();
                        }
                    }
                }
            } else if (mode == ResID.MULTIPLAYER) {
                if (Gdx.input.justTouched()) {
                    for (int i = 0; i < slots.length; i++) {
                        if (slots[i].isToched(camera)) {
                            move = slots[i].slotNumber;
                            animationStarted = true;
                            if (slots[i].side == turn && slots[i].currentStonesNumber != 0)
                                regShot();
                            logic();
                            printState();
                        }
                    }
                }
            }
        }
        if (mode == ResID.SINGLE_PLAYER) {
            if (turn) {
                if (Gdx.input.justTouched()) {
                    for (int i = 0; i < slots.length; i++) {
                        if (slots[i].isToched(camera)) {

                            move = slots[i].slotNumber;
                            animationStarted = true;
                            hasIMakedMove = true;
                            if (slots[i].side == turn && slots[i].currentStonesNumber != 0)
                                regShot();
                            logic();
                            printState();
                        }
                    }
                }
            } else {
                currentTime += deltaTime;
                think = true;
                if (currentTime > DELAY) {
                    switch (TheTogyzQumalaq.botLevel) {
                        case 0:
                            move = AI.makeMoveEasyAI(slots);
                            hasAIMakedMove = true;
                            animationStarted = true;
                            break;
                        case 1:
                            move = AI.makeMoveNormalAI(slots, possibleMoves);
                            hasAIMakedMove = true;
                            animationStarted = true;
                            break;
                        case 2:
                            move = AI.makeMoveHardAI(slots, possibleMoves);
                            hasAIMakedMove = true;
                            animationStarted = true;
                            break;
                        case 3:
                            move = AI.makeMoveEffectiveAI(slots);
                            hasAIMakedMove = true;
                            animationStarted = true;
                            break;
                        default:
                            move = AI.makeMoveNormalAI(slots, possibleMoves);
                            hasAIMakedMove = true;
                            animationStarted = true;
                    }
                    logic();
                    currentTime = 0;
                    printState();
                    if (wasNoTuzdykAlready) for (int i = 0; i < 9; i++) {
                        if (slots[i].isTuzdyk) {
                            isAIMoveTuzdykMaker = true;
                            wasNoTuzdykAlready = false;
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
        if (think) thinkCounter += dt;
        if (alpha > 0) {
            alpha -= 0.02f;
            blackSprite.setAlpha(alpha);
        }
        if (thinkingBar.length() > 2) {
            thinkingBar = "";
        } else if (thinkCounter > 0.3f) {
            thinkingBar += ".";
            thinkCounter = 0;
        }
        if (mode == ResID.SINGLE_PLAYER || mode == ResID.MULTIPLAYER) try {
            saveGame();
        } catch (FileNotFoundException fnfe) {
        }
        if (((!opponentID.equals("")) && mode == ResID.INTERNET) || mode == ResID.SINGLE_PLAYER || mode == ResID.MULTIPLAYER)
            handleInput();

        if (animationStarted) {
            for (int i = 0; i < slots.length; i++) {
                slots[i].fadeStoneAnimation(1 / (float) slowness);
            }
            for (int i = 0; i < stoneBanks.length; i++) {
                stoneBanks[i].fadeStoneAnimation(1 / (float) slowness);
            }
            frameCounter++;
        }
        if (frameCounter == slowness) {
            animationStarted = false;
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
        if (mode == ResID.INTERNET) updateServer(dt);
        checkTheVictory();
        camera.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        sb.draw(bg, 0, 0);
        if (((!opponentID.equals("")) && mode == ResID.INTERNET) || mode == ResID.SINGLE_PLAYER || mode == ResID.MULTIPLAYER) {
            for (int i = 0; i < slots.length; i++) {
                //slots[i].resetStonesAlpha();
                if (!slots[i].isTuzdyk)
                    sb.draw(slots[i].texture, slots[i].x, slots[i].y);
                else {
                    sb.draw(tuzdykTexture, slots[i].x, slots[i].y);
                }


                slots[i].drawStones(sb);

                if (slots[i].side) {
                    slots[i].drawCurrentNumber(sb, bitmapFont, bitmapFontIceCream);
                } else {
                    slots[i].drawCurrentNumber(sb, bitmapFontFlipped, bitmapFontIceCreamFlipped);

                }

            }
            for (int i = 0; i < stoneBanks.length; i++) {
                sb.draw(stoneBanks[i].texture, stoneBanks[i].x, stoneBanks[i].y);
                stoneBanks[i].drawStones(sb);
                stoneBanks[i].drawCurrentNumber(sb, bitmapFont, i);
            }
            slots[move].glow(sb, glowTexture);
            sb.draw(undoTexture, 822f, 475f);
            sb.draw(homeTexture, 822f, 42f);
            sb.draw(soundTexture, 822f, 106f);
            if (think) TheTogyzQumalaq.getBitmapFont().draw(sb, thinkingBar, 822f, 440f);


            if (turn) {
                sb.draw(turnDownTexture, 5, 5);
            } else {
                sb.draw(turnUpTexture, 5, 265);
            }
        } else {
            if (opponentID.equals("") && TheTogyzQumalaq.getCreateConnect() == ResID.CREATE && mode == ResID.INTERNET) {
                TheTogyzQumalaq.getBitmapFont().draw(sb, TheTogyzQumalaq.WORDS[21], (TheTogyzQumalaq.WIDTH - wWaiting) / 2, TheTogyzQumalaq.HEIGHT / 2);
                TheTogyzQumalaq.getBitmapFont().draw(sb, TheTogyzQumalaq.WORDS[22] + myID, (TheTogyzQumalaq.WIDTH - wYourId) / 2, TheTogyzQumalaq.HEIGHT / 2 + 100f);
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
        bitmapFontIceCream.dispose();
        bitmapFont.dispose();
        bitmapFontFlipped.dispose();
        bitmapFontIceCreamFlipped.dispose();
        blackBackground.dispose();
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
            if (sound && (mode == ResID.SINGLE_PLAYER && !turn || (mode == ResID.MULTIPLAYER || mode == ResID.INTERNET)))
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
        source = move;

        if (lastSlotIndex != -1) {
            destination = lastSlotIndex;
            if (mode == ResID.MULTIPLAYER || mode == ResID.INTERNET) isMoveTuzdykMaker = false;
            if (mode == ResID.SINGLE_PLAYER && hasIMakedMove && isMoveTuzdykMaker)
                isMoveTuzdykMaker = false;
            if (mode == ResID.SINGLE_PLAYER && hasAIMakedMove && isAIMoveTuzdykMaker)
                isAIMoveTuzdykMaker = false;
            if (mode == ResID.SINGLE_PLAYER && !turn) {
                think = false;
            }
            if (turn == !slots[lastSlotIndex].side) {

                if (slots[lastSlotIndex].currentStonesNumber % 2 == 0) {

                    if (slots[lastSlotIndex].currentStonesNumber >= 16 && sound) jackpot.play();

                    stoneBanks[turn ? 0 : 1].currentStonesNumber += slots[lastSlotIndex].currentStonesNumber;

                    slots[lastSlotIndex].currentStonesNumber = 0;

                    if (sound) {
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
                        if (sound) tuzdykSound.play();
                        isEffective = true;
                        isMoveTuzdykMaker = true;
                    }
                }
                prevMove = move;
            }
            if (!isEffective)
                if (sound) {
                    empty.play();
                }
            isEffective = false;
            moveHasFinished = true;
            flag = false;
        }

        if (hasAIMakedMove) hasAIMakedMove = false;
        if (hasIMakedMove) hasIMakedMove = false;
        turn ^= true;
    }

    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.BACK) {
            // Optional back button handling (e.g. ask for confirmation)
            if (mode == ResID.INTERNET) socket.disconnect();
            gsm.set(new MenuState(gsm, TheTogyzQumalaq.POSTFIX));
            /*if (shouldReallyQuit)
                Gdx.app.exit();*/
        } else if (keycode == Input.Keys.SPACE) {
            //socket.emit("playerMoved", move);
        } else if (keycode == Input.Keys.ENTER) {
            //updateBoard();
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
        animationStarted = true;
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
        File file = fileHandle.file();
        if (file.exists()) {
            loadGame();
        }
    }

    private static void refreshGame() throws FileNotFoundException {
        if (mode == ResID.MULTIPLAYER || mode == ResID.MULTIPLAYER) {
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
                if (mode == ResID.SINGLE_PLAYER) {
                    if (slots[i].side == !turn) {
                        slots[i].texture = slotTexture;
                        slots[i].isTuzdyk = false;

                    }
                } else if (mode == ResID.MULTIPLAYER || mode == ResID.INTERNET) {
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
                    wasNoTuzdykAlready = true;
                }
            }
        }
        try {
            shots.get(shots.size() - 1).getShot(this);
            shots.remove(shots.size() - 1);
            animationStarted = true;

        } catch (ArrayIndexOutOfBoundsException aiEx) {
            if (sound) error.play();
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
                animationStarted = true;
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