package com.clase.oneroomproject.vista;

public interface StageInterface {
    /**
     * Inicialización de los objetos pertenecientes al Stage
     */
    public void initComponentes();

    /**
     * Añade los componentes inicializados al Stage
     */
    public void addComponentes();

    /**
     * Añade el tamaño y la posición de los componentes del Stage
     */
    public void putComponentes();

    /**
     * Método creado para el gestión de eventos de los actores del Stage
     */
    public void gestionEventos();
}
