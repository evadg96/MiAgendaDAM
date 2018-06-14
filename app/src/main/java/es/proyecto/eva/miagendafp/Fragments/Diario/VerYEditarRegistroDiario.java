package es.proyecto.eva.miagendafp.Fragments.Diario;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
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

import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendafp.R;
import es.proyecto.eva.miagendafp.VolleyController.AppController;

import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.fecha_seleccionada;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.id_dia_seleccionado;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.jornada_partida_seleccionada;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.hora_inicio_1_seleccionada;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.hora_fin_1_seleccionada;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.hora_inicio_2_seleccionada;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.hora_fin_2_seleccionada;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.descripcion_seleccionada;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.minuto_fin_1_seleccionado;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.minuto_fin_2_seleccionado;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.minuto_inicio_1_seleccionado;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.minuto_inicio_2_seleccionado;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.valoracion_seleccionada;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.reunion_fct_seleccionada;
import static es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment.horas_reunion_seleccionada;
// ****************** PUBLICIDAD ************************
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
/***************************************************************************************************
 * Clase que permite la visualización y actualización de datos de un registro seleccionado del
 * listado de registros de diario del usuario activo
 **************************************************************************************************/
public class VerYEditarRegistroDiario extends AppCompatActivity {
    EditText txtFechaSeleccionada, txtHoraInicio1Seleccionada, txtHoraFin1Seleccionada, txtHoraInicio2Seleccionada, txtHoraFin2Seleccionada, txtDescripcionSeleccionada, txtTiempoReunion;
    ImageButton btnValoracionSeleccionadaBueno, btnValoracionSeleccionadaRegular, btnValoracionSeleccionadaMalo;
    Switch switchReunionSeleccionada, switchJornadaSeleccionada;
    LinearLayout bloqueTurno2, tiempoReunion;
    Button btnVerHoras;
    TextView turno1, turno2, txtHoras, txtInfoReunion, txtInfoReunion2; // para mostrar u ocultar los títulos que identifican a cada turno en caso de que haya varios
    private StringRequest request;
    private String descripcionNueva = "", valoracionNueva = valoracion_seleccionada;
    // Creamos variable de horas y minutos que serán las que usemos para obtener la hora seleccionada en el timepicker
    private int tpHoras, tpMinutos;
    // Creamos variables para almacenar las distintas horas introducidas
    private int horaInicio1 = 0, minutoInicio1 = 0, horaInicio2 = 0, minutoInicio2 = 0, horaFin1 = 0, minutoFin1 = 0, horaFin2 = 0, minutoFin2 = 0; //quizá no hagan falta las del turno 2, pero las ponemos por si acaso
    // creamos las horas de la jornada también en strings para guardarlas en la base de datos como texto
    private String sHoraInicio1 = "0", sMinInicio1 = "0", sHoraFin1 = "0", sMinFin1 = "0", sHoraInicio2 = "0", sMinInicio2 = "0", sHoraFin2 = "0", sMinFin2 = "0";
    // Creamos variables de horas y minutos para los turnos que serán el resultado de las restas de horas y minutos de arriba
    private int horasTurno1, minutosTurno1, horasTurno2, minutosTurno2;
    // Creamos horas y minutos que serán el producto de sumar las horas resultantes (horasTurno1 + horasTurno2) (minutosTurno1 + minutorTurno2)
    private int horasResultado, minutosResultado;
    private String sHorasResultado, sMinutosResultado; // son las dos anteriores pasadas a string, para poder guardarlas como texto en la base de datos

    // Creamos booleanos para validar desde donde se ha abierto el timepicker y guardar las cifras como correspondan
    private boolean esHoraInicio1 = false, esHoraInicio2 = false, esHoraFin1 = false, esHoraFin2 = false, hayDosJornadas = false; // por defecto en false
    private boolean verHoras = false; // valdrá para verificar que al llamar al método validarJornada, se está haciendo por haber pulsado este botón,
    // para así saber que se tiene que poner las horas en el textView que hay al lado del botón. Así no se pondrá siempre que se ejecute el método,
    // porque también se ejecuta al dar a guardar el registro
    private boolean hayReunion = false;
    private String jornada_partida = "0"; // por defecto en 0, que sería que no es partida
    private String reunion_fct = "0";
    private String horas_reunion = "0";
//    private String url_consulta = "http://192.168.0.12/MiAgenda/update_registro_diario.php";
 //   private String url_consulta2 = "http://192.168.0.12/MiAgenda/delete_registro_diario.php";
//    private String url_consulta = "http://192.168.0.159/MiAgenda/update_registro_diario.php";
    private String url_consulta = "http://miagendafp.000webhostapp.com/update_registro_diario.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/delete_registro_diario.php";
    // declaramos los nuevos datos del registro
    private String idUsuario = "";
    private boolean editando = false;
    private ProgressDialog progressDialog;

