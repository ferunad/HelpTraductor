package com.example.dfml.helptraductor;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
public class AdaptadorVideos extends BaseAdapter   {
    private Context context;
    private ArrayList<listados> listItems;

    public AdaptadorVideos(Context context, ArrayList<listados> listItems) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        listados item=(listados) getItem(position);
        convertView=LayoutInflater.from(context).inflate(R.layout.item_videos,null);
        TextView titulo=(TextView) convertView.findViewById(R.id.titulo);
        titulo.setText(item.getNombre());
        return convertView;
    }
}
