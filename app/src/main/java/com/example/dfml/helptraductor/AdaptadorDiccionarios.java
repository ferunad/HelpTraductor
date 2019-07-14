package com.example.dfml.helptraductor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorDiccionarios extends BaseAdapter   {
    //Variables
    private Context context;
    private ArrayList<listados> listItems;
//Prueba comentario
    // Getters, setter y constructor
    public AdaptadorDiccionarios(Context context, ArrayList<listados> listItems) {
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

    //Metodo para crear las vistas de los elementos de la lista
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        listados item=(listados) getItem(position);

        //asignamos la view a el xml plantilla
        convertView=LayoutInflater.from(context).inflate(R.layout.item_diccionarios,null);

        //referenciamos el textview de la plantilla
        TextView titulo=(TextView) convertView.findViewById(R.id.titulo);

        //cambiamos el texto de la plantilla en base a la información del item
        titulo.setText(item.getNombre());
        return convertView;
    }
}
