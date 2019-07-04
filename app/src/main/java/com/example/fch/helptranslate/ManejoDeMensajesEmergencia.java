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

public class ManejoDeMensajesEmergencia {

    //Variables
    Context context;
    private FirebaseDatabase baseDatos;
    private static final String TAG = "Mensajes de emergencia";

    //constructor
    public ManejoDeMensajesEmergencia(Context context) {
        this.context = context;
    }

    //metodo para crear Mensajes
    public void registroMensajes(final String mensaje,final String celular) {

        //filtros para que no haya datos vacios
        if (mensaje.isEmpty()) {
            Toast.makeText(context, "ingrese el titulo del mensaje", Toast.LENGTH_SHORT).show();
        }else if(celular.isEmpty()){
            Toast.makeText(context, "ingrese el mensaje", Toast.LENGTH_SHORT).show();
        }else {

            //revisamos de cual usuario debemos traer la información
            FirebaseUser usuarioActual=FirebaseAuth.getInstance().getCurrentUser();
            final String id = usuarioActual.getUid();
            try {
                //conectamos con la base de datos
                baseDatos= FirebaseDatabase.getInstance();

                //especificamos la ruta
                DatabaseReference myRef = baseDatos.getReference("Usuarios/"+id+"/Emergencia");

                //creamos el objeto en el que se enviaran los datos
                MensajeEmergencia mensajeTemporal=new MensajeEmergencia(mensaje,celular);

                //enviamos los datos
                myRef.setValue(mensajeTemporal);
            } catch (Exception e) {
                // esto se ejecutara en el resto de casos donde no se logre completar el registro del mensaje
                Toast.makeText(context, "No se pudo enviar los datos",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
