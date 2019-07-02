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

public class ListaDiccionarios extends AppCompatActivity {

    //Variables
    private ListView listaElementos;
    private AdaptadorDiccionarios adaptador;
    private String TAG ="Links diccionarios" ;
    private ArrayList<listados> listadeElemtnosNo=new ArrayList<listados>();
    private listados[] listaDiccionario = new listados[10];
    private ImageView botonRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_diccionarios);

        // conexiones de elementos con el xml
        listaElementos =findViewById(R.id.ListaTareas2);
        botonRegresar=findViewById(R.id.botonVolver);

        //creacion de los objetos de la lista
        listaDiccionario[0]=new listados("Abecedario Paraguay","https://drive.google.com/open?id=1jRwn7h6FIMf-_ZBw0WVpXcSKmkSowCjy");
        listaDiccionario[1]=new listados("Abecedario Panama","https://drive.google.com/open?id=12-zS_UVUhQpL2wokbSB-JycwH6R3RRxP");
        listaDiccionario[2]=new listados("Abecedario Nicaragua","https://drive.google.com/open?id=17mhQIffpnNOFV5HRsdgRS4LUDu04rz0N");
        listaDiccionario[3]=new listados("Abecedario Cuba","https://drive.google.com/open?id=1VlZa4AL3hOYxX9RpIu_hvyOHKkFNCJSm");
        listaDiccionario[4]=new listados("Abecedario Costarica","https://drive.google.com/open?id=1nXD_xJlzr1eUT1b_nfOVXvgb7x6aKB38");
        listaDiccionario[5]=new listados("Abecedario Colombia","https://drive.google.com/open?id=106mhHyKbtvsOWcn4OIMZ7OGy3cOHcHi8");
        listaDiccionario[6]=new listados("Abecedario Brasil","https://drive.google.com/open?id=1Loy00FuIxLCfDWfdniL44oLw_dCDelf5");
        listaDiccionario[7]=new listados("Abecedario Chile","https://drive.google.com/open?id=1vhYFSJmwVJkWVInUPLkIoL_Hv6yTavu6");
        listaDiccionario[8]=new listados("Abecedario España","https://drive.google.com/open?id=1J0soZAYeF4ddYR2QwvPHaI9QlfIj4i3G");
        listaDiccionario[9]=new listados("Abecedario Bolivia","https://drive.google.com/open?id=1lS1X5uJXooaTV96WEkwK83NHvdTM9Ya4");

        //agregar los objetos al arraylist
        for(int i = 0; i< listaDiccionario.length; i++){
            listadeElemtnosNo.add(listaDiccionario[i]);
        }

        // hacemos que se pueda interactuar con la lista
        listaElementos.setClickable(true);

        //Creamos el adaptador que se encarga de traducir los elementos del arraylist a la lista visual
        adaptador=new AdaptadorDiccionarios(ListaDiccionarios.this,listadeElemtnosNo);
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
