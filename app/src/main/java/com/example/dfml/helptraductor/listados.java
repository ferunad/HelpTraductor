package com.example.dfml.helptraductor;

public class listados {
    public String nombre;
    public String url;

    public listados(String nombre, String url) {
        this.nombre = nombre;
        this.url = url;
    }
    public listados() {

    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}