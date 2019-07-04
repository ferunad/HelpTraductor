package com.example.fch.helptranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OpcionesMensajes extends AppCompatActivity {


    //Variables
    private static final String TAG ="Opciones contactos";
    private EditText TextoTituloMensaje, TextoMensajes;
    private ImageView botonEditar,botonAceptar,botonCancelar,botonEliminar,botonAceptarEliminar,botonCancelarEliminar,botonRegresar;
    private TextView TextoTitulo,TextoInformacion,TextoGuardar,TextoEliminar;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_mensajes);

        //informacion del Bundle
        final String tituloMensaje=getIntent().getExtras().getString("tituloMensaje");
        final String mensajes=getIntent().getExtras().getString("mensajes");


        //revisamos de cual usuario debemos traer la información
        FirebaseUser usuarioActual= FirebaseAuth.getInstance().getCurrentUser();
        id = usuarioActual.getUid();


        //conexion con el xml
        TextoTitulo = (TextView) findViewById(R.id.textoEntrar);
        TextoInformacion = (TextView) findViewById(R.id.titulo);
        TextoGuardar= (TextView) findViewById(R.id.textoGuardarCambios);
        TextoEliminar=(TextView) findViewById(R.id.textoEliminarElemento);
        botonRegresar=findViewById(R.id.botonVolver);
        TextoTituloMensaje =findViewById(R.id.titulomensaje);
        TextoMensajes =findViewById(R.id.mensaje);
        botonAceptar=findViewById(R.id.aceptar) ;
        botonCancelar=findViewById(R.id.cancelar) ;
        botonEditar=findViewById(R.id.editar) ;
        botonEliminar=findViewById(R.id.eliminar) ;
        botonAceptarEliminar=findViewById(R.id.aceptarEliminar);
        botonCancelarEliminar=findViewById(R.id.cancelarEliminar);

        //visibilidad inicial
        TextoGuardar.setVisibility(View.GONE);
        TextoEliminar.setVisibility(View.GONE);
        botonAceptar.setVisibility(View.GONE);
        botonCancelar.setVisibility(View.GONE);
        botonAceptarEliminar.setVisibility(View.GONE);
        botonCancelarEliminar.setVisibility(View.GONE);

        // valores iniciales
        TextoInformacion.setText(tituloMensaje);
        TextoTituloMensaje.setText(tituloMensaje);
        TextoMensajes.setText(mensajes);

        //bloqueo de intercaccion
        TextoMensajes.setEnabled(false);
        TextoTituloMensaje.setEnabled(false);

        //listen de boton encargado de editar el contacto
        botonEditar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //cambios visuales para esta opción
                TextoTitulo.setText("Ya puedes realizar los cambios:");
                botonEditar.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.GONE);
                botonAceptar.setVisibility(View.VISIBLE);
                botonCancelar.setVisibility(View.VISIBLE);
                TextoGuardar.setVisibility(View.VISIBLE);
                //activamos la modificacion de campos
                TextoMensajes.setEnabled(true);
            }
        });

        //listen de boton encargado de aceptar los cambios al contacto
        botonAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //convertimos los datos a Strings
                String tituloMensaje= TextoTituloMensaje.getText().toString();
                String mensaje= TextoMensajes.getText().toString();

                //usamos el metodo de ManejoDeContactos para actualizar los datos
                ManejoDeMensajes mensajesTemporal=new ManejoDeMensajes(OpcionesMensajes.this);
                mensajesTemporal.actualizarContacto(tituloMensaje,mensaje);
                finish();
            }

        });

        //listen de boton encargado de cancelar los cambios al contacto
        botonCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //cambios visuales para esta opción
                TextoTitulo.setText("Datos del contacto:");
                botonEditar.setVisibility(View.VISIBLE);
                botonEliminar.setVisibility(View.VISIBLE);
                botonAceptar.setVisibility(View.GONE);
                botonCancelar.setVisibility(View.GONE);
                TextoGuardar.setVisibility(View.GONE);

                //reasignamos los valores iniciales
                TextoTituloMensaje.setText(tituloMensaje);
                TextoMensajes.setText(mensajes);

                //bloqueamos la edicion de los campos
                TextoMensajes.setEnabled(false);

            }

        });

        //listen de boton encargado de eliminar el contacto
        botonEliminar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //cambios visuales para esta opción
                botonEditar.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.GONE);
                botonAceptarEliminar.setVisibility(View.VISIBLE);
                botonCancelarEliminar.setVisibility(View.VISIBLE);
                TextoEliminar.setVisibility(View.VISIBLE);
            }

        });

        //listen de boton encargado de aceptar eliminar el contacto
        botonAceptarEliminar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //conexion con la base de datos
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios/"+id+"/Mensajes");

                //especificamos la ruta
                DatabaseReference currentUserBD = mDatabase.child(tituloMensaje);

                //la eliminamos
                currentUserBD.removeValue();

                //volvemos a la lista de contactos
                finish();
            }

        });

        //listen de boton encargado de cancelar eliminar el contacto
        botonCancelarEliminar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //cambios visuales para esta opción
                botonEditar.setVisibility(View.VISIBLE);
                botonEliminar.setVisibility(View.VISIBLE);
                botonAceptarEliminar.setVisibility(View.GONE);
                botonCancelarEliminar.setVisibility(View.GONE);
                TextoEliminar.setVisibility(View.GONE);
            }

        });

        //boton de regresar que finaliza la activity para volver a la anterior
        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
