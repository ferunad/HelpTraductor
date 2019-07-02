package com.example.dfml.helptraductor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class ListaApliacaciones extends AppCompatActivity {

    //Variables
    private ListView listaElementos;
    private AdaptadorAplicaciones adaptador;
    private String TAG ="Links aplicaciones" ;
    private ArrayList<listados> listadeElemtnosNo=new ArrayList<listados>();
    private listados[] listaAplicaciones = new listados[9];
    private ImageView botonRegresar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_apliacaciones);

        // conexiones de elementos con el xml
        listaElementos =findViewById(R.id.ListaTareas2);
        botonRegresar=findViewById(R.id.botonVolver);

        //creacion de los objetos de la lista
        listaAplicaciones[0]=new listados("Asistente de sordos y mudos","https://play.google.com/store/apps/details?id=com.ncatz.yeray.deafmutehelper&hl=es");
        listaAplicaciones[1]=new listados("Sordo Ayuda","https://play.google.com/store/apps/details?id=com.yeho.tuvoz&hl=es");
        listaAplicaciones[2]=new listados("Dilo en señas","https://play.google.com/store/apps/details?id=com.jaguarlabs.lsm&hl=es");
        listaAplicaciones[3]=new listados("Despertador para Sordos (mDVA)","https://play.google.com/store/apps/details?id=ar.com.innoligent.moderndva2&hl=es");
        listaAplicaciones[4]=new listados("Háblalo","https://play.google.com/store/apps/details?id=appinventor.ai_mateo_nicolas_salvatto.Sordos&hl=es");
        listaAplicaciones[5]=new listados("aprender lenguaje de señas","https://play.google.com/store/apps/details?id=mimo.language.sign&hl=es");
        listaAplicaciones[6]=new listados("Sordo-Mudo Ayudante","https://play.google.com/store/apps/details?id=com.jpgironb.assistiveguru&hl=es");
        listaAplicaciones[7]=new listados("Aprende el lenguaje de señas gratis","https://play.google.com/store/apps/details?id=com.LearnSignLanguageFree.Mimpiandroid&hl=es");
        listaAplicaciones[8]=new listados("STranscripción instantánea","https://play.google.com/store/apps/details?id=com.google.audio.hearing.visualization.accessibility.scribe&hl=es");



        //agregar los objetos al arraylist
        for(int i = 0; i< listaAplicaciones.length; i++){
            listadeElemtnosNo.add(listaAplicaciones[i]);
        }

        // hacemos que se pueda interactuar con la lista
        listaElementos.setClickable(true);

        //Creamos el adaptador que se encarga de traducir los elementos del arraylist a la lista visual
        adaptador=new AdaptadorAplicaciones(ListaApliacaciones.this,listadeElemtnosNo);
        listaElementos.setAdapter(adaptador);

        //creamos el listener encargado de las accciones en caso de que se interactue con un elemento de la lista
        listaElementos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listados item = (listados) adaptador.getItem(position);
                String nombre=item.getNombre();
                String url=item.getUrl();
                Intent vista=new Intent("android.intent.action.VIEW", Uri.parse(url));
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
    }
}
