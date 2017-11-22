package es.proyecto.eva.miagendadam;

import android.app.ProgressDialog;
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
    private Button btnRegistroUsuario;
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
        btnRegistroUsuario = (Button) findViewById(R.id.btn_registrarse);
        txtNombreUsuario = (EditText) findViewById(R.id.editText_nombre_usuario);
        txtClave = (EditText) findViewById(R.id.editText_clave);

        // AL HACER CLICK EN LOS BOTONES...
        // Botón Registrarse, abre actividad de RegistroNuevoUsuario
        btnRegistroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PantallaLogin.this, RegistroNuevoUsuario.class);
                startActivity(intent);
            }
        });
        // Botón Iniciar sesión
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
                                                        // Creamos ventana de diálogo con circulo de carga para la espera de carga de los datos
                                                        ProgressDialog progressDialog = new ProgressDialog(PantallaLogin.this);
                                                        progressDialog.setTitle("Carga");
                                                        progressDialog.setMessage("Comprobando datos. Por favor, espere un momento.");
                                                        progressDialog.show();
                                                        System.out.println("LOGIN CORRECTO :)");
                                                        nombre_usuario = nUsuario; // si hemos llegado hasta aquí, es que el nombre de usuario
                                                        // y la clave introducidos por el usuario son válidos, por tanto se guarda el dato que el
                                                        // usuario ha introducido para mostrar luego los datos que le correspondan
                                                        // También, en el script php, se cambiará el campo del usuario isLogged a 1, para que al
                                                        // cargar la PantallaCarga, el programa seleccione su campo, y, si es 1 pase directamente
                                                        // a la pantalla principal, o si es 0, entre en la pantalla Login.
                                                        guardarPreferencias(); // almacenamos el nombre de usuario que se ha introducido
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

    /*********************************************************************************************************************************
     * Mediante este método guardamos como preferencias el nombre de usuario que el usuario haya introducido al hacer sesión.
     * para poder utilizarlo después en todas las consultas que utilicen como filtro el nombre del usuario (que serán casi todas)
     ********************************************************************************************************************************/
    private void guardarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nombre_de_usuario", nombre_usuario);
        editor.commit();
    }
}
