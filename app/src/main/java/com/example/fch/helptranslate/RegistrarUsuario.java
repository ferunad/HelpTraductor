package com.example.fch.helptranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class RegistrarUsuario extends AppCompatActivity {

    //Variables
    private static final String TAG="Iniciar Sesión";
    private Button boton;
    private ImageView botonRegresar;
    private FirebaseDatabase baseDatos;
    private FirebaseAuth mAuth;
    private EditText TextoCorreo,TextoContrasena,TextoCelular,TextoNombre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        // conexiones de elementos con el xml
        TextoCorreo=findViewById(R.id.correo);
        TextoContrasena=findViewById(R.id.contrasena);
        TextoCelular=findViewById(R.id.celular);
        TextoNombre=findViewById(R.id.nombre);
        boton=findViewById(R.id.botonCrear);
        botonRegresar=findViewById(R.id.botonVolver);

        //Conexión con la base de datos
        baseDatos=FirebaseDatabase.getInstance();

        //boton encargado de accionar el resgistro
        boton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                registrarse();
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
        FirebaseUser wenas=mAuth.getCurrentUser();

        //extracion de datos
        String correo=TextoCorreo.getText().toString();
        String contrasena=TextoContrasena.getText().toString();
        String nombre=TextoNombre.getText().toString();
        String celular=TextoCelular.getText().toString();
        String id = wenas.getUid();

        //registramos el usuario en la base de datos basandonos en los datos que ingreso para la autentificacion con firebase
        InicioSesionTodosLosCasos usuario=new InicioSesionTodosLosCasos(RegistrarUsuario.this);

        //usarmos el metodo de registro de usurios creado anteriormente
        usuario.registroUsuarios( correo, nombre,celular,contrasena,id);

    }
    private void registrarse(){

        //Extraemos los datos
        String correo=TextoCorreo.getText().toString();
        String contrasena=TextoContrasena.getText().toString();

        //filtros para revisar que los campos no esten vacios
        if (correo.isEmpty()) {
            Toast.makeText(RegistrarUsuario.this, "ingrese su correo porfavor",
                    Toast.LENGTH_SHORT).show();
        }else if(contrasena.isEmpty()){
            Toast.makeText(RegistrarUsuario.this, "ingrese su contraseña porfavor",
                    Toast.LENGTH_SHORT).show();
        }else {
            //conexion con la base de datos
            mAuth= FirebaseAuth.getInstance();

            //usamos el metodo de creacion de usarios de firebase
            mAuth.createUserWithEmailAndPassword(correo,contrasena)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //verificamos que se haya completado
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");

                                //ejecutamos la otra funcion de registro para que tambien lo guarde en la base de datos
                                registrarseCompleto();

                                // redirigimos al menu principal y finalizamos la vista
                                Intent ir=new Intent(RegistrarUsuario.this,MenuPrincipal.class);
                                startActivity(ir);
                                finish();
                            } else {
                                // esto se ejecutara en caso de que haya un error o problema
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            }
                        }
                    });
        }

    }

}
