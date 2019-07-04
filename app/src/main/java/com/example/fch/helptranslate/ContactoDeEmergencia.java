package com.example.fch.helptranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactoDeEmergencia extends AppCompatActivity {
    private FirebaseDatabase baseDatos;
    private EditText mensaje, celular;
    LocationManager locationManager;
    private ImageView botonRegresar;
    private Button botonGuardar,botonEnviar,botonUbicación;
    private static final String TAG = "Datos Emergencia";
    private double longitudeGPS, latitudeGPS;


    @Override
    protected void onStart() {
        super.onStart();
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
        setContentView(R.layout.activity_contacto_de_emergencia);
        mensaje = findViewById(R.id.mensajeGrande);
        celular = findViewById(R.id.celular);
        botonGuardar=findViewById(R.id.botonGuardarEmergencia);
        botonEnviar=findViewById(R.id.EnviarMensajeDeEmergencia);
        botonRegresar=findViewById(R.id.botonVolver);
        botonUbicación=findViewById(R.id.enviarUbicacion);





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
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManejoDeMensajesEmergencia actualizar= new ManejoDeMensajesEmergencia(ContactoDeEmergencia.this);
                actualizar.registroMensajes(mensaje.getText().toString(),celular.getText().toString());
                Toast.makeText(ContactoDeEmergencia.this, "Datos Guardados", Toast.LENGTH_SHORT).show();

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
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(ContactoDeEmergencia.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ContactoDeEmergencia.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);
                if(latitudeGPS==0){

                    Toast.makeText(ContactoDeEmergencia.this, "Espere.. estamos buscado su ubicacion vuelva a oprimir el boton en 5 a 10 segundos", Toast.LENGTH_SHORT).show();
                }else{

                    verficarPermisos2();
                }

            }
        });

    }



    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
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

        public void enviarMensaje() {
            String numero = celular.getText().toString();
            String mensajeEnviar = mensaje.getText().toString();
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero, null, mensajeEnviar, null, null);
        }

    public void verficarPermisos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS },
                    10);
        } else {
            enviarMensaje();
        }
    }

    public void verficarPermisos2() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS },
                    10);
        } else {
            enviarMensaje2();
        }
    }
    public void enviarMensaje2() {
        String numero = celular.getText().toString();
        SmsManager sms = SmsManager.getDefault();
        String mensajeUbicacion="http://maps.google.com/?q=<"+latitudeGPS+">,<"+longitudeGPS+">";
        sms.sendTextMessage(numero, null, mensajeUbicacion , null, null);
        Toast.makeText(ContactoDeEmergencia.this, "Mensaje enviado"+latitudeGPS+" "+longitudeGPS, Toast.LENGTH_SHORT).show();
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }else{
                //User denied Permission.
            }
        }
    }
}