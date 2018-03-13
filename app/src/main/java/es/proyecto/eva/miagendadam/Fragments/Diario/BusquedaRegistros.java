package es.proyecto.eva.miagendadam.Fragments.Diario;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

public class BusquedaRegistros extends AppCompatActivity {
    EditText txtHorasExactas, txtRangoHoras1, txtRangoHoras2, txtHoraInicioJornada, txtHoraFinJornada, txtFechaExacta, txtRangoFechas1, txtRangoFechas2;
    Spinner spinnerMeses, spinnerValoraciones;
    ImageButton btnBuscarHorasExactas, btnBuscarRangoHoras, btnBuscarInicioJornada, btnBuscarFinJornada, btnBuscarJornadaPartida, btnBuscarFechaExacta, btnBuscarRangoFechas, btnBuscarMes, btnBuscarValoracion;
    private String horas_exactas = "", horas_1 = "", horas_2 = "", hora_inicio = "", hora_fin = "", minuto_inicio = "", minuto_fin = "", sDia = "", sMes = "", sAnyo = "", fecha_1 = "", fecha_2 = "";
    private String valoracion = "", mes = "";
    private boolean esFechaExacta = false, esFechaRango1 = false, esFechaRango2 = false;
    private String idUsuario = "";
    private StringRequest request;
    private String url_consulta = "http://miagendafp.000webhostapp.com/busqueda_horas_exactas.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/busqueda_rango_horas.php";
    private String url_consulta3 = "http://miagendafp.000webhostapp.com/busqueda_hora_inicio_jornada.php";
    private String url_consulta4 = "http://miagendafp.000webhostapp.com/busqueda_hora_fin_jornada.php";
    private String url_consulta5 = "http://miagendafp.000webhostapp.com/busqueda_registros_jornada_partida.php";
    private String url_consulta6 = "http://miagendafp.000webhostapp.com/busqueda_registros_fecha_exacta.php";
    private String url_consulta7 = "http://miagendafp.000webhostapp.com/busqueda_registros_rango_fechas.php";
    private String url_consulta8 = "http://miagendafp.000webhostapp.com/busqueda_registros_mes.php";
    private String url_consulta9 = "http://miagendafp.000webhostapp.com/busqueda_registros_valoracion.php";
    public static JSONArray jsonArray;
    private boolean esHoraInicio = false, esHoraFin = false;
    // Creamos variable de horas y minutos que serán las que usemos para obtener la hora seleccionada en el timepicker
    int tpHoras, tpMinutos;
    // Creamos variables para almacenar las distintas horas introducidas
    int horaInicio = 0, minutoInicio = 0, horaFin = 0, minutoFin = 0;

    // Array de meses
    private String[] meses = {"Selecciona un mes", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
            "Octubre", "Noviembre", "Diciembre" };

    // Array de valoraciones
    private String[] valoraciones = {"Selecciona una valoración", "Bueno", "Regular", "Malo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_registros);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario al que vamos a introducir el registro.
        txtHorasExactas = (EditText) findViewById(R.id.txt_horas_exactas);
        txtRangoHoras1 = (EditText) findViewById(R.id.txt_rango_horas_1);
        txtRangoHoras2 = (EditText) findViewById(R.id.txt_rango_horas_2);
        txtHoraInicioJornada = (EditText) findViewById(R.id.txt_hora_inicio_jornada);
        txtHoraFinJornada = (EditText) findViewById(R.id.txt_hora_fin_jornada);
        txtFechaExacta = (EditText) findViewById(R.id.txt_fecha);
        txtRangoFechas1 = (EditText) findViewById(R.id.txt_fecha_1);
        txtRangoFechas2 = (EditText) findViewById(R.id.txt_fecha_2);
        spinnerMeses = (Spinner) findViewById(R.id.spinner_meses);
        spinnerMeses.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, meses));
        spinnerValoraciones = (Spinner) findViewById(R.id.spinner_valoraciones);
        spinnerValoraciones.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valoraciones));
        btnBuscarHorasExactas = (ImageButton) findViewById(R.id.btn_buscar_horas_exactas);
        btnBuscarRangoHoras = (ImageButton) findViewById(R.id.btn_buscar_rango_horas);
        btnBuscarInicioJornada = (ImageButton) findViewById(R.id.btn_buscar_inicio_jornada);
        btnBuscarFinJornada = (ImageButton) findViewById(R.id.btn_buscar_fin_jornada);
        btnBuscarJornadaPartida = (ImageButton) findViewById(R.id.btn_buscar_jornada_partida);
        btnBuscarFechaExacta = (ImageButton) findViewById(R.id.btn_buscar_fecha);
        btnBuscarRangoFechas = (ImageButton) findViewById(R.id.btn_buscar_rango_fechas);
        btnBuscarMes = (ImageButton) findViewById(R.id.btn_buscar_mes);
        btnBuscarValoracion = (ImageButton) findViewById(R.id.btn_buscar_valoraciones);

        // controlamos la selección del spinner y lo añadimos al String valoracion
        spinnerValoraciones.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        valoracion = valoraciones[(position)];
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        // controlamos la selección del spinner y lo añadimos al String mes
        spinnerMeses.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        mes = meses[(position)];
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

        btnBuscarHorasExactas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                horas_exactas = txtHorasExactas.getText().toString();
                if (horas_exactas.isEmpty()){
                    Toast.makeText(BusquedaRegistros.this, R.string.error_horas_exactas, Toast.LENGTH_SHORT).show();
                } else {
                    // consulta
                    request = new StringRequest(Request.Method.POST, url_consulta,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("0")){ // 0 significa que no se obtienen registros
                                        Toast.makeText(BusquedaRegistros.this, R.string.error_no_hay_resultados, Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            response = response.replace("][", ",");
                                            jsonArray = new JSONArray(response);
                                            Intent intent = new Intent(BusquedaRegistros.this, ResultadosBusqueda.class);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                    //Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(android.R.id.content),
                                            R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                                    // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                            Map<String, String> parametros = new HashMap<>();
                            parametros.put("horas", horas_exactas);
                            parametros.put("idUsuario", idUsuario);
                            return parametros;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(request);
                }
            }
        });

        btnBuscarRangoHoras.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                horas_1 = txtRangoHoras1.getText().toString();
                horas_2 = txtRangoHoras2.getText().toString();
                if (horas_1.isEmpty() || horas_2.isEmpty()){
                    Toast.makeText(BusquedaRegistros.this, R.string.error_rango_horas, Toast.LENGTH_SHORT).show();
                } else {
                    // consulta
                    request = new StringRequest(Request.Method.POST, url_consulta2,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("0")){ // 0 significa que no se obtienen registros
                                        Toast.makeText(BusquedaRegistros.this, R.string.error_no_hay_resultados, Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            response = response.replace("][", ",");
                                            jsonArray = new JSONArray(response);
                                            Intent intent = new Intent(BusquedaRegistros.this, ResultadosBusqueda.class);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                    //Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(android.R.id.content),
                                            R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                                    // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                            Map<String, String> parametros = new HashMap<>();
                            parametros.put("hora_1", horas_1);
                            parametros.put("hora_2", horas_2);
                            parametros.put("idUsuario", idUsuario);
                            return parametros;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(request);
                }
            }
        });

        txtHoraInicioJornada.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    esHoraInicio = true;
                    abrirTimePicker();
                }
                return true;
            }
        });
        btnBuscarInicioJornada.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (hora_inicio.isEmpty()){
                    Toast.makeText(BusquedaRegistros.this, R.string.error_hora_inicio_jornada, Toast.LENGTH_SHORT).show();
                } else {
                    // consulta
                    request = new StringRequest(Request.Method.POST, url_consulta3,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("0")){ // 0 significa que no se obtienen registros
                                        Toast.makeText(BusquedaRegistros.this, R.string.error_no_hay_resultados, Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            response = response.replace("][", ",");
                                            jsonArray = new JSONArray(response);
                                            Intent intent = new Intent(BusquedaRegistros.this, ResultadosBusqueda.class);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                    //Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(android.R.id.content),
                                            R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                                    // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                            Map<String, String> parametros = new HashMap<>();
                            parametros.put("hora_inicio", hora_inicio);
                            parametros.put("minuto_inicio", minuto_inicio);
                            parametros.put("idUsuario", idUsuario);
                            return parametros;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(request);
                }
            }
        });

        txtHoraFinJornada.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    esHoraFin = true;
                    abrirTimePicker();
                }
                return true;
            }
        });
        btnBuscarFinJornada.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (hora_fin.isEmpty()){
                    Toast.makeText(BusquedaRegistros.this, R.string.error_hora_fin_jornada, Toast.LENGTH_SHORT).show();
                } else {
                    // consulta
                    request = new StringRequest(Request.Method.POST, url_consulta4,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("0")){ // 0 significa que no se obtienen registros
                                        Toast.makeText(BusquedaRegistros.this, R.string.error_no_hay_resultados, Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            response = response.replace("][", ",");
                                            jsonArray = new JSONArray(response);
                                            Intent intent = new Intent(BusquedaRegistros.this, ResultadosBusqueda.class);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                    //Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(android.R.id.content),
                                            R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                                    // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                            Map<String, String> parametros = new HashMap<>();
                            parametros.put("hora_fin", hora_fin);
                            parametros.put("minuto_fin", minuto_fin);
                            parametros.put("idUsuario", idUsuario);
                            return parametros;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(request);
                }
            }
        });

        btnBuscarJornadaPartida.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // consulta, no hay que pedir datos de nada, solo recoger los resultados de la consulta
                request = new StringRequest(Request.Method.POST, url_consulta5,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("0")){ // 0 significa que no se obtienen registros
                                    Toast.makeText(BusquedaRegistros.this, R.string.error_no_hay_resultados, Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        response = response.replace("][", ",");
                                        jsonArray = new JSONArray(response);
                                        Intent intent = new Intent(BusquedaRegistros.this, ResultadosBusqueda.class);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                //Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                Snackbar.make(findViewById(android.R.id.content),
                                        R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                                // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("idUsuario", idUsuario);
                        return parametros;
                    }
                };
                AppController.getInstance().addToRequestQueue(request);
            }
        });

        // Al pulsar sobre el campo de texto de la fecha exacta, se abre el datePicker para seleccionar la fecha
        txtFechaExacta.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    esFechaExacta = true;
                    abrirDatePicker();
                }
                return true;
            }
        });

        btnBuscarFechaExacta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               if (sDia.isEmpty() || sMes.isEmpty() || sAnyo.isEmpty()){
                   Toast.makeText(BusquedaRegistros.this, R.string.error_fecha_exacta, Toast.LENGTH_SHORT).show();
               } else {
                   // consulta, se pasan por parámetro las variables sDia, sMes y sAnyo
                   request = new StringRequest(Request.Method.POST, url_consulta6,
                           new Response.Listener<String>() {
                               @Override
                               public void onResponse(String response) {
                                   if (response.equals("0")){ // 0 significa que no se obtienen registros
                                       Toast.makeText(BusquedaRegistros.this, R.string.error_no_hay_resultados, Toast.LENGTH_SHORT).show();
                                   } else {
                                       try {
                                           response = response.replace("][", ",");
                                           jsonArray = new JSONArray(response);
                                           Intent intent = new Intent(BusquedaRegistros.this, ResultadosBusqueda.class);
                                           startActivity(intent);
                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                   }
                               }
                           },
                           new Response.ErrorListener() {
                               @Override
                               public void onErrorResponse(VolleyError error) {
                                   // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                   //Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                   Snackbar.make(findViewById(android.R.id.content),
                                           R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                                   // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
                               }
                           }) {
                       @Override
                       protected Map<String, String> getParams() throws AuthFailureError {
                           // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                           Map<String, String> parametros = new HashMap<>();
                           parametros.put("dia", sDia);
                           parametros.put("mes", sMes);
                           parametros.put("anyo", sAnyo);
                           parametros.put("idUsuario", idUsuario);
                           return parametros;
                       }
                   };
                   AppController.getInstance().addToRequestQueue(request);
               }
            }
        });

        txtRangoFechas1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    esFechaRango1 = true;
                    abrirDatePicker();
                }
                return true;
            }
        });

        txtRangoFechas2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    esFechaRango2 = true;
                    abrirDatePicker();
                }
                return true;
            }
        });

        btnBuscarRangoFechas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fecha_1.isEmpty() || fecha_2.isEmpty()){
                    Toast.makeText(BusquedaRegistros.this, R.string.error_rango_fechas, Toast.LENGTH_SHORT).show();
                } else {
                    // consulta
                    request = new StringRequest(Request.Method.POST, url_consulta7,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("0")){ // 0 significa que no se obtienen registros
                                        Toast.makeText(BusquedaRegistros.this, R.string.error_no_hay_resultados, Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            response = response.replace("][", ",");
                                            jsonArray = new JSONArray(response);
                                            System.out.println("FECHA 1: " + fecha_1);
                                            System.out.println("FECHA 2: " + fecha_2);
                                            System.out.println("ID USUARIO: " + idUsuario);
                                            System.out.println("LONGITUD ARRAY: "+ jsonArray.length());
                                            for (int i = 0; i < jsonArray.length(); i++){
                                                System.out.println(jsonArray.get(i));
                                            }
                                            Intent intent = new Intent(BusquedaRegistros.this, ResultadosBusqueda.class);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                    //Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(android.R.id.content),
                                            R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                                    // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                            Map<String, String> parametros = new HashMap<>();
                            parametros.put("fecha_1", fecha_1);
                            parametros.put("fecha_2", fecha_2);
                            parametros.put("idUsuario", idUsuario);
                            return parametros;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(request);
                }
            }
        });

        btnBuscarMes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mes.isEmpty() || mes.equals("Selecciona un mes")){
                    Toast.makeText(BusquedaRegistros.this, R.string.error_mes, Toast.LENGTH_SHORT).show();
                } else{
                    // Cambiamos el valor del mes de nombre a número para ejecutarlo en la consulta
                    // ya que en la base de datos se almacena en número
                    if (mes.equals("Enero")){
                        mes = "01";
                    } else if (mes.equals("Febrero")){
                        mes = "02";
                    } else if (mes.equals("Marzo")){
                        mes = "03";
                    } else if (mes.equals("Abril")){
                        mes = "04";
                    } else if (mes.equals("Mayo")){
                        mes = "05";
                    } else if (mes.equals("Junio")){
                        mes = "06";
                    } else if (mes.equals("Julio")){
                        mes = "07";
                    } else if (mes.equals("Agosto")){
                        mes = "08";
                    } else if (mes.equals("Septiembre")){
                        mes = "09";
                    } else if (mes.equals("Octubre")){
                        mes = "10";
                    } else if (mes.equals("Noviembre")){
                        mes = "11";
                    } else if (mes.equals("Diciembre")){
                        mes = "12";
                    }
                    // consulta
                    request = new StringRequest(Request.Method.POST, url_consulta8,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("0")){ // 0 significa que no se obtienen registros
                                        Toast.makeText(BusquedaRegistros.this, R.string.error_no_hay_resultados, Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            response = response.replace("][", ",");
                                            jsonArray = new JSONArray(response);
                                            for (int i = 0; i < jsonArray.length(); i++){
                                                System.out.println(jsonArray.get(i));
                                            }
                                            Intent intent = new Intent(BusquedaRegistros.this, ResultadosBusqueda.class);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                    //Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(android.R.id.content),
                                            R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                                    // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                            Map<String, String> parametros = new HashMap<>();
                            parametros.put("mes", mes);
                            parametros.put("idUsuario", idUsuario);
                            return parametros;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(request);
                }
            }
        });

        btnBuscarValoracion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (valoracion.isEmpty() || valoracion.equals("Selecciona una valoración")){
                    Toast.makeText(BusquedaRegistros.this, R.string.error_valoraciones, Toast.LENGTH_SHORT).show();
                } else {
                    // consulta
                    request = new StringRequest(Request.Method.POST, url_consulta9,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("0")){ // 0 significa que no se obtienen registros
                                        Toast.makeText(BusquedaRegistros.this, R.string.error_no_hay_resultados, Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            response = response.replace("][", ",");
                                            jsonArray = new JSONArray(response);
                                            for (int i = 0; i < jsonArray.length(); i++){
                                                System.out.println(jsonArray.get(i));
                                            }
                                            Intent intent = new Intent(BusquedaRegistros.this, ResultadosBusqueda.class);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                    //Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                    Snackbar.make(findViewById(android.R.id.content),
                                            R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                                    // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                            Map<String, String> parametros = new HashMap<>();
                            parametros.put("valoracion", valoracion);
                            parametros.put("idUsuario", idUsuario);
                            return parametros;
                        }
                    };
                    AppController.getInstance().addToRequestQueue(request);
                }
            }
        });
    }

    /***********************************************************************************************
     * Método que abre un selector de fecha
     **********************************************************************************************/
    public void abrirDatePicker(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        Button btnAceptarDp, btnCancelarDp;
        final AlertDialog dialog = alert.create();
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.selector_date_picker, (ViewGroup) findViewById(R.id.selector_date_picker));
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        dialog.setView(view);
        dialog.show();
        btnAceptarDp = (Button) view.findViewById(R.id.btn_aceptar_dp);
        btnCancelarDp = (Button) view.findViewById(R.id.btn_cancelar_dp);
        btnAceptarDp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // obtenemos los datos de la fecha seleccionada
                int dia = datePicker.getDayOfMonth();
                int mes = datePicker.getMonth() + 1; // sumamos 1 porque los meses los recoge a partir de 0 (enero = 0, febrero = 1 etc.)
                int anyo = datePicker.getYear();
                // pasamos los datos a Strings para guardarlos en la base de datos
                sDia = String.valueOf(dia);
                sMes = String.valueOf(mes);
                sAnyo = String.valueOf(anyo);
                // convertimos datos para que quede en formato “bonito” (dd/mm/aaaa, y no d/m/aaaa o dd/m/aaaa, o d/mm/aaaa)
                // le añadimos un 0 delante a los días y meses de un solo dígito
                if (mes < 10){
                    sMes = "0"+sMes;
                }
                if (dia < 10){
                    sDia = "0"+sDia;
                }

                String fecha = sDia+"/"+sMes+"/"+sAnyo;

                // si se ha abierto desde el campo de fecha exacta, se pone la fecha en dicho campo al seleccionarla y aceptar
                if (esFechaExacta) {
                    txtFechaExacta.setText(fecha);
                    esFechaExacta = false;
                } else if (esFechaRango1){ // si se ha abierto desde la primera fecha de la búsqueda de rangos, se guarda en el formato correspondiente y se pone la fecha en el campo
                    txtRangoFechas1.setText(fecha);
                    fecha_1 = sAnyo + "-" + sMes + "-" + sDia;
                    esFechaRango1 = false;
                } else if (esFechaRango2){ // lo mismo que el anterior pero en la segunda fecha de búsqueda de rangos de fecha
                    txtRangoFechas2.setText(fecha);
                    fecha_2 = sAnyo + "-" + sMes + "-" + sDia;
                    esFechaRango2 = false;
                }
                dialog.cancel();
            }
        });

        btnCancelarDp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel(); // cerramos diálogo sin hacer nada
                esFechaExacta = false;
                esFechaRango1 = false;
                esFechaRango2 = false;
            }
        });
    }

    /***********************************************************************************************
     * Método que abre el time picker para seleccionar una hora concreta de inicio o fin de jornada
     **********************************************************************************************/
    public void abrirTimePicker(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final AlertDialog dialog = alert.create();
        LayoutInflater inflater = getLayoutInflater();
        //inflate view for alertdialog since we are using multiple views inside a viewgroup (root = Layout top-level) (linear, relative, framelayout etc..)
        View view = inflater.inflate(R.layout.selector_time_picker, (ViewGroup) findViewById(R.id.selector_time_picker));
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true); // para que se vean las horas a elegir en formato 24h
        Button btnAceptar, btnCancelar;
        btnAceptar = (Button) view.findViewById(R.id.btn_aceptar_tp);
        btnCancelar = (Button) view.findViewById(R.id.btn_cancelar_tp);

        // ************* PONEMOS LA HORA QUE SE HA SELECCIONADO EN EL RELOJ AL ABRIRLO, YA QUE SI NO SE PONDRÁ SIEMPRE
        // ******* LA HORA ACTUAL.

        if (esHoraInicio) {
            if (horaInicio != 0) { // primero comprobamos que se haya puesto alguna hora, para que no se ponga en 00:00
                // VERSIONES CON ANDROID 6.0 EN ADELANTE
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    timePicker.setHour(Integer.valueOf(hora_inicio));
                    timePicker.setMinute(Integer.valueOf(minuto_inicio));
                } else {
                    timePicker.setCurrentHour(Integer.valueOf(hora_inicio));
                    timePicker.setCurrentMinute(Integer.valueOf(minuto_inicio));
                }
            }
        } else if (esHoraFin){
            if (horaFin != 0) {
                // VERSIONES CON ANDROID 6.0 EN ADELANTE
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    timePicker.setHour(Integer.valueOf(hora_fin));
                    timePicker.setMinute(Integer.valueOf(minuto_fin));
                } else {
                    timePicker.setCurrentHour(Integer.valueOf(hora_fin));
                    timePicker.setCurrentMinute(Integer.valueOf(minuto_fin));
                }
            }
        }
        // botón Aceptar del diálogo del timepicker que recoge los datos seleccionados y los pone en el campo de texto correspondiente
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Obtenemos los datos por el usuario en el timepicker
                // pero antes validamos el api del dispositivo (la versión android)
                // para poder usar unas funciones u otras

                // VERSIONES CON ANDROID 6.0 EN ADELANTE
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                    // Hacemos esto porque a partir del api 23 (android Marshmallow) las funciones
                    // para obtener las horas y minutos del timepicker son estas, y las de
                    // getCurrentHour y getCurrentMinute quedaron deprecated, así que podría
                    // traer problemas de compatibilidad en un futuro, ya que solo se mantienen
                    // para dispositivos antiguos que no tengan las versiones más recientes
                    tpHoras = timePicker.getHour();
                    tpMinutos = timePicker.getMinute();
                    Log.d("NuevoRegistroDiario", "6.0+");
                    //  VERSIONES INFERIORES A ANDROID 6.0
                } else{
                    // Estas dos funciones quedaron depreciadas en la versión 6.0, pero las incluimos
                    // en caso de que sea un dispositivo antiguo el que esté ejecutando la aplicación
                    tpHoras = timePicker.getCurrentHour();
                    tpMinutos = timePicker.getCurrentMinute();
                    Log.d("NuevoRegistroDiario", "6.0-");
                }
                // pasamos el tiempo obtenido a texto
                String sHoras = String.valueOf(tpHoras);
                String sMinutos = String.valueOf(tpMinutos);

                // para añadir un cero y que no quede un formato h:mm
                if (tpHoras<10){
                    sHoras = "0"+sHoras;
                }
                // para añadir un cero y que no quede un formato hh:m
                if (tpMinutos<10){
                    sMinutos = "0"+sMinutos;
                }

                // para poner los datos recogidos en los campos de texto
                String tiempo;

                // Validamos desde donde se abre para saber donde guardar los datos obtenidos
                if (esHoraInicio){
                    horaInicio = tpHoras;
                    minutoInicio = tpMinutos;
                    hora_inicio = sHoras;
                    minuto_inicio = sMinutos;
                    System.out.println("MINUTOS: "+ sMinutos);
                    txtHoraInicioJornada.setText(sHoras + ":" + sMinutos);
                } else if (esHoraFin){
                    horaFin = tpHoras;
                    minutoFin = tpMinutos;
                    hora_fin = sHoras;
                    minuto_fin = sMinutos;
                    System.out.println("MINUTOS: "+ sMinutos);
                    txtHoraFinJornada.setText(sHoras + ":" + sMinutos);
                }
                // ponemos de nuevo en false todos los booleanos cuando ya hayamos hecho las operaciones
                esHoraInicio = false;
                esHoraFin = false;

                dialog.cancel(); // cerramos diálogo
            }
        });

        // botón cancelar del diálogo del timepicker que solo cierra el diálogo
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel(); // cerramos diálogo sin hacer nada más
                esHoraInicio = false;
                esHoraFin = false;
            }
        });
        dialog.setView(view);
        dialog.setCanceledOnTouchOutside(false); // evitamos la posibilidad de cerrar el diálogo al pulsar fuera,
        // para evitarnos el problema de que no se ponga correctamente la hora que toca en el timepicker
        dialog.show();
    }

    // // Al pulsar la fecha de volver atrás...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
