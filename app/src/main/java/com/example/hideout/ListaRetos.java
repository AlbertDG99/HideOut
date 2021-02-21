package com.example.hideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

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
    private Location locUsu= new Location("LocUsu");
    RetosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_retos);

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
               retosArrayList= organizarLista(retosArrayList);
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
            error=false;
            float dAnterior = 0;
            for (int i = 0; i < retosArrayList.size(); i++) {
                Reto r = retosArrayList.get(i);

                Location actual = new Location("Actual");
                actual.setLatitude(r.getLatitud());
                actual.setLongitude(r.getLongitud());

                float dActual = actual.distanceTo(locUsu);
                if (dActual < dAnterior && i != 0) {
                    Reto aux = retosArrayList.get(i - 1);
                    retosArrayList.set(i - 1, r);
                    retosArrayList.set(i, aux);
                    error = true;
                }
                dAnterior = dActual;

            }
        }
        return  retosArrayList;
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
    /**
     * Release memory when the UI becomes hidden or when system resources become low.
     * @param level the memory-related event that was raised.
     */
    public void onTrimMemory(int level) {

        // Determine which lifecycle or system event was raised.
        switch (level) {

            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:

                    /*
                       Release any UI objects that currently hold memory.

                       The user interface has moved to the background.
                    */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:

                    /*
                       Release any memory that your app doesn't need to run.

                       The device is running low on memory while the app is running.
                       The event raised indicates the severity of the memory-related event.
                       If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                       begin killing background processes.
                    */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:

                    /*
                       Release as much memory as the process can.

                       The app is on the LRU list and the system is running low on memory.
                       The event raised indicates where the app sits within the LRU list.
                       If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                       the first to be terminated.
                    */

                break;

            default:
                    /*
                      Release any non-critical data structures.

                      The app received an unrecognized memory level value
                      from the system. Treat this as a generic low-memory message.
                    */
                break;
        }
    }
}