package es.proyecto.eva.miagendadam.Fragments.Notas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

public class NotasFragment extends Fragment {
    FloatingActionButton btnNuevo;
    ListView listaResultado;
    TextView txt;
    // Datos obtenidos para mostrar en el listado
    private String contenido_nota, dia_edicion_nota, mes_edicion_nota, anyo_edicion_nota, fecha_edicion_nota, color_nota;
    private String idUsuario = "";

    // Datos del item seleccionado que se van a mostrar al pulsar sobre una nota para verla en detalle
    public static String id_nota_seleccionada, contenido_seleccionado, fecha_creacion_seleccionada, fecha_edicion_seleccionada,
            dia_edicion_seleccionado, mes_edicion_seleccionado, anyo_edicion_seleccionado, color_seleccionado, color_action_bar_seleccionado;

    // Petición para volley
    private StringRequest request;

    // Array con las notas obtenidas
    public static JSONArray jsonArrayNotas;

    // Arrays con los datos de las notas
    ArrayList<String> arrayContenidoNota = new ArrayList<>(); // array en el que introduciremos los contenidos obtenidos de las notas
    ArrayList <String> arrayFechaEdicionNota = new ArrayList<>(); // array en el que introduciremos las fechas de última edición de las notas
    ArrayList <String> arrayColorNota = new ArrayList<>(); // array en el que introduciremos los colores de las notas

    // Adaptador personalizado para las notas
    AdaptadorListaNotas adaptador; // objeto de la clase AdaptadorListaNotas que utilizaremos como adaptador personalizado para el aspecto de la lista de notas


    // Consultas para mostrado y borrado de datos
    private String url_consulta = "http://miagendafp.000webhostapp.com/select_notas.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/delete_all_notas.php";

    private boolean hayNotas;
    ProgressDialog progressDialog;

    // Para codificar caracteres especiales
    private String codificaString(String dato){
        String datoCodificado = "";
        try {
            byte[] arrByteNombre = dato.getBytes("ISO-8859-1");
            datoCodificado = new String(arrByteNombre);
        } catch (Exception e){
            e.printStackTrace();
        }
        return datoCodificado;
    }

    public NotasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();
        obtenerNotas();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // para visualizar el menú en el action bar
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notas, container, false);
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        SharedPreferences preferences = getActivity().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario
        listaResultado = (ListView) view.findViewById(R.id.lista);
        txt = (TextView) view.findViewById(R.id.txt_vacio);
        btnNuevo = (FloatingActionButton) view.findViewById(R.id.btn_nueva_nota);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.dialog_cargando);
        progressDialog.setMessage("Obteniendo notas...");
        progressDialog.show();

        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NuevaNota.class);
                startActivity(intent);
            }
        });

        // Al pulsar sobre algún item de la lista (sobre algún registro del diario) lo mostramos en detalle en otra actividad:
        listaResultado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> customerAdapter, View footer, int selectedInt, long selectedLong) {
                // String listChoice = (String) listaResultado.getItemAtPosition(selectedInt); // para mostrar la selección pulsada
                int id = (int) listaResultado.getItemIdAtPosition(selectedInt); // obtenemos el id del elemento del listado seleccionado
                // para saber qué id de día debemos obtener
                try {
                    // obtenemos los datos del elemento seleccionado
                    id_nota_seleccionada = jsonArrayNotas.getJSONObject(id).getString("idNota");
                    fecha_creacion_seleccionada = jsonArrayNotas.getJSONObject(id).getString("fechaCreacion");
                    dia_edicion_seleccionado = jsonArrayNotas.getJSONObject(id).getString("diaEdicion");
                    mes_edicion_seleccionado = jsonArrayNotas.getJSONObject(id).getString("mesEdicion");
                    anyo_edicion_seleccionado = jsonArrayNotas.getJSONObject(id).getString("anyoEdicion");
                    contenido_seleccionado = codificaString(jsonArrayNotas.getJSONObject(id).getString("contenidoNota"));
                    color_seleccionado = jsonArrayNotas.getJSONObject(id).getString("colorNota");
                    color_action_bar_seleccionado =  jsonArrayNotas.getJSONObject(id).getString("colorActionBar");
                    Log.d("AnotacionesFr", "Vista detalle de una nota");
                    Intent intent = new Intent(getActivity(), VerEditarNota.class);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("AnotacionesFr", "Error al obtener los datos de la nota a visualizar en detalle");
                }
            }
        });

        return view;
    }

    /***********************************************************************************************
     * Método que obtiene automáticamente los contactos que el usuario haya creado
     **********************************************************************************************/
    public void obtenerNotas() {
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!idUsuario.isEmpty()) { // aseguramos que las preferencias no están vacías
                            if (response.equals("0")) { // Respuesta 0 = El usuario no tiene contactos creados
                                txt.setText(R.string.texto_notas_vacio);
                                progressDialog.cancel();
                                hayNotas = false;
                                if (!hayNotas) {
                                    System.out.println("NO HAY CONTACTOS");
                                }
                            } else { // El usuario tiene contactos
                                try {
                                    hayNotas = true;
                                    txt.setText(""); // ponemos el texto de que no hay contactos en blanco por si acaso, y obtenemos datos
                                    response = response.replace("][", ","); // SUSTITUIMOS LOS CARACTERES QUE SEPARAN CADA RESULTADO DEL ARRAY
                                    // PORQUE SI NO NOS TOMARÍA SOLO EL PRIMER ARRAY. DE ESTA MANERA HACEMOS QUE LOS DETECTE COMO OBJETOS (EN VEZ DE COMO ARRAYS DIFERENTES)
                                    // DENTRO DE UN ÚNICO ARRAY
                                    // YA QUE LOS ARRAYS TIENEN FORMATO [{...}][{...}], ... CON LO QUE, SI OBTIENE ASÍ LOS RESULTADOS, SOLO VA A COGER EL PRIMERO
                                    // Y UN ARRAY DE OBJETOS TENDRÍA ESTE OTRO FORMATO [{...}, {...}, {...}] DONDE LOS CORCHETES DETERMINAN EL ARRAY, Y LAS LLAVES LOS OBJETOS.
                                    jsonArrayNotas = new JSONArray(response); // guardamos las notas en el array
                                    cargarNotas(); // cargamos en pantalla las notas
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else { // si no hay preferencias, es decir, no hay datos del usuario (cosa improbable), notificamos
                            //Toast.makeText(getActivity(), R.string.error_no_hay_usuario, Toast.LENGTH_SHORT).show();
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    R.string.error_no_hay_usuario, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                        //Toast.makeText(getActivity(), R.string.error_servidor, Toast.LENGTH_LONG).show();
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idUsuario", idUsuario); // pasamos el nombre de usuario como parámetro de la consulta para obtener sus registros del diario
                return parametros;
            }

        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /*********************************************************************************************************
     * Método que carga las notas del usuario activo obtenidas en un ListView personalizado
     ********************************************************************************************************/
    private void cargarNotas() {
        arrayContenidoNota = new ArrayList<>();
        arrayFechaEdicionNota = new ArrayList<>();
        arrayColorNota = new ArrayList<>();
        Calendar c = new GregorianCalendar();
        int anyo_actual = c.get(Calendar.YEAR);
        for (int i = 0; i < jsonArrayNotas.length(); i++){ // hasta que se hayan obtenido todos los contactos:
            try {
                // obtenemos todos los datos y los vamos guardando en su correspondiente array
                contenido_nota = codificaString(jsonArrayNotas.getJSONObject(i).getString("contenidoNota")); // obtenemos contenido de la nota
                // añadimos validación de longitud de contenido:
                if (contenido_nota.length() > 20) { // si el contenido obtenido supera los 20 caracteres
                    // guardamos solo los 20 primeros caracteres y concatenamos puntos suspensivos para indicar que la nota continúa
                    String contenido_nota_abreviado = contenido_nota.substring(0, 20);
                    contenido_nota_abreviado = contenido_nota_abreviado + "...";
                    arrayContenidoNota.add(contenido_nota_abreviado); // añadimos cada contenido abreviado de nota obtenido al array de la lista
                } else { // si la longitud no es superior a 20 caracteres, no modificamos nada y simplemente lo añadimos
                    arrayContenidoNota.add(contenido_nota); // añadimos cada contenido de nota obtenido al array de la lista
                }
                dia_edicion_nota = jsonArrayNotas.getJSONObject(i).getString("diaEdicion");
                mes_edicion_nota = jsonArrayNotas.getJSONObject(i).getString("mesEdicion");
                String sMes = "";
                if (mes_edicion_nota.equals("1")){
                    sMes = "ene.";
                } else if (mes_edicion_nota.equals("2")){
                    sMes = "feb.";
                } else if (mes_edicion_nota.equals("3")){
                    sMes = "mar.";
                } else if (mes_edicion_nota.equals("4")){
                    sMes = "abr.";
                } else if (mes_edicion_nota.equals("5")){
                    sMes = "may.";
                } else if (mes_edicion_nota.equals("6")){
                    sMes = "jun.";
                } else if (mes_edicion_nota.equals("7")){
                    sMes = "jul.";
                } else if (mes_edicion_nota.equals("8")){
                    sMes = "ago.";
                } else if (mes_edicion_nota.equals("9")){
                    sMes = "sep.";
                } else if (mes_edicion_nota.equals("10")){
                    sMes = "oct.";
                } else if (mes_edicion_nota.equals("11")){
                    sMes = "nov.";
                } else if (mes_edicion_nota.equals("12")){
                    sMes = "dic.";
                }
                anyo_edicion_nota = jsonArrayNotas.getJSONObject(i).getString("anyoEdicion");
                if (Integer.valueOf(anyo_edicion_nota) == anyo_actual){ // si el año de última edición de la nota es el mismo que el año actual, no lo indicamos,
                    // puesto que podemos obviarlo al ser en el año presente
                    fecha_edicion_nota = dia_edicion_nota + " " + sMes;
                } else { // si no es igual, lo ponemos para que se vea que no es del año presente
                    fecha_edicion_nota = dia_edicion_nota + " " + sMes + " " + anyo_edicion_nota;
                }
                arrayFechaEdicionNota.add(fecha_edicion_nota);
                color_nota = jsonArrayNotas.getJSONObject(i).getString("colorNota");
                arrayColorNota.add(color_nota);
            } catch (Exception e){ // Error al intentar obtener los datos
                e.printStackTrace();
            }
        }
        // creamos el adaptador personalizado
        adaptador = new AdaptadorListaNotas(getActivity(), arrayContenidoNota, arrayFechaEdicionNota, arrayColorNota);
        progressDialog.cancel(); // cerramos el cuadro de carga cuando ya se hayan puesto los datos obtenidos en pantalla
        listaResultado.setAdapter(adaptador); // asociamos el adaptador a la lista
    }

    // Creamos el menú en el action bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notas_fragment, menu);
    }

    // Selección de opciones del menú del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_borrar_todo: // Opción de guardar los datos de usuario actualizados
                Log.i("DiarioFragment", "Action Borrar todo");
                // Preguntamos antes de proceder con el borrado de datos
                if (!hayNotas){
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.no_hay_notas, Snackbar.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.alert_borrar_todo_title); // titulo del diálogo
                    builder.setMessage(R.string.alert_borrar_notas)
                            .setPositiveButton(R.string.alert_borrar_todo_btn_borrar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    borrarNotas();
                                }
                            })
                            .setNegativeButton(R.string.respuesta_dialog_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // dejamos en blanco, no se hace nada, solo cierra el diálogo
                                }
                            });

                    Dialog dialog = builder.create();
                    dialog.show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**************************************************************************************************
     * Método que elimina TODAS las anotaciones del usuario, solicitando confirmación previa
     *************************************************************************************************/
    public void borrarNotas(){
        request = new StringRequest(Request.Method.POST, url_consulta2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // notificamos del borrado
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.notas_borradas, Snackbar.LENGTH_LONG).show();
                        // ponemos adaptador a nulo para que no aparezcan las que había creadas previamente en pantalla
                        listaResultado.setAdapter(null); // vaciamos la lista para no ver los registros
                        // ponemos el texto de que no hay notas
                        txt.setText(R.string.texto_notas_vacio);
                        hayNotas = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getActivity(), R.string.error_servidor, Toast.LENGTH_LONG).show();
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.e("DiarioFragment", "Error al realizar la conexión con el servidor al borrar todos los registros del usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idUsuario", idUsuario);
                return parametros;
            }

        };
        AppController.getInstance().addToRequestQueue(request);
    }
}

