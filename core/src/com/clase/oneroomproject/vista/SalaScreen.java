package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.clase.oneroomproject.Modelo.Machine;
import com.clase.oneroomproject.Modelo.Room;
import com.clase.oneroomproject.Modelo.RoomLoader;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.ArrayList;
import java.util.HashMap;

public class SalaScreen implements Screen, StageInterface {

    private MainGame game;
    private OrthographicCamera camera;
    private Texture marco;
    private SpriteBatch batchG;
    private Stage stage;
    private Skin skin;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private TextButton btnStats;
    private TextButton btnTienda;
    /**
     * Window con las estadísticas
     */
    private Window windowStats;
    private Window windowTienda;
    private TextButton btnCerrarStats;
    private TextButton btnCerrarTienda;
    private ArrayList<String> namesMachine;

    public SalaScreen(MainGame game){
        this.game = game;
    }

    @Override
    public void show() {
        marco = new Texture("PruebasAssets/marco.png");
        batchG = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
        namesMachine =  new ArrayList<>();
        //TODO Eliminar ejemplo
        namesMachine.add("Uno");
        namesMachine.add("Dos");
        initComponentes();
        addComponentes();
        putComponentes();
        gestionEventos();
        makeTileMap();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();
        batchG.begin();
        batchG.draw(marco, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batchG.end();
        stage.draw();
        stage.act(Gdx.graphics.getDeltaTime());

    }

    @Override
    public void resize(int width, int height) {

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
    }


    @Override
    public void initComponentes() {
        skin= new Skin(Gdx.files.internal("pruebaSkin/uiskin.json"));
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        btnStats = new TextButton("Estadisticas", skin);
        windowStats = new Window("Estadisticas", skin);
        btnTienda = new TextButton("Tienda", skin);
        btnCerrarStats = new TextButton("Cerrar", skin);
        windowTienda = new Window("Tienda", skin);
        btnCerrarTienda = new TextButton("Cerrar", skin);
    }

    @Override
    public void addComponentes() {
        stage.addActor(btnStats);
        stage.addActor(btnTienda);
    }

    @Override
    public void putComponentes() {
        btnStats.setSize(100f,20f);
        btnTienda.setSize(100f, 20f);
        windowStats.setSize(800f, 400f);
        windowTienda.setSize(800f, 400f);
        btnStats.setPosition(800f,((float) Gdx.graphics.getHeight())-btnStats.getHeight()-12f);
        btnTienda.setPosition(btnStats.getX()+btnStats.getWidth()+15f,btnStats.getY());
        windowStats.setPosition(((float) Gdx.graphics.getWidth()/2f)-(windowStats.getWidth()/2f), ((float) Gdx.graphics.getHeight()/2f)-(windowStats.getHeight()/2f));
        windowTienda.setPosition(windowStats.getX(), windowStats.getY());
    }

    @Override
    public void gestionEventos() {
        btnStats.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                windowStats.addActor(btnCerrarStats);
                stage.addActor(windowStats);

                btnCerrarStats.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        windowStats.remove();
                    }
                });
            }
        });
        btnTienda.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                /**
                 * Los objetos Window por dentro son tablas (heredan de Table y funcinoan como tablas)
                 * Cada vez que escribes row() terminas una fila
                 */
                stage.addActor(windowTienda);
                windowTienda.add(btnCerrarTienda).align(Align.topRight).expandX().expandY().row();
                windowTienda.getTitleLabel().setAlignment(Align.center);
                crearLabels(namesMachine.size(), namesMachine);

                /**
                 * Evento para cerrar la Window
                 */
                btnCerrarTienda.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        windowTienda.remove();
                    }
                });



            }
        });

        stage.addListener(new ClickListener(){
        });
    }
    //TODO Crear un método para recorrer un array de imágenes y crear ImageButtons

    /**
     * Método para la creación de labels dentro de la Window
     * @param numLabels
     */
    private void crearLabels(int numLabels, ArrayList<String> n){
        for (int i = 0; i < numLabels; i++){
            windowTienda.add(new Label(n.get(i), skin)).expandX().expandY();
        }
        windowTienda.row();
    }

    /**
     * Crea el TileMap y lo inicializa.
     */
    public void makeTileMap()
    {
        RoomLoader rmLoader = game.gm.rmLoader;
        Room rm = rmLoader.GetCurrentRoom();
        //Creamos el TileMap.
        map = new TiledMap();
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        //Creamos las layers.
        MapLayers layers = map.getLayers();
        Vector2 rmSize = rm.getRoomSize();

        //BG Layer.
        TiledMapTileLayer bgLayer = new TiledMapTileLayer((int)rmSize.x, (int)rmSize.y, 128, 128);
        bgLayer.setName("bgLayer");
        layers.add(bgLayer);

        //Machine Layer
        TiledMapTileLayer mcLayer = new TiledMapTileLayer((int)rmSize.x, (int)rmSize.y, 128, 128);
        mcLayer.setName("mcLayer");
        layers.add(mcLayer);

        //Shadow Layer
        TiledMapTileLayer shadowLayer = new TiledMapTileLayer((int)rmSize.x, (int)rmSize.y, 128, 128);
        shadowLayer.setName("shadowLayer");
        layers.add(shadowLayer);
        UpdateTileMap();
    }

    /**
     * Actualiza los tiles asignados a cada celda del tilemap.
     */
    public void UpdateTileMap()
    {
        //Obtenemos la información pertinente.
        Room rm = game.gm.rmLoader.GetCurrentRoom();

        Vector2 rmSize = rm.getRoomSize();
        int[][] roomBgData = rm.getBgData();

        MapLayers layers = map.getLayers();

        //Actualizamos las tiles del Background.
        TiledMapTileLayer bgLayer = (TiledMapTileLayer) layers.get("bgLayer");

        String bgTileSetID = rm.getTileSetID();
        TiledMapTileSet bgTileSet = game.tsm.GetTileSet("tileSetSotanoBg");
        for(int y = 0; y < rmSize.y; y++)
        {
            for(int x = 0; x < rmSize.x; x++)
            {
                TiledMapTileLayer.Cell ce = new TiledMapTileLayer.Cell();
                ce.setTile(bgTileSet.getTile(roomBgData[y][x]));
                bgLayer.setCell(x, y, ce);
            }
        }


        //Actualizamos las tiles de las máquinas.
        TiledMapTileLayer mcLayer = (TiledMapTileLayer) layers.get("mcLayer");

        //Obtenemos la lista de máquinas.
        Machine[][] mcData = rm.getMachineData();

        //Damos por supuesto que todas las máquinas de una sala tendrán un mismo tileSet.
        //Lo extraemos de la primera máquina que hay.
        //String mcTileSetID = mcData[0][0].getTileSetID();
        TiledMapTileSet mcTileSet = game.tsm.GetTileSet("tileSetSotanoMc");
        for(int y = 0; y < rmSize.y; y++)
        {
            for(int x = 0; x < rmSize.x; x++)
            {
                //Sacamos la máquina.
                Machine mc = mcData[y][x];
                //Si la máquina no existe, la saltamos (para evitar excepciones).
                if(mc == null)
                {
                    continue;
                }

                TiledMapTileLayer.Cell ce = new TiledMapTileLayer.Cell();
                ce.setTile(mcTileSet.getTile(mc.getTilePos()));
                mcLayer.setCell(x, y, ce);
            }
        }

    }


}
