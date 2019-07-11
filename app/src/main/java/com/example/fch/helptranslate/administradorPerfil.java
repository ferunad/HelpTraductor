package com.example.fch.helptranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class administradorPerfil extends AppCompatActivity {

    //variables
    private ImageView botonRegresar;
    private FirebaseDatabase baseDatos;
    private String TAG ="Administrar Perfil" ;
    private EditText TextoCorreo,TextoContrasena,TextoCelular,TextoNombre,TextoContrasena2;
    private Button boton,botonCambiar,botonAceptar;
    private TextView tituloCambio;
    private FirebaseAuth mAuth;
    private ConstraintLayout campo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_perfil);


        //conexion con el xml
        TextoCorreo=findViewById(R.id.correo);
        TextoContrasena=findViewById(R.id.contrasena);
        TextoCelular=findViewById(R.id.celular);
        TextoNombre=findViewById(R.id.nombre);
        boton=findViewById(R.id.botonCrear);
        botonRegresar=findViewById(R.id.botonVolver);
        botonCambiar=findViewById(R.id.botonCambiarContraseña);
        botonAceptar=findViewById(R.id.Aceptar);
        TextoContrasena2=findViewById(R.id.contrasenaRestablecer);
        tituloCambio=findViewById(R.id.tituloCambio);
        campo=findViewById(R.id.ingresarconstrasena2);

        //revisamos
        revisarSiEsValidoParaCambio();

        //ocultamos los campos de cambio de contraseña
        botonCambiar.setVisibility(View.GONE);
        botonAceptar.setVisibility(View.GONE);
        campo.setVisibility(View.GONE);
        tituloCambio.setVisibility(View.GONE);


        //desactivamos los capos que no pueden cambiar
        TextoCorreo.setEnabled(false);
        TextoContrasena.setEnabled(false);

        //traemos los datos actuales
        inicializarDatosIniciales();


        // listeners de los botones
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarseCompleto();
            }
        });

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        botonCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonAceptar.setVisibility(View.VISIBLE);
                campo.setVisibility(View.VISIBLE);
                tituloCambio.setVisibility(View.VISIBLE);
            }
        });

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarContraseña();
            }
        });
    }

    //metodo para registrar los cambios en firebase database
    private void registrarseCompleto(){
        // obtenemos el usario que acabamos de crear
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        //extracion de datos
        String correo=TextoCorreo.getText().toString();
        String contrasena=TextoContrasena.getText().toString();
        String nombre=TextoNombre.getText().toString();
        String celular=TextoCelular.getText().toString();
        String id =usuarioActual.getUid();

        //registramos el usuario en la base de datos basandonos en los datos que ingreso para la autentificacion con firebase
        InicioSesionTodosLosCasos usuario=new InicioSesionTodosLosCasos(administradorPerfil.this);

        //usarmos el metodo de registro de usurios creado anteriormente
        usuario.actualizarUsuarios( correo, nombre,celular,contrasena,id);

    }

    private void registrarseCompletoCambioConstrasena(){
        // obtenemos el usario que acabamos de crear
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        //extracion de datos
        String correo=TextoCorreo.getText().toString();
        String contrasena=TextoContrasena2.getText().toString();
        String nombre=TextoNombre.getText().toString();
        String celular=TextoCelular.getText().toString();
        String id =usuarioActual.getUid();

        //registramos el usuario en la base de datos basandonos en los datos que ingreso para la autentificacion con firebase
        InicioSesionTodosLosCasos usuario=new InicioSesionTodosLosCasos(administradorPerfil.this);

        //usarmos el metodo de registro de usurios creado anteriormente
        usuario.actualizarUsuarios( correo, nombre,celular,contrasena,id);

    }

    public void revisarSiEsValidoParaCambio(){
        //revisamos de cual usuario debemos traer la información
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        String id = usuarioActual.getUid();


        //Conexion con la base de datos
        baseDatos = FirebaseDatabase.getInstance();
        DatabaseReference myRef = baseDatos.getReference("Usuarios/" + id );
        try {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                //verificamos con el mensaje no este repetido
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //conectamos con la base de datos
                        Usuario temporal = dataSnapshot.getValue(Usuario.class);
                        //creamos el objeto en el que se enviaran los datos

                        String contrasena = temporal.getConstrasena();
                       if(contrasena.equals("0")){
                           botonCambiar.setVisibility(View.GONE);
                       }else{
                           botonCambiar.setVisibility(View.VISIBLE);
                       }
                    } else {
                        Toast.makeText(administradorPerfil.this, "No hay datos de usuario", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(administradorPerfil.this, "No se pudo acceder a la informacion del usuario",
                    Toast.LENGTH_SHORT).show();
        }
    }


    //metodo para extraer los cambios de firebase
    public void inicializarDatosIniciales(){

        //revisamos de cual usuario debemos traer la información
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        String id = usuarioActual.getUid();


        //Conexion con la base de datos
        baseDatos = FirebaseDatabase.getInstance();
        DatabaseReference myRef = baseDatos.getReference("Usuarios/" + id );
        try {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                //verificamos con el mensaje no este repetido
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //conectamos con la base de datos
                        Usuario temporal = dataSnapshot.getValue(Usuario.class);
                        //creamos el objeto en el que se enviaran los datos
                        String nombre = temporal.getNombre();
                        String correo = temporal.getCorreo();
                        String celular = temporal.getCelular();
                        String contrasena = temporal.getConstrasena();
                        TextoCorreo.setText(correo);
                        TextoCelular.setText(celular);
                        TextoContrasena.setText(contrasena);
                        TextoNombre.setText(nombre);
                    } else {
                        Toast.makeText(administradorPerfil.this, "No hay datos de usuario", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(administradorPerfil.this, "No se pudo acceder a la informacion del usuario",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void cambiarContraseña(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(  TextoCorreo.getText().toString().trim(), TextoContrasena.getText().toString().trim());
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(TextoContrasena2.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        registrarseCompletoCambioConstrasena();
                                        Log.d(TAG, "Contraseña actualizada correctamente");
                                    } else {
                                        Log.d(TAG, "No se pudo actualizar la constraseña");
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Problema de autentificación");
                        }
                    }
                });


    }

}
