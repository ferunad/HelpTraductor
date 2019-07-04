package com.example.fch.helptranslate;

public class MensajeEmergencia {
    // Variables
    public String mensaje;
    public String numero;


    //cosntructor getters and setters
    public MensajeEmergencia(String mensaje, String numero) {
        this.mensaje = mensaje;
        this.numero = numero;
    }
    public MensajeEmergencia() {

    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }


}
