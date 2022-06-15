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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.clase.oneroomproject.Modelo.Machine;
import com.clase.oneroomproject.Modelo.Room;
import com.clase.oneroomproject.Modelo.RoomLoader;
import com.sun.org.apache.xpath.internal.operations.Or;

public class SalaScreen implements Screen, StageInterface {

    private MainGame game;
    OrthographicCamera camera;
    Texture marco;
    SpriteBatch batchG;
    Stage stage;
    Skin skin;
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;


    public SalaScreen(MainGame game){
        this.game = game;
    }

    @Override
    public void show() {
        marco = new Texture("PruebasAssets/marco.png");
        batchG = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
        initComponentes();
        makeTileMap();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();
        /*batchG.begin();
        //batchG.draw(marco, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batchG.end();*/
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
    }

    @Override
    public void addComponentes() {

    }

    @Override
    public void putComponentes() {

    }

    @Override
    public void gestionEventos() {

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
