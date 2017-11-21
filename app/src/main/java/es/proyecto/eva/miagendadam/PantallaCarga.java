package es.proyecto.eva.miagendadam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.VolleyController.AppController;
import static es.proyecto.eva.miagendadam.PantallaLogin.nombre_usuario;

public class PantallaCarga extends AppCompatActivity {
    private final int DURACION_SPLASH = 3000; // los segundos que se verá la pantalla (3)
    private String url_consulta = "http://192.168.0.10/MiAgenda/consulta_isLogged.php";
    static String nombre_de_usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);
        getSupportActionBar().hide(); // para ocultar la barra de titulo de la pantalla
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, // para poner en pantalla completa la actividad, así no se verá la barra
                WindowManager.LayoutParams.FLAG_FULLSCREEN);        // de notificaciones con la hora etc. (luego vuelve a aparecer)

        // Referenciamos al SharedPreferences que habíamos creado en la clase PantallaLogin
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

        // ****************** ¡¡¡¡ UTILIZAR ESTE FRAGMENTO CADA VEZ QUE SE QUIERA REFERENCIAR AL NOMBRE DE USUARIO ALMACENADO POR LA APLICACIÓN !!!! **********************
          nombre_de_usuario = preferences.getString("nombre_de_usuario", "");
        // ****************************************************************************************************************************************************************

        System.out.println("NOMBRE DE USUARIO ALMACENADO: " + nombre_de_usuario); // mostramos el nombre de usuario que esté guardado al momento de ejecutar la aplicación (debug)
        if (!nombre_de_usuario.isEmpty()) { // Validamos si hay contenido en nombre_de_usuario (es decir, ya se ha hecho login previamente y se ha guardado)
            // si no se cumple esta condición, es que nunca se ha hecho ningún inicio de sesión con la aplicación

            // **************************** COMPROBAMOS VALOR DE isLogged PARA IR A UNA PANTALLA U OTRA ******************************************
                // INICIAMOS CONEXIÓN CON VOLLEY
                StringRequest request = new StringRequest(Request.Method.POST, url_consulta,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                               try {
                                   System.out.println("YA SE HA LOGEADO? (1 = SI, 0 = NO): " + response); // visualizamos la respuesta obtenida
                                    if (response.equals("1")) { // si devuelve 1, significará que sí se había logeado
                                        // así que le mandaremos a la pantalla principal, sin hacer el login de nuevo

                                        // PARA TARDAR 3 SEGUNDOS DE CARGA ANTES DE ABRIR LA SIGUIENTE ACTIVIDAD
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                                                Intent intent = new Intent(PantallaCarga.this, PantallaPrincipal.class);
                                                startActivity(intent);
                                                finish();
                                            };
                                        }, DURACION_SPLASH);
                                    } else {
                                        if (response.equals("0")){ // si devuelve 0 significará que no se ha logeado (o ha cerrado sesión)
                                            // así que le mandamos a la pantalla de login para que introduzca sus datos de usuarioIntent intent = new Intent(PantallaCarga.this, PantallaPrincipal.class);

                                            // PARA TARDAR 3 SEGUNDOS DE CARGA ANTES DE ABRIR LA SIGUIENTE ACTIVIDAD
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
                               } catch (Exception e){
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
                        parametros.put("nUsuario", nombre_de_usuario);
                        return parametros;
                    }
                };
                AppController.getInstance().addToRequestQueue(request);

        } else { // Se cumplirá este caso por ejemplo cuando la aplicación esté recién instalada en el dispositivo o nunca se haya hecho inicio de sesión,
            // y por tanto, no se puede haber almacenado ningún usuario.
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
