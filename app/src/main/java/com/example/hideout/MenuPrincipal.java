package com.example.hideout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        user = mAuth.getCurrentUser();

        comprobarLogin();

        findViewById(R.id.buttonSalir).setOnClickListener(this);
        findViewById(R.id.imgSettings).setOnClickListener(this);
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

            //botón cerrar sesion
            case R.id.buttonSalir:

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

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
                            case R.id.itemCSesion:
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
        if (user != null) {
            tNombre.setText(user.getDisplayName());
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}