package com.example.hideout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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

        //permisos de localizacion
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

        //el juego está pensado para pantalla vertical, así que forzamos dicha posicion
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //instanciamos los campos del layout
        tNombre = findViewById(R.id.tNombre);
        tNivel = findViewById(R.id.tNivel);
        mAuth = FirebaseAuth.getInstance();

        //metodo que comprueba si el usuario esta logueado
        comprobarLogin();
        //metodo que actualiza el valor de las monedas
        comprobarMonedas();

        //listeners
        findViewById(R.id.bCrear).setOnClickListener(this);
        findViewById(R.id.bJugar).setOnClickListener(this);
        findViewById(R.id.imgSettings).setOnClickListener(this);
        findViewById(R.id.bRanking).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        comprobarLogin(); //comprobamos el login
        comprobarMonedas(); //actualizamos las monedas
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //boton de crear reto
            case R.id.bCrear:
                startActivity(new Intent(MenuPrincipal.this, CrearReto.class));
                break;
            //boton de jugar
            case R.id.bJugar:
                startActivity(new Intent(MenuPrincipal.this, ListaRetos.class));
                break;
            //boton de ranking
            case R.id.bRanking:
                startActivity(new Intent(MenuPrincipal.this, Ranking.class));
                break;
            //menu desplegable
            case R.id.imgSettings:
                PopupMenu settings = new PopupMenu(this, v);
                settings.getMenuInflater().inflate(R.menu.settings_popup, settings.getMenu());
                settings.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //boton perfil
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

    /**
     * Método de comprobacion del login del usuario
     */
    public void comprobarLogin() {

        //se obtiene el usuario actual
        user = mAuth.getCurrentUser();
        //si no es nulo
        if (user != null) {
            //se muestra el nombre
            tNombre.setText(user.getDisplayName());
        } else { //sino se finaliza la activity y redirige al login
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
            finish();
        }
    }

    /**
     * Método que actualiza las monedas del usuario o crea el registro si no existe
     */
    private void comprobarMonedas() {

        //listener
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            Boolean existeUsu = false; //booleana de control

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //si se obtienen datos
                if (dataSnapshot.getValue() != null){
                    //bucle para recorrer los hijos de lo obtenido
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //obtenemos los datos y los introducimos en un objeto "Usuario"
                        Usuario usuarioInfo = snapshot.getValue(Usuario.class);

                        //si el usuario no es nulo
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

                //si no existe el usuario
                if(!existeUsu){
                    //se crea un registro
                    Usuario newUser = new Usuario();
                    newUser.setNombre(user.getDisplayName());
                    newUser.setIdUsu(user.getUid());
                    newUser.setMonedas(0);

                    myRef.child(user.getUid()).setValue(newUser);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }
}


