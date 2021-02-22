package com.example.hideout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MenuPrincipal extends AppCompatActivity implements View.OnClickListener {

    //intance de la base de datos
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://hideout-d08d6-default-rtdb.firebaseio.com/");
    //base de datos y colección
    private DatabaseReference myRef = database.getReference("Usuarios");

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private TextView tNombre;
    private TextView tNivel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);

        tNombre = findViewById(R.id.tNombre);
        tNivel = findViewById(R.id.tNivel);
        mAuth = FirebaseAuth.getInstance();

        comprobarLogin();
        comprobarMonedas();

        findViewById(R.id.bCrear).setOnClickListener(this);
        findViewById(R.id.bJugar).setOnClickListener(this);
        findViewById(R.id.imgSettings).setOnClickListener(this);
        findViewById(R.id.bJugar).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        comprobarLogin();
        comprobarMonedas();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bCrear:
                startActivity(new Intent(MenuPrincipal.this, CrearReto.class));
                break;

            case R.id.bJugar:
                startActivity(new Intent(MenuPrincipal.this, ListaRetos.class));
                break;

            case R.id.imgSettings:
                PopupMenu settings = new PopupMenu(this, v);
                settings.getMenuInflater().inflate(R.menu.settings_popup, settings.getMenu());
                settings.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.itemPerfil:
                                startActivity(new Intent(MenuPrincipal.this, PerfilUsuario.class));
                                break;
                            //botón cerrar sesion
                            case R.id.itemCSesion:
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
                                finish();
                                break;
                        }
                        return false;
                    }
                });
                settings.show();
                break;
        }

    }

    public void comprobarLogin() {

        user = mAuth.getCurrentUser();
        if (user != null) {
            tNombre.setText(user.getDisplayName());
        } else {
            finish();
        }
    }

    private void comprobarMonedas() {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            Boolean existeUsu = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //obtenemos los datos y los introducimos en un objeto "Usuario"
                        Usuario usuarioInfo = snapshot.getValue(Usuario.class);

                        if (usuarioInfo != null) {
                            //traemos la info y la mostramos si coincide el ID
                            if (usuarioInfo.getIdUsu().equals(user.getUid())) {
                                existeUsu = true;
                                //mostramos el numero de monedas
                                tNivel.setText(Integer.toString(usuarioInfo.getMonedas()));
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


