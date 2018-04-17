package es.proyecto.eva.miagendadam.Fragments.Horas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.NavMenu;
import es.proyecto.eva.miagendadam.PantallaLogin;
import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

/***************************************************************************************************
 * Fragmento de la opción Horas que permite la visualización de las horas de prácticas que lleva
 * trabajadas el usuario activo, así como de las horas restantes para finalizar el módulo.
 **************************************************************************************************/
public class HorasFragment extends Fragment {
    TextView txtHorasTrabajadas, txtHorasModuloFCT, txtHorasRestantes, txtMinutosTrabajados, txtMinutosRestantes, txtM1, txtM2;
    private int horas_restantes = 0;
    private int minutos_restantes = 0;
    private String horas_fct = "";
    int horas_trabajadas = 0, minutos_trabajados = 0;
    String sHoras_trabajadas = "", sMinutos_trabajados = "";
    private StringRequest request;
    private String url_consulta = "http://miagendafp.000webhostapp.com/select_horas_minutos_trabajados.php";
    private String idUsuario = "";
    private JSONArray jsonArray;
    ProgressDialog progressDialog;

    public HorasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horas, container, false);
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        SharedPreferences preferences = getActivity().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario al que vamos a introducir el registro.
        horas_fct = preferences.getString("horas_fct", ""); // obtenemos horas del módulo fct del usuario
        txtHorasTrabajadas = (TextView) view.findViewById(R.id.txt_horas_trabajadas);
        txtHorasModuloFCT = (TextView) view.findViewById(R.id.txt_horas_modulo_fct);
        txtHorasRestantes = (TextView) view.findViewById(R.id.txt_horas_restantes);
        txtMinutosTrabajados = (TextView) view.findViewById(R.id.txt_minutos_trabajados);
        txtMinutosRestantes = (TextView) view.findViewById(R.id.txt_minutos_restantes);
        txtM1 = (TextView) view.findViewById(R.id.txt_min); // si no hay minutos, la letra m desaperecerá en ambos casos
        txtM2 = (TextView) view.findViewById(R.id.txt_min_2);
        txtHorasModuloFCT.setText(horas_fct);
        // Creamos la ventana de diálogo con círculo de carga para la espera de carga de los datos
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.dialog_cargando);
        progressDialog.setMessage("Obteniendo horas...");
        progressDialog.show();
        obtenerHorasTrabajadas();
        return view;
    }

    /***********************************************************************************************
     * Método que ejecuta una consulta que obtiene la suma de horas y minutos trabajadas del usuario
     * de sus registros de diario
     **********************************************************************************************/
    public void obtenerHorasTrabajadas(){
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            try {
                                jsonArray = new JSONArray(response);
                                // obtenemos el sumatorio de minutos y horas trabajadas
                                sHoras_trabajadas = jsonArray.getJSONObject(0).getString("sumaHoras");
                                sMinutos_trabajados = jsonArray.getJSONObject(0).getString("sumaMinutos");
                                // validamos si se han obtenido datos
                                if (sHoras_trabajadas.isEmpty() && sMinutos_trabajados.isEmpty()){
                                    progressDialog.cancel();
                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                            R.string.alert_horas_no_hay_registros, Snackbar.LENGTH_LONG).show();
                                } else{
                                    // pasamos los String a enteros para hacer las cuentas
                                    horas_trabajadas = Integer.valueOf(sHoras_trabajadas);
                                    minutos_trabajados = Integer.valueOf(sMinutos_trabajados);
                                    // comprobamos que los minutos obtenidos no pasen de 59. De ser así se deberá restar 60 e incrementar una hora hasta que la cifra de minutos
                                    // esté finalmente por debajo de 590
                                    while (minutos_trabajados > 59) {
                                        minutos_trabajados = minutos_trabajados - 60;
                                        horas_trabajadas++;
                                        System.out.println("SUMA HORA");
                                    }
                                    // comprobamos que los cálculos se han hecho bien
                                    System.out.println("HORAS TRABAJADAS: " + horas_trabajadas + "\n MINUTOS TRABAJADOS: " + minutos_trabajados);
                                    guardarPreferencias(); // guardamos el número de horas obtenidas
                                    // ponemos la cifra de las horas trabajadas obtenidas
                                    txtHorasTrabajadas.setText(String.valueOf(horas_trabajadas));
                                    // si hay minutos se ponen, si no se ocultan todos los elementos de los minutos (cifra y letra correspondiente)
                                    if (minutos_trabajados > 0) {
                                        txtMinutosTrabajados.setText(String.valueOf(minutos_trabajados));
                                    } else {
                                        txtMinutosTrabajados.setVisibility(View.GONE);
                                        txtM1.setVisibility(View.GONE);
                                    }
                                }
                                // A continuación obtenemos las horas restantes de trabajo
                                obtenerHorasRestantes();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                        //Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                        // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
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

    /***********************************************************************************************
     * Método que obtiene el tiempo restante de trabajo teniendo en cuenta las horas del módulo fct
     * y las horas que se han trabajado ya
     **********************************************************************************************/
    public void obtenerHorasRestantes(){
        // Solo seguiremos el siguiente procedimiento si hay minutos sueltos trabajados, puesto que si no será tan fácil como directamente restar horas con horas
        if (minutos_trabajados > 0) {
            // Para poder obtener una cifra operando con horas y minutos, lo que haremos será pasarlo todos a minutos primero, y luego ya pasar a horas:
            // Primero obtenemos el tiempo trabajado en total en minutos
            int horas_trabajadas_en_minutos = horas_trabajadas * 60; // primero pasamos las horas trabajadas a minutos
            System.out.println("HORAS TR. EN MINUTOS: " + horas_trabajadas_en_minutos);
            int tiempo_trabajado_en_minutos = horas_trabajadas_en_minutos + minutos_trabajados; // después sumamos las horas pasadas a minutos con los minutos, y obtenemos el tiempo total trabajado

            // A continuación pasamos las horas del módulo FCT a minutos
            int horas_fct_en_minutos = Integer.valueOf(horas_fct) * 60;
            System.out.println("HORAS FCT EN MINUTOS: " + horas_fct_en_minutos);
            // Después restamos el tiempo trabajado al tiempo del módulo fct
            int tiempo_restante_en_minutos = horas_fct_en_minutos - tiempo_trabajado_en_minutos;
            System.out.println("TIEMPO RESTANTE EN MINUTOS: " + tiempo_restante_en_minutos);
            // Por último pasaremos ese tiempo a horas y minutos
            while (tiempo_restante_en_minutos > 59) { // mientras los minutos pasen de 59, seguimos restando
                horas_restantes++; // incrementamos una hora por cada 60 minutos que restemos
                tiempo_restante_en_minutos = tiempo_restante_en_minutos - 60; // restamos los 60 minutos
                minutos_restantes = tiempo_restante_en_minutos; // igualamos los minutos restantes al tiempo restante en minutos por si quedan minutos sueltos que no pasen de 59 en el último ciclo del bucle
            }
            System.out.println("HORAS RESTANTES: " + horas_restantes);
            System.out.println("MINUTOS RESTANTES:  " + minutos_restantes);
        } else {
            horas_restantes = Integer.valueOf(horas_fct) - horas_trabajadas;
        }
        // Ponemos los valores en los campos, no sin antes cerrar el diálogo de carga
        progressDialog.cancel();
        txtHorasRestantes.setText(String.valueOf(horas_restantes)); // ponemos en el campo de horas restantes las horas obtenidas
        if (minutos_restantes > 0) { // si hay minutos restantes, los ponemos
            txtMinutosRestantes.setText(String.valueOf(minutos_restantes));
        } else { // si no, se oculta la letra m de los minutos
            txtM2.setVisibility(View.GONE);
        }
    }

    /***********************************************************************************************
     * Método que guarda en preferencias las horas trabajadas obtenidas de los registros.
     * Nos servirá para tener el dato guardado y detectar si se ha completado el cómputo
     * de horas del módulo, en cuyo caso no se permitirá la creación de más registros, ya
     * que se da por entendido que se ha superado el período de prácticas y ya no se debe
     * acudir más
     **********************************************************************************************/
    private void guardarPreferencias() {
        SharedPreferences preferences = getActivity().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("horas_trabajadas", String.valueOf(horas_trabajadas));
        editor.commit();
    }
}
