package es.proyecto.eva.miagendadam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.VolleyController.AppController;

/***************************************************************************************************
 *  Clase que confirma el registro de un usuario. Al momento del registro se le envía un código
 *  de confirmación al correo que haya introducido en el formulario de registro, y en esta pantalla
 *  deberá introducir el código. Esta clase se encargará de comprobar que el código introducido
 *  coincide con el código que se generó y se le mandó por correo, y le confirma en la base de datos.
 ***************************************************************************************************/

public class ConfirmaRegistro extends AppCompatActivity {
    /**
     * SE VALIDARÁ AL USUARIO POR SU CORREO, NO POR NOMBRE. ES DECIR, SE CONFIRMARÁ AL USUARIO CON EL CORREO QUE SE HAYA INTRODUCIDO
     * EN EL CAMPO DE CORREO, BIEN DURANTE EL REGISTRO, BIEN EN LA PETICIÓN DE REENVÍO DE CÓDIGO DE CONFIRMACIÓN
     */
    Button btnConfirmar, btnReenviar;
    EditText txtCorreo, txtCodigo;
    static String codigo_de_confirmacion;
    private String correo_electronico = "";
    private StringRequest request;

    // ******************************* SERVIDORES Y CONSULTAS **************************************

//    private String url_consulta = "http://192.168.0.12/MiAgenda/update_isConfirmed.php";
//    private String url_consulta2 = "http://192.168.0.12/MiAgenda/check_correo.php";

//    private String url_consulta="http://192.168.0.159/MiAgenda/update_isConfirmed.php";
//    private String url_consulta2 = "http://192.168.0.159/MiAgenda/check_correo.php";

    private String url_consulta = "http://miagendafp.000webhostapp.com/update_isConfirmed.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/check_correo.php";

