package es.proyecto.eva.miagendadam.Fragments.Recursos;

import android.content.Context;
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
 * Clase adaptador de la lista de páginas web recomendadas en los recursos para el usuario
 * utilizando como estructura base el layout item_pagina_recomendada.xml
 **************************************************************************************************/
public class AdaptadorListaPaginasRecomendadas extends BaseAdapter {
    private Context context;
    private ArrayList <String> arrayTitulos;
    private ArrayList <String> arrayCategorias;

    public AdaptadorListaPaginasRecomendadas(Context context, ArrayList<String> arrayTitulos, ArrayList<String> arrayCategorias){
        this.context = context;
        this.arrayTitulos = arrayTitulos;
        this.arrayCategorias = arrayCategorias;
    }

    @Override
    public int getCount() {
        return arrayTitulos.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayTitulos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_pagina_recomendada, null);
        }
        TextView tvTitulo = (TextView) convertView.findViewById(R.id.tv_titulo_pag); // el título representativo de la página
        TextView tvCategoria = (TextView) convertView.findViewById(R.id.tv_categoria_pag); // la categoría en la que clasificamos la página y su contenido
        tvTitulo.setText(arrayTitulos.get(position));
        tvCategoria.setText(arrayCategorias.get(position));

        return convertView;
    }
}
