package com.example.fch.helptranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListaVideos extends AppCompatActivity {

    //Variables
    private ListView listaElementos;
    private AdaptadorVideos adaptador;
    private String TAG ="Link Videos" ;
    private ArrayList<listados> listadeElemtnosNo=new ArrayList<listados>();
    private listados[] listaVideos= new listados[22];
    private ImageView botonRegresar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_videos);

        // conexiones de elementos con el xml
        listaElementos =findViewById(R.id.ListaTareas2);
        botonRegresar=findViewById(R.id.botonVolver);

        //creacion de los objetos de la lista
        listaVideos[0]=new listados("Vocabulario Básico 1 - Lengua de Señas Colombiana LSC","EOcVvy1mcYI");
        listaVideos[1]=new listados("Vocabulario Básico 2 - Lengua de Señas Colombiana LSC","q8j1aXIRCv8");
        listaVideos[2]=new listados("Vocabulario Básico 3 - Verbos Curso Lengua de Señas Colombiana","-8e3FaA-Rak");
        listaVideos[3]=new listados("Vocabulario Básico 4 - Verbos Curso Lengua de Señas Colombiana","-8e3FaA-Rak");
        listaVideos[4]=new listados("Lección 1 - Cómo saludar en lengua de señas (LSC)","BTe1ulzazio");
        listaVideos[5]=new listados("Lección 2 - Las diez señas que debes saber (LSC)","xXC_x4Jvnj8");
        listaVideos[6]=new listados("10 Señas Básicas (LSM) | Tutorial Rápido","rLL4LJdPRtY");
        listaVideos[7]=new listados("Aprende la Lengua de Señas de tu país con 26 Canales de YouTube","PO9pBiirH1Q");
        listaVideos[8]=new listados("SALUDOS Y EXPRESIONES DE CORTESIA","1MB4zx7Jq-Y");
        listaVideos[9]=new listados("EMOCIONES","R4AkgkXx_yg");
        listaVideos[10]=new listados("ADJETIVOS","-D5YsNax_1I");
        listaVideos[11]=new listados("¿Quién es una persona sorda? ¿Porqué no somos sordomudos?","YOhdHDNjxb8");
        listaVideos[12]=new listados("PRONOMBRES INTERROGATIVOS","1Q7CBRz7cvY");
        listaVideos[13]=new listados("FAMILIA","wheNDU97YcM");
        listaVideos[14]=new listados("PRONOMBRES","hbDjqgzW8_4w");
        listaVideos[15]=new listados("No es lenguaje de señas, es Lengua de Señas","erzaw5M0QuY");
        listaVideos[16]=new listados("¿Qué es Identidad? Y Cuales aspectos no hacen parte de la Identidad Sorda","1i3C059nroA");
        listaVideos[17]=new listados("Cursos virtuales Lengua de Señas Colombiana","8Dtl9FOfaGs");
        listaVideos[18]=new listados("Historia de la LSC - Parte 1","HGgfpkqF9wA");
        listaVideos[19]=new listados("Historia de la LSC - Parte 2","nYGqvhAnSzw");
        listaVideos[20]=new listados("Historia de la LSC - Parte 3","T-kZevrwooU");
        listaVideos[21]=new listados("Aprende la Lengua de Señas de tu país con 26 Canales de YouTube","7nw1jdO1Gz8");

        //agregar los objetos al arraylist
        for(int i=0;i<listaVideos.length;i++){
            listadeElemtnosNo.add(listaVideos[i]);
        }

        // hacemos que se pueda interactuar con la lista
        listaElementos.setClickable(true);

        //Creamos el adaptador que se encarga de traducir los elementos del arraylist a la lista visual
        adaptador=new AdaptadorVideos(ListaVideos.this,listadeElemtnosNo);
        listaElementos.setAdapter(adaptador);

        //creamos el listener encargado de las accciones en caso de que se interactue con un elemento de la lista
        listaElementos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listados item = (listados) adaptador.getItem(position);
                String nombre=item.getNombre();
                String url=item.getUrl();

                //creamos un bundle para mover datos a otra pantalla
                Bundle linkVideo=new Bundle();

                //ingresamos los datos en el bundle
                linkVideo.putString("Url",url);
                
                //cambiamos de activity y enviamos el bundle
                Intent vista=new Intent(ListaVideos.this,FullscreenDemoActivity.class);
                vista.putExtras(linkVideo);
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
