package com.example.hideout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Opening extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        //logo principal
        ImageView logo = findViewById(R.id.mainLogo);
        //animacion logo principal
        Animation animacionLogo = AnimationUtils.loadAnimation(this, R.anim.animtitle);
        logo.startAnimation(animacionLogo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //abrimos el activity principal
                Intent i = new Intent(Opening.this,MainActivity.class);
                startActivity(i);
                //acabamos con el activity actual para que no se pueda volver a él
                finish();

            }
        }, 3000); //delay de 5 segundos

    }
}