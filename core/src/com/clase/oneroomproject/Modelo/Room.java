package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Clase que contiene los datos de la Sala y permite interactuar con ellos.
 * Esta clase es construida desde JSON o BD por el RoomLoader.
 *
 * @author Daniel García.
 */
public class Room
{
    /*-------------
     * Constructors
     * -------------*/

    public Room()
    {
        //Hay que instanciar las cosas que no se instancian en el JSON.
    }

    /**
     * DEBUG CONSTRUCTOR!! Para meter datos de prueba.
     * @param roomName Nombre de la room.
     * @param width Ancho.
     * @param height Alto.
     */
    public Room(String roomName, int width, int height)
    {
        this.roomName = roomName;
        this.roomSize = new Vector2(width, height);
        this.espacioTotal = 10;

        this.tileSetID = "BGTiles";
        bgData = new int[height][width];
        machineData = new Machine[height][width];

        this.recursosMaximos = new HashMap<>();
        this.recursosOcupados = new HashMap<>();

        //También habría que cargar los atributos desde algún sitio...
        bgData = new int[][]
                {
                        {2, 2, 2, 2, 2, 2, 2, 2},
                        {2, 3, 3, 3, 3, 3, 3, 2},
                        {2, 3, 3, 3, 3, 3, 3, 2},
                        {2, 3, 3, 3, 3, 3, 3, 2},
                        {2, 2, 2, 2, 2, 2, 2, 2},
                };

        for (Machine[] machineDatum : machineData)
        {
            Arrays.fill(machineDatum, null);
        }

        recursosDefecto = new HashMap<>();
        recursosDefecto.put("Electricidad", 5);
        recursosDefecto.put("Refrigeracion", 10);
        recursosDefecto.put("Abonos", 8);

        recursosMaximos = new HashMap<>(recursosDefecto);

        //Inicialmente, los recursos ocupados tienen el valor 0.
        //  Totamos los nombres de los por defecto, pero su valor será 0.
        recursosOcupados = new HashMap<>();
        for(Map.Entry<String, Integer> recurso : recursosDefecto.entrySet())
        {
            recursosOcupados.put(recurso.getKey(), 0);
        }

        Gdx.app.debug("Room - " + roomName, "Se ha creado una la Room Debug: " + roomName);

    }

    /*-------------
     * Methods
     * -------------*/

    /**
     * Añade una máquina a la sala, realizando todas las comprobaciones pertinentes.
     * @param x Posición x.
     * @param y Posición y.
     * @param machineToAdd Ptr a la máquina que se quiere añadir.
     * @return true si la máquina se ha añadido correctamente, false si ha ocurrido un error.
     */
    public boolean AddMachine(int x, int y, Machine machineToAdd)
    {
        if(!ComprobarMaquina(machineToAdd, x, y))
        {
            Gdx.app.log("Room - " + roomName, "No se puede añadir la máquina "
                    + machineToAdd.machineID + " a la room " + roomName);
            return false;
        }

        ModificarRecursosPorMaquina(machineToAdd, true);

        //Añadimos un ptr de la máquina en las coordenadas.
        machineData[y][x] = machineToAdd;

        //Añadimos el dinero que produce.
        //Todo: Esto debería ser únicamente en máquinas de dinero o
        // en todas las máquinas. Podemos considerar que si no producen dinero tendrán valor 0 en dineroProducido.
        dineroPorCiclo += machineToAdd.dineroProducido;
        roomScore += machineToAdd.machineCost;

        //Se consume un hueco de espacio en la sala.
        espacioOcupado += 1;

        return true;
    }

    //He estado pensando en como va esto.
    //(Hay que tener en cuenta que no los almacenamos, los generamos al cargar las máquinas).
    // Podemos usar Atributo y Atributo_max y almacenar todos en un único HashMap.
    // También podemos tener dos HashMap, uno para los atributos y otro para los máximos.
    //      Con unos if-else comprobamos/añadimos en el que corresponda según sea la máquina.

    /**
     * Comprueba si se tiene la capacidad suficiente de un recurso para añadir el valor indicado.
     * @param attribute Nombre del recurso a comprobar.
     * @param valorComprobar Cantidad que se pretende añadir.
     * @return True si es posible añadir esa cantidad, False si no hay capacidad suficiente.
     */
    public boolean ComprobarCapacidadRecurso(String attribute, int valorComprobar)
    {
        Integer max = recursosMaximos.get(attribute);
        Integer ocupados = recursosOcupados.get(attribute);
        if(max == null || ocupados == null)
        {
            Gdx.app.error("Room - " + roomName , "El recurso " + attribute + " no existe en esta sala");
            return false;
        }
        if((ocupados + valorComprobar) >= max)
        {
            //Gdx.app.debug("Room - " + roomName, "El recurso " + attribute + " de esta sala ya está lleno.");
            return false;
        }
        return true;
    }

    /**
     * Comprueba si es posible eliminar la cantidad indicada del recurso máximo indicado sin que las máquinas consuman más de lo que tienen.
     * @param recurso ID del recurso.
     * @param cantidad Cantidad del recurso que se quiere eliminar.
     * @return True si es posible eliminarlo, false si no.
     */
    public boolean ComprobarEliminarRecursoMaximo(String recurso, int cantidad)
    {
        //Tomamos los recursos.
        Integer max = recursosMaximos.get(recurso);
        Integer ocupados = recursosOcupados.get(recurso);
        //Comprobamos si existen.
        if(max == null || ocupados == null)
        {
            Gdx.app.error("Room - " + roomName , "El recurso " + recurso + " no existe en esta sala");
            return false;
        }
        //Comprobamos si se superaría el máximo en caso de quitarlo.
        if(ocupados > (max - cantidad))
        {
            //Gdx.app.debug("Room - " + roomName, "El recurso " + attribute + " de esta sala ya está lleno.");
            return false;
        }
        return true;
    }

    /**
     * Permite comprobar si se puede añadir una máquina a la sala.
     * @param maquinaComprobar Ptr a la máquina que se quiere comprobar.
     * @return True si es posible añadir la máquina, False si no hay recursos o espacio suficiente.
     */
    public boolean ComprobarRecursosParaMaquina(Machine maquinaComprobar)
    {

        //En principio, las máquinas de recursos siempre se pueden poner.
        if(maquinaComprobar.esDeRecursos)
        {
            return true;
        }

        //Se procesarían los atributos para comprobar si se puede añadir la máquina.
        for(String key : maquinaComprobar.attributes.keySet())
        {
            int value = maquinaComprobar.GetAtributeValue(key);
            if(!ComprobarCapacidadRecurso(key, value))
            {
                return false;
            }
        }
        return true;
    }


    /**
     * Permite comprobar si hy hueco (se puede poner una máquina) en la posición indicada.
     * @param x posición x.
     * @param y posición y.
     * @return True si hay hueco y se puede poner una máquina. False si la posición ya está ocupada.
     */
    public boolean ComprobarPosicion(int x, int y)
    {
        //Evitamos salirnos del Array.
        if(y >= machineData.length || x >= machineData[0].length)
        {
            return false;
        }

        Machine mc = machineData[y][x];
        if(mc == null)
        {
            return true;
        }
        return false;
    }

    /**
     * Realiza todas las comprobaciones para ver si es posible colocar la máquina indicada en la posición indicada.
     * Hace uso de los otros métodos de comprobación (ComprobarRecursos, ComprobarPosición,...).
     * @param maquinaComprobar Ptr a la máquina. Permite comprobar si se tiene la capacidad de recursos necesaria.
     * @param x Posición X.
     * @param y Posición Y.
     * @return True si es posible colocar la máquina y en esa posición. False si no se puede.
     */
    public boolean ComprobarMaquina(Machine maquinaComprobar, int x, int y)
    {
        //Comprobamos si queda hueco en la room para añadir más máquinas.
        if((espacioOcupado+1) > espacioTotal)
        {
            Gdx.app.log("Room - " + roomName, "No queda hueco en esta sala, no se pueden añadir más máquinas.");
            return false;
        }

        //Comprobamos si la posición es válida.
        if(!ComprobarPosicion(x, y))
        {
            Gdx.app.log("Room - " + roomName, "No se puede poner una máquina en " +
                    "x:" + x + ", y:" + y + ". La posición ya está ocupada.");
            return false;
        }

        //Comprobamos que tenemos todos los recursos necesarios.
        if(!ComprobarRecursosParaMaquina(maquinaComprobar))
        {
            Gdx.app.log("Room - " + roomName, "No tienes capacidad de recursos suficiente para esta máquina...");
            return false;
        }

        //Si está correcto, true.
        return true;
    }


