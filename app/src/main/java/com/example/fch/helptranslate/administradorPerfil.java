package com.example.fch.helptranslate;

import androidx.appcompat.app.AppCompatActivity;

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


    private ImageView botonRegresar;
    private FirebaseDatabase baseDatos;
    private String TAG ="Administrar Perfil" ;
    private EditText TextoCorreo,TextoContrasena,TextoCelular,TextoNombre;
    private Button boton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_perfil);
        TextoCorreo=findViewById(R.id.correo);
        TextoContrasena=findViewById(R.id.contrasena);
        TextoCelular=findViewById(R.id.celular);
        TextoNombre=findViewById(R.id.nombre);
        boton=findViewById(R.id.botonCrear);
        botonRegresar=findViewById(R.id.botonVolver);

        //desactivamos los capos que no pueden cambiar
        TextoCorreo.setEnabled(false);
        TextoContrasena.setEnabled(false);

        //traemos los datos actuales
        inicializarDatosIniciales();



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


    }

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
            Toast.makeText(administradorPerfil.this, "No se pudo acceder a la informacion del contacto",
                    Toast.LENGTH_SHORT).show();
        }

    }

}
