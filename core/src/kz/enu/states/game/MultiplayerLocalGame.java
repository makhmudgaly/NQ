package kz.enu.states.game;

import com.badlogic.gdx.Gdx;

import kz.enu.states.model.GameStateManager;

/**
 * Created by Meirkhan on 03.09.2018.
 */

public class MultiplayerLocalGame extends PlayState{

    public MultiplayerLocalGame(GameStateManager gsm, int mode, boolean isNewGame) {
        super(gsm, mode, isNewGame);
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
}
