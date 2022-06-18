package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

//TODO: Si no se tiene un usuario, se muestra un diálogo y se dice que primero tiene que crear una cuenta.

public class OnlineScreen implements Screen, StageInterface {
    private MainGame game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batchG;
    private Texture fondo;
    /**
     * Campo de texto donde escribes el ID
     */
    private TextField txtID;
    private TextButton btnAtras;
    private TextButton btnBuscar;

    public OnlineScreen(MainGame game) {
        this.game = game;
        batchG = game.getBatch();
        fondo = new Texture("Assets/fondoMenu.png");
        initComponentes();
        addComponentes();
        putComponentes();
        gestionEventos();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batchG.begin();
        batchG.draw(fondo, 0, 0);
        batchG.end();
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
        skin = game.skin;
        stage = new Stage();
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
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }
}