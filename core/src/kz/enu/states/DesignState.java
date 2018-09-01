package kz.enu.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import kz.enu.Registry;
import kz.enu.TheTogyzQumalaq;
import kz.enu.system.Util;

import static kz.enu.Registry.skinsList;
import static kz.enu.TheTogyzQumalaq.botLevel;
import static kz.enu.TheTogyzQumalaq.bPlaySound;

/**
 * Created by SLUX on 27.06.2017.
 */

public class DesignState extends State implements InputProcessor {
    private float alpha;
    private Texture bg;
    private Array<Example> examples = new Array<Example>();
    private PrintWriter pw;
    public static FileHandle fileHandle;
    private static int selectedTheme;
    private static int selectedLanguage;
    private Texture homeTexture;
    private Texture soundTexture;
    private Texture soundOnTexture;
    private Texture soundOffTexture;
    private Texture exampleOutglow;
    private Texture wrapperTexture;
    private int oldX = 0;
    private int newX = 0;

    private static Sprite blackSprite;


    private Rectangle homeRectangle;
    private Rectangle soundRectangle;
    private Rectangle acceptRectangle;
    private Rectangle exampleRectangle;
    private Rectangle backRectangle;
    private Rectangle themeLeftArrowRectangle;
    private Rectangle themeRightArrowRectangle;
    GlyphLayout glyphLayout = new GlyphLayout();
    float wOK,wSkinName,wBack;
    public DesignState(GameStateManager gsm,int selectedLanguage) {
        super(gsm);
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        this.selectedLanguage = selectedLanguage;
        bg = new Texture(Registry.BACKGROUND + TheTogyzQumalaq.POSTFIX+".png");
        fileHandle = Gdx.files.local("profile.txt");
        try {
            pw = new PrintWriter(fileHandle.file());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        selectedTheme = TheTogyzQumalaq.getIndexOfTheme() + 1;
        initTextureArray();

        glyphLayout.setText(TheTogyzQumalaq.getMainFont(),"OK");
        wOK = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), Registry.SKINS_NAME[selectedLanguage][selectedTheme]);
        wSkinName = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(),TheTogyzQumalaq.LOCALE[17]);
        wBack = glyphLayout.width;
        homeTexture = new Texture(Registry.HOME + TheTogyzQumalaq.POSTFIX+".png");
        soundOnTexture = new Texture(Registry.SOUND + TheTogyzQumalaq.POSTFIX+".png");
        exampleOutglow = new Texture(Registry.EXAMPLE_OUTGLOW);
        soundOffTexture = new Texture(Registry.SOUND_OFF + TheTogyzQumalaq.POSTFIX+".png");
        wrapperTexture = new Texture(Registry.WRAPPER + TheTogyzQumalaq.POSTFIX + ".png");
        soundTexture = TheTogyzQumalaq.bPlaySound ?soundOnTexture:soundOffTexture;
        homeRectangle = new Rectangle(812f,465f,homeTexture.getWidth()+20,homeTexture.getHeight()+20);
        soundRectangle = new Rectangle(812f,400f,soundTexture.getWidth()+20,soundTexture.getHeight()+20);
        acceptRectangle = new Rectangle(754f,30f,wOK+100f,70f);
        exampleRectangle = new Rectangle(175f,135f,exampleOutglow.getWidth(),exampleOutglow.getHeight());
        backRectangle = new Rectangle(0f,30f,wBack+20f,70f);
        blackSprite = new Sprite(Util.getTexture(Registry.BLACK_BG, ""));
        alpha = 1f;
        blackSprite.setAlpha(alpha);
        blackSprite.setPosition(0,0);


        themeLeftArrowRectangle = new Rectangle(100f,260f,63f,70f);
        themeRightArrowRectangle = new Rectangle(740f,260f,63f,70f);
        camera.setToOrtho(false, TheTogyzQumalaq.WIDTH,TheTogyzQumalaq.HEIGHT);

    }

    private void initTextureArray(){

        for(int i = 0; i< skinsList.length; i++){
            //if(i%4==0){y = 540f;j++;}
            examples.add(new Example(175f,135f,new Texture("example_"+skinsList[i]+".png")));
        }
    }
    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()){
            Vector3 tmp = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
            camera.unproject(tmp);

            if(acceptRectangle.contains(tmp.x,tmp.y)){
                accept();
            }else if(backRectangle.contains(tmp.x,tmp.y)){
                if(TheTogyzQumalaq.bPlaySound)TheTogyzQumalaq.getButtonSound().play();
                gsm.set(new SettingState(gsm));}
            else if(themeLeftArrowRectangle.contains(tmp.x,tmp.y)){
                if(selectedTheme>0)selectedTheme--;
                else selectedTheme = skinsList.length-1;
                if(TheTogyzQumalaq.bPlaySound)TheTogyzQumalaq.getButtonSound().play();
            }else if(themeRightArrowRectangle.contains(tmp.x,tmp.y)){
                if(selectedTheme<skinsList.length-1)selectedTheme++;
                else selectedTheme = 0;
                TheTogyzQumalaq.getButtonSound().play();
            }else if(homeRectangle.contains(tmp.x,tmp.y)){
                gsm.set(new MenuState(gsm,TheTogyzQumalaq.POSTFIX));
            }else if(soundRectangle.contains(tmp.x,tmp.y)){
                if(bPlaySound){
                    bPlaySound = false;
                    TheTogyzQumalaq.getBackgroundMusic().pause();
                    soundTexture = soundOffTexture;
                }else {
                    bPlaySound = true;
                    TheTogyzQumalaq.getBackgroundMusic().play();
                    soundTexture = soundOnTexture;
                }
            }
        }
    }

    private void accept(){
        TheTogyzQumalaq.getBackgroundMusic().pause();
        TheTogyzQumalaq.getBackgroundMusic().dispose();
        pw.println(skinsList[selectedTheme]);
        pw.println(TheTogyzQumalaq.bPlaySound);
        pw.println(selectedLanguage);
        pw.println(botLevel);
        pw.println((int)(TheTogyzQumalaq.getBackgroundMusic().getVolume()*32));
        pw.flush();
        new TheTogyzQumalaq().reboot();
        gsm.set(new MenuState(gsm,TheTogyzQumalaq.POSTFIX));
    }

    @Override
    public void update(float dt) {
        if(alpha>0) {
            alpha-=0.08f;
            blackSprite.setAlpha(alpha);
        }
        glyphLayout.reset();
        glyphLayout.setText(TheTogyzQumalaq.getMainFont(), Registry.SKINS_NAME[selectedLanguage][selectedTheme]);
        wSkinName = glyphLayout.width;
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(bg,0,0, TheTogyzQumalaq.WIDTH,TheTogyzQumalaq.HEIGHT);
        sb.draw(wrapperTexture,734f,33f);
        sb.draw(wrapperTexture,wBack+40f-wrapperTexture.getWidth(),33f);
        //bitmapFont.draw(sb,"In development process. . .\nTouch to go back",TheTogyzQumalaq.WIDTH/2,TheTogyzQumalaq.HEIGHT/2);
            sb.draw(examples.get(selectedTheme).texture,examples.get(selectedTheme).x,examples.get(selectedTheme).y);
        examples.get(selectedTheme).glow(sb,exampleOutglow);
        TheTogyzQumalaq.getSecondaryFont().draw(sb, Registry.LEFT_ARROW,110f,320f);
        TheTogyzQumalaq.getSecondaryFont().draw(sb, Registry.RIGTH_ARROW,750f,320f);
        TheTogyzQumalaq.getMainFont().draw(sb, Registry.SKINS_NAME[selectedLanguage][selectedTheme],(TheTogyzQumalaq.WIDTH-wSkinName)/2,90f);
        TheTogyzQumalaq.getMainFont().draw(sb,"OK",754,90f);
        TheTogyzQumalaq.getMainFont().draw(sb,TheTogyzQumalaq.LOCALE[17],20f,90f);
        sb.draw(homeTexture,822f,475f);
        sb.draw(soundTexture,822f,410f);
        if(alpha>0) {
            blackSprite.draw(sb);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        for(int i = 0;i<skinsList.length;i++)examples.get(i).texture.dispose();
        pw.close();
        bg.dispose();
        homeTexture.dispose();
        soundOffTexture.dispose();
        soundOnTexture.dispose();
        soundTexture.dispose();
        wrapperTexture.dispose();
        exampleOutglow.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            // Optional back button handling (e.g. ask for confirmation)
            gsm.set(new MenuState(gsm,TheTogyzQumalaq.POSTFIX));
            /*if (shouldReallyQuit)
                Gdx.app.exit();*/
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.ESCAPE){
            this.dispose();
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
        oldX=screenX;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        newX = screenX;
        Vector3 tmp = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
        camera.unproject(tmp);
        if(exampleRectangle.contains(tmp.x,tmp.y))
            if(oldX<newX){
                if(selectedTheme>0)selectedTheme--;
                else selectedTheme = skinsList.length-1;
                oldX=screenX;
            }else if(oldX>newX){
                if(selectedTheme<skinsList.length-1)selectedTheme++;
                else selectedTheme = 0;
                oldX=screenX;
            }
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

class Example{
    public Texture texture;
    public Rectangle rectangle;
    public float x;
    public float y;
    public Example(float x,float y,Texture texture){
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.rectangle = new Rectangle(x,y,texture.getWidth(),texture.getHeight());
    }

    public void glow(SpriteBatch sb,Texture glowTexture){
        sb.draw(glowTexture,this.x-7,this.y-7);
    }
}