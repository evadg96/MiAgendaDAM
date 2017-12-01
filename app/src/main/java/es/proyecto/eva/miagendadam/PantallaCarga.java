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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.VolleyController.AppController;
import static es.proyecto.eva.miagendadam.PantallaLogin.nombre_usuario;
import static es.proyecto.eva.miagendadam.RegistroNuevoUsuario.correo;

public class PantallaCarga extends AppCompatActivity {
    private final int DURACION_SPLASH = 3000; // los segundos que se verá la pantalla (3)
    private String url_consulta = "http://192.168.0.10/MiAgenda/consulta_isLogged.php";
    private String url_consulta2 = "http://192.168.0.10/MiAgenda/consulta_isConfirmed.php";
    private String url_consulta3 = "http://192.168.0.10/MiAgenda/consulta_update_fechaLogin.php";

  //  private String url_consulta = "http://192.168.0.156/MiAgenda/consulta_isLogged.php";
  //  private String url_consulta2 = "http://192.168.0.156/MiAgenda/consulta_isConfirmed.php";
  //  private String url_consulta3 = "http://192.168.0.156/MiAgenda/consulta_update_fechaLogin.php";

    /*******************************************************************
     *                          SERVIDOR REMOTO
     ******************************************************************/
//    private String url_consulta = "http://miagendafp.000webhostapp.com/consulta_isLogged.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
  //  private String url_consulta2 = "http://miagendafp.000webhostapp.com/consulta_isConfirmed.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
    //private String url_consulta3 = "http://miagendafp.000webhostapp.com/consulta_update_fechaLogin.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";

    static StringRequest request;
    static String fecha_ultimo_login = "";
    // *************  PARA REFERENCIAR A LOS VALORES GUARDADOS EN PREFERENCIAS *********************
    static String nombre_de_usuario;
    static String codigo_de_confirmacion;
    static String correo_electronico;
    // *********************************************************************************************
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
        nombre_de_usuario = preferences.getString("nombre_de_usuario", ""); // habiendo declarado la variable CON EL MISMO NOMBRE arriba
        // ****************************************************************************************************************************************************************
        codigo_de_confirmacion = preferences.getString("codigo_de_confirmacion", "");
        correo_electronico = preferences.getString("correo_electronico", "");

        System.out.println("NOMBRE DE USUARIO ALMACENADO: " + nombre_de_usuario); // mostramos el nombre de usuario que esté guardado al momento de ejecutar la aplicación (debug)
        System.out.println("CÓDIGO DE CONFIRMACIÓN!: " + codigo_de_confirmacion);
        System.out.println("CORREO ELECTRÓNICO ALMACENADO!: " + correo_electronico);

        /*******************************************************************************************
         *              COMPROBAMOS SI EL USUARIO HA CONFIRMADO SU REGISTRO
         ******************************************************************************************/
        if (!nombre_de_usuario.isEmpty()) { // si hay un código de confirmación... quiere decir que se ha hecho un registro
            // con lo cual disponemos de todas las preferencias (salvo quizá el nombre de usuario, que solo se almacena al hacer login)
            // Así pues, comprobaremos si el usuario está confirmado
            request = new StringRequest(Request.Method.POST, url_consulta2,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                            try {
                                if (response.equals("0")) { // si devuelve 0 significará que no ha confirmado su registro
                                    // así que le mandamos a la pantalla de confirmación de registro para que introduzca
                                    // el código que se le ha enviado al correo que introdujo en el formulario de registro

                                    // PARA TARDAR 3 SEGUNDOS DE CARGA ANTES DE ABRIR LA SIGUIENTE ACTIVIDAD
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                                            Intent intent = new Intent(PantallaCarga.this, ConfirmaRegistro.class);
                                            startActivity(intent);
                                            finish();
                                        };
                                    }, DURACION_SPLASH);
                                } else { // si no devuelve un 0, asumimos que el usuario sí está confirmado, y pasamos a comprobar si está logeado
                                    compruebaLogin();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                            Toast.makeText(PantallaCarga.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                            System.out.println("ERROR ONCREATE() HAS COMPROBADO SI LA IP ES CORRECTA?");
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

        } // fin de if que comprueba el isConfirmed
        else {
            // si no hay datos del correo, se procede a comprobar el valor de isLogged
            // pues podría darse el siguiente caso:
            // 1. El usuario instaló la app por primera vez, se registró y se confirmó.
            // 2. El usuario borra la aplicación, pero mantiene su cuenta de usuario
            // 3. El usuario, pasado un tiempo, vuelve a instalar la app, y, lógicamente, ya no
            // tiene que registrarse, así que va directamente a hacer inicio de sesión.
            // por tanto, se generaría en preferencias el nombre_usuario PERO NO EL CORREO O EL
            // CÓDIGO DE CONFIRMACIÓN, porque NO HA PASADO POR EL FORMULARIO DE REGISTRO en ningún momento
            // Así que sería un error mandar siempre de aquí a la pantalla de login, porque
            // la preferencia "codigo_de_confirmacion" nunca tendría ningún dato, y SIEMPRE le llevaría
            // a la pantalla de login, aunque isLogged estuviese activado.
            compruebaLogin();
        }
    }

