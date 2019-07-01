package com.example.dfml.helptraductor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MenuPrincipal extends AppCompatActivity {

    private ImageButton botonVideos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        botonVideos= findViewById(R.id.videos);


        botonVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent video=new Intent(MenuPrincipal.this,ListaVideos.class);
                startActivity(video);
            }
        });
    }
}
