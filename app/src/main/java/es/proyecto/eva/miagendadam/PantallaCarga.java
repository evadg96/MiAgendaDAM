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

/********************************************************************************************************************
 * Pantalla inicial (splash).
 * En función de si ya se ha registrado un usuario previamente, si se ha confirmado, logeado, o nada
 * de lo anterior, realizará unas determinadas acciones u otras:
 *  1. Primera vez que se abre la aplicación --> no se hace nada 'raro'. Simplemente se lleva
 *  a la pantalla de login para que el usuario vaya a registrarse.
 *  2. Se abre la aplicación tras haber hecho un registro pero sin haberse confirmado --> se comprueba
 *  el estado de isConfirmed del usuario, y, al no estar confirmado, se le manda a la pantalla de confirmación.
 *  2b. Siguiendo con el caso 2, se sale de la pantall de confirmación y se va a la pantalla de login. Al intentar
 *  iniciar la sesión, se detectará que no se ha confirmado, así que no le dejará entrar (pero esta validación
 *  se hace en la clase PantallaLogin, así que aquí no nos interesa).
 *  3. Se abre la aplicación tras haber confirmado --> se comprueba el login. Como en teoría aún
 *  no se ha iniciado sesión, estará a 0, así que se va automáticamente a la pantalla de login.
 *  4. Se abre la aplicación después de haberla cerrado después de haber hecho login --> se comprueba el login.
 *  Como ya se ha iniciado sesión, isLogged estará a 1, así que se va directamente a la pantalla principal.
 ********************************************************************************************************************/

public class PantallaCarga extends AppCompatActivity {
    private final int DURACION_SPLASH = 3000; // los segundos que se verá la pantalla (3)

    // Servidor local
    private String url_consulta = "http://192.168.0.10/MiAgenda/consulta_isLogged.php";
    private String url_consulta2 = "http://192.168.0.10/MiAgenda/consulta_isConfirmed.php";
    private String url_consulta3 = "http://192.168.0.10/MiAgenda/consulta_update_fechaLogin.php";

  //  private String url_consulta = "http://192.168.0.156/MiAgenda/consulta_isLogged.php";
  //  private String url_consulta2 = "http://192.168.0.156/MiAgenda/consulta_isConfirmed.php";
  //  private String url_consulta3 = "http://192.168.0.156/MiAgenda/consulta_update_fechaLogin.php";

    /***********************************************************************************************
     *                            SERVIDOR REMOTO (no funciona a día 01/12)
     **********************************************************************************************/
//    private String url_consulta = "http://miagendafp.000webhostapp.com/consulta_isLogged.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
  //  private String url_consulta2 = "http://miagendafp.000webhostapp.com/consulta_isConfirmed.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
    //private String url_consulta3 = "http://miagendafp.000webhostapp.com/consulta_update_fechaLogin.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";

    static StringRequest request;
    static Date fecha = new Date();
    static String fecha_ultimo_login = fecha.toString();

    // ****************  PARA REFERENCIAR A LOS VALORES GUARDADOS EN PREFERENCIAS ******************
    static String nombre_de_usuario = "";
    static String codigo_de_confirmacion;
    static String correo_electronico;
    // *********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);
        getSupportActionBar().hide(); // para ocultar la barra de titulo de la pantalla (la toolbar)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, // para poner en pantalla completa la actividad, así no se verá la barra
                WindowManager.LayoutParams.FLAG_FULLSCREEN);        // de notificaciones con la hora etc. (luego vuelve a aparecer)

        // Referenciamos al SharedPreferences que habíamos creado en la clase PantallaLogin
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);


        // ************* ¡¡¡¡ UTILIZAR ESTAS LÍNEAS CADA VEZ QUE SE QUIERA REFERENCIAR A LAS PREFERENCIAS ALMACENADAS !!!! ****************************
        // (Habiendo declarado las variables CON EL MISMO NOMBRE arriba)
        nombre_de_usuario = preferences.getString("nombre_de_usuario", "");
        codigo_de_confirmacion = preferences.getString("codigo_de_confirmacion", "");
        correo_electronico = preferences.getString("correo_electronico", "");
        // ********************************************************************************************************************************************

        // Las mostramos en la consola de debug para controlar los datos que tenemos almacenados por el momento
        System.out.println("NOMBRE DE USUARIO ALMACENADO: " + nombre_de_usuario);
        System.out.println("CÓDIGO DE CONFIRMACIÓN!: " + codigo_de_confirmacion);
        System.out.println("CORREO ELECTRÓNICO ALMACENADO!: " + correo_electronico);

        /*******************************************************************************************
         *              COMPROBAMOS SI EL USUARIO HA CONFIRMADO SU REGISTRO
         ******************************************************************************************/
        if (!nombre_de_usuario.isEmpty()) { // el nombre de usuario es una preferencia que se almacena al hacer un login correcto,
            // porque es la única manera de comprobar que el nombre de usuario es válido
            // si no tenemos esta preferencia, quiere decir que aún no se ha hecho ningún login correcto, así que comprobamos
            // si el usuario ya ha confirmado su registro mediante la siguiente consulta:
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
                                    // porque solo puede devolver un 1 o un 0. Si no es 0, tiene que ser 1.
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
        else { // si no hay datos de la preferencia del nombre, se deduce entonces que nunca se ha hecho inicio de sesión
            // por lo tanto obligamos a ir a la pantalla de login.
            // Podría darse un supuesto muy 'rebuscado' en el que un usuario haya estado utilizando la aplicación, y por X motivo
            // un día la borre sin haber cerrado su sesión (el campo isLogged seguiría a 1 en la base de datos), pero tiempo después
            // vuelva a instalarla. Ahí no sería cierto que nunca ha iniciado sesión en la aplicación, pero sí que no lo ha hecho desde
            // que la ha instalado nuevamente. Las preferencias en una aplicación se borran si la aplicación se desinstala, con lo cual
            // no podríamos conservar aún la preferenia del nombre de usuario de esta persona, y por tanto no podríamos validar a este usuario,
            // así que directamente le obligaríamos a iniciar nuevamente sesión, almacenando así su nombre de usuario como preferencia.
            // No importa que el campo isLogged ya estuviera en 1, porque al hacer el UPDATE del campo se guardará el mismo dato nuevamente, no
            // da ningún error por guardar lo mismo.
            abrePantallaLogin();
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

                                    // Almacenamos primero el dato de la fecha como dato de última sesión iniciada:
                                    actualizaFechaLogin(); // solo se actualiza (en esta clase) si isLogged está a 1, que es
                                    // cuando se hace inicio de sesión automático

                                    // PARA TARDAR 3 SEGUNDOS DE CARGA ANTES DE ABRIR LA SIGUIENTE ACTIVIDAD
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
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
                            System.out.println("ERROR COMPRUEBALOGIN();");
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
                        //Date fecha = new Date();
                        //System.out.println(fecha);
                        //fecha_ultimo_login = fecha.toString();
                        System.out.println("FECHA DE ÚLTIMO INICIO DE SESIÓN: "+ fecha_ultimo_login);
                        System.out.println("FECHA DE ÚLTIMO INICIO DE SESIÓN ACTUALIZADA.");
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaCarga.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                        System.out.println("ERROR ACTUALIZAFECHALOGIN() ¿? ");
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
