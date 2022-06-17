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
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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


    /*
        UI con cosas.
     */
    private TextButton btnStats;
    private TextButton btnTienda;
    /**
     * Window con las estadísticas
     */
    private Window windowStats;
    private Window windowTienda;

    private HashMap<String, ArrayList<String>> mcTiendaEnSala;

    /*
        Útiles
     */
    public boolean modoCompra = false;

    private Vector2 tileSize;
    private Vector2 tileMapPositionOffset;

    private float tileUnitScale;

    //Almacenará la MC mientras está en periodo de compra / espectral.
    private Machine mcEspectral;
    private Vector2 tileNum;

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
        mcSotano.add("Sotano_Raspi");
        mcSotano.add("Sotano_Mineria");
        mcSotano.add("Sotano_Rack");
        mcSotano.add("Sotano_PC");

        mcTiendaEnSala.put("Room_Sotano", mcSotano);

        tileSize = new Vector2(64, 64);
        tileMapPositionOffset = new Vector2(-46, -41);
        //tileMapPositionOffset = new Vector2(0, 0);
        tileUnitScale = 2f;

        tileNum = new Vector2();
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(stage);
        marco = new Texture("PruebasAssets/marco.png");
        camera.setToOrtho(false, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
        camera.translate(tileMapPositionOffset);
        CreateTileMap();

    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();
        batchG.begin();
        batchG.draw(marco, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batchG.end();
        stage.draw();
        stage.act(Gdx.graphics.getDeltaTime());

        //Modo espectral.
        //Comprobamos si estamos en modo espectral.
        //En caso afirmativo, mostraremos la máquina espectral.
        if(modoCompra)
        {
            RenderModoEspectral();
        }

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
        windowTienda = new Window("Tienda", skin);
    }

    @Override
    public void addComponentes()
    {
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

        //Añadimos los componentes Correspondientes.

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
        btnStats.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                MostrarStats();
            }
        });

        btnTienda.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                MostrarTienda();
            }
        });

        stage.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                super.clicked(event, x, y);

                if(event.isHandled())
                {
                    return;
                }

                Vector2 tileNum = ScreenToTileMapCoors(x, y);

                //Comprobamos si estamos en modo compra.
                if(!modoCompra)
                {
                    //Obtenemos la posición dentro del TileMap que corresponde con el ratón.
                    Gdx.app.log("SalaScreen", "Tile: " + (int)tileNum.x + ", " + (int)tileNum.y);

                    //Se comprobará esa posición para ver si hay una máquina.
                    // Si la hay, se seleccionará y mostrará la ventana de información.
                    //  Y se pondrá la máquina con formato "brilli".
                }
                //Si estamos en modo compra...
                if(modoCompra)
                {
                    //Se comprueba si la posición es correcta.
                    //Se realiza la compra.
                    Gdx.app.log("SalaScreen", "Tile: " + (int)tileNum.x + ", " + (int)tileNum.y);


                    modoCompra = false;
                    //Se limpia la capa espectral.
                    ClearShadowLayer();

                }

            }
        });

        //TODO Me gustaría utilizar el ESC para cancelar la compra de una máquina.
    }


    /**
     * Crea el TileMap y lo inicializa.
     */
    public void CreateTileMap()
    {
        RoomLoader rmLoader = game.gm.rmLoader;
        Room rm = rmLoader.GetCurrentRoom();
        //Creamos el TileMap.
        map = new TiledMap();
        mapRenderer = new OrthogonalTiledMapRenderer(map, tileUnitScale);

        //Creamos las layers.
        MapLayers layers = map.getLayers();
        Vector2 rmSize = rm.getRoomSize();

        //BG Layer.
        TiledMapTileLayer bgLayer = new TiledMapTileLayer((int)rmSize.x, (int)rmSize.y, (int)tileSize.x, (int)tileSize.y);
        bgLayer.setName("bgLayer");
        layers.add(bgLayer);

        //Machine Layer
        TiledMapTileLayer mcLayer = new TiledMapTileLayer((int)rmSize.x, (int)rmSize.y, (int)tileSize.x, (int)tileSize.y);
        mcLayer.setName("mcLayer");
        layers.add(mcLayer);

        //Shadow Layer
        TiledMapTileLayer shadowLayer = new TiledMapTileLayer((int)rmSize.x, (int)rmSize.y, (int)tileSize.x, (int)tileSize.y);
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

        TiledMapTileSet bgTileSet = game.tsm.GetTileSet(rm.getTileSetID());
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

                //Obtenemos el TileSet para esa máquina.
                TiledMapTileSet mcTileSet = game.tsm.GetTileSet(mc.getTileSetID());

                TiledMapTileLayer.Cell ce = new TiledMapTileLayer.Cell();
                ce.setTile(mcTileSet.getTile(mc.getTilePos()));
                mcLayer.setCell(x, y, ce);
            }
        }


        //Añadimos las Celdas con Tiles "vacíos" a la ShadowLayer.
        TiledMapTileLayer shadowLayer = (TiledMapTileLayer) layers.get("shadowLayer");
        //Obtenemos la Tile vacía desde las máquinas.
        TiledMapTile clearTile = game.tsm.GetTileSet(mcData[0][0].getTileSetID()).getTile(0);

        for(int y = 0; y < rmSize.y; y++)
        {
            for(int x = 0; x < rmSize.x; x++)
            {
                TiledMapTileLayer.Cell ce = new TiledMapTileLayer.Cell();
                ce.setTile(clearTile);
                shadowLayer.setCell(x, y, ce);
            }
        }



    }

    /**
     * Limpia la ShadowLayer con tiles "vacías".
     */
    public void ClearShadowLayer()
    {
        //Obtenemos la layer.
        TiledMapTileLayer shadowLayer = (TiledMapTileLayer) map.getLayers().get("shadowLayer");


        //Obtenemos el Tile limpio desde uno de los TileSets.
        TiledMapTile clearTile = game.tsm.GetTileSet(mcEspectral.getTileSetID()).getTile(0);

        //Iteramos por la layer y actualizamos todas las celdas.
        for(int y = 0; y < shadowLayer.getHeight(); y++)
        {
            for(int x = 0; x < shadowLayer.getWidth(); x++)
            {
                //Obtenemos la Cell que hay en esa posición.
                TiledMapTileLayer.Cell c = shadowLayer.getCell(x, y);
                c.setTile(clearTile);
            }
        }

    }


    /**
     * Convierte las coords dadas por parámetro a posición dentro del Tilemap.
     * @param x Posición x.
     * @param y Posición y.
     * @return Vector con la posición dentro del TileMap
     */
    public Vector2 ScreenToTileMapCoors(float x, float y)
    {
        Vector2 tileIndex = new Vector2();
        //Convertimos en función del tamaño de los tiles.
        tileIndex.x = (float) (Math.floor(x) / (tileSize.x * tileUnitScale));
        tileIndex.y = (float) (Math.floor(y) / (tileSize.y * tileUnitScale));

        return tileIndex;
    }


    /**
     * Muestra la ventana con las estadísticas de la sala.
     */
    private void MostrarStats()
    {
        btnStats.setDisabled(true);

        windowStats.setDebug(true, true);

        TextButton btnCerrarStats = new TextButton("Cerrar", skin);
        windowStats.addActor(btnCerrarStats);


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

        stage.addActor(windowStats);


        btnCerrarStats.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                windowStats.remove();
                windowStats.clearChildren();
                btnStats.setDisabled(false);
            }
        });
    }

    /**
     * Muestra la ventana de la tienda.
     */
    private void MostrarTienda()
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

    /**
     * Método para añadir las máquinas comprables a la tienda.
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
            TiledMapTileSet tileSet = game.tsm.GetTileSet(mc.getTileSetID());

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
     * Crea la ventana con los datos de la máquina seleccionada y el botón para comprar.
     * @param mcName Nombre de la máquina.
     * @param mcTx Textura de la máquina.
     */
    public void ComprarMachine(String mcName, TextureRegion mcTx)
    {
            //Obtenemos la máquina sobre la que trabajamos.
            final Machine mc = MachineLoader.getInstance().GetMachine(mcName);

            //Creamos una nueva ventana para mostrar la información de la máquina, permitir comprar, ...
            final Window mcBuyWindow = new Window(mcName, skin);
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
            TextButton btnCerrarCompra = new TextButton("Cerrar", skin);
            //Si se pulsa el botón de cerrar, se cancela la compra.
            btnCerrarCompra.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    mcBuyWindow.remove();
                    mcBuyWindow.clearChildren();
                }
            });

            TextButton btnComprarMC = new TextButton("Comprar", skin);
            //Si se pulsa el botón de comprar, comienza el proceso de compra (espectral).
            btnComprarMC.addListener(new ChangeListener()
            {
                @Override
                public void changed(ChangeEvent event, Actor actor)
                {
                    //event.handle();

                    //Cerramos la ventana.
                    mcBuyWindow.remove();
                    mcBuyWindow.clearChildren();
                    //Cerramos la tienda y reactivamos su botón.
                    windowTienda.remove();
                    windowTienda.clearChildren();
                    btnTienda.setDisabled(false);

                    //Indicamos la máquina a comprar.
                    mcEspectral = mc;
                    modoCompra = true;

                    //TODO: Hay que comprobar si se puede realizar la compra (hay dinero, hay recursos, ...) -> métodos de GM.
                    //  Si no los hay, se muestra un dialog con error.
                }
            });

            mcBuyWindow.add(btnCerrarCompra);
            mcBuyWindow.add(btnComprarMC);

            mcBuyWindow.layout();

            stage.addActor(mcBuyWindow);


            //Dada una máquina, la pondrá en modo espectral y comenzará el procedimiento de compra.
        Gdx.app.log("SalaScreen", "Se ha comprado la máquina: " + mcName);
    }

    public void RenderModoEspectral()
    {

        //Comprobamos la posición del Tilemap sobre la que está el ratón.
        Vector3 mousePos =  camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0));
        Vector2 newTileNum = ScreenToTileMapCoors(mousePos.x, mousePos.y);

        //Si el ratón no se ha movido.
        if(newTileNum.epsilonEquals(tileNum))
        {
            //Podemos terminar por aquí.
            return;
        }

        //Si se ha movido, habrá que cambiar la tile en que se muestra, así como actualizar la posición.

        //Obtenemos los datos necesarios.
        TiledMapTileLayer shadowLayer = (TiledMapTileLayer) map.getLayers().get("shadowLayer");
        //Obtenemos la tile.
        TiledMapTile mcTile = game.tsm.GetTileSet(mcEspectral.getTileSetID()).getTile(mcEspectral.getTilePos());
        //FIXME: Al cambiar de Tile hay que comprobar si la nueva tile está ocupada. De ser así se pondrá la TilePos = 1 en vez de la mc.


        TiledMapTile clearTile = game.tsm.GetTileSet(mcEspectral.getTileSetID()).getTile(0);


        //Modificamos las celdas del TileMap.
        //Limpiamos la antigua.
        TiledMapTileLayer.Cell cOld = shadowLayer.getCell((int)tileNum.x, (int)tileNum.y);
        //Cambiamos la nueva.
        TiledMapTileLayer.Cell cNew = shadowLayer.getCell((int)newTileNum.x, (int)newTileNum.y);

        if(cOld == null || cNew == null)
        {
            return;
        }

        cOld.setTile(clearTile);
        cNew.setTile(mcTile);

        tileNum = newTileNum;
    }


}
