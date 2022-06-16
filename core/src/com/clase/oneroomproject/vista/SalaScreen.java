package com.clase.oneroomproject.vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.clase.oneroomproject.Modelo.Machine;
import com.clase.oneroomproject.Modelo.MachineLoader;
import com.clase.oneroomproject.Modelo.Room;
import com.clase.oneroomproject.Modelo.RoomLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private HashMap<String, ArrayList<String>> mcTiendaEnSala;

    public SalaScreen(MainGame game)
    {
        this.game = game;
        batchG = game.getBatch();
        camera = new OrthographicCamera();
        initComponentes();
        addComponentes();
        putComponentes();
        gestionEventos();

        mcTiendaEnSala = new HashMap<>();

        ArrayList<String> mcSotano = new ArrayList<>();
        mcSotano.add("base_raspi_mk1");
        mcSotano.add("test_mach2");
        mcSotano.add("base_raspi_mk1");

        mcTiendaEnSala.put("testRoom", mcSotano);
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);
        marco = new Texture("PruebasAssets/marco.png");
        camera.setToOrtho(false, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
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
    public void initComponentes() {
        skin= game.skin;
        stage = new Stage();

        btnStats = new TextButton("Estadisticas", skin);
        windowStats = new Window("Estadisticas", skin);
        btnTienda = new TextButton("Tienda", skin);
        btnCerrarStats = new TextButton("Cerrar", skin);
        windowTienda = new Window("Tienda", skin);
    }

    @Override
    public void addComponentes() {
        //stage.addActor(btnStats);
        //stage.addActor(btnTienda);
    }

    @Override
    public void putComponentes() {

        btnStats.setSize(100f,20f);
        btnTienda.setSize(100f, 20f);
        windowStats.setSize(800f, 400f);
        windowTienda.setSize(800f, 400f);

        windowStats.setResizable(true);
        windowTienda.setResizable(true);

        HorizontalGroup hGroup = new HorizontalGroup();
        stage.addActor(hGroup);

        hGroup.align(Align.topRight);

        hGroup.setWidth(Gdx.graphics.getWidth());
        hGroup.setHeight(Gdx.graphics.getHeight());

        hGroup.addActor(btnStats);
        hGroup.addActor(btnTienda);

        hGroup.padRight(80);
        hGroup.padTop(10);
        hGroup.space(25);

        hGroup.layout();

        //btnStats.setPosition(800f,((float) Gdx.graphics.getHeight())-btnStats.getHeight()-12f);
        //btnTienda.setPosition(btnStats.getX()+btnStats.getWidth()+15f,btnStats.getY());
        windowStats.setPosition(((float) Gdx.graphics.getWidth()/2f)-(windowStats.getWidth()/2f), ((float) Gdx.graphics.getHeight()/2f)-(windowStats.getHeight()/2f));
        windowTienda.setPosition(windowStats.getX(), windowStats.getY());
    }

    @Override
    public void gestionEventos()
    {
        btnStats.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                btnStats.setDisabled(true);

                windowStats.addActor(btnCerrarStats);
                stage.addActor(windowStats);
                windowStats.setDebug(true, true);

                //Mostramos las estadísticas de la sala.
                Room r = RoomLoader.getInstance().GetCurrentRoom();
                //Guardamos el nombre de los recursos.
                Set<String> nombresRecursos = r.getRecursosOcupados().keySet();

                //Mostramos la información para cada uno de los recursos.
                VerticalGroup group = new VerticalGroup();
                group.align(Align.topLeft);
                group.setFillParent(true);
                group.columnLeft();
                group.moveBy(45, -45);
                windowStats.addActor(group);

                Label roomName = new Label(r.getRoomName(), skin);
                roomName.setFontScale(1.2f);
                group.addActor(roomName);
                group.addActor(new Label("Puntuacion: " + r.getRoomScore(), skin));
                group.addActor(new Label("Dinero por ciclo: " + r.getDineroPorCiclo(), skin));

                group.addActor(new Label("Precio de compra: " + r.getRoomPrice(), skin));

                group.addActor(new Label("Recursos de la sala: ", skin));


                for (String rec : nombresRecursos) {
                    String rs = "   - " + rec + ": " + 5 + "/" + 10;
                    group.addActor(new Label(rs, skin));
                }

                group.validate();
                group.layout();


                btnCerrarStats.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        windowStats.remove();
                        windowStats.clearChildren();
                        btnStats.setDisabled(false);
                    }
                });
            }
        });

        btnTienda.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                /**
                 * Los objetos Window por dentro son tablas (heredan de Table y funcinoan como tablas)
                 * Cada vez que escribes row() terminas una fila
                 */
                stage.addActor(windowTienda);
                btnTienda.setDisabled(true);

                TextButton bttCerrar = new TextButton("Cerrar", skin);
                bttCerrar.align(Align.top);
                Cell<TextButton> btnCerrarCell = windowTienda.add(bttCerrar);

                windowTienda.setDebug(true, true);

                windowTienda.row();

                windowTienda.getTitleLabel().setAlignment(Align.center);
                AddMcToTienda();

                //Indicamos el colspan para el botón de cerrar (para centrarlo).
                btnCerrarCell.colspan(windowTienda.getColumns());

                /**
                 * Evento para cerrar la Window
                 */
                bttCerrar.addListener(new ChangeListener()
                {
                    @Override
                    public void changed(ChangeEvent event, Actor actor)
                    {
                        windowTienda.clearChildren();
                        windowTienda.remove();
                        btnTienda.setDisabled(false);
                    }
                });

            }
        });

        //Utilizado cuando haces click en modo espectral.
        stage.addListener(new ClickListener()
        {

        });
    }

    /**
     * Método para la creación de labels dentro de la Window
     */
    private void AddMcToTienda()
    {
        Room r = RoomLoader.getInstance().GetCurrentRoom();

        //Obtenemos la lista con los nombres de las máquinas que puede haber en esa sala.
        ArrayList<String> mcNameList = mcTiendaEnSala.get(r.getRoomName());

        //Iteramos por las máquinas para añadirlas a la tienda.
        for (int i = 0; i < mcNameList.size(); i++)
        {
            //Obtenemos las máquinas.
            final String mcName = mcNameList.get(i);
            Machine mc = MachineLoader.getInstance().GetMachine(mcName);

            //Obtenemos las texturas de las máquinas.
            //Empezamos obteniendo la lista de tiles.
            String tileSetID = mc.getTileSetID();
            //FIXME: Cambiar el nombre del TileSet por el String.
            TiledMapTileSet tileSet = game.tsm.GetTileSet("tileSetSotanoMc");

            //Obtenemos la textureRegion específica.
            final TextureRegion tx = tileSet.getTile(mc.getTilePos()).getTextureRegion();
            final TextureRegionDrawable drw = new TextureRegionDrawable(tx);

            //Creamos la imagen.
            ImageButton bt = new ImageButton(drw);

            bt.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    ComprarMachine(mcName, tx);
                }
            });

            //Creamos un grupo para la imagen/nombre.
            VerticalGroup group = new VerticalGroup();
            group.align(Align.center);
            group.addActor(bt);
            group.addActor(new Label(mcName, skin));
            group.addActor(new Label(mc.getMachineCost() + "$", skin));

            group.validate();
            group.layout();

            windowTienda.add(group).expandX().expandY();

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


    public void ComprarMachine(String mcName, TextureRegion mcTx)
    {
        //Obtenemos la máquina sobre la que trabajamos.
        Machine mc = MachineLoader.getInstance().GetMachine(mcName);

        //Creamos una nueva ventana para mostrar la información de la máquina, permitir comprar, ...
        Window mcBuyWindow = new Window(mcName, skin);
        mcBuyWindow.setSize(250, 250);
        mcBuyWindow.align(Align.center);

        mcBuyWindow.add(new Label(mcName, skin)).colspan(2);
        mcBuyWindow.row();

        mcBuyWindow.add(new Label(mc.getDineroProducido() + "$ / ciclo", skin));
        mcBuyWindow.add(new Image(mcTx));

        mcBuyWindow.row();

        mcBuyWindow.add(new Label("Recursos: ", skin));

        mcBuyWindow.row();

        String stats = "";

        //Obtenemos las Stats de la máquina.
        HashMap<String, Integer> listaRecusosMC = mc.getAttributes();
        for(Map.Entry<String, Integer> recurso : listaRecusosMC.entrySet())
        {
            stats += recurso.getKey() + ": " + recurso.getValue() + "\n";
        }

        mcBuyWindow.add(new Label(stats, skin));

        mcBuyWindow.row();

        mcBuyWindow.add(new Label("Precio: " + mc.getMachineCost() + "$", skin)).colspan(2);

        mcBuyWindow.row();

        //Botones.
        mcBuyWindow.add(new TextButton("Cerrar", skin));
        mcBuyWindow.add(new TextButton("Comprar", skin));

        mcBuyWindow.layout();

        stage.addActor(mcBuyWindow);

        //Dada una máquina, la pondrá en modo espectral y comenzará el procedimiento de compra.
        Gdx.app.log("SalaScreen", "Se ha comprado la máquina: " + mcName);
    }


}
