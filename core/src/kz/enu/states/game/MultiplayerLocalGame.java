package kz.enu.states.game;

import com.badlogic.gdx.Gdx;

import kz.enu.TheTogyzQumalaq;
import kz.enu.states.model.GameStateManager;
import kz.enu.states.model.LocalGame;

/**
 * Multiplayer game on same device
 * Created by Meirkhan on 03.09.2018.
 */

public class MultiplayerLocalGame extends LocalGame {

    public MultiplayerLocalGame(GameStateManager gsm, boolean isNewGame) {
        super(gsm, isNewGame);
    }

    @Override
    protected void initVariables() {
        super.initVariables();
        sSaveFile = "save.txt";
    }

    @Override
    protected void undo() {
        bUndoTurn = turn;
        super.undo();
    }

    @Override
    protected void handleSpecificAction() {
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
    @Override
    protected void specificCorrectMoveAction() {
        isMoveTuzdykMaker = false;
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
