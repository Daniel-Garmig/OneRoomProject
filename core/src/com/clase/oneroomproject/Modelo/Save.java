package com.clase.oneroomproject.Modelo;

import java.util.HashMap;

/**
 * Clase contenedora para los datos de guardado de la partida.
 *
 * @author Daniel García.
 */
public class Save
{

    public int dinero;

    /*
        Indica si el jugador tiene comprada esa sala y, por tanto, ha de aparecer/cargarse.
     */
    public HashMap<String, Boolean> ownedRooms;

    public boolean isOnline = false;

}
