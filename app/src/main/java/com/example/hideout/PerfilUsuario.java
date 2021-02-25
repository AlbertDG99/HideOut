package com.example.hideout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

        //el juego está pensado para pantalla vertical, así que forzamos dicha posicion
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

        //listeners
        buttonEditar.setOnClickListener(this);
        findViewById(R.id.buttonChange).setOnClickListener(this);
        findViewById(R.id.buttonDelete).setOnClickListener(this);
        findViewById(R.id.imageBack).setOnClickListener(this);

    }

    /**
     * Método onclick de los listeners
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //botón editar nombre
            case R.id.buttonEditar:

                //si los campos estan activados y no están vacios
                if (textUserName.isEnabled() && textUserName.getText().length() != 0 && textUserMail.isEnabled() && textUserMail.getText().length() != 0) {

                    //actualizamos el nombre de usuario
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(String.valueOf(textUserName.getText()))
                            .build();

                    //si el nombre de usuario nuevo es diferente al antiguo
                    if(!Objects.equals(user.getDisplayName(), textUserName.getText().toString())){
                        //lo actualizamos
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //si el proceso se completa se notifica
                                        if (task.isSuccessful()) {
                                            Toast toast =
                                                    Toast.makeText(getApplicationContext(),
                                                            "Nombre actualizado con éxito", Toast.LENGTH_SHORT);

                                            toast.show();
                                        }
                                    }
                                });
                    }

                    //si el email nuevo es diferente al antiguo
                    if(!Objects.equals(user.getEmail(), textUserMail.getText().toString())) {
                        //lo actualizamos
                        user.updateEmail(String.valueOf(textUserMail.getText()))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //si el proceso se completa se notifica
                                        if (task.isSuccessful()) {
                                            Toast toast =
                                                    Toast.makeText(getApplicationContext(),
                                                            "Email actualizado con éxito", Toast.LENGTH_SHORT);

                                            toast.show();
                                        }else{
                                            //si el email ya existe en la base de datos
                                            if(validacionMail(textUserMail.getText().toString())){
                                                Toast toast =
                                                        Toast.makeText(getApplicationContext(),
                                                                "Ese email ya está registrado", Toast.LENGTH_SHORT);

                                                toast.show();
                                            }else{
                                                //si el formato del email no es válido
                                                Toast toast =
                                                        Toast.makeText(getApplicationContext(),
                                                                "Formato de email erróneo", Toast.LENGTH_SHORT);

                                                toast.show();
                                            }

                                        }
                                    }
                                });
                    }

                    //una vez actualizados los datos los valores de los campos vuelve a los iniciales
                    textUserName.setEnabled(false);
                    textUserMail.setEnabled(false);
                    buttonEditar.setText("EDITAR PERFIL");

                } else {
                    //al primer click se activan los campos para modificar los datos
                    textUserName.setEnabled(true);
                    textUserMail.setEnabled(true);
                    buttonEditar.setText("GUARDAR CAMBIOS");
                }
                break;

            //botón editar password
            case R.id.buttonChange:

                //si los campos de password estan vacios
                if(textNewPass.getText().length() == 0 || textRepNewPass.getText().length() == 0){
                    Toast toast =
                            Toast.makeText(getApplicationContext(),
                                    "Los campos no pueden estar vacios", Toast.LENGTH_SHORT);

                    toast.show();
                }else{
                    //comprobación de si coinciden los paswords nuevos
                    if(!textNewPass.getText().toString().equals(textRepNewPass.getText().toString())){

                        Toast toast =
                                Toast.makeText(getApplicationContext(),
                                        "Las contraseñas no coinciden", Toast.LENGTH_SHORT);

                        toast.show();
                    }else{
                        //si coinciden
                        String newPassword = textNewPass.getText().toString();
                        //se actualizan
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
                        //se vacían los campos
                        textNewPass.setText("");
                        textRepNewPass.setText("");
                    }
                }
                break;

            //botón eliminar perfil
            case R.id.buttonDelete:

                //creamos el dialog con el que pdiremos la confirmacion del usuario
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                //se requiere la confirmacion
                builder.setMessage("Se borrará el perfil actual. Esta acción no se puede deshacer ¿Está de acuerdo?")
                        .setPositiveButton("Eliminar perfil",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //se elimina el perfil del usuario
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(PerfilUsuario.this);
                                                    //informamos al usuario con un dialog del exito
                                                    builder.setTitle("Información");
                                                    builder.setMessage("Perfil eliminado con éxito.");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("Aceptar",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    // redirigimos a la pantalla principal
                                                                    FirebaseUser user = null;
                                                                    finish();
                                                                }
                                                    });
                                                    builder.setNegativeButton("",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    // redirigimos a la pantalla principal
                                                                    FirebaseUser user = null;
                                                                    finish();
                                                                }
                                                            });
                                                    AlertDialog infoDialog = builder.create();
                                                    infoDialog.show();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;

            //botón atrás
            case R.id.imageBack:
                finish();
                break;
        }
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