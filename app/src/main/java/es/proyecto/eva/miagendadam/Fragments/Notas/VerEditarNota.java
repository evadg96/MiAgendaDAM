package es.proyecto.eva.miagendadam.Fragments.Notas;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

import static es.proyecto.eva.miagendadam.Fragments.Notas.NotasFragment.color_action_bar_seleccionado;
import static es.proyecto.eva.miagendadam.Fragments.Notas.NotasFragment.color_seleccionado;
import static es.proyecto.eva.miagendadam.Fragments.Notas.NotasFragment.contenido_seleccionado;
import static es.proyecto.eva.miagendadam.Fragments.Notas.NotasFragment.id_nota_seleccionada;


public class VerEditarNota extends AppCompatActivity {
    EditText txtContenido;
    LinearLayout fondoNota;
    StringRequest request;
    private String url_consulta = "http://miagendafp.000webhostapp.com/update_nota.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/delete_nota.php";
    int dia = 0, mes = 0, anyo = 0;
    String colorElegido = color_seleccionado;
    String colorActionBar = color_action_bar_seleccionado;
    private String diaEdicion = "", mesEdicion = "", anyoEdicion = "",
            fechaUltimaEdicion = "", contenido = "", idUsuario = "";
    AlertDialog.Builder alert;
    AlertDialog dialog;
    private boolean editando = false;
    Calendar c = new GregorianCalendar();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_editar_nota);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtContenido = (EditText) findViewById(R.id.txt_contenido_editar);
        fondoNota = (LinearLayout) findViewById(R.id.fondo_nota_editar);
        // Obtenemos de las preferencias el nombre del usuario
        SharedPreferences preferences = this.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario al que vamos a introducir la nota.
        deshabilitarEdicion();
        // Cargamos los valores correspondientes a la nota (contenido y color de fondo)
        txtContenido.setText(contenido_seleccionado);
        fondoNota.setBackgroundColor(Color.parseColor(color_seleccionado));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color_action_bar_seleccionado))); // cambiamos también el color de la barra action bar
        if (color_seleccionado.equals("#00000000") || color_seleccionado.equals("#cddc39") || color_seleccionado.equals("#ffeb3b") ||
                color_seleccionado.equals("#ffc107")){
            txtContenido.setTextColor(Color.BLACK);
        } else {
            txtContenido.setTextColor(Color.WHITE);
        }
    }

    /***********************************************************************************************
     * Método que deshabilita la edición de la nota
     **********************************************************************************************/
    public void deshabilitarEdicion(){
        //txtContenido.setEnabled(false);
        //txtContenido.setHorizontallyScrolling(true);
        txtContenido.setFocusable(false);
        txtContenido.setFocusableInTouchMode(false);
        txtContenido.setClickable(false);
        txtContenido.setLongClickable(false);
        editando = false;
        invalidateOptionsMenu();
    }

    /***********************************************************************************************
     * Método que habilita la edición y muestra el teclado automáticamente
     **********************************************************************************************/
    public void habilitarEdicion(){
        //txtContenido.setEnabled(true);
        txtContenido.setFocusable(true);
        txtContenido.setFocusableInTouchMode(true);
        txtContenido.setClickable(true);
        txtContenido.setLongClickable(true);
        // para desplegar automáticamente el teclado cuando se pulse el botón de editar la nota
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(txtContenido, InputMethodManager.SHOW_FORCED);
        editando = true;
        invalidateOptionsMenu();
    }

    /***********************************************************************************************
     * Crea el menú de opciones de la barra de acciones
     * @param menu
     * @return
     **********************************************************************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar_nota, menu);
            if (editando) { // si estamos en modo edición, habilitamos el icono de guardado y ocultamos el de editar
                //Log.d("VerYEditarRegistroD", "Modo edición: ocultamos icono editar y mostramos el de guardar");
                menu.findItem(R.id.menu_guardar).setVisible(true);
                menu.findItem(R.id.menu_color).setVisible(true);
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
            case R.id.menu_color: // Opción de seleccionar el color de la nota
                abrePaletaColores();
                return true;
            case R.id.menu_guardar: // Opción de guardar la nota
                // obtenemos la fecha actual cuando se pulse el botón de guardar
                dia = c.get(Calendar.DATE);
                mes = c.get(Calendar.MONTH);
                // sumamos uno al mes porque empieza en 0 (enero = 0, febrero = 1, marzo = 2...)
                mes = mes + 1;
                anyo = c.get(Calendar.YEAR);
                // pasamos los enteros a strings para guardarlos en la base de datos
                diaEdicion = String.valueOf(dia);
                anyoEdicion = String.valueOf(anyo);
                mesEdicion = String.valueOf(mes);
                String sMes = "";
                if (mes == 1){
                    sMes = "ene.";
                } else if (mes == 2){
                    sMes = "feb.";
                } else if (mes == 3){
                    sMes = "mar.";
                } else if (mes == 4){
                    sMes = "abr.";
                } else if (mes == 5){
                    sMes = "may.";
                } else if (mes == 6){
                    sMes = "jun.";
                } else if (mes == 7){
                    sMes = "jul.";
                } else if (mes == 8){
                    sMes = "ago.";
                } else if (mes == 9){
                    sMes = "sep.";
                } else if (mes == 10){
                    sMes = "oct.";
                } else if (mes == 11){
                    sMes = "nov.";
                } else if (mes == 12){
                    sMes = "dic.";
                }
                fechaUltimaEdicion = diaEdicion + " " + sMes + anyoEdicion;
                contenido = txtContenido.getText().toString();
                // validamos que el contenido de la nota no esté en blanco
                if (contenido.isEmpty()){
                    Toast.makeText(VerEditarNota.this, R.string.error_nota_vacia, Toast.LENGTH_SHORT).show();
                } else { // si no está en blanco, guardamos la nota
                    actualizarNota();
                }
                return true;
            case android.R.id.home: // Opción de volver hacia atrás
                //Log.i("VerYEditarRegistroD", "Action Atrás");
                onBackPressed();
                return true;
            case R.id.menu_borrar: // Opción de borrar nota
                //Log.i("VerYEditarRegistroD", "Action Atrás");
                borrarNota();
                return true;
            case R.id.menu_editar: // Opción de editar la nota
                //Log.i("VerYEditarRegistroD", "Action Atrás");
                habilitarEdicion();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Método que actualiza la nota seleccionada
     **********************************************************************************************/
    public void actualizarNota(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.dialog_cargando);
        progressDialog.setMessage("Actualizando contacto...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        // consulta para guardar la nota
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("1")) {
                            progressDialog.dismiss();
                            Toast.makeText(VerEditarNota.this, R.string.nota_actualizada, Toast.LENGTH_SHORT).show();
                            deshabilitarEdicion();
                            // finish(); // todo permanecer en la nota o volver atrás?
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(VerEditarNota.this, R.string.error_actualizar_nota, Toast.LENGTH_SHORT).show();
                            //Snackbar.make(this.findViewById(android.R.id.content),
                            //      R.string.error_no_hay_usuario, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(VerEditarNota.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        // Snackbar.make(getActivity().findViewById(android.R.id.content),
                        // R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idNota", id_nota_seleccionada);
                parametros.put("diaEdicion", diaEdicion);
                parametros.put("mesEdicion", mesEdicion);
                parametros.put("anyoEdicion", anyoEdicion);
                parametros.put("fechaUltimaEdicion", fechaUltimaEdicion);
                parametros.put("contenidoNota", contenido);
                parametros.put("colorNota", colorElegido);
                parametros.put("colorActionBar", colorActionBar);
                parametros.put("idUsuario", idUsuario);
                return parametros;
            }

        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /*****************************************************************************************************************
     * Método que abre un diálogo de selección de colores para lo que será el color de fondo de la nota a guardar
     ****************************************************************************************************************/
    public void abrePaletaColores(){
        alert = new AlertDialog.Builder(this);
        dialog = alert.create();
        LayoutInflater inflater = getLayoutInflater();
        //inflate view for alertdialog since we are using multiple views inside a viewgroup (root = Layout top-level) (linear, relative, framelayout etc..)
        View view = inflater.inflate(R.layout.paleta_colores, (ViewGroup) findViewById(R.id.dialogo_colores));

        final Button btnGranate, btnRojo, btnRosa, btnMorado, btnVioleta, btnAzulOscuro, btnAzul, btnCyan, btnTurquesa, btnAqua, btnVerdeMedio, btnVerde, btnLima, btnAmarillo,
                btnNaranjaSuave, btnNaranja, btnNaranjaFuerte, btnMarron, btnGrisClaro, btnGrisOscuro;
        Button btnAceptar, btnNoColor;

        btnGranate = (Button) view.findViewById(R.id.granate);
        btnRojo = (Button) view.findViewById(R.id.rojo);
        btnRosa = (Button) view.findViewById(R.id.rosa);
        btnMorado = (Button) view.findViewById(R.id.morado);
        btnVioleta = (Button) view.findViewById(R.id.violeta);
        btnAzulOscuro = (Button) view.findViewById(R.id.azul_oscuro);
        btnAzul = (Button) view.findViewById(R.id.azul);
        btnCyan = (Button) view.findViewById(R.id.cyan);
        btnTurquesa = (Button) view.findViewById(R.id.turquesa);
        btnAqua = (Button) view.findViewById(R.id.aqua);
        btnVerdeMedio = (Button) view.findViewById(R.id.verde_medio);
        btnVerde = (Button) view.findViewById(R.id.verde);
        btnLima = (Button) view.findViewById(R.id.lima);
        btnAmarillo = (Button) view.findViewById(R.id.amarillo);
        btnNaranjaSuave = (Button) view.findViewById(R.id.naranja_suave);
        btnNaranja = (Button) view.findViewById(R.id.naranja);
        btnNaranjaFuerte = (Button) view.findViewById(R.id.naranja_fuerte);
        btnMarron = (Button) view.findViewById(R.id.marron);
        btnGrisClaro = (Button) view.findViewById(R.id.gris_claro);
        btnGrisOscuro = (Button) view.findViewById(R.id.gris_oscuro);
        btnNoColor = (Button) view.findViewById(R.id.btn_sin_color);
        btnAceptar = (Button) view.findViewById(R.id.btn_aceptar);

        // Códigos de colores:
        final String granate = "#b71c1c", rojo = "#f44336", rosa = "#e91e8a", morado = "#673ab7", violeta = "#9c27b0", azul_oscuro = "#1a43b4", azul = "#03a9f4",
                cyan = "#00bcd4", turquesa = "#00acc1", aqua = "#009688", verde_medio = "#4caf50", verde = "#8bc34a", lima = "#cddc39", amarillo = "#ffeb3b",
                naranja_suave = "#ffc107", naranja = "#ff9800", naranja_fuerte = "#ff7322", marron = "#795548", gris_claro = "#9e9e9e", gris_oscuro = "#607d8b";


        btnGranate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // establecemos el fondo de color del botón al pulsarlo
                colorElegido = granate;
                colorActionBar = "#7b1313";
                btnGranate.setBackgroundColor(Color.parseColor("#FFB66D6D"));
                colorearNota();
            }
        });

        btnRojo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = rojo;
                colorActionBar = "#b33127";
                btnRojo.setBackgroundColor(Color.parseColor("#FFF1928B"));
                colorearNota();
            }
        });

        btnRosa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = rosa;
                colorActionBar = "#a0145e";
                btnRosa.setBackgroundColor(Color.parseColor("#FFE4AAC9"));
                colorearNota();
            }
        });

        btnMorado.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = morado;
                colorActionBar = "#422575";
                btnMorado.setBackgroundColor(Color.parseColor("#FF9C89BF"));
                colorearNota();
            }
        });

        btnVioleta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = violeta;
                colorActionBar = "#771e86";
                btnVioleta.setBackgroundColor(Color.parseColor("#FFBD91C4"));
                colorearNota();
            }
        });

        btnAzulOscuro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = azul_oscuro;
                colorActionBar = "#122e7a";
                btnAzulOscuro.setBackgroundColor(Color.parseColor("#FF7287C2"));
                colorearNota();
            }
        });

        btnAzul.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = azul;
                colorActionBar = "#0084c0";
                btnAzul.setBackgroundColor(Color.parseColor("#FF7ECDF1"));
                colorearNota();
            }
        });

        btnCyan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = cyan;
                colorActionBar = "#019aae";
                btnCyan.setBackgroundColor(Color.parseColor("#FF99DAE2"));
                colorearNota();
            }
        });

        btnTurquesa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = turquesa;
                colorActionBar = "#028797";
                btnTurquesa.setBackgroundColor(Color.parseColor("#FF84BEC6"));
                colorearNota();
            }
        });

        btnAqua.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = aqua;
                colorActionBar = "#006d63";
                btnAqua.setBackgroundColor(Color.parseColor("#FF77ABA6"));
                colorearNota();
            }
        });

        btnVerdeMedio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = verde_medio;
                colorActionBar = "#3a853d";
                btnVerdeMedio.setBackgroundColor(Color.parseColor("#FF9CC69E"));
                colorearNota();
            }
        });

        btnVerde.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = verde;
                colorActionBar = "#6f9b3c";
                btnVerde.setBackgroundColor(Color.parseColor("#FFBDE490"));
                colorearNota();
            }
        });

        btnLima.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = lima;
                colorActionBar = "#a4af2f";
                btnLima.setBackgroundColor(Color.parseColor("#FFDBE297"));
                colorearNota();
            }
        });

        btnAmarillo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = amarillo;
                colorActionBar = "#d3c233";
                btnAmarillo.setBackgroundColor(Color.parseColor("#FFF2E99B"));
                colorearNota();
            }
        });

        btnNaranjaSuave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = naranja_suave;
                colorActionBar = "#c29305";
                btnNaranjaSuave.setBackgroundColor(Color.parseColor("#FFFFD862"));
                colorearNota();
            }
        });

        btnNaranjaFuerte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = naranja_fuerte;
                colorActionBar = "#c3581b";
                btnNaranjaFuerte.setBackgroundColor(Color.parseColor("#FFFFA978"));
                colorearNota();
            }
        });

        btnNaranja.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = naranja;
                colorActionBar = "#d07c01";
                btnNaranja.setBackgroundColor(Color.parseColor("#FFFFC064"));
                colorearNota();
            }
        });

        btnMarron.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = marron;
                colorActionBar = "#5b4036";
                btnMarron.setBackgroundColor(Color.parseColor("#FF9F8177"));
                colorearNota();
            }
        });

        btnGrisClaro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = gris_claro;
                colorActionBar = "#797878";
                btnGrisClaro.setBackgroundColor(Color.parseColor("#FFC7C7C7"));
                colorearNota();
            }
        });

        btnGrisOscuro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = gris_oscuro;
                colorActionBar = "#3f525b";
                btnGrisOscuro.setBackgroundColor(Color.parseColor("#FF7B868B"));
                colorearNota();
            }

        });

        btnNoColor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorElegido = "#00000000";
                colorActionBar = "#2f867f";
                colorearNota();
            }
        });

        // ******* OCULTO *******
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Color elegido: " + colorElegido);
                colorearNota();
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    /***********************************************************************************************
     * Método que es llamado cuando se ha seleccionado un color (o no color) para la nota actual
     *  para cerrar el diálogo de selección de color y colorear la nota y la letra de su contenido
     *   en función del color que se haya seleccionado para contrastar adecuadamente
     **********************************************************************************************/
    public void colorearNota(){
        // cerramos diálogo
        dialog.cancel();
        fondoNota.setBackgroundColor(Color.parseColor(colorElegido));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorActionBar))); // cambiamos también el color de la barra action bar
        if (colorElegido.equals("#00000000") || colorElegido.equals("#cddc39") || colorElegido.equals("#ffeb3b") ||
                colorElegido.equals("#ffc107")){
            txtContenido.setTextColor(Color.BLACK);
        } else {
            txtContenido.setTextColor(Color.WHITE);
        }
    }

    /***********************************************************************************************
     * Método que se llama al pulsar el botón de ir hacia atrás
     **********************************************************************************************/
    public void onBackPressed(){
        if (editando) { // hacemos las siguientes validaciones SOLO si se está editando la nota. Si solo se está visualizando, volvemos atrás sin preguntar nada
            contenido = txtContenido.getText().toString();
            // comprobamos si se ha efectuado algún cambio. Si no se ha modificado nada de la nota, se vuelve sin preguntar
            // nada porque se asume que no se han hecho cambios
            if (contenido.equals(contenido_seleccionado)) { // si el contenido actual es igual que el que se había seleccionado de la nota, asumimos que no se ha cambiado nada
                editando = false;
                deshabilitarEdicion();
                invalidateOptionsMenu();
            } else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VerEditarNota.this);
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
            }
        } else {
            finish();
        }
    }

    /***********************************************************************************************
     * Método que borra la nota seleccionada
     **********************************************************************************************/
    public void borrarNota(){
        // Preguntamos antes de borrar definitivamente
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VerEditarNota.this);
        builder.setTitle(R.string.dialog_borrar_nota); // titulo del diálogo
        builder.setMessage(R.string.dialog_texto_borrar_nota)
                .setPositiveButton(R.string.dialog_opcion_borrar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // consulta para guardar la nota
                        request = new StringRequest(Request.Method.POST, url_consulta2,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.equals("1")) { // aseguramos que las preferencias no están vacías
                                            Toast.makeText(VerEditarNota.this, R.string.nota_borrada, Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(VerEditarNota.this, R.string.error_borrar_nota, Toast.LENGTH_SHORT).show();
                                            //Snackbar.make(this.findViewById(android.R.id.content),
                                            //      R.string.error_no_hay_usuario, Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(VerEditarNota.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                                        // Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        // R.string.error_servidor, Snackbar.LENGTH_SHORT).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> parametros = new HashMap<>();
                                parametros.put("idNota", id_nota_seleccionada);
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

}
