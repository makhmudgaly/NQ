package kz.enu.states.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import kz.enu.TheTogyzQumalaq;
import kz.enu.system.Registry;

/**
 * Transition State before Game
 * Created by SLUX on 26.06.2017.
 */

public class LoadingState extends kz.enu.states.model.State {
    private Texture oBlackBackground;
    private int GAME_MODE;
    private boolean IS_NEW_GAME;
    private static int counter;
    private static String progressBar;
    private float wLoading,wProgress;

    public LoadingState(kz.enu.states.model.GameStateManager gsm, int mode, boolean isNewGame) {
        super(gsm);
        GAME_MODE = mode;
        IS_NEW_GAME = isNewGame;
        counter = 0;
        progressBar = "";
        oBlackBackground = new Texture(Registry.BLACK_BG);
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[14]);
        wLoading = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(),"■■■■■■■■■■■■■■■");
        wProgress = glyphLayout.width;
        TheTogyzQumalaq.getMainFont().setColor(Registry.LOADING_COLORS[TheTogyzQumalaq.getIndexOfTheme()]);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        counter++;
        if(counter % 7 == 0){ progressBar+="■"; }
        if(progressBar.length() >15) {
            TheTogyzQumalaq.getMainFont().setColor(Registry.COLORS[TheTogyzQumalaq.getIndexOfTheme()]);
            // Rendering corresponding game state
            switch (GAME_MODE) {
                case Registry.SINGLE_PLAYER:
                    gsm.set(new kz.enu.states.game.SinglePlayerGame(gsm, IS_NEW_GAME));
                    break;
                case Registry.MULTIPLAYER:
                    gsm.set(new kz.enu.states.game.MultiplayerLocalGame(gsm, IS_NEW_GAME));
                    break;
                case Registry.INTERNET:
                    gsm.set(new kz.enu.states.game.MultiplayerInternetGame(gsm));
                    break;
                case Registry.TUTORIAL:
                    gsm.set(new kz.enu.states.game.TrainingState(gsm));
                    break;
            }
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
            sb.draw(oBlackBackground,0,0);
            TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[14],(TheTogyzQumalaq.WIDTH-wLoading)/2, TheTogyzQumalaq.HEIGHT*0.2f);
            TheTogyzQumalaq.getMainFont().draw(sb,progressBar,(TheTogyzQumalaq.WIDTH-wProgress)/2, TheTogyzQumalaq.HEIGHT*0.3f);
        sb.end();
    }

    @Override
    public void dispose() {
        oBlackBackground.dispose();
    }
}
