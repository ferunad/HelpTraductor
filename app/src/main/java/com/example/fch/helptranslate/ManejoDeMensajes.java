package com.example.fch.helptranslate;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManejoDeMensajes {

    //Variables
    Context context;
    private FirebaseDatabase baseDatos;
    private static final String TAG = "Crear Mensajes";

    //constructor
    public ManejoDeMensajes(Context context) {
        this.context = context;
    }

    //metodo para crear Mensajes
    public void registroContacto(final String tituloMensaje,final String mensaje) {

        //filtros para que no haya datos vacios
        if (tituloMensaje.isEmpty()) {
            Toast.makeText(context, "ingrese el titulo del mensaje", Toast.LENGTH_SHORT).show();
        }else if(mensaje.isEmpty()){
            Toast.makeText(context, "ingrese el mensaje", Toast.LENGTH_SHORT).show();
        }else {

            //revisamos de cual usuario debemos traer la información
            FirebaseUser usuarioActual=FirebaseAuth.getInstance().getCurrentUser();
            final String id = usuarioActual.getUid();

            //conexion con la base de datos
            baseDatos = FirebaseDatabase.getInstance();

            //especificacion de la base de la ruta a la base de datos
            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+id+"/Mensajes"+tituloMensaje);
            try {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    //verificamos con el mensaje no este repetido
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Toast.makeText(context,"ya existe un mensaje con ese titulo", Toast.LENGTH_SHORT).show();
                        }else{
                            //conectamos con la base de datos
                            baseDatos= FirebaseDatabase.getInstance();

                            //especificamos la ruta
                            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+id+"/Mensajes");

                            //creamos el objeto en el que se enviaran los datos
                            Mensajes mensajeTemporal=new Mensajes(tituloMensaje,mensaje);

                            //enviamos los datos
                            myRef.child(tituloMensaje).setValue(mensajeTemporal);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // esto se ejecutara si hay algun problema con el servidor
                        Log.w(TAG, "Fallo al conectar con el servidor", error.toException());
                    }
                });
            } catch (Exception e) {
                // esto se ejecutara en el resto de casos donde no se logre completar el registro del mensaje
                Toast.makeText(context, "No se pudo crear el mensaje",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //metodo para actualizar los datos de un mensaje
    public void actualizarContacto(final String tituloMensaje,final String mensaje ) {

        //filtros para que no haya datos vacios
        if (tituloMensaje.isEmpty()) {
            Toast.makeText(context, "ingrese el nombre", Toast.LENGTH_SHORT).show();
        }else if(mensaje.isEmpty()){
            Toast.makeText(context, "ingrese el correo", Toast.LENGTH_SHORT).show();
        }else {

            //revisamos de cual usuario debemos traer la información
            FirebaseUser usuarioActual= FirebaseAuth.getInstance().getCurrentUser();
            final String id = usuarioActual.getUid();

            //conexion con la base de datos
            baseDatos = FirebaseDatabase.getInstance();

            //especificacion de la base de la ruta a la base de datos
            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+id+"/Mensajes"+tituloMensaje);
            try {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    //verificamos con el contacto exista
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            //conexion con la base de datos
                            baseDatos= FirebaseDatabase.getInstance();

                            //especificamos la ruta a la cual ira el dato
                            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+id+"/Mensajes");

                            //creamos el objeto para enviar los datos
                            Mensajes mensajes=new Mensajes(tituloMensaje,mensaje);

                            //enviamos los datos al servidor
                            myRef.child(tituloMensaje).setValue(mensajes);

                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // esto se ejecutara si hay algun problema con el servidor
                        Log.w(TAG, "Fallo al conectar con el servidor", error.toException());
                    }
                });
            } catch (Exception e) {
                // esto se ejecutara en el resto de casos donde no se logre completar el registro del mensaje
                Toast.makeText(context, "No se pudo crear el mensaje",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
