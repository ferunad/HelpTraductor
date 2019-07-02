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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;

public class PantallaPrincipal extends AppCompatActivity implements View.OnClickListener {

    //variables
    public static final int RC_SIGN_IN=0;
    public static final String TAG="SingInOpertation";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private EditText TextoCorreo,TextoContrasena;
    private Button boton, botonMicrosoft,botonRegistrar;
    private OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_principal);

        //conexiones con el xml
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        TextoCorreo=findViewById(R.id.correo);
        TextoContrasena=findViewById(R.id.contrasena);
        boton=findViewById(R.id.botonEntrar);
        botonMicrosoft =findViewById(R.id.botonFacebook);
        boton=findViewById(R.id.botonEntrar);
        botonRegistrar=findViewById(R.id.botonRegistrarse);

        //Acceder con google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //opciones visuales del boton de google
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener( this);

        //conectamos con la base de datos
        mAuth = FirebaseAuth.getInstance();

        //boton que ejecuta los metodos para acceder con correo y contrasena
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicioSesion();
            }
        });

        //boton que redirecciona a registrar
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registrar= new Intent(PantallaPrincipal.this,RegistrarUsuario.class);
                startActivity(registrar);
            }
        });

        //boton para registrarse con cuenta de microsoft
        botonMicrosoft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //metodo para inciar sesion con facebook
                mAuth.startActivityForSignInWithProvider(/* activity= */ PantallaPrincipal.this, provider.build())
                        .addOnSuccessListener(
                                new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        // User is signed in.
                                        // IdP data available in
                                        // authResult.getAdditionalUserInfo().getProfile().
                                        // The OAuth access token can also be retrieved:
                                        // authResult.getCredential().getAccessToken().
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle failure.
                                    }
                                });

            }

        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        //revisamos si ya hay un cuenta activa
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                // inciar secion con google
                signIn();
                break;
        }
    }

    //metodo para inciar sesión con google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //resultado de acceder
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            //ejecutamos la tarea de inciar sesión
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            //usamos el metodo para saber los resultados
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            //esto se ejecutara si se incio sesion correctamente y rediccionara al menu principal
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Intent ir=new Intent(PantallaPrincipal.this,MenuPrincipal.class);
            startActivity(ir);
        } catch (ApiException e) {
            // esto se ejecutara si ahi algun error
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    //metodo de inicio de sesion por firebase con correo y contraseña
    private void inicioSesion(){

        //extraemos los datos
        String correo=TextoCorreo.getText().toString();
        String contrasena=TextoContrasena.getText().toString();

        //filtros para revisar que los campos no esten vacios
        if (correo.isEmpty()) {
            Toast.makeText(PantallaPrincipal.this, "ingrese su correo porfavor", Toast.LENGTH_SHORT).show();
        }else if(contrasena.isEmpty()){
            Toast.makeText(PantallaPrincipal.this, "ingrese su contraseña porfavor", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(PantallaPrincipal.this, "Iniciando sesión...", Toast.LENGTH_SHORT).show();

            //conexion con la base de datos
            mAuth= FirebaseAuth.getInstance();

            //metodo de firebase para iniciar sesión
            mAuth.signInWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        //resultado de incio de sesión
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // inicio de sesión completo
                                Log.d(TAG, "signInWithEmail:success");
                                Intent ir=new Intent(PantallaPrincipal.this, MenuPrincipal.class);
                                startActivity(ir);
                                finish();
                            } else {
                                // en caso de error se ejecutara este codigo
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(PantallaPrincipal.this, "Usuario o Contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
