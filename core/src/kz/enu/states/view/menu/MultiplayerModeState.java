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
import kz.enu.states.view.MenuState;
import kz.enu.system.Util;

/**
 * Created by SLUX on 02.07.2017.
 */

public class MultiplayerModeState extends kz.enu.states.model.State implements InputProcessor {

    private static int GAME_MODE;
    private Texture background;
    private Rectangle netRectangle;
    private Rectangle localRectangle;
    private Rectangle backRectangle;

    private boolean bBackAnimation;
    private int selected;
    private static float offset;
    private static final float boundX = 10f, boundY = 10f;

    public MultiplayerModeState(kz.enu.states.model.GameStateManager gsm, int mode) {
        super(gsm);
        float wNet, wLocal, wBack;
        GAME_MODE = mode;
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        bBackAnimation = false;
        offset = TheTogyzQumalaq.WIDTH * 0.56f;
        background = Util.getTexture(Registry.MAIN_MENU);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[19]);
        wNet = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[20]);
        wLocal = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), TheTogyzQumalaq.LOCALE[17]);
        wBack = glyphLayout.width;
        netRectangle = new Rectangle(TheTogyzQumalaq.WIDTH * 0.44f - boundX, 324f - boundY, wNet + boundX * 2, 50f + boundY * 2);
        localRectangle = new Rectangle(TheTogyzQumalaq.WIDTH * 0.44f - boundX, 216f - boundY, wLocal + boundX * 2, 50f + boundY * 2);
        backRectangle = new Rectangle(TheTogyzQumalaq.WIDTH * 0.44f - boundX, 108f - boundY, wBack + boundX * 2, 50f + boundX * 2);
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
    }

    @Override
    protected void handleInput() {
        Vector3 tmp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(tmp);
        if (Gdx.input.justTouched()) {
            if (netRectangle.contains(tmp.x, tmp.y)) {
                bBackAnimation = true;
                if (TheTogyzQumalaq.bPlaySound) TheTogyzQumalaq.getButtonSound().play();
                selected = 0;
            } else if (localRectangle.contains(tmp.x, tmp.y)) {
                bBackAnimation = true;
                if (TheTogyzQumalaq.bPlaySound) TheTogyzQumalaq.getButtonSound().play();
                selected = 1;
            } else if (backRectangle.contains(tmp.x, tmp.y)) {
                bBackAnimation = true;
                if (TheTogyzQumalaq.bPlaySound) TheTogyzQumalaq.getButtonSound().play();
                selected = 2;
            }
        }
    }

    @Override
    public void update(float dt) {
        if (bBackAnimation) {
            offset += 18f;
        } else if (offset > 0) {
            offset -= 18f;
        }
        handleInput();
        if (offset >= TheTogyzQumalaq.WIDTH * 0.8f && bBackAnimation) {
            switch (selected) {
                case 0:
                    gsm.set(new CreateConnectState(gsm, 0));
                    break;
                case 1:
                    gsm.set(new LoadModeState(gsm, GAME_MODE));
                    break;
                case 2:
                    gsm.set(new MenuState(gsm, TheTogyzQumalaq.POSTFIX));
                    break;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
        TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[19], TheTogyzQumalaq.WIDTH * 0.44f + offset, 374f);
        TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[20], TheTogyzQumalaq.WIDTH * 0.44f + offset, 266f);
        TheTogyzQumalaq.getMainFont().draw(sb, TheTogyzQumalaq.LOCALE[17], TheTogyzQumalaq.WIDTH * 0.44f + offset, 158f);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            // Optional back button handling (e.g. ask for confirmation)
            bBackAnimation = true;
            if (TheTogyzQumalaq.bPlaySound) TheTogyzQumalaq.getButtonSound().play();
            selected = 2;
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
}
