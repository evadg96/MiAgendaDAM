package es.proyecto.eva.miagendadam.Fragments.Diario;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import es.proyecto.eva.miagendadam.R;

/**
 * Created by Eva on 01/01/2018.
 */


public class AdaptadorListaDiario extends BaseAdapter {
    private Context context;
    private ArrayList<String> arrayFechas;
    private ArrayList<String> arrayHoras;
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
            convertView = View.inflate(context, R.layout.listview_item_row, null);
        }
        TextView tvFecha = (TextView) convertView.findViewById(R.id.tv_fecha);
        TextView tvHoras = (TextView) convertView.findViewById(R.id.tv_horas);
        TextView tvMinutos = (TextView) convertView.findViewById(R.id.tv_minutos);
        TextView fondo = (TextView) convertView.findViewById(R.id.tv_valoracion);
        tvFecha.setText(arrayFechas.get(position));
        tvHoras.setText(arrayHoras.get(position));
        tvMinutos.setText(arrayMinutos.get(position));
        if (arrayValoraciones.get(position).equals("Bueno")){
            fondo.setBackgroundColor(Color.parseColor("#7B34B903"));
        } else if (arrayValoraciones.get(position).equals("Regular")){
            fondo.setBackgroundColor(Color.parseColor("#7BB9AD03"));
        } else if (arrayValoraciones.get(position).equals("Malo")){
            fondo.setBackgroundColor(Color.parseColor("#7BB90303"));
        }
        return convertView;
    }
}
