package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.clase.oneroomproject.Modelo.SaveLoader;

public class MapaScreen implements Screen, StageInterface {

    private MainGame game;
    private Stage stage;
    private SpriteBatch batchG;
    private OrthographicCamera camera;
    private Texture fondo;

    private Button btnSala1;
    private Button btnSala2;
    private Skin skin;

    public MapaScreen(MainGame game)
    {
        this.game=game;
        batchG = game.getBatch();
        camera = new OrthographicCamera();
        fondo = new Texture("PruebasAssets/mapa.png");
        initComponentes();
        addComponentes();
        putComponentes();
        gestionEventos();
    }

    @Override
    public void show() {
        camera.setToOrtho(false, 0, 0);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batchG.begin();
        batchG.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batchG.end();
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
    public void dispose() {
        stage.dispose();
    }


    @Override
    public void initComponentes() {
        stage = new Stage();
        skin = game.skin;
        btnSala1 = new Button(skin);
        btnSala2 = new Button(skin);
    }

    @Override
    public void addComponentes() {
        stage.addActor(btnSala1);
        stage.addActor(btnSala2);
    }

    @Override
    public void putComponentes() {
        btnSala1.setWidth(20f);
        btnSala2.setWidth(20f);
        btnSala1.setPosition(554f,Gdx.graphics.getHeight()-320f);
        btnSala2.setPosition(651.f, Gdx.graphics.getHeight()-523.0f);
    }

    @Override
    public void gestionEventos()
    {
        //TODO cada btn cargará e iniciará una sala
        //TODO Si un jugador tiene comprada una sala podrá cargar la información al hacer click sobre ella, sino le aparecera un dialog para comprarla

        //Click en el botón de la sala del sótano.
        btnSala1.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                //Comprobamos si tiene comprada la sala.
                if (game.gm.saveData.ownedRooms.get("testRoom"))
                {
                    game.gm.rmLoader.SetCurrentRoom("testRoom");
                    game.setScreen(game.salaG);
                }else
                {
                    //Saldrá el menú para comprar la sala.
                }
            }
        });
    }
}
