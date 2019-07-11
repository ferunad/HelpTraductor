package com.example.fch.helptranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class EnviarCorreo extends AppCompatActivity {


    private ListView listaElementos;
    private ImageView botonRegresar;
    private AdaptadorContactos adaptador;
    private FirebaseDatabase baseDatos;
    private EditText para,asunto;
    private String TAG ="Compartir video" ;
    private ArrayList<Contactos> listadeContactos =new ArrayList<Contactos>();
    private Button enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_correo);

        //conexiones con el xml
        botonRegresar=findViewById(R.id.botonVolver);
        listaElementos =findViewById(R.id.ListaTareas2);
        para=findViewById(R.id.para);
        asunto=findViewById(R.id.asunto);
        enviar=findViewById(R.id.botonEnviar);


        //revisamos de cual usuario debemos traer la información
        FirebaseUser usuarioActual= FirebaseAuth.getInstance().getCurrentUser();
        String id = usuarioActual.getUid();


        //Conexion con la base de datos
        baseDatos = FirebaseDatabase.getInstance();
        DatabaseReference temporal = baseDatos.getReference("Usuarios/"+ id);


        //Traemos todos los contactos del usuario
        Query myTopPostsQuery = temporal.child("Contactos").orderByChild("starCount");
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {

            //traermos una copia de los datos de la base de datos
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listadeContactos.clear();
                //transcribimos esa copia de los contactos a un objeto y lo agregamos a un array list
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Contactos temporal=postSnapshot.getValue(Contactos.class);
                    String correo=temporal.getCorreo();
                    String nombre=temporal.getNombre();
                    String numero=temporal.getNumero();
                    Contactos usuario=new Contactos(nombre,correo,numero);
                    listadeContactos.add(usuario);
                }



                //usamos el adaptador para mostrar la lista
                adaptador=new AdaptadorContactos(EnviarCorreo.this, listadeContactos);
                listaElementos.setAdapter(adaptador);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // en caso de algun error con la conexion con la base de datos saldra este error
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        // listener para ejecutar accion cada vez que se oprima sobre el contacto
        listaElementos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //obtenmos los datos de ese objeto
                Contactos temporal = (Contactos) adaptador.getItem(position);

                //separamos los datos que nos interesan en variables aparte
                String correo=temporal.getCorreo();

                para.setText(correo);
            }
        });

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    protected void sendEmail() {
        String[] TO = {para.getText().toString()}; //aquí pon tu correo
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto.getText().toString());
        final String Url=getIntent().getExtras().getString("url");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Url);
        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email..."));

            Toast.makeText(EnviarCorreo.this, "Email Enviando", Toast.LENGTH_SHORT).show();
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EnviarCorreo.this, "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
        }
    }
}
