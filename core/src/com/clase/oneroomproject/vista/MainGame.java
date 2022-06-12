package com.clase.oneroomproject.vista;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainGame extends Game {
	/**
	 * SpriteBatch para dibujar texturas
	 */
	SpriteBatch batch;
	/**
	 * Pantalla de carga del juego
	 */
	CargandoScreen cargando;
	/**
	 * Menú principal del juego
	 */
	MainMenuScreen menu;
	/**
	 * Pantalla del modo Online
	 */
	OnlineScreen online;
	/**
	 * Mapa principal para elegir la sala a la que quiere ir el jugador
	 */
	MapaScreen mapa;
	AssetManager assetManager;
	
	@Override
	public void create () {
		init();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		super.render(); // IMPORTANTE //SIN ESTO NO FUNCIONA NADA XD
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		cargando.dispose();
	}

	//Getters y Setters

	public SpriteBatch getBatch() {
		return batch;
	}
	private void init(){
		try{
			batch = new SpriteBatch();
			//Inicialización de las Screen del juego
			cargando = new CargandoScreen(this);
			menu = new MainMenuScreen(this);
			online = new OnlineScreen(this);
			mapa = new MapaScreen(this);
			System.out.println("Antes de cargando");
			setScreen(menu);
		}catch (Exception e){
			e.printStackTrace();
		}


	}
}
