package com.example.hideout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

public class MenuPrincipal extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private TextView tNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        tNombre = findViewById(R.id.tNombre);
        mAuth = FirebaseAuth.getInstance();

        comprobarLogin();

        findViewById(R.id.bCrear).setOnClickListener(this);
        findViewById(R.id.imgSettings).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        comprobarLogin();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bCrear:
                startActivity(new Intent(MenuPrincipal.this, CrearReto.class));
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


}


