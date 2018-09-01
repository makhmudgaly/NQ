package kz.enu.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


import kz.enu.Registry;
import kz.enu.TheTogyzQumalaq;
import kz.enu.system.Util;


/**
 * Created by SLUX on 17.05.2017.
 */

public class GameOver extends State {

    private Texture background;
    private static final float DELAY = 1.2f;
    private static float currentTime;
    private Sound win;
    float w, w1;

    public GameOver(GameStateManager gsm) {
        super(gsm);
        background = Util.getTexture(Registry.BACKGROUND);

        win = Gdx.audio.newSound(Gdx.files.internal(Registry.WIN));
        if (TheTogyzQumalaq.bPlaySound) win.play();
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
        currentTime = 0;
        GlyphLayout glyphLayout = new GlyphLayout();
        String item = PlayState.getResult();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), item);
        w = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), PlayState.getGameOverWords());
        w1 = glyphLayout.width;

    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            gsm.set(new MenuState(gsm, TheTogyzQumalaq.POSTFIX));
        }
    }

    @Override
    public void update(float dt) {
        currentTime += dt;
        if (currentTime > DELAY)
            handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        sb.draw(background, 0, 0, TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
        TheTogyzQumalaq.getMainFont().draw(sb, PlayState.getGameOverWords(), (TheTogyzQumalaq.WIDTH - w1) / 2, 350f);
        if (!PlayState.isTurn()) {
            TheTogyzQumalaq.getMainFont().draw(sb, PlayState.getResult(), (TheTogyzQumalaq.WIDTH / 2) - (w / 2), TheTogyzQumalaq.HEIGHT / 2);
        } else {
            TheTogyzQumalaq.getMainFont().draw(sb, PlayState.getResult(), (TheTogyzQumalaq.WIDTH / 2) - (w / 2), TheTogyzQumalaq.HEIGHT / 2);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        win.dispose();
        System.out.println("GameOver Disposed");
    }


}
