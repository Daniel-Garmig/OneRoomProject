package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class OnlineScreen implements Screen, StageInterface {
    private MainGame game;
    private Stage stage;
    private Skin skin;
    /**
     * Campo de texto donde escribes el ID
     */
    private TextField txtID;
    private TextButton btnAtras;
    private TextButton btnBuscar;

    public OnlineScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        initComponentes();
        addComponentes();
        putComponentes();
        gestionEventos();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose()
    {
        stage.dispose();
    }

    @Override
    public void initComponentes()
    {
        skin = new Skin(Gdx.files.internal("pruebaSkin/uiskin.json"));
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        txtID = new TextField("Introduce el ID del jugador:", skin);
        btnAtras = new TextButton("Atras", skin);
        btnBuscar = new TextButton("Buscar", skin);
    }

    @Override
    public void addComponentes()
    {
        stage.addActor(txtID);
        stage.addActor(btnAtras);
        stage.addActor(btnBuscar);
    }

    @Override
    public void putComponentes()
    {
        txtID.setWidth(250f);
        btnBuscar.setWidth(125f);
        btnAtras.setWidth(125f);
        txtID.setPosition(((float) Gdx.graphics.getWidth()/2f)-(txtID.getWidth()/2f), ((float) Gdx.graphics.getHeight())-((float) Gdx.graphics.getHeight()/2f));
        btnBuscar.setPosition((txtID.getX()+btnBuscar.getWidth()), txtID.getY()-txtID.getHeight());
        btnAtras.setPosition(btnBuscar.getX()-btnAtras.getWidth(), btnBuscar.getY());
    }

    @Override
    public void gestionEventos()
    {
        btnAtras.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                game.setScreen(game.menu);
            }
        });
    }
}