package com.example.hideout;

/**
 * Clase usuario
 */
public class Usuario {
    private String nombre; //nombre del usuario
    private String idUsu; //id del usuario
    private int monedas; //monedas del usuario

    //contructor
    public Usuario(){}

    //getters y setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdUsu() {
        return idUsu;
    }

    public void setIdUsu(String idUsu) {
        this.idUsu = idUsu;
    }

    public int getMonedas() {
        return monedas;
    }

    public void setMonedas(int monedas) {
        this.monedas = monedas;
    }
}
