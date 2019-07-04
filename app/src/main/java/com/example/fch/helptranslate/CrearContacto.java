package com.example.fch.helptranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CrearContacto extends AppCompatActivity {

    //Variables
    private EditText TextoNombre, TextoCorreo, TextoCelular;
    private Button botonCrear;
    private ImageView botonRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_contacto);

        //Conexion con el xml
        TextoNombre = findViewById(R.id.nombre);
        TextoCorreo = findViewById(R.id.correo);
        TextoCelular = findViewById(R.id.celular);
        botonCrear =findViewById(R.id.botonCrear);
        botonRegresar=findViewById(R.id.botonVolver);

        // listener para el boton encargado de comenzar el proceso de registrar los datos
        botonCrear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String nombre=TextoNombre.getText().toString();
                String correo= TextoCorreo.getText().toString();
                String celular= TextoCelular.getText().toString();
                ManejoDeContactos nuevoContacto=new ManejoDeContactos(CrearContacto.this);
                nuevoContacto.registroContacto(nombre,correo,celular);
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
