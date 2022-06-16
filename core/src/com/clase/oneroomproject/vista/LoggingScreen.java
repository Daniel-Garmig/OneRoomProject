package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LoggingScreen implements Screen, StageInterface{

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
    public LoggingScreen(MainGame game) {
        this.game = game;
        camera= new OrthographicCamera();
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
        txtFieldNick = new TextField("Escribe tu nick", skin);
        txtFieldPasswd = new TextField("Escribe tu contraseña", skin);
        lbNick = new Label("Nick: ", skin);
        lbPasswd = new Label("Password:", skin);
        btnAceptar = new TextButton("Aceptar", skin);
        btnCancelar = new TextButton("Cancelar", skin);
    }

    @Override
    public void addComponentes() {
        stage.addActor(txtFieldNick);
        stage.addActor(txtFieldPasswd);
        stage.addActor(lbNick);
        stage.addActor(lbPasswd);
        stage.addActor(btnAceptar);
        stage.addActor(btnCancelar);
    }

    @Override
    public void putComponentes() {
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
    public void gestionEventos() {
        btnAceptar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //TODO Comprobar si existe un usuario con ese mismo nick
                //TODO Después de la comprobación envía la información a la BBDD
                //TODO Cambia la Screen a pVez
            }
        });
        btnCancelar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                txtFieldNick.setText("Escribe tu nick");
                txtFieldPasswd.setText("Escribe tu contrasena");
            }
        });
    }
}