    // ******* PUBLICIDAD *******
    private AdView mAdView;
    /***********************************************************************************************
     * Método que codifica un dato que se le pase por parámetro para visualizar sus tildes y otros
     * caracteres especiales
     * @param dato
     * @return
     **********************************************************************************************/
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_y_editar_registro_diario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        // **************************** PUBLICIDAD *****************************************
// Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario
        // Mostramos el dato obtenido para verificar que es correcto
        //Log.d("VerYEditarRegistroD", "idUsuario obtenido: " + idUsuario);
        txtFechaSeleccionada = (EditText) findViewById(R.id.editText_fecha_seleccionada);
        txtDescripcionSeleccionada = (EditText) findViewById(R.id.editText_descripcion_seleccionada);
        txtHoraInicio1Seleccionada = (EditText) findViewById(R.id.txt_hora_inicio_1_seleccionada);
        txtHoraFin1Seleccionada = (EditText) findViewById(R.id.txt_hora_fin_1_seleccionada);
        txtHoraInicio2Seleccionada = (EditText) findViewById(R.id.txt_hora_inicio_2_seleccionada);
        txtHoraFin2Seleccionada = (EditText) findViewById(R.id.txt_hora_fin_2_seleccionada);
        btnValoracionSeleccionadaBueno = (ImageButton) findViewById(R.id.btn_bueno_seleccionado);
        btnValoracionSeleccionadaRegular = (ImageButton) findViewById(R.id.btn_regular_seleccionado);
        btnValoracionSeleccionadaMalo = (ImageButton) findViewById(R.id.btn_malo_seleccionado);
        switchReunionSeleccionada = (Switch) findViewById(R.id.switch_reunion_seleccionada);
        switchJornadaSeleccionada = (Switch) findViewById(R.id.switch_jornada_seleccionada);
        btnVerHoras = (Button) findViewById(R.id.btn_ver_horas); // obtiene las horas resultantes de las jornadas introducidas
        turno1 = (TextView) findViewById(R.id.tv_turno1_seleccionado);
        turno2 = (TextView) findViewById(R.id.tv_turno2_seleccionado);
        bloqueTurno2 = (LinearLayout) findViewById(R.id.bloque_turno_2_seleccionado);
        tiempoReunion = (LinearLayout) findViewById(R.id.tiempo_reunion);
        txtTiempoReunion = (EditText) findViewById(R.id.txt_horas_reunion_seleccionada);
        txtInfoReunion = (TextView) findViewById(R.id.txt_info_reunion_seleccionada);
        txtInfoReunion2 = (TextView) findViewById(R.id.txt_info_reunion_seleccionada_2);
        txtHoras = (TextView) findViewById(R.id.txt_horas_obtenidas);
        btnVerHoras.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                verHoras = true;
                validarJornada();
            }
        });
        // selecionamos o deseleccionamos en función de si el día tiene o no reunión
        if (Integer.valueOf(reunion_fct_seleccionada) == 1){
            switchReunionSeleccionada.setChecked(true);
            System.out.println("REUNIÓN ON");
            hayReunion = true;
            reunion_fct = "1";
            tiempoReunion.setVisibility(View.VISIBLE);
            txtInfoReunion.setVisibility(View.GONE);
            txtTiempoReunion.setText(horas_reunion_seleccionada);
        } else {
            switchReunionSeleccionada.setChecked(false);
            System.out.println("REUNIÓN OFF");
            hayReunion = false;
            reunion_fct = "0";
            tiempoReunion.setVisibility(View.GONE);
        }
        // y lo mismo con la jornada partida
        if (Integer.valueOf(jornada_partida_seleccionada) == 1){
            switchJornadaSeleccionada.setChecked(true);
            System.out.println("TURNO PARTIDO ON");
            // Activamos la visualización de campos
            turno1.setVisibility(View.VISIBLE);
            turno2.setVisibility(View.VISIBLE);
            bloqueTurno2.setVisibility(View.VISIBLE);
            hayDosJornadas = true;
            jornada_partida = "1"; // cambiamos a 1 para guardarlo en la base de datos
        } else {
            switchJornadaSeleccionada.setChecked(false);
            System.out.println("TURNO PARTIDO OFF");
            // Desactivamos la visualización de campos
            turno1.setVisibility(View.GONE);
            turno2.setVisibility(View.GONE);
            bloqueTurno2.setVisibility(View.GONE);
            hayDosJornadas = false;
            jornada_partida = "0"; // cambiamos a 0 para guardarlo en la base de datos
        }
        // obtenemos valores de los switch
        // Comprobamos el estado del selector de turno partido
        switchJornadaSeleccionada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        System.out.println("REUNION FCT = "+ reunion_fct);
        // Comprobamos el estado del selector de reunión fct
        switchReunionSeleccionada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){ // si se activa el selector
                    System.out.println("REUNIÓN ON");
                    hayReunion = true;
                    reunion_fct = "1";
                    tiempoReunion.setVisibility(View.VISIBLE);
                    txtInfoReunion.setVisibility(View.GONE);
                    txtInfoReunion2.setVisibility(View.VISIBLE);
                    txtTiempoReunion.setText(horas_reunion_seleccionada);
                } else if (!isChecked){ // si se desactiva
                    System.out.println("REUNIÓN OFF");
                    hayReunion = false;
                    tiempoReunion.setVisibility(View.GONE);
                    if (reunion_fct_seleccionada.equals("1")) { // validamos que solo se muestre que se van a restar las horas si el registro tenía horas. Si no tiene
                        // horas puestas, no es necesario que se indique nada porque no habría ninguna hora que restar
                        txtInfoReunion.setVisibility(View.VISIBLE);
                        reunion_fct = "0";
                        txtInfoReunion2.setVisibility(View.GONE);
                        horas_reunion = "0";
                    } else {
                        tiempoReunion.setVisibility(View.GONE);
                        reunion_fct = "0";
                        txtInfoReunion2.setVisibility(View.GONE);
                        horas_reunion = "0";
                    }

                }

            }
        });

        sHoraInicio1 = hora_inicio_1_seleccionada;
        sMinInicio1 = minuto_inicio_1_seleccionado;
        sHoraFin1 = hora_fin_1_seleccionada;
        sMinFin1 = minuto_fin_1_seleccionado;
        sHoraInicio2 = hora_inicio_2_seleccionada;
        sMinInicio2 = minuto_inicio_2_seleccionado;
        sHoraFin2 = hora_fin_2_seleccionada;
        sMinFin2 = minuto_fin_2_seleccionado;
        horaInicio1 = Integer.valueOf(hora_inicio_1_seleccionada);
        minutoInicio1 = Integer.valueOf(minuto_inicio_1_seleccionado);
        horaFin1 = Integer.valueOf(hora_fin_1_seleccionada);
        minutoFin1 = Integer.valueOf(minuto_fin_1_seleccionado);
        horaInicio2 = Integer.valueOf(hora_inicio_2_seleccionada);
        minutoInicio2 = Integer.valueOf(minuto_inicio_2_seleccionado);
        horaFin2 = Integer.valueOf(hora_fin_2_seleccionada);
        minutoFin2 = Integer.valueOf(minuto_fin_2_seleccionado);
        horas_reunion = horas_reunion_seleccionada;

        // PONEMOS DATOS del registro seleccionado en sus correspondientes campos
        // Fijamos los datos que queremos que se muestren
        txtFechaSeleccionada.setText(fecha_seleccionada);
        txtHoraInicio1Seleccionada.setText(sHoraInicio1 + ":" + sMinInicio1);
        txtHoraFin1Seleccionada.setText(sHoraFin1 + ":" + sMinFin1);
        txtHoraInicio2Seleccionada.setText(sHoraInicio2 + ":" + sMinInicio2);
        txtHoraFin2Seleccionada.setText(sHoraFin2 + ":" + sMinFin2);
        txtDescripcionSeleccionada.setText(descripcion_seleccionada);
        //  Inhabilitamos edición de campos
        deshabilitarEdicion();
        // marcamos el icono que se corresponda con la valoración del día
        if (valoracion_seleccionada.equals("Bueno")) {
            btnValoracionSeleccionadaBueno.setAlpha(1f);
        } else if (valoracion_seleccionada.equals("Regular")) {
            btnValoracionSeleccionadaRegular.setAlpha(1f);
        } else if (valoracion_seleccionada.equals("Malo")) {
            btnValoracionSeleccionadaMalo.setAlpha(1f);
        }
    }

    /***********************************************************************************************
     * Crea el menú de opciones de la barra de acciones
     * @param menu
     * @return
     **********************************************************************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edicion_actualizacion, menu); // la R referencia a la ubicación del archivo
        if (editando) { // si estamos en modo edición, habilitamos el icono de guardado y ocultamos el de editar
            //Log.d("VerYEditarRegistroD", "Modo edición: ocultamos icono editar y mostramos el de guardar");
            menu.findItem(R.id.menu_actualizar).setVisible(true);
            menu.findItem(R.id.menu_cancelar).setVisible(true);
            menu.findItem(R.id.menu_editar).setVisible(false);
            menu.findItem(R.id.menu_borrar).setVisible(false);
        }
        return true;
    }

    /***********************************************************************************************
     *     Opciones del menú de la barra de acciones
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_actualizar: // Opción de guardar el registro actualizado
                txtFechaSeleccionada.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                txtHoraInicio1Seleccionada.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                txtHoraFin1Seleccionada.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                txtHoraInicio2Seleccionada.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                txtHoraFin2Seleccionada.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                txtTiempoReunion.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                txtDescripcionSeleccionada.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);// /Log.i("VerYEditarRegistroD", "Action Guardar (actualizar) registro");
                // obtenemos los datos nuevos
                descripcionNueva = txtDescripcionSeleccionada.getText().toString();
                validarJornada();
                return true;
            case R.id.menu_editar: // Opción de editar el registro
                //Log.i("VerYEditarRegistroD", "Action Editar registro");
                modoEditar(); // entramos en "modo edición", habilitamos campos para escribir en ellos
                return true;
            case R.id.menu_cancelar:
                editando = false;
                deshabilitarEdicion();
                invalidateOptionsMenu(); // llamamos otra vez para quitar el icono de guardado
                return true;
            case R.id.menu_borrar: //Opción de borrar el registro
                //Log.i("VerYEditarRegistroD", "Action Borrar registro");
                borrarRegistro();
                return true;
            case android.R.id.home: // Opción de volver hacia atrás
                //Log.i("VerYEditarRegistroD", "Action Atrás");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Método que deshabilita la edición de campos para que no se puedan cambiar datos del registro
     **********************************************************************************************/
    public void deshabilitarEdicion(){
        // ************************* Inhabilitamos posibilidad de seleccionar/editar campos *****************************
        txtDescripcionSeleccionada.setEnabled(false);
        txtTiempoReunion.setEnabled(false);
        txtFechaSeleccionada.setEnabled(false);
        txtHoraInicio1Seleccionada.setEnabled(false);
        txtHoraFin1Seleccionada.setEnabled(false);
        txtHoraInicio2Seleccionada.setEnabled(false);
        txtHoraFin2Seleccionada.setEnabled(false);
        btnValoracionSeleccionadaBueno.setClickable(false);
        btnValoracionSeleccionadaMalo.setClickable(false);
        btnValoracionSeleccionadaRegular.setClickable(false);
        // *************************************************************************************************************
       // Esto es necesario ya que una vez se ha pulsado la edición, luego los campos de hora y fecha se quedan 'clickables'
        txtFechaSeleccionada.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    // no se hace nada
                }
                return true;
            }
        });
        // Muestra el timepicker para elegir las horas correspondientes al tocar alguno de los campos
        // de horas
        txtHoraInicio1Seleccionada.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    // nada
                }
                return true;
            }
        });
        txtHoraInicio2Seleccionada.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    // nada
                }
                return true;
            }
        });
        txtHoraFin1Seleccionada.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    // nada
                }
                return true;
            }
        });
        txtHoraFin2Seleccionada.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if(MotionEvent.ACTION_UP == event.getAction()) {
                   // nada
                }
                return true;
            }
        });
    }

    /***********************************************************************************************
     * Método que habilita la edición de campos para actualizar datos del registro seleccionado
     **********************************************************************************************/
    public void modoEditar() {
        invalidateOptionsMenu(); // para llamar de nuevo al onCreateOptionsMenu y ocultar el botón de editar
        // y mostrar el de guardar
        editando = true;
        txtDescripcionSeleccionada.setEnabled(true);
        txtTiempoReunion.setEnabled(true);
        txtHoraInicio1Seleccionada.setEnabled(true);
        txtHoraFin1Seleccionada.setEnabled(true);
        txtHoraInicio2Seleccionada.setEnabled(true);
        txtHoraFin2Seleccionada.setEnabled(true);

        // Al pulsar sobre el campo de fecha mostramos mensaje de que no se puede cambiar la fecha
        txtFechaSeleccionada.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // para que no se abra dos veces
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_editar_fecha_registro, Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        // Muestra el timepicker para elegir las horas correspondientes al tocar alguno de los campos
        // de horas
        txtHoraInicio1Seleccionada.setOnTouchListener(new View.OnTouchListener() {
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
        txtHoraInicio2Seleccionada.setOnTouchListener(new View.OnTouchListener() {
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
        txtHoraFin1Seleccionada.setOnTouchListener(new View.OnTouchListener() {
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
        txtHoraFin2Seleccionada.setOnTouchListener(new View.OnTouchListener() {
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
        btnValoracionSeleccionadaBueno.setClickable(true);
        btnValoracionSeleccionadaRegular.setClickable(true);
        btnValoracionSeleccionadaMalo.setClickable(true);
        btnValoracionSeleccionadaBueno.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnValoracionSeleccionadaBueno.setAlpha(1f); // opaco
                btnValoracionSeleccionadaRegular.setAlpha(0.5f); // semitransparente
                btnValoracionSeleccionadaMalo.setAlpha(0.5f); // "
                valoracionNueva = "Bueno";
            }
        });
        btnValoracionSeleccionadaRegular.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnValoracionSeleccionadaRegular.setAlpha(1f);
                btnValoracionSeleccionadaBueno.setAlpha(0.5f);
                btnValoracionSeleccionadaMalo.setAlpha(0.5f);
                valoracionNueva = "Regular";
            }
        });
        btnValoracionSeleccionadaMalo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnValoracionSeleccionadaMalo.setAlpha(1f);
                btnValoracionSeleccionadaBueno.setAlpha(0.5f);
                btnValoracionSeleccionadaRegular.setAlpha(0.5f);
                valoracionNueva = "Malo";
            }
        });
    }

    /***********************************************************************************************
     * Método que elimina el registro seleccionado, preguntando previamente si se desea realizar la
     * operación
     **********************************************************************************************/
    public void borrarRegistro(){
        // Preguntamos antes de borrar definitivamente
        AlertDialog.Builder builder = new AlertDialog.Builder(VerYEditarRegistroDiario.this);
        builder.setTitle(R.string.dialog_borrar_registro); // titulo del diálogo
        builder.setMessage(R.string.dialog_texto_borrar_registro)
                .setPositiveButton(R.string.dialog_opcion_borrar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        request = new StringRequest(Request.Method.POST, url_consulta2,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(VerYEditarRegistroDiario.this, R.string.toast_registro_eliminado, Toast.LENGTH_LONG).show();
                                        finish(); // cerramos la actividad para volver al fragmento con el listado de registros
                                        //Log.d("VerYEditarRegistroDiario", "Registro borrado");

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                                      //  Snackbar.make(findViewById(android.R.id.content),
                                        //        R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                                        //Log.e("VerYEditarRegistroDiario", "Error al conectar con el servidor para borrar el registro seleccionado");
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> parametros = new HashMap<>();
                                parametros.put("idDia", id_dia_seleccionado);
                                return parametros;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(request);
                    }
                })
                .setNegativeButton(R.string.respuesta_dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //no hacemos nada, y al pulsar el botón simplemente se cerrará el diálogo
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
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
                Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_datos_jornada_2, Toast.LENGTH_SHORT).show();
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
                     Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                    System.out.println("JORNADA(S) NO VÁLIDA(S) 1.");
                    //Snackbar.make(this.findViewById(android.R.id.content),
                      //      R.string.error_datos_jornada_4, Snackbar.LENGTH_SHORT).show();
                } else { // los datos son válidos, continuamos validando
                    if (horaInicio2 <= horaFin1) {// || horaInicio2 == horaFin1 && minutoInicio2 <= minutoFin1){ <--- Comento porque no sé si será posible en algún convenio hacer un descanso entre turno y turno inferior a una hora, que sería el único supuesto en el que coincidirían las horas de fin e inicio
                        // la hora de inicio del segundo turno es menor o igual que la de fin del primer turno. No puede ser.
                        System.out.println("JORNADA(S) NO VÁLIDA(S) 2.");
                        Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                        //Snackbar.make(this.findViewById(android.R.id.content),
                           //     R.string.error_datos_jornada_4, Snackbar.LENGTH_LONG).show();
                    } else if (horasResultado <= 0 || horaInicio1 == horaFin1 && minutoInicio1 == minutoFin1 || horaInicio2 == horaFin2 && minutoInicio2 == minutoFin2) { // si diese un número negativo de horas, o que las horas obtenidas sean 0
                        Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                        System.out.println("JORNADA(S) NO VÁLIDA(S) 3.");
                        //Snackbar.make(this.findViewById(android.R.id.content),
                          //      R.string.error_datos_jornada_4, Snackbar.LENGTH_SHORT).show();
                    } else if (horasResultado > 8) { // si da una jornada total mayor a 8 horas estaría superando la jornada permitida
                        Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_datos_jornada_5, Toast.LENGTH_LONG).show();
                        System.out.println("JORNADA(S) NO VÁLIDA(S) 4.");
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
                                Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_horas_reunion, Toast.LENGTH_SHORT).show();
                               // Snackbar.make(findViewById(android.R.id.content),
                                 //       R.string.error_horas_reunion, Snackbar.LENGTH_SHORT).show();
                            } else { // SÍ HAY HORAS DE REUNIÓN, validamos formato...
                                if (Integer.valueOf(horas_reunion) == 0){
                                    System.out.println("ERROR HORAS REUNIÓN 1.");
                                    Toast.makeText(VerYEditarRegistroDiario.this, R.string.alert_horas_reunion_2, Toast.LENGTH_SHORT).show();
                                    //Snackbar.make(findViewById(android.R.id.content),
                                      //      R.string.alert_horas_reunion_2, Snackbar.LENGTH_SHORT).show();
                                } else if (Integer.valueOf(horas_reunion) > 2){
                                    System.out.println("ERROR HORAS REUNIÓN 2.");
                                    Toast.makeText(VerYEditarRegistroDiario.this, R.string.alert_horas_reunion, Toast.LENGTH_SHORT).show();
                                    //Snackbar.make(findViewById(android.R.id.content),
                                      //      R.string.alert_horas_reunion, Snackbar.LENGTH_SHORT).show();
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
                                        actualizarRegistro();
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
                                actualizarRegistro();
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
                    Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                    System.out.println("JORNADA NO VÁLIDA 1.");
                   // Snackbar.make(this.findViewById(android.R.id.content),
                     //       R.string.error_datos_jornada_4, Snackbar.LENGTH_SHORT).show();
                } else { // los datos son válidos, continuamos validando
                    if (horasResultado <= 0) { // si diese un número negativo de horas, o que las horas obtenidas sean 0
                         Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_datos_jornada_4, Toast.LENGTH_SHORT).show();
                        System.out.println("JORNADA NO VÁLIDA 2.");
                       // Snackbar.make(this.findViewById(android.R.id.content),
                         //       R.string.error_datos_jornada_4, Snackbar.LENGTH_SHORT).show();
                    } else if (horasResultado > 8) { // si da una jornada total mayor a 8 horas estaría superando la jornada permitida
                        Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_datos_jornada_5, Toast.LENGTH_LONG).show();
                        System.out.println("JORNADA NO VÁLIDA 3.");
                        //Snackbar.make(findViewById(android.R.id.content),
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
                                Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_horas_reunion, Toast.LENGTH_SHORT).show();
                               // Snackbar.make(findViewById(android.R.id.content),
                                 //       R.string.error_horas_reunion, Snackbar.LENGTH_SHORT).show();
                            } else {
                                if (Integer.valueOf(horas_reunion) == 0){
                                    System.out.println("ERROR HORAS REUNIÓN 2 (UNA JORNADA).");
                                    Toast.makeText(VerYEditarRegistroDiario.this, R.string.alert_horas_reunion_2, Toast.LENGTH_SHORT).show();
                                   // Snackbar.make(findViewById(android.R.id.content),
                                     //       R.string.alert_horas_reunion_2, Snackbar.LENGTH_SHORT).show();
                                } else if (Integer.valueOf(horas_reunion) > 2){
                                    System.out.println("ERROR HORAS REUNIÓN 3 (UNA JORNADA).");
                                    Toast.makeText(VerYEditarRegistroDiario.this, R.string.alert_horas_reunion, Toast.LENGTH_SHORT).show();
                                   // Snackbar.make(findViewById(android.R.id.content),
                                     //       R.string.alert_horas_reunion, Snackbar.LENGTH_SHORT).show();
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
                                        actualizarRegistro();
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
                                actualizarRegistro();
                            }
                        }
                    }
                }
            } else { // los CAMPOS de la jornada base están EN BLANCO
                Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_datos_jornada_1, Toast.LENGTH_SHORT).show();
                System.out.println("ERROR DATOS JORNADA EN BLANCO.");
             //   Snackbar.make(this.findViewById(android.R.id.content),
               //         R.string.error_datos_jornada_1, Snackbar.LENGTH_SHORT).show();
            }
        }
        verHoras = false;
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
            // VERSIONES CON ANDROID 6.0 EN ADELANTE
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                timePicker.setHour(Integer.valueOf(sHoraInicio1));
                timePicker.setMinute(Integer.valueOf(sMinInicio1));
            } else {
                timePicker.setCurrentHour(Integer.valueOf(sHoraInicio1));
                timePicker.setCurrentMinute(Integer.valueOf(sMinInicio1));
            }
        } else if (esHoraFin1){
            // VERSIONES CON ANDROID 6.0 EN ADELANTE
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                timePicker.setHour(Integer.valueOf(sHoraFin1));
                timePicker.setMinute(Integer.valueOf(sMinFin1));
            } else {
                timePicker.setCurrentHour(Integer.valueOf(sHoraFin1));
                timePicker.setCurrentMinute(Integer.valueOf(sMinFin1));
            }
        } else if (esHoraInicio2){
            if (!sHoraInicio2.equals("0")) {
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
            if (!sHoraFin2.equals("0")) {
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
                // Validamos desde donde se abre para saber donde guardar los datos obtenidos
                if (esHoraInicio1){
                    horaInicio1 = tpHoras;
                    minutoInicio1 = tpMinutos;
                    sHoraInicio1 = sHoras;
                    sMinInicio1 = sMinutos;
                    System.out.println("MINUTOS: "+ sMinutos);
                    txtHoraInicio1Seleccionada.setText(sHoras + ":" + sMinutos);
                } else if (esHoraInicio2){
                    horaInicio2 = tpHoras;
                    minutoInicio2 = tpMinutos;
                    sHoraInicio2 = sHoras;
                    sMinInicio2 = sMinutos;
                    System.out.println("MINUTOS: "+ sMinutos);
                    txtHoraInicio2Seleccionada.setText(sHoras + ":" + sMinutos);
                } else if (esHoraFin1){
                    horaFin1 = tpHoras;
                    minutoFin1 = tpMinutos;
                    sHoraFin1 = sHoras;
                    sMinFin1 = sMinutos;
                    System.out.println("MINUTOS: "+ sMinutos);
                    txtHoraFin1Seleccionada.setText(sHoras + ":" + sMinutos);
                } else if (esHoraFin2){
                    horaFin2 = tpHoras;
                    minutoFin2 = tpMinutos;
                    sHoraFin2 = sHoras;
                    sMinFin2 = sMinutos;
                    System.out.println("MINUTOS: "+ sMinutos);
                    txtHoraFin2Seleccionada.setText(sHoras + ":" + sMinutos);
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
                esHoraFin1 = false;
                esHoraInicio2 = false;
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

    /***********************************************************************************************
     * Método que deshabilita la edición de campos para solo poder visualizarlos, y guarda los
     *  nuevos datos introducidos actualizando los datos del registro
     **********************************************************************************************/
    public void actualizarRegistro() {
        // validamos que no queden campos en blanco
        if (descripcionNueva.isEmpty() || valoracionNueva.isEmpty()) {
            Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            //Snackbar.make(this.findViewById(android.R.id.content),
              //      R.string.error_campos_vacios, Snackbar.LENGTH_LONG).show();
            Log.d("actualizarRegistro","descripcion/valoracion VACÍO");
        } else {
            // Deshabilitamos la edición de t0dos los campos
            editando = false;
            //Log.i("VerYEditarRegistroD", "Edición de campos deshabilitada");
            deshabilitarEdicion();
            // consulta volley para guardar datos
            System.out.println("Actualizando registro...");
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.dialog_cargando);
            progressDialog.setMessage("Actualizando registro...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            request = new StringRequest(Request.Method.POST, url_consulta,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("1")) {
                                progressDialog.dismiss();
                                Toast.makeText(VerYEditarRegistroDiario.this, R.string.toast_cambios_guardados, Toast.LENGTH_LONG).show();
                                //Snackbar.make(findViewById(android.R.id.content),
                                  //      R.string.toast_cambios_guardados, Snackbar.LENGTH_LONG).show();
                                System.out.println("DATOS A GUARDAR: HORA INICIO 1: " + sHoraInicio1 + "\n MINUTO INICIO 1: " + sMinInicio1 + "\n HORA FIN 1: " + sHoraFin1 + "\n MINUTO FIN 1: " + sMinFin1 + "\n HORA INICIO 2: " + sHoraInicio2
                                + "\n MINUTO INICIO 2: "+ sMinInicio2 + "\n HORA FIN 2: " + sHoraFin2 + "\n MINUTO FIN 2: " + sMinFin2);
                                //Log.d("VerYEditarRegistroDiario", "Registro actualizado");
                                invalidateOptionsMenu(); // llamamos otra vez para quitar el icono de guardado una vez que se ha guardado correctamente
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_actualizar_registro, Toast.LENGTH_LONG).show();
                               // Snackbar.make(findViewById(android.R.id.content),
                                 //       R.string.error_actualizar_registro, Snackbar.LENGTH_LONG).show();
                                //Log.e("VerYEditarRegistroDiario", "Error: No se ha obtenido la respuesta esperada del script para actualizar el registro");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                           // Snackbar.make(findViewById(android.R.id.content),
                             //       R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                            //Log.e("VerYEditarRegistroDiario", "Error al conectar con el servidor para actualizar el registro");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
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
                    parametros.put("descripcion", descripcionNueva);
                    parametros.put("valoracion", valoracionNueva);
                    parametros.put("horas",sHorasResultado);
                    parametros.put("minutos", sMinutosResultado);
                    parametros.put("idUsuario", idUsuario);
                    parametros.put("idDia", id_dia_seleccionado);
                    return parametros;
                }
            };
            AppController.getInstance().addToRequestQueue(request);
        }
    }


    /**************************************************************************************************
     * Método que decide se ejecuta cuando se quiera volver atrás, bien con el botón del dispositivo,
     * bien con el botón virtual de la barra de acciones de la aplicación
     *************************************************************************************************/
    @Override
    public void onBackPressed(){
        if (editando) { // Si se está editando (no se ha dado a guardar) y se pulsa Atrás, se pregunta si se está seguro
            AlertDialog.Builder builder = new AlertDialog.Builder(VerYEditarRegistroDiario.this);
            builder.setTitle(R.string.titulo_dialog_salir_sin_guardar); // titulo del diálogo
            builder.setMessage(R.string.contenido_dialog_salir_editar_sin_guardar)
                    .setPositiveButton(R.string.respuesta_dialog_volver, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();// cerramos la actividad actual
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
        } else { // Si no se está editando o ya se ha guardado, se vuelve atrás
            finish(); // cerramos la actividad actual
        }
    }
}
