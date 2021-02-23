package com.example.hideout;

import android.content.Context;
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

public class RankingAdapter  extends ArrayAdapter<Usuario> {


    public RankingAdapter(@NonNull Context context,  @NonNull ArrayList<Usuario> usuarios) {
        super(context, 0, usuarios);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://hideout-d08d6.appspot.com");
        // Get the data item for this position
        Usuario usuario = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_ranking, parent, false);
        }
        // Lookup view for data population
        ImageView imagen = (ImageView) convertView.findViewById(R.id.imagenMed);
        TextView textSuperiorRank = (TextView) convertView.findViewById(R.id.textSuperiorRank);
        TextView textInferiorRank = (TextView) convertView.findViewById(R.id.textInferiorRank);

        // Populate the data into the template view using the data object

        assert usuario != null;
        textSuperiorRank.setText(usuario.getIdUsu());
        textInferiorRank.setText(usuario.getMonedas());

        // Return the completed view to render on screen
        return convertView;
    }

}
