package kz.enu.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import kz.enu.ResID;
import kz.enu.TheTogyzQumalaq;

/**
 * Created by SLUX on 02.07.2017.
 */

public class CreateConnectState extends State implements InputProcessor {

    private static int mode;
    private Texture background;
    private Rectangle createRectangle;
    private Rectangle connectRectangle;
    private Rectangle backRectangle;

    private boolean backAnimatino;
    private int selected;
    static float offset;
    float wCreate, wConnect, wBack;
    private static final float boundX = 10f, boundY = 10f;

    public CreateConnectState(GameStateManager gsm, int mode) {
        super(gsm);
        this.mode = mode;
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        backAnimatino = false;
        offset = TheTogyzQumalaq.WIDTH * 0.56f;
        background = new Texture(ResID.MAIN_MENU + TheTogyzQumalaq.POSTFIX + ".png");
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(TheTogyzQumalaq.getBitmapFont(), TheTogyzQumalaq.WORDS[24]);
        wCreate = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getBitmapFont(), TheTogyzQumalaq.WORDS[25]);
        wConnect = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getBitmapFont(), TheTogyzQumalaq.WORDS[17]);
        wBack = glyphLayout.width;
        createRectangle = new Rectangle(TheTogyzQumalaq.WIDTH * 0.44f - boundX, 324f - boundY, wCreate + boundX * 2, 50f + boundY * 2);
        connectRectangle = new Rectangle(TheTogyzQumalaq.WIDTH * 0.44f - boundX, 216f - boundY, wConnect + boundX * 2, 50f + boundY * 2);
        backRectangle = new Rectangle(TheTogyzQumalaq.WIDTH * 0.44f - boundX, 108f - boundY, wBack + boundX * 2, 50f + boundX * 2);
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
    }

    @Override
    protected void handleInput() {
        Vector3 tmp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(tmp);
        if (Gdx.input.justTouched()) {
            if (createRectangle.contains(tmp.x, tmp.y)) {
                backAnimatino = true;
                if (TheTogyzQumalaq.sound) TheTogyzQumalaq.getButtonSound().play();
                selected = 0;
            } else if (connectRectangle.contains(tmp.x, tmp.y)) {
                backAnimatino = true;
                if (TheTogyzQumalaq.sound) TheTogyzQumalaq.getButtonSound().play();
                selected = 1;
            } else if (backRectangle.contains(tmp.x, tmp.y)) {
                backAnimatino = true;
                if (TheTogyzQumalaq.sound) TheTogyzQumalaq.getButtonSound().play();
                selected = 2;
            }
        }
    }

    @Override
    public void update(float dt) {
        if (offset > 0 && backAnimatino == false) offset -= 18f;
        if (backAnimatino) offset += 18f;
        handleInput();
        if (offset >= TheTogyzQumalaq.WIDTH * 0.8f && backAnimatino) {
            switch (selected) {
                case 0: {
                    TheTogyzQumalaq.setCreateConnect(ResID.CREATE);
                    gsm.set(new LoadingState(gsm, ResID.INTERNET, 0));
                }
                break;
                case 1: {
                    TheTogyzQumalaq.setCreateConnect(ResID.CONNECT);
                    gsm.set(new LoadingState(gsm, ResID.INTERNET, 0));
                }
                break;
                case 2: {
                    gsm.set(new MenuState(gsm, TheTogyzQumalaq.POSTFIX));
                }
                break;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
        TheTogyzQumalaq.getBitmapFont().draw(sb, TheTogyzQumalaq.WORDS[24], TheTogyzQumalaq.WIDTH * 0.44f + offset, 374f);
        TheTogyzQumalaq.getBitmapFont().draw(sb, TheTogyzQumalaq.WORDS[25], TheTogyzQumalaq.WIDTH * 0.44f + offset, 266f);
        TheTogyzQumalaq.getBitmapFont().draw(sb, TheTogyzQumalaq.WORDS[17], TheTogyzQumalaq.WIDTH * 0.44f + offset, 158f);
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
            backAnimatino = true;
            if (TheTogyzQumalaq.sound) TheTogyzQumalaq.getButtonSound().play();
            selected = 2;
            /*if (shouldReallyQuit)
                Gdx.app.exit();*/
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