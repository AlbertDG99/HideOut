package com.example.hideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Juego extends AppCompatActivity implements View.OnClickListener {

    //intance de la base de datos
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://hideout-d08d6-default-rtdb.firebaseio.com/");
    //base de datos y colección
    private DatabaseReference myRef = database.getReference("Usuarios");

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    Handler handler = new Handler();
    Bitmap imagen;
    String pista = "";
    double latitudReto;
    double longitudReto;
    double longitudUsu;
    double latitudUsu;
    ImageView imgRadar;
    TextView tPista;
    Button bFinReto;
    int monedas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);

        Bundle extras = getIntent().getExtras();
        Reto reto = (Reto) extras.getSerializable("Reto");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        latitudReto = reto.getLatitud();
        longitudReto = reto.getLongitud();
        tPista = findViewById(R.id.tPista);
        tPista.setText(reto.getPista());
        imgRadar = findViewById(R.id.imgRadar);
        bFinReto = findViewById(R.id.bFinReto);

        monedas = 50;

        bFinReto.setEnabled(false);

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

        findViewById(R.id.imageBack).setOnClickListener(this);
        findViewById(R.id.bFinReto).setOnClickListener(this);
    }

    public void obtenerLoc() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LocationTrack location = new LocationTrack(Juego.this);
                latitudUsu = location.getLatitude();
                longitudUsu = location.getLongitude();

                bFinReto.setEnabled(false);
                bFinReto.setAlpha((float) 0.2);

                Location locReto = new Location("LocReto");
                locReto.setLatitude(latitudReto);
                locReto.setLongitude(longitudReto);

                Location locUsu = new Location("LocUsu");
                locUsu.setLongitude(longitudUsu);
                locUsu.setLatitude(latitudUsu);

                float distancia = locReto.distanceTo(locUsu);
                if(distancia>200){
                    imgRadar.setImageResource(R.drawable.radar0);
                }else if(distancia>100){
                    imgRadar.setImageResource(R.drawable.radar1);
                }else if(distancia>50){
                    imgRadar.setImageResource(R.drawable.radar2);
                }else if(distancia>10){
                    imgRadar.setImageResource(R.drawable.radar3);
                }
                else if(distancia<=10){
                    imgRadar.setImageResource(R.drawable.radar3);
                    bFinReto.setEnabled(true);
                    bFinReto.setAlpha(1);
                }
                handler.postDelayed(this, 1500);
            }
        }, 1500);

    }

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

    private void sumarMonedas(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //obtenemos los datos y los introducimos en un objeto "Usuario"
                    Usuario usuarioInfo = snapshot.getValue(Usuario.class);

                    if(usuarioInfo != null){
                        //traemos la info y la mostramos si coincide el ID
                        if(usuarioInfo.getIdUsu().equals(user.getUid())){
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