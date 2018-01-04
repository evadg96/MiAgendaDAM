package es.proyecto.eva.miagendadam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
        System.out.println("CÓDIGO DE CONFIRMACIÓN ACTUAL: " + codigo_de_confirmacion);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_correo();
            }
        });

        // Botón reenviar código, abrimos pantalla de reenvío de código
        btnReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        correo_electronico = txtCorreo.getText().toString();
        if (!correo_electronico.isEmpty()){ // si se ha introducido un dato en el campo de correo...
            request = new StringRequest(Request.Method.POST, url_consulta2,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("0")) { // NO EXISTE el usuario en la bd
                                Toast.makeText(ConfirmaRegistro.this, "No hay ningún usuario registrado con ese correo.", Toast.LENGTH_SHORT).show();
                            } else if(response.equals("1")){ // SÍ EXISTE, comprobamos código de confirmación:
                                request = new StringRequest(Request.Method.POST, url_consulta,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                // Obtenemos el dato introducido por el usuario en el campo de texto del código
                                                String codigo = txtCodigo.getText().toString();
                                                if (!codigo_de_confirmacion.isEmpty()) { // aseguramos que tengamos el dato del código almacenado
                                                    if (codigo.equals(codigo_de_confirmacion)) { // EL CÓDIGO ES CORRECTO
                                                        // Creamos diálogo alerta de aviso que lleva a pantalla login
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmaRegistro.this);
                                                        builder.setMessage("¡Usuario confirmado! Ya puedes iniciar sesión.")
                                                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        Intent intent = new Intent(ConfirmaRegistro.this, PantallaLogin.class);
                                                                        startActivity(intent); // arrancamos la actividad PantallaLogin una vez se ha confirmado
                                                                    }
                                                                })
                                                                .setNegativeButton(R.string.btn_cancelar_confirm, new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        // Se cancela el diálogo, dejamos
                                                                        // en blanco para que no se haga nada,
                                                                        // solo cerrar el diálogo
                                                                    }
                                                                });
                                                        Dialog dialog = builder.create();
                                                        dialog.show(); // mostramos el diálofo
                                                    } else { // El código NO es correcto
                                                        Toast.makeText(ConfirmaRegistro.this, "El código introducido no es correcto.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else { // Si no hay codigo de confirmación en las preferencias, le decimos que ha expirado, para que solicite uno nuevo
                                                    Toast.makeText(ConfirmaRegistro.this, "El código ha expirado o no hay ningún usuario que confirmar. Solicite uno nuevo.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        },
                                        // Error al confirmar el registro
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(ConfirmaRegistro.this, "Error al confirmar el registro. Por favor, vuelva a intentarlo.", Toast.LENGTH_LONG).show();

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
                            // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                            Toast.makeText(ConfirmaRegistro.this, "Se ha producido un error al comprobar el correo electrónico. Por favor, vuelva a intentarlo.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(ConfirmaRegistro.this, "Debes introducir tu correo electrónico.", Toast.LENGTH_SHORT).show();
        }
    }

    // Al pulsar el botón de atrás de la barra de acciones, se dirige a la pantalla login
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // Para que no nos mande de vuelta al registro recién hecho, porque esta pantalla se abre
                // por primera vez al terminar el registro de nuevo usuario
                Intent intent = new Intent (ConfirmaRegistro.this, PantallaLogin.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Método que "inhabilita" la vuelta atrás al pulsar la flecha de ir hacia atrás del dispositivo,
     * para que no pueda volver al formulario de registro cuando le aparezca la pantalla de confirmación
     * de registro por primera vez.
     **********************************************************************************************/
    public void onBackPressed(){
        // dejamos en blanco, no hace nada
    }
}
