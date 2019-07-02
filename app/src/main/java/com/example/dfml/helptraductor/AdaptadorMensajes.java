package com.example.dfml.helptraductor;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class AdaptadorMensajes extends BaseAdapter   {
    //Variables
    private Context context;
    private ArrayList<Mensajes> listItems;

    // Getters, setter y constructor
    public AdaptadorMensajes(Context context, ArrayList<Mensajes> listItems) {
        this.context = context;
        this.listItems = listItems;
    }
    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    //Metodo para crear las vistas de los elementos de la lista
    public View getView(int position, View convertView, ViewGroup parent) {
        Mensajes item=(Mensajes) getItem(position);

        convertView=LayoutInflater.from(context).inflate(R.layout.item_mensajes,null);

        //referenciamos el textview de la plantilla
        TextView titulo=(TextView) convertView.findViewById(R.id.titulo);

        //cambiamos el texto de la plantilla en base a la información del item
        titulo.setText(item.getTituloMensaje());
        return convertView;
    }
}
