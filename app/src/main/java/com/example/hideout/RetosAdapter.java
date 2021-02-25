package com.example.hideout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Adaptador del listview de los retos
 */
public class RetosAdapter  extends ArrayAdapter<Reto> {

    TextView textSuperior;
    TextView textInferior;

    Bitmap bmp;

    /**
     * Conructor del adapter
     *
     * @param context activity
     * @param retos arraylist de retos
     */
    public RetosAdapter(@NonNull Context context, @NonNull ArrayList<Reto> retos) {
        super(context, 0, retos);
    }

    /**
     * Método para mostrar la info en el layout deseado
     *
     * @param position posicion del reto
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //instance de la db
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://hideout-d08d6.appspot.com");
        // Cogemos el dato de la posicion
        Reto reto = getItem(position);
        // si el convertview es nulo
        if (convertView == null) {
            //aplicamos la vista del layout
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_listview, parent, false);
        }
        // instanciamos los campos del layout
        final ImageView imagen = (ImageView) convertView.findViewById(R.id.imagen);
        textSuperior = (TextView) convertView.findViewById(R.id.textSuperior);
        textInferior = (TextView) convertView.findViewById(R.id.textInferior);

        //referencia del storage
        StorageReference gsReference = storage.getReferenceFromUrl(reto.getImagen());

        final long ONE_MEGABYTE = 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagen.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        //locationtrack
        LocationTrack location = new LocationTrack(this.getContext());

        //posicion del reto
        Location actual = new Location("Actual");
        actual.setLongitude(reto.getLongitud());
        actual.setLatitude(reto.getLatitud());

        //posicion del usuario
        Location usu = new Location("usu");
        usu.setLatitude(location.getLatitude());
        usu.setLongitude(location.getLongitude());

        textSuperior.setText(distancia(actual.distanceTo(usu)));
        textInferior.setText(reto.getPista());

        return convertView;
    }

    /**
     * Método que traduce la distancia entre el usuario y el reto en "MUY CERCA, CERCA, MEDIO, LEJOS y MUY LEJOS"
     *
     * @param actualDistance distancia
     * @return devuelve un string
     */
    public String distancia(float actualDistance){

        String distancia = "";

        //segun la distancia cambia el string y el color de la palabra
        if(actualDistance>350){
            distancia = "MUY LEJOS";
            textSuperior.setTextColor(Color.rgb(240, 55, 0));
        }else if(actualDistance>250){
            distancia = "LEJOS";
            //textSuperior.setTextColor(Color.argb(1, 240, 127, 0));
            textSuperior.setTextColor(Color.rgb(240, 127, 0));
        }else if(actualDistance>100){
            distancia = "MEDIO";
            //textSuperior.setTextColor(Color.argb(1, 201, 201, 0));
            textSuperior.setTextColor(Color.rgb(201, 201, 0));
        }else if(actualDistance>50){
            distancia = "CERCA";
            //textSuperior.setTextColor(Color.argb(1, 17, 195, 0));
            textSuperior.setTextColor(Color.rgb(17, 195, 0));
        }else{
            distancia = "MUY CERCA";
            //textSuperior.setTextColor(Color.argb(1, 44, 235, 25));
            textSuperior.setTextColor(Color.rgb(44, 235, 25));
        }

        return distancia;
    }

    /**
     * Método para limpiar memoria cache ocupada por el bitmap
     */
    public void clearMemory(){
        try {
            bmp.recycle();
        }catch (Exception e){

        }
    }
}
