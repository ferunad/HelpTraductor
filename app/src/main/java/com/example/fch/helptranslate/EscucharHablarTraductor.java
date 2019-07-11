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
import android.widget.Toast;

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

    //variables
    private TextToSpeech miVoz;
    private Translate translate;
    private String idioma,pais;
    private Button botonTraducir,botonEspanol, botonGrabar,botonTutorial;
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

        //conexion con el xml
        botonTraducir= findViewById(R.id.emergencia);
        spinner = (Spinner) findViewById(R.id.spinnerIdiomas);
        textoTraducir =(EditText)findViewById(R.id.mensajeGrande);
        listaElementos =findViewById(R.id.ListaTareas2);
        botonEspanol= findViewById(R.id.espanol);
        botonTutorial= findViewById(R.id.botonTutorial);
        botonGrabar = (Button) findViewById(R.id.grabar);
        botonVolver=findViewById(R.id.botonVolver);
        cargando=findViewById(R.id.cargando);
        nohayelementos=findViewById(R.id.nohayelementos);
        textoTraducido= findViewById(R.id.mensajeGrande2);

        //propiedades iniciales
        nohayelementos.setVisibility(View.GONE);
        listaElementos.setClickable(true);

        //lista con los idiomas
        final String[] nombre = {"Chino", "German", "Ingles", "hindi", "Arabe", "Portugués", "Bengali", "Ruso", "Japones", "Panyabí", "French"};
        final String[] letra = {"zh-TW", "de", "en", "hi", "ar", "pt", "bd", "ru", "ja", "pa", "fr",};
        final String[] lugar = {"CHN", "DEU", "USA", "NPL", "SAU", "PRT", "BGD", "RUS", "JPN", "IND", "FRA",};

        //creamos el adapter del spinner y le asignamos los datos
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nombre));

        //listener del spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                //leemos la informacion de la eleccion
                idioma=(String) adapterView.getItemAtPosition(pos);

                //hacemos un for para settear el pais segund el idioma
                for(int i=0;i<nombre.length;i++){
                    if(nombre[i]==idioma){
                        idioma=letra[i];
                        pais=lugar[i];
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {}
        });

        //listeners de los botones
        botonGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarPermisos();
            }
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
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        botonTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTranslateService();
                traducir("Esta app HelpTranslator y todo lo que yo escribe lo traducirá en audio y cuando yo le acerque el teléfono a usted me debera hablar para que la app me traduzca a texto. Gracias.");

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

                //iniciamos el traductor
                getTranslateService();

                //llamamos al metodo de traducir
                traducir(mensaje);
            }
        });
    }

    //metodo para llamar el reocnocimiento de voz de google
    public void grabar(){
        //metodo de reconocimiento de voz integrado con el de google
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        //extraemos el lenguaje elejido por el usuario
        String language = idioma+"-"+pais;

        //setteamos las preferencias de usuario
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS, language);

        //iniciamos el resultado de la actividad
        startActivityForResult(intent, 10);

    }

    //cuando la actividad de reconocer termine con exito elejimos lo que nos interese
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10: {
                String texto="";
                try {

                //Creamos un arrayList donde guardamos los resultados
                ArrayList<String> resultado=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                //seleccionamos el mas fiable
                texto = traducirTextualmente(resultado.get(0).trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //lo colocamos en el edit text
                textoTraducido.setText(texto);
                break;
            }
        }
    }

    //metodo para escuchar cualquier mensaje por tts
    public void escucharMensaje(final String textoADecir){

        //creamos un nuevo tts
        miVoz = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    //extraemos la opcion del usuario y lo colocamos en el tts para que tradusca segun el idioma y la entonacion
                    Locale localeToUse = new Locale(idioma,pais);
                    miVoz.setLanguage(localeToUse);

                    //iniciamos el metodo para hablar
                    miVoz.speak(textoADecir,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    }

    //metodo para escuhcar cualquier mensaje pero en español
    public void escucharMensajeEnEspañol(final String textoADecir){
        miVoz = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                    //extraemos la opcion del usuario y lo colocamos en el tts para que tradusca segun el idioma y la entonacion
                    Locale localeToUse = new Locale("es","COL");
                    miVoz.setLanguage(localeToUse);

                    //iniciamos el metodo para hablar
                    miVoz.speak(textoADecir,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });
    }

    // metodo para traducir
    public void  traducir(String mensaje){

        //usamos la api de traduccion de google
        Translation translation = translate.translate(mensaje, TranslateOption.sourceLanguage("es"), TranslateOption.targetLanguage(idioma));

        //iniciamos el metodo de escuchar
        escucharMensaje(translation.getTranslatedText());
    }


    //traducimos el resultado del audio a texto
    public String traducirTextualmente(String mensaje)throws Exception{

        //inicialisamos el servicio de traduccion
        getTranslateService();

        //usamos la api de traduccion de google
        Translation translation = translate.translate(mensaje, TranslateOption.sourceLanguage(idioma), TranslateOption.targetLanguage("es"));
        String resultado = translation.getTranslatedText();
        return resultado;
    }

    //metodo para definir las credeciale para usar la api de google de traductor
    public void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

            //Gobtener las credenciales
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //ponemos las credeciale sen el tradcutor
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //meoto para verificar el permiso para leer el almacenamiento
    public void verificarPermisos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 10);
        } else {
            verificarAudioPermisos();

        }
    }

    //meoto para verificar el permiso para usar el microfono
    public void verificarAudioPermisos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO },
                    10);
        } else {
            grabar();
        }
    }


    //resultados de pedir los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                grabar();
            }else{
                Toast.makeText(EscucharHablarTraductor.this, "Se necesita que se acepten los permisos para usar esta funcionalidad", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
