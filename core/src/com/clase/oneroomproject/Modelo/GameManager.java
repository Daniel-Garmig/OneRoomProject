package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.Map;

/*
    ToDo:
     - ¿Creamos una función para operar con dinero? De ser así,
        modificar todas las asignaciones manuales.
 */

public class GameManager
{
    private static GameManager instance;

    private final String localSavePath = "saves/";
    private final String localRoomSavePath = "saves/Rooms/";
    private final String internalDataPath = "data/";
    private final String internalRoomDataPath = "data/";

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
     * Realiza el guardado del save y de las salas.
     */
    public void SaveGame()
    {
        SaveLoader.SaveToJSON(saveData, Gdx.files.local(localSavePath + "save.json"));
        rmLoader.SaveAllRoomsToJSON();
    }

    /**
     * Carga todas las salas que el jugador tenga compradas.
     * No activa ninguna de ellas.
     * Todo: Probablemente queramos también autentificar al usuario.
     */
    public void LoadGame()
    {
        SaveLoader.LoadFromJSON(Gdx.files.local(localSavePath + "save.json"));

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
     * Reinicia la sala indicada.
     * Para ello, copia el archivo por defecto a la carpeta de guardado del jugador.
     * Fixme: Quizás en vez de copiar el archivo queramos cargarlo y luego guardarlo...
     * @param roomID ID de la room a reiniciar.
     */
    public void ReiniciarRoom(String roomID)
    {
        FileHandle prefab = Gdx.files.internal(internalDataPath + roomID + ".json");
        FileHandle roomSave = Gdx.files.local(localSavePath + roomID + ".json");
        prefab.copyTo(roomSave);
    }

    public boolean ComprarRoom(String roomID)
    {
        //Cargamos la sala por defecto desde el prefab de la misma.
        rmLoader.LoadRoomFromJSON(Gdx.files.internal(internalRoomDataPath + roomID + ".json"));
        Room rm = rmLoader.GetRoomByID(roomID);

        if(saveData.dinero < rm.roomPrice)
        {
            Gdx.app.log("GameManager", "No tienes dinero para comprar la sala");
            return false;
        }

        //Si se puede comprar, guardamos la sala.
        rmLoader.SaveRoomToJSON(roomID, Gdx.files.local(localRoomSavePath + roomID + ".json"));
        //Cobramos por ella.
        saveData.dinero -= rm.roomPrice;

        //Fixme: Debería devolver true/false en función de cómo haya ido.
        return true;
    }

    public boolean ComprarMachine(Machine mc, int posX, int posY)
    {
        //Comprobamos si tenemos dinero suficiente.
        if(saveData.dinero < mc.machineCost)
        {
            Gdx.app.log("GameManager", "No tienes dinero para comprar la máquina: " + mc.machineID);
            return false;
        }

        //Intentamos añadir la máquina.
        if(rmLoader.GetCurrentRoom().AddMachine(posX, posY, mc))
        {
            return false;
        }

        //Si es correcto, restamos el dinero gastado.
        saveData.dinero -= mc.machineCost;

        return true;
    }





}
