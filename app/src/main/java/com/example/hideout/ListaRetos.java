package com.example.hideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_retos);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        retosList = findViewById(R.id.retosList);

        retosArrayList = new ArrayList<Reto>();

        //final ArrayAdapter<Reto> adapter = new ArrayAdapter<Reto>(this, android.R.layout.simple_dropdown_item_1line, retosArrayList);

        final RetosAdapter adapter = new RetosAdapter(this, retosArrayList);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //obtenemos los datos y los introducimos en un objeto "Usuario"
                    Reto reto = snapshot.getValue(Reto.class);

                    retosArrayList.add(reto);
                    adapter.notifyDataSetChanged();
                }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //botón atrás
            case R.id.imageBack:

                finish();

                break;

        }
    }
}