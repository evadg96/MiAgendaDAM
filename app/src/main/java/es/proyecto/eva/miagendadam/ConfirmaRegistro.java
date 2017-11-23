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


public class ConfirmaRegistro extends AppCompatActivity {
    Button btnConfirmar;
    Button btnReenviar;
    EditText txtCodigo;
    static String codigo_de_confirmacion;
    static String correo_electronico;
    static String url_consulta="http://192.168.0.10/MiAgenda/consulta_update_isConfirmed.php";
    static StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_registro);
        setTitle("Confirmar registro");
        btnConfirmar = (Button) findViewById(R.id.btn_confirmar);
        btnReenviar = (Button) findViewById(R.id.btn_reenviar_codigo);
        txtCodigo = (EditText) findViewById(R.id.editText_codigo);
        // Referenciamos al SharedPreferences que habíamos creado en la clase PantallaLogin
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        codigo_de_confirmacion = preferences.getString("codigo_de_confirmacion", ""); // obtenemos preferencia del código
        correo_electronico = preferences.getString("correo_electronico", ""); // obtenemos preferencia del correo
        System.out.println("CÓDIGO DE CONFIRMACIÓN ACTUAL: " + codigo_de_confirmacion);
        System.out.println("CORREO DE CONFIRMACIÓN ACTUAL: " + correo_electronico);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // INICIAMOS CONEXIÓN CON VOLLEY
                if (!correo_electronico.isEmpty()) {
                    request = new StringRequest(Request.Method.POST, url_consulta,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                                    String codigo = txtCodigo.getText().toString();

                                        if (!codigo_de_confirmacion.isEmpty()) { // aseguramos que las preferencias no están vacías
                                            if (codigo.equals(codigo_de_confirmacion)) {
                                                // Creamos ventana alerta de aviso que lleva a pantalla login
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmaRegistro.this);
                                                builder.setMessage("Usuario confirmado. Ya puedes iniciar sesión.")
                                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                Intent intent = new Intent(ConfirmaRegistro.this, PantallaLogin.class);
                                                                startActivity(intent);
                                                            }
                                                        })
                                                        .setNegativeButton(R.string.btn_cancelar_confirm, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                // User cancelled the dialog
                                                                // en blanco solo cierra el diálogo
                                                            }
                                                        });
                                                // Create the AlertDialog object and return it
                                                Dialog dialog = builder.create();
                                                dialog.show();
                                            } else {
                                                Toast.makeText(ConfirmaRegistro.this, "El código introducido no es correcto.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else { // si no hay codigo de confirmación en las preferencias, le decimos que ha expirado, para que solicite uno nuevo
                                            Toast.makeText(ConfirmaRegistro.this, "El código ha expirado.", Toast.LENGTH_SHORT).show();
                                        }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                    Toast.makeText(ConfirmaRegistro.this, "Error de conexión.", Toast.LENGTH_SHORT).show();

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
                } else { // si el dato correo en preferencias está vacío
                    Toast.makeText(ConfirmaRegistro.this, "El código ha expirado.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Botón reenviar código, abrimos pantalla de reenvío de código
        btnReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ConfirmaRegistro.this, ReenviarCodigoConfirmacion.class);
                startActivity(intent);
            }
        });
    }
}
