package es.proyecto.eva.miagendadam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
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


public class PantallaLogin extends AppCompatActivity {
    private Button btnIniciarSesion;
    private EditText txtNombreUsuario;
    private EditText txtClave;
    private String url_consulta = "http://192.168.0.10/MiAgenda/consulta_datos_usuario2.php";
    static String nombre_usuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_login);
        setTitle("Inicio de sesión");
        btnIniciarSesion = (Button) findViewById(R.id.btn_iniciar_sesion);
        txtNombreUsuario = (EditText) findViewById(R.id.editText_nombre_usuario);
        txtClave = (EditText) findViewById(R.id.editText_clave);
        final Context context = this;
        final SharedPreferences sharprefs = getSharedPreferences("ArchivoSP", context.MODE_PRIVATE);
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final String nUsuario = txtNombreUsuario.getText().toString();
                final String clave = txtClave.getText().toString();
                if (nUsuario.isEmpty()) { // validamos que el campo no se haya dejado en blanco
                    Toast.makeText(PantallaLogin.this, "Debes introducir un nombre de usuario.", Toast.LENGTH_SHORT).show();
                } else {
                    if (clave.isEmpty()) {
                        Toast.makeText(PantallaLogin.this, "Debes introducir una clave.", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("DATOSSSSSSSSS!!!!!!!!: " + nUsuario + clave);
                        // INICIAMOS CONEXIÓN CON VOLLEY
                        StringRequest request = new StringRequest(Request.Method.POST, url_consulta,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                                        if (response.equals("2")) {
                                            try {
                                                Toast.makeText(PantallaLogin.this, "No existe ningún usuario con ese nombre.", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            if (response.equals("3")) {
                                                try {
                                                    Toast.makeText(PantallaLogin.this, "La clave introducida no es correcta.", Toast.LENGTH_SHORT).show();
                                                    System.out.println("ERROR DE CLAVE");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                if (response.equals("correcto")) {
                                                    try {
                                                        System.out.println("LOGIN CORRECTO :)");
                                                        Intent intent = new Intent(PantallaLogin.this, PantallaPrincipal.class);
                                                        startActivity(intent);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }

                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                        Toast.makeText(PantallaLogin.this, "Error de conexión.", Toast.LENGTH_SHORT).show();

                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                                Map<String, String> parametros = new HashMap<>();
                                parametros.put("nUsuario", nUsuario);
                                parametros.put("clave", clave);
                                return parametros;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(request);
                    }

                }
            }
        });
    }
}
