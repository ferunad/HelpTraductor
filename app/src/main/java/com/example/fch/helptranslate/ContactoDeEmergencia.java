package com.example.fch.helptranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactoDeEmergencia extends AppCompatActivity {
    private FirebaseDatabase baseDatos;
    private EditText mensaje, celular;
    LocationManager locationManager;
    private ImageView botonRegresar;
    private Button botonGuardar,botonEnviar,botonUbicación;
    private static final String TAG = "Datos Emergencia";
    private double longitudeGPS, latitudeGPS;
    private String direccion;
    private ListView listaElementos;
    private AdaptadorContactos adaptador;
    private ArrayList<Contactos> listadeContactos =new ArrayList<Contactos>();
    private TextView cargando,nohayelementos;
    private Location posicionActual;


    @Override
    protected void onStart() {
        super.onStart();

        //revisamos que la autorizacion a los permisos necesesarios este activada
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    10);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                        10);
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS },
                            10);
                } else {

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //conexion con el xml
        setContentView(R.layout.activity_contacto_de_emergencia);
        mensaje = findViewById(R.id.mensajeGrande);
        celular = findViewById(R.id.celular);
        botonGuardar=findViewById(R.id.botonGuardarEmergencia);
        botonEnviar=findViewById(R.id.EnviarMensajeDeEmergencia);
        botonRegresar=findViewById(R.id.botonVolver);
        botonUbicación=findViewById(R.id.enviarUbicacion);
        listaElementos =findViewById(R.id.ListaTareas2);
        cargando=findViewById(R.id.cargando);
        nohayelementos=findViewById(R.id.nohayelementos);

        //Inizializamos la lista de contactos
        inizializarLista();
        inicializarDatosIniciales();

        //bloqueamos el campo de celular y deparecemos el letrero no hay ningun elemento
        celular.setEnabled(false);
        nohayelementos.setVisibility(View.GONE);

        // listeners de botones
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              guardarDatosPrestablecidos();

            }
        }
        );

        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verficarPermisos();
                Toast.makeText(ContactoDeEmergencia.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
            }
        });

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        botonUbicación.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iniciamos el location manager
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                //revisamos que efectivamente esten activos los permisos
                if (ActivityCompat.checkSelfPermission(ContactoDeEmergencia.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ContactoDeEmergencia.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }

                //traemos la localizacion
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);

                //Debido a que se demora un momento en traer la ubicacion ponemos un anuncio para que lo sepa el usuario
                if(latitudeGPS==0){
                    Toast.makeText(ContactoDeEmergencia.this, "Espere.. estamos buscado su ubicacion vuelva a oprimir el boton en 5 a 10 segundos", Toast.LENGTH_SHORT).show();
                }else{
                    //enviamos a la verificacion de permisos
                    verficarPermisos2();
                }

            }
        });

        // listener para ejecutar accion cada vez que se oprima sobre el contacto
        listaElementos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //obtenmos los datos de ese objeto
                Contactos temporal = (Contactos) adaptador.getItem(position);

                //separamos los datos que nos interesan en variables aparte
                String numero=temporal.getNumero();

                //colocamos la variente dentro de edit text
                celular.setText(numero);
            }
        });
    }

    //metodo para extraer la informacion que nos intereza del gps
    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {

            //extraemos la posicion actual en latitud y longitud
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            //seteamos el lugar el el location
            posicionActual=location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //metodo para enviar un mensaje
        public void enviarMensaje() {
            //extraemos los datos necesarios
            String numero = celular.getText().toString();
            String mensajeEnviar = mensaje.getText().toString();

            //iniciamos el manager de sms
            SmsManager sms = SmsManager.getDefault();

            //le adjuntamos los datos y lo enviamos
            sms.sendTextMessage(numero, null, mensajeEnviar, null, null);
        }


    // Verificacion de permismo para enviar sms en el caso de enviar el mensaje de emergencia
        public void verficarPermisos() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS }, 10);
            } else {
                enviarMensaje();
            }
        }

    // Verificacion de permismo para enviar sms en el caso de enviar la ubicacion
    public void verficarPermisos2() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS }, 10);
        } else {
            enviarMensaje2();
        }
    }

    //Metodo para enviar la ubicacion actual por un sms
    public void enviarMensaje2() {

        //extremos los datos
        String numero = celular.getText().toString();


        //iniciamos el sms manager
        SmsManager sms = SmsManager.getDefault();

        //usamos nuestro metodo set location
        setLocation(posicionActual);
        String mensajeUbicacion=direccion.trim();

        //adjuntamos los daatos en el mensaje
        sms.sendTextMessage(numero, null, mensajeUbicacion, null, null);

        //hacemos un toask informadole al usuario la información
        Toast.makeText(ContactoDeEmergencia.this, "Mensaje enviado" +direccion, Toast.LENGTH_SHORT).show();
    }

    //metodo para saber si se acepto o denego el permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }else{
                Toast.makeText(ContactoDeEmergencia.this,"Se necesita que actives los permisos para que la apliacación pueda funcionar ",Toast.LENGTH_SHORT).show();
                //User denied Permission.
            }
        }
    }


    //metodo para saber la ubicacion en direccion
    public void setLocation(Location location) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            try {

                //iniciamos el geocoder
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                //ingresamos los datos en una lista
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!list.isEmpty()) {

                    //obtenemos el mas fiable
                    Address DirCalle = list.get(0);

                    //lo traducimos a un string
                    direccion="Mi direccion es: \n" + DirCalle.getAddressLine(0);
                }

            } catch (IOException e) {
                // en caso de error se mostrara este mensaje
                e.printStackTrace();
            }
        }
    }

    //metodo para traer la infromacion del la lista de contactos
    public void inizializarLista(){

        //revisamos de cual usuario debemos traer la información
        FirebaseUser usuarioActual= FirebaseAuth.getInstance().getCurrentUser();
        String id = usuarioActual.getUid();


        //Conexion con la base de datos
        baseDatos = FirebaseDatabase.getInstance();
        DatabaseReference temporal = baseDatos.getReference("Usuarios/"+ id);


        //Traemos todos los contactos del usuario
        Query myTopPostsQuery = temporal.child("Contactos").orderByChild("starCount");
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {

            //traermos una copia de los datos de la base de datos
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listadeContactos.clear();
                //transcribimos esa copia de los contactos a un objeto y lo agregamos a un array list
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Contactos temporal=postSnapshot.getValue(Contactos.class);
                    String correo=temporal.getCorreo();
                    String nombre=temporal.getNombre();
                    String numero=temporal.getNumero();
                    Contactos usuario=new Contactos(nombre,correo,numero);
                    listadeContactos.add(usuario);
                }

                //letrero de cargando
                cargando.setVisibility(View.GONE);

                //aviso en caso de que no haya elementos que mostrar
                if(listadeContactos.size()==0 || listadeContactos ==null){
                    nohayelementos.setVisibility(View.VISIBLE);
                }else{
                    nohayelementos.setVisibility(View.GONE);
                }

                //usamos el adaptador para mostrar la lista
                adaptador=new AdaptadorContactos(ContactoDeEmergencia.this, listadeContactos);
                listaElementos.setAdapter(adaptador);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // en caso de algun error con la conexion con la base de datos saldra este error
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });


    }

    //metodo para traer los datos inciales
    public void inicializarDatosIniciales(){

        //revisamos de cual usuario debemos traer la información
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        String id = usuarioActual.getUid();


        //Conexion con la base de datos
        baseDatos = FirebaseDatabase.getInstance();
        DatabaseReference myRef = baseDatos.getReference("Usuarios/" + id + "/Emergencia");
        try {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                //verificamos con el mensaje no este repetido
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //conectamos con la base de datos
                        MensajeEmergencia temporal=dataSnapshot.getValue(MensajeEmergencia.class);
                        //creamos el objeto en el que se enviaran los datos
                        String mensajeTemporal=temporal.getMensaje();
                        String celularTemporal=temporal.getNumero();
                        //enviamos los datos
                        mensaje.setText(mensajeTemporal);
                        celular.setText(celularTemporal);


                    } else {
                        Toast.makeText(ContactoDeEmergencia.this, "Aun no se a asignado un mensaje de emergencia", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // esto se ejecutara si hay algun problema con el servidor
                    Log.w(TAG, "Fallo al conectar con el servidor", error.toException());
                }
            });
        } catch (Exception e) {
            // esto se ejecutara en el resto de casos donde no se logre completar el registro del mensaje
            Toast.makeText(ContactoDeEmergencia.this, "No se pudo acceder al mensaje",
                    Toast.LENGTH_SHORT).show();
        }

    }

    //este metodo es para guardar los datos nuevos del usario
    public void guardarDatosPrestablecidos(){
        //iniciamos la classe que tiene los metodos
        ManejoDeMensajesEmergencia actualizar= new ManejoDeMensajesEmergencia(ContactoDeEmergencia.this);

        //utilizamos el metodo de actualizar datos
        actualizar.registroMensajes(mensaje.getText().toString(),celular.getText().toString());

        //se lo informamos al usuario
        Toast.makeText(ContactoDeEmergencia.this, "Datos Guardados", Toast.LENGTH_SHORT).show();
    }
}