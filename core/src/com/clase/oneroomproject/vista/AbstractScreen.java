package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class AbstractScreen implements Screen {
    /**
     * Dependencia de la clase game para poder usarla
     */
    private final MainGame game;
    /**
     * Camara
     */
    private final OrthographicCamera camera;

    public AbstractScreen(MainGame game){
        this.game=game;
        camera=new OrthographicCamera();
        camera.setToOrtho(false, 0, 0);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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

    }
}
