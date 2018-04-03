package es.proyecto.eva.miagendadam.Fragments.Contactos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

public class ContactosFragment extends Fragment {
    FloatingActionButton btnNuevo;
    ListView listaResultado;
    TextView txt;
    private String nombre_de_usuario = "";
    private String nombreContacto = "", correoContacto = "", modulo = "", telefono = "";
    private StringRequest request; // petición volley
    private JSONArray jsonArrayContactos; // array JSON con los contactos obtenidos de la base de datos
    private boolean hayRegistros;
    private String url_consulta = "http://miagendafp.000webhostapp.com/select_contactos.php";
    private ArrayList<String> lista;
    private ArrayAdapter<String> adaptador;
    // Variables de los datos del contacto selaccionado que serán visualizadas en la actividad de vista en detalle del contacto seleccionado
    // (Variables públicas estáticas para acceder a ellas a través de otras clases)
    public static String id_contacto_seleccionado = "", nombre_seleccionado_codificado = "", modulo_seleccionado_codificado = "", correo_seleccionado = "", telefono_seleccionado = "";
    private ProgressDialog progressDialog;
    public ContactosFragment() {
        // Required empty public constructor
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactos, container, false);
        // Obtenemos de las preferencias el nombre del usuario
        SharedPreferences preferences = this.getActivity().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        nombre_de_usuario = preferences.getString("nombre_de_usuario", "");
        listaResultado = (ListView) view.findViewById(R.id.lista);
        txt = (TextView) view.findViewById(R.id.txt_vacio);
        btnNuevo = (FloatingActionButton) view.findViewById(R.id.btn_nuevo_contacto);
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NuevoContacto.class);
                startActivity(intent);
            }
        });

        // Al pulsar sobre algún item de la lista (sobre algún contacto) lo mostramos en detalle en otra actividad:
        listaResultado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> customerAdapter, View footer, int selectedInt, long selectedLong) {
                // String listChoice = (String) listaResultado.getItemAtPosition(selectedInt); // para mostrar la selección pulsada
                int id = (int) listaResultado.getItemIdAtPosition(selectedInt); // obtenemos el id del elemento del listado seleccionado
                // para saber qué id de día debemos obtener
                try {
                    // obtenemos los datos del elemento seleccionado
                    id_contacto_seleccionado = jsonArrayContactos.getJSONObject(id).getString("idContacto");
                    String nombre_seleccionado = jsonArrayContactos.getJSONObject(id).getString("nombreContacto");
                    nombre_seleccionado_codificado = codificaString(nombre_seleccionado);
                    String modulo_seleccionado = jsonArrayContactos.getJSONObject(id).getString("modulo");
                    modulo_seleccionado_codificado = codificaString(modulo_seleccionado);
                    correo_seleccionado = jsonArrayContactos.getJSONObject(id).getString("correoContacto");
                    telefono_seleccionado = jsonArrayContactos.getJSONObject(id).getString("telefono");
                    // después de obtener los datos abrimos la nueva actividad que nos permitirá visualizarlos
                    // y editarlos en sus correspondientes campos
                    Log.d("DiarioFragment", "Vista detalle de un registro");
                    Intent intent = new Intent(getActivity(), VerContacto.class);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                    Log.e("ContactosFragment", "Error al obtener los datos del contacto a visualizar en detalle");
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ContactosFragment", "Fragment reanudado");
        // Creamos la ventana de diálogo con círculo de carga para la espera de carga de los datos
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.dialog_cargando);
        progressDialog.setMessage("Obteniendo contactos...");
        progressDialog.show();
        obtenerContactos();
    }

    /***********************************************************************************************
     * Método que obtiene automáticamente los contactos que el usuario haya creado
     **********************************************************************************************/
    public void obtenerContactos() {
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!nombre_de_usuario.isEmpty()) { // aseguramos que las preferencias no están vacías
                            if (response.equals("0")) { // Respuesta 0 = El usuario no tiene contactos creados
                                txt.setText(R.string.texto_contactos_vacio);
                                hayRegistros = false;
                                if (!hayRegistros) {
                                    System.out.println("NO HAY CONTACTOS");
                                }
                            } else { // El usuario tiene contactos
                                try {
                                    hayRegistros = true;
                                    txt.setText(""); // ponemos el texto de que no hay contactos en blanco por si acaso, y obtenemos datos
                                    response = response.replace("][", ","); // SUSTITUIMOS LOS CARACTERES QUE SEPARAN CADA RESULTADO DEL ARRAY
                                    // PORQUE SI NO NOS TOMARÍA SOLO EL PRIMER ARRAY. DE ESTA MANERA HACEMOS QUE LOS DETECTE COMO OBJETOS (EN VEZ DE COMO ARRAYS DIFERENTES)
                                    // DENTRO DE UN ÚNICO ARRAY
                                    // YA QUE LOS ARRAYS TIENEN FORMATO [{...}][{...}], ... CON LO QUE, SI OBTIENE ASÍ LOS RESULTADOS, SOLO VA A COGER EL PRIMERO
                                    // Y UN ARRAY DE OBJETOS TENDRÍA ESTE OTRO FORMATO [{...}, {...}, {...}] DONDE LOS CORCHETES DETERMINAN EL ARRAY, Y LAS LLAVES LOS OBJETOS.
                                    jsonArrayContactos = new JSONArray(response); // guardamos los registros en el array
                                    cargarContactos(); // cargamos en pantalla los registros
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
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_de_usuario); // pasamos el nombre de usuario como parámetro de la consulta para obtener sus registros del diario
                return parametros;
            }

        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /*********************************************************************************************************
     * Método que carga los registros de diario del usuario activo obtenidos en un ListView personalizado
     ********************************************************************************************************/
    private void cargarContactos() {
        ArrayList<String> lista = new ArrayList<>(); // al declararlo nuevamente lo estaremos vaciando, para no duplicar cada vez que se obtengan
        adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lista);
        for (int i = 0; i < jsonArrayContactos.length(); i++){ // hasta que se hayan obtenido todos los contactos:
            try {
                nombreContacto = jsonArrayContactos.getJSONObject(i).getString("nombreContacto"); // obtenemos nombre
                // Codificamos los datos de la descripción para visualizar sus tildes y otros caracteres
                String nombreCodificado = codificaString(nombreContacto);
                lista.add(nombreCodificado); // añadimos cada nombre obtenido al array de la lista
            } catch (Exception e){ // Error al intentar obtener los datos
                e.printStackTrace();
            }
        }
        System.out.println("CONTACTOS OBTENIDOS: " + lista.size());
        progressDialog.cancel();
        listaResultado.setAdapter(adaptador); // asociamos el adaptador a la lista
    }
}
