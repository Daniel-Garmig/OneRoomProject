package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.clase.oneroomproject.Modelo.dbConnector;

public class LoggingScreen implements Screen, StageInterface
{

    private MainGame game;
    private SpriteBatch batchG;
    private Texture fondo;
    private Stage stage;
    private Label lbNick;
    private Label lbPasswd;
    private TextField txtFieldNick;
    private TextField txtFieldPasswd;
    private Skin skin;
    private OrthographicCamera camera;
    private TextButton btnAceptar;
    private TextButton btnCancelar;
    private VerticalGroup vGroup;

    public LoggingScreen(MainGame game)
    {
        this.game = game;
        batchG = game.getBatch();
        camera= new OrthographicCamera();
        fondo = new Texture("Assets/fondoMenu.png");

        initComponentes();
        addComponentes();
        putComponentes();
        gestionEventos();
    }

    @Override
    public void show()
    {
        camera.setToOrtho(false, 0, 0);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batchG.begin();
        batchG.draw(fondo, 0, 0);
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
    public void dispose()
    {
        stage.dispose();
    }

    @Override
    public void initComponentes()
    {
        skin = game.skin;
        stage = new Stage();
        ClickListener textFieldClick = new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                super.clicked(event, x, y);
                TextField f = (TextField) event.getListenerActor();
                f.setText("");
            }
        };
        vGroup = new VerticalGroup();
        txtFieldNick = new TextField("Escribe tu nick", skin);
        txtFieldNick.addListener(textFieldClick);

        txtFieldPasswd = new TextField("Escribe tu contraseña", skin);
        txtFieldPasswd.addListener(textFieldClick);

        lbNick = new Label("Nick: ", skin);
        lbPasswd = new Label("Password:", skin);
        btnAceptar = new TextButton("Aceptar", skin);
        btnCancelar = new TextButton("Cancelar", skin);
    }

    @Override
    public void addComponentes()
    {
        stage.addActor(vGroup);
    }

    @Override
    public void putComponentes()
    {
        lbNick.setSize(100f, 30f);
        lbPasswd.setSize(100f, 30f);
        txtFieldNick.setSize(200f, 30f);
        txtFieldPasswd.setSize(200f, 30f);
        btnAceptar.setSize(150f, 30f);
        btnCancelar.setSize(150f, 30f);

        vGroup.align(Align.center);

        vGroup.setWidth(Gdx.graphics.getWidth());
        vGroup.setHeight(Gdx.graphics.getHeight());
        vGroup.space(60f);


        HorizontalGroup hGroupNick= new HorizontalGroup();
        HorizontalGroup hGroupPasswd= new HorizontalGroup();
        HorizontalGroup hGroupBotones= new HorizontalGroup();

        hGroupNick.align(Align.left);
        hGroupPasswd.align(Align.left);
        hGroupBotones.align(Align.left);

        //FIXME: CONTROLAR ESPACIOS
        float widthHGroup =100f;
        hGroupNick.setWidth(widthHGroup);
        hGroupPasswd.setWidth(widthHGroup);
        hGroupBotones.setWidth(widthHGroup);

        hGroupNick.addActor(lbNick);
        hGroupNick.addActor(txtFieldNick);

        hGroupPasswd.addActor(lbPasswd);
        hGroupPasswd.addActor(txtFieldPasswd);

        hGroupBotones.addActor(btnAceptar);
        hGroupBotones.addActor(btnCancelar);

        vGroup.addActor(hGroupNick);
        vGroup.addActor(hGroupPasswd);
        vGroup.addActor(hGroupBotones);


        //FIXME: CONTROLAR ESPACIOS(QUITAR ESTA LINEA)
        vGroup.setDebug(true, true);

    }

    @Override
    public void gestionEventos()
    {
        btnAceptar.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                //Extraemos los datos de los campos.
                String nick = txtFieldNick.getText().trim();
                String pass = txtFieldPasswd.getText().trim();

                Gdx.app.log("Logging", "Username: " + nick + "; pass: " + pass);

                //TODO: Habría que hacer un Regex para comprobar los datos.

                //Comprobamos si el nick está disponible.
                if(nick.matches("[ a-zA-Z0-9]{0,2}") || !dbConnector.ComprobarNickDisponible(nick))
                {
                    Dialog dgNick = new Dialog("Nick no disponible", skin);
                    dgNick.text("El nick indicado no está disponible. \nElija otro");
                    dgNick.button("Ok");
                    stage.addActor(dgNick);
                }

                if(pass.matches("[ a-zA-Z0-9]{0,2}"))
                {
                    Dialog dgPass = new Dialog("Contraseña no válida", skin);
                    dgPass.text("Tiene que indicar una contraseña.");
                    dgPass.button("Ok");
                    stage.addActor(dgPass);
                }

                game.gm.CrearNuevoUsuario(nick, pass);

                //Llevamos a la pantalla de primera vez.
                game.setScreen(new pVezScreen(game));
            }
        });

        //Vuelve al menu.
        btnCancelar.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                txtFieldNick.setText("Escribe tu nick");
                txtFieldPasswd.setText("Escribe tu contrasena");
                game.setScreen(new MainMenuScreen(game));
                //FIXME: Se debería de eliminar la Screen en el SceneManager.
            }
        });
    }
}
