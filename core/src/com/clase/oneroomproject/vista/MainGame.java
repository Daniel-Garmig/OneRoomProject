package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.clase.oneroomproject.Modelo.GameManager;
import com.clase.oneroomproject.Modelo.TileSetManager;

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
	 * Pantalla de logging (Cuando no hay datos del jugador.)
	 */
	LoggingScreen logging;
	/**
	 * Pantalla de la primera vez (cuando no tienes una partida creada).
	 */
	pVezScreen pvScreen;
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
	public void dispose ()
	{
		batch.dispose();
		cargando.dispose();
		menu.dispose();
		online.dispose();
		mapa.dispose();
		logging.dispose();
		salaG.dispose();
	}

	//Getters y Setters

	public SpriteBatch getBatch() {
		return batch;
	}

	private void init()
	{
		try
		{
			batch = new SpriteBatch();
			//Inicialización de las Screen del juego
			cargando = new CargandoScreen(this);
			menu = new MainMenuScreen(this);
			online = new OnlineScreen(this);
			mapa = new MapaScreen(this);
			logging = new LoggingScreen(this);
			salaG = new SalaScreen(this);
			tsm = new TileSetManager();

			LoadGameAssets();

			setScreen(menu);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Carga todas las texturas y datos internos necesarios.
	 */
	private void LoadGameAssets()
	{
		tsm.loadTileSet("PruebasAssets/tiles.png", "tileSetSotanoBg", 128, 128);
		tsm.loadTileSet("PruebasAssets/PruebaMap.png", "tileSetSotanoMc", 8,8);
		gm.mcLoader.LoadFromJSON(Gdx.files.internal("data/testMachines.json"));
	}
}
