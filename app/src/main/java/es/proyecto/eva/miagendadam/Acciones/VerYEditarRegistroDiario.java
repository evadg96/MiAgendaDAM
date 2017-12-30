package es.proyecto.eva.miagendadam.Acciones;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import es.proyecto.eva.miagendadam.Fragments.DiarioFragment;
import es.proyecto.eva.miagendadam.NavMenu;
import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

import static es.proyecto.eva.miagendadam.Fragments.DiarioFragment.fecha_seleccionada;
import static es.proyecto.eva.miagendadam.Fragments.DiarioFragment.id_dia_seleccionado;
import static es.proyecto.eva.miagendadam.Fragments.DiarioFragment.horas_seleccionadas;
import static es.proyecto.eva.miagendadam.Fragments.DiarioFragment.minutos_seleccionados;
import static es.proyecto.eva.miagendadam.Fragments.DiarioFragment.descripcion_seleccionada;
import static es.proyecto.eva.miagendadam.Fragments.DiarioFragment.valoracion_seleccionada;


public class VerYEditarRegistroDiario extends AppCompatActivity {
    EditText txtFechaSeleccionada, txtHorasSeleccionadas, txtMinutosSeleccionados, txtDescripcionSeleccionada;
    ImageButton btnValoracionSeleccionadaBueno, btnValoracionSeleccionadaRegular, btnValoracionSeleccionadaMalo;
    private StringRequest request;
    private Menu menu;
    private String url_consulta = "http://192.168.0.12/MiAgenda/update_registro_diario.php";
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar, menu); // la R referencia a la ubicación del archivo
        if (editando) { // si estamos en modo edición, habilitamos el icono de guardado
            menu.findItem(R.id.menu_actualizar).setVisible(true);
            menu.findItem(R.id.menu_editar).setVisible(false);
        }
        return true; // .menu es el directorio, y .toolbar el archivo
    }

    /***********************************************************************************************
     *     Opciones del menú de la barra de acciones
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.menu_actualizar: // Opción de guardar el registro actualizado
                Log.i("ActionBar", "Guardar!");
                actualizarRegistro();
                return true;
            case R.id.menu_editar: // Opción de editar el registro
                Log.i("ActionBar", "Editar!");
                modoEditar(); // entramos en "modo edición", habilitamos campos para escribir en ellos
                return true;
            case android.R.id.home: // Opción de volver hacia atrás
                if (editando) { // Si se está editando (no se ha dado a guardar) y se pulsa Atrás, se pregunta si se está seguro
                    AlertDialog.Builder builder = new AlertDialog.Builder(VerYEditarRegistroDiario.this);
                    builder.setTitle(R.string.titulo_dialog_salir_sin_guardar); // titulo del diálogo
                    builder.setMessage(R.string.contenido_dialog_salir_editar_sin_guardar)
                            .setPositiveButton(R.string.respuesta_dialog_volver, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    onBackPressed(); // volvemos atrás
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
                    actualizaDiario = true; // para indicarle a la actividad NavMenu que queremos que recargue el fragmento
                    Intent intent = new Intent (this, NavMenu.class); // llamamos al  nav menu para refrescar el fragmento y
                    startActivity(intent);                                          // obtener los datos actualizados al volver
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Método que habilita la edición de campos para actualizar datos del registro seleccionado
     **********************************************************************************************/
    public void modoEditar() {
        invalidateOptionsMenu();
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
            Toast.makeText(VerYEditarRegistroDiario.this, "Debes completar todos los datos.", Toast.LENGTH_SHORT).show();
            System.out.println("DATOS: " + fechaNueva + " " + horasNuevas + " " + minutosNuevos + " " + descripcionNueva + " " + valoracionNueva);
        } else {
            editando = false;
            // deshabilitamos la edición
            txtFechaSeleccionada.setFocusableInTouchMode(false);
            txtHorasSeleccionadas.setFocusableInTouchMode(false);
            txtMinutosSeleccionados.setFocusableInTouchMode(false);
            txtDescripcionSeleccionada.setFocusableInTouchMode(false);
            btnValoracionSeleccionadaBueno.setClickable(false);
            btnValoracionSeleccionadaRegular.setClickable(false);
            btnValoracionSeleccionadaMalo.setClickable(false);

            // obtenemos los nuevos datos

            // consulta volley para guardar datos
            request = new StringRequest(Request.Method.POST, url_consulta,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("1")) {
                                Toast.makeText(VerYEditarRegistroDiario.this, "Registro actualizado con éxito.", Toast.LENGTH_LONG).show();
                                System.out.println("Registro actualizado!");
                                invalidateOptionsMenu(); // llamamos otra vez para quitar el icono de guardado una vez que se ha guardado correctamente
                            } else {
                                Toast.makeText(VerYEditarRegistroDiario.this, "Se ha producido un error. No se ha podido actualizar el registro.", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                            Toast.makeText(VerYEditarRegistroDiario.this, "Error al actualizar el registro.", Toast.LENGTH_SHORT).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
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
}
