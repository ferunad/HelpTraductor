package com.example.dfml.helptraductor;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InicioSesionTodosLosCasos {

    //Variables
    Context context;
    private FirebaseAuth mAuth;
    private static final String TAG = "Iniciar sesión";
    private FirebaseDatabase baseDatos;

    //constructor
    public InicioSesionTodosLosCasos(Context context) {
        this.context = context;
    }

    //metodo para iniciar sesión
    public void comprobarInicio(String nombre, final String contrasena) {

        //filtros para revisar que ningun campo este vacio
        if (nombre.isEmpty()) {
            Toast.makeText(context, "ingrese su id porfavor", Toast.LENGTH_SHORT).show();
        }else if (contrasena.isEmpty()) {
            Toast.makeText(context, "ingrese su contraseña porfavor", Toast.LENGTH_SHORT).show();
        }else {

            //Conexión con la base de datos
            baseDatos = FirebaseDatabase.getInstance();

            //Especificación de la ura de la base de datos
            DatabaseReference myRef = baseDatos.getReference("Usuarios/" + nombre);
            try {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //verificacion de la existencia del usuario en la base de datos
                        if (dataSnapshot.exists()) {

                            //copia de los datos del usuario en la base de datos
                            Usuario value = dataSnapshot.getValue(Usuario.class);
                            Log.d(TAG, "Value is: " + value);

                            // separamos la contraseña de la copia de los datos
                            String validar = value.getConstrasena();
                            String validacion = contrasena;

                            //validación de la contraseña
                            if (validar.equals(validacion)) {
                                Intent holi = new Intent(context,MenuPrincipal.class);
                                context.startActivity(holi);
                            } else {

                                //esto se ejectua si las contraseñas no conciden
                                Toast.makeText(context, "Datos incorrectos",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }else{

                            //esto se ejecuta en caso de que no exista el usuario
                            Toast.makeText(context,"No existe ese usuario",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    // en caso de no poder leer algun valor
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            } catch (Exception e) {

                //se ejecuta en todos los casos sobrantes donde no se ingrese
                Toast.makeText(context, "Usuario o Contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }


        }
    }

    public void registroUsuarios(final String correo, final String nombre, final String celular,final String constrasena, final String id) {

        //filtros para revisar que ningun campo este vacio
        if (correo.isEmpty()) {
            Toast.makeText(context, "ingrese el correo porfavor", Toast.LENGTH_SHORT).show();
        } else if (nombre.isEmpty()) {
            Toast.makeText(context, "ingrese su nombre porfavor", Toast.LENGTH_SHORT).show();
        }else if (celular.isEmpty()) {
            Toast.makeText(context, "ingrese su celular porfavor", Toast.LENGTH_SHORT).show();
        }else if (constrasena.isEmpty()) {
            Toast.makeText(context, "ingrese su contraseña porfavor", Toast.LENGTH_SHORT).show();
        }else {

            // conexion ocn la base de datos
            baseDatos = FirebaseDatabase.getInstance();

            // especificacionde la ruta a la que se va acceder en la base de datos
            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+ id);
            try {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    //hacemos una busqueda en la base de datos
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //verificamos si ya existe el usuario
                        if (dataSnapshot.exists()){
                            Toast.makeText(context,"El usuario ya existe", Toast.LENGTH_SHORT).show();
                        }else{

                            //conexion con la base de datos
                            baseDatos= FirebaseDatabase.getInstance();

                            //especificamos la ruta final
                            DatabaseReference myRef = baseDatos.getReference("Usuarios");

                            // creamos el objeto a enviar
                            Usuario usuario= new Usuario(correo,nombre,celular,constrasena,id);

                            //le pedimos que creee una sub carpeta donde ingrese los datos basandose en el id unico del usuario
                            myRef.child(id).setValue(usuario);
                        }
                    }

                    //en caso de algun proble de conexion o no poder leer datos
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            } catch (Exception e) {

                // esto se ejectura en todos los casos sobrantes posibles donde no se logre registrar al usuario
                Toast.makeText(context, "Registro fallido",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //metodo para poder actualizar los datos de los usuarios en caso de que se necesario
    public void actualizarUsuarios(final String correo, final String nombre, final String celular,final String constrasena, final String id) {

        //filtros para revisar que no haya campos vacios
        if (correo.isEmpty()) {
            Toast.makeText(context, "ingrese el correo porfavor", Toast.LENGTH_SHORT).show();
        }else if (nombre.isEmpty()) {
            Toast.makeText(context, "ingrese su nombre porfavor", Toast.LENGTH_SHORT).show();
        }else if (celular.isEmpty()) {
            Toast.makeText(context, "ingrese su celular porfavor", Toast.LENGTH_SHORT).show();
        }else if (constrasena.isEmpty()) {
            Toast.makeText(context, "ingrese su contraseña porfavor", Toast.LENGTH_SHORT).show();
        }else {

            //conexion con la base de datos
            baseDatos = FirebaseDatabase.getInstance();

            //especificamos la ruta en la que vamos a buscar
            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+ id);
            try {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    //buscamos y traemos una copia de los datos del usuario
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            //vovlermos a conectar con la base de datos
                            baseDatos= FirebaseDatabase.getInstance();

                            //especificamos la direccion donde vamos a escribir
                            DatabaseReference myRef = baseDatos.getReference("Usuarios");

                            //creamos el objeto con el cual enviaremos los datos
                            Usuario usuario= new Usuario(correo,nombre,celular,constrasena,id);

                            //sobre escribimos los datos existentes con los nuevos
                            myRef.child(id).setValue(usuario);
                    }

                    // en caso de que haya algun problema de conexion
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w(TAG, "Problema al leer el valor.", error.toException());
                    }
                });
            } catch (Exception e) {

                // esto se ejecutara en todos los casos sobrantes donde no se complete la actualización de datos
                Toast.makeText(context, "Error al actualizar",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}