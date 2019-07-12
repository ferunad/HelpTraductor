package com.example.fch.helptranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MenuPrincipal extends AppCompatActivity {

    //variables
    private ImageView botonVideos,botonApps,botonDicionarios,botonContactos,botonMensajes;
    private Button botonEmergencia,cerrarSesion,botonPerfil,camara,historial;
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
        botonEmergencia=findViewById(R.id.emergencia);
        cerrarSesion=findViewById(R.id.CerrarSesion);
        botonPerfil=findViewById(R.id.perfil);
        historial=findViewById(R.id.Historial);


        //Listener para el boton que lleva a mensajes
        botonMensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent diciconario=new Intent(MenuPrincipal.this,ListaMensajes.class);
                startActivity(diciconario);
            }
        });


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

        //Listener para el boton que lleva a al traductor
        traductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aplicaciones=new Intent(MenuPrincipal.this,EscucharHablarTraductor.class);
                startActivity(aplicaciones);
            }
        });

        //Listener para el boton que lleva a emergencia
        botonEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emergencia= new Intent(MenuPrincipal.this,ContactoDeEmergencia.class);
                startActivity(emergencia);
            }
        });

        //Listener y metodo para cerrar sesion
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        //listener para el boton de lleva a perfil
        botonPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent perfil=new Intent(MenuPrincipal.this,administradorPerfil.class);
                startActivity(perfil);
            }
        });


        historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Historial= new Intent(MenuPrincipal.this,HistorialTraducciones.class);
                startActivity(Historial);
            }
        });
    }
}
