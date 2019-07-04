package com.example.fch.helptranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;


public class EscucharHablarTraductor extends AppCompatActivity{
    private TextToSpeech miVoz;
    private Translate translate;
    private String idioma,pais;
    private Button botonTraducir,botonEspanol, b1,botonTutorial;
    private Spinner spinner;
    private ImageView  botonVolver;
    private EditText textoTraducir,textoTraducido;
    private ListView listaElementos;
    private AdaptadorMensajesSinBoton adaptador;
    private FirebaseDatabase baseDatos;
    private String TAG ="Traductor" ;
    private ArrayList<Mensajes> listadeMensajes =new ArrayList<Mensajes>();
    private TextView cargando,nohayelementos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escuchar_hablar_traductor);
        botonTraducir= findViewById(R.id.emergencia);
        spinner = (Spinner) findViewById(R.id.spinnerIdiomas);
        textoTraducir =(EditText)findViewById(R.id.textoTraducir);
        listaElementos =findViewById(R.id.ListaTareas2);
        botonEspanol= findViewById(R.id.espanol);
        botonTutorial= findViewById(R.id.botonTutorial);
        b1 = (Button) findViewById(R.id.grabar);
        botonVolver=findViewById(R.id.botonVolver);
        cargando=findViewById(R.id.cargando);
        nohayelementos=findViewById(R.id.nohayelementos);
        textoTraducido= findViewById(R.id.textoTraducido);

        //propiedades iniciales
        nohayelementos.setVisibility(View.GONE);
        listaElementos.setClickable(true);



        final String[] letra = {"en","de","ru","pt","ja","hi","fr","zh-TW","ar"};
        final String[] lugar = {"USA","DEU","RUS","PRT","JPN","NPL","FRA","CHN","SAU"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecordBtnClickedStorge();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {

                idioma=(String) adapterView.getItemAtPosition(pos);
                for(int i=0;i<letra.length;i++){
                    if(letra[i]==idioma){
                        pais=lugar[i];
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {}
        });
        botonEspanol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escucharMensajeEnEspañol(textoTraducir.getText().toString());

            }
        });
        botonTraducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getTranslateService();
                    traducir(textoTraducir.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //boton de regresar que finaliza la activity para volver a la anterior
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                adaptador=new AdaptadorMensajesSinBoton(EscucharHablarTraductor.this, listadeMensajes);
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
                getTranslateService();
                traducir(mensaje);

            }
        });
        botonTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTranslateService();
                traducir("Esta app HelpTranslator y todo lo que yo escribe lo traducirá en audio y cuando yo le acerque el teléfono a usted me debera hablar para que la app me traduzca a texto. Gracias.");

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10: {
                String texto="";
                try {
                ArrayList<String> resultado=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                texto = traducirTextualmente(resultado.get(0).trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textoTraducido.setText(texto);
                break;
            }

        }
    }


    public void escucharMensaje(final String textoADecir){
        miVoz = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    Locale localeToUse = new Locale(idioma,pais);
                    miVoz.setLanguage(localeToUse);
                    miVoz.speak(textoADecir,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    }

    public void escucharMensajeEnEspañol(final String textoADecir){
        miVoz = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    Locale localeToUse = new Locale("es","COL");
                    miVoz.setLanguage(localeToUse);
                    miVoz.speak(textoADecir,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    }
    public void  traducir(String mensaje){
        // Instantiates a client

        // Translates some text into Russian
        Translation translation = translate.translate(mensaje, TranslateOption.sourceLanguage("es"), TranslateOption.targetLanguage(idioma));
        escucharMensaje(translation.getTranslatedText());
    }

    public String traducirTextualmente(String mensaje)throws Exception{
        // Instantiates a client
        // Translates some text into Russian
        getTranslateService();
        Translation translation = translate.translate(mensaje, TranslateOption.sourceLanguage("en"), TranslateOption.targetLanguage("es"));
        String hola = translation.getTranslatedText();
        return hola;
    }

    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();

            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public void grabar(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        String language = idioma+"-"+pais;
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS, language);
        startActivityForResult(intent, 10);

    }
    public void onRecordBtnClickedStorge() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    10);
        } else {
            onRecordBtnClicked();

        }
    }

    public void onRecordBtnClicked() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO },
                    10);
        } else {
            grabar();
        }
    }
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                grabar();
            }else{
                //User denied Permission.
            }
        }
    }

}
