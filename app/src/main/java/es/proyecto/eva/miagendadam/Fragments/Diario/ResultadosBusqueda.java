package es.proyecto.eva.miagendadam.Fragments.Diario;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import static es.proyecto.eva.miagendadam.Fragments.Diario.BusquedaRegistros.jsonArray;

import es.proyecto.eva.miagendadam.R;

public class ResultadosBusqueda extends AppCompatActivity {
    ListView listaResultado;
    ArrayList<String> arrayFechas = new ArrayList<>(); // array en el que introduciremos las fechas obtenidas
    ArrayList <String> arrayHoras = new ArrayList<>(); // array en el que introduciremos las horas obtenidas
    ArrayList <String> arrayMinutos = new ArrayList<>(); // array en el que introduciremos los minutos obtenidos
    ArrayList <String> arrayValoraciones = new ArrayList<>(); // array en el que introduciremos las valoraciones obtenidas
    AdaptadorListaDiario adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_busqueda);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listaResultado = (ListView) findViewById(R.id.lista_resultados_busqueda);
        // recorremos el array para obtener los datos y añadirlos a la lista
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                String dia = jsonArray.getJSONObject(i).getString("dia");
                String mes = jsonArray.getJSONObject(i).getString("mes");
                String anyo = jsonArray.getJSONObject(i).getString("anyo");
                String fecha = dia + "/" + mes + "/" + anyo;
                System.out.println("FECHA: " + fecha);
                arrayFechas.add("Día "+fecha);
                String horas = jsonArray.getJSONObject(i).getString("horas");
                System.out.println("HORAS: " + horas);
                arrayHoras.add(horas + " horas"); // las añadimos al array de horas
                String minutos = jsonArray.getJSONObject(i).getString("minutos"); // obtenemos minutos
                System.out.println("MINUTOS: " + minutos);
                if (Integer.valueOf(minutos) > 0){ // si hay minutos se añaden
                    arrayMinutos.add(" y "+ minutos + " minutos");
                } else if (Integer.valueOf(minutos) < 1){ // si no se deja en blanco para no poner un 0
                    arrayMinutos.add("");
                }
                String valoracion = jsonArray.getJSONObject(i).getString("valoracion"); // obtenemos valoración
                System.out.println("VALORACIÓN: " + valoracion);
                arrayValoraciones.add(valoracion); // las añadimos al array de valoraciones
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        adaptador = new AdaptadorListaDiario(this, arrayFechas, arrayHoras, arrayMinutos, arrayValoraciones);
        listaResultado.setAdapter(adaptador); // lo asociamos a la lista
/**
        // Al pulsar sobre algún item de la lista (sobre algún registro del diario) lo mostramos en detalle en otra actividad:
        listaResultado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> customerAdapter, View footer, int selectedInt, long selectedLong) {
                // String listChoice = (String) listaResultado.getItemAtPosition(selectedInt); // para mostrar la selección pulsada
                int id = (int) listaResultado.getItemIdAtPosition(selectedInt); // obtenemos el id del elemento del listado seleccionado
                // para saber qué id de día debemos obtener
                try {
                    // obtenemos los datos del elemento seleccionado

                    // después de obtener los datos abrimos la nueva actividad que nos permitirá visualizarlos
                    // y editarlos en sus correspondientes campos
                    Log.d("DiarioFragment", "Vista detalle de un registro");
                    Intent intent = new Intent(ResultadosBusqueda.this, VerYEditarRegistroDiario.class);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("DiarioFragment", "Error al obtener los datos del registro a visualizar en detalle");
                }
            }
        }); **/
    }

}