    public void compruebaLogin(){
        if (!nombre_de_usuario.isEmpty()) { // Validamos si hay contenido en nombre_de_usuario (es decir, ya se ha hecho login previamente y se ha guardado)
            // si no se cumple esta condición, es que nunca se ha hecho ningún inicio de sesión con la aplicación
            // o que no hay preferencias almacenadas, y por tanto, tenemos que obligar a iniciar sesión
            /***************************************************************************************
             *              COMPROBAMOS SI EL USUARIO HA INICIADO YA SESIÓN (valor de isLogged)
             ***************************************************************************************/
            // INICIAMOS CONEXIÓN CON VOLLEY
            request = new StringRequest(Request.Method.POST, url_consulta,
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
                                            // Almacenamos el dato de la fecha como dato de última sesión iniciada:
                                            actualizaFechaLogin(); // solo se actualiza (en esta clase) si isLogged está a 1, que es
                                            // cuando se hace inicio de sesión automático
                                            Intent intent = new Intent(PantallaCarga.this, NavMenu.class);
                                            startActivity(intent);
                                            finish();
                                        };
                                    }, DURACION_SPLASH);
                                } else {
                                    if (response.equals("0")){ // si devuelve 0 significará que no se ha logeado (o ha cerrado sesión)
                                        // así que le mandamos a la pantalla de login para que introduzca sus datos de usuario
                                        abrePantallaLogin();
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
                            System.out.println("ERROR  COMPRUEBALOGIN();");
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

        } else { // se cumple cuando no se ha hecho un inicio de sesión, o cuando se haya borrado y reinstalado
            // la aplicación, por ejemplo, que no tendremos los datos de las preferencias para hacer las validaciones,
            // así que le obligaremos a ir a la pantalla de inicio de sesión.
            abrePantallaLogin();
        }
    }


    /***********************************************************************************************
     * Método que actualiza el registro con la fecha de último inicio de sesión del usuario
     **********************************************************************************************/
    public void actualizaFechaLogin(){
        request = new StringRequest(Request.Method.POST, url_consulta3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Al cerrar sesión estaremos actualizando el campo isLogged a 0 para que no se detecte como sesión iniciada en la pantalla
                        // de carga al volver a abrir la aplicación
                        Date fecha = new Date();
                        fecha_ultimo_login = fecha.toString();
                        System.out.println("FECHA DE ÚLTIMO INICIO DE SESIÓN: "+ fecha_ultimo_login);
                        System.out.println("FECHA DE ÚLTIMO INICIO DE SESIÓN ACTUALIZADA.");
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaCarga.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                        System.out.println("ERROR ACTUALIZAFECHALOGIN()");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_de_usuario);
                parametros.put("fecha_ultimo_login", fecha_ultimo_login);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);

    }

    /**********************************************************
     * Método que abre la pantalla de inicio de sesión        *
     *********************************************************/
    public void abrePantallaLogin(){
        System.out.println("NO HAY DATOS PARA INICIAR SESIÓN, O isLogged ESTÁ A 0");
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
