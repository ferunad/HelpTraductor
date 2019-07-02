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

    public static final int RC_SIGN_IN=0;
    public static final String TAG="SingInOpertation";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private EditText TextoCorreo,TextoContrasena;
    private Button boton,botonFacebook,botonRegistrar;
    private OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_principal);
        //Acceder con google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener( this);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        //acceder con correo y contrasena
        TextoCorreo=findViewById(R.id.correo);
        TextoContrasena=findViewById(R.id.contrasena);
        boton=findViewById(R.id.botonEntrar);
        botonFacebook=findViewById(R.id.botonFacebook);
        boton=findViewById(R.id.botonEntrar);
        botonRegistrar=findViewById(R.id.botonRegistrarse);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicioSesion();
            }
        });

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registrar= new Intent(PantallaPrincipal.this,RegistrarUsuario.class);
                startActivity(registrar);
            }
        });
        botonFacebook.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
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
    /*
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.w(TAG, "si junciono" );

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }
    private void inicioSesion(){
        String correo=TextoCorreo.getText().toString();
        String contrasena=TextoContrasena.getText().toString();
        if (correo.isEmpty()) {
            Toast.makeText(PantallaPrincipal.this, "ingrese su correo porfavor",
                    Toast.LENGTH_SHORT).show();
        }else if(contrasena.isEmpty()){
            Toast.makeText(PantallaPrincipal.this, "ingrese su contraseña porfavor",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(PantallaPrincipal.this, "Iniciando sesión...",
                    Toast.LENGTH_SHORT).show();
            mAuth= FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                InicioSesionTodosLosCasos hola=new InicioSesionTodosLosCasos(PantallaPrincipal.this);
                                Intent ir=new Intent(PantallaPrincipal.this, MenuPrincipal.class);
                                startActivity(ir);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(PantallaPrincipal.this, "Usuario o Contraseña incorrectos",
                                        Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });

        }
    }


}
