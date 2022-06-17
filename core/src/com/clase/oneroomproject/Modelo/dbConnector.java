package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.sql.*;

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

    private static int IDPartida = -1;

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


    /**
     * Permite comprobar si el Nick indicado está disponible en la DB
     * y puede ser utilizado para crear un nuevo usuario.
     * @param nick UserName que se quiere comprobar.
     * @return True si el nick está disponible. False si ya está en uso.
     */
    public static boolean ComprobarNickDisponible(String nick)
    {
        //TODO: Hacer consulta y devolver resultado.
        return true;
    }

    /**
     * Dados los datos de usuario, añade un nuevo usuario a la DB.
     * @param datosUsuario Datos del usuario que se quiere añadir.
     */
    public static void AddNewUser(LoginData datosUsuario)
    {
        //Comprobamos que tenemos datos.
        if(datosUsuario == null)
        {
            Gdx.app.error("dbConnector", "No se puede crear un usuario. " +
                    "Los datos de usuario están vacíos.");
            return;
        }

        //Creamos el Statement.
        Statement stt = CrearStatement();

        //Añadimos el nuevo usuario.
        String query = "INSERT INTO usuarios(nickname,passwd) VALUES ('" + datosUsuario.nickname +
                "','" + datosUsuario.password + "');";
        try
        {
            stt.execute(query);
        } catch (SQLException e)
        {
            Gdx.app.error("dbConnector", "No se ha podido añadir el nuevo usuario: " + datosUsuario.nickname);
        }

        //Al terminar, cerramos el Statement.
        CerrarStatement(stt);
    }

    /**
     * Autentifica al usuario.
     * Comprobará en la BD si los datos (usuario y contraseña) son correctos.
     * @param datosUsuario Datos del usuario a autentificar.
     * @return True si los datos son correctos, false si no.
     */
    public static boolean AutentificarUsuario(LoginData datosUsuario)
    {
        //Comprobamos que los datos de usuario son correctos.
        if(datosUsuario == null)
        {
            Gdx.app.error("dbConnector", "No se puede crear autentificar el usuario. " +
                    "Los datos de usuario están vacíos.");
            return false;
        }

        //Creamos el Statement.
        Statement stt = CrearStatement();

        //Utilizamos el procedimiento de autentificación que tenemos en la BD.
        String query = "SELECT validar('" + datosUsuario.nickname +
                "','" + datosUsuario.password + "');";
        boolean autentificado = false;
        try
        {
            ResultSet rs = stt.executeQuery("query");
            //Obtenemos el resultado de la consulta.
            rs.next();
            autentificado = rs.getBoolean(0);

        } catch (SQLException e)
        {
            Gdx.app.error("dbConnector", "No se ha podido autentificar el usuario " + datosUsuario.nickname);
            return false;
        }

        //Al terminar, cerramos el Statement.
        CerrarStatement(stt);
        return autentificado;
    }

    /**
     * Devuelve el ID de partida del jugador. Utiliza el usuario con el que está autentificado.
     * Sólo se realiza la consulta una vez, luego el valor se guarda.
     * @return ID de partida del jugador.
     */
    public static int GetIDPartidaFromDB()
    {
        //Como siempre es la misma consulta, podemos reutilizar los datos y ahorrarnos la consulta.
        if(IDPartida >= 0)
        {
            return IDPartida;
        }

        //Si no tenemos los datos, hacemos la consulta.
        Statement stt = CrearStatement();

        //Obtenemos el ID de partida.
        String username = LoginSystem.GetUserName();
        
        IDPartida = GetIDPartidaFromDB(username);

        return IDPartida;
    }

    /**
     * Permite obtener el ID de la partida dado un usuario.
     * Este método siempre realiza la consulta para obtener este ID.
     * @param username Nombre del usuario del que obtener la partida.
     * @return ID de la partida del usuario indicado.
     */
    public static int GetIDPartidaFromDB(String username)
    {
        //En este caso, siempre hemos de hacer la consulta.
        //Creamos el Statement.
        Statement stt = CrearStatement();

        //Obtenemos el ID de partida.
        String query = "SELECT id FROM partidas WHERE nickname_usuario='" + username + "';";
        int id = -1;
        try
        {
            ResultSet set = stt.executeQuery(query);
            id = set.getInt(0);
        } catch (SQLException e)
        {
            Gdx.app.error("dbConnector", "No se ha podido obtener el ID de partida.");
        }

        //Cerramos el Statement.
        CerrarStatement(stt);
        return id;
    }


    /**
     * Añade una nueva sala a la partida del jugador en la DB.
     * Es utilizado al comprar una nueva sala.
     * @param roomName Nombre de la sala a añadir.
     */
    public static void AddNewRoomToDB(String roomName)
    {
        //Obtenemos los datos necesarios.
        int idPartida = GetIDPartidaFromDB();
        String roomData = RoomLoader.getInstance().GetRoomJSON(roomName);

        //Comprobamos que se ha obtenido la sala.
        if(roomData.equals(""))
        {
            Gdx.app.error("dbConnector", "No se puede añadir la sala " + roomName +
                    ". No se han podido obtener los datos de la sala desde el RoomLoader.");
            return;
        }

        //Creamos el Statement.
        Statement stt = CrearStatement();

        //Añadimos la nueva sala a la DB.
        String query = "INSERT INTO salas(nombre,id_partida,sala_data) VALUE ('"
                + roomName + "',"
                + idPartida + ",'"
                + roomData + "');";
        try
        {
            stt.execute(query);
        } catch (SQLException e)
        {
            Gdx.app.error("dbConnector", "No se ha podido añadir la nueva sala " + roomName);
        }

        //Al terminar, cerramos el Statement.
        CerrarStatement(stt);
    }

    /**
     * Guarda la nueva partida en la DB.
     * Es utilizado al crear una partida nueva.
     * @param datosPartida Datos de la partida a guardar.
     */
    public static void AddNewSaveDataToDB(Save datosPartida)
    {
        //Obtenemos los datos que necesitamos.
        String username = LoginSystem.GetUserName();
        int puntuacionTotal = GameManager.getInstance().GetPuntuacionTotal();

        //Comprobamos que tenemos los datos de la partida.
        if(datosPartida == null)
        {
            Gdx.app.error("dbConnector", "No se ha podido crear una nueva partida. " +
                    "Los datos de partida están vacíos.");
            return;
        }

        //Creamos el Statement.
        Statement stt = CrearStatement();

        //Añadimos la nueva partida a la DB.
        String query = "INSERT INTO partidas(nickname_usuario,dinero,puntuacion) VALUE ('"
                + username + "',"
                + datosPartida.dinero + ","
                + puntuacionTotal + ");";
        try
        {
            stt.execute(query);
        } catch (SQLException e)
        {
            Gdx.app.error("dbConnector", "No se ha podido crear una nueva partida para el usuario " + username);
        }

        //Al terminar, cerramos el Statement.
        CerrarStatement(stt);
    }


    /**
     * Sube a la DB el Save de la partida y la puntuación total.
     */
    public static void SaveSaveDataToDB(Save datosPartida, int puntuacionTotal)
    {
        //Obtenemos los datos que necesitamos.
        String username = LoginSystem.GetUserName();

        //Comprobamos que tenemos los datos de la partida.
        if(datosPartida == null)
        {
            Gdx.app.error("dbConnector", "No se ha podido actualizar la partida. " +
                    "Los datos de partida están vacíos.");
            return;
        }

        //Creamos el Statement.
        Statement stt = CrearStatement();

        //Actualizamos los datos de la partida.
        String query = "UPDATE partida SET " +
                "dinero=" + datosPartida.dinero + "," +
                "puntuacion=" + puntuacionTotal + "," +
                "where nickname_usuario='" + username + "';";
        try
        {
            stt.execute(query);
        } catch (SQLException e)
        {
            Gdx.app.error("dbConnector", "No se ha podido actualizar los datos de partida para el usuario " + username);
        }

        //Al terminar, cerramos el Statement.
        CerrarStatement(stt);

    }

    
    /**
     * Sube la sala indicada a la BD.
     * @param roomName Nombre de la sala a guardar.
     */
    public static void SaveRoomToDB(String roomName)
    {
        //Obtenemos los datos necesarios.
        int idPartida = GetIDPartidaFromDB();
        String roomJSON = RoomLoader.getInstance().GetRoomJSON(roomName);

        //Comprobamos que se han obtenido los datos de la sala.
        if(roomJSON.equals(""))
        {
            Gdx.app.error("dbConnector", "No se puede guardar la sala " + roomName +
                    ". No se han podido obtener sus datos.");
            return;
        }

        //Creamos el Statement.
        Statement stt = CrearStatement();
        String query = "update salas set sala_data = '" + roomJSON + "' " +
                "where nombre = '" + roomName + "' and " +
                "id_partida = " + idPartida + ";";
        //Subimos la sala a la DB.
        try
        {
            stt.executeUpdate(query);
        } catch (SQLException e)
        {
            Gdx.app.error("dbConnector", "No se ha podido actualizar la sala " + roomName);
        }

        //Al terminar, cerramos el Statement.
        CerrarStatement(stt);
    }


    /**
     * Obtiene los datos de una partida del usuario indicado.
     * @param username Nombre del usuario.
     * @return Datos de la partida del usuario.
     */
    public static Save GetSaveDataFromDB(String username)
    {
        int idPartidaUser = GetIDPartidaFromDB(username);

        //Creamos el statement.
        Statement stt = CrearStatement();

        Save s = new Save();

        //Obtenemos el dinero.
        String queryDinero = "SELECT dinero FROM partidas WHERE " +
                "id = " + idPartidaUser + " AND " +
                "nickname_usuario = '" + username + "';";
        try
        {
            ResultSet rs = stt.executeQuery(queryDinero);
            s.dinero = rs.getInt(0);
        } catch (SQLException e)
        {
            Gdx.app.error("dbConnector", "No se ha podido cargar la partida del usuario " + username);
        }

        //Obtenemos la lista de las salas que tiene el usuario.
        String querySalas = "SELECT nombre FROM salas WHERE " +
                "id_partida = " + idPartidaUser + ";";
        try
        {
            ResultSet rs = stt.executeQuery(querySalas);

            //Iteramos por las salas, añadiéndolas al Save.
            while(rs.next())
            {
                String nombreSala = rs.getString(0);
                //Indicamos que tiene esa sala comprada.
                s.ownedRooms.put(nombreSala, true);
            }

        } catch (SQLException e)
        {
            Gdx.app.error("dbConnector", "No se ha podido cargar la partida del usuario " + username);
        }

        CerrarStatement(stt);

        return s;
    }

    /**
     * Permite cargar la sala del usuario indicado desde DB.
     * @param roomName Nombre de la sala a cargar.
     * @param username Nombre del usuario creador de la sala.
     * @return
     */
    public static String GetRoomDataFromDB(String roomName, String username)
    {
        //Obtenemos los datos necesarios.
        int idPartida = GetIDPartidaFromDB(username);

        //Creamos el Statement.
        Statement stt = CrearStatement();

        //Obtenemos el JSON de la sala desde la DB.
        String datosJSON = "";
        String query = "SELECT sala_data FROM salas WHERE" +
                "nombre = '" + roomName + "' AND " +
                "id_partida = " + idPartida + ";";
        try
        {
            ResultSet rs = stt.executeQuery(query);
            datosJSON = rs.getString(0);
        } catch (SQLException e)
        {
            Gdx.app.error("dbConnector", "No se han podido obtener los datos de la " + roomName +
                    " del usuario " + username);
        }
        CerrarStatement(stt);

        return datosJSON;
    }




    /**
     * Crea un nuevo Statement a partir de la conexión y lo devuelve.
     * @return Statement creado.
     */
    private static Statement CrearStatement()
    {
        Statement stt = null;
        try
        {
            stt = connect.createStatement();
        }
        catch (Exception e)
        {
            System.err.println("No ha sido posible comunicarse con la BD.");
        }
        return stt;
    }

    /**
     * CUIDADO!! Al cerrar Statement, se rompen los datos de ResultSet.
     * @param sttCerrar Statement que se quiere cerrar.
     */
    private static void CerrarStatement(Statement sttCerrar)
    {
        try
        {
            sttCerrar.close();
        }
        catch (Exception e)
        {
            System.err.println("No se ha podido cerrar el Statement.");
        }
    }

}
