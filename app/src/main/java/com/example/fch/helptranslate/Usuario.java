package com.example.fch.helptranslate;

public class Usuario {

    //Variables
    private String correo;
    private String nombre;
    private String celular;
    private String constrasena;
    private String id;

    //Getters, Setters y Constructor
    public Usuario(String correo, String nombre, String celular, String constrasena, String id) {
        this.correo = correo;
        this.nombre = nombre;
        this.celular = celular;
        this.constrasena = constrasena;
        this.id = id;
    }
    public Usuario(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getConstrasena() {
        return constrasena;
    }

    public void setConstrasena(String constrasena) {
        this.constrasena = constrasena;
    }
}
