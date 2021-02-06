package com.example.hideout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class PerfilUsuario extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText textUserName;
    private EditText textUserMail;
    private Button buttonEditar;
    private EditText textNewPass;
    private EditText textRepNewPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        //instanciamos los campos del layout
        textUserName = findViewById(R.id.textUserName);
        textUserMail = findViewById(R.id.textUserMail);
        buttonEditar = findViewById(R.id.buttonEditar);
        textNewPass = findViewById(R.id.textNewPass);
        textRepNewPass = findViewById(R.id.textRepNewPass);


        //Desactivamos los campos
        textUserName.setEnabled(false);
        textUserMail.setEnabled(false);

        //obtenemos el instance de firebase
        mAuth = FirebaseAuth.getInstance();
        //obtenemos el usuario
        user = mAuth.getCurrentUser();
        //si el usuario existe cargamos sus datos
        if (user != null) {
            textUserName.setText(user.getDisplayName());
            textUserMail.setText(user.getEmail());
        } else { //sino, lo reenviamos al inicio
            finish();
        }

        buttonEditar.setOnClickListener(this);
        findViewById(R.id.buttonChange).setOnClickListener(this);
        findViewById(R.id.imageBack).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //botón editar nombre
            case R.id.buttonEditar:

                if (textUserName.isEnabled() && textUserName.getText().length() != 0 && textUserMail.isEnabled() && textUserMail.getText().length() != 0) {

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(String.valueOf(textUserName.getText()))
                            .build();

                    if(!Objects.equals(user.getDisplayName(), textUserName.getText().toString())){
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
                    }

                    if(!Objects.equals(user.getEmail(), textUserMail.getText().toString())) {
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
                    }

                    textUserName.setEnabled(false);
                    textUserMail.setEnabled(false);
                    buttonEditar.setText("EDITAR PERFIL");

                } else {
                    textUserName.setEnabled(true);
                    textUserMail.setEnabled(true);
                    buttonEditar.setText("GUARDAR CAMBIOS");
                }

                break;

            //botón editar password
            case R.id.buttonChange:

                if(textNewPass.getText().length() == 0 || textRepNewPass.getText().length() == 0){
                    Toast toast =
                            Toast.makeText(getApplicationContext(),
                                    "Los campos no pueden estar vacios", Toast.LENGTH_SHORT);

                    toast.show();
                }else{
                    if(!textNewPass.getText().toString().equals(textRepNewPass.getText().toString())){
                        Toast toast =
                                Toast.makeText(getApplicationContext(),
                                        "Las contraseñas no coinciden", Toast.LENGTH_SHORT);

                        toast.show();
                    }else{
                        String newPassword = textNewPass.getText().toString();

                        user.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast toast =
                                                    Toast.makeText(getApplicationContext(),
                                                            "Contraseña Actualizada", Toast.LENGTH_SHORT);

                                            toast.show();
                                        }
                                    }
                                });

                        textNewPass.setText("");
                        textRepNewPass.setText("");
                    }

                }



                break;

            //botón atrás
            case R.id.imageBack:

                finish();

                break;

        }

    }
}