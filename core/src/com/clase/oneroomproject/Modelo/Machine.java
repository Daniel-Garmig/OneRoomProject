package com.clase.oneroomproject.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Null;

import java.util.HashMap;


/**
 * Almacena los datos relativos a una máquina.
 * Esta clase es construida a partir de JSON automáticamente en MachineLoader.
 *
 * @author Daniel García
 */
public class Machine
{
    /*-------------
    * Constructors
    * -------------*/
    public Machine()
    {
        this.machineID = "null";
        this.esDeRecursos = false;
        this.attributes = new HashMap<String, Integer>();
    }

    public Machine(String machineID)
    {
        this.machineID = machineID;
        this.esDeRecursos = false;
        this.attributes = new HashMap<>();
    }

    private Machine(Machine machine)
    {
        //Privatizamos el constructor de copia.
        //No debería ser necesario (Java no hace copias de objetos). Pero por si acaso.
    }
    /*----------------
     * Getters/Setters
     * -------------*/

    public String getMachineID() { return machineID; }
    public void setMachineID(String machineID) { this.machineID = machineID; }

    public boolean isEsDeRecursos() { return esDeRecursos; }
    public void setEsDeRecursos(boolean esDeRecursos) { this.esDeRecursos = esDeRecursos; }

    public HashMap<String, Integer> getAttributes() { return attributes; }

    public String getTileSetID() { return tileSetID; }
    public void setTileSetID(String tileSetID) { this.tileSetID = tileSetID; }

    public int getTilePos() { return tilePos; }
    public void setTilePos(int tilePos) { this.tilePos = tilePos; }

    public int getMachineCost() { return machineCost; }

    public int getDineroProducido() { return dineroProducido; }

    /*-------------
     * Methods
     * -------------*/

    //Se podría unificar con UpdateAtribute
    public void AddAtribute(String name, int value)
    {
        attributes.put(name, value);
    }

    public void UpdateAtribute(String name, int newValue)
    {
        attributes.put(name, newValue);
    }

    public @Null int GetAtributeValue(String name)
    {
        Integer value = attributes.get(name);
        if(value == null)
        {
            Gdx.app.error("Machine - " + machineID, "El atributo solicitado no existe: " + name
                                       + " en la maquina " + machineID);
        }
        return value;
    }


    /*-------------
     * Variables
     * -------------*/

    String machineID;
    public HashMap<String, Integer> attributes;

    //Coste de comprar la máquina.
    int machineCost;
    //Dinero que produce cada ciclo.
    int dineroProducido;

    boolean esDeRecursos;

    /*
        Permiten una asociación sencilla con los datos para el renderizado.
        Es más cómodo que usar Enums en código o hardcodear estos valores.
        De nada, equipo de frontend.
     */
    String tileSetID;
    int tilePos;

}
