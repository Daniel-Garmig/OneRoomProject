package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class CargandoScreen implements Screen {
    /**
     * Dependencia de la clase Game para poder acceder a ella
     */
    MainGame game;
    /**
     * SpriteBatch de la clase Game
     */
    SpriteBatch batchG;
    /**
     * Fondo de pantalla de la screen cargando
     */
    Texture carganadoFondo;
    /**
     * Cámara
     */
    OrthographicCamera camera;
    /**
     * Constructor de la clase con la inicialización de las propiedades del objeto
     * @param game Tipo MainGame
     */
    public CargandoScreen(MainGame game) {
        this.game=game;
    }

    @Override
    public void show() {
        batchG = game.getBatch();
        carganadoFondo= new Texture("PruebasAssets\\cargandoFondo.jpg");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 0, 0);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 0);
        //Con el objeto de tipo SpriteBatch dibujamos la imagen de fondo
        batchG.begin();
        batchG.draw(carganadoFondo, 0, 0);
        batchG.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        carganadoFondo.dispose();
    }
}
