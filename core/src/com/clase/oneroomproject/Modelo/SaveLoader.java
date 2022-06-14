package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * Clase estática para la carga/guardado de datos de partida.
 * Estos datos incluyen el dinero y las salas compradas, entre otros.
 * Puede cargar/guardar desde JSON y desde DB.
 *
 * @author Daniel García.
 */
public class SaveLoader
{
    private SaveLoader() {}

    /**
     * Permite comprobar si existe el archivo de datos de partida.
     * @return True si existe el archivo, False si no.
     */
    public static boolean ComprobarExistePartida(FileHandle file)
    {
        //Comprobamos si existe el file.
        if(file.exists())
        {
            return true;
        }
        return false;
    }


    /**
     * Permite cargar los datos de una partida desde un archivo JSON.
     * @param file FileHandle (internal) del JSON de datos.
     * @return El objeto Save que se ha creado.
     */
    public static Save LoadFromJSON(FileHandle file)
    {
        //Comprobamos si existe antes de intentar cargarlo.
        ComprobarExistePartida(file);

        Json json = new Json();
        Gdx.app.debug("SaveLoader", "Se están cargado los datos de guardado desde: " + file);
        return json.fromJson(Save.class, file);
    }

    /**
     * Permite guardar un objeto Save a un JSON.
     * @param saveToSerialize Ptr al objeto Save que se quiere guardar.
     * @param file FileHandle (Escribible, como local) en que guardar el JSON.
     */
    public static void SaveToJSON(Save saveToSerialize, FileHandle file)
    {
        //Creamos y configuramos el serializador.
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);

        String data = json.toJson(saveToSerialize);
        //FileHandle file = Gdx.files.local(path);
        file.writeString(json.prettyPrint(data), false);
        Gdx.app.log("SaveLoader", "Se ha guardado un save en " + file);
    }

}
