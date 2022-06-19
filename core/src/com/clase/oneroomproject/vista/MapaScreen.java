package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.clase.oneroomproject.Modelo.GameManager;
import com.clase.oneroomproject.Modelo.Room;
import com.clase.oneroomproject.Modelo.RoomLoader;

import java.awt.event.ActionEvent;

public class MapaScreen implements Screen, StageInterface {

    private MainGame game;
    private Stage stage;
    private SpriteBatch batchG;
    private OrthographicCamera camera;
    private Texture fondo;

    private ImageButton btnSotano;
    private ImageButton btnInvernadero;
    private Skin skin;
    private Label lbSotano;
    private Label lbInvernadero;

    public MapaScreen(MainGame game)
    {
        this.game=game;
        batchG = game.getBatch();
        camera = new OrthographicCamera();
        fondo = new Texture("Assets/mapa_sin_casas.png");
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

        final Texture txCasaInvernadero = new Texture("Assets/casa_invernadero.png");
        final Texture txCasaSotano = new Texture("Assets/casa_sotano.png");
        final TextureRegionDrawable drwCasaSotano = new TextureRegionDrawable(txCasaSotano);
        final TextureRegionDrawable drwCasaInvernadero = new TextureRegionDrawable(txCasaInvernadero);

        btnSotano = new ImageButton(drwCasaSotano);
        btnSotano.setName("Sotano");
        lbSotano = new Label("Sotano",skin);
        btnInvernadero = new ImageButton(drwCasaInvernadero);
        btnInvernadero.setName("Invernadero");
        lbInvernadero = new Label("Invernadero",skin);
    }

    @Override
    public void addComponentes() {
        stage.addActor(btnSotano);
        stage.addActor(lbSotano);
        stage.addActor(btnInvernadero);
        stage.addActor(lbInvernadero);
    }

    @Override
    public void putComponentes() {
        //FIXME: añadir VerticalGroup para hacerlo automático
        Vector2 posSotano= new Vector2();
        posSotano.x = 917f;
        posSotano.y = 80f;
        Vector2 posInvernadero= new Vector2();
        posInvernadero.x = 75f;
        posInvernadero.y = 193.0f;
        btnSotano.setPosition(posSotano.x,posSotano.y);
        lbSotano.setPosition(posSotano.x,posSotano.y-lbSotano.getHeight());
        btnInvernadero.setPosition(posInvernadero.x, posInvernadero.y);
        lbInvernadero.setPosition(posInvernadero.x,posInvernadero.y-lbInvernadero.getHeight());
    }

    @Override
    public void gestionEventos()
    {
        /*
        //DEBUG: para ver donde haces click y colocar los botones en el mapa
        stage.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                System.out.println("x: "+x+" y:"+y);
            }

        });
        */

        ChangeListener cL = new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                String nombreSala="Room_"+actor.getName();
                //Comprobamos si tiene comprada la sala.
                if (game.gm.saveData.ownedRooms.get(nombreSala))
                {
                    game.gm.rmLoader.SetCurrentRoom(nombreSala);
                    game.setScreen(new SalaScreen(game));
                }else
                {
                    Gdx.app.log("MapaScreen","Intento comprar "+nombreSala);
                    crearVentanaComprar(nombreSala);

                }
            }
        };

        //Click en el botón de la sala del sótano.
        btnSotano.addListener(cL);
        btnInvernadero.addListener(cL);
    }

    public void crearVentanaComprar(final String nombreSala){
        final Window windowComprar = new Window("Comprar "+nombreSala,skin);

        //FIXME: Codigo para cargar sala copiado de GameManage.ComprarRoom
        RoomLoader rmLoader=RoomLoader.getInstance();
        rmLoader.LoadRoomFromJSON(Gdx.files.internal("data/" + nombreSala + ".json"));
        Label precio = new Label("¿Comprar "+nombreSala+" por "+ RoomLoader.getInstance().GetRoomByID(nombreSala).getRoomPrice()+"?",skin);
        rmLoader.UnloadRoom(nombreSala);

        windowComprar.add(precio).colspan(2);
        windowComprar.row();

        TextButton btnComprar= new TextButton("Comprar",skin);
        TextButton btnAtras= new TextButton("Atras",skin);


        btnComprar.addListener(new ClickListener(){
            @Override
            public void clicked (InputEvent event, float x, float y)
            {
                GameManager.getInstance().ComprarRoom(nombreSala);
            }
        });

        btnAtras.addListener(new ClickListener(){
            @Override
            public void clicked (InputEvent event, float x, float y)
            {
                windowComprar.clearChildren();
                windowComprar.remove();
            }
        });

        windowComprar.add(btnAtras);
        windowComprar.add(btnComprar);
        windowComprar.pack();

        windowComprar.setPosition(((float)Gdx.graphics.getWidth()/2) - (windowComprar.getWidth()/2),
                                  ((float)Gdx.graphics.getHeight()/2)- (windowComprar.getHeight()/2));

        stage.addActor(windowComprar);
        windowComprar.setDebug(true,true);
    }
}
