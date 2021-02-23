package com.example.hideout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class RankingAdapter  extends ArrayAdapter<Usuario> {

    ImageView imagen;

    private int cont = 0;

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
        imagen = (ImageView) convertView.findViewById(R.id.imagenMed);
        TextView textSuperiorRank = (TextView) convertView.findViewById(R.id.textSuperiorRank);
        TextView textInferiorRank = (TextView) convertView.findViewById(R.id.textInferiorRank);

        // Populate the data into the template view using the data object

        assert usuario != null;
        textSuperiorRank.setText(usuario.getNombre());
        textInferiorRank.setText(Integer.toString(usuario.getMonedas()));
        imagenMedallas();

        cont++;

        // Return the completed view to render on screen
        return convertView;
    }

    private void imagenMedallas(){

        switch(cont){
            case 0:
                imagen.setImageResource(R.drawable.goldicon);
                break;
            case 1:
                imagen.setImageResource(R.drawable.silvericon);
                break;
            case 2:
                imagen.setImageResource(R.drawable.bronceicon);
                break;
            default:
                break;
        }
    }
}
