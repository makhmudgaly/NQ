package kz.enu.states.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import kz.enu.TheTogyzQumalaq;
import kz.enu.system.Registry;
import kz.enu.sprites.Board;
import kz.enu.sprites.Slot;
import kz.enu.sprites.StoneBank;
import kz.enu.states.view.GameOver;
import kz.enu.states.view.MenuState;
import kz.enu.system.FontManager;
import kz.enu.system.Util;

import static kz.enu.TheTogyzQumalaq.bPlaySound;


/**
 * Main play state
 * Created by SLUX on 17.05.2017.
 */

public class PlayState extends State implements InputProcessor, Input.TextInputListener {

    private static final String FONT_PATH = "arial.ttf";
    private static final int FONT_SIZE = 15;

    protected static boolean bAnimationStarted;
    private static int frameCounter = 0;
    private final static int slowness = 20;

    private final static float SPASING = 87f;
    private final static float Y_OFFSET = 5f;

    protected static Slot[] slots = new Slot[18];
    protected static StoneBank[] stoneBanks = new StoneBank[2];

    protected static String sSaveFile;

    protected static boolean turn;

    private static ArrayList<Board> shots = new ArrayList<Board>();

    private static Sound effective;
    private Sound empty;
    private Sound tuzdykSound;
    private Sound jackpot;
    private static Sound error;

    private Texture bg;
    protected Texture slotTexture;
    private float alpha;

    protected static Texture tuzdykTexture;
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
    protected boolean bNeedHandleInput = true;
    protected boolean bNeedSaveGame = true;
    protected boolean bNeedErrorSound = true;
    protected boolean bUndoTurn;

    private BitmapFont oMainFont;
    private BitmapFont oMainFontFlipped;
    private BitmapFont oIceCreamFont;
    private BitmapFont oIceCreamFontFlipped;

    protected boolean isMoveTuzdykMaker = false;
    protected boolean bPlayerMadeMove = false;


    private boolean isEffective = false;

