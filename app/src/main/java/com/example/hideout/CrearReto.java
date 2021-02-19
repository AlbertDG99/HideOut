package com.example.hideout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CrearReto extends AppCompatActivity implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    double latitud;
    double longitud;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String currentPhotoPath;
    private LocationTrack locationTrack;
    private String urlImagen;
    private EditText eTPista;
    private Reto reto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_reto);

        mAuth = FirebaseAuth.getInstance();
        //obtenemos el usuario
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance("gs://hideout-d08d6.appspot.com");

        storageRef = storage.getReference();

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

        eTPista = findViewById(R.id.eTPista);


        findViewById(R.id.bSubirFotoReto).setOnClickListener(this);
        findViewById(R.id.imageBack).setOnClickListener(this);
        findViewById(R.id.bCrearReto).setOnClickListener(this);

        /*
        StorageReference gsReference = storage.getReferenceFromUrl("gs://hideout-d08d6.appspot.com/imagenes/JPEG_20210214_213750_5124400265805042627.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ImageView img = findViewById(R.id.image);
                img.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bSubirFotoReto:

                dispatchTakePictureIntent();
                break;

            //botón atrás
            case R.id.imageBack:

                finish();

                break;

            case R.id.bCrearReto:

                if(validar()){
                    try{
                        reto = new Reto();
                        reto.setIdUsu(user.getUid());
                        reto.setNomUsu(user.getDisplayName());
                        reto.setPista(eTPista.getText().toString());
                        reto.setLatitud(latitud);
                        reto.setLongitud(longitud);
                        reto.setImagen(currentPhotoPath);
                        uploadImage();
                    }catch(IOException e){}

                    AlertDialog.Builder builder = new AlertDialog.Builder(CrearReto.this);
                    //informamos al usuario con un dialog del exito
                    builder.setTitle("Información");
                    builder.setMessage("¡Reto creado con éxito!");
                    builder.setCancelable(false);
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println(ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            locationTrack = new LocationTrack(this);
            latitud = locationTrack.getLatitude();
            longitud = locationTrack.getLongitude();

            setPic();
        }
    }


    private void setPic() {
        ImageView imageView = findViewById(R.id.imgReto);
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        imageView.setImageBitmap(bitmap);
    }



    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void uploadImage() throws IOException {
        Uri file = Uri.fromFile(new File(currentPhotoPath));
        final StorageReference imgRef = storageRef.child("imagenes/"+file.getLastPathSegment());

       Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(currentPhotoPath)));//
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);//
        byte[] data = baos.toByteArray();
       UploadTask uploadTask = imgRef.putBytes(data);


        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
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
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                /*
                Toast toast =
                        Toast.makeText(getApplicationContext(),
                                "EXITO", Toast.LENGTH_SHORT);

                toast.show();*/
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri downloadUrl)
                    {
                    urlImagen= downloadUrl.toString();
                        reto.setImagen(urlImagen);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Retos");
                        myRef.push().setValue(reto);

                    }
                });
            }
        });
    }

    private boolean validar() {

        //booleano de control
        boolean todoOk = true;

        //comprobacion de que la pista tiene al menos 2 caracteres y menos de 150
        if (eTPista.getText().length() < 2 || eTPista.getText().length() > 150) {
            todoOk = false;
            eTPista.setError("Este campo ha de tener entre 2 y 150 caracteres");
        }

        //comprobacion de que el email es correcto
        if (latitud == 0 || longitud == 0) {
            todoOk = false;
            Toast toast =
                    Toast.makeText(getApplicationContext(),
                            "Ha ocurrido un error de geolocalización", Toast.LENGTH_SHORT);

            toast.show();
        }

        //comprobacion de que el password tiene al menos 5 caracteres y menos de 20
        if (currentPhotoPath.length() == 0) {
            todoOk = false;
            Toast toast =
                    Toast.makeText(getApplicationContext(),
                            "Debe hacer una foto para crear el reto", Toast.LENGTH_SHORT);

            toast.show();
        }

        return todoOk;
    }

}