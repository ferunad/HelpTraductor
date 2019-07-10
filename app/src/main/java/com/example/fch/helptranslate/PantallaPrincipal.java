package com.example.fch.helptranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class PantallaPrincipal extends AppCompatActivity implements View.OnClickListener {

    //variables
    public static final int RC_SIGN_IN=0;
    public static final String TAG="SingInOpertation";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private EditText TextoCorreo,TextoContrasena;
    private Button boton,botonRegistrar;
    private FirebaseDatabase baseDatos;
    private CallbackManager callbackManager;
    private LoginButton facebook;

    //private OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_principal);


        //conexiones con el xml
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        TextoCorreo=findViewById(R.id.correo);
        TextoContrasena=findViewById(R.id.contrasena);
        boton=findViewById(R.id.botonCrear);
        boton=findViewById(R.id.botonCrear);
        botonRegistrar=findViewById(R.id.botonRegistrarse);
        facebook=findViewById(R.id.login_button);

        //Acceder con google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.tokenIngreso)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //opciones visuales del boton de google
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener( this);

        //conectamos con la base de datos
        mAuth = FirebaseAuth.getInstance();
        baseDatos=FirebaseDatabase.getInstance();
        callbackManager = CallbackManager.Factory.create();

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

        facebook = (LoginButton) findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });


    }


    @Override
    protected void onStart() {
        super.onStart();

        //revisamos si ya hay un cuenta activa
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){

        }else{
            Intent verificacion=new Intent(PantallaPrincipal.this,MenuPrincipal.class);
            startActivity(verificacion);
        }
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
    @Override
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
            firebaseAuthWithGoogle(account);
            Intent ir=new Intent(PantallaPrincipal.this,MenuPrincipal.class);
            startActivity(ir);
        } catch (ApiException e) {
            // esto se ejecutara si ahi algun error
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }
    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            guardarDatosFirebase(acct);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());


                        }

                        // ...
                    }
                });
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
    public void guardarDatosFirebase(GoogleSignInAccount acct){

        Log.d(TAG, "signInWithCredential:success");
        FirebaseUser user = mAuth.getCurrentUser();
        //extracion de datos
        String correo= acct.getEmail();
        String nombre= acct.getDisplayName();
        String numeroBasico="0";
        String contrasena="0";
        String uid=user.getUid();

        //registramos el usuario en la base de datos basandonos en los datos que ingreso para la autentificacion con firebase
        InicioSesionTodosLosCasos usuario=new InicioSesionTodosLosCasos(PantallaPrincipal.this);

        //usarmos el metodo de registro de usurios creado anteriormente
        usuario.registroUsuarios( correo, nombre,numeroBasico,contrasena,uid);

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //extracion de datos
                            String correo= user.getEmail();
                            String nombre= user.getDisplayName();
                            String numeroBasico="0";
                            String contrasena="0";
                            String uid=user.getUid();

                            //registramos el usuario en la base de datos basandonos en los datos que ingreso para la autentificacion con firebase
                            InicioSesionTodosLosCasos usuario=new InicioSesionTodosLosCasos(PantallaPrincipal.this);

                            //usarmos el metodo de registro de usurios creado anteriormente
                            usuario.registroUsuarios( correo, nombre,numeroBasico,contrasena,uid);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(PantallaPrincipal.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


}
