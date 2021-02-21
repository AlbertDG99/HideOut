package com.example.hideout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.List;

import javax.security.auth.Destroyable;

public class RetosAdapter  extends ArrayAdapter<Reto> {

    Bitmap bmp;
    public RetosAdapter(@NonNull Context context, @NonNull ArrayList<Reto> retos) {
        super(context, 0, retos);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://hideout-d08d6.appspot.com");
        // Get the data item for this position
        Reto reto = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_listview, parent, false);
        }
        // Lookup view for data population
        final ImageView imagen = (ImageView) convertView.findViewById(R.id.imagen);
        TextView textSuperior = (TextView) convertView.findViewById(R.id.textSuperior);
        TextView textInferior = (TextView) convertView.findViewById(R.id.textInferior);

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

        // Populate the data into the template view using the data object

        LocationTrack location = new LocationTrack(this.getContext());

        Location actual = new Location("Actual");
        actual.setLongitude(reto.getLongitud());
        actual.setLatitude(reto.getLatitud());

        Location usu = new Location("usu");
        usu.setLatitude(location.getLatitude());
        usu.setLongitude(location.getLongitude());

        textSuperior.setText(Float.toString(actual.distanceTo(usu)));
        textInferior.setText(reto.getPista());
        // Return the completed view to render on screen


        return convertView;
    }



    public void clearMemory(){
        bmp.recycle();
    }
}
