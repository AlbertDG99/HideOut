package com.example.hideout;

import java.io.Serializable;

public class Reto implements Serializable {

    private String idUsu;
    private String nomUsu;
    private double longitud;
    private double latitud;
    private String pista;
    private String imagen;

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