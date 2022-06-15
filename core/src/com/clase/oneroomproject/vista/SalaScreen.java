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
        marco = new Texture("PruebasAssets\\marco.png");
        batchG = game.getBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false,0, 0);
        initComponentes();
        makeTileMap();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
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
        skin= new Skin(Gdx.files.internal("PruebaSkin\\uiskin.json"));
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
    public void makeTileMap(){
        RoomLoader rmLoader = game.gm.rmLoader;
        Room rm = rmLoader.GetCurrentRoom();
        map = new TiledMap();
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        MapLayers layers = map.getLayers();
        Vector2 rmSize = rm.getRoomSize();
        int[][] roomBgData = rm.getBgData();
        TiledMapTileLayer bgLayer = new TiledMapTileLayer((int)rmSize.x, (int)rmSize.y, 8, 8);
        bgLayer.setName("bgLayer");
        String nombre = rm.getRoomName();
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
        layers.add(bgLayer);

    }
}
