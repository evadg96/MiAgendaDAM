package es.proyecto.eva.miagendadam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import es.proyecto.eva.miagendadam.VolleyController.AppController;
import static es.proyecto.eva.miagendadam.PantallaLogin.nombre_usuario;

public class PantallaCarga extends AppCompatActivity {
    private final int DURACION_SPLASH = 3000; // los segundos que se verá la pantalla (3)
    private String url_consulta = "http://192.168.0.10/MiAgenda/consulta_isLogged.php";
    private JSONArray jsonArray;
    private String valor_isLogged;
    static String valor_n_usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);
        getSupportActionBar().hide(); // para ocultar la barra de titulo de la pantalla
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, // para poner en pantalla completa la actividad, así no se verá la barra
                WindowManager.LayoutParams.FLAG_FULLSCREEN);        // de notificaciones con la hora etc. (luego vuelve a aparecer)

        SharedPreferences sharpref = getPreferences(MODE_PRIVATE); // igual puede fallar, sería context.MODE_PRIVATE
        valor_n_usuario = sharpref.getString("nombre_de_usuario", "");
        System.out.println("NOMBRE DE USUARIO ALMACENADO: " + valor_n_usuario);
        if (!valor_n_usuario.isEmpty()) { // si hay contenido en nombre_usuario (es decir, ya se ha hecho login previamente)
           // vamos a comprobar el valor de isLogged
            // ************** IMPORTANTE!!!!
            // Para ello debemos guardar el valor de la variable nombre_usario con un SharedPreferences o algo así
                // INICIAMOS CONEXIÓN CON VOLLEY
                StringRequest request = new StringRequest(Request.Method.POST, url_consulta,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                               try {
                                   jsonArray = new JSONArray(response);
                                   for (int i = 0; i<jsonArray.length(); i++){
                                       valor_isLogged = jsonArray.getJSONObject(i).getString("isLogged");
                                   }
                                   System.out.println("YA SE HA LOGEADO? (1 = SI, 0 = NO): " + valor_isLogged);
                                    if (valor_isLogged.equals("1")) { // si devuelve 1, significará que sí se había logeado
                                        // así que le mandaremos a la pantalla principal, sin hacer el login de nuevo

                                        // PARA TARDAR 3 SEGUNDOS DE CARGA
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                                                Intent intent = new Intent(PantallaCarga.this, PantallaPrincipal.class);
                                                startActivity(intent);
                                                finish();
                                            };
                                        }, DURACION_SPLASH);
                                    } else {
                                        if (valor_isLogged.equals("0")){ // si devuelve 0 significará que no se ha logeado (o ha cerrado sesión)
                                            // así que le mandamos a la pantalla de login para que introduzca sus datos de usuarioIntent intent = new Intent(PantallaCarga.this, PantallaPrincipal.class);

                                            // PARA TARDAR 3 SEGUNDOS DE CARGA
                                            new Handler().postDelayed(new Runnable() {
                                                public void run() {
                                                    // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                                                    Intent intent = new Intent(PantallaCarga.this, PantallaLogin.class);
                                                    startActivity(intent);
                                                    finish();
                                                };
                                            }, DURACION_SPLASH);
                                        }
                                    }
                               } catch (JSONException e){
                                   e.printStackTrace();
                               }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                Toast.makeText(PantallaCarga.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("nUsuario", valor_n_usuario);
                        return parametros;
                    }
                };
                AppController.getInstance().addToRequestQueue(request);

        } else {
            System.out.println("NO HAY DATOS DE USUARIOS PREVIOS!!");
            // Para dejar la actividad visible durante 3 segundos
            // Después se pasa a la otra pantalla
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                    Intent intent = new Intent(PantallaCarga.this, PantallaLogin.class);
                    startActivity(intent);
                    finish();
                }

                ;
            }, DURACION_SPLASH);
        }
    }
}
