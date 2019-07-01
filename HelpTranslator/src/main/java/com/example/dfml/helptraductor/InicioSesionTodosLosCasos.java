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
    Context context;
    private FirebaseAuth mAuth;
    private static final String TAG = "Iniciar sesión";
    private FirebaseDatabase baseDatos;

    public InicioSesionTodosLosCasos(Context context) {
        this.context = context;
    }

    public void comprobarInicio(String nombre, final String contrasena) {
        if (nombre.isEmpty()) {
            Toast.makeText(context, "ingrese su id porfavor",
                    Toast.LENGTH_SHORT).show();
        } else if (contrasena.isEmpty()) {
            Toast.makeText(context, "ingrese su contraseña porfavor",
                    Toast.LENGTH_SHORT).show();
        } else {
            baseDatos = FirebaseDatabase.getInstance();
            DatabaseReference myRef = baseDatos.getReference("Usuarios/" + nombre);
            try {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Usuario value = dataSnapshot.getValue(Usuario.class);
                            Log.d(TAG, "Value is: " + value);
                            String validar = value.getConstrasena();
                            String validacion = contrasena;
                            if (validar.equals(validacion)) {
                                Intent holi = new Intent(context,MenuPrincipal.class);
                                context.startActivity(holi);
                            } else {
                                Toast.makeText(context, "Datos incorrectos",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(context,"No existe ese usuario",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Usuario o Contraseña incorrectos",
                        Toast.LENGTH_SHORT).show();
            }


        }
    }

    public void registroUsuarios(final String correo, final String nombre, final String celular,final String constrasena, final String id) {

        if (correo.isEmpty()) {
            Toast.makeText(context, "ingrese el correo porfavor", Toast.LENGTH_SHORT).show();
        } else if (nombre.isEmpty()) {
            Toast.makeText(context, "ingrese su nombre porfavor", Toast.LENGTH_SHORT).show();
        }else if (celular.isEmpty()) {
            Toast.makeText(context, "ingrese su celular porfavor", Toast.LENGTH_SHORT).show();
        }else if (constrasena.isEmpty()) {
            Toast.makeText(context, "ingrese su contraseña porfavor", Toast.LENGTH_SHORT).show();
        }else {
            baseDatos = FirebaseDatabase.getInstance();
            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+ id);
            try {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Toast.makeText(context,"El usuario ya existe", Toast.LENGTH_SHORT).show();
                        }else{
                            baseDatos= FirebaseDatabase.getInstance();
                            DatabaseReference myRef = baseDatos.getReference("Usuarios");
                            Usuario usuario= new Usuario(correo,nombre,celular,constrasena,id);
                            myRef.child(id).setValue(usuario);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Usuario o Contraseña incorrectos",
                        Toast.LENGTH_SHORT).show();
            }


        }
    }
    public void actualizarUsuarios(final String correo, final String nombre, final String celular,final String constrasena, final String id) {
        if (correo.isEmpty()) {
            Toast.makeText(context, "ingrese el correo porfavor", Toast.LENGTH_SHORT).show();
        } else if (nombre.isEmpty()) {
            Toast.makeText(context, "ingrese su nombre porfavor", Toast.LENGTH_SHORT).show();
        }else if (celular.isEmpty()) {
            Toast.makeText(context, "ingrese su celular porfavor", Toast.LENGTH_SHORT).show();
        }else if (constrasena.isEmpty()) {
            Toast.makeText(context, "ingrese su contraseña porfavor", Toast.LENGTH_SHORT).show();
        }else {
            baseDatos = FirebaseDatabase.getInstance();
            DatabaseReference myRef = baseDatos.getReference("Usuarios/"+ id);
            try {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            baseDatos= FirebaseDatabase.getInstance();
                            DatabaseReference myRef = baseDatos.getReference("Usuarios");
                            Usuario usuario= new Usuario(correo,nombre,celular,constrasena,id);
                            myRef.child(id).setValue(usuario);
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error al actualizar",
                        Toast.LENGTH_SHORT).show();
            }


        }
    }


}