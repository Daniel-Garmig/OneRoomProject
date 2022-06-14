package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/*
    ToDo:
     - Comprobar que los archivos (los paths) existen.
 */


/**
 * Carga los datos de las máquinas desde el JSON de datos y almacena los "prefabs" de las mismas.
 *
 * @author Daniel García
 */
public class MachineLoader
{
    private static MachineLoader instance;


    /*-------------
     * Constructors
     * -------------*/

    private MachineLoader()
    {
        machineMap = new HashMap<>();
    }

    public static MachineLoader getInstance()
    {
        if(instance == null)
        {
            instance = new MachineLoader();
        }
        return instance;
    }


    /*-------------
     * Methods
     * -------------*/

    /**
     * Carga una lista de máquinas desde el archivo JSON.
     * @param file Ubicación del JSON (Preferiblemente, FileHandle.internal).
     */
    public void LoadFromJSON(FileHandle file)
    {
        //Comprobamos si existe el archivo de datos.
        if(!file.exists())
        {
            Gdx.app.error("MachineLoader", "No se han podido cargar las máquinas. " +
                    "No existe el archivo: " + file.path());
            return;
        }

        //Cargamos las máquinas en un ArrayList.
        Json json = new Json();
        //FileHandle file = Gdx.files.internal(path);
        ArrayList<Machine> list = json.fromJson(ArrayList.class, file);

        //Iteramos y añadimos las máquinas al mapa identificadas por su ID.
        for(Machine mc : list)
        {
            machineMap.put(mc.getMachineID(), mc);
        }
        Gdx.app.log("MachineLoader", "Se han cargado " + machineMap.size() + " máquinas.");

        //Limpiamos.
        list.clear();
    }

    /**
     * Permite guardar el estado del machineLoader (las máquinas del mapa) a un JSON.
     * Utilizado para debug o herramientas de desarrollo.
     * @param file Ubicación en que guardar el JSON (Tiene que ser un FileHandle escribible.).
     */
    public void SaveToJSON(FileHandle file)
    {
        //Creamos y configuramos el serializador.
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);

        //Obtenemos una lista de las máquinas almacenadas y las convertimos en JSON.
        Collection<Machine> mcList = machineMap.values();
        String jsonData = json.toJson(mcList);

        //Abrimos el archivo y escribimos los datos.
        //FileHandle file = Gdx.files.local(filePath);
        file.writeString(json.prettyPrint(jsonData), false);
        Gdx.app.log("MachineLoader", "Se han guardado " + machineMap.size() + " máquinas" +
                " en " + file);
    }

    /**
     * Añade al mapa la máquina pasada por parámetro. Utiliza el ID de la máquina como Key.
     * @param machineToAdd Máquina que añadir al mapa.
     */
    public void AddMachine(Machine machineToAdd)
    {
        machineMap.put(machineToAdd.machineID, machineToAdd);
    }

    /**
     * Permite obtener un puntero a una máquina dado su ID.
     * @param machineID ID de la máquina a obtener.
     * @return Puntero a la máquina pedida.
     */
    public Machine GetMachine(String machineID)
    {

        Machine mc = machineMap.get(machineID);
        if(mc == null)
        {
            Gdx.app.log("MachineLoader" ,"El mapa no contiene una máquina de ID: " + machineID);
        }
        if(!mc.getMachineID().equals(machineID))
        {
            Gdx.app.error("MachineLoader" , "Hay una inconsistencia en los IDs de máquina: "
                + mc.getMachineID() + "!=" + machineID);
        }

        return mc;
    }



    /*-------------
     * Variables
     * -------------*/
    private HashMap<String, Machine> machineMap;

}
