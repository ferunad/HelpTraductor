package com.example.fch.helptranslate;

public class Mensajes {

    //Variables
    public String tituloMensaje;
    public String mensajes;

    //constructor, getter y setters


    public Mensajes(String tituloMensaje, String mensajes) {
        this.tituloMensaje = tituloMensaje;
        this.mensajes = mensajes;
    }

    public Mensajes() {

    }

    public String getTituloMensaje() {
        return tituloMensaje;
    }

    public void setTituloMensaje(String tituloMensaje) {
        this.tituloMensaje = tituloMensaje;
    }

    public String getMensajes() {
        return mensajes;
    }

    public void setMensajes(String mensajes) {
        this.mensajes = mensajes;
    }
}
