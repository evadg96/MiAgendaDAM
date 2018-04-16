package es.proyecto.eva.miagendadam.Fragments.Diario;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.Fragments.Contactos.NuevoContacto;
import es.proyecto.eva.miagendadam.VolleyController.AppController;
import es.proyecto.eva.miagendadam.R;

/***************************************************************************************************
 *  Clase que se abre con la pulsación del botón "+" del diario (nuevo registro de diario)         *
 *  y que sirve para crear un nuevo registro en el diario del usuario.                             *
 *  Contiene los campos de fecha, horas, descripción y valoración.                                 *
 **************************************************************************************************/
public class NuevoRegistroDiario extends AppCompatActivity {
    ImageButton btnBueno, btnRegular, btnMalo; // botones de imagen de la valoración del día
    EditText txtFecha, txtHoraInicio1, txtHoraFin1, txtHoraInicio2, txtHoraFin2, txtDescripcion, txtTiempoReunion; // campos de texto de información del día
    TextView turno1, turno2, txtHoras, txtInfoReunion; // para mostrar u ocultar los títulos que identifican a cada turno en caso de que haya varios
    Button btnVerHoras;
    Switch switchJornada, switchReunion; // para marcar o desmarcar las opciones y en base a eso mostrar u ocultar contenido
    LinearLayout bloqueTurno2, tiempoReunion; // la capa que contiene los campos de texto para introducir hora de inicio y fin del segundo turno
    // por defecto será invisible, solo se vera al marcar la opción Jornada partida

    private StringRequest request; // petición volley
    private String fecha_registro = "", fecha, descripcion = "", idUsuario = "", valoracionDia = "";
//    private String url_consulta = "http://192.168.0.12/MiAgenda/inserta_nuevo_registro_diario.php";
//    private String url_consulta = "http://192.168.0.159/MiAgenda/inserta_nuevo_registro_diario.php";
    private String url_consulta = "http://miagendafp.000webhostapp.com/inserta_nuevo_registro_diario.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/check_fecha_registro.php";
    private String reunion_fct = "0";
    private String horas_reunion = "0";
    String sDia = "", sMes = "", sAnyo = ""; // la fecha seleccionada por el usuario a través del timePicker
    // Creamos variable de horas y minutos que serán las que usemos para obtener la hora seleccionada en el timepicker
    int tpHoras, tpMinutos;
    // Creamos variables para almacenar las distintas horas introducidas
    int horaInicio1 = 0, minutoInicio1 = 0, horaInicio2 = 0, minutoInicio2 = 0, horaFin1 = 0, minutoFin1 = 0, horaFin2 = 0, minutoFin2 = 0; //quizá no hagan falta las del turno 2, pero las ponemos por si acaso
    // creamos las horas de la jornada también en strings para guardarlas en la base de datos como texto
    String sHoraInicio1 = "0", sMinInicio1 = "0", sHoraFin1 = "0", sMinFin1 = "0", sHoraInicio2 = "0", sMinInicio2 = "0", sHoraFin2 = "0", sMinFin2 = "0";
    // Creamos variables de horas y minutos para los turnos que serán el resultado de las restas de horas y minutos de arriba
    int horasTurno1, minutosTurno1, horasTurno2, minutosTurno2;
    // Creamos horas y minutos que serán el producto de sumar las horas resultantes (horasTurno1 + horasTurno2) (minutosTurno1 + minutorTurno2)
    int horasResultado, minutosResultado;
    String sHorasResultado, sMinutosResultado; // son las dos anteriores pasadas a string, para poder guardarlas como texto en la base de datos

