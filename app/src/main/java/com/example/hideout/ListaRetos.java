package com.example.hideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
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

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);

        LocationTrack location = new LocationTrack(this);
        latitudUsu = location.getLatitude();
        longitudUsu = location.getLongitude();

        locUsu.setLongitude(longitudUsu);
        locUsu.setLatitude(latitudUsu);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        retosList = findViewById(R.id.retosList);

        retosArrayList = new ArrayList<Reto>();

        //final ArrayAdapter<Reto> adapter = new ArrayAdapter<Reto>(this, android.R.layout.simple_dropdown_item_1line, retosArrayList);

        adapter = new RetosAdapter(this, retosArrayList);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //obtenemos los datos y los introducimos en un objeto "Usuario"
                    Reto reto = snapshot.getValue(Reto.class);

                    if (reto != null) {

                        retosArrayList.add(reto);

                    }
                }
                retosArrayList = organizarLista(retosArrayList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        retosList.setAdapter(adapter);

        findViewById(R.id.imageBack).setOnClickListener(this);

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

    private ArrayList<Reto> organizarLista(ArrayList<Reto> retosArrayList) {
        Boolean error = true;
        while (error) {
            error = false;
            float dAnterior = 0;
            for (int i = 0; i < retosArrayList.size(); i++) {
                Reto r = retosArrayList.get(i);

                Location actual = new Location("Actual");
                actual.setLatitude(r.getLatitud());
                actual.setLongitude(r.getLongitud());

                float dActual = actual.distanceTo(locUsu);
                if (dActual > 400) {
                    retosArrayList.remove(i);
                    i--;
                    error=true;
                } else if (dActual < dAnterior && i != 0) {
                    Reto aux = retosArrayList.get(i - 1);
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