package com.example.hideout;

import java.io.Serializable;

/**
 * Clase Reto
 */
public class Reto implements Serializable {

    private String idUsu; //id del usuario creador del reto
    private String nomUsu; //nombre del usuario creador del reto
    private double longitud; //longitud donde se encuentra el reto
    private double latitud; //latitud donde se encuentra el reto
    private String pista; //pista del reto
    private String imagen; //imagen del reto

    //constructores

    public Reto() {
    }

    public Reto(String idUsu, String nomUsu, double longitud, double latitud, String pista, String imagen) {
        this.idUsu = idUsu;
        this.nomUsu = nomUsu;
        this.longitud = longitud;
        this.latitud = latitud;
        this.pista = pista;
        this.imagen = imagen;
    }

    //getters y setters

    public String getIdUsu() {
        return idUsu;
    }

    public void setIdUsu(String idUsu) {
        this.idUsu = idUsu;
    }

    public String getNomUsu() {
        return nomUsu;
    }

    public void setNomUsu(String nomUsu) {
        this.nomUsu = nomUsu;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public String getPista() {
        return pista;
    }

    public void setPista(String pista) {
        this.pista = pista;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}