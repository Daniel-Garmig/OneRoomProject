package com.clase.oneroomproject.Modelo;


/**
 * Contiene los datos necesarios para la conexión con la BD.
 * Esta clase se serializa automáticamente a JSON en DBConnector.
 *
 * @author Daniel García.
 */
public class dbData
{
    public dbData() {}

    String hostname;
    String port;

    String user;
    String password;

    String dbName;
    String args;


}