    /**
     * Permite modificar de forma segura los recursos de la sala. Pueden sumarse o restarse.
     * @param resourceName Nombre del recurso a modificar.
     * @param mod Modificador a aplicar (positivo suma, negativo se resta).
     * @param esMaximo Indica si se trata de la capacidad máxima de la sala (true= o de la capacidad ocupada (false).
     * @return True si se ha conseguido modificar. False si ha ocurrido un error.
     */
    public boolean ModificarRecurso(String resourceName, int mod, boolean esMaximo)
    {
        //Obtenemos los actuales recursos ocupados.
        Integer act = null;
        if(esMaximo)
        {
            act = recursosMaximos.get(resourceName);
        }
        if(!esMaximo)
        {
            act = recursosOcupados.get(resourceName);
        }
        if(act == null)
        {
            Gdx.app.error("Room - " + roomName, "Esta room no tiene el recurso " + resourceName);
            return false;
        }
        //Añadimos el modificador y lo actualizamos.
        act += mod;
        if(esMaximo)
        {
            recursosMaximos.put(resourceName, act);
        }
        if(!esMaximo)
        {
            recursosOcupados.put(resourceName, act);
        }
        return true;
    }

    /**
     * Modifica los recursos de la sala a partir de lo consumido/generado por una máquina.
     * Utilizado al añadir y eliminar una máquina.
     * @param mc Máquina cuyos recursos se quieren añadir/restar.
     * @param add Indica si los recursos se sumaran (true) o se restarán (false).
     */
    public void ModificarRecursosPorMaquina(Machine mc, boolean add)
    {
        //Utilizamos el modificador para sumar/restar los recusos.
        //Si se añaden, el modificador será 1, si se restan, -1 (el opuesto).
        int mod = (add ? 1 : -1);
        //Añadimos los recursos que consume.
        for(Map.Entry<String, Integer> resource : mc.getAttributes().entrySet())
        {
            ModificarRecurso(resource.getKey(), mod * resource.getValue(), mc.esDeRecursos);
        }
    }

    /**
     * Comprueba si es posible eliminar la máquina indicada.
     * Es decir, si los recursos de esta máquina no son esenciales para mantener a las demás.
     * @param maquinaComprobar Máquina que se quiere comprobar.
     * @return True si se puede eliminar, false si no.
     */
    public boolean ComprobarEliminarMaquinaRecursos(Machine maquinaComprobar)
    {
        //Si no es de recursos, no hace falta comprobar esto. Se puede eliminar sin problemas.
        if(!maquinaComprobar.esDeRecursos)
        {
            return true;
        }

        //Se procesarían los atributos para comprobar si se puede añadir la máquina.
        for(String key : maquinaComprobar.attributes.keySet())
        {
            int value = maquinaComprobar.GetAtributeValue(key);
            if(!ComprobarCapacidadRecurso(key, value))
            {
                return false;
            }
        }
        return true;
    }


    /**
     * Mueve una máquina de una casilla a otra.
     * Como solo la movemos, no hace falta modificar atributos de la sala.
     * @param oldX Posición X antigua.
     * @param oldY Posición y antigua.
     * @param newX Posición x nueva.
     * @param newY Posición y nueva.
     */
    public void MoverMaquina(int oldX, int oldY, int newX, int newY)
    {
        //Comprobamos si la posición nueva está disponible.
        if(!ComprobarPosicion(newX, newY))
        {
            Gdx.app.log("Room - " + roomName, "La posición nueva: " + newX + ", " + newY + " ya está ocupada.");
            return;
        }

        //Comprobamos si existe una máquina en la posición antigua.
        Machine mc = machineData[oldY][oldX];
        if(mc == null)
        {
            Gdx.app.log("Room - " + roomName, "No existe ninguna máquina en la posición inicia: " + newX + ", " + newY);
            return;
        }
        //Si está correcto, la movemos.
        machineData[oldY][oldX] = null;
        machineData[newY][newX] = mc;
    }


