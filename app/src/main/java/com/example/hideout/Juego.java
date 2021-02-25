package com.example.hideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Clase juego en la que intentaremos encontrar la posicion del reto para ganar
 */
public class Juego extends AppCompatActivity implements View.OnClickListener {

    //intance de la base de datos
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://hideout-d08d6-default-rtdb.firebaseio.com/");
    //base de datos y colección
    private DatabaseReference myRef = database.getReference("Usuarios");

    //Usuario actual en uso
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    Handler handler = new Handler(); //Objeto que ejecuta asyncronamente elementos especificos
     //Posicion del reto y del Usuario
    double latitudReto;
    double longitudReto;

    double longitudUsu;
    double latitudUsu;

    //Elementos del layout
    ImageView imgRadar;
    TextView tPista;
    Button bFinReto;
    //Numero de monedas que da el reto al completarse
    int monedas;

    /**
     * Metodo que se ejecuta en el lanzamiento del activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        //Pide permiso para obtener tu localización si no los tiene ya
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

        //el juego está pensado para pantalla vertical, así que forzamos dicha posicion
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Obtiene del bundle el objeto reto que enviamos de la activity anterior
        Bundle extras = getIntent().getExtras();
        Reto reto = (Reto) extras.getSerializable("Reto");

        //Obtiene el usuario actual de la base de datos
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Guarda todos los elementos del reto en las variables de la clase
        latitudReto = reto.getLatitud();
        longitudReto = reto.getLongitud();

        //Iguala las variables a los elementos del layout.
        tPista = findViewById(R.id.tPista);
        tPista.setText(reto.getPista());
        imgRadar = findViewById(R.id.imgRadar);
        bFinReto = findViewById(R.id.bFinReto);

        //Settea el numero de monedas
        rellenarMonedas();

        //Settea el botón de encontrado a false
        bFinReto.setEnabled(false);

        //Obtiene la instancia de la base de datos
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://hideout-d08d6.appspot.com");

        //Obtiene la referencia del storage de firebase
        StorageReference gsReference = storage.getReferenceFromUrl(reto.getImagen());
        final long ONE_MEGABYTE = 1024 * 1024;
        //Rellena el imageview con la imagen obtenida de la url de firestorage
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ImageView imagenReto = findViewById(R.id.imgReto);
                imagenReto.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        //Obtiene la localización del usuario de manera asyncrona cada segundo y medio
        obtenerLoc();

        //Listener para los botones del layout
        findViewById(R.id.imageBack).setOnClickListener(this);
        findViewById(R.id.bFinReto).setOnClickListener(this);
    }

    /**
     * Metodo que genera el numero de monedas respecto a tu posición con el reto
     */
    public void rellenarMonedas() {

        //Localización en este instante del usuario
        LocationTrack location = new LocationTrack(Juego.this);
        latitudUsu = location.getLatitude();
        longitudUsu = location.getLongitude();

        //Localización del reto
        Location locReto = new Location("LocReto");
        locReto.setLatitude(latitudReto);
        locReto.setLongitude(longitudReto);

        //Creamos un objeto localización con los datos del user
        Location locUsu = new Location("LocUsu");
        locUsu.setLongitude(longitudUsu);
        locUsu.setLatitude(latitudUsu);

        //Calculamos la distancia de USU a RETO
        float distancia = locReto.distanceTo(locUsu);

        //Lo redondeamos a int
        monedas = Math.round(distancia);
    }

    /**
     * Obtiene la localización cada 1.5 segundos de manera asyncrona
     */
    public void obtenerLoc() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Localización del usuario
                LocationTrack location = new LocationTrack(Juego.this);
                latitudUsu = location.getLatitude();
                longitudUsu = location.getLongitude();

                //Pone el botón de encontrado deshabilitad
                bFinReto.setEnabled(false);
                bFinReto.setAlpha((float) 0.2);

                //Crea el objeto localización con los datos del reto
                Location locReto = new Location("LocReto");
                locReto.setLatitude(latitudReto);
                locReto.setLongitude(longitudReto);

                //Crea el objeto localización con los datos del usuario
                Location locUsu = new Location("LocUsu");
                locUsu.setLongitude(longitudUsu);
                locUsu.setLatitude(latitudUsu);

                //Calculamos la distancia entre ambos puntos
                float distancia = locReto.distanceTo(locUsu);

                //Dependiendo de la distancia, la imagen del radar cambiará para indicar si te acercas o alejas
                if (distancia > 200) {
                    imgRadar.setImageResource(R.drawable.radar0);
                } else if (distancia > 100) {
                    imgRadar.setImageResource(R.drawable.radar1);
                } else if (distancia > 50) {
                    imgRadar.setImageResource(R.drawable.radar2);
                } else if (distancia > 25) {
                    imgRadar.setImageResource(R.drawable.radar3);
                } else if (distancia <= 25) {
                    //Una vez estés en el rango suficiente, podrás pulsar el botón de encontrado
                    imgRadar.setImageResource(R.drawable.radar3);
                    bFinReto.setEnabled(true);
                    bFinReto.setAlpha(1);
                }
                //Se reejecuta cada 1500 ms
                handler.postDelayed(this, 1500);
            }
        }, 1500);

    }

    /**
     * Metodo que se ejecuta cuando clickas un botón del layout
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //botón atrás
            case R.id.imageBack:

                finish();

                break;

            //boton encontrado
            case R.id.bFinReto:

                //metodo añadir monedas
                sumarMonedas();

                AlertDialog.Builder builder = new AlertDialog.Builder(Juego.this);
                //informamos al usuario con un dialog del exito
                builder.setTitle("¡ENHORABUENA!");
                builder.setMessage("Has encontrado el lugar oculto.\nHas ganado: " + monedas + " monedas.");
                builder.setCancelable(false);
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // redirigimos a la pantalla principal
                                finish();
                            }
                        });
                builder.setNegativeButton("",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // redirigimos a la pantalla principal
                                finish();
                            }
                        });
                AlertDialog infoDialog = builder.create();
                infoDialog.show();

                break;
        }
    }

    /**
     * Metodo que suma las monedas al usuario que ha ganado el reto
     */
    private void sumarMonedas() {
        //Obtenemos el listado de usuarios
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //Recorremos un bucle for en el que evaluamos cada usuario para encontrar el actual
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //obtenemos los datos y los introducimos en un objeto "Usuario"
                    Usuario usuarioInfo = snapshot.getValue(Usuario.class);

                    if (usuarioInfo != null) {
                        //traemos la info y la mostramos si coincide el ID
                        if (usuarioInfo.getIdUsu().equals(user.getUid())) {
                            //mostramos el numero de monedas
                            usuarioInfo.setMonedas(usuarioInfo.getMonedas() + monedas);
                            myRef.child(user.getUid()).setValue(usuarioInfo);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }
}