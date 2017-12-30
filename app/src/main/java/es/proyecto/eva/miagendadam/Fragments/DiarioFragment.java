package es.proyecto.eva.miagendadam.Fragments;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.Acciones.NuevoRegistroDiario;
import es.proyecto.eva.miagendadam.Acciones.VerYEditarRegistroDiario;
import es.proyecto.eva.miagendadam.NavMenu;
import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

import static es.proyecto.eva.miagendadam.NavMenu.nombre_de_usuario;

public class DiarioFragment extends Fragment {
    FloatingActionButton btnNuevo;
    ListView listaResultado;
    TextView txt;
    static String idDia;
    static String fecha;
    static String horas;
    static String minutos;
    static String descripcion;
    static String valoracion;
    public static String id_dia_seleccionado;
    public static String fecha_seleccionada;
    public static String horas_seleccionadas;
    public static String minutos_seleccionados;
    public static String descripcion_seleccionada;
    public static String valoracion_seleccionada;
    public boolean diarioVacio = false;
    private ArrayList<String> lista = new ArrayList<>();
    private ArrayAdapter<String> adaptador;
    private StringRequest request;
    public static JSONArray jsonArrayDiario;
    private String  url_consulta = "http://192.168.0.12/MiAgenda/select_dias.php";

    public DiarioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ON CREATE FRAGMENT");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("ON CREATE VIEW FRAGMENT");
        View view = inflater.inflate(R.layout.fragment_diario, container, false);
        listaResultado = (ListView) view.findViewById(R.id.lista);
        txt = (TextView) view.findViewById(R.id.txt_vacio);
        if (diarioVacio){
            txt.setText(R.string.texto_diario_vacio);
            System.out.println("PONEMOS TEXTO DE VACÍO");
            System.out.println("DIARIO VACÍO = "+diarioVacio);
            diarioVacio = false;
        } else {
            obtenerRegistrosDiario();
        }
        // Al pulsar en el botón de nuevo (+) procedemos a crear un nuevo registro
        btnNuevo = (FloatingActionButton) view.findViewById(R.id.btn_nuevo_registro);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NuevoRegistroDiario.class);
                startActivity(intent);
            }
        });

        // Al pulsar sobre algún item de la lista (sobre algún registro del diario):
        listaResultado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> customerAdapter, View footer, int selectedInt, long selectedLong) {
               // String listChoice = (String) listaResultado.getItemAtPosition(selectedInt); // para mostrar la selección pulsada
                int id = (int) listaResultado.getItemIdAtPosition(selectedInt); // obtenemos el id del elemento del listado seleccionado
                // para saber qué id de día debemos obtener
                try {
                    // obtenemos los datos del elemento seleccionado
                    id_dia_seleccionado = jsonArrayDiario.getJSONObject(id).getString("idDia");
                    fecha_seleccionada = jsonArrayDiario.getJSONObject(id).getString("fecha");
                    horas_seleccionadas = jsonArrayDiario.getJSONObject(id).getString("horas");
                    minutos_seleccionados = jsonArrayDiario.getJSONObject(id).getString("minutos");
                    descripcion_seleccionada = jsonArrayDiario.getJSONObject(id).getString("descripcion");
                    valoracion_seleccionada = jsonArrayDiario.getJSONObject(id).getString("valoracion");

                    // después de obtener los datos abrimos la nueva actividad que nos permitirá visualizarlos
                    // y editarlos en sus correspondientes campos
                    Intent intent = new Intent (getActivity(), VerYEditarRegistroDiario.class);
                    startActivity(intent);

                    System.out.println("ID DEL DIA = "+id_dia_seleccionado + "\nFecha del día = "+fecha_seleccionada
                            + "\nHora del día = "+horas_seleccionadas+"\nMinutos = "+minutos_seleccionados+"\nDescripción = "+
                            descripcion_seleccionada + "\nValoración = "+valoracion_seleccionada);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    /***********************************************************************************************
     * Método que obtiene los registros del usuario de la opción "Diario"
     **********************************************************************************************/
    public void obtenerRegistrosDiario(){
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!nombre_de_usuario.isEmpty()) { // aseguramos que las preferencias no están vacías
                            if (response.equals("0")){ // Respuesta 0 = El usuario no tiene registros en el diario
                                diarioVacio = true; // luego el diario está vacío
                                System.out.println("EL USUARIO NO TIENE REGISTROS DE DIARIO");
                                System.out.println("DIARIO VACÍO = "+ diarioVacio);
                            } else {
                                try {
                                    response = response.replace("][",","); // SUSTITUIMOS LOS CARACTERES QUE SEPARAN CADA RESULTADO DEL ARRAY
                                    // PORQUE SI NO NOS TOMARÍA SOLO EL PRIMER ARRAY. DE ESTA MANERA HACEMOS QUE LOS DETECTE COMO OBJETOS (EN VEZ DE COMO ARRAYS DIFERENTES)
                                    // DENTRO DE UN ÚNICO ARRAY
                                    // YA QUE LOS ARRAYS TIENEN FORMATO [{...}][{...}], ... CON LO QUE, SI OBTIENE ASÍ LOS RESULTADOS, SOLO VA A COGER EL PRIMERO
                                    // Y UN ARRAY DE OBJETOS TENDRÍA ESTE OTRO FORMATO [{...}, {...}, {...}] DONDE LOS CORCHETES DETERMINAN EL ARRAY, Y LAS LLAVES LOS OBJETOS.

                                    jsonArrayDiario = new JSONArray(response); // guardamos los registros en el array
                                    cargarRegistros(); // cargamos en pantalla los registros
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else { // si no hay preferencias, es decir, no hay datos del usuario (cosa improbable), notificamos
                            Toast.makeText(getActivity(), "No se pudo obtener el nombre de usuario.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(getActivity(), "Error de conexión.", Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_de_usuario); // pasamos el nombre de usuario como parámetro de la consulta para obtener sus registros del diario
                return parametros;
            }

        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /*********************************************************************************************************
     * Método que carga los registros de diario del usuario activo obtenidos en un ListView
     ********************************************************************************************************/
    private void cargarRegistros() {
        adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lista);
        for (int i = 0; i < jsonArrayDiario.length(); i++) {
            try {
                idDia = jsonArrayDiario.getJSONObject(i).getString("idDia");
                fecha = jsonArrayDiario.getJSONObject(i).getString("fecha");
                horas = jsonArrayDiario.getJSONObject(i).getString("horas");
                minutos = jsonArrayDiario.getJSONObject(i).getString("minutos");
                descripcion = jsonArrayDiario.getJSONObject(i).getString("descripcion");
                valoracion = jsonArrayDiario.getJSONObject(i).getString("valoracion");

//                if (valoracion.equals("Bueno")){
//                    listaResultado.setBackgroundColor(Color.GREEN);
//                } else if (valoracion.equals("Regular")){
//                    listaResultado.setBackgroundColor(Color.YELLOW);
//                } else if (valoracion.equals("Malo")){
//                    listaResultado.setBackgroundColor(Color.RED);
//                }
                // verde A629AE00
                // rojo A2E70B0B
                // amarillo CEFFBA19

                // Validamos si hay minutos. Si están a 0 los obviamos y solo mostramos las horas
                if (Integer.valueOf(minutos) > 0){
                    lista.add(fecha + "\nTiempo: " + horas + " horas y " + minutos + " minutos");
                } else if (Integer.valueOf(minutos) < 1){
                    lista.add(fecha + "\nTiempo: " + horas + " horas");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        listaResultado.setAdapter(adaptador);
    }
}