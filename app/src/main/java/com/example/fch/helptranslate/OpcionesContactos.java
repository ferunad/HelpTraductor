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

public class OpcionesContactos extends AppCompatActivity {

    //Variables
    private static final String TAG ="Opciones contactos";
    private EditText TextoNombre,TextoCorreo,TextoCelular ;
    private ImageView botonEditar,botonAceptar,botonCancelar,botonEliminar,botonAceptarEliminar,botonCancelarEliminar,botonRegresar;
    private TextView TextoTitulo,TextoInformacion,TextoGuardar,TextoEliminar;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_contactos);

        //informacion del Bundle
        final String nombre=getIntent().getExtras().getString("nombre");
        final String correo=getIntent().getExtras().getString("correo");
        final String celular=getIntent().getExtras().getString("celular");

        //revisamos de cual usuario debemos traer la información
        FirebaseUser usuarioActual=FirebaseAuth.getInstance().getCurrentUser();
        id = usuarioActual.getUid();


        //conexion con el xml
        TextoTitulo = (TextView) findViewById(R.id.textoEntrar);
        TextoInformacion = (TextView) findViewById(R.id.titulo);
        TextoGuardar= (TextView) findViewById(R.id.textoGuardarCambios);
        TextoEliminar=(TextView) findViewById(R.id.textoEliminarElemento);
        botonRegresar=findViewById(R.id.botonVolver);
        TextoNombre=findViewById(R.id.nombre);
        TextoCorreo=findViewById(R.id.correo);
        TextoCelular=findViewById(R.id.celular);
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
        TextoInformacion.setText(nombre);
        TextoNombre.setText(nombre);
        TextoCorreo.setText(correo);
        TextoCelular.setText(celular);


        //bloqueo de intercaccion
        TextoCorreo.setEnabled(false);
        TextoNombre.setEnabled(false);
        TextoCelular.setEnabled(false);

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
                TextoNombre.setEnabled(true);
                TextoCorreo.setEnabled(true);
            }
        });

        //listen de boton encargado de aceptar los cambios al contacto
        botonAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //convertimos los datos a Strings
                String nombre=TextoNombre.getText().toString();
                String correo=TextoCorreo.getText().toString();
                String celular=TextoCelular.getText().toString();

                //usamos el metodo de ManejoDeContactos para actualizar los datos
                ManejoDeContactos contacto=new ManejoDeContactos(OpcionesContactos.this);
                contacto.actualizarContacto(nombre,correo,celular);
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
                TextoNombre.setText(nombre);
                TextoCorreo.setText(correo);
                TextoCelular.setText(celular);

                //bloqueamos la edicion de los campos
                TextoNombre.setEnabled(false);
                TextoCorreo.setEnabled(false);

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
                DatabaseReference mDatabase =FirebaseDatabase.getInstance().getReference("Usuarios/"+id+"/Contactos");

                //especificamos la ruta
                DatabaseReference currentUserBD = mDatabase.child(celular);

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
