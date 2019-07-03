package com.example.dfml.helptraductor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Locale;

public class ListaMensajes extends AppCompatActivity {


    //Variables
    private ListView listaElementos;
    private ImageView botonRegresar,botonNuevo;
    private AdaptadorMensajes adaptador;
    private FirebaseDatabase baseDatos;
    private String TAG ="Lista Mensajes" ;
    private ArrayList<Mensajes> listadeMensajes =new ArrayList<Mensajes>();
    private TextView cargando,nohayelementos;
    TextToSpeech myTTS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mensajes);

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


        //Traemos todos los mensajes del usuario
        Query myTopPostsQuery = temporal.child("Mensajes").orderByChild("starCount");
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {

            //traermos una copia de los datos de la base de datos
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listadeMensajes.clear();
                //transcribimos esa copia de los mensajes a un objeto y lo agregamos a un array list
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Mensajes temporal=postSnapshot.getValue(Mensajes.class);
                    String tituloMensajes=temporal.getTituloMensaje();
                    String mensajes=temporal.getMensajes();
                    Mensajes mensajesTemporal=new Mensajes(tituloMensajes,mensajes);
                    listadeMensajes.add(mensajesTemporal);
                }

                //letrero de cargando
                cargando.setVisibility(View.GONE);

                //aviso en caso de que no haya elementos que mostrar
                if(listadeMensajes.size()==0 || listadeMensajes ==null){
                    nohayelementos.setVisibility(View.VISIBLE);
                }else{
                    nohayelementos.setVisibility(View.GONE);
                }

                //usamos el adaptador para mostrar la lista
                adaptador=new AdaptadorMensajes(ListaMensajes.this, listadeMensajes);
                listaElementos.setAdapter(adaptador);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // en caso de algun error con la conexion con la base de datos saldra este error
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        // listener para ejecutar accion cada vez que se oprima sobre el mensaje
        listaElementos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //obtenmos los datos de ese objeto
                Mensajes temporal = (Mensajes) adaptador.getItem(position);

                //separamos los datos que nos interesan en variables aparte
                String tituloMensaje=temporal.getTituloMensaje();
                String mensaje=temporal.getMensajes();

                //creamos un bundle para mover datos a otra pantalla
                Bundle nombreBaseDatos=new Bundle();

                //ingresamos los datos en el bundle
                nombreBaseDatos.putString("tituloMensaje",tituloMensaje);
                nombreBaseDatos.putString("mensajes",mensaje);


                //cambiamos de activity y enviamos el bundle
                Intent vista=new Intent(ListaMensajes.this,OpcionesMensajes.class);
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

        //boton que lleva a la pantalla para crear mensajes
        botonNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent elemento=new Intent(ListaMensajes.this, CrearMensajes.class);
                startActivity(elemento);
            }
        });
    }




    public void escuchar(View view) {

        ConstraintLayout parentRow = (ConstraintLayout) view.getParent();

        TextView vistaDelTitulo = (TextView) parentRow.findViewById(R.id.titulo);

        String titulo=vistaDelTitulo.getText().toString();

        for(final Mensajes x:listadeMensajes){
            if(x.getTituloMensaje()==titulo){
                myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            // replace this Locale with whatever you want
                            Locale localeToUse = new Locale("es","COL");
                            myTTS.setLanguage(localeToUse);
                            myTTS.speak(x.getMensajes(),TextToSpeech.QUEUE_FLUSH,null);
                        }
                    }
                });


            }

        }
    }

}
