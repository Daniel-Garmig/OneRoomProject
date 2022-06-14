package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.HashMap;
import java.util.Map;

/*
    ToDo:
     - Comprobar que los archivos (los paths) existen.
 */


/**
 * Clase encargada de gestionar las diferentes salas:
 *      Cargarlas, guardarlas, almacenarlas, ...
 * Cada sala está guardada en su propio JSON.
 *
 * @author Daniel García.
 */
public class RoomLoader
{
    private static RoomLoader instance;

    public static RoomLoader getInstance()
    {
        if (instance == null)
        {
            instance = new RoomLoader();
        }
        return instance;
    }

    private RoomLoader()
    {
        currentRoom = null;
        loadedRooms = new HashMap<>();

        mcLoader = MachineLoader.getInstance();

        loaderJson = new Json();
        loaderJson.setOutputType(JsonWriter.OutputType.json);
        loaderJson.setSerializer(Room.class, roomSerializer);

    }


    /**
     * Carga una Room desde un archivo.
     * @param file Ubicación del archivo JSON de datos de la Room. (Un FileHandle, preferiblemente, internal.)
     */
    public void LoadRoomFromJSON(FileHandle file)
    {
        //Comprobamos si existe el archivo...
        if(!file.exists())
        {
            Gdx.app.error("RoomLoader", "No existe el archivo: " + file.path());
            return;
        }

        Room r = loaderJson.fromJson(Room.class, file);

        loadedRooms.put(r.roomName, r);

        Gdx.app.log("RoomLoader", "Se ha cargado la Room " + r.roomName + " correctamente.");
    }

    /**
     * Guarda la Room indicada al archivo indicado.
     * @param roomID ID de la Room a guardar.
     * @param file Ubicación del archivo JSON de destino. (FileHandle en que se pueda escribir).
     */
    public void SaveRoomToJSON(String roomID, FileHandle file)
    {
        String data = GetRoomJSON(roomID);
        //FileHandle file = Gdx.files.local(path);
        file.writeString(loaderJson.prettyPrint(data), false);

        Gdx.app.log("RoomLoader", "Se ha guardado la room " + roomID + " en " + file);
    }

    /**
     * Guarda en JSON todas las salas cargadas.
     */
    public void SaveAllRoomsToJSON()
    {
        for(Room r : loadedRooms.values())
        {
            FileHandle file = Gdx.files.local("save/" + r.roomName + ".json");
            SaveRoomToJSON(r.roomName, file);
        }
    }


    /**
     * Carga la sala del usuario indicado desde la BD.
     * @param roomName ID de la sala que se quiere cargar.
     * @param username Usuario dueño de esa sala.
     */
    public void LoadRoomFromDB(String roomName, String username)
    {
        //Obtenemos los datos de la sala desde la DB en formato JSON.
        String jsonData = dbConnector.GetRoomDataFromDB(roomName, username);

        //Cargamos la sala a partir de los datos.
        Room r = loaderJson.fromJson(Room.class, jsonData);
        r.esOnline = true;

        //Añadimos la nueva sala a las salas cargadas.
        loadedRooms.put(r.roomName, r);

        Gdx.app.log("RoomLoader", "Se ha cargado la Room " + r.roomName + " correctamente.");
    }


    /**
     * Devuelve los datos de la sala en formato JSON.
     * @param roomName Nombre de la sala a obtener.
     * @return Datos de la sala indicada en formato JSON.
     */
    public String GetRoomJSON(String roomName)
    {
        Room rSave = loadedRooms.get(roomName);
        if(rSave == null)
        {
            Gdx.app.error("RoomLoader", "No es posible guardar la Room: " + roomName +
                    "; ¿Está cargada?");
            return "";
        }

        String data = loaderJson.toJson(rSave);
        return data;
    }


    /**
     * Descarga una Room dado su ID.
     * @param roomID ID de la Room a descargar.
     */
    public void UnloadRoom(String roomID)
    {
        loadedRooms.remove(roomID);
        Gdx.app.log("RoomLoader", "Se ha descargado (supuestamente) la Room: " + roomID);
        //¿Habría que matar al objeto manualmente?
        //¿Quizás debamos hacerlo un recurso de LibGDX para poder matarlo?
        // En ningún momento estamos comprobando que se haya eliminado correctamente.
    }

