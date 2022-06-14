package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.mysql.jdbc.Driver;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Clase estática que realiza todas las interacciones con la BD.
 * Carga los datos de conexión desde un archivo JSON.
 *
 * @author Daniel García.
 */
public class dbConnector
{
    private static Connection connect = null;
    private static dbData datosConexion = null;

    private static final String localDefaultSavePath = "gameData/";
    private static final String defaultFileName = "dbData.json";

    private static final String mensajeErrorGenerico = "No se ha podido interactuar con la BD.";

    /**
     * Inicializa la conexión con la BD.
     * Utiliza los datos de conexión del fichero JSON.
     */
    public static void InitDbConnection()
    {
        //Cargamos los datos de Acceso desde el JSON.
        LoadConnectionDataFromJSON();

        //Comprobamos que están cargados los datos de acceso.
        if(datosConexion == null)
        {
            Gdx.app.error("dbConnector", "No se ha podido establecer conexión con la DB. " +
                    "No hay datos de conexión.");
            return;
        }

        //Realizamos la conexión.
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://" + datosConexion.hostname
                    + ":" + datosConexion.port
                    + "/" + datosConexion.dbName;

            connect = DriverManager.getConnection(url,
                    datosConexion.user,
                    datosConexion.password);

            //Comprobamos si la conexión funciona.
            try
            {
                Statement stt = connect.createStatement();
                stt.execute("use " + datosConexion.dbName + ";");
            }
            catch (Exception e)
            {
                Gdx.app.error("dbConnector", "No ha sido posible comunicarse con la BD.");
                return;
            }

            //CerrarStatement(stt);
        }
        catch (Exception e)
        {
            Gdx.app.error("dbConnector", "No se ha podido cargar el Driver de MySQL.");
        }

    }

    /**
     * Carga los datos desde el archivo JSON.
     * Utiliza la ubicación por defecto definida en las variables de clase.
     */
    private static void LoadConnectionDataFromJSON()
    {
        //Obtenemos el archivo.
        FileHandle file = Gdx.files.local(localDefaultSavePath + defaultFileName);
        if(!file.exists())
        {
            Gdx.app.error("dbConnector", "El archivo de datos de conexión no está disponible.");
            return;
        }

        //Cargamos los datos desde el JSON.
        Json json = new Json();
        datosConexion = json.fromJson(dbData.class, file);
    }

    /*
    FIXME: DEBUG
        Genera un archivo JSON con los datos indicados.
     */
    public static void SaveConnectionDataToJSON()
    {
        dbData data = new dbData();
        data.hostname = "192.168.5.1";
        data.port = "3306";
        data.user = "root";
        data.password = "password";
        data.dbName = "pepeDB";

        Json json = new Json(JsonWriter.OutputType.json);
        String datosString = json.toJson(data);
        FileHandle file = Gdx.files.local(localDefaultSavePath + defaultFileName);
        file.writeString(json.prettyPrint(datosString), false);
    }




    //TODO: ¿Qué necesito? Creo que el ID de partida...
    //  ¿Dónde lo guardo?
    public static void SaveRoomToDB()
    {

    }

}
