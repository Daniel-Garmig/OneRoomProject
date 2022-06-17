package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.clase.oneroomproject.Modelo.dbConnector;

public class LoggingScreen implements Screen, StageInterface
{

    private MainGame game;
    private Stage stage;
    private Label lbNick;
    private Label lbPasswd;
    private TextField txtFieldNick;
    private TextField txtFieldPasswd;
    private Skin skin;
    private OrthographicCamera camera;
    private TextButton btnAceptar;
    private TextButton btnCancelar;
    public LoggingScreen(MainGame game)
    {
        this.game = game;
        camera= new OrthographicCamera();
        skin = game.skin;

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
        stage.addActor(txtFieldNick);
        stage.addActor(txtFieldPasswd);
        stage.addActor(lbNick);
        stage.addActor(lbPasswd);
        stage.addActor(btnAceptar);
        stage.addActor(btnCancelar);
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
        lbNick.setPosition(230f-lbNick.getWidth(), 600f);
        lbPasswd.setPosition(230f-lbPasswd.getWidth(), lbNick.getY()-60f);
        txtFieldNick.setPosition(lbPasswd.getX()+(txtFieldNick.getWidth()/2f), lbNick.getY());
        txtFieldPasswd.setPosition(lbPasswd.getX()+(txtFieldPasswd.getWidth()/2f), lbPasswd.getY());
        btnAceptar.setPosition(lbPasswd.getX(), lbPasswd.getY()-60f);
        btnCancelar.setPosition(txtFieldPasswd.getRight()-btnCancelar.getWidth(), btnAceptar.getY());
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
