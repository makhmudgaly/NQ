package kz.enu.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import kz.enu.Registry;
import kz.enu.TheTogyzQumalaq;
import kz.enu.system.Util;

/**
 * Created by SLUX on 26.06.2017.
 */

public class LoadingState extends State {
    private Texture oBlackBackground;
    private int GAME_MODE;
    private boolean IS_NEW_GAME;
    private static int counter,points;
    private static String progressBar;
    private float wLoading,wProgress;

    public LoadingState(GameStateManager gsm,int mode, boolean isNewGame) {
        super(gsm);
        GAME_MODE = mode;
        IS_NEW_GAME = isNewGame;
        counter = 0;
        points = 0;
        progressBar = "";
        oBlackBackground = Util.getTexture(Registry.BLACK_BG, "");
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH,TheTogyzQumalaq.HEIGHT);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(),TheTogyzQumalaq.LOCALE[14]);
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
        if(counter%7==0){progressBar+="■";points++;}
        if(points>15){
            TheTogyzQumalaq.getMainFont().setColor(Registry.COLORS[TheTogyzQumalaq.getIndexOfTheme()]);
            gsm.set(new PlayState(gsm, GAME_MODE, IS_NEW_GAME));}
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
            sb.draw(oBlackBackground,0,0);
            TheTogyzQumalaq.getMainFont().draw(sb,TheTogyzQumalaq.LOCALE[14],(TheTogyzQumalaq.WIDTH-wLoading)/2,TheTogyzQumalaq.HEIGHT*0.2f);
            TheTogyzQumalaq.getMainFont().draw(sb,progressBar,(TheTogyzQumalaq.WIDTH-wProgress)/2,TheTogyzQumalaq.HEIGHT*0.3f);
        sb.end();
    }

    @Override
    public void dispose() {
        oBlackBackground.dispose();
    }
}
