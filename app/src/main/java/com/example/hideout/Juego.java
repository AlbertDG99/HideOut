package com.example.hideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Juego extends AppCompatActivity {
    Handler handler = new Handler();
    Bitmap imagen;
    String pista = "";
    double latitudReto;
    double longitudReto;
    double longitudUsu;
    double latitudUsu;
    ImageView imgRadar;
    TextView tPista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);


        Bundle extras = getIntent().getExtras();
        Reto reto = (Reto) extras.getSerializable("Reto");

        latitudReto = reto.getLatitud();
        longitudReto = reto.getLongitud();
        tPista = findViewById(R.id.tPista);
        tPista.setText(reto.getPista());
        imgRadar = findViewById(R.id.imgRadar);

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://hideout-d08d6.appspot.com");
        StorageReference gsReference = storage.getReferenceFromUrl(reto.getImagen());

        final long ONE_MEGABYTE = 1024 * 1024;
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




        obtenerLoc();

    }

    public void obtenerLoc() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LocationTrack location = new LocationTrack(Juego.this);
                latitudUsu = location.getLatitude();
                longitudUsu = location.getLongitude();

                Location locReto = new Location("LocReto");
                locReto.setLatitude(latitudReto);
                locReto.setLongitude(longitudReto);

                Location locUsu = new Location("LocUsu");
                locUsu.setLongitude(longitudUsu);
                locUsu.setLatitude(latitudUsu);

                float distancia = locReto.distanceTo(locUsu);
                if(distancia>200){

                }else if(distancia>100){

                }else if(distancia>50){

                }else if(distancia>10){

                }
                else if(distancia<=5){

                }
                handler.postDelayed(this, 1500);
            }
        }, 1500);

    }


}