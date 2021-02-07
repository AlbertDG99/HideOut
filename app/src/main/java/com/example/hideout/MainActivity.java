package com.example.hideout;

import android.content.Intent;
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

        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        comprobarLogin();

        findViewById(R.id.buttonIniciar).setOnClickListener(this);
        findViewById(R.id.buttonRegistro).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = mAuth.getCurrentUser();
        comprobarLogin();
    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.hideoutalpha)
                        .setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();

                comprobarLogin();

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //botón iniciar sesion
            case R.id.buttonIniciar:

                showSignInOptions();

                break;

            //botón registro
            case R.id.buttonRegistro:

                Intent i = new Intent(this, Registro.class);
                startActivity(i);

                break;

        }

    }

    public void comprobarLogin() {
        if (user != null) {
            Intent i = new Intent(this, MenuPrincipal.class);
            startActivity(i);
        }
    }
}