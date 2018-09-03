package kz.enu.states.game;

import kz.enu.states.model.GameStateManager;

/**
 * Created by Meirkhan on 03.09.2018.
 */

public class SinglePlayerGame extends PlayState{
    public SinglePlayerGame(GameStateManager gsm, int mode, boolean isNewGame) {
        super(gsm, mode, isNewGame);
    }
}
