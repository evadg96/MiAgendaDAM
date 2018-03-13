package es.proyecto.eva.miagendadam.Fragments.Diario;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.proyecto.eva.miagendadam.R;

/**
 * Created by Eva on 01/01/2018.
 */

/***************************************************************************************************
 * Clase adaptador de la lista de registros de diario del usuario que sirve para personalizarla
 * utilizando como estructura base el layout listview_item_row.xml
 **************************************************************************************************/
public class AdaptadorListaDiario extends BaseAdapter {
    private Context context;
    private ArrayList <String> arrayFechas;
    private ArrayList <String> arrayHoras;
    private ArrayList <String> arrayMinutos;
    private ArrayList <String> arrayValoraciones;

    public AdaptadorListaDiario (Context context, ArrayList<String> arrayFechas, ArrayList<String> arrayHoras, ArrayList<String> arrayMinutos, ArrayList<String> arrayValoraciones){
        this.context = context;
        this.arrayFechas = arrayFechas;
        this.arrayHoras = arrayHoras;
        this.arrayMinutos = arrayMinutos;
        this.arrayValoraciones = arrayValoraciones;
    }

    @Override
    public int getCount() {
        return arrayFechas.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayFechas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_registro_diario, null);
        }
        TextView tvFecha = (TextView) convertView.findViewById(R.id.tv_fecha); // la fecha del registro (dd/mm/aa)
        TextView tvHoras = (TextView) convertView.findViewById(R.id.tv_horas); // las horas del registro
        TextView tvMinutos = (TextView) convertView.findViewById(R.id.tv_minutos); // los minutos del registro
        TextView tag_valoracion = (TextView) convertView.findViewById(R.id.tv_valoracion); // la valoración del registro.
        // Será un cuadrado en el lateral superior derecho de cada registro que servirá para indicarnos a primera vista
        // mediante un color de fondo definitorio la valoración del registro sin entrar en él para verlo.

        tvFecha.setText(arrayFechas.get(position));
        tvHoras.setText(arrayHoras.get(position));
        tvMinutos.setText(arrayMinutos.get(position));
        // Si la valoración del registro es buena, se pone el cuadro de color verde
        if (arrayValoraciones.get(position).equals("Bueno")){
            tag_valoracion.setBackgroundColor(Color.parseColor("#cc76ce04"));
        // Si la valoración del registro es regular, se pone el cuadro de color amarillo
        } else if (arrayValoraciones.get(position).equals("Regular")){
            tag_valoracion.setBackgroundColor(Color.parseColor("#cedfbe02"));
        // Si la valoración del registro es mala, se pone el cuadro de color rojo
        } else if (arrayValoraciones.get(position).equals("Malo")){
            tag_valoracion.setBackgroundColor(Color.parseColor("#c7de0000"));
        }
        return convertView;
    }
}
