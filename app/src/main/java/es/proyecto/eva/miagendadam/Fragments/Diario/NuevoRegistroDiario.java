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

import es.proyecto.eva.miagendadam.VolleyController.AppController;
import es.proyecto.eva.miagendadam.R;

// TODO: IMPLEMENTAR CAMPO DE HORAS DE REUNIÓN FCT
/***************************************************************************************************
 *  Clase que se abre con la pulsación del botón "+" del diario (nuevo registro de diario)         *
 *  y que sirve para crear un nuevo registro en el diario del usuario.                             *
 *  Contiene los campos de fecha, horas, descripción y valoración.                                 *
 **************************************************************************************************/
public class NuevoRegistroDiario extends AppCompatActivity {
    ImageButton btnBueno, btnRegular, btnMalo; // botones de imagen de la valoración del día
    EditText txtFecha, txtHoraInicio1, txtHoraFin1, txtHoraInicio2, txtHoraFin2, txtDescripcion; // campos de texto de información del día
    TextView turno1, turno2, txtHoras; // para mostrar u ocultar los títulos que identifican a cada turno en caso de que haya varios
    Button btnVerHoras;
    Switch switchJornada, switchReunion; // para marcar o desmarcar las opciones y en base a eso mostrar u ocultar contenido
    LinearLayout bloqueTurno2; // la capa que contiene los campos de texto para introducir hora de inicio y fin del segundo turno
    // por defecto será invisible, solo se vera al marcar la opción Jornada partida

    private StringRequest request; // petición volley
    private String fecha, horas, minutos, descripcion, idUsuario = "", valoracionDia = "";
//    private String url_consulta = "http://192.168.0.12/MiAgenda/inserta_nuevo_registro_diario.php";
//    private String url_consulta = "http://192.168.0.159/MiAgenda/inserta_nuevo_registro_diario.php";
    private String url_consulta = "http://miagendafp.000webhostapp.com/inserta_nuevo_registro_diario.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/check_fecha_registro.php";

    String sDia = "", sMes = "", sAnyo = ""; // la fecha seleccionada por el usuario a través del timePicker

