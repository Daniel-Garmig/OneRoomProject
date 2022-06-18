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
import com.clase.oneroomproject.Modelo.*;

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

    private Room currentRoom;


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
        currentRoom = RoomLoader.getInstance().GetCurrentRoom();
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

                //Obtengo la posición del ratón.
                Vector3 mousePos =  camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0));
                Vector2 mouseTileNum = ScreenToTileMapCoors(mousePos.x, mousePos.y);

                //Comprobamos si estamos en modo compra.
                if(!modoCompra)
                {
                    SeleccionarMC((int)mouseTileNum.x, (int)mouseTileNum.y);
                }
                //Si estamos en modo compra...
                if(modoCompra)
                {
                    RealizarCompraMC((int)mouseTileNum.x, (int)mouseTileNum.y);
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
        //Creamos el TileMap.
        map = new TiledMap();
        mapRenderer = new OrthogonalTiledMapRenderer(map, tileUnitScale);

        //Creamos las layers.
        MapLayers layers = map.getLayers();
        Vector2 rmSize = currentRoom.getRoomSize();

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

        Vector2 rmSize = currentRoom.getRoomSize();
        int[][] roomBgData = currentRoom.getBgData();

        MapLayers layers = map.getLayers();

        //Actualizamos las tiles del Background.
        TiledMapTileLayer bgLayer = (TiledMapTileLayer) layers.get("bgLayer");

        TiledMapTileSet bgTileSet = game.tsm.GetTileSet(currentRoom.getTileSetID());
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
        Machine[][] mcData = currentRoom.getMachineData();

        for(int y = 0; y < rmSize.y; y++)
        {
            for(int x = 0; x < rmSize.x; x++)
            {
                //Sacamos la máquina.
                Machine mc = mcData[y][x];

                //Si la máquina no existe, utilizamos la tile vacía de la NULLMC.
                if(mc == null)
                {
                    mc = MachineLoader.getInstance().GetMachine("NULLMC");
                }
                TiledMapTileSet mcTileSet = game.tsm.GetTileSet(mc.getTileSetID());

                //Obtenemos el TileSet para esa máquina.

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
        tileIndex.x = (float) Math.floor(x / (tileSize.x * tileUnitScale));
        tileIndex.y = (float) Math.floor(y / (tileSize.y * tileUnitScale));

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
        //Guardamos el nombre de los recursos.
        Set<String> nombresRecursos = currentRoom.getRecursosOcupados().keySet();

        //Mostramos la información para cada uno de los recursos.
        VerticalGroup group = new VerticalGroup();
        group.align(Align.topLeft);
        group.setFillParent(true);
        group.columnLeft();
        group.moveBy(45, -45);
        windowStats.addActor(group);

        Label roomName = new Label(currentRoom.getRoomName(), skin);
        roomName.setFontScale(1.2f);
        group.addActor(roomName);
        group.addActor(new Label("Puntuacion: " + currentRoom.getRoomScore(), skin));
        group.addActor(new Label("Dinero por ciclo: " + currentRoom.getDineroPorCiclo(), skin));

        group.addActor(new Label("Precio de compra: " + currentRoom.getRoomPrice(), skin));

        group.addActor(new Label("Recursos de la sala: ", skin));

        //Sabiendo los recursos de la sala, obtenemos los máximos y los ocupados.
        for (String rec : nombresRecursos)
        {
            int recurOcu = currentRoom.getRecursosOcupados().get(rec);
            int recurMax = currentRoom.getRecursosMaximos().get(rec);

            String rs = "   - " + rec + ": " + recurOcu + "/" + recurMax;
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

        //Obtenemos la lista con los nombres de las máquinas que puede haber en esa sala.
        ArrayList<String> mcNameList = mcTiendaEnSala.get(currentRoom.getRoomName());

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
                    MostrarCompraMaquina(mcName, tx);
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
    public void MostrarCompraMaquina(String mcName, TextureRegion mcTx)
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

                    //Realizamos las comprobaciones pertinentes.
                    //Comprobamos si tiene dinero.
                    if(game.gm.GetDinero() < mc.getMachineCost())
                    {
                        CreateDialog("No tienes dinero", "No tienes dinero para comprar esta máquina.", "OK.");
                        return;
                    }
                    //Comprobamos si la sala tiene recursos para esa máquina.
                    if(!currentRoom.ComprobarRecursosParaMaquina(mc))
                    {
                        CreateDialog("No tienes recursos", "No se puede comprar la máquina, \nla sala no tiene recursos suficientes.", "OK.");
                        return;
                    }

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

                }
            });

            mcBuyWindow.add(btnCerrarCompra);
            mcBuyWindow.add(btnComprarMC);

            mcBuyWindow.pack();
            mcBuyWindow.setPosition(((float)Gdx.graphics.getWidth()/2) - (mcBuyWindow.getWidth()/2),
                                    ((float)Gdx.graphics.getHeight()/2) - (mcBuyWindow.getHeight()/2));

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

        //Comprobamos si esa posición está ocupada. De ser así, indicamos que no se puede.
        if(!currentRoom.ComprobarPosicion((int)newTileNum.x, (int)newTileNum.y))
        {
            //Modificamos la tile por la que indica que no se puede poner.
            mcTile = game.tsm.GetTileSet(mcEspectral.getTileSetID()).getTile(1);
        }


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


    /**
     * Ejecutado cuando se está en modo espectral y se hace click.
     * De ser posible, compra la máquina indicada.
     */
    public void RealizarCompraMC(int tileNumX, int tileNumY)
    {
        //Obtenemos la posición del Click

        //Se comprueba si la posición es válida.
        if(!currentRoom.ComprobarPosicion(tileNumX, tileNumY))
        {
            CreateDialog("La posicion no es valida.", "La posicion indicada no es valida. \nVuelva a intentarlo.", "Ok");
            modoCompra = false;
            ClearShadowLayer();
            return;
        }

        //Se realiza la compra.
        Gdx.app.log("SalaScreen", "Tile: " + tileNumX + ", " + tileNumY);

        //Si no se completa la compra, error.
        if(!game.gm.ComprarMachine(mcEspectral, tileNumX, tileNumY))
        {
            CreateDialog("ERROR", "No se ha podido comprar la máquina.\nVuelva a intentarlo mas tarde", "OK.");
        }

        modoCompra = false;
        //Se limpia la capa espectral.
        ClearShadowLayer();

        //Actualizamos el TileMap.
        UpdateTileMap();
    }


    public void SeleccionarMC(final int tileNumX, final int tileNumY)
    {
        //Comprobamos si hay una MC en la posicón indicada.
        if(currentRoom.ComprobarPosicion(tileNumX, tileNumY))
        {
            //Si la posición está vacía, no hay máquina, terminamos.
            return;
        }

        //Obtenemos la MC en esta posición.
        final Machine mc = currentRoom.getMachineData()[tileNumY][tileNumX];

        //Comprobamos si es una NULLMC, pues no se puede seleccionar.
        if(mc.getMachineID().equals("NULLMC"))
        {
            return;
        }

        //Seleccionamos esa máquina (poniendo brillo)
        //Obtenemos la tile con brillo.
        TiledMapTile brilliTile = game.tsm.GetTileSet(mc.getTileSetID()).getTile(mc.getTilePos()+1);
        final TiledMapTile mcTile = game.tsm.GetTileSet(mc.getTileSetID()).getTile(mc.getTilePos());

        //Obtenemos la celda y le cambiamos el brillo.
        TiledMapTileLayer mcLayer = (TiledMapTileLayer) map.getLayers().get("mcLayer");
        final TiledMapTileLayer.Cell c = mcLayer.getCell(tileNumX, tileNumY);

        //Comprobamos si ya está en brillo. En cuyo caso, no tenemos que abrir otra ventana.
        if(c.getTile().equals(brilliTile))
        {
            return;
        }

        c.setTile(brilliTile);


        //Y mostramos la ventana de información.
        final Window mcInfoWindow = new Window(mc.getMachineID(), skin);


        //Mostramos información sobre la máquina.
        Label posLabel = new Label("Position: " + tileNumX + ", " + tileNumY, skin);
        mcInfoWindow.add(posLabel);

        Image mcImage = new Image(mcTile.getTextureRegion());
        mcInfoWindow.add(mcImage);

        mcInfoWindow.row();

        Label dineroLabel = new Label("Dinero/ciclo: " + mc.getDineroProducido(), skin);
        mcInfoWindow.add(dineroLabel);

        mcInfoWindow.row();

        Label vCompraLabel = new Label("Valor Compra: " + mc.getMachineCost(), skin);
        mcInfoWindow.add(vCompraLabel);

        Label vVentaLabel = new Label("Valor Venta: " + mc.getMachineCost() * 0.8f, skin);
        mcInfoWindow.add(vVentaLabel);

        mcInfoWindow.row();

        TextButton cerrarBt = new TextButton("Cerrar", skin);
        mcInfoWindow.add(cerrarBt);

        TextButton eliminarBt = new TextButton("Vender", skin);
        mcInfoWindow.add(eliminarBt);


        cerrarBt.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                //Cambiamo la tile a la normal.
                c.setTile(mcTile);

                //Cerramos la ventana.
                mcInfoWindow.clearChildren();
                mcInfoWindow.remove();
            }
        });

        eliminarBt.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                //Probamos a eliminar la máquina.
                if(!game.gm.VenderMaquina(mc, tileNumX, tileNumY))
                {
                    CreateDialog("No se puede vender", "La máquina indicada no puede ser vendida." +
                            "\nSi es de recursos, comprueba que tienes recusos suficientes", "ok");
                    return;
                }

                //Cerramos la ventana.
                mcInfoWindow.clearChildren();
                mcInfoWindow.remove();

                UpdateTileMap();
            }
        });


        mcInfoWindow.pack();
        stage.addActor(mcInfoWindow);


        //Se comprobará esa posición para ver si hay una máquina.
        // Si la hay, se seleccionará y mostrará la ventana de información.
        //  Y se pondrá la máquina con formato "brilli".
    }

    /**
     * Crea un nuevo diálogo y lo añade al Stage.
     * @param title Título del diálogo.
     * @param text Texto que tiene
     * @param textButton Texto en el botón.
     */
    public void CreateDialog(String title, String text, String textButton)
    {
        Dialog dg = new Dialog(title, skin);
        dg.text(text);
        dg.button(textButton);
        //dg.layout();
        //dg.validate();
        dg.align(Align.center);
        dg.pack();
        dg.setPosition(((float)Gdx.graphics.getWidth()/2) - (dg.getWidth()/2),
                       ((float)Gdx.graphics.getHeight()/2) - (dg.getHeight()/2));
        stage.addActor(dg);
    }
}