    /**
     * Elimina la máquina en la posición indicada.
     * Devuelve los recursos que esta máquina consumía/generaba.
     *
     * @param posX Posición x de la máquina a eliminar.
     * @param posY Posición y de la máquina a eliminar.
     * @return True si se ha completado la eliminación. False si ha ocurrido un error.
     */
    public boolean EliminarMaquina(int posX, int posY)
    {
        //Obtenemos la máquina de esta posición.
        Machine mc = machineData[posY][posX];

        //Si es de recursos, comprobamos que se pueda eliminar.
        if(mc.esDeRecursos)
        {
            //Si no se puede eliminar, no continuamos.
            if(!ComprobarEliminarMaquinaRecursos(mc))
            {
                return false;
            }
        }

        ModificarRecursosPorMaquina(mc, false);

        //Quitamos el dinero que produce.
        dineroPorCiclo -= mc.dineroProducido;
        roomScore -= mc.machineCost;

        //Devolvemos el hueco ocupado.
        espacioOcupado--;

        //Quitamos la MC del mapa.
        machineData[posY][posX] = null;

        return true;
    }


    /**
     * Comprueba si el archivo de guardado de la Room ha sido modificado de forma externa.
     * Es decir, que no hay más recursos consumidos que disponibles.
     * Que no hay más huecos utilizados de los disponibles, ...
     * @return True si se ha modificado, false si parece correcto (se ha modificado pero bien).
     */
    public boolean ComprobarRoomHackeada()
    {
        if(espacioOcupado > espacioTotal)
        {
            Gdx.app.log("Room", "Esta sala tiene más huecos ocupados de los que debe...");
            return true;
        }

        //Iteramos por los recursos.
        for(Map.Entry<String, Integer> recurso : recursosOcupados.entrySet())
        {
            //Obtenemos el valor.
            int valor = recurso.getValue();
            //Obtenemos el valor máximo que se puede tener.
            int valorMaximo = recursosMaximos.get(recurso.getKey());

            if(valor > valorMaximo)
            {
                Gdx.app.log("Room", "Esta sala está gastando más recursos de los que tiene...");
                return true;
            }

        }

        //Si parece en orden...
        return false;
    }


    public String getRoomName() { return roomName; }

    public Vector2 getRoomSize() { return roomSize; }

    public int getEspacioOcupado() { return espacioOcupado; }

    public int getEspacioTotal() { return espacioTotal; }

    public String getTileSetID() { return tileSetID; }

    public int[][] getBgData() { return bgData; }

    public Machine[][] getMachineData() { return machineData; }

    public boolean isEsOnline() { return esOnline; }

    public int getRoomPrice() { return roomPrice; }

    public HashMap<String, Integer> getRecursosDefecto() { return recursosDefecto; }

    public int getDineroPorCiclo() { return dineroPorCiclo; }

    public int getRoomScore() { return roomScore; }

    public HashMap<String, Integer> getRecursosOcupados() { return recursosOcupados; }

    public HashMap<String, Integer> getRecursosMaximos() { return recursosMaximos; }

    /*-------------
     * Variables
     * -------------*/

    //Variables que se almacenan en el JSON.

    //Nombre identificador de la sala.
    String roomName;
    //Tamaño de la sala. Todo: Revisar pues el tamaño de paredes y maq. debería ser diferente.
    Vector2 roomSize;

    //El dinero que cuesta comprar esta sala. Se almacena en al JSON.
    int roomPrice;

    //Indica el total de celdas que tiene la sala. Se incluyen paredes.
    int espacioTotal;


    //Almacena la capacidad que tiene una sala por defecto. Es a partir de esta que se genera la actual cuando se carga.
    HashMap<String, Integer> recursosDefecto;


    //Permite saber de qué tileSet son los ints de bgData.
    String tileSetID;
    int[][] bgData;
    Machine[][] machineData;



    //Variables adicionales que se rellenan en Runtime (Cuando carga la sala).

    //Cuanto dinero genera esta sala por ciclo. var = Sum(maquinas dinero).
    int dineroPorCiclo;

    //Puntuación de la sala.
    // Todo: Lo podemos almacenar como variable y modificarlo en
    //   según se modifique la sala o añadir una función que lo calcule cuando sea necesario.
    int roomScore;

    //Recursos y capacidad de la sala.
    //Recursos ocupados actualmente de la sala.
    HashMap<String, Integer> recursosOcupados;
    //Los recursos máximos que caben en la sala. var = defecto + máquinas de recursos.
    HashMap<String, Integer> recursosMaximos;

    //Contador de la cantidad de máquinas añadidas.
    int espacioOcupado;

    //Indica si la sala es propia (cargada desde JSON) o de otro (Cargada desde DB).
    boolean esOnline = false;

}
