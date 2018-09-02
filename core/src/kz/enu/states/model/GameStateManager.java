package kz.enu.states.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Created by SLUX on 17.05.2017.
 */

public final class GameStateManager {

    private Stack<kz.enu.states.model.State> states;

    public GameStateManager(){
        states = new Stack<kz.enu.states.model.State>();
    }

    public void push(kz.enu.states.model.State state){
        states.push(state);
    }

    public void pop(){
        states.pop().dispose();
    }

    public void set(kz.enu.states.model.State state){
        states.pop().dispose();
        states.push(state);
    }

    public void update(float dt){
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb){
        states.peek().render(sb);
    }
}
