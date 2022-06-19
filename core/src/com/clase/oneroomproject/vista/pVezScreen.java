package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.clase.oneroomproject.Modelo.GameManager;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class pVezScreen implements Screen
{

    private MainGame game;

    private OrthographicCamera camera;

    private Stage stage;
    private Skin skin;

    private SpriteBatch batch;

    private VideoPlayer video;

    private ArrayList<Texture> txList;
    private int currentTxID;

    private TextButton btNext;
    
    public pVezScreen(MainGame game)
    {
        this.game=game;
        batch = game.batch;
        camera = new OrthographicCamera();

        stage = new Stage();
        skin = game.skin;

        //Cargamos el video del juego.
        video = VideoPlayerCreator.createVideoPlayer();

        txList = new ArrayList<>();
        txList.add(new Texture("nuevaPartida/img1.png"));
        txList.add(new Texture("nuevaPartida/img2.png"));
        txList.add(new Texture("nuevaPartida/img3.png"));
        txList.add(new Texture("nuevaPartida/img4.png"));

        currentTxID = 0;
    }

    @Override
    public void show()
    {
        camera.setToOrtho(false, 0, 0);
        Gdx.input.setInputProcessor(stage);

        //Iniciamos el video.
//        try
//        {
//            video.play(Gdx.files.internal("Assets/videoInicial.webm"));
//        } catch (FileNotFoundException e)
//        {
//            CreateDialog("No video", "Ha ocurrido un error al cargar el video", "Ok.");
//        }

        video.setOnCompletionListener(new VideoPlayer.CompletionListener()
        {
            @Override
            public void onCompletionListener(FileHandle file)
            {
                MostrarBotonContinuar();
            }
        });


        btNext = new TextButton("Siguiente", skin);
        btNext.setSize(150, 30);
        btNext.setPosition(((float)Gdx.graphics.getWidth() / 2 - btNext.getWidth() / 2),
                           35);

        stage.addActor(btNext);


        btNext.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {

                if(currentTxID >= 3)
                {
                    MostrarBotonContinuar();
                    btNext.setDisabled(true);
                    return;
                }
                currentTxID++;
            }
        });

    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.begin();
        if(video.isBuffered())
        {
            video.update();
        }
        batch.draw(txList.get(currentTxID), 0, 0);

        batch.end();
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
        video.dispose();
    }


    public void MostrarBotonContinuar()
    {
        //Paramos el video. Por si acaso.
        //video.stop();

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

        btNueva.setSize(150f, 30f);
        btNueva.setPosition(((float)Gdx.graphics.getWidth() / 2 - btNueva.getWidth() / 2),
                            ((float)Gdx.graphics.getHeight() / 2 - btNueva.getHeight() / 2 - 50));

        stage.addActor(btNueva);
    }

    public void CrearNuevaPartida()
    {
        //Se crea una nueva partida...
        GameManager.getInstance().CrearNuevaPartida();
    }

    /**
     * Crea un nuevo diálogo y lo añade al Stage.
     * @param title Título del diálogo.
     * @param text Texto que tiene
     * @param textButton Texto en el botón.
     */
    public void CreateDialog(String title, String text, String textButton)
    {
        Dialog dg = new Dialog(title, skin);
        dg.text(text);
        dg.button(textButton);
        //dg.layout();
        //dg.validate();
        dg.align(Align.center);
        dg.pack();
        dg.setPosition(((float)Gdx.graphics.getWidth()/2) - (dg.getWidth()/2),
                       ((float)Gdx.graphics.getHeight()/2) - (dg.getHeight()/2));
        stage.addActor(dg);
    }
}
