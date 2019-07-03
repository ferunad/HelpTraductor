package com.example.dfml.helptraductor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaContactos extends AppCompatActivity {

    //Variables
    private ListView listaElementos;
    private ImageView botonRegresar,botonNuevo;
    private AdaptadorContactos adaptador;
    private FirebaseDatabase baseDatos;
    private String TAG ="Lista Contactos" ;
    private ArrayList<Contactos> listadeContactos =new ArrayList<Contactos>();
    private TextView cargando,nohayelementos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        //conexiones con el xml
        botonRegresar=findViewById(R.id.botonVolver);
        botonNuevo=findViewById(R.id.botonNuevo);
        listaElementos =findViewById(R.id.ListaTareas2);
        cargando=findViewById(R.id.cargando);
        nohayelementos=findViewById(R.id.nohayelementos);

        //propiedades iniciales
        nohayelementos.setVisibility(View.GONE);
        listaElementos.setClickable(true);

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

                //letrero de cargando
                cargando.setVisibility(View.GONE);

                //aviso en caso de que no haya elementos que mostrar
                if(listadeContactos.size()==0 || listadeContactos ==null){
                    nohayelementos.setVisibility(View.VISIBLE);
                }else{
                    nohayelementos.setVisibility(View.GONE);
                }

                //usamos el adaptador para mostrar la lista
                adaptador=new AdaptadorContactos(ListaContactos.this, listadeContactos);
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
                String nombre=temporal.getNombre();
                String numero=temporal.getNumero();

                //creamos un bundle para mover datos a otra pantalla
                Bundle nombreBaseDatos=new Bundle();

                //ingresamos los datos en el bundle
                nombreBaseDatos.putString("correo",correo);
                nombreBaseDatos.putString("nombre",nombre);
                nombreBaseDatos.putString("celular",numero);

                //cambiamos de activity y enviamos el bundle
                Intent vista=new Intent(ListaContactos.this,OpcionesContactos.class);
                vista.putExtras(nombreBaseDatos);
                startActivity(vista);

            }
        });

        //boton de regresar que finaliza la activity para volver a la anterior
        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //boton que lleva a la pantalla para crear contactos
        botonNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent elemento=new Intent(ListaContactos.this, CrearContacto.class);
                startActivity(elemento);
            }
        });
    }
}
