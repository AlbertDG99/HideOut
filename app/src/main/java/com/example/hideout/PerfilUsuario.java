package com.example.hideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class PerfilUsuario extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText textUserName;
    private EditText textUserMail;
    private Button buttonEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        //instanciamos los campos del layout
        textUserName = findViewById(R.id.textUserName);
        textUserMail = findViewById(R.id.textUserMail);
        buttonEditar = findViewById(R.id.buttonEditar);

        //Desactivamos los campos
        textUserName.setEnabled(false);
        textUserMail.setEnabled(false);

        //obtenemos el instance de firebase
        mAuth = FirebaseAuth.getInstance();
        //obtenemos el usuario
        user = mAuth.getCurrentUser();
        //si el usuario existe cargamos sus datos
        if(user != null){
            textUserName.setText(user.getDisplayName());
            textUserMail.setText(user.getEmail());
        }else{ //sino, lo reenviamos al inicio
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        buttonEditar.setOnClickListener(this);
        findViewById(R.id.buttonChange).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //botón editar nombre
            case R.id.buttonEditar:

                if(textUserName.isEnabled() && textUserName.getText() != null && textUserMail.isEnabled() && textUserMail.getText() != null){

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(String.valueOf(textUserName.getText()))
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast toast =
                                                Toast.makeText(getApplicationContext(),
                                                        "Nombre actualizado con éxito", Toast.LENGTH_SHORT);

                                        toast.show();
                                    }
                                }
                            });

                    user.updateEmail(String.valueOf(textUserMail.getText()))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast toast =
                                                Toast.makeText(getApplicationContext(),
                                                        "Email actualizado con éxito", Toast.LENGTH_SHORT);

                                        toast.show();
                                    }
                                }
                            });


                    textUserName.setEnabled(false);
                    textUserMail.setEnabled(false);
                    buttonEditar.setText("EDITAR");

                }else{
                    textUserName.setEnabled(true);
                    textUserMail.setEnabled(true);
                    buttonEditar.setText("GUARDAR CAMBIOS");
                }

                break;

            //botón editar password
            case R.id.buttonChange:



                break;

        }

    }
}