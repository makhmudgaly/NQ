package kz.enu.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import kz.enu.ResID;
import kz.enu.TheTogyzQumalaq;

/**
 * Created by SLUX on 26.06.2017.
 */

public class LoadingState extends State {
    private Texture black_bachground;
    private int mode;
    private int newCon;
    private static int counter,points;
    private static String progressBar;
    float wLoading,wProgress;
    public LoadingState(GameStateManager gsm,int mode,int newCon) {
        super(gsm);
        this.mode = mode;
        this.newCon = newCon;
        counter = 0;
        points = 0;
        progressBar = "";
        black_bachground = new Texture(ResID.BLACK_BG);
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH,TheTogyzQumalaq.HEIGHT);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(),TheTogyzQumalaq.LOCALE[14]);
        wLoading = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(),"■■■■■■■■■■■■■■■");
        wProgress = glyphLayout.width;
        TheTogyzQumalaq.getMainFont().setColor(ResID.LOADING_COLORS[TheTogyzQumalaq.getIndexOfTheme()]);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        counter++;
        if(counter%7==0){progressBar+="■";points++;}
        if(points>15){
            TheTogyzQumalaq.getMainFont().setColor(ResID.COLORS[TheTogyzQumalaq.getIndexOfTheme()]);
            gsm.set(new PlayState(gsm,mode,newCon));}
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
            sb.draw(black_bachground,0,0);
            TheTogyzQumalaq.getMainFont().draw(sb,TheTogyzQumalaq.LOCALE[14],(TheTogyzQumalaq.WIDTH-wLoading)/2,TheTogyzQumalaq.HEIGHT*0.2f);
            TheTogyzQumalaq.getMainFont().draw(sb,progressBar,(TheTogyzQumalaq.WIDTH-wProgress)/2,TheTogyzQumalaq.HEIGHT*0.3f);
        sb.end();
    }

    @Override
    public void dispose() {
        black_bachground.dispose();
    }
}
