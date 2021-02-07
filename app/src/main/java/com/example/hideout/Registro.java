package com.example.hideout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Registro extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private EditText regUserName;
    private EditText regUserMail;
    private EditText regPass;
    private EditText regRepPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();

        //instanciamos los campos del formulario
        regUserName = findViewById(R.id.regUserName);
        regUserMail = findViewById(R.id.regUserMail);
        regPass = findViewById(R.id.regPass);
        regRepPass = findViewById(R.id.regRepPass);

        //declaramos los listeners
        findViewById(R.id.buttonNewRegistro).setOnClickListener(this);
        findViewById(R.id.imageBack).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //botón de registro
            case R.id.buttonNewRegistro:

                if(validar()){
                    mAuth.createUserWithEmailAndPassword(regUserMail.getText().toString(), regPass.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        //actualizamos perfil con el nombre del usuario
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(String.valueOf(regUserName.getText()))
                                                .build();

                                        //ejecutamos la actualizacion
                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                        }
                                                    }
                                                });

                                        //creamos el dialog con el que pdiremos la confirmacion del usuario
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                                        //informamos al usuario con un dialog del exito
                                        builder.setTitle("Información");
                                        builder.setMessage("Usuario registrado con éxito.");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Aceptar",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // redirigimos a la pantalla principal
                                                        finish();
                                                    }
                                                });
                                        AlertDialog infoDialog = builder.create();
                                        infoDialog.show();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                    }

                                    // ...
                                }
                            });
                }

                break;

            //botón atrás
            case R.id.imageBack:

                finish();

                break;

        }
    }

    /**
     * Método que valida los inputs del formulario
     *
     * @return devuelve true si esta correcto y false si no lo está
     */
    public boolean validar() {

        //booleano de control
        boolean todoOk = true;

        //comprobacion de que el nombre tiene al menos 2 caracteres y menos de 15
        if (regUserName.getText().length() < 2 || regUserName.getText().length() > 15) {
            todoOk = false;
            regUserName.setError("Este campo ha de tener al menos 2 caracteres");
        }

        //comprobacion de que el email es correcto
        if (regUserMail.getText().length() == 0 || regUserMail.getText().length() > 30 || !validacionMail(regUserMail.getText().toString())) {
            todoOk = false;
            regUserMail.setError("El email debe seguir la estructura tradicional");
        }

        //comprobacion de que el password tiene al menos 5 caracteres y menos de 20
        if (regPass.getText().length() < 5 || regPass.getText().length() > 20) {
            todoOk = false;
            regPass.setError("Este campo ha de tener al menos 5 caracteres");
        }

        //comprobación de que la repeticion de la contraseña coincide con la original
        if (!regRepPass.getText().toString().equals(regPass.getText().toString())) {
            todoOk = false;
            regRepPass.setError("Las contraseñas no coinciden");
        }

        return todoOk;
    }

    /**
     * Método de validacion de email mediante expresionas regulares (palabra + @ + palabra + . + palabra)
     *
     * @param email email a comprobar
     * @return true si es válido y false si no lo es
     */
    static boolean validacionMail(String email) {
        String expReg = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(expReg);
    }


}