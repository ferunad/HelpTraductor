package com.example.fch.helptranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class CrearMensajes extends AppCompatActivity {

    //Variables
    private EditText TextoTituloMensaje, TextoMensaje;
    private Button botonCrear;
    private ImageView botonRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_mensajes);

        //Conexion con el xml
        TextoTituloMensaje = findViewById(R.id.titulomensaje);
        TextoMensaje = findViewById(R.id.mensaje);
        botonCrear =findViewById(R.id.botonEnviar);
        botonRegresar=findViewById(R.id.botonVolver);

        // listener para el boton encargado de comenzar el proceso de registrar los datos
        botonCrear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String tituloMensaje= TextoTituloMensaje.getText().toString();
                String mensaje= TextoMensaje.getText().toString();
                ManejoDeMensajes nuevoContacto=new ManejoDeMensajes(CrearMensajes.this);
                nuevoContacto.registroContacto(tituloMensaje,mensaje);
                finish();
            }

        });
        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
