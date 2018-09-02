package kz.enu.states.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import kz.enu.TheTogyzQumalaq;
import kz.enu.system.Registry;
import kz.enu.states.view.*;
import kz.enu.states.view.LoadingState;
import kz.enu.system.Util;

/**
 * Created by SLUX on 07.06.2017.
 */

public class NewConState extends kz.enu.states.model.State implements InputProcessor {
    private static int GAME_MODE;
    private Texture background;
    private Rectangle newRectangle;
    private Rectangle conRectangle;
    private Rectangle backRectangle;

    private boolean backAnimatino;
    private int selected;
    static float offset ;
    float wNew,wCon,wBack;
    private static final float boundX = 10f,boundY=10f;
    public NewConState(kz.enu.states.model.GameStateManager gsm, int mode) {
        super(gsm);
        GAME_MODE = mode;
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        backAnimatino = false;
        offset = TheTogyzQumalaq.WIDTH*0.56f;
        background = Util.getTexture(Registry.MAIN_MENU);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[3]);
        wNew = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[4]);
        wCon = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[17]);
        wBack = glyphLayout.width;
        newRectangle = new Rectangle(TheTogyzQumalaq.WIDTH*0.44f-boundX,324f-boundY,wNew+boundX*2,50f+boundY*2);
        conRectangle = new Rectangle(TheTogyzQumalaq.WIDTH*0.44f-boundX,216f-boundY,wCon+boundX*2,50f+boundY*2);
        backRectangle = new Rectangle(TheTogyzQumalaq.WIDTH*0.44f-boundX,108f-boundY,wBack+boundX*2,50f+boundX*2);
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
    }

    @Override
    protected void handleInput() {
        Vector3 tmp = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
        camera.unproject(tmp);
        if(Gdx.input.justTouched()) {
            if (newRectangle.contains(tmp.x, tmp.y)) {
                backAnimatino = true;
                if(TheTogyzQumalaq.bPlaySound) TheTogyzQumalaq.getButtonSound().play();
                selected = 0;
            }else if (conRectangle.contains(tmp.x, tmp.y)) {
                backAnimatino = true;
                if(TheTogyzQumalaq.bPlaySound) TheTogyzQumalaq.getButtonSound().play();
                selected = 1;
            }else if(backRectangle.contains(tmp.x,tmp.y)){
                backAnimatino = true;
                if(TheTogyzQumalaq.bPlaySound) TheTogyzQumalaq.getButtonSound().play();
                selected = 2;
            }
        }
    }

    @Override
    public void update(float dt) {
        if (backAnimatino) {
            offset += 18f;
        } else if (offset > 0) {
            offset -= 18f;
        }
        handleInput();
        if(offset>= TheTogyzQumalaq.WIDTH*0.8f&&backAnimatino) {
            switch (selected) {
                case 0:gsm.set(new kz.enu.states.view.LoadingState(gsm, GAME_MODE,false));break;
                case 1:gsm.set(new LoadingState(gsm, GAME_MODE,true));break;
                case 2:gsm.set(new MenuState(gsm, TheTogyzQumalaq.POSTFIX));break;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
            sb.draw(background,0,0, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
            TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[3], TheTogyzQumalaq.WIDTH*0.44f+offset, 374f);
            TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[4], TheTogyzQumalaq.WIDTH*0.44f+offset, 266f);
            TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[17], TheTogyzQumalaq.WIDTH*0.44f+offset, 158f);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            // Optional back button handling (e.g. ask for confirmation)
            backAnimatino = true;
            if(TheTogyzQumalaq.bPlaySound) TheTogyzQumalaq.getButtonSound().play();
            selected = 2;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.ESCAPE){
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
}
