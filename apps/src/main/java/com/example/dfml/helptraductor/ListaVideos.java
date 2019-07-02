package com.example.dfml.helptraductor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListaVideos extends AppCompatActivity {
    private ListView listaElementos;
    private AdaptadorVideos adaptador;
    private String TAG ="Link Videos" ;
    private ArrayList<listados> listadeElemtnosNo=new ArrayList<listados>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_videos);
        listaElementos =findViewById(R.id.ListaTareas2);
        listaElementos.setClickable(true);
        listados video1=new listados("Vocabulario Básico 1 - Lengua de Señas Colombiana LSC","https://youtu.be/EOcVvy1mcYI");
        listados video2=new listados("Vocabulario Básico 2 - Lengua de Señas Colombiana LSC","https://youtu.be/q8j1aXIRCv8");
        listados video3=new listados("Vocabulario Básico 3 - Verbos Curso Lengua de Señas Colombiana","https://youtu.be/-8e3FaA-Rak");
        listados video4=new listados("Vocabulario Básico 4 - Verbos Curso Lengua de Señas Colombiana","https://youtu.be/-8e3FaA-Rak");
        listados video5=new listados("Lección 1 - Cómo saludar en lengua de señas (LSC)","https://youtu.be/BTe1ulzazio");
        listados video6=new listados("Lección 2 - Las diez señas que debes saber (LSC)","https://youtu.be/xXC_x4Jvnj8");
        listados video7=new listados("10 Señas Básicas (LSM) | Tutorial Rápido","https://youtu.be/rLL4LJdPRtY");
        listadeElemtnosNo.add(video1);
        listadeElemtnosNo.add(video2);
        listadeElemtnosNo.add(video3);
        listadeElemtnosNo.add(video4);
        listadeElemtnosNo.add(video5);
        listadeElemtnosNo.add(video6);
        listadeElemtnosNo.add(video7);
        adaptador=new AdaptadorVideos(ListaVideos.this,listadeElemtnosNo);
        listaElementos.setAdapter(adaptador);

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

    }
}