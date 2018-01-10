package es.proyecto.eva.miagendadam.Fragments.Diario;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

import static es.proyecto.eva.miagendadam.Fragments.Diario.DiarioFragment.fecha_seleccionada;
import static es.proyecto.eva.miagendadam.Fragments.Diario.DiarioFragment.id_dia_seleccionado;
import static es.proyecto.eva.miagendadam.Fragments.Diario.DiarioFragment.horas_seleccionadas;
import static es.proyecto.eva.miagendadam.Fragments.Diario.DiarioFragment.minutos_seleccionados;
import static es.proyecto.eva.miagendadam.Fragments.Diario.DiarioFragment.descripcion_seleccionada;
import static es.proyecto.eva.miagendadam.Fragments.Diario.DiarioFragment.valoracion_seleccionada;


/***************************************************************************************************
 * Clase que permite la visualización y actualización de datos de un registro seleccionado del
 * listado de registros de diario del usuario activo
 **************************************************************************************************/
public class VerYEditarRegistroDiario extends AppCompatActivity {
    EditText txtFechaSeleccionada, txtHorasSeleccionadas, txtMinutosSeleccionados, txtDescripcionSeleccionada;
    ImageButton btnValoracionSeleccionadaBueno, btnValoracionSeleccionadaRegular, btnValoracionSeleccionadaMalo;
    private StringRequest request;

//    private String url_consulta = "http://192.168.0.12/MiAgenda/update_registro_diario.php";
 //   private String url_consulta2 = "http://192.168.0.12/MiAgenda/delete_registro_diario.php";
//    private String url_consulta = "http://192.168.0.159/MiAgenda/update_registro_diario.php";
    private String url_consulta = "http://miagendafp.000webhostapp.com/update_registro_diario.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/delete_registro_diario.php";
    // declaramos los nuevos datos del registro
    private String fechaNueva = "", horasNuevas = "", minutosNuevos = "", descripcionNueva = "", valoracionNueva = "";
    String idUsuario = "";
    boolean editando = false;
    public static boolean actualizaDiario = false; // la usamos para lo siguiente:
    // Este dato se pondrá en true cuando se haya editado un registro y se hayan guardado los cambios. Nos servirá para indicar
    // al sistema que es necesario que vuelva a cargar los datos porque ha habido cambios. Así, desde el fragmento, si se detecta
    // que esto está en true, se volverán a ejecutar los métodos de obtención y carga de datos.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_y_editar_registro_diario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario
        txtFechaSeleccionada = (EditText) findViewById(R.id.editText_fecha_seleccionada);
        txtHorasSeleccionadas = (EditText) findViewById(R.id.editText_horas_seleccionadas);
        txtMinutosSeleccionados = (EditText) findViewById(R.id.editText_minutos_seleccionados);
        txtDescripcionSeleccionada = (EditText) findViewById(R.id.editText_descripcion_seleccionada);
        btnValoracionSeleccionadaBueno = (ImageButton) findViewById(R.id.btn_bueno_seleccionado);
        btnValoracionSeleccionadaRegular = (ImageButton) findViewById(R.id.btn_regular_seleccionado);
        btnValoracionSeleccionadaMalo = (ImageButton) findViewById(R.id.btn_malo_seleccionado);
        // Fijamos los datos que queremos que se muestren
        txtFechaSeleccionada.setText(fecha_seleccionada);
        txtDescripcionSeleccionada.setText(descripcion_seleccionada);
        txtHorasSeleccionadas.setText(horas_seleccionadas);
        txtMinutosSeleccionados.setText(minutos_seleccionados);
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
        getMenuInflater().inflate(R.menu.menu_editar, menu); // la R referencia a la ubicación del archivo
        if (editando) { // si estamos en modo edición, habilitamos el icono de guardado
            menu.findItem(R.id.menu_actualizar).setVisible(true);
            menu.findItem(R.id.menu_editar).setVisible(false);
        }
        return true; // .menu es el directorio, y .menu_editar la capa
    }

    /***********************************************************************************************
     *     Opciones del menú de la barra de acciones
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_actualizar: // Opción de guardar el registro actualizado
                actualizarRegistro();
                return true;
            case R.id.menu_editar: // Opción de editar el registro
                modoEditar(); // entramos en "modo edición", habilitamos campos para escribir en ellos
                return true;
            case R.id.menu_borrar: //Opción de borrar el registro
                borrarRegistro();
                return true;
            case android.R.id.home: // Opción de volver hacia atrás
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Método que habilita la edición de campos para actualizar datos del registro seleccionado
     **********************************************************************************************/
    public void modoEditar() {
        invalidateOptionsMenu(); // para llamar de nuevo al onCreateOptionsMenu y ocultar el botón de editar
        // y mostrar el de guardar
        editando = true;
        System.out.println("EDITANDO = " + editando);
        txtFechaSeleccionada.setFocusableInTouchMode(true);
        txtHorasSeleccionadas.setFocusableInTouchMode(true);
        txtMinutosSeleccionados.setFocusableInTouchMode(true);
        txtDescripcionSeleccionada.setFocusableInTouchMode(true);
        btnValoracionSeleccionadaBueno.setClickable(true);
        btnValoracionSeleccionadaRegular.setClickable(true);
        btnValoracionSeleccionadaMalo.setClickable(true);
        btnValoracionSeleccionadaBueno.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnValoracionSeleccionadaBueno.setAlpha(1f); // opaco
                btnValoracionSeleccionadaRegular.setAlpha(0.5f); // semitransparente
                btnValoracionSeleccionadaMalo.setAlpha(0.5f); // "
                valoracion_seleccionada = "Bueno";
            }
        });
        btnValoracionSeleccionadaRegular.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnValoracionSeleccionadaRegular.setAlpha(1f);
                btnValoracionSeleccionadaBueno.setAlpha(0.5f);
                btnValoracionSeleccionadaMalo.setAlpha(0.5f);
                valoracion_seleccionada = "Regular";
            }
        });
        btnValoracionSeleccionadaMalo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnValoracionSeleccionadaMalo.setAlpha(1f);
                btnValoracionSeleccionadaBueno.setAlpha(0.5f);
                btnValoracionSeleccionadaRegular.setAlpha(0.5f);
                valoracion_seleccionada = "Malo";
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
                                        System.out.println("ID DEL DÍA A BORRAR: "+ id_dia_seleccionado);
                                        Toast.makeText(VerYEditarRegistroDiario.this, R.string.toast_registro_eliminado, Toast.LENGTH_LONG).show();
                                        finish(); // cerramos la actividad para volver al fragmento con el listado de registros
                                        System.out.println("Registro borrado!");

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();

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
     * Método que deshabilita la edición de campos para solo poder visualizarlos, y guarda los
     *  nuevos datos introducidos actualizando los datos del registro
     **********************************************************************************************/
    public void actualizarRegistro() {
        // obtenemos los datos nuevos
        fechaNueva = txtFechaSeleccionada.getText().toString();
        horasNuevas = txtHorasSeleccionadas.getText().toString();
        minutosNuevos = txtMinutosSeleccionados.getText().toString();
        descripcionNueva = txtDescripcionSeleccionada.getText().toString();
        valoracionNueva = valoracion_seleccionada;

        // validamos que no queden campos en blanco
        if (fechaNueva.isEmpty() || horasNuevas.isEmpty() || minutosNuevos.isEmpty() || descripcionNueva.isEmpty() || valoracionNueva.isEmpty()) {
            Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            System.out.println("DATOS: " + fechaNueva + " " + horasNuevas + " " + minutosNuevos + " " + descripcionNueva + " " + valoracionNueva);
        } else {
            editando = false;
            // deshabilitamos la edición
            txtFechaSeleccionada.setFocusable(false);
            txtHorasSeleccionadas.setFocusable(false);
            txtMinutosSeleccionados.setFocusable(false);
            txtDescripcionSeleccionada.setFocusable(false);
            btnValoracionSeleccionadaBueno.setClickable(false);
            btnValoracionSeleccionadaRegular.setClickable(false);
            btnValoracionSeleccionadaMalo.setClickable(false);

            // Obtenemos los nuevos datos:

            // consulta volley para guardar datos
            request = new StringRequest(Request.Method.POST, url_consulta,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("1")) {
                                Toast.makeText(VerYEditarRegistroDiario.this, R.string.toast_cambios_guardados, Toast.LENGTH_LONG).show();
                                System.out.println("Registro actualizado!");
                                invalidateOptionsMenu(); // llamamos otra vez para quitar el icono de guardado una vez que se ha guardado correctamente
                            } else {
                                Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_actualizar_registro, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("fecha", fechaNueva);
                    parametros.put("descripcion", descripcionNueva);
                    parametros.put("horas", horasNuevas);
                    parametros.put("minutos", minutosNuevos);
                    parametros.put("valoracion", valoracionNueva);
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
