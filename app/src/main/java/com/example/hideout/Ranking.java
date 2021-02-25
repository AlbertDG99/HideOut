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
import java.util.Collections;

public class Ranking extends AppCompatActivity implements View.OnClickListener {
    //intance de la base de datos
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://hideout-d08d6-default-rtdb.firebaseio.com/");
    //base de datos y colección
    private DatabaseReference myRef = database.getReference("Usuarios");
    private ListView userList; //listview con los usuarios
    ArrayList<Usuario> userArrayList; //arraylist con los usuarios

    //adaptador del listview
    RankingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        //el juego está pensado para pantalla vertical, así que forzamos dicha posicion
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //instanciamos los campos del layout
        userList = findViewById(R.id.userList);
        userArrayList = new ArrayList<Usuario>();
        adapter = new RankingAdapter(this, userArrayList);
        findViewById(R.id.imageBack).setOnClickListener(this);

        //obtenemos los datos de la database (una vez)
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //bucle foreach que recorrer la informacion del hijo de la obtenida
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //obtenemos los datos y los introducimos en un objeto "Usuario"
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    //si el usuario existe
                    if (usuario != null) {
                        //se añade al arrayList
                        userArrayList.add(usuario);
                    }
                }

                userArrayList = rankingUsuarios(rankingUsuarios(userArrayList));
                //si hay más de cuatro usuarios
                if(userArrayList.size()>4)
                Collections.reverse(userArrayList); //se ordena la lista
                userArrayList.subList(4, userArrayList.size()).clear(); //se dejan solo los 4 primeros
                adapter.notifyDataSetChanged(); //se notifica al adaptador
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        //se aplica el adaptador
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

    /**
     * Método para ordenar los usuarios del arraylist
     *
     * @param userArrayList recibe el arraylist de usuarios
     * @return devuelve el arraylist ordenado
     */
    private ArrayList<Usuario> rankingUsuarios(ArrayList<Usuario> userArrayList) {
        Boolean error = true; //booleana de control
        //mientras la variable de control sea true
        while (error) {
            error = false; //cambiamos la variable de control a false
            float mAnterior = 0;
            //bucle for para recorrer el arraylist
            for (int i = 0; i < userArrayList.size(); i++) {
                //llenamos el usuario de la posicion i
                Usuario u = userArrayList.get(i);

                //si el usuario de la posicion anterior tenia menos monedas
                if (u.getMonedas()< mAnterior && i != 0) {
                    Usuario aux = userArrayList.get(i - 1); //cogemos el usuario anterior
                    //intercambiamos posiciones de los usuarios en el arraylist
                    userArrayList.set(i - 1, u);
                    userArrayList.set(i, aux);
                    error = true; //cambia el valor de la variable de control
                }
                mAnterior = u.getMonedas(); //cogemos el numero de monedas del usuario
            }
        }
        return userArrayList;
    }
}


