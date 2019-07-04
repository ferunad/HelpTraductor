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

public class ManejoDeContactos {

    //Variables
    Context context;
    private FirebaseDatabase baseDatos;
    private static final String TAG = "Crear Contacto";

    //constructor
    public ManejoDeContactos(Context context) {
        this.context = context;
    }

    //metodo para crear contactos
    public void registroContacto(final String nombre,final String correo,final String celular ) {

        //filtros para que no haya datos vacios
        if (nombre.isEmpty()) {
            Toast.makeText(context, "ingrese el nombre", Toast.LENGTH_SHORT).show();
        }else if(correo.isEmpty()){
            Toast.makeText(context, "ingrese el correo", Toast.LENGTH_SHORT).show();
        }else if(celular.isEmpty()){
            Toast.makeText(context, "ingrese el numero celular", Toast.LENGTH_SHORT).show();
        }else {

            //revisamos de cual usuario debemos traer la información
            FirebaseUser usuarioActual=FirebaseAuth.getInstance().getCurrentUser();
            final String id = usuarioActual.getUid();

            //conexion con la base de datos
            baseDatos = FirebaseDatabase.getInstance();

            //especificacion de la base de la ruta a la base de datos
            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+id+"/Contactos"+celular);
            try {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    //verificamos con el contacto no este repetido
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Toast.makeText(context,"El contacto ya existe", Toast.LENGTH_SHORT).show();
                        }else{
                            //conectamos con la base de datos
                            baseDatos= FirebaseDatabase.getInstance();

                            //especificamos la ruta
                            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+id+"/Contactos");

                            //creamos el objeto en el que se enviaran los datos
                            Contactos contacto=new Contactos(nombre,correo,celular);

                            //enviamos los datos
                            myRef.child(celular).setValue(contacto);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // esto se ejecutara si hay algun problema con el servidor
                        Log.w(TAG, "Fallo al conectar con el servidor", error.toException());
                    }
                });
            } catch (Exception e) {
                // esto se ejecutara en el resto de casos donde no se logre completar el registro del contacto
                Toast.makeText(context, "No se pudo crear el contacto",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //metodo para actualizar los datos de un contacto
    public void actualizarContacto(final String nombre,final String correo,final String celular ) {

        //filtros para que no haya datos vacios
        if (nombre.isEmpty()) {
            Toast.makeText(context, "ingrese el nombre", Toast.LENGTH_SHORT).show();
        }else if(correo.isEmpty()){
            Toast.makeText(context, "ingrese el correo", Toast.LENGTH_SHORT).show();
        }else if(celular.isEmpty()){
            Toast.makeText(context, "ingrese el numero celular", Toast.LENGTH_SHORT).show();
        }else {

            //revisamos de cual usuario debemos traer la información
            FirebaseUser usuarioActual= FirebaseAuth.getInstance().getCurrentUser();
            final String id = usuarioActual.getUid();

            //conexion con la base de datos
            baseDatos = FirebaseDatabase.getInstance();

            //especificacion de la base de la ruta a la base de datos
            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+id+"/Contactos"+celular);
            try {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    //verificamos con el contacto exista
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            //conexion con la base de datos
                            baseDatos= FirebaseDatabase.getInstance();

                            //especificamos la ruta a la cual ira el dato
                            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+id+"/Contactos");

                            //creamos el objeto para enviar los datos
                            Contactos contacto=new Contactos(nombre,correo,celular);

                            //enviamos los datos al servidor
                            myRef.child(celular).setValue(contacto);


                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // esto se ejecutara si hay algun problema con el servidor
                        Log.w(TAG, "Fallo al conectar con el servidor", error.toException());
                    }
                });
            } catch (Exception e) {
                // esto se ejecutara en el resto de casos donde no se logre completar el registro del contacto
                Toast.makeText(context, "No se pudo crear el contacto",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
