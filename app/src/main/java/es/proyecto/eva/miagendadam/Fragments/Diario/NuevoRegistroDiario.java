package es.proyecto.eva.miagendadam.Fragments.Diario;

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

import es.proyecto.eva.miagendadam.NavMenu;
import es.proyecto.eva.miagendadam.VolleyController.AppController;
import es.proyecto.eva.miagendadam.R;
import static es.proyecto.eva.miagendadam.Fragments.Diario.VerYEditarRegistroDiario.actualizaDiario;

/***************************************************************************************************
 *  Clase que se abre con la pulsación del botón "+" del diario (nuevo registro de diario)         *
 *  y que sirve para crear un nuevo registro en el diario del usuario.                             *
 *  Contiene los campos de fecha, horas, descripción y valoración.                                 *
 **************************************************************************************************/
public class NuevoRegistroDiario extends AppCompatActivity {
    ImageButton btnBueno, btnRegular, btnMalo, btnInfoMinutos; // botones de imagen de la valoración del día
    EditText txtFecha, txtHoras, txtMinutos, txtDescripcion; // campos de texto de información del día
    private StringRequest request; // petición volley
    private String fecha, horas, minutos, descripcion, idUsuario = "", valoracionDia = "";

//    private String url_consulta = "http://192.168.0.12/MiAgenda/inserta_nuevo_registro_diario.php";
//    private String url_consulta = "http://192.168.0.159/MiAgenda/inserta_nuevo_registro_diario.php";
    private String url_consulta = "http://miagendafp.000webhostapp.com/inserta_nuevo_registro_diario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_registro_diario);
        setTitle(R.string.title_activity_nuevo_registro_diario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnBueno = (ImageButton) findViewById(R.id.btn_bueno);
        btnRegular = (ImageButton) findViewById(R.id.btn_regular);
        btnMalo = (ImageButton) findViewById(R.id.btn_malo);
        btnInfoMinutos = (ImageButton) findViewById(R.id.btn_info_minutos);
        txtFecha = (EditText) findViewById(R.id.editText_fecha);
        txtHoras = (EditText) findViewById(R.id.editText_horas);
        txtMinutos = (EditText) findViewById(R.id.editText_minutos);
        txtDescripcion = (EditText) findViewById(R.id.editText_descripcion);
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario
        // al que vamos a introducir el registro.

        // Los iconos por defecto aparecen con semitransparencia, para ponerse opacos en su selección
        // para saber cuál está marcado
        btnBueno.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnBueno.setAlpha(1f); // opaco
                btnRegular.setAlpha(0.5f); // semitransparente
                btnMalo.setAlpha(0.5f); // "
                valoracionDia = "Bueno";
            }
        });
        btnRegular.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnRegular.setAlpha(1f);
                btnBueno.setAlpha(0.5f);
                btnMalo.setAlpha(0.5f);
                valoracionDia = "Regular";
            }
        });
        btnMalo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnMalo.setAlpha(1f);
                btnBueno.setAlpha(0.5f);
                btnRegular.setAlpha(0.5f);
                valoracionDia = "Malo";
            }
        });

        // Botón de información que aparece al lado del campo de minutos para saber qué se debe poner
        // en caso de que no haya minutos, por si no se diera por hecho
        btnInfoMinutos.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(NuevoRegistroDiario.this);
                builder.setTitle(R.string.titulo_info_minutos); // titulo del diálogo
                builder.setMessage(R.string.info_minutos)
                        .setPositiveButton(R.string.btn_aceptar_dialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });
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
                fecha = txtFecha.getText().toString();
                horas = txtHoras.getText().toString();
                minutos = txtMinutos.getText().toString();
                descripcion = txtDescripcion.getText().toString();
                if (fecha.isEmpty()|| horas.isEmpty()||minutos.isEmpty()||descripcion.isEmpty() || valoracionDia.isEmpty()){
                    Toast.makeText(NuevoRegistroDiario.this, "Debes completar todos los datos.", Toast.LENGTH_SHORT).show();
                } else {
                    guardarRegistro(); // guardamos el registro en la base de datos
                }
                return true;
            case android.R.id.home: // Opción de volver hacia atrás
                    AlertDialog.Builder builder = new AlertDialog.Builder(NuevoRegistroDiario.this);
                    builder.setTitle(R.string.titulo_dialog_salir_sin_guardar); // titulo del diálogo
                    builder.setMessage(R.string.contenido_dialog_salir_sin_guardar)
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
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                            System.out.println("Nuevo registro creado!");
                            onBackPressed();
                        } else {
                            Toast.makeText(NuevoRegistroDiario.this, R.string.error_registro, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(NuevoRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("fecha", fecha);
                parametros.put("descripcion", descripcion);
                parametros.put("horas", horas);
                parametros.put("minutos", minutos);
                parametros.put("valoracion", valoracionDia);
                parametros.put("idUsuario", idUsuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }
}
