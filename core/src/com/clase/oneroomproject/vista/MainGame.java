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
	AssetManager assetManager;
	
	@Override
	public void create () {
		assetManager=new AssetManager();
		batch = new SpriteBatch();
		cargando = new CargandoScreen(this);
		System.out.println("Antes de cargando");
		setScreen(cargando);
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
}