    /**
     * Descarga todas las salas que tenga.
     */
    public void UnloadAllRooms()
    {
        //Iteramos por todas las salas, descargándolas.
        for(Room r : loadedRooms.values())
        {
            UnloadRoom(r.roomName);
        }
        //También podríamos ejecutar directamente un
        //loadedRooms.clear();
    }


    /**
     * Permite saber si una Room está cargada.
     * @param roomID ID de la Room a comprobar.
     * @return True si está cargada, False si no lo está.
     */
    public boolean isRoomLoader(String roomID)
    {
        return loadedRooms.containsKey(roomID);
    }

    /**
     * Establece la Room activa.
     * La Room debe estar cargada para poder ser la activa.
     * Si la Room a activar no está cargada, no se cambia la Room activa.
     * @param roomID ID de la Room Activa.
     */
    public void SetCurrentRoom(String roomID)
    {
        Room r = loadedRooms.get(roomID);
        if(r == null)
        {
            Gdx.app.error("RoomLoader", "No se puede activar la Room " + roomID + " ; No está cargada.");
            return;
        }
        currentRoom = r;
    }

    /**
     * Devuelve la Room actualmente activa.
     * @return Ptr a la Room actualmente activa.
     */
    public Room GetCurrentRoom()
    {
        return currentRoom;
    }

    /**
     * Devuelve la Room con el RoomID indicado.
     * @param roomID ID de la Room que se quiere.
     * @return La room solicitada.
     */
    public Room GetRoomByID(String roomID)
    {
        return loadedRooms.get(roomID);
    }


    //FIXME DEBUG: Para hacer pruebas desde BackEndTest.java.
    public void AddRoom(Room rToAdd)
    {
        loadedRooms.put(rToAdd.roomName, rToAdd);
    }


    /*-------------
     * Variables
     * -------------*/
    Room currentRoom;
    HashMap<String, Room> loadedRooms;

    MachineLoader mcLoader;

    Json loaderJson;