    // Creamos booleanos para validar desde donde se ha abierto el timepicker y guardar las cifras como correspondan
    boolean esHoraInicio1 = false, esHoraInicio2 = false, esHoraFin1 = false, esHoraFin2 = false, hayDosJornadas = false; // por defecto en false
    boolean verHoras = false; // valdrá para verificar que al llamar al método validarJornada, se está haciendo por haber pulsado este botón,
    // para así saber que se tiene que poner las horas en el textView que hay al lado del botón. Así no se pondrá siempre que se ejecute el método,
    // porque también se ejecuta al dar a guardar el registro
    boolean hayReunion = false;
    // Creamos la siguiente variable para determinar en la base de datos si es jornada partida o no
    String jornada_partida = "0"; // por defecto en 0, que sería que no es partida

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_registro_diario);
        setTitle(R.string.title_activity_nuevo_registro_diario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // para evitar que el teclado se abra solo automáticamente al abrirse la pantalla
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        btnBueno = (ImageButton) findViewById(R.id.btn_bueno);
        btnRegular = (ImageButton) findViewById(R.id.btn_regular);
        btnMalo = (ImageButton) findViewById(R.id.btn_malo);
        txtFecha = (EditText) findViewById(R.id.editText_fecha);
        txtHoraInicio1 = (EditText) findViewById(R.id.txt_hora_inicio_1);
        txtHoraFin1 = (EditText) findViewById(R.id.txt_hora_fin_1);
        txtHoraInicio2 = (EditText) findViewById(R.id.txt_hora_inicio_2);
        txtHoraFin2 = (EditText) findViewById(R.id.txt_hora_fin_2);
        txtHoras = (TextView) findViewById(R.id.txt_horas_obtenidas);
        txtInfoReunion = (TextView) findViewById(R.id.txt_info_reunion);
        txtTiempoReunion = (EditText) findViewById(R.id.txt_horas_reunion);
        // titulos de los bloques
        turno1 = (TextView) findViewById(R.id.tv_turno1);
        turno2 = (TextView) findViewById(R.id.tv_turno2);
        // capa que contiene el segundo bloque de horas para el segundo turno en el caso de que lo haya
        // por defecto aparece en modo gone (ni se ve ni ocupa espacio en la pantalla) pero si se marca
        // la opción jornada partida aparece
        bloqueTurno2 = (LinearLayout) findViewById(R.id.bloque_turno_2);
        tiempoReunion = (LinearLayout) findViewById(R.id.tiempo_reunion);
        btnVerHoras = (Button) findViewById(R.id.btn_ver_horas); // obtiene las horas resultantes de las jornadas introducidas
        switchJornada = (Switch)findViewById(R.id.switch_jornada); // selector de turno partido
        switchReunion = (Switch)findViewById(R.id.switch_reunion); // selector de reunión
        txtDescripcion = (EditText) findViewById(R.id.editText_descripcion);
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario al que vamos a introducir el registro.
        // Mostramos el dato obtenido de las preferencias para verificar que es correcto
        //Log.d("NuevoRegistroDiario", "idUsuario almacenado: " + idUsuario);

// Muestra el timepicker para elegir las horas correspondientes al tocar alguno de los campos
        // de horas
        txtHoraInicio1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    esHoraInicio1 = true;
                    abrirTimePicker();
                }
                return true;
            }
        });
        txtHoraInicio2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    esHoraInicio2 = true;
                    abrirTimePicker();
                }
                return true;
            }
        });
        txtHoraFin1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    esHoraFin1 = true;
                    abrirTimePicker();
                }
                return true;
            }
        });
        txtHoraFin2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    esHoraFin2 = true;
                    abrirTimePicker();
                }
                return true;
            }
        });


        // Comprobamos el estado del selector de turno partido
        switchJornada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Cuando esté marcado el selector, si isChecked está en true, es = activado. En false es = desactivado
                // Validamos:
                if (isChecked){ // si se activa el selector
                    System.out.println("TURNO PARTIDO ON");
                    // Activamos la visualización de campos
                    turno1.setVisibility(View.VISIBLE);
                    turno2.setVisibility(View.VISIBLE);
                    bloqueTurno2.setVisibility(View.VISIBLE);
                    hayDosJornadas = true;
                    jornada_partida = "1"; // cambiamos a 1 para guardarlo en la base de datos
                } else { // si se desactiva
                    System.out.println("TURNO PARTIDO OFF");
                    // Desactivamos la visualización de campos
                    turno1.setVisibility(View.GONE);
                    turno2.setVisibility(View.GONE);
                    bloqueTurno2.setVisibility(View.GONE);
                    hayDosJornadas = false;
                    jornada_partida = "0"; // cambiamos a 0 para guardarlo en la base de datos
                    sHoraInicio2 = "0";
                    sMinInicio2 = "0";
                    sHoraFin2 = "0";
                    sMinFin2 = "0";
                }

            }
        });

        // Comprobamos el estado del selector de reunión fct
        switchReunion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){ // si se activa el selector
                    System.out.println("REUNIÓN ON");
                    hayReunion = true;
                    reunion_fct = "1";
                    tiempoReunion.setVisibility(View.VISIBLE);
                    txtInfoReunion.setVisibility(View.VISIBLE);
                } else { // si se desactiva
                    System.out.println("REUNIÓN OFF");
                    hayReunion = false;
                    reunion_fct = "0";
                    horas_reunion = "0";
                    tiempoReunion.setVisibility(View.GONE);
                    txtInfoReunion.setVisibility(View.GONE);
                }

            }
        });


        // Al pulsar sobre el campo de fecha se despliega directamente el calendario
        txtFecha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    abrirDatePicker();
                }
                return true;
            }
        });

        // Ponemos los iconos por defecto con semitransparencia, para ponerse opacos en su selección
        // para poder saber cuál está marcado en t0do momento
        btnBueno.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnBueno.setAlpha(1f); // opaco
                btnRegular.setAlpha(0.5f); // semitransparente
                btnMalo.setAlpha(0.5f); // "
                valoracionDia = "Bueno";
               // Log.i("NuevoRegistroDiario", "Valoración Bueno");
            }
        });
        btnRegular.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnRegular.setAlpha(1f);
                btnBueno.setAlpha(0.5f);
                btnMalo.setAlpha(0.5f);
                valoracionDia = "Regular";
               // Log.i("NuevoRegistroDiario", "Valoración Regular");
            }
        });
        btnMalo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnMalo.setAlpha(1f);
                btnBueno.setAlpha(0.5f);
                btnRegular.setAlpha(0.5f);
                valoracionDia = "Malo";
               // Log.i("NuevoRegistroDiario", "Valoración Malo");
            }
        });

        // Obtiene el total de horas y minutos haciendo los pertinentes cálculos con las horas de inicio y fin de jornada(s) introducidas
        // y lo muestra en un textView
        btnVerHoras.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                verHoras = true;
                validarJornada();
            }
        });
    }

    /***********************************************************************************************
     * Método que comprueba que no se crea un registro nuevo con una fecha de un registro
     * que ya existe en el diario.
     * Si se pasa la validación correctamente, se procede a guardar satisfactoriamente el registro
     * llamando al método correspondiente (guardarRegistro() )
     **********************************************************************************************/
    public void validarFecha(){
        System.out.println("Validando fecha...");
        request = new StringRequest(Request.Method.POST, url_consulta2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("1")){ // Hay un registro con esa fecha
                            System.out.println("ERROR FECHA DE REGISTRO YA EXISTENTE.");
                           // Snackbar.make(findViewById(android.R.id.content),
                             //       R.string.alert_ya_existe_fecha, Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(NuevoRegistroDiario.this, R.string.alert_ya_existe_fecha, Toast.LENGTH_SHORT).show();
                        } else if (response.equals("0")){ // No hay ningún registro con esa fecha
                            System.out.println("Guardando registro...");
                            guardarRegistro(); // guardamos el registro en la base de datos
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        //Snackbar.make(findViewById(android.R.id.content),
                          //      R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                        Log.e("DiarioFragment", "Error al realizar la conexión con el servidor para comprobar la fecha del registro nuevo.");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
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


    /***********************************************************************************************
     * Método que comprueba que los campos de la(s) jornada(s) no esté(n) en blanco, valida los datos
     * introducidos y después valida los obtenidos para verificar que no hay datos incorrectos en
     * ningún caso.
     * También pone las horas y minutos obtenidos de la jornada en un textView
     **********************************************************************************************/
    public void validarJornada(){
        // Validamos que no haya campos en blanco:
        if (hayDosJornadas) { // Si HAY DOS JORNADAS ...
            System.out.println("HAY DOS JORNADAS");
            if (horaInicio2 == 0 || horaFin2 == 0 || horaInicio1 == 0 || horaFin1 == 0) { // hay dos jornadas, y alguno de los campos está en blanco
                Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_2, Toast.LENGTH_SHORT).show();
                System.out.println("DATOS EN BLANCO EN DOS JORNADAS.");
                //Snackbar.make(this.findViewById(android.R.id.content),
                  //      R.string.error_datos_jornada_2, Snackbar.LENGTH_SHORT).show();
            } else {
                // hay dos jornadas y no hay datos en blanco en ninguna jornada, así que procedemos a hacer los cálculos y validaciones
                // primero hacemos todos los cálculos de horas y minutos correspondientes
                System.out.println("Hay dos jornadas y los datos están completos. Calculando horas...");
                calcularHoras();
                System.out.println("Tiempo calculado correctamente. Validando datos...");
                // Validamos previamente que no salen jornadas excesivas o negativas:
                if (horaInicio1 > horaFin1 || horaInicio2 > horaFin2) { // alguna de las horas de inicio es más tarde que la hora de entrada, IMPOSIBLE
                    Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                    System.out.println("JORNADA(S) NO VÁLIDA(S) 1.");
                    //Snackbar.make(this.findViewById(android.R.id.content),
                      //      R.string.error_datos_jornada_4, Snackbar.LENGTH_SHORT).show();
                } else { // los datos son válidos, continuamos validando
                    if (horaInicio2 <= horaFin1) {// || horaInicio2 == horaFin1 && minutoInicio2 <= minutoFin1){ <--- Comento porque no sé si será posible en algún convenio hacer un descanso entre turno y turno inferior a una hora, que sería el único supuesto en el que coincidirían las horas de fin e inicio
                        // la hora de inicio del segundo turno es menor o igual que la de fin del primer turno. No puede ser.
                        System.out.println("JORNADA(S) NO VÁLIDA(S) 2.");
                        Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                        //Snackbar.make(this.findViewById(android.R.id.content),
                            //    R.string.error_datos_jornada_4, Snackbar.LENGTH_LONG).show();
                    } else if (horasResultado <= 0 || horaInicio1 == horaFin1 && minutoInicio1 == minutoFin1 || horaInicio2 == horaFin2 && minutoInicio2 == minutoFin2) { // si diese un número negativo de horas, o que las horas obtenidas sean 0
                        Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                        System.out.println("JORNADA(S) NO VÁLIDA(S) 3.");
                       // Snackbar.make(this.findViewById(android.R.id.content),
                        //        R.string.error_datos_jornada_4, Snackbar.LENGTH_SHORT).show();
                    } else if (horasResultado > 8) { // si da una jornada total mayor a 8 horas estaría superando la jornada permitida
                        // Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_5, Toast.LENGTH_LONG).show();
                        System.out.println("JORNADA(S) NO VÁLIDA(S) 4.");
                        Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_5, Toast.LENGTH_SHORT).show();
                    //Snackbar.make(this.findViewById(android.R.id.content),
                          //      R.string.error_datos_jornada_5, Snackbar.LENGTH_SHORT).show();
                    } else { // No hay ningún error con los datos de la jornada. Pasamos a la siguiente comprobación: ¿hay reunión fct?
                        // creamos los String para poner las horas y los minutos en el textView de horas
                        sHorasResultado = String.valueOf(horasResultado); // le damos el valor del resultado de la suma de las horas
                        sMinutosResultado = String.valueOf(minutosResultado); // hacemos lo mismo con los minutos
                        if (hayReunion){ // SÍ HAY REUNIÓN
                            System.out.println("HAY REUNIÓN.");
                            horas_reunion = txtTiempoReunion.getText().toString();
                            if (horas_reunion.isEmpty()){ // si NO HAY HORAS DE REUNIÓN introducidas...
                                System.out.println("HORAS REUNIÓN EN BLANCO.");
                                Toast.makeText(NuevoRegistroDiario.this, R.string.error_horas_reunion, Toast.LENGTH_SHORT).show();
                                //Snackbar.make(findViewById(android.R.id.content),
                                  //      R.string.error_horas_reunion, Snackbar.LENGTH_SHORT).show();
                            } else { // SÍ HAY HORAS DE REUNIÓN, validamos formato...
                                if (Integer.valueOf(horas_reunion) == 0){
                                    System.out.println("ERROR HORAS REUNIÓN 1.");
                                    Toast.makeText(NuevoRegistroDiario.this, R.string.alert_horas_reunion_2, Toast.LENGTH_SHORT).show();
                                    //Snackbar.make(findViewById(android.R.id.content),
                                      //      R.string.alert_horas_reunion_2, Snackbar.LENGTH_SHORT).show();
                                } else if (Integer.valueOf(horas_reunion) > 2){
                                    System.out.println("ERROR HORAS REUNIÓN 2.");
                                    Toast.makeText(NuevoRegistroDiario.this, R.string.alert_horas_reunion, Toast.LENGTH_SHORT).show();
                                   // Snackbar.make(findViewById(android.R.id.content),
                                     //       R.string.alert_horas_reunion, Snackbar.LENGTH_SHORT).show();
                                } else { // No hay ningún problema con las horas, por lo tanto se pasan todas las validaciones y se guarda el registro
                                    System.out.println("SUMAMOS " + horas_reunion + " HORAS DE REUNIÓN FCT AL TOTAL DE HORAS CALCULADAS DE LA JORNADA");
                                    horasResultado = horasResultado + Integer.valueOf(horas_reunion);
                                    sHorasResultado = String.valueOf(horasResultado);
                                    if (verHoras) { // si está en true, es porque se ha pulsado el botón de ver las horas obtenidas de la(s) jornada(s) introducida(s)
                                        System.out.println("VISUALIZACIÓN DE HORAS OBTENIDAS SIN GUARDAR EL REGISTRO, CON REUNIÓN, DOS JORNADAS.");
                                        if (minutosResultado == 0) { // No hay minutos
                                            if (horasResultado == 1){ // Solo hay una hora
                                                txtHoras.setText(sHorasResultado + " hora");
                                            } else { // Más de una hora
                                                txtHoras.setText(sHorasResultado + " horas");
                                            }
                                        } else { // Hay minutos
                                            if (horasResultado == 1){ // Solo hay una hora
                                                txtHoras.setText(sHorasResultado + " hora y "  + sMinutosResultado + " minutos");
                                            } else {
                                                txtHoras.setText(sHorasResultado + " horas y " + sMinutosResultado + " minutos");                                            }
                                        }
                                    } else {
                                        System.out.println("PASA A VALIDACIÓN FINAL DESDE 2 JORNADAS CON REUNIÓN");
                                        pasaValidacion();
                                    }
                                }
                            }
                        } else { // NO HAY REUNIÓN, se pasa directamente a guardar el registro
                            if (verHoras) { // si está en true, es porque se ha pulsado el botón de ver las horas obtenidas de la(s) jornada(s) introducida(s)
                                // y por tanto solo VEREMOS LOS DATOS, no guardaremos nada
                                System.out.println("VISUALIZACIÓN DE HORAS OBTENIDAS SIN GUARDAR EL REGISTRO, SIN REUNIÓN, DOS JORNADAS.");
                                if (minutosResultado == 0) { // No hay minutos
                                    if (horasResultado == 1){ // Solo hay una hora
                                        txtHoras.setText(sHorasResultado + " hora");
                                    } else { // Más de una hora
                                        txtHoras.setText(sHorasResultado + " horas");
                                    }
                                } else { // Hay minutos
                                    if (horasResultado == 1){ // Solo hay una hora
                                        txtHoras.setText(sHorasResultado + " hora y "  + sMinutosResultado + " minutos");
                                    } else {
                                        txtHoras.setText(sHorasResultado + " horas y " + sMinutosResultado + " minutos");                                            }
                                }
                            } else {
                                System.out.println("PASA A VALIDACIÓN FINAL DESDE 2 JORNADAS SIN REUNIÓN");
                                pasaValidacion();
                            }
                        }
                    }
                }
            }
        } else { // SOLO UNA JORNADA, validamos entonces con una sola:
            if (horaInicio1 != 0 || horaFin1 != 0) { // los datos de la jornada base NO están EN BLANCO
                System.out.println("Los campos de la jornada no están vacíos, calculamos horas...");// , y los campos de la jornada base no están en blanco, así que calculamos
                // Primero hacemos todos los cálculos de horas y minutos correspondientes
                calcularHoras();
                // validamos previamente que no salen jornadas excesivas o negativas
                if (horaInicio1 > horaFin1) { // alguna de las horas de inicio es más tarde que la hora de entrada, IMPOSIBLE
                    Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                    System.out.println("JORNADA NO VÁLIDA 1.");
                   // Snackbar.make(this.findViewById(android.R.id.content),
                     //       R.string.error_datos_jornada_4, Snackbar.LENGTH_SHORT).show();
                } else { // los datos son válidos, continuamos validando
                    if (horasResultado <= 0) { // si diese un número negativo de horas, o que las horas obtenidas sean 0
                      Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                        System.out.println("JORNADA NO VÁLIDA 2.");
                       // Snackbar.make(this.findViewById(android.R.id.content),
                          //      R.string.error_datos_jornada_4, Snackbar.LENGTH_SHORT).show();
                    } else if (horasResultado > 8) { // si da una jornada total mayor a 8 horas estaría superando la jornada permitida
                        Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_5, Toast.LENGTH_LONG).show();
                        System.out.println("JORNADA NO VÁLIDA 3.");
                       // Snackbar.make(findViewById(android.R.id.content),
                          //      R.string.error_datos_jornada_5, Snackbar.LENGTH_SHORT).show();
                    } else { // No hay errores con la jornada, seguimos con las comprobaciones:
                        // creamos los String para poner las horas y los minutos en el textView de horas
                        sHorasResultado = String.valueOf(horasResultado); // le damos el valor del resultado de la suma de las horas
                        sMinutosResultado = String.valueOf(minutosResultado); // hacemos lo mismo con los minutos
                        if (hayReunion){ // si HAY REUNIÓN...
                            System.out.println("HAY REUNIÓN (UNA JORNADA)");
                            horas_reunion = txtTiempoReunion.getText().toString();
                            if (horas_reunion.isEmpty()){ // comprobamos si se han introducido las horas
                                System.out.println("ERROR HORAS REUNIÓN 1 (UNA JORNADA).");
                                Toast.makeText(NuevoRegistroDiario.this, R.string.error_horas_reunion, Toast.LENGTH_SHORT).show();
                                //Snackbar.make(findViewById(android.R.id.content),
                                   //     R.string.error_horas_reunion, Snackbar.LENGTH_SHORT).show();
                            } else {
                                if (Integer.valueOf(horas_reunion) == 0){
                                    System.out.println("ERROR HORAS REUNIÓN 2 (UNA JORNADA).");
                                    Toast.makeText(NuevoRegistroDiario.this, R.string.alert_horas_reunion_2, Toast.LENGTH_SHORT).show();
                                   // Snackbar.make(findViewById(android.R.id.content),
                                     //       R.string.alert_horas_reunion_2, Snackbar.LENGTH_SHORT).show();
                                } else if (Integer.valueOf(horas_reunion) > 2){
                                    System.out.println("ERROR HORAS REUNIÓN 3 (UNA JORNADA).");
                                    Toast.makeText(NuevoRegistroDiario.this, R.string.alert_horas_reunion, Toast.LENGTH_SHORT).show();
                               //     Snackbar.make(findViewById(android.R.id.content),
                                 //           R.string.alert_horas_reunion, Snackbar.LENGTH_SHORT).show();
                                } else { // No hay errores con las horas de la reunión, pasamos al guardado del registro.
                                    System.out.println("SUMAMOS " + horas_reunion + " HORAS DE REUNIÓN FCT AL TOTAL DE HORAS CALCULADAS DE LA JORNADA");
                                    horasResultado = horasResultado + Integer.valueOf(horas_reunion);
                                    sHorasResultado = String.valueOf(horasResultado);
                                    if (verHoras) { // si está en true, es porque se ha pulsado el botón de ver las horas obtenidas de la(s) jornada(s) introducida(s)
                                        // y por tanto solo veremos los resultados, no guardarmos nada
                                        System.out.println("VISUALIZACIÓN DE HORAS OBTENIDAS SIN GUARDAR EL REGISTRO, CON REUNIÓN, UNA JORNADA.");
                                        if (minutosResultado == 0) { // No hay minutos
                                            if (horasResultado == 1){ // Solo hay una hora
                                                txtHoras.setText(sHorasResultado + " hora");
                                            } else { // Más de una hora
                                                txtHoras.setText(sHorasResultado + " horas");
                                            }
                                        } else { // Hay minutos
                                            if (horasResultado == 1){ // Solo hay una hora
                                                txtHoras.setText(sHorasResultado + " hora y "  + sMinutosResultado + " minutos");
                                            } else {
                                                txtHoras.setText(sHorasResultado + " horas y " + sMinutosResultado + " minutos");                                            }
                                        }
                                    } else { // si no está en true, quiere decir que se ha pulsado el botón de guardar y no el de ver horas, así que directamente pasamos
                                        System.out.println("PASA A VALIDACIÓN FINAL DESDE 1 JORNADA CON REUNIÓN");
                                        pasaValidacion();
                                    }
                                }
                            }
                        } else { // NO HAY REUNIÓN, se pasa directamente al guardado del registro
                            if (verHoras) { // si está en true, es porque se ha pulsado el botón de ver las horas obtenidas de la(s) jornada(s) introducida(s)
                                System.out.println("VISUALIZACIÓN DE HORAS OBTENIDAS SIN GUARDAR EL REGISTRO, SIN REUNIÓN, UNA JORNADA.");
                                if (minutosResultado == 0) { // No hay minutos
                                    if (horasResultado == 1){ // Solo hay una hora
                                        txtHoras.setText(sHorasResultado + " hora");
                                    } else { // Más de una hora
                                        txtHoras.setText(sHorasResultado + " horas");
                                    }
                                } else { // Hay minutos
                                    if (horasResultado == 1){ // Solo hay una hora
                                        txtHoras.setText(sHorasResultado + " hora y "  + sMinutosResultado + " minutos");
                                    } else {
                                        txtHoras.setText(sHorasResultado + " horas y " + sMinutosResultado + " minutos");
                                    }
                                }
                            } else {
                                System.out.println("PASA A VALIDACIÓN FINAL DESDE 1 JORNADA SIN REUNIÓN");
                                pasaValidacion();
                            }
                        }
                    }
                }
            } else { // los CAMPOS de la jornada base están EN BLANCO
                Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_1, Toast.LENGTH_SHORT).show();
                System.out.println("ERROR DATOS JORNADA EN BLANCO.");
               // Snackbar.make(this.findViewById(android.R.id.content),
                 //       R.string.error_datos_jornada_1, Snackbar.LENGTH_SHORT).show();
            }
        }
        verHoras = false;
    }

    /***********************************************************************************************
     * Método que se ejecuta una vez se han pasado todas las validaciones del método validarJornada()
     * Cuando se ejecuta este, se llama a la validación de la fecha introducida, y desde ahí
     * se determina si se guarda el registro o no.
     **********************************************************************************************/
    public void pasaValidacion(){
        descripcion = txtDescripcion.getText().toString();
        if (sDia.isEmpty() || sMes.isEmpty() || sAnyo.isEmpty() || descripcion.isEmpty() || valoracionDia.isEmpty()){
             Toast.makeText(NuevoRegistroDiario.this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            System.out.println("ALGUNO DE LOS CAMPOS DE FECHA / DESCRIPCIÓN / VALORACIÓN ESTÁ VACÍO");
           // Snackbar.make(findViewById(android.R.id.content),
             //       R.string.error_campos_vacios, Snackbar.LENGTH_SHORT).show();
        } else {
            System.out.println("Pasando a validación de fecha...");
            validarFecha(); // validamos la fecha para comprobar que no haya ya un registro con esa misma fecha
        }
    }

    /***********************************************************************************************
     * Método que abre el DatePicker (calendario para seleccionar la fecha exacta del
     * registro a crear)
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
                fecha = sDia + "/" + sMes + "/" + sAnyo;
                fecha_registro = sAnyo + "-" + sMes + "-" + sDia; // guardamos la fecha en el formato aaaa-mm-dd para la base de datos
                System.out.println("FECHA BD: " + fecha_registro);
                dialog.cancel();
                txtFecha.setText(fecha);
            }
        });

        btnCancelarDp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel(); // cerramos diálogo sin hacer nada
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

        if (esHoraInicio1) {
            if (horaInicio1 != 0) { // primero comprobamos que se haya puesto alguna hora, para que no se ponga en 00:00
                // VERSIONES CON ANDROID 6.0 EN ADELANTE
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    timePicker.setHour(Integer.valueOf(sHoraInicio1));
                    timePicker.setMinute(Integer.valueOf(sMinInicio1));
                } else {
                    timePicker.setCurrentHour(Integer.valueOf(sHoraInicio1));
                    timePicker.setCurrentMinute(Integer.valueOf(sMinInicio1));
                }
            }
        } else if (esHoraFin1){
            if (horaFin1 != 0) {
                // VERSIONES CON ANDROID 6.0 EN ADELANTE
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    timePicker.setHour(Integer.valueOf(sHoraFin1));
                    timePicker.setMinute(Integer.valueOf(sMinFin1));
                } else {
                    timePicker.setCurrentHour(Integer.valueOf(sHoraFin1));
                    timePicker.setCurrentMinute(Integer.valueOf(sMinFin1));
                }
            }
        } else if (esHoraInicio2){
            if (horaInicio2 != 0) {
                // VERSIONES CON ANDROID 6.0 EN ADELANTE
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    timePicker.setHour(Integer.valueOf(sHoraInicio2));
                    timePicker.setMinute(Integer.valueOf(sMinInicio2));
                } else {
                    timePicker.setCurrentHour(Integer.valueOf(sHoraInicio2));
                    timePicker.setCurrentMinute(Integer.valueOf(sMinInicio2));
                }
            }
        } else if (esHoraFin2){
            if (horaFin2 != 0) {
                // VERSIONES CON ANDROID 6.0 EN ADELANTE
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    timePicker.setHour(Integer.valueOf(sHoraFin2));
                    timePicker.setMinute(Integer.valueOf(sMinFin2));
                } else {
                    timePicker.setCurrentHour(Integer.valueOf(sHoraFin2));
                    timePicker.setCurrentMinute(Integer.valueOf(sMinFin2));
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
                if (esHoraInicio1){
                    horaInicio1 = tpHoras;
                    minutoInicio1 = tpMinutos;
                    sHoraInicio1 = sHoras;
                    sMinInicio1 = sMinutos;
                    System.out.println("MINUTOS: "+ sMinutos);
                    txtHoraInicio1.setText(sHoras + ":" + sMinutos);
                } else if (esHoraInicio2){
                    horaInicio2 = tpHoras;
                    minutoInicio2 = tpMinutos;
                    sHoraInicio2 = sHoras;
                    sMinInicio2 = sMinutos;
                    System.out.println("MINUTOS: "+ sMinutos);
                    txtHoraInicio2.setText(sHoras + ":" + sMinutos);
                } else if (esHoraFin1){
                    horaFin1 = tpHoras;
                    minutoFin1 = tpMinutos;
                    sHoraFin1 = sHoras;
                    sMinFin1 = sMinutos;
                    System.out.println("MINUTOS: "+ sMinutos);
                    txtHoraFin1.setText(sHoras + ":" + sMinutos);
                } else if (esHoraFin2){
                    horaFin2 = tpHoras;
                    minutoFin2 = tpMinutos;
                    sHoraFin2 = sHoras;
                    sMinFin2 = sMinutos;
                    System.out.println("MINUTOS: "+ sMinutos);
                    txtHoraFin2.setText(sHoras + ":" + sMinutos);
                }
                // ponemos de nuevo en false todos los booleanos cuando ya hayamos hecho las operaciones
                esHoraInicio1 = false;
                esHoraInicio2 = false;
                esHoraFin1 = false;
                esHoraFin2 = false;

                System.out.println("HORA INICIO 1: " + horaInicio1);
                System.out.println("HORA FIN 1: " + horaFin1);
                dialog.cancel(); // cerramos diálogo
            }
        });

        // botón cancelar del diálogo del timepicker que solo cierra el diálogo
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel(); // cerramos diálogo sin hacer nada más
                esHoraInicio1 = false;
                esHoraInicio2 = false;
                esHoraFin1 = false;
                esHoraFin2 = false;
            }
        });
        dialog.setView(view);
        dialog.setCanceledOnTouchOutside(false); // evitamos la posibilidad de cerrar el diálogo al pulsar fuera,
        // para evitarnos el problema de que no se ponga correctamente la hora que toca en el timepicker
        dialog.show();
    }

    /***********************************************************************************************
     * Método que realiza todos los cálculos con las horas de inicio y fin de jornadas introducidas
     * obteniendo a partir de ello las horas resultantes desde el inicio hasta el fin de la jornada,
     * tanto en horas como en minutos exactos si no son franjas de horas redondas
     **********************************************************************************************/
    public void calcularHoras(){
        System.out.println("Calculando horas...");
        horasTurno1 = horaFin1 - horaInicio1;
        minutosTurno1 = minutoFin1 - minutoInicio1;
        if (minutoFin1 > minutoInicio1) {
            System.out.println("MinF > MinI");
            // Siguiendo el algoritmo que hemos definido, si el minuto de fin es mayor que el de inicio, tan solo hay que restarlo y punto
            // si no, a continuación vemos
            horasTurno1 = horaFin1 - horaInicio1;
            minutosTurno1 = minutoFin1 - minutoInicio1;
        } else if (minutoFin1 < minutoInicio1){
            System.out.println("MinF < MinI");
            // Si por el contrario, el minuto de fin es menor que el de inicio, no será tan fácil como restar, puesto que también variará
            // el número de horas.
            // Por tanto, lo que haremos será restar una hora y hacer otro cálculo con los minutos:
            horasTurno1 = (horaFin1 - horaInicio1) - 1;
            minutosTurno1 = 60 - (minutoInicio1 - minutoFin1);
        } else if (minutoFin2 == minutoInicio2){ // si se da el caso de que los minutos son los mismos, solo restamos las horas
            horasTurno1 = horaFin1 - horaInicio1;
            System.out.println("MinF = MinI");
        }
        // Validamos si debemos hacer cuentas para un segundo turno:
        if (hayDosJornadas){ // hay dos turnos
            System.out.println("Calculando tiempo de la segunda jornada...");
            if (minutoFin2 > minutoInicio2) {
                horasTurno2 = horaFin2 - horaInicio2;
                minutosTurno2 = minutoFin2 - minutoInicio2;
            } else if (minutoFin2 < minutoInicio2){
                horasTurno2 = (horaFin2 - horaInicio2) - 1;
                minutosTurno2 = 60 - (minutoInicio2 - minutoFin2);
            } else if (minutoFin2 == minutoInicio2){
                horasTurno2 = horaFin2 - horaInicio2;
            }
            // al haber segundo turno, debemos sumar las horas de ambos turnos para tener las horas y minutos totales finales del día completo
            horasResultado = horasTurno1 + horasTurno2;
            minutosResultado = minutosTurno1 + minutosTurno2;
            if (minutosResultado > 59){ // si la suma de minutos sobrepasa los 60, sumamos una hora y restamos esos 60
                // ya que los minutos nunca pueden ser más de 59
                minutosResultado = minutosResultado - 60;
                horasResultado++;
            }
            System.out.println("HORAS Y MINUTOS OBTENIDOS: " + horasResultado + " horas y " + minutosResultado + " minutos");
        } else {
            // al no haber dos jornadas, el total será simplemente las horas y minutos de la jornada base
            horasResultado = horasTurno1;
            minutosResultado = minutosTurno1;
            if (minutosResultado > 59){
                minutosResultado = minutosResultado - 60;
                horasResultado++;
            }
            System.out.println("HORAS Y MINUTOS OBTENIDOS: " + horasResultado + " horas y " + minutosResultado + " minutos");
        }
    }

    // Añade los iconos a la barra de acciones (en este caso, el de guardar)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo, menu);
        return true; // .menu es el directorio, y .menu_nuevo el archivo
    }

    /***********************************************************************************************
     *     Opciones del menú de la barra de acciones
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.menu_guardar: // Opción de guardar registro
               // Log.i("NuevoRegistroDiario", "Action Guardar registro");
                validarJornada(); // validamos la jornada introducida
                return true;
            case android.R.id.home: // Opción de volver hacia atrás
               // Log.i("NuevoRegistroDiario", "Action Atrás");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // En caso de pulsar hacia atrás, bien desde la flecha del dispositivo, bien desde la de la app,
    // preguntamos si está seguro, y si es que sí, cerramos la actividad
    public void onBackPressed(){
        // validamos primero que se haya introducido algún dato en algún campo, de no ser así entenderemos que no se ha escrito nada y por tanto no se va
        // a perder nada de información, así que cerraríamos sin preguntar
        if (fecha_registro.isEmpty() && jornada_partida.equals("0") && sHoraInicio1.equals("0") && sMinInicio1.equals("0")
                && sHoraFin1.equals("0") && sMinFin1.equals("0") && sHoraInicio2.equals("0") && sMinInicio2.equals("0") && sHoraFin2.equals("0")
                && sMinFin2.equals("0") && reunion_fct.equals("0") && horas_reunion.equals("0") && descripcion.isEmpty()
                && valoracionDia.isEmpty()){
            System.out.println("ESTÁ VACÍO");
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(NuevoRegistroDiario.this);
            builder.setTitle(R.string.titulo_dialog_salir_sin_guardar); // titulo del diálogo
            builder.setMessage(R.string.contenido_dialog_salir_sin_guardar)
                    .setPositiveButton(R.string.respuesta_dialog_volver, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish(); // volvemos atrás
                        }
                    })
                    .setNegativeButton(R.string.respuesta_dialog_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            //no hacemos nada, y al pulsar el botón simplemente se cerrará el diálogo
                        }
                    });
            // Create the AlertDialog object and return it
            Dialog dialog = builder.create();
            dialog.show();
        }

    }

    /***********************************************************************************************
     * Método que comprueba si el usuario que intenta iniciar sesión está bloqueado (estado isLocked)
     **********************************************************************************************/
    private void guardarRegistro(){
        System.out.println("Guardando registro...");
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("1")) {
                            Toast.makeText(NuevoRegistroDiario.this, R.string.toast_registro_creado, Toast.LENGTH_LONG).show();
                            //  Log.d("NuevoRegistroDiario", "Registro creado");
                            finish(); // cerramos la actividad tras crear un nuevo registro
                        } else {
                            Toast.makeText(NuevoRegistroDiario.this, R.string.error_crear_registro, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        //Snackbar.make(findViewById(android.R.id.content),
                          //      R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                       // Log.d("NuevoRegistroDiario", "Error de conexión con el servidor al intentar guardar el registro");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("dia", sDia);
                parametros.put("mes", sMes);
                parametros.put("anyo", sAnyo);
                parametros.put("fecha", fecha_registro);
                parametros.put("jornada_partida", jornada_partida);
                parametros.put("hora_inicio_1", sHoraInicio1);
                parametros.put("minuto_inicio_1", sMinInicio1);
                parametros.put("hora_fin_1", sHoraFin1);
                parametros.put("minuto_fin_1", sMinFin1);
                parametros.put("hora_inicio_2", sHoraInicio2);
                parametros.put("minuto_inicio_2", sMinInicio2);
                parametros.put("hora_fin_2", sHoraFin2);
                parametros.put("minuto_fin_2", sMinFin2);
                parametros.put("reunion_fct", reunion_fct);
                parametros.put("horas_reunion", horas_reunion);
                parametros.put("descripcion", descripcion);
                parametros.put("horas", sHorasResultado);
                parametros.put("minutos", sMinutosResultado);
                parametros.put("valoracion", valoracionDia);
                parametros.put("idUsuario", idUsuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }
}
