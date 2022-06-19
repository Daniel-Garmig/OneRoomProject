package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.HashMap;
import java.util.Map;

/*
    ToDo:
     - ¿Creamos una función para operar con dinero? De ser así,
        modificar todas las asignaciones manuales.
 */

public class GameManager
{
    private static GameManager instance;

    private final String localSavePath = "gameData/";
    private final String localRoomSavePath = "gameData/Rooms/";
    private final String internalDataPath = "data/";
    private final String internalRoomDataPath = "data/";


    private final int dineroInicial = 1000;
    private final String roomInicial = "Room_Sotano";

    public MachineLoader mcLoader;
    public RoomLoader rmLoader;
    public Save saveData;

    private GameManager()
    {
        mcLoader = MachineLoader.getInstance();
        rmLoader = RoomLoader.getInstance();

        saveData = new Save();
    }

    public static GameManager getInstance()
    {
        if(instance == null)
        {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * Permite comprobar si existe el archivo de datos de usuario y se puede iniciar sesión.
     * @return True si existe, false si no.
     */
    public boolean ComprobarExisteUsuario()
    {
        return LoginSystem.ComprobarExisteUsuario();
    }

    /**
     * Permite crear un nuevo usuario a partir de los datos introducidos.
     * Se creará el archivo de datos de usuario y se añadirá a la DB.
     * @param username Nombre de usuario.
     * @param pass Contraseña.
     */
    public void CrearNuevoUsuario(String username, String pass)
    {
        LoginSystem.CrearNuevoUsuario(username, pass);
        LoginSystem.AddNewUserToDB();
    }


    /**
     * Utilizado para autentificar un usuario.
     */
    public boolean AutentificarUsuario()
    {
        //Comprobamos que tenemos los datos de usuario.
        if(!ComprobarExisteUsuario())
        {
            Gdx.app.error("GameManager", "No se ha podido autentificar el usuario. " +
                    "Los datos de usuario no existen.");
            return false;
        }
        return LoginSystem.AutentificarUsuario();
    }


    /**
     * Permite comprobar si existe la partida y se puede cargar
     * o hay que comenzar una nueva.
     * @return True si existe, false si no.
     */
    public boolean ComprobarExistePartida()
    {
        return SaveLoader.ComprobarExistePartida(Gdx.files.local(localSavePath + "save.json"));
    }

    /**
     * Realiza todas las acciones necesarias para crear una partida nueva.
     * Crea unos nuevos datos de partida.
     * Reinicia la sala de inicio.
     * Crea el archivo save y el de la sala de inicio.
     * Añade la partida y la sala a la DB.
     */
    public void CrearNuevaPartida()
    {
        //Nos aseguramos que los datos de partida están limpios.
        saveData = null;

        saveData = new Save();
        saveData.dinero = dineroInicial;

        //Indicamos que tiene la sala inicial.
        saveData.ownedRooms = new HashMap<>();
        saveData.ownedRooms.put("Room_Sotano", true);
        saveData.ownedRooms.put("Room_Invernadero", false);
        //Reiniciamos la sala inicial.
        ReiniciarRoom(roomInicial);
        //Cargamos la sala inicial.
        rmLoader.LoadRoomFromJSON(Gdx.files.local(localRoomSavePath + roomInicial + ".json"));

        SaveGameToJSON();

        //Creamos la nueva partida en la DB.
        dbConnector.AddNewSaveDataToDB(saveData);
        //Creamos la nueva sala de inicio en la DB.
        dbConnector.AddNewRoomToDB(roomInicial);
    }


    /**
     * Realiza el guardado del save y de las salas.
     */
    public void SaveGameToJSON()
    {
        SaveLoader.SaveToJSON(saveData, Gdx.files.local(localSavePath + "save.json"));
        rmLoader.SaveAllRoomsToJSON();
    }

    /**
     * Carga todas las salas que el jugador tenga compradas.
     * No activa ninguna de ellas.
     */
    public void LoadGameFromJSON()
    {
        //Limpiamos los datos que pueda haber.
        rmLoader.UnloadAllRooms();
        saveData = null;

        //Comprobamos que existe la partida.
        if(!ComprobarExistePartida())
        {
            Gdx.app.error("GameManager", "No se puede cargar la partida. " +
                    "El archivo de datos no existe.");
            return;
        }

        saveData = SaveLoader.LoadFromJSON(Gdx.files.local(localSavePath + "save.json"));

        //Cargamos desde sus archivos todas las salas que el jugador tenga compradas.
        for(Map.Entry<String, Boolean> rooms : saveData.ownedRooms.entrySet())
        {
            if(rooms.getValue())
            {
                String path = localRoomSavePath + rooms.getKey() + ".json";
                rmLoader.LoadRoomFromJSON(Gdx.files.local(path));
            }
        }
    }

    /**
     * Guarda la partida del usuario en la DB.
     * También guardará/actualizará las salas que tenga compradas.
     * Actualiza también la puntuación total de la partida.
     */
    public void SaveGameToDB()
    {
        //Obtenemos la puntuación total.
        int puntuacionTotal = GetPuntuacionTotal();

        //Actualizamos los datos de la partida en la DB.
        dbConnector.SaveSaveDataToDB(saveData, puntuacionTotal);

        //Actualizamos los datos de las salas que tiene compradas.
        for(Map.Entry<String, Boolean> tieneSala : saveData.ownedRooms.entrySet())
        {
            if(tieneSala.getValue())
            {
                dbConnector.SaveRoomToDB(tieneSala.getKey());
            }
        }
    }

    /**
     * Utilizado para cargar partidas de otros usuarios desde la BD.
     * Se ha de indicar el usuario de la partida que se quiere cargar.
     * @param username Nombre del usuario cuya partida se quiere cargar.
     */
    public void LoadGameFromDB(String username)
    {
        //Descargamos todas las salas que tengamos cargadas.
        RoomLoader.getInstance().UnloadAllRooms();

        //Limpiamos los datos de Save.
        saveData = null;

        //Cargamos el nuevo Save desde la BD.
        saveData = dbConnector.GetSaveDataFromDB(username);

        //Cargamos todas las salas que tenga este save.
        for(Map.Entry<String, Boolean> salas : saveData.ownedRooms.entrySet())
        {
            //Comprobamos si tiene la sala.
            if(salas.getValue())
            {
                //Cargamos las salas desde DB.
                rmLoader.LoadRoomFromDB(salas.getKey(), username);
            }
        }
    }


    /**
     * Reinicia la sala indicada.
     * Para ello, copia el archivo por defecto a la carpeta de guardado del jugador.
     * Quizás en vez de copiar el archivo queramos cargarlo y luego guardarlo...
     * @param roomID ID de la room a reiniciar.
     */
    public void ReiniciarRoom(String roomID)
    {
        FileHandle prefab = Gdx.files.internal(internalDataPath + roomID + ".json");
        FileHandle roomSave = Gdx.files.local(localRoomSavePath + roomID + ".json");
        prefab.copyTo(roomSave);
    }

    /**
     * Realiza la compra de una nueva sala. Para ello:
     *  - Carga la sala por defecto desde el prefab.
     *  - Se comprueba que se tiene dinero.
     *  - Guarda la sala en la ubicación de salas del jugador.
     *  - Resta el dinero que cuesta esta sala.
     *  - Actualiza el Save para indicar que se tiene esa sala.
     * @param roomID Nombre de la sala a comprar.
     * @return True si se ha completado la compra. False si ha ocurrido un error.
     */
    public boolean ComprarRoom(String roomID)
    {
        //Cargamos la sala por defecto desde el prefab de la misma.
        rmLoader.LoadRoomFromJSON(Gdx.files.internal(internalRoomDataPath + roomID + ".json"));
        Room rm = rmLoader.GetRoomByID(roomID);

        //Es necesario cargar la sala primero para saber su precio.
        if(saveData.dinero < rm.roomPrice)
        {
            Gdx.app.log("GameManager", "No tienes dinero para comprar la sala");
            //Si no se ha comprado, deberíamos descargar la sala.
            rmLoader.UnloadRoom(roomID);
            return false;
        }

        //Si se puede comprar, guardamos la sala.
        rmLoader.SaveRoomToJSON(roomID, Gdx.files.local(localRoomSavePath + roomID + ".json"));
        //Cobramos por ella.
        saveData.dinero -= rm.roomPrice;

        //Actualizamos el Save para indicar que se tiene esa sala.
        saveData.ownedRooms.put(roomID, true);

        //Añadimos la sala comprada a la BD (La versión por defecto que tenemos cargada).
        dbConnector.AddNewRoomToDB(roomID);

        return true;
    }

    /**
     * Realiza las operaciones necesarias para comprar una máquina para la sala activa.
     * @param mc Prt a la máquina que queremos añadir.
     * @param posX Posición x para la nueva máquina.
     * @param posY Posición y para la nueva máquina.
     * @return True si la compra se ha completado, false si no.
     */
    public boolean ComprarMachine(Machine mc, int posX, int posY)
    {
        //Comprobamos si tenemos dinero suficiente.
        if(saveData.dinero < mc.machineCost)
        {
            Gdx.app.log("GameManager", "No tienes dinero para comprar la máquina: " + mc.machineID);
            return false;
        }

        //Intentamos añadir la máquina.
        if(!rmLoader.GetCurrentRoom().AddMachine(posX, posY, mc))
        {
            return false;
        }

        //Si es correcto, restamos el dinero gastado.
        saveData.dinero -= mc.machineCost;

        return true;
    }

    /**
     * Realiza la venta de la máquina indicada. Devolverá el 80% del coste original de la máquina.
     * @param mc Datos de la máquina que se quiere vender.
     * @param posX Posición de la Máquina X.
     * @param posY Posición de la Máquina Y.
     * @return True si se ha completado la venta. False si hay fallado algo.
     */
    public boolean VenderMaquina(Machine mc, int posX, int posY)
    {
        //Intentamos vender la máquina.
        if(!rmLoader.GetCurrentRoom().EliminarMaquina(posX, posY))
        {
            return false;
        }

        //Si es correcto, devolvemos el 80% del valor de la máquina.
        saveData.dinero += mc.machineCost * 0.8;

        return true;
    }


    /**
     * Completa un ciclo del juego y el jugador obtiene el dinero de sus máquinas.
     */
    public void CompletarCiclo()
    {
        //Calculamos el dinero obtenido.
        int dineroObtenido = 0;

        //Obtenemos un Ptr al RoomLoader.
        RoomLoader rl = RoomLoader.getInstance();

        //Iteramos por las salas que posee según el save.
        for(Map.Entry<String, Boolean> salas : saveData.ownedRooms.entrySet())
        {
            //Comprobamos si posee esa sala.
            if(salas.getValue())
            {
                int dineroSala = rl.GetRoomByID(salas.getKey()).dineroPorCiclo;
                dineroObtenido += dineroSala;
            }
        }

        //Se itera por las salas y se suma su dinero por ciclo al dinero total.
        saveData.dinero += dineroObtenido;
    }

    /**
     * Devuelve la puntuación total de la partida cargada.
     * @return Puntuación de la partida.
     */
    public int GetPuntuacionTotal()
    {
        int total = 0;

        //Obtenemos la puntuación de cada una de las salas.
        for(Map.Entry<String, Boolean> tieneSala : saveData.ownedRooms.entrySet())
        {
            if(tieneSala.getValue())
            {
                Room r = rmLoader.GetRoomByID(tieneSala.getKey());
                total += r.roomScore;
            }
        }

        return total;
    }

    public int GetDinero()
    {
        return saveData.dinero;
    }
}
