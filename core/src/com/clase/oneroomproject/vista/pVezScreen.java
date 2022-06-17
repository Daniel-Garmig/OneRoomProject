package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.clase.oneroomproject.Modelo.GameManager;

public class pVezScreen implements Screen {

    private MainGame game;

    private Stage stage;
    private Skin skin;
    
    public pVezScreen(MainGame game)
    {
        this.game=game;
        stage = new Stage();
        skin = game.skin;
    }

    @Override
    public void show()
    {
        TextButton btNueva = new TextButton("Crear nueva partida", skin);
        btNueva.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                CrearNuevaPartida();
            }
        });

    }

    @Override
    public void render(float delta) {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}


    public void CrearNuevaPartida()
    {
        //Se crea una nueva partida...
        GameManager.getInstance().CrearNuevaPartida();
    }
}