    // Creamos variable de horas y minutos que serán las que usemos para obtener la hora seleccionada en el timepicker
    int tpHoras, tpMinutos;
    // Creamos variables para almacenar las distintas horas introducidas
    int horaInicio1 = 0, minutoInicio1 = 0, horaInicio2 = 0, minutoInicio2 = 0, horaFin1 = 0, minutoFin1 = 0, horaFin2 = 0, minutoFin2 = 0; //quizá no hagan falta las del turno 2, pero las ponemos por si acaso
    // creamos las horas de la jornada también en strings para guardarlas en la base de datos como texto
    String sHoraInicio1 = "", sHoraFin1 = "", sHoraInicio2 = "" , sHoraFin2 = "";
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
        // titulos de los bloques
        turno1 = (TextView) findViewById(R.id.tv_turno1);
        turno2 = (TextView) findViewById(R.id.tv_turno2);
        // capa que contiene el segundo bloque de horas para el segundo turno en el caso de que lo haya
        // por defecto aparece en modo gone (ni se ve ni ocupa espacio en la pantalla) pero si se marca
        // la opción jornada partida aparece
        bloqueTurno2 = (LinearLayout) findViewById(R.id.bloque_turno_2);
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
                }

            }
        });

        // Comprobamos el estado del selector de reunión fct
        switchReunion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){ // si se activa el selector
                    System.out.println("REUNIÓN ON");
                    // Sumaríamos las horas correspondientes de la reunión
                } else { // si se desactiva
                    System.out.println("REUNIÓN OFF");
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
     * que ya existe en el diario
     **********************************************************************************************/
    public void validarFecha(){
        request = new StringRequest(Request.Method.POST, url_consulta2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("1")){ // Hay un registro con esa fecha
                            Snackbar.make(findViewById(android.R.id.content),
                                    R.string.alert_ya_existe_fecha, Snackbar.LENGTH_SHORT).show();
                        } else if (response.equals("0")){ // No hay ningún registro con esa fecha
                            guardarRegistro(); // guardamos el registro en la base de datos
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getActivity(), R.string.error_servidor, Toast.LENGTH_LONG).show();
                        Snackbar.make(findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                        Log.e("DiarioFragment", "Error al realizar la conexión con el servidor para comprobar la fecha del registro nuevo.");
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


    /***********************************************************************************************
     * Método que comprueba que los campos de la(s) jornada(s) no esté(n) en blanco, valida los datos
     * introducidos y después valida los obtenidos para verificar que no hay datos incorrectos en
     * ningún caso.
     * También pone las horas y minutos obtenidos de la jornada en un textView
     **********************************************************************************************/
    public void validarJornada(){
        // Validamos que no haya campos en blanco
        if (hayDosJornadas) { // hay dos jornadas
            System.out.println("HAY DOS JORNADAS");
            if (horaInicio2 == 0 || horaFin2 == 0 || horaInicio1 == 0 || horaFin1 == 0) { // hay dos jornadas, y alguno de los campos está en blanco
                //Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_2, Toast.LENGTH_SHORT).show();
                Snackbar.make(this.findViewById(android.R.id.content),
                        R.string.error_datos_jornada_2, Snackbar.LENGTH_SHORT).show();
            } else { // hay dos jornadas y no hay datos en blanco en ninguna jornada, así que procedemos a hacer los cálculos y validaciones
                // primero hacemos todos los cálculos de horas y minutos correspondientes
                System.out.println("Hay dos jornadas y los datos están completos. Calculando horas...");
                calcularHoras();
                System.out.println("Tiempo calculado correctamente. Validando datos...");
                // Validamos previamente que no salen jornadas excesivas o negativas
                if (horaInicio1 > horaFin1 || horaInicio2 > horaFin2) { // alguna de las horas de inicio es más tarde que la hora de entrada, IMPOSIBLE
                   // Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_3, Toast.LENGTH_SHORT).show();
                    Snackbar.make(this.findViewById(android.R.id.content),
                            R.string.error_datos_jornada_3, Snackbar.LENGTH_SHORT).show();
                } else { // los datos son válidos, continuamos validando
                    if (horaInicio2 <= horaFin1 ){// || horaInicio2 == horaFin1 && minutoInicio2 <= minutoFin1){ <--- Comento porque no sé si será posible en algún convenio hacer un descanso entre turno y turno inferior a una hora, que sería el único supuesto en el que coincidirían las horas de fin e inicio
                        // la hora de inicio del segundo turno es menor o igual que la de fin del primer turno. No puede ser.
                        Snackbar.make(this.findViewById(android.R.id.content),
                                R.string.error_datos_jornada_6, Snackbar.LENGTH_LONG).show();
                    } else {
                        if (horasResultado <= 0) { // si diese un número negativo de horas, o que las horas obtenidas sean 0
                            //Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                            Snackbar.make(this.findViewById(android.R.id.content),
                                    R.string.error_datos_jornada_4, Snackbar.LENGTH_SHORT).show();
                        } else if (horasResultado > 8) { // si da una jornada total mayor a 8 horas estaría superando la jornada permitida
                           // Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_5, Toast.LENGTH_LONG).show();
                            Snackbar.make(this.findViewById(android.R.id.content),
                                    R.string.error_datos_jornada_5, Snackbar.LENGTH_SHORT).show();
                        } else {
                            // creamos los String para poner las horas y los minutos en el textView de horas
                            sHorasResultado = String.valueOf(horasResultado); // le damos el valor del resultado de la suma de las horas
                            sMinutosResultado = String.valueOf(minutosResultado); // hacemos lo mismo con los minutos
                            System.out.println("HORAS RESULTADO: " + horasResultado);
                            System.out.println("MINUTOS RESULTADO: " + minutosResultado);
                            System.out.println("HORAS TURNO 1: " + horasTurno1);
                            System.out.println("HORAS TURNO 2: " + horasTurno2);
                            // validamos si hay minutos, para no poner un 0 en el textView
                            if (verHoras) {
                                if (minutosResultado == 0) {
                                    txtHoras.setText(sHorasResultado + " horas");
                                } else {
                                    txtHoras.setText(sHorasResultado + " horas y " + sMinutosResultado + " minutos");
                                }
                            }

                        }
                    }
                }
            }
        } else { // No hay dos jornadas, validamos entonces con una sola:
            if (horaInicio1 != 0 || horaFin1 != 0) { // los datos de la jornada base no están en blanco
                System.out.println("Los campos de jornada no están vacíos");// , y los campos de la jornada base no están en blanco, así que calculamos
                // Primero hacemos todos los cálculos de horas y minutos correspondientes
                calcularHoras();
                // validamos previamente que no salen jornadas excesivas o negativas
                if (horaInicio1 > horaFin1) { // alguna de las horas de inicio es más tarde que la hora de entrada, IMPOSIBLE
                    //Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_3, Toast.LENGTH_SHORT).show();
                    Snackbar.make(this.findViewById(android.R.id.content),
                            R.string.error_datos_jornada_3, Snackbar.LENGTH_SHORT).show();
                } else { // los datos son válidos, continuamos validando
                    if (horasResultado <= 0) { // si diese un número negativo de horas, o que las horas obtenidas sean 0
                      //  Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                        Snackbar.make(this.findViewById(android.R.id.content),
                                R.string.error_datos_jornada_4, Snackbar.LENGTH_SHORT).show();
                    } else if (horasResultado > 8) { // si da una jornada total mayor a 8 horas estaría superando la jornada permitida
                        //Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_5, Toast.LENGTH_LONG).show();
                        Snackbar.make(findViewById(android.R.id.content),
                                R.string.error_datos_jornada_5, Snackbar.LENGTH_SHORT).show();
                    } else {
                        // creamos los String para poner las horas y los minutos en el textView de horas
                        sHorasResultado = String.valueOf(horasResultado); // le damos el valor del resultado de la suma de las horas
                        sMinutosResultado = String.valueOf(minutosResultado); // hacemos lo mismo con los minutos
                        System.out.println("HORAS RESULTADO: " + horasResultado);
                        System.out.println("MINUTOS RESULTADO: " + minutosResultado);
                        System.out.println("HORAS TURNO 1: " + horasTurno1);
                        if (verHoras) {
                            if (minutosResultado == 0) {
                                txtHoras.setText(sHorasResultado + " horas");
                            } else {
                                txtHoras.setText(sHorasResultado + " horas y " + sMinutosResultado + " minutos");
                            }
                        }
                    }
                }
            } else { // los campos de la jornada base están en blanco
                //Toast.makeText(NuevoRegistroDiario.this, R.string.error_datos_jornada_1, Toast.LENGTH_SHORT).show();
                Snackbar.make(this.findViewById(android.R.id.content),
                        R.string.error_datos_jornada_1, Snackbar.LENGTH_SHORT).show();
            }
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
        Button btnAceptar, btnCancelar;
        btnAceptar = (Button) view.findViewById(R.id.btn_aceptar_tp);
        btnCancelar = (Button) view.findViewById(R.id.btn_cancelar_tp);

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
                    tiempo = sHoras + ":" + sMinutos; // concatenamos con dos puntos en medio para dar aspecto de reloj
                    sHoraInicio1 = tiempo;
                    txtHoraInicio1.setText(tiempo); // lo ponemos en el campo de texto correspondiente
                } else if (esHoraInicio2){
                    horaInicio2 = tpHoras;
                    minutoInicio2 = tpMinutos;
                    tiempo = sHoras + ":" + sMinutos;
                    sHoraInicio2 = tiempo;
                    txtHoraInicio2.setText(tiempo);
                } else if (esHoraFin1){
                    horaFin1 = tpHoras;
                    minutoFin1 = tpMinutos;
                    tiempo = sHoras + ":" + sMinutos;
                    sHoraFin1 = tiempo;
                    txtHoraFin1.setText(tiempo);
                } else if (esHoraFin2){
                    horaFin2 = tpHoras;
                    minutoFin2 = tpMinutos;
                    tiempo = sHoras + ":" + sMinutos;
                    sHoraFin2 = tiempo;
                    txtHoraFin2.setText(tiempo);
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
            }
        });

        dialog.setView(view);
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
                horas = txtHoraInicio1.getText().toString();
                minutos = txtHoraFin1.getText().toString();
                validarJornada(); // validamos la jornada introducida
                descripcion = txtDescripcion.getText().toString();
                if (sDia.isEmpty() || sMes.isEmpty() || sAnyo.isEmpty() || horas.isEmpty()||minutos.isEmpty()||descripcion.isEmpty() || valoracionDia.isEmpty()){
                   // Toast.makeText(NuevoRegistroDiario.this, "Debes completar todos los datos.", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(android.R.id.content),
                            R.string.error_campos_vacios, Snackbar.LENGTH_SHORT).show();
                } else {
                    validarFecha(); // validamos la fecha para comprobar que no haya ya un registro con esa misma fecha
                }
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

    /***********************************************************************************************
     * Método que comprueba si el usuario que intenta iniciar sesión está bloqueado (estado isLocked)
     **********************************************************************************************/
    private void guardarRegistro(){
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("1")){ // Registro guardado con éxito
                            Toast.makeText(NuevoRegistroDiario.this, R.string.toast_registro_creado, Toast.LENGTH_LONG).show();
                          //  Log.d("NuevoRegistroDiario", "Registro creado");
                            finish(); // cerramos la actividad tras crear un nuevo registro
                        } else {
                            //Toast.makeText(NuevoRegistroDiario.this, R.string.error_registro, Toast.LENGTH_LONG).show();
                            Snackbar.make(findViewById(android.R.id.content),
                                    R.string.error_registro, Snackbar.LENGTH_SHORT).show();
                           // Log.e("NuevoRegistroDiario", "ERROR: No se ha obtenido la respuesta esperada en el script de consulta al guardar el registro");
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
                parametros.put("jornada_partida", jornada_partida);
                parametros.put("hora_inicio_1", sHoraInicio1);
                parametros.put("hora_fin_1", sHoraFin1);
                parametros.put("hora_inicio_2", sHoraInicio2);
                parametros.put("hora_fin_2", sHoraFin2);
                parametros.put("descripcion", descripcion);
                parametros.put("horas",sHorasResultado);
                parametros.put("minutos", sMinutosResultado);
                parametros.put("valoracion", valoracionDia);
                parametros.put("idUsuario", idUsuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }
}
