package com.clase.oneroomproject.vista;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.ScreenUtils;
import com.clase.oneroomproject.Modelo.GameManager;
import com.clase.oneroomproject.Modelo.TileSetManager;

import java.util.concurrent.TimeoutException;

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
	/**
	 * Pantalla de loggin
	 */
	LogginScreen loggin;
	/**
	 * Clase api (GameManager)
	 */
	GameManager gm = GameManager.getInstance();
	/**
	 * Sala genérica que carga los datos del json
	 */
	SalaScreen salaG;
	/**
	 * TileSetManager
	 */
	TileSetManager tsm;
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
			loggin = new LogginScreen(this);
			salaG = new SalaScreen(this);
			tsm = new TileSetManager();
			tsm.loadTileSet("PruebasAssets/tiles.png", "tileSetSotanoBg", 128, 128);
			tsm.loadTileSet("PruebasAssets/PruebaMap.png", "tileSetSotanoMc", 8,8);
			gm.mcLoader.LoadFromJSON(Gdx.files.internal("data/testMachines.json"));
			setScreen(menu);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
