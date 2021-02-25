package com.example.hideout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Clase crear reto en la que insertaremos una imagen, una pista y enviaremos a firebase los datos
 */
public class CrearReto extends AppCompatActivity implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1; //Petición de permisos de la imagen
    double latitud; //Latitud del usuario
    double longitud; //Longitud del usuario
    private FirebaseAuth mAuth; //Autentificación del usuario
    private FirebaseUser user; //Usuario obtenido de firebase
    private FirebaseStorage storage; //Referencia al contenedor de aerchivos de firebase
    private StorageReference storageRef; //Referencia a la base de datos de firebase
    private String currentPhotoPath; //Ruta de la imagen creada
    private LocationTrack locationTrack; //Clase locationTrack que muestra el geoposicionamiento del usuario
    private String urlImagen; //URL de la imagen subida a firestorage
    private EditText eTPista; //EditText de la pista del reto
    private Reto reto; //Objeto reto a subir a firebase

    /**
     * Metodo que se ejecuta al lanzar el activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_reto); //Layout asignado a la clase

        //el juego está pensado para pantalla vertical, así que forzamos dicha posicion
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance(); //Obtiene la instandcia de la base de datos previamente asignada
        //obtenemos el usuario
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance("gs://hideout-d08d6.appspot.com"); //Enlace a la base de datos de firebase

        storageRef = storage.getReference(); //Obtenemos la referencia a la base de datos

        //Pide acceso a la geolocalización en caso de que no se haya asignado antes
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

        //Asigna los elementos a los del layout y crea los listener
        eTPista = findViewById(R.id.eTPista);
        findViewById(R.id.bSubirFotoReto).setOnClickListener(this);
        findViewById(R.id.imageBack).setOnClickListener(this);
        findViewById(R.id.bCrearReto).setOnClickListener(this);

    }

    /**
     * Metodo que se ejecuta al clickar en los distintos elementos
     * @param v Elemento pulsado
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bSubirFotoReto: //Botón que abre el intent de la camara

                dispatchTakePictureIntent();
                break;

            //botón atrás
            case R.id.imageBack: //Botón atrás

                finish();

                break;

            case R.id.bCrearReto: //Botón que crea el reto insertandolo en bd

                if (validar()) { //Si cumple los requisitos intentará subir a firebase el reto
                    try {
                        reto = new Reto();
                        reto.setIdUsu(user.getUid()); //ID del usuario
                        reto.setNomUsu(user.getDisplayName()); //Nombre del usuario
                        reto.setPista(eTPista.getText().toString()); //Pista del reto
                        //Posicion actual del reto
                        reto.setLatitud(latitud);
                        reto.setLongitud(longitud);

                        reto.setImagen(currentPhotoPath);//Ruta de la imagen creada
                        uploadImage(); //Subimos la imagen individualmente a firestorage
                    } catch (IOException e) {
                    }

                    // Muestra un mensaje indicando que se ha subido el reto correctamente
                    AlertDialog.Builder builder = new AlertDialog.Builder(CrearReto.this);
                    //informamos al usuario con un dialog del exito
                    builder.setTitle("Información");
                    builder.setMessage("¡Reto creado con éxito!");
                    builder.setCancelable(false);

                    //Botónes para terminar el reto, cerrando la activity
                    builder.setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // redirigimos a la pantalla principal
                                    finish();
                                }
                            });
                    builder.setNegativeButton("",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // redirigimos a la pantalla principal
                                    finish();
                                }
                            });
                    AlertDialog infoDialog = builder.create();
                    infoDialog.show();
                }

                break;
        }
    }

    /**
     * Metodo que crea el archivo que se guardará en el movil a partir de la foto creada
     * @return Archvivo creado
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Crea el nombre del archivo con la fecha actual
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Carpeta donde se guardará la imagen
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Guarda la ruta de la imagen para que sea usada
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1; //Pide permiso para usar la camara del movil

    /**
     * Metodo que realiza la foto y la guarda en un archivo creado
     */
    private void dispatchTakePictureIntent() {
//Pide permiso para guardar en el almacenamiento externo
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //  Se asegura de que la camara está operativa
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Crea el archivo en el que irá la foto
            File photoFile = null;
            try {
                photoFile = createImageFile(); //Crea el archivo y lo guarda en su ruta asignada
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println(ex);
            }
            // Solo si la imagen ha sido creada correctamente continua
            if (photoFile != null) {
                //Path en la que se guarda la imagen
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                //Abre el intent de la camara para guardar la foto
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Metodo que se ejcuta tras realizar la foto
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Si ha creado la foto con exito, pide la localización al usuario y la almacena en las variables
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            locationTrack = new LocationTrack(this);
            latitud = locationTrack.getLatitude();
            longitud = locationTrack.getLongitude();

            setPic(); //Muestra la imagen en el ImageView
        }
    }

    /**
     * A partir de la imagen creada, la redimensiona y la muestra en el ImageView
     */
    private void setPic() {
        ImageView imageView = findViewById(R.id.imgReto);
        // Dimensiones del marco
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Dimensiones de la imagen
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Escala la imagen
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Descomprime el bitmap para usarlo en el imageview
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        bitmap = rotateImage(bitmap, 90); //Rota la imagen ya para que no salga tumbada
        imageView.setImageBitmap(bitmap); //Muestra la imagen
    }

    /**
     * Metodo que sube la imagen al firestorage de firebase y tras ello, obtiene el link e inserta el reto en BD
     * @throws IOException
     */
    private void uploadImage() throws IOException {
        Uri file = Uri.fromFile(new File(currentPhotoPath)); //Obtiene la imagen desde la ruta
        final StorageReference imgRef = storageRef.child("imagenes/" + file.getLastPathSegment()); //Ruta de Firestorage en la que se guardará

        //Genera el bitmap desde el archivo obtenido
        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(currentPhotoPath)));

        bmp=rotateImage(bmp,90); //Rota la imagen para que no esté tumbadaa
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos); //Comprime la imagen para que no pese mucho.
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imgRef.putBytes(data);


        // Observador que notifica cuando la subida de la foto ha finalizado
        uploadTask.addOnFailureListener(new OnFailureListener() {
            /**
             * Si falla no subirá nada a firebase
             * @param exception
             */
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                /*
                Toast toast =
                        Toast.makeText(getApplicationContext(),
                                "FRACASO", Toast.LENGTH_SHORT);

                toast.show();*/
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            /**
             * Si consigue subir la imagen, obtendremos la url y seguidamente insertaremos el objeto reto
             * @param taskSnapshot
             */
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                /*
                Toast toast =
                        Toast.makeText(getApplicationContext(),
                                "EXITO", Toast.LENGTH_SHORT);

                toast.show();*/
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        urlImagen = downloadUrl.toString(); //URL DE LA IMAGEN
                        reto.setImagen(urlImagen);
                        //Obtiene la instacia de la base de datos y lo pushea
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Retos");

                        myRef.push().setValue(reto);

                    }
                });
            }
        });
    }

    /**
     * Metodo que valida el reto antes de subirlo
     * @return
     */
    private boolean validar() {

        //booleano de control
        boolean todoOk = true;

        //comprobacion de que la pista tiene al menos 2 caracteres y menos de 150
        if (eTPista.getText().length() < 2 || eTPista.getText().length() > 150) {
            todoOk = false;
            eTPista.setError("Este campo ha de tener entre 2 y 150 caracteres");
        }

        //Comprobacion de que el email es correcto
        if (latitud == 0 || longitud == 0) {
            todoOk = false;
            Toast toast =
                    Toast.makeText(getApplicationContext(),
                            "Ha ocurrido un error de geolocalización", Toast.LENGTH_SHORT);

            toast.show();
        }

        //comprobacion de que el password tiene al menos 5 caracteres y menos de 20
        if (currentPhotoPath == null) {
            todoOk = false;
            Toast toast =
                    Toast.makeText(getApplicationContext(),
                            "Debe hacer una foto para crear el reto", Toast.LENGTH_SHORT);

            toast.show();
        }

        return todoOk;
    }

    /**
     * Metodo que rota la imagen los grados que le indiquemos para enderezarla
     * @param img imagen a girar
     * @param degree grados
     * @return retorna la imagen girada
     */
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle(); //Recicla el bitmap para que no ocupe mucha memoria
        return rotatedImg;
    }

}