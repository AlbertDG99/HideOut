package com.example.hideout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

                Intent i = new Intent(this, PerfilUsuario.class);
                startActivity(i);

                break;

        }

    }

    public void comprobarLogin(){
        if(user != null){
            tNombre.setText(user.getDisplayName());
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}