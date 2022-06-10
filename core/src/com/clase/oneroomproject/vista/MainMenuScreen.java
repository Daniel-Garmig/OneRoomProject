package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import jdk.internal.misc.TerminatingThreadLocal;
import org.w3c.dom.Text;

import java.io.File;

public class MainMenuScreen implements Screen {
    /**
     * Dependencia de la clase Game para poder acceder a ella
     */
    MainGame game;
    /**
     * SpriteBatch de la clase Game
     */
    SpriteBatch batchG;
    /**
     * Cámara
     */
    OrthographicCamera camera;
    /**
     * Stage en el que colocar los componentes
     */
    Stage stage;
    /**
     * Botón para empezar a jugar
     */
    TextButton btnJugar;
    /**
     * Bóton para llevarte al modo online
     */
    TextButton btnOnline;
    /**
     * Fondo de la Screen
     */
    Texture fondo;
    /**
     * Skin para darle formato a todos los objetos de Scene2D
     */
    Skin skin;
    /**
     * Constructor de la clase
     * @param game Tipo Game
     */
    public MainMenuScreen(MainGame game){
        this.game=game;
    }

    @Override
    public void show() {
        batchG = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 0, 0);
        fondo = new Texture("PruebasAssets\\cargandoFondo.jpg");
        initComponents();
        addComponentes();
        putComponentes();
        gestionEventos();
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
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
    private void initComponents(){
        skin = new Skin(Gdx.files.internal("C:\\Users\\KingAlfy\\IdeaProjects\\OneRoomProject\\OneRoomProject\\assets\\pruebaSkin\\uiskin.json"));
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        btnJugar = new TextButton("Pulsa para jugar", skin);
        btnOnline= new TextButton("Modo Online", skin);
    }
    private void addComponentes(){
        stage.addActor(btnJugar);
        stage.addActor(btnOnline);
    }

    private void putComponentes(){
        btnOnline.setWidth(250f);
        btnJugar.setWidth(250f);
        btnOnline.setPosition(((float) Gdx.graphics.getWidth()/2f)-(btnOnline.getWidth()/2f), ((float) Gdx.graphics.getHeight())-((float) Gdx.graphics.getHeight()/4));
        btnJugar.setPosition(((float) Gdx.graphics.getWidth()/2f)-((float) btnJugar.getWidth()/2f), (float) btnOnline.getY()-50f);
    }
    private void gestionEventos(){
        btnOnline.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //Si el .json no existe entonces cargará por primera vez el juego y creará el json
                //Por el momento siempre pondrá el mapa
                /**
                File f= new File("");

                if (true){
                    game.setScreen(game.online);
                }else{
                    game.setScreen(game.pVez);
                }
                 */
                System.out.println("Funciona");
            }
        });
    }
}
