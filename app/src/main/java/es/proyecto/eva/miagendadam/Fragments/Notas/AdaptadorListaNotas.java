package es.proyecto.eva.miagendadam.Fragments.Notas;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import es.proyecto.eva.miagendadam.R;

/**
 * Created by Eva on 01/01/2018.
 */

/***************************************************************************************************
 * Clase adaptador de la lista de anotaciones del usuario que sirve para personalizarla
 * utilizando como estructura base el layout item_nota.xml
 **************************************************************************************************/
public class AdaptadorListaNotas extends BaseAdapter {
    private Context context;
    private ArrayList <String> arrayContenidoNota;
    private ArrayList <String> arrayFechaEdicionNota;
    private ArrayList <String> arrayColorNota;

    public AdaptadorListaNotas(Context context, ArrayList<String> arrayContenidoNota, ArrayList<String> arrayFechaEdicionNota, ArrayList<String> arrayColorNota){
        this.context = context;
        this.arrayContenidoNota = arrayContenidoNota;
        this.arrayFechaEdicionNota = arrayFechaEdicionNota;
        this.arrayColorNota = arrayColorNota;
    }

    @Override
    public int getCount() {
        return arrayContenidoNota.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayContenidoNota.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_nota, null);
        }
        TextView tvContenido = (TextView) convertView.findViewById(R.id.tv_contenido); // el contenido de forma breve
        TextView tvFechaEdicion = (TextView) convertView.findViewById(R.id.tv_fecha_edicion); // las horas del registro
        LinearLayout fondoNota = (LinearLayout) convertView.findViewById(R.id.fondo_nota); // la valoración del registro.

        tvContenido.setText(arrayContenidoNota.get(position));
        tvFechaEdicion.setText(arrayFechaEdicionNota.get(position));

        // vamos obteniendo el código para cada registro
        String codigoColor = (arrayColorNota.get(position));
        // si se detecta este color (transparente) es que se ha guardado la nota sin color, y tendremos que poner el texto del contenido
        // de color negro para que pueda leerse ya que el fondo se verá blanco
        if (codigoColor.equals("#00000000") || codigoColor.equals("#cddc39") || codigoColor.equals("#ffeb3b") ||
                codigoColor.equals("#ffc107")){
            tvContenido.setTextColor(Color.BLACK);
            tvFechaEdicion.setTextColor(Color.BLACK);
        } else {
            tvContenido.setTextColor(Color.WHITE);
            tvFechaEdicion.setTextColor(Color.WHITE);
        }
        // establecemos el color de fondo de la nota en función del color que corresponda con cada una
        fondoNota.setBackgroundColor(Color.parseColor(codigoColor.toString()));
        return convertView;
    }
}
