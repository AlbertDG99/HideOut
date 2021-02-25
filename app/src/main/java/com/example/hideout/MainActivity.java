package com.example.hideout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 7679;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    List<AuthUI.IdpConfig> providers;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //el juego está pensado para pantalla vertical, así que forzamos dicha posicion
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //opciones de login
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        mAuth = FirebaseAuth.getInstance();
        //obtenemos el usuario actual
        user = mAuth.getCurrentUser();

        //si ya esta logueado te redirige a la siguiente activity
        comprobarLogin();

        //listeners
        findViewById(R.id.buttonIniciar).setOnClickListener(this);
        findViewById(R.id.buttonRegistro).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //cada vez que entramos en la activity se comprueba si el usuario esta logueado
        user = mAuth.getCurrentUser();
        comprobarLogin();
    }

    /**
     * Método que muestra las opciones de login
     */
    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.hideoutblue)
                        .setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //respuesta del login
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // login con exito
                user = FirebaseAuth.getInstance().getCurrentUser();
                comprobarLogin();
            } else {
                // login erroneo
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //botón iniciar sesion
            case R.id.buttonIniciar:
                //muestra opciones de login
                showSignInOptions();
                break;

            //botón registro
            case R.id.buttonRegistro:
                Intent i = new Intent(this, Registro.class);
                startActivity(i);
                break;
        }
    }

    /**
     * Método que comprueba si el usuario ha logueado
     */
    public void comprobarLogin() {
        //si el usuario actual existe te lleva a la pantalla principal del juego
        if (user != null) {
            Intent i = new Intent(this, MenuPrincipal.class);
            startActivity(i);
            finish();
        }
    }
}