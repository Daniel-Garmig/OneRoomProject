package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import jdk.internal.misc.TerminatingThreadLocal;
import org.w3c.dom.Text;

import java.io.File;

public class MainMenuScreen implements Screen, StageInterface
{
    /**
     * Dependencia de la clase Game para poder acceder a ella
     */
    private MainGame game;
    /**
     * SpriteBatch de la clase Game
     */
    private SpriteBatch batchG;
    /**
     * Cámara
     */
    private OrthographicCamera camera;
    /**
     * Stage en el que colocar los componentes
     */
    private Stage stage;
    /**
     * Botón para empezar a jugar
     */
    private TextButton btnJugar;
    /**
     * Bóton para llevarte al modo online
     */
    private TextButton btnOnline;
    /**
     * Fondo de la Screen
     */
    private Texture fondo;
    /**
     * Skin para darle formato a todos los objetos de Scene2D
     */
    private Skin skin;

    /**
     * Constructor de la clase
     * @param game Tipo Game
     */
    public MainMenuScreen(MainGame game)
    {
        this.game=game;
        batchG = game.getBatch();
        camera = new OrthographicCamera();

        fondo = new Texture("PruebasAssets/cargandoFondo.jpg");

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
        batchG.draw(fondo, 0, 0);
        batchG.end();
        stage.draw();
        stage.act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height);
    }

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
        btnJugar = new TextButton("Pulsa para jugar", skin);
        btnOnline= new TextButton("Modo Online", skin);
    }

    @Override
    public void addComponentes() {
        stage.addActor(btnJugar);
        stage.addActor(btnOnline);
    }

    @Override
    public void putComponentes() {
        btnOnline.setWidth(250f);
        btnJugar.setWidth(250f);
        btnOnline.setPosition(((float) Gdx.graphics.getWidth()/2f)-(btnOnline.getWidth()/2f), ((float) Gdx.graphics.getHeight())-((float) Gdx.graphics.getHeight()/2f));
        btnJugar.setPosition(((float) Gdx.graphics.getWidth()/2f)-((float) btnJugar.getWidth()/2f), (float) btnOnline.getY()-50f);
    }

    @Override
    public void gestionEventos()
    {
        btnJugar.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                //Si el json de partida no existe entonces cargará por primera vez el juego y creará el json
                if (!game.gm.ComprobarExistePartida())
                {
                    game.setScreen(new pVezScreen(game));
                }else
                {
                    //Cargamos la partida.
                    game.gm.LoadGameFromJSON();
                    game.setScreen(new MapaScreen(game));
                }
            }
        });

        btnOnline.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                game.setScreen(game.online);
            }
        });
    }
}
