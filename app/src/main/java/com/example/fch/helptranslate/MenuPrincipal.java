package com.example.fch.helptranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MenuPrincipal extends AppCompatActivity {

    //variables
    private ImageView botonVideos,botonApps,botonDicionarios,botonContactos,botonMensajes;
    private Button traductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // conexiones de elementos con el xml
        botonVideos= findViewById(R.id.botonVideos);
        botonApps= findViewById(R.id.botonApps);
        botonDicionarios=findViewById(R.id.botonDiccionarios);
        botonContactos=findViewById(R.id.botonContactos);
        botonMensajes=findViewById(R.id.botonMensajes);
        traductor=findViewById(R.id.tradcutor);


        //Listener para el boton que lleva a mensajes
        botonMensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent diciconario=new Intent(MenuPrincipal.this,ListaMensajes.class);
                startActivity(diciconario);
            }
        });

        //Listener para el boton que lleva a contactos
        botonContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent diciconario=new Intent(MenuPrincipal.this,ListaContactos.class);
                startActivity(diciconario);
            }
        });

        //Listener para el boton que lleva a diccionarios
        botonDicionarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent diciconario=new Intent(MenuPrincipal.this,ListaDiccionarios.class);
                startActivity(diciconario);
            }
        });

        //Listener para el boton que lleva a videos
        botonVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent video=new Intent(MenuPrincipal.this,ListaVideos.class);
                startActivity(video);
            }
        });

        //Listener para el boton que lleva a apliaciones
        botonApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aplicaciones=new Intent(MenuPrincipal.this,ListaApliacaciones.class);
                startActivity(aplicaciones);
            }
        });

        //Listener para el boton que lleva a apliaciones
        traductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aplicaciones=new Intent(MenuPrincipal.this,EscucharHablarTraductor.class);
                startActivity(aplicaciones);
            }
        });
    }
}
