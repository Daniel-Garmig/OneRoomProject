package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.clase.oneroomproject.Modelo.LoginSystem;
import jdk.internal.misc.TerminatingThreadLocal;
import org.w3c.dom.Text;

import java.io.File;

//TODO: Hay que hacer la autentificación cuando sea necesario.
//  Así evitamos la conexión a la BD si no es necesaria.

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
    private ImageButton btnJugar;
    /**
     * Bóton para llevarte al modo online
     */
    private ImageButton btnOnline;

    private VerticalGroup vGroup;

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

        fondo = new Texture("Assets/fondoMenu.png");

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
        final Texture txOnline = new Texture("Assets/botonOnline.png");
        final Texture txJugar = new Texture("Assets/botonJugar.png");
        final TextureRegionDrawable drwOnline = new TextureRegionDrawable(txOnline);
        final TextureRegionDrawable drwJugar = new TextureRegionDrawable(txJugar);
        vGroup=new VerticalGroup();
        btnJugar =  new ImageButton(drwJugar);
        btnOnline= new ImageButton(drwOnline);
    }

    @Override
    public void addComponentes() {
        stage.addActor(vGroup);
    }

    @Override
    public void putComponentes() {
        vGroup.align(Align.center);
        vGroup.padTop(100f);

        vGroup.setWidth(Gdx.graphics.getWidth());
        vGroup.setHeight(Gdx.graphics.getHeight());

        vGroup.addActor(btnJugar);
        vGroup.addActor(btnOnline);
        vGroup.space(50f);

        vGroup.validate();
        vGroup.layout();

    }

    @Override
    public void gestionEventos()
    {
        btnJugar.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                //Si el json de usuario no existe entonces pedirá al usuario que cree una cuenta.
                if (!game.gm.ComprobarExisteUsuario())
                {
                    game.setScreen(new LoggingScreen(game));

                }
                //Si existe el usuario, pero no la partida, creamos una nueva partida.
                else if (!game.gm.ComprobarExistePartida())
                {
                    //Cargamos el usuario.
                    LoginSystem.LoadFromJSON();
                    if(!game.gm.AutentificarUsuario())
                    {
                        CreateDialog("Error de Autenticación",
                                     "Los datos de inicio de sesión no son válidos.",
                                     "Siendo haber intentado hackearlo ;(");
                        return;
                    }

                    game.setScreen(new pVezScreen(game));
                }else
                {
                    //Cargamos el usuario y autentificamos al usuario.
                    LoginSystem.LoadFromJSON();
                    //Si no está logged, inicia sesión.
                    if(!LoginSystem.IsLogged())
                    {
                        if(!game.gm.AutentificarUsuario())
                        {
                            CreateDialog("Error de Autenticación",
                                         "Los datos de inicio de sesión no son válidos.",
                                         "Siendo haber intentado hackearlo ;(");
                            return;
                        }
                    }

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
                //Comprobamos si existe el usuario.
                if(!game.gm.ComprobarExisteUsuario())
                {
                    //Si no existe, le pedimos que juegue primero.
                    CreateDialog("Aún no puedes",
                                 "Primero tienes que tener tu propia partida" +
                                         "\nCrea una partida antes de entrar al OnLine",
                                 "Ok, lo haré.");
                    return;
                }

                //Cargamos el usuario.
                LoginSystem.LoadFromJSON();

                if(!game.gm.AutentificarUsuario())
                {
                    CreateDialog("Error de Autenticación",
                                 "Los datos de inicio de sesión no son válidos.",
                                 "Siendo haber intentado hackearlo ;(");
                    return;
                }

                game.setScreen(new OnlineScreen(game));
            }
        });
    }

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
