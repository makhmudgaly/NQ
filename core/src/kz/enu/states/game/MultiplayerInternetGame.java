package kz.enu.states.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import kz.enu.TheTogyzQumalaq;
import kz.enu.states.model.GameStateManager;
import kz.enu.states.model.PlayState;
import kz.enu.states.view.GameOver;
import kz.enu.system.Registry;

/**
 * Multiplayer internet game
 * Created by Meirkhan on 03.09.2018.
 */

public class MultiplayerInternetGame extends PlayState {
    private static String opponentID = "";
    private static Socket socket;
    private final static float UPDATE_TIME = 1 / 30f;
    private float timer = 0;
    private static boolean moveHasFinished;
    private boolean sinteticMove;
    private static String myID = "";
    private static float wWaiting, wYourId;


    public MultiplayerInternetGame(GameStateManager gsm) {
        super(gsm);
        bNeedSaveGame = false;
        if (opponentID.equals("") && TheTogyzQumalaq.getCreateConnect() == Registry.CONNECT) {
            Gdx.input.getTextInput(this, TheTogyzQumalaq.LOCALE[23], "", "");
        }

        connectSocket();
        configSocketEvents();
        moveHasFinished = false;
    }

    @Override
    protected void initFonts() {
        super.initFonts();

        // Text width adjustments
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[21]);
        wWaiting = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[22] + "000000000");
        wYourId = glyphLayout.width;
        glyphLayout.reset();
    }

    @Override
    public void update(float dt) {
        bNeedHandleInput = !opponentID.equals("");
        super.update(dt);
        updateServer(dt);
    }

    @Override
    public void checkTheVictory() {
        super.checkTheVictory();
        socket.emit("playerMoved", move);
        moveHasFinished = false;
        gsm.set(new GameOver(gsm, getGameOverWords()));
    }

    @Override
    protected void goToMainMenu() {
        super.goToMainMenu();
        socket.disconnect();
    }

    @Override
    protected void undo() {
        bUndoTurn = turn;
        super.undo();
    }

    @Override
    protected void handleSpecificAction() {
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
    }
    @Override
    protected void renderSpecificAction(SpriteBatch sb) {
        if (opponentID.equals("") && TheTogyzQumalaq.getCreateConnect() == Registry.CREATE) {
            TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[21], (TheTogyzQumalaq.WIDTH - wWaiting) / 2, TheTogyzQumalaq.HEIGHT / 2);
            TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[22] + myID, (TheTogyzQumalaq.WIDTH - wYourId) / 2, TheTogyzQumalaq.HEIGHT / 2 + 100f);
        }
    }
    @Override
    protected void specificCorrectMoveAction() {
        isMoveTuzdykMaker = false;
        moveHasFinished = true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            socket.disconnect();
        }
        return super.keyDown(keycode);
    }

    private void updateServer(float dt) {
        timer += dt;
        if (timer >= UPDATE_TIME && moveHasFinished && !sinteticMove) {
            socket.emit("playerMoved", move, opponentID);
            moveHasFinished = false;
        }
    }

    private static void connectSocket() {
        try {
            socket = IO.socket("https://togyz.herokuapp.com");
            //socket = IO.socket("http://localhost:3000");
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configSocketEvents() {
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
    public String getGameOverWords() {
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
    }
}
