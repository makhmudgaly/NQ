package kz.enu.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import kz.enu.TheTogyzQumalaq;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(TheTogyzQumalaq.WIDTH, TheTogyzQumalaq.HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new TheTogyzQumalaq();
        }
}