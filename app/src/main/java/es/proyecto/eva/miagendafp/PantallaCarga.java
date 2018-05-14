package es.proyecto.eva.miagendafp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendafp.VolleyController.AppController;

/********************************************************************************************************************
 * Pantalla inicial (splash) --> Primera pantalla de la app, donde se realizan comprobaciones y se obtienen datos
 * Comprobaciones:
 *  1. Se comprueba la conexión a internet. Si no se dispone de conexión, bien sea wifi, bien de datos móviles, no se
 *  permite continuar: directamente se muestra un mensaje de error de conexión y cuando se pulsa el botón de Aceptar la
 *  aplicación se cierra por completo. Esta acción se repite hasta que se detecte que hay conexión a internet.
 *  (A partir de aquí asumimos que hay conexión a internet)
 *  2. Primera vez que se abre la aplicación --> no se hace nada 'raro'. Simplemente se lleva
 *  a la pantalla de login para que el usuario vaya a registrarse.
 *  3. Se abre la aplicación tras haber hecho un registro pero sin haberse confirmado --> se comprueba
 *  el estado de isConfirmed del usuario, y, al no estar confirmado, se le manda a la pantalla de confirmación.
 *  3b. Siguiendo con el caso 2, se sale de la pantalla de confirmación y se va a la pantalla de login. Al intentar
 *  iniciar la sesión, se detectará que no se ha confirmado, así que no le dejará entrar (pero esta validación
 *  se hace en la clase PantallaLogin, así que aquí no nos interesa).
 *  4. Se abre la aplicación tras haber confirmado --> se comprueba el login. Como en teoría aún
 *  no se ha iniciado sesión, estará a 0, así que se va automáticamente a la pantalla de login.
 *  5. Se abre la aplicación después de haberla cerrado después de haber hecho login --> se comprueba el login.
 *  Como ya se ha iniciado sesión, isLogged estará a 1, así que se va directamente a la pantalla principal sin pedirle
 *  datos al usuario.
 ********************************************************************************************************************/

public class PantallaCarga extends AppCompatActivity {
    private final int DURACION_SPLASH = 1500; // los segundos que se verá la pantalla (3)
    ProgressBar progressBar; // barra circular giratoria
    // Consultas de servidor local
//    private String url_consulta = "http://192.168.0.12/MiAgenda/check_isLogged.php";
//    private String url_consulta2 = "http://192.168.0.12/MiAgenda/check_isConfirmed.php";
//    private String url_consulta3 = "http://192.168.0.12/MiAgenda/update_fechaLogin.php";
//    private String url_consulta4 = "http://192.168.0.12/MiAgenda/check_isLocked.php";
//
//    private String url_consulta = "http://192.168.0.159/MiAgenda/check_isLogged.php";
//    private String url_consulta2 = "http://192.168.0.159/MiAgenda/check_isConfirmed.php";
//    private String url_consulta3 = "http://192.168.0.159/MiAgenda/update_fechaLogin.php";
//    private String url_consulta4 = "http://192.168.0.159/MiAgenda/check_isLocked.php";

    private String url_consulta = "http://miagendafp.000webhostapp.com/check_isLogged.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/check_isConfirmed.php";
    private String url_consulta3 = "http://miagendafp.000webhostapp.com/update_fechaLogin.php";
    private String url_consulta4 = "http://miagendafp.000webhostapp.com/check_isLocked.php";
    public static boolean estaBloqueado = false;
    private StringRequest request; // petición de consulta

    // Obtención de fecha y hora
    public static String getFecha(){
        Date date = new Date();
        String fecha = date.toString();
        return fecha;
    }

    private String getAndroidVersion(){
        String version = Build.VERSION.RELEASE;
        return version;
    }