    Json.Serializer<Room> roomSerializer = new Json.Serializer<Room>()
    {
        @Override
        public void write(Json json, Room object, Class knownType)
        {
            //Crearlo como un objeto puede venir bien si lo vamos a tener junto a otros datos externos.
            //  Hay que tener cuidado no rompamos el Read.
            json.setOutputType(JsonWriter.OutputType.json);

            json.writeObjectStart();

            //Guardamos datos de la sala.
            json.writeValue("RoomName", object.roomName);
            json.writeValue("RoomSize", object.roomSize);
            json.writeValue("RoomPrice", object.roomPrice);

            json.writeValue("HuecosDisponibles", object.espacioTotal);

            //Serializamos los atributos/capacidad base de la sala.
            json.writeValue("CapacidadPorDefecto", object.recursosDefecto, HashMap.class, Integer.class);

            //Almacenamos los datos del background.
            json.writeValue("BackGroundTileSet", object.tileSetID);
            json.writeValue("BackgroundData", object.bgData);

            //Datos de máquinas. Creamos un JSON array bidimensional con el ID de la máquina.
            json.writeArrayStart("MaquinasData");
            for(int y = 0; y < object.machineData.length; y++)
            {
                json.writeArrayStart();
                for(int x = 0; x < object.machineData[y].length; x++)
                {
                    //Almacenamos el ID de la máquina.
                    Machine mc = object.machineData[y][x];
                    if(mc == null)
                    {
                        //Si no hay máquina será un Null.
                        json.writeValue(null);
                    } else
                    {
                        json.writeValue(mc.machineID);
                    }
                }
                json.writeArrayEnd();
            }
            json.writeArrayEnd();

            json.writeObjectEnd();

            //En principio, los recursos ocupados/disponibles los podemos reconstruir al cargar a
            //  partir de las máquinas que haya en la sala.
            //  Quizás habría que crear un método en Room que hiciera eso.

        }

        @Override
        public Room read(Json json, JsonValue jsonData, Class type)
        {
            //Instanciamos la nueva sala y modificaremos sus variables.
            Room room = new Room();
            //jsonData = jsonData.child();
            room.roomName = jsonData.get("RoomName").asString();

            //Cargamos Tamaño y huecos de la sala.
            room.roomSize = new Vector2();
            room.roomSize.x = jsonData.get("RoomSize").get("x").asFloat();
            room.roomSize.y = jsonData.get("RoomSize").get("y").asFloat();

            room.roomPrice = jsonData.get("RoomPrice").asInt();

            room.espacioTotal = jsonData.get("HuecosDisponibles").asInt();

            //Cargamos la capacidad por defecto de la sala.
            room.recursosDefecto = new HashMap<>();

            for(JsonValue recurso : jsonData.get("CapacidadPorDefecto"))
            {
                room.recursosDefecto.put(recurso.name, recurso.asInt());
            }

            //Inicializamos los otros hashmaps de recursos.
            //Inicialmente, los recursos máximos son una copia de los recursos por defecto.
            room.recursosMaximos = new HashMap<>(room.recursosDefecto);

            //Inicialmente, los recursos ocupados tienen el valor 0.
            //  Totamos los nombres de los por defecto, pero su valor será 0.
            room.recursosOcupados = new HashMap<>();
            for(Map.Entry<String, Integer> recurso : room.recursosDefecto.entrySet())
            {
                room.recursosOcupados.put(recurso.getKey(), 0);
            }


            room.tileSetID = jsonData.get("BackGroundTileSet").asString();
            //ToDo: En principio, bgData debería ser el tamaño de la sala (el grid de máquinas) + 2 (para paredes).
            room.bgData = new int[(int)room.roomSize.y][(int)room.roomSize.x];
            for(int y = 0; y < room.bgData.length; y++)
            {
                room.bgData[y] = jsonData.get("BackgroundData").get(y).asIntArray();
            }

            /*
            Cargamos los datos relativos a las máquinas.
            Únicamente guardamos su ID, pues lo que nos interesa guardar en la sala es un ptr.
                Las máquinas reales viven dentro del mcLoader.
             */
            room.machineData = new Machine[(int)room.roomSize.y][(int)room.roomSize.x];
            //room.machineData_Test = new String[(int)room.roomSize.y][(int)room.roomSize.x];
            //ToDO: Revisar si el bucle de JsonValue.java es más eficiente.
            for (int y = 0; y < room.machineData.length; y++)
            {
                //Obtenemos el array de esta "y".
                JsonValue yData = jsonData.get("MaquinasData").get(y);

                for(int x = 0; x < room.machineData[y].length; x++)
                {
                    String mcName = yData.get(x).asString();

                    //En caso de que no haya máquina (en el JSON haya null) -> Establecemos Null.
                    // TODO: Recuerda que los null de JSON devuelven un objeto null.
                    if(mcName == null)
                    {
                        //Nos podemos saltar la asignación... (Java por defecto establece null al crear el Array).
                        room.machineData[y][x] = null;
                        continue;
                    }

                    //Obtenemos un ptr a la máquina que corresponda.
                    // TODO: Revisar y utilizar los métodos de Room para añadir máquinas.
                    // Si lo añadimos con el método, se puede romper si no hay recursos suficiente por el orden.
                    // Hay que hacer un método concreto.

                    //Obtenemos la máquina.
                    Machine mc = mcLoader.GetMachine(mcName);
                    //Añadimos su ptr en la posición.
                    room.machineData[y][x] = mc;

                    //Añadimos los recursos a la sala. No se rompe nada pues no se comprueba que sean válidos.
                    room.ModificarRecursosPorMaquina(mc, true);

                    //Indicamos que se ha ocupado un hueco.
                    room.espacioOcupado += 1;

                    //Añadimos el dinero que generará esa máquina y la puntuación que da.
                    room.dineroPorCiclo += mc.dineroProducido;
                    //TODO: Añadir puntuación que da.
                }
            }

            //TODO: Añadir un método que compruebe que los recursos consumidos no son mayores que los máximos.
            //  En caso de serlo, habrá sido una modificación manual y sería genial que le saltara un mensaje al
            //  jugador diciendo que le hemos pillado.

            // Utilizar el método: room.comprobarRoomHackeada(). No lo tengo puesto porque no está probado.

            return room;
        }
    };

}
