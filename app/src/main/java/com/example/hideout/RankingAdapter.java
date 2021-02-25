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

/**
 * Adaptador del listview del ranking
 */
public class RankingAdapter  extends ArrayAdapter<Usuario> {

    ImageView imagen; //imagen mostrar

    private int cont = 0; //contador auxiliar para mostrar las medallas

    /**
     * contructor del adapter
     *
     * @param context activity
     * @param usuarios arraylist de usuarios
     */
    public RankingAdapter(@NonNull Context context,  @NonNull ArrayList<Usuario> usuarios) {
        super(context, 0, usuarios);
    }

    /**
     * Método para mostrar la info en el layout deseado
     *
     * @param position posicion del usuario
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //instance de la db
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://hideout-d08d6.appspot.com");
        // Cogemos el dato de la posicion
        Usuario usuario = getItem(position);
        // si el convertview es nulo
        if (convertView == null) {
            //aplicamos la vista del layout
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_ranking, parent, false);
        }
        // instanciamos los campos del layout
        imagen = (ImageView) convertView.findViewById(R.id.imagenMed);
        TextView textSuperiorRank = (TextView) convertView.findViewById(R.id.textSuperiorRank);
        TextView textInferiorRank = (TextView) convertView.findViewById(R.id.textInferiorRank);

        //si usuario no es nulo
        assert usuario != null;
        textSuperiorRank.setText(usuario.getNombre());
        textInferiorRank.setText(Integer.toString(usuario.getMonedas()));
        //llamada al metodo de mostrar la medalla correcta
        imagenMedallas();

        //aumentamos el contador
        cont++;

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Método para mostrar las medallas en orden
     */
    private void imagenMedallas(){

        //switch segun la posicion del usuario
        switch(cont){
            case 0: //primera posicion
                imagen.setImageResource(R.drawable.goldicon);
                break;
            case 1: //segunda posicion
                imagen.setImageResource(R.drawable.silvericon);
                break;
            case 2: //tercera posicion
                imagen.setImageResource(R.drawable.bronceicon);
                break;
            default:
                break;
        }
    }
}
