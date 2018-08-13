package kz.enu.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import kz.enu.TheTogyzQumalaq;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = TheTogyzQumalaq.HEIGHT;
		config.width = TheTogyzQumalaq.WIDTH;
		config.title = TheTogyzQumalaq.TITLE;
		//config.fullscreen = true;
		//config.resizable = true;
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		new LwjglApplication(new TheTogyzQumalaq(), config);
	}
}
