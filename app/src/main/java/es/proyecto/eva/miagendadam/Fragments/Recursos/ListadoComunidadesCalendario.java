package es.proyecto.eva.miagendadam.Fragments.Recursos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import es.proyecto.eva.miagendadam.R;

public class ListadoComunidadesCalendario extends AppCompatActivity {
    private String provincia = ""; // para el cal. de festivos locales
    private String comunidad_autonoma = ""; // para el cal. escolar
    private ArrayList<String> lista_comunidades;
    private ArrayAdapter<String> adaptador;
    public static String comunidad_seleccionada = "";
    ListView listaResultado;
    // Array de provincias
    private String[] comunidades = {"Andalucía", "Aragón", "Principado de Asturias", "Islas Baleares","Canarias","Cantabria","Castilla-La Mancha","Castilla y León",
            "Cataluña","Comunidad Valenciana", "Extremadura","Galicia","La Rioja","Comunidad de Madrid","Región de Murcia","Comunidad Foral de Navarra","País Vasco"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otras_comunidades);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listaResultado = (ListView) findViewById(R.id.lista_comunidades);
        // Obtenemos la provincia del usuario para saber qué calendario debemos cargar
        SharedPreferences preferences = this.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        provincia = preferences.getString("provincia", "");
        lista_comunidades = new ArrayList<String>();

        // rellenamos array list de comunidades
        for (int i = 0; i < comunidades.length; i++){
            comunidad_autonoma = comunidades[i]; // obtenemos las comunidades
            System.out.println(comunidad_autonoma);
            lista_comunidades.add(comunidad_autonoma); // añadimos al array list las comunidades que vayamos obteniendo
        }

        // ******************* DE COMPROBACIÓN EN DEBUG **********************
        System.out.println("ARRAY LIST DE COMUNIDADES: ");
        // lo recorremos para verificar que se han introducido correctamente
        for (int i = 0; i < lista_comunidades.size(); i++ ){
            System.out.println(lista_comunidades.get(i));
        }
        // *******************************************************************

        // Quitamos la comunidad autónoma de la lista que se corresponda
        if (provincia.equals("A Coruña") || provincia.equals("Lugo") || provincia.equals("Pontevedra") || provincia.equals("Orense")) {
            lista_comunidades.remove("Galicia");
        } else if (provincia.equals("Asturias")) {
            lista_comunidades.remove("Principado de Asturias");
        } else if (provincia.equals("Cantabria")) {
            lista_comunidades.remove("Cantabria");
        } else if (provincia.equals("Álava") || provincia.equals("Guipúzcoa") || provincia.equals("Vizcaya")) {
            lista_comunidades.remove("País Vasco");
        } else if (provincia.equals("Madrid")) {
            lista_comunidades.remove("Comunidad de Madrid");
        } else if (provincia.equals("Valladolid") || provincia.equals("Soria") || provincia.equals("Segovia") || provincia.equals("Salamanca") || provincia.equals("Burgos")
                || provincia.equals("Ávila") || provincia.equals("Palencia") || provincia.equals("Zamora") || provincia.equals("León")) {
            lista_comunidades.remove("Castilla y León");
        } else if (provincia.equals("Murcia")) {
            lista_comunidades.remove("Región de Murcia");
        } else if (provincia.equals("Albacete") || provincia.equals("Ciudad Real") || provincia.equals("Cuenca") || provincia.equals("Guadalajara")
                || provincia.equals("Toledo")) {
            lista_comunidades.remove("Castilla-La Mancha");
        } else if (provincia.equals("Las Palmas") || provincia.equals("Santa Cruz de Tenerife")) {
            lista_comunidades.remove("Canarias");
        } else if (provincia.equals("Almería") || provincia.equals("Cádiz") || provincia.equals("Córdoba") || provincia.equals("Granada") || provincia.equals("Huelva")
                || provincia.equals("Jaén") || provincia.equals("Málaga") || provincia.equals("Sevilla")) {
            lista_comunidades.remove("Andalucía");
        } else if (provincia.equals("Huesca") || provincia.equals("Teruel") || provincia.equals("Zaragoza")) {
            lista_comunidades.remove("Aragón");
        } else if (provincia.equals("Baleares")) {
            lista_comunidades.remove("Islas Baleares");
        } else if (provincia.equals("Barcelona") || provincia.equals("Girona") || provincia.equals("Lleida") || provincia.equals("Tarragona")) {
            lista_comunidades.remove("Cataluña");
        } else if (provincia.equals("Alicante") || provincia.equals("Valencia") || provincia.equals("Castellón")) {
            lista_comunidades.remove("Comunidad Valenciana");
        } else if (provincia.equals("Badajoz") || provincia.equals("Cáceres")) {
            lista_comunidades.remove("Extremadura");
        } else if (provincia.equals("La Rioja")) {
            lista_comunidades.remove("La Rioja");
        } else if (provincia.equals("Navarra")) {
            lista_comunidades.remove("Navarra");
        }
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista_comunidades);
        listaResultado.setAdapter(adaptador); // asociamos el adaptador a la lista

        // Al pulsar sobre algún item de la lista (sobre algún registro del diario) lo mostramos en detalle en otra actividad:
        listaResultado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> customerAdapter, View footer, int selectedInt, long selectedLong) {
                // String listChoice = (String) listaResultado.getItemAtPosition(selectedInt); // para mostrar la selección pulsada
                int id = (int) listaResultado.getItemIdAtPosition(selectedInt); // obtenemos el id del elemento del listado seleccionado
                // para saber qué id de día debemos obtener
                try {
                    comunidad_seleccionada = lista_comunidades.get(id);
                    System.out.println("COMUNIDAD SELECCIONADA: " + comunidad_seleccionada);
                    Intent intent = new Intent(ListadoComunidadesCalendario.this, WebView.class);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("DiarioFragment", "Error al obtener los datos del registro a visualizar en detalle");
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Al pulsar el icono de la flecha atrás de la barra de acciones
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