    public PlayState(GameStateManager gsm) {
        super(gsm);

        initVariables();
        initResource();
        initSlots(slotTexture);
        initStoneBanks(stoneBankTexture);


        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        frameCounter = 0;

        shots.clear();
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

        } else if (stoneBanks[0].currentStonesNumber == 81 && stoneBanks[1].currentStonesNumber == 81) {
            try {
                refreshGame();
            } catch (FileNotFoundException fnfe) {
            }
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
    protected void refreshGame() throws FileNotFoundException {}

    protected void saveGame() throws FileNotFoundException {}

    public String getGameOverWords() {
        return "";
    }

    protected void initVariables() {
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
        bAnimationStarted = false;
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

    protected void initResource() {
        // Texture initialization
        initTextures();

        // Font initialization
        initFonts();

        // Sound refactor
        initSound();

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

    protected void initFonts() {
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
        oIceCreamFont = FontManager.getFont(FONT_PATH, FONT_SIZE, oIceCreamColor, fBorderWidth, oBorderColor, false);
        oIceCreamFontFlipped = FontManager.getFont(FONT_PATH, FONT_SIZE, oIceCreamColor, fBorderWidth, oBorderColor, true);
        oIceCreamFontFlipped.getData().setScale(-1, 1);
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
                goToMainMenu();
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
            } else {
                handleSpecificAction();
            }
        }
    }


    @Override
    public void update(float dt) {
        // Transition animation
        fadeOffBlackWindow();

        if (bNeedSaveGame) {
            try {
                saveGame();
            } catch (FileNotFoundException fnfe) {}
        }
        if (bNeedHandleInput)
            handleInput();

        // Animation of stones TODO: Reimplement with hand
        if (bAnimationStarted) {
            for (Slot slot :slots) {
                slot.fadeStoneAnimation(1 / (float) slowness);
            }
            for (StoneBank stoneBank : stoneBanks) {
                stoneBank.fadeStoneAnimation(1 / (float) slowness);
            }
            frameCounter++;
        }
        if (frameCounter == slowness) {
            bAnimationStarted = false;
            for (Slot slot :slots) {
                slot.fadeInAlpha = 0;
                slot.fadeOutAlpha = 1;
                slot.oldStonesNumber = slot.currentStonesNumber;
                slot.resetStonesAlpha();
            }
            for (StoneBank stoneBank : stoneBanks) {
                stoneBank.fadeInAlpha = 0;
                stoneBank.oldStonesNumber = stoneBank.currentStonesNumber;
                stoneBank.resetStonesAlpha();
            }
            frameCounter = 0;
        }
        checkTheVictory();
        camera.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        sb.draw(bg, 0, 0);
        if (bNeedHandleInput) {
            for (Slot slot :slots) {
                if (!slot.isTuzdyk)
                    sb.draw(slot.texture, slot.x, slot.y);
                else {
                    sb.draw(tuzdykTexture, slot.x, slot.y);
                }

                slot.drawStones(sb);

                if (slot.side) {
                    slot.drawCurrentNumber(sb, oMainFont, oIceCreamFont);
                } else {
                    slot.drawCurrentNumber(sb, oMainFontFlipped, oIceCreamFontFlipped);
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

            // For single player only
            renderThinkingBar(sb);

            if (turn) {
                sb.draw(turnDownTexture, 5, 5);
            } else {
                sb.draw(turnUpTexture, 5, 265);
            }
        } else {
            renderSpecificAction(sb);
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
        for (Slot slot :slots) {
            slot.dispose();
        }
        for (StoneBank stoneBank : stoneBanks) {
            stoneBank.dispose();
        }
        oIceCreamFont.dispose();
        oMainFont.dispose();
        oMainFontFlipped.dispose();
        oIceCreamFontFlipped.dispose();
        turnDownTexture.dispose();
        turnUpTexture.dispose();
    }

    protected static void printState() {
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

    protected static boolean isAtsyrau() {
        boolean b = true;
        for (Slot slot :slots) {
            if (turn == slot.side && slot.currentStonesNumber != 0) {
                b = false;
            }
        }
        return b;
    }

    protected int makeMove(int slotIndex) {
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
            if (bPlaySound && bNeedErrorSound)
                error.play();
            return -1;
        }

        return lastSlotIndex;

    }

    private static int getOpposite(int Index) {
        return (Index + 9) % 18;
    }

    protected void logic() {

        int lastSlotIndex;
        if (isAtsyrau()) {
            gsm.set(new GameOver(gsm, getGameOverWords()));
        }

        lastSlotIndex = makeMove(move);

        if (lastSlotIndex != -1) {
            specificCorrectMoveAction();

            if (turn == !slots[lastSlotIndex].side) {

                if (slots[lastSlotIndex].currentStonesNumber % 2 == 0) {

                    if (slots[lastSlotIndex].currentStonesNumber >= 16 && bPlaySound)
                        jackpot.play();

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
            }
            if (!isEffective)
                if (bPlaySound) {
                    empty.play();
                }
            isEffective = false;
        }

        if (bPlayerMadeMove) bPlayerMadeMove = false;
        turn ^= true;
    }

    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.BACK) {
            gsm.set(new MenuState(gsm, TheTogyzQumalaq.POSTFIX));

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


    protected static void regShot() {
        shots.clear();
        shots.add(new Board(slots, stoneBanks, turn));
    }

    protected void undo() {
        if (isMoveTuzdykMaker) {
            for (Slot slot :slots) {
                if (slot.side == bUndoTurn) {
                    slot.texture = slotTexture;
                    slot.isTuzdyk = false;

                }
            }
            isMoveTuzdykMaker = false;
        }
        specificUndoAction();
        try {
            shots.get(shots.size() - 1).getShot(this);
            shots.remove(shots.size() - 1);
            bAnimationStarted = true;

        } catch (ArrayIndexOutOfBoundsException aiEx) {
            if (bPlaySound) error.play();
        }

    }


    private static boolean isTuzdykAvailable() {
        boolean b = true;
        if (!turn) {
            for (int i = 0; i < 9; i++) {
                if (slots[i].isTuzdyk) {
                    b = false;
                }
            }
        } else {
            for (int i = 9; i < 18; i++) {
                if (slots[i].isTuzdyk) {
                    b = false;
                }
            }
        }
        return b;
    }

    @Override
    public void input(String text) { }

    @Override
    public void canceled() {
        Gdx.app.log("X", "Cc");
    }

    private void fadeOffBlackWindow() {
        if (alpha > 0) {
            alpha -= 0.02f;
            blackSprite.setAlpha(alpha);
        }
    }

    protected void goToMainMenu() {
        if (TheTogyzQumalaq.bPlaySound) TheTogyzQumalaq.getButtonSound().play();
        gsm.set(new MenuState(gsm, TheTogyzQumalaq.POSTFIX));
    }

    protected void handleSpecificAction() {}
    protected void renderSpecificAction(SpriteBatch sb) {}
    protected void renderThinkingBar(SpriteBatch sb) {}
    protected void specificCorrectMoveAction() {}
    protected void specificUndoAction() {}
}
