package es.proyecto.eva.miagendadam.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import es.proyecto.eva.miagendadam.Acciones.NuevoRegistroDiario;
import es.proyecto.eva.miagendadam.R;
import static es.proyecto.eva.miagendadam.NavMenu.jsonArrayDiario;

public class Diario extends Fragment {
    FloatingActionButton btnNuevo;
    ListView listaResultado;
    static String fecha;
    static String horas;
    static String descripcion;
    static String valoracion;
    public Diario() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diario, container, false);
        listaResultado = (ListView) view.findViewById(R.id.lista);
        cargarRegistros();
        btnNuevo = (FloatingActionButton) view.findViewById(R.id.btn_nuevo_registro);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NuevoRegistroDiario.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
// ____
    private void cargarRegistros() {
        ArrayList<String> lista = new ArrayList<>();
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lista);
        View rowView;
        for (int i = 0; i < jsonArrayDiario.length(); i++) { // i < long2 para determinar el número de ciclos que debe recorrer, eso será la cantidad de objetos que contenga el array, es decir, resultados tras la búsqueda
            try {
                fecha = jsonArrayDiario.getJSONObject(i).getString("fecha");
                horas = jsonArrayDiario.getJSONObject(i).getString("horas");
                descripcion = jsonArrayDiario.getJSONObject(i).getString("descripcion");
                valoracion = jsonArrayDiario.getJSONObject(i).getString("valoracion");
                System.out.println("VALORACIÓN: "+ valoracion);
                // verde A629AE00
                // rojo A2E70B0B
                // amarillo CEFFBA19
                adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lista){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent){
                        // Get the current item from ListView
                        View view = super.getView(position,convertView,parent);
                        if(valoracion.equals("Bueno")) {
                            // Set a background color for ListView regular row/item
                            view.setBackgroundColor(Color.parseColor("#A629AE00"));
                        }
                        else if (valoracion.equals("Regular")){
                            // Set the background color for alternate row/item
                            view.setBackgroundColor(Color.parseColor("#CEFFBA19"));
                        } else if (valoracion.equals("Malo")){
                            view.setBackgroundColor(Color.parseColor("#A2E70B0B"));
                        }
                        return view;
                    }
                };
                lista.add("Fecha: " + fecha
                        + "\nHoras: " + horas);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listaResultado.setAdapter(adaptador);
    }
    // ___fgf_____________________
}
