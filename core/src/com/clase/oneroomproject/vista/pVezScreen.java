package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.clase.oneroomproject.Modelo.GameManager;

public class pVezScreen implements Screen
{

    private MainGame game;

    private OrthographicCamera camera;

    private Stage stage;
    private Skin skin;
    
    public pVezScreen(MainGame game)
    {
        this.game=game;
        camera = new OrthographicCamera();

        stage = new Stage();
        skin = game.skin;

    }

    @Override
    public void show()
    {
        camera.setToOrtho(false, 0, 0);
        Gdx.input.setInputProcessor(stage);

        TextButton btNueva = new TextButton("Crear nueva partida", skin);
        btNueva.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                CrearNuevaPartida();

                //Comenzamos el juego.
                game.setScreen(new MapaScreen(game));
            }
        });

        btNueva.setPosition(250f, 250f);
        btNueva.setSize(150f, 30f);

        stage.addActor(btNueva);

    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        stage.draw();
        stage.act(Gdx.graphics.getDeltaTime());
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
    public void dispose() {}


    public void CrearNuevaPartida()
    {
        //Se crea una nueva partida...
        GameManager.getInstance().CrearNuevaPartida();
    }
}
