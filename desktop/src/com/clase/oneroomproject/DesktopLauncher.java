package com.clase.oneroomproject;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.clase.oneroomproject.vista.MainGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);

		//Título del juego
		config.setTitle("OneRoomProject");
		//Configuraciones de la ventana
		config.setWindowedMode(1240, 720);
		config.setResizable(false);
		new Lwjgl3Application(new MainGame(), config);
	}
}
