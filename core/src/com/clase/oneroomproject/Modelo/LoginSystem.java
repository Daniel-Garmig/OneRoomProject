package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * Clase que realiza el inicio de sesión y autenticación del usuario.
 * Toma los datos desde el archivo JSON y los verifica con la DB.
 * También crea nuevos usuarios y permite saber si el usuario está autentificado.
 *
 * @author Daniel García.
 */
public class LoginSystem
{
    private LoginSystem() {}

    /**
     * Permite autentificar a un usuario dado su archivo de datos.
     * @return True si la autentificación es correcta. False si no.
     */
    public static boolean AutentificarUsuario()
    {

        //Se llamará al procedimiento de la DB.
        //Se le pasarán los datos de datosUsuario.
        //  si datosUsuario = null -> LoadFromJSON();
        return true;
    }

    /**
     * Indica si el usuario se ha autentificado correctamente.
     * @return True si el usuario está autentificado. False si no lo está.
     */
    public static boolean IsLogged() { return logged; }

    public static String GetUserName()
    {
        return datosUsuario.nickname;
    }

    /**
     * Comprueba si existen los datos de inicio de sesión del usuario.
     * @return True si existe el fichero de datos, false si no.
     */
    public static boolean ComprobarExisteUsuario()
    {
        FileHandle file = Gdx.files.local(localDefaultSavePath + defaultFileName);
        if(!file.exists())
        {
            return false;
        }

        return true;
    }

    /**
     * Comprueba en la BD si ese nick ya está ocupado.
     * @param username Nombre de usuario a comprobar.
     * @return True si el nick está disponible, False si está ocupado.
     */
    public static boolean ComprobarNickDisponible(String username)
    {
        return dbConnector.ComprobarNickDisponible(username);
    }

    /**
     * Crea un nuevo usuario, lo guarda en un JSON y lo añade a la BD.
     * @param username Nombre de usuario.
     * @param password Contraseña.
     */
    public static void CrearNuevoUsuario(String username, String password)
    {
        //Creará un nuevo LoginData:
        //  Lo guardará como JSON.
        //  Añadirá el usuario a la DB -> AddUserToDB

        //Comprobamos si el nick está disponible. Si no lo está, cancelamos.
        if(!ComprobarNickDisponible(username))
        {
            Gdx.app.log("LoginSystem", "El nombre " + username + " no está disponible.");
            return;
        }

        //Creamos el usuario.
        datosUsuario = new LoginData();
        datosUsuario.nickname = username;
        datosUsuario.password = password;

        //Lo guardamos al JSON.
        SaveToJSON();

        //Lo añadimos a la DB.
        dbConnector.AddNewUser(datosUsuario);

    }

    /**
     * Carga los datos de usuario desde un JSON de la ubicación por defecto.
     */
    private static void LoadFromJSON()
    {
        //Comprobamos si el archivo existe.
        if(!ComprobarExisteUsuario())
        {
            Gdx.app.error("LoginSystem", "No se puede cargar el archivo de datos de usuario. " +
                    "El Archivo no existe");
            return;
        }

        //Cargamos desde archivo.
        FileHandle file = Gdx.files.local(localDefaultSavePath + defaultFileName);

        Json json = new Json();
        datosUsuario = json.fromJson(LoginData.class, file);
    }

    /**
     * Guarda los datos de usuario a un JSON en la ubicación por defecto.
     */
    private static void SaveToJSON()
    {
        //Creamos el JSON y lo generamos.
        Json json = new Json(JsonWriter.OutputType.json);
        String datos = json.toJson(datosUsuario);

        //Abrimos el archivo y lo escribimos.
        FileHandle file = Gdx.files.local(localDefaultSavePath + defaultFileName);
        file.writeString(json.prettyPrint(datos), false);
    }



    private static boolean logged = false;
    private static LoginData datosUsuario = null;


    private static final String localDefaultSavePath = "gameData/";
    private static final String defaultFileName = "user.json";
}