    private String fecha_ultimo_login = getFecha(); // aquí lo obtenemos así porque el usuario no interacciona de ninguna manera con la interfaz, se carga t0do
    // automático y no hay tiempos de espera para que el usuario interaccione, con lo que la fecha se va a obtener bien
    // Por ejemplo, en PantallaLogin se podría dar el caso de que el usuario se quede en la pantalla 5 minutos porque está poniendo mal la contraseña.
    // Si lo hiciesemos de esta misma manera, se obtendría la hora en la que se ha abierto la actividad, no en la que realmente puede haber hecho el login

    // ****************************************  VALORES GUARDADOS EN PREFERENCIAS ****************************************************
    static String nombre_de_usuario = ""; // nombre del usuario que ha hecho login. En la primera ejecución de la app estará en blanco
    static String codigo_de_confirmacion; // código de confirmación de registro que se le ha enviado al usuario
    static String correo_de_usuario; // correo electrónico que se obtiene del usuario que ha hecho login
    // ********************************************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);
        getSupportActionBar().hide(); // para ocultar la barra de titulo de la pantalla (toolbar/actionbar)
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, // para poner en pantalla completa la actividad, así no se verá la barra
                WindowManager.LayoutParams.FLAG_FULLSCREEN);        // de notificaciones con la hora etc. (luego vuelve a aparecer)
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_IN); // coloreamos el progressbar circular
        System.out.println("VERSIÓN ANDROID DEL DISPOSITIVO: " + getAndroidVersion());
        // Referenciamos al SharedPreferences que hemos creado en la clase PantallaLogin
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        // Obtenemos los datos de las preferencias
        nombre_de_usuario = preferences.getString("nombre_de_usuario", "");
        codigo_de_confirmacion = preferences.getString("codigo_de_confirmacion", "");
        correo_de_usuario = preferences.getString("correo_de_usuario", "");
        // ********************************************************************************************************************************************
        // Las mostramos en la consola de debug para controlar los datos que tenemos almacenados por el momento
        //Log.d("PantallaCarga", "Nombre de usuario almacenado: " + nombre_de_usuario);
        //Log.d("PantallaCarga", "Correo electrónico almacenado: " + correo_de_usuario);
        //Log.d("PantallaCarga", "Hora actual: "+fecha_ultimo_login);
        // Comprobamos conexión a internet del dispositivo
        checkConexion();
    }

    /***********************************************************************************************
     * Método que comprueba si el dispositivo tiene conexión a internet
     **********************************************************************************************/
    private void checkConexion(){
        //Log.d("PantallaCarga", "Comprobando conexión a internet en el dispositivo:");
        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean conexionWifi = false;
        boolean conexionDatos = false;

        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected()) {
                conexionWifi = true;
                //Log.d("PantallaCarga", "Hay conexión WiFi");
            }
            if (mMobile.isConnected()) {
                conexionDatos = true;
                //Log.d("PantallaCarga", "Hay conexión de datos móviles");
            }

            if (conexionWifi == true || conexionDatos == true) {
               /// tenemos conexión a internet, seguimos con la siguiente comprobación:
                if (!nombre_de_usuario.isEmpty()) { // Si hay nombre de usuario almacenado...
                    // Comprobamos bloqueo:
                    //Log.d("PantallaCarga", "Hay conexión. Comprobamos bloqueo de usuario:");
                    check_isLocked();
                } else { // si no lo hay, vamos a la pantalla de login
                    //Log.e("PantallaCarga", "No hay usuario almacenado");
                    abrePantallaLogin();
                }
            }
        } else {
            // No está conectado a internet, mostramos mensaje de alerta
            //Log.i("PantallaCarga", "No hay conexión a internet");
            AlertDialog.Builder builder = new AlertDialog.Builder(PantallaCarga.this);
            builder.setTitle(R.string.title_dialog_conexion); // titulo del diálogo
            builder.setMessage(R.string.info_conexion)
                    .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           // forzamos cierre de la aplicación para que cada vez que se abra la app se
                            // muestre el mensaje y el usuario se vea obligado a conectarse a internet
                            // para usar la aplicación
                            //Log.d("PantallaCarga", "Aplicación cerrada");
                            finish();
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false); // impedimos que el diálogo se pueda cerrar al pulsar fuera del mismo
            // para que no aparezca la pantalla en carga infinita, ya que al no haber internet no se accederá
            // a la app
        }
    }

    /***********************************************************************************************
     * Método que comprueba si el usuario que intenta iniciar sesión está bloqueado (estado isLocked)
     **********************************************************************************************/
    private void check_isLocked(){
        //Log.d("PantallaCarga", "Comprobando bloqueo de usuario:");
        request = new StringRequest(Request.Method.POST, url_consulta4,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("0")) { // Usuario NO BLOQUEADO
                            //Log.i("PantallaCarga", "No está bloqueado");
                            // si no está bloqueado, comprobamos si está confirmado:
                            check_isConfirmed();
                        } else { // Usuario BLOQUEADO
                            // Impedimos el acceso y mandamos a la pantalla de inicio de sesión, aunque no podrá iniciar sesión
                            // al estar bloqueado
                            //Log.i("PantallaCarga", "Está bloqueado");
                            estaBloqueado = true;
                            Toast.makeText(PantallaCarga.this, R.string.aviso_usuario_bloqueado_2, Toast.LENGTH_LONG).show();
                            abrePantallaLogin(); // en la pantalla de login está el botón de acerca de donde se puede ver el correo de contacto
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Se ejecuta cuando algo sale mal en la consulta
                       // Toast.makeText(PantallaCarga.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        Snackbar.make(findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.e("PantallaCarga", "Error al conectar con el servidor para comprobar el bloqueo del usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Enviamos los datos empaquetados en un objeto MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_de_usuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }


    /*******************************************************************************************
     *  Método que comprueba si el usuario ha confirmado su registro (estado isConfirmed)
     ******************************************************************************************/

    // En teoría esta comprobación no sería necesaria, porque la preferencia de nombre de usuario
    // no se guarda hasta que no se haga un login correcto, y para hacer el login correcto se tiene
    // que tener la cuenta confirmada. Por tanto esta comprobación nunca dará que no está confirmado,
    // salvo que lo cambiase el administrador desde la base de datos y el usuario de la aplicación
    // ya hubiese hecho inicios de sesión correctos previos
    public void check_isConfirmed(){
        //Log.i("PantallaCarga", "Comprobando confirmación de registro:");
            request = new StringRequest(Request.Method.POST, url_consulta2,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (response.equals("0")) { // si devuelve 0 significará que no ha confirmado su registro
                                    // así que le mandamos a la pantalla de confirmación de registro para que introduzca
                                    // el código que se le ha enviado al correo que introdujo en el formulario de registro

                                    // PARA TARDAR X SEGUNDOS DE CARGA ANTES DE ABRIR LA SIGUIENTE ACTIVIDAD:
                                    // (comentado para no hacer más larga la espera, de por sí ya tarda algunos segundos)

                                    //new Handler().postDelayed(new Runnable() {
                                        //public void run() {
                                            // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                                            Intent intent = new Intent(PantallaCarga.this, ConfirmaRegistro.class);
                                            startActivity(intent);
                                            finish();
                                      //  };
                                    //}, DURACION_SPLASH);
                                } else { // si no devuelve un 0, asumimos que el usuario sí está confirmado, y pasamos a comprobar si está logeado
                                    // porque solo puede devolver un 1 o un 0. Si no es 0, tiene que ser 1.
                                    //Log.i("PantallaCarga", "Usuario confirmado");
                                    check_isLogged();
                                }

                            } catch (Exception e) {
                                //Log.e("PantallaCarga", "Error al realizar la consulta de comprobación de confirmación");
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                           // Toast.makeText(PantallaCarga.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                            Snackbar.make(findViewById(android.R.id.content),
                                    R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                            //Log.e("PantallaCarga", "Error al conectar con el servidor para comprobar la confirmación del usuario");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("nUsuario", nombre_de_usuario);
                    return parametros;
                }
            };
            AppController.getInstance().addToRequestQueue(request);
    }

    /***************************************************************************************
     *  Método que comprueba si el usuario ya ha iniciado sesión (estado isLogged)
     ***************************************************************************************/
    public void check_isLogged(){
        //Log.d("PantallaCarga", "Comprobando login:");
            // INICIAMOS CONEXIÓN CON VOLLEY
            request = new StringRequest(Request.Method.POST, url_consulta,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (response.equals("1")) { // si devuelve 1, significará que sí se había logeado
                                    // así que le mandaremos a la pantalla principal, sin hacer el login de nuevo
                                    //Log.d("PantallaCarga", "Sesión activa. Iniciamos sesión automáticamente");
                                    // Almacenamos primero el dato de la fecha como dato de última sesión iniciada:
                                    actualizaFechaLogin(); // solo se actualiza (en esta clase) si isLogged está a 1, que es
                                    // cuando se hace inicio de sesión automático

                                    // PARA TARDAR X SEGUNDOS DE CARGA ANTES DE ABRIR LA SIGUIENTE ACTIVIDAD
                                    // (comentado para no hacer más larga la espera, de por sí ya tarda algunos segundos)
                                    //new Handler().postDelayed(new Runnable() {
                                      //  public void run() {
                                            // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                                            Intent intent = new Intent(PantallaCarga.this, NavMenu.class);
                                            startActivity(intent);
                                            finish();
                                        //};
                                    //}, DURACION_SPLASH);
                                } else {
                                    if (response.equals("0")){ // si devuelve 0 significará que no se ha logeado (o ha cerrado sesión)
                                        // así que le mandamos a la pantalla de login para que introduzca sus datos de usuario
                                        //Log.d("PantallaCarga", "Sesión no activa. Solicitamos credenciales de usuario");
                                        abrePantallaLogin();
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                                //Log.e("PantallaCarga", "Error al realizar la consulta de comprobación de login");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                         //   Toast.makeText(PantallaCarga.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                            Snackbar.make(findViewById(android.R.id.content),
                                    R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                            //Log.e("PantallaCarga", "Error al conectar con el servidor para comprobar el login del usuario");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("nUsuario", nombre_de_usuario);
                    return parametros;
                }
            };
            AppController.getInstance().addToRequestQueue(request);
    }


    /***********************************************************************************************
     * Método que actualiza el registro con la fecha de último inicio de sesión del usuario
     **********************************************************************************************/
    public void actualizaFechaLogin(){
        //Log.d("PantallaCarga", "Actualizamos fecha de último inicio de sesión");
        request = new StringRequest(Request.Method.POST, url_consulta3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fecha_ultimo_login = getFecha();
                        //Log.i("PantallaCarga", "Fecha obtenida: "+ fecha_ultimo_login);
                        //Log.i("PantallaCarga", "Fecha actualizada");
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                       // Toast.makeText(PantallaCarga.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        Snackbar.make(findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.e("PantallaCarga", "Error al conectar con el servidor para actualizar la fecha de último inicio de sesión");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
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
        //Log.d("PantallaCarga", "No hay datos del usuario o no hay sesión activa");
        //Log.i("PantallaCarga", "Abrimos pantalla de inicio de sesión");
        // Para dejar la actividad visible durante X segundos
        // (comentado para no hacer más larga la espera, de por sí ya tarda algunos segundos)
        // Después se pasa a la otra pantalla
       // new Handler().postDelayed(new Runnable() {
         //   public void run() {
                Intent intent = new Intent(PantallaCarga.this, PantallaLogin.class);
                startActivity(intent);
                finish();
           // }

           // ;
       // }, DURACION_SPLASH);
    }
}