    // *********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_registro);
        setTitle(R.string.title_activity_confirma_registro);
        btnConfirmar = (Button) findViewById(R.id.btn_confirmar);
        btnReenviar = (Button) findViewById(R.id.btn_reenviar_codigo);
        txtCorreo = (EditText) findViewById(R.id.editText_correo);
        txtCodigo = (EditText) findViewById(R.id.editText_codigo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Referenciamos al SharedPreferences que habíamos creado en la clase PantallaLogin
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        codigo_de_confirmacion = preferences.getString("codigo_de_confirmacion", ""); // obtenemos preferencia del código
        // comprobamos si el correo existe en la base de datos
        //Log.i("ConfirmaRegistro", "Código de confirmación actual: " + codigo_de_confirmacion);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("ConfirmaRegistro", "Confirmar registro");
                check_correo();
            }
        });

        // Botón reenviar código, abrimos pantalla de reenvío de código
        btnReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("ConfirmaRegistro", "Reenviar código de confirmación");
                Intent intent = new Intent(ConfirmaRegistro.this, ReenviarCodigoConfirmacion.class);
                startActivity(intent);
            }
        });
    }

    /***********************************************************************************************
     * Método que comprueba que el correo introducido para confirmar el registro de usuario existe
     * en la base de datos
     **********************************************************************************************/
    public void check_correo(){
        //Log.i("ConfirmaRegistro", "Comprobamos existencia de correo introducido");
        correo_electronico = txtCorreo.getText().toString();
        if (!correo_electronico.isEmpty()){ // si se ha introducido un dato en el campo de correo...
            request = new StringRequest(Request.Method.POST, url_consulta2,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("0")) { // NO EXISTE el usuario en la bd
                                //Log.i("ConfirmaRegistro", "No existe ningún usuario con ese correo");
                                //Toast.makeText(ConfirmaRegistro.this, R.string.error_correo_no_existe, Toast.LENGTH_SHORT).show();
                                Snackbar.make(findViewById(android.R.id.content),
                                        R.string.error_correo_no_existe, Snackbar.LENGTH_LONG).show();
                            } else if(response.equals("1")){ // SÍ EXISTE, comprobamos código de confirmación:
                                //Log.i("ConfirmaRegistro", "Comprobamos código de confirmación");
                                request = new StringRequest(Request.Method.POST, url_consulta,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                // Obtenemos el dato introducido por el usuario en el campo de texto del código
                                                String codigo = txtCodigo.getText().toString();
                                                if (!codigo_de_confirmacion.isEmpty()) { // aseguramos que tengamos el dato del código almacenado
                                                    if (codigo.equals(codigo_de_confirmacion)) { // EL CÓDIGO ES CORRECTO
                                                        //Log.d("ConfirmaRegistro", "Código correcto");
                                                        // Creamos diálogo alerta de aviso que lleva a pantalla login
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmaRegistro.this);
                                                        builder.setMessage(R.string.dialog_confirmacion_correcta)
                                                                .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        finish(); // cerramos la actividad para volver a pantalla de inicio de sesión
                                                                    }
                                                                })
                                                                .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        // Se cancela el diálogo, dejamos
                                                                        // en blanco para que no se haga nada,
                                                                        // solo cerrar el diálogo
                                                                    }
                                                                });
                                                        Dialog dialog = builder.create();
                                                        dialog.show(); // mostramos el diálogo
                                                    } else { // El código NO es correcto
                                                       // Toast.makeText(ConfirmaRegistro.this, R.string.error_codigo_incorrecto, Toast.LENGTH_SHORT).show();
                                                        Snackbar.make(findViewById(android.R.id.content),
                                                                R.string.error_codigo_incorrecto, Snackbar.LENGTH_SHORT).show();
                                                        //Log.d("ConfirmaRegistro", "Código incorrecto");
                                                    }
                                                } else { // Si no hay codigo de confirmación en las preferencias, le decimos que ha expirado, para que solicite uno nuevo
                                                   // Toast.makeText(ConfirmaRegistro.this, R.string.error_codigo_expirado, Toast.LENGTH_LONG).show();
                                                    Snackbar.make(findViewById(android.R.id.content),
                                                            R.string.error_codigo_expirado, Snackbar.LENGTH_LONG).show();
                                                    //Log.d("ConfirmaRegistro", "No hay ningún código almacenado");
                                                }
                                            }
                                        },
                                        // Error al confirmar el registro
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                               // Toast.makeText(ConfirmaRegistro.this, R.string.error_confirmar_registro, Toast.LENGTH_LONG).show();
                                                Snackbar.make(findViewById(android.R.id.content),
                                                        R.string.error_confirmar_registro, Snackbar.LENGTH_LONG).show();
                                                //Log.e("ConfirmaRegistro", "Error al conectar con el servidor para confirmar el registro del usuario");
                                            }
                                        }) {
                                    // Enviamos los datos necesarios al script php para que pueda realizar la consulta
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        // Enviamos los datos en un objeto Map<clave, valor> (el nombre del dato que se pide en el script y el nombre de la variable
                                        // que se enviará como ese dato)
                                        Map<String, String> parametros = new HashMap<>();
                                        parametros.put("correo", correo_electronico); // en este caso enviamos el correo
                                        // para confirmar el registro del usuario asociado a dicho correo
                                        return parametros;
                                    }

                                };
                                AppController.getInstance().addToRequestQueue(request); // añadimos a la cola de peticiones la petición actual
                            }
                        }
                    },
                    // Error al comprobar el correo
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                           // Toast.makeText(ConfirmaRegistro.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                            Snackbar.make(findViewById(android.R.id.content),
                                    R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                            //Log.e("ConfirmaRegistro", "Error al conectar con el servidor para comprobar el correo del usuario");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("correo", correo_electronico);
                    return parametros;
                }

            };
            AppController.getInstance().addToRequestQueue(request);
        } else { // si el campo de nombre de usuario está vacío
          //  Toast.makeText(ConfirmaRegistro.this, R.string.error_introducir_correo, Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.error_introducir_correo, Snackbar.LENGTH_SHORT).show();
            //Log.e("ConfirmaRegistro", "No hay nombre de usuario almacenado");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Al pulsar el icono de la flecha atrás de la barra de acciones
            case android.R.id.home:
                //Log.i("ConfirmaRegistro", "Action Atrás");
                // Para que no nos mande de vuelta al registro recién hecho, porque esta pantalla se abre
                // por primera vez al terminar el registro de nuevo usuario
               onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Al pulsar hacia atrás se cierra la actividad
     **********************************************************************************************/
    public void onBackPressed(){
        finish();
    }
}
