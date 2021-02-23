package com.example.hideout;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Ranking extends AppCompatActivity implements View.OnClickListener {
    //intance de la base de datos
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://hideout-d08d6-default-rtdb.firebaseio.com/");
    //base de datos y colección
    private DatabaseReference myRef = database.getReference("Usuarios");
    private ListView userList; //listview con los usuarios
    ArrayList<Usuario> userArrayList; //arraylist con los usuarios

    RankingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        //el juego está pensado para pantalla vertical, así que forzamos dicha posicion
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        userList = findViewById(R.id.userList);
        userArrayList = new ArrayList<Usuario>();
        adapter = new RankingAdapter(this, userArrayList);
        findViewById(R.id.imageBack).setOnClickListener(this);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //obtenemos los datos y los introducimos en un objeto "Usuario"
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    if (usuario != null) {

                        userArrayList.add(usuario);

                    }
                }
                userArrayList = rankingUsuarios(rankingUsuarios(userArrayList));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        userList.setAdapter(adapter);
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



    private ArrayList<Usuario> rankingUsuarios(ArrayList<Usuario> userArrayList) {
        Boolean error = true;
        while (error) {
            error = false;
            float mAnterior = 0;
            for (int i = 0; i < userArrayList.size(); i++) {
                Usuario u = userArrayList.get(i);

                if (u.getMonedas()< mAnterior && i != 0) {
                    Usuario aux = userArrayList.get(i - 1);
                    userArrayList.set(i - 1, u);
                    userArrayList.set(i, aux);
                    error = true;
                    mAnterior = u.getMonedas();
                }


            }
        }
        return userArrayList;
    }


}


