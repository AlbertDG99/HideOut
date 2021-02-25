package com.example.hideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ListaRetos extends AppCompatActivity implements View.OnClickListener {

    //intance de la base de datos
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://hideout-d08d6-default-rtdb.firebaseio.com/");
    //base de datos y colección
    private DatabaseReference myRef = database.getReference("Retos");

    private DatabaseReference mDatabase;

    private ListView retosList; //listview con los retos
    ArrayList<Reto> retosArrayList; //arraylist con los retos

    private double latitudUsu;
    private double longitudUsu;
    private Location locUsu = new Location("LocUsu");
    RetosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_retos);

        //geoposicionamiento
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

        //el juego está pensado para pantalla vertical, así que forzamos dicha posicion
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //obtenemos la posicion del usuario
        LocationTrack location = new LocationTrack(this);
        latitudUsu = location.getLatitude();
        longitudUsu = location.getLongitude();
        locUsu.setLongitude(longitudUsu);
        locUsu.setLatitude(latitudUsu);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        retosList = findViewById(R.id.retosList);

        retosArrayList = new ArrayList<Reto>();

        adapter = new RetosAdapter(this, retosArrayList);

        //recibimos la informacion de la db
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //bucle para recoorer los hijos de lo cibido
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //obtenemos los datos y los introducimos en un objeto "Usuario"
                    Reto reto = snapshot.getValue(Reto.class);

                    //si reto no es nulo
                    if (reto != null) {
                        //lo añadimos al arraylist
                        retosArrayList.add(reto);
                    }
                }
                //organizamos la lista de retos
                retosArrayList = organizarLista(retosArrayList);
                Collections.reverse(retosArrayList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        //aplicamos el adapter
        retosList.setAdapter(adapter);

        //listener
        findViewById(R.id.imageBack).setOnClickListener(this);

        //listener del listview
        retosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //meter en intent -> retosArrayList.get(position); (Reto)
                Intent intent = new Intent(ListaRetos.this, Juego.class);
                intent.putExtra("Reto", (Serializable) retosArrayList.get(position));
                startActivity(intent);
            }
        });
    }

    /**
     * Método que organiza el arraylist de retos
     *
     * @param retosArrayList
     * @return devuelve el arraylist organizado
     */
    private ArrayList<Reto> organizarLista(ArrayList<Reto> retosArrayList) {
        //booleana de control
        Boolean error = true;
        while (error) {
            error = false;
            float dAnterior = 0;
            //bucle para recorrer el arraylist
            for (int i = 0; i < retosArrayList.size(); i++) {
                //cogemos el reto de la posicion i
                Reto r = retosArrayList.get(i);

                //posicion del reto
                Location actual = new Location("Actual");
                actual.setLatitude(r.getLatitud());
                actual.setLongitude(r.getLongitud());

                //distancia al usuario
                float dActual = actual.distanceTo(locUsu);
                //si es mayor a 400m se elimina de la lista
                if (dActual > 400) {
                    retosArrayList.remove(i);
                    i--;
                } else if (dActual < dAnterior && i != 0) {
                    //sino se ordena
                    //reto de la posicion actual
                    Reto aux = retosArrayList.get(i - 1);
                    //intercambiamos la posicon de los retos
                    retosArrayList.set(i - 1, r);
                    retosArrayList.set(i, aux);
                    error = true;
                    dAnterior=dActual;
                }
            }
        }
        return retosArrayList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //botón atrás
            case R.id.imageBack:
                adapter.clearMemory();
                finish();
                break;

        }
    }
}