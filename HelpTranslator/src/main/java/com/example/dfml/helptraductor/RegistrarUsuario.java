package com.example.dfml.helptraductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrarUsuario extends AppCompatActivity {
    private static final String TAG="Iniciar Sesión";
    private Button boton;
    private FirebaseDatabase baseDatos;
    private FirebaseAuth mAuth;


    private EditText TextoCorreo,TextoContrasena,TextoCelular,TextoNombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        TextoCorreo=findViewById(R.id.correo);
        TextoContrasena=findViewById(R.id.contrasena);
        TextoCelular=findViewById(R.id.celular);
        TextoNombre=findViewById(R.id.nombre);
        boton=findViewById(R.id.botonEntrar);


        baseDatos=FirebaseDatabase.getInstance();

        boton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                registrarse();
            }
        });
    }

    private void registrarseCompleto(){
        // base de datos
        FirebaseUser wenas=mAuth.getCurrentUser();
        //extracion de datos
        String correo=TextoCorreo.getText().toString();
        String contrasena=TextoContrasena.getText().toString();
        String nombre=TextoNombre.getText().toString();
        String celular=TextoCelular.getText().toString();
        String id = wenas.getUid();
        InicioSesionTodosLosCasos usuario=new InicioSesionTodosLosCasos(RegistrarUsuario.this);
        usuario.registroUsuarios( correo, nombre,celular,contrasena,id);

    }
    private void registrarse(){
        String correo=TextoCorreo.getText().toString();
        String contrasena=TextoContrasena.getText().toString();
        if (correo.isEmpty()) {
            Toast.makeText(RegistrarUsuario.this, "ingrese su correo porfavor",
                    Toast.LENGTH_SHORT).show();
        }else if(contrasena.isEmpty()){
            Toast.makeText(RegistrarUsuario.this, "ingrese su contraseña porfavor",
                    Toast.LENGTH_SHORT).show();
        }else {
            mAuth= FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(correo,contrasena)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                registrarseCompleto();
                                Intent ir=new Intent(RegistrarUsuario.this,MenuPrincipal.class);
                                startActivity(ir);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            }

                            // ...
                        }
                    });

        }

    }

}
