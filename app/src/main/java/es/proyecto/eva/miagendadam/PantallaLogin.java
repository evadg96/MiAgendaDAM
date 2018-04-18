package es.proyecto.eva.miagendadam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import es.proyecto.eva.miagendadam.VolleyController.AppController;
import static es.proyecto.eva.miagendadam.PantallaCarga.estaBloqueado;

public class PantallaLogin extends AppCompatActivity {
    private Button btnIniciarSesion,  btnRegistroUsuario,  btnRecuperarClave, btnDesbloquearCuenta;
    private EditText txtNombreUsuario, txtClave;
    private LinearLayout desbloquearCuenta; // perteneciente al apartado de desbloqueo del usuario
    // es la capa que contiene el campo de texto para introducir el código que se le envió al usuario
    // al correo, y con el que desbloqueará la cuenta. Por defecto es invisible, pero se visualiza cuando
    // se ha enviado el código correctamente al usuario tras comprobar que este existe en la base de datos.

//    private String url_consulta = "http://192.168.0.12/MiAgenda/check_usuario_existe.php";
//    private String url_consulta2 = "http://192.168.0.12/MiAgenda/update_isLogged.php";
//    private String url_consulta3 = "http://192.168.0.12/MiAgenda/check_isLocked.php";
//    private String url_consulta4 = "http://192.168.0.12/MiAgenda/update_isLocked.php";
//    private String url_consulta5 = "http://192.168.0.12/MiAgenda/check_isConfirmed.php";
//    private String url_consulta6 = "http://192.168.0.12/MiAgenda/check_clave.php";
//    private String url_consulta7 = "http://192.168.0.12/MiAgenda/check_num_intentos_login.php";
//    private String url_consulta8 = "http://192.168.0.12/MiAgenda/update_intentos_login.php";
//    private String url_consulta9 = "http://192.168.0.12/MiAgenda/update_fecha_bloqueo.php";
//    private String url_consulta10 = "http://192.168.0.12/MiAgenda/consulta_recuperar_correo.php";
//    private String url_consulta11 = "http://192.168.0.12/MiAgenda/consulta_recuperar_id_usuario.php";
//
//    private String url_consulta = "http://192.168.0.159/MiAgenda/check_usuario_existe.php";
//    private String url_consulta2 = "http://192.168.0.159/MiAgenda/update_isLogged.php";
//    private String url_consulta3 = "http://192.168.0.159/MiAgenda/check_isLocked.php";
//    private String url_consulta4 = "http://192.168.0.159/MiAgenda/update_isLocked.php";
//    private String url_consulta5 = "http://192.168.0.159/MiAgenda/check_isConfirmed.php";
//    private String url_consulta6 = "http://192.168.0.159/MiAgenda/check_clave.php";
//    private String url_consulta7 = "http://192.168.0.159/MiAgenda/check_num_intentos_login.php";
//    private String url_consulta8 = "http://192.168.0.159/MiAgenda/update_intentos_login.php";
//    private String url_consulta9 = "http://192.168.0.159/MiAgenda/update_fecha_bloqueo.php";
//    private String url_consulta10 = "http://192.168.0.159/MiAgenda/consulta_recuperar_correo.php";
//    private String url_consulta11 = "http://192.168.0.159/MiAgenda/consulta_recuperar_id_usuario.php";
    /*****************************************************************************************
     *                              SERVIDOR REMOTO
     ****************************************************************************************/
    private String url_consulta = "http://miagendafp.000webhostapp.com/check_usuario_existe.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/update_isLogged.php";
    private String url_consulta3 = "http://miagendafp.000webhostapp.com/check_isLocked.php";
    private String url_consulta4 = "http://miagendafp.000webhostapp.com/update_isLocked.php";
    private String url_consulta5 = "http://miagendafp.000webhostapp.com/check_isConfirmed.php";
    private String url_consulta6 = "http://miagendafp.000webhostapp.com/check_clave.php";
    private String url_consulta7 = "http://miagendafp.000webhostapp.com/check_num_intentos_login.php";
    private String url_consulta8 = "http://miagendafp.000webhostapp.com/update_intentos_login.php";
    private String url_consulta9 = "http://miagendafp.000webhostapp.com/update_fecha_bloqueo.php";
    private String url_consulta11 = "http://miagendafp.000webhostapp.com/consulta_recuperar_id_usuario.php";
    private String url_consulta12 = "http://miagendafp.000webhostapp.com/clave_gmail.php";

    private String nombre_usuario = ""; // para guardar el nUsuario cuando confirmamos que es válido
    private String clave = "";
    private String nUsuario=""; // el nombre de usuario que introduce el usuario para logearse (no tiene por qué ser válido, hay que comprobarlo)
    private String correo_de_usuario = ""; // será el email que le corresponde al usuario, y se obtendrá por consulta
    private String familiaCiclo = "";
    private String horas_fct = "";
    private String provincia = "";
    private String idUsuario = ""; // el identificador de usuario que utilizaremos para realizar consultas posteriores
    // a su familia
    private StringRequest request;

    public String getFecha() {
        Date date = new Date();
        String fecha = date.toString();
        return fecha;
    }

    // por el usuario
    private String pattern_formato_n_usuario_y_clave = "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z" // minúsculas
            + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z" // mayúsculas
            + "|0|1|2|3|4|5|6|7|8|9" // números
            + "|!|=|-|_|@|:|%|~|#)+";

    private String fecha_ultimo_login = "";
    private String fecha_bloqueo = "";
    // Declaramos el número de intentos de inicio de sesión base, para ir restándolo y mostrándoselo al usuario con cada intento fallido que haga
    private String intentos_login = "";
    private Session session;
    private String motivo_bloqueo = "";
    private String valor_isLocked = "";
    private int codigo_desbloqueo = 0;
    private String sCodigoDesbloqueo = "";
    private ProgressDialog progressDialog;

    private String codificaString(String dato){
        String datoCodificado = "";
        try {
            byte[] arrByteNombre = dato.getBytes("ISO-8859-1");
            datoCodificado = new String(arrByteNombre);
        } catch (Exception e){
            e.printStackTrace();
        }
        return datoCodificado;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_login);
        setTitle(R.string.title_activity_login);
        btnIniciarSesion = (Button) findViewById(R.id.btn_iniciar_sesion);
        btnRegistroUsuario = (Button) findViewById(R.id.btn_registrarse);
        btnRecuperarClave = (Button) findViewById(R.id.btn_recuperar_clave);
        btnDesbloquearCuenta = (Button) findViewById(R.id.btn_desbloquear_cuenta);
        txtNombreUsuario = (EditText) findViewById(R.id.editText_nombre_usuario);
        txtClave = (EditText) findViewById(R.id.editText_clave);
        desbloquearCuenta = (LinearLayout) findViewById(R.id.opc_recuperar_cuenta);
        // Si se detecta desde la pantalla de carga que está bloqueado el usuario en preferencias,
        // mostramos el campo directamente
        if (estaBloqueado){
           desbloquearCuenta.setVisibility(View.VISIBLE);
        }

        // Botón Registrarse, abre actividad de RegistroNuevoUsuario
        btnRegistroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("PantallaLogin", "Registrar nuevo usuario");
                Intent intent = new Intent(PantallaLogin.this, RegistroNuevoUsuario.class);
                startActivity(intent);
            }
        });

        // Botón He olvidado mi contraseña, abre actividad de RecuperarDatosUsuario
        btnRecuperarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("PantallaLogin", "Recuperar datos de usuario");
                Intent intent = new Intent(PantallaLogin.this, RecuperarDatosUsuario.class);
                startActivity(intent);
            }
        });

        // Botón Iniciar sesión
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               Log.i("PantallaLogin", "Iniciar sesión");
                nUsuario = txtNombreUsuario.getText().toString();
                clave = txtClave.getText().toString();
                // Actualizamos la hora cada vez que se pulse el botón
                fecha_bloqueo = getFecha();
                fecha_ultimo_login = getFecha();
               Log.i("PantallaLogin", "Fecha obtenida actual: "+ getFecha());
                if (nUsuario.isEmpty()) { // validamos que el campo no se haya dejado en blanco
                   // Snackbar.make(findViewById(android.R.id.content),
                     //       R.string.error_introducir_nombre_usuario, Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(PantallaLogin.this, R.string.error_introducir_nombre_usuario, Toast.LENGTH_SHORT).show();
                } else {
                    if (clave.isEmpty()) {
                      //  Snackbar.make(findViewById(android.R.id.content),
                        //        R.string.error_introducir_clave, Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(PantallaLogin.this, R.string.error_introducir_clave, Toast.LENGTH_SHORT).show();
                    } else {
                        if (!nUsuario.matches(pattern_formato_n_usuario_y_clave) || !clave.matches(pattern_formato_n_usuario_y_clave)){
                            Toast.makeText(PantallaLogin.this, R.string.error_formato_clave, Toast.LENGTH_SHORT).show();
                        } else {
                            System.out.println("DATOS INTRODUCIDOS: " + nUsuario + " " + clave);
                            progressDialog = new ProgressDialog(PantallaLogin.this);
                            progressDialog.setTitle(R.string.dialog_cargando);
                            progressDialog.setMessage("Comprobando datos. Por favor, espera un momento.");
                            progressDialog.show();
                            compruebaUsuario();
                        }
                    }
                }
            }
        });

        // Botón de desbloqueo de cuenta de usuario. Solo aparece si se detecta al usuario bloqueado
        btnDesbloquearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("PantallaLogin", "Registrar nuevo usuario");
                desbloquearUsuario();
            }
        });
    }

    /***********************************************************************************************
     * Método que inicia el flujo de comprobación de datos:
     * 1. Comprueba si el usuario introducido existe. Si existe, continúa con las comprobaciones (2),
     * si no, muestra alerta de que no existe.
     * 2. Comprueba  si el usuario está confirmado. Si lo está, pasa a 3. Si no, se manda a pantalla de
     * confirmación.
     * 3. Comprueba si el usuario está bloqueado. Si es así, le muestra un mensaje informándole. Si
     * no, pasa a comprobar la clave del usuario (4).
     * 4. Comprueba la contraseña del usuario. Si es correcta se hace login y se guarda en preferenias
     * el nombre del usuario para posteriores usos. Si no es correcta se van restando intentos hasta
     * que los intentos se agoten por completo. Entonces se bloqueará al usuario enviándole un correo
     * de aviso.
     ***********************************************************************************************/
    private void compruebaUsuario(){
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("2")) { // ERROR: usuario no existe
                            try {
                                progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                                  Toast.makeText(PantallaLogin.this, R.string.error_usuario_no_existe, Toast.LENGTH_SHORT).show();
                                //Snackbar.make(findViewById(android.R.id.content),
                                  //      R.string.error_usuario_no_existe, Snackbar.LENGTH_LONG).show();
                                //Log.i("PantallaLogin", "El usuario introducido no existe");
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                                //Log.e("PantallaLogin", "Error al comprobar el usuario");
                            }
                        } else { // Sí existe el usuario
                            nombre_usuario = nUsuario;
                            // ******* NO MOVER PORQUE SI NO NO SE OBTIENEN CORRECTAMENTE ********** //
                            obtenerDatosUsuario();
                            //**********************************************************************//
                            check_isConfirmed();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                       // Snackbar.make(findViewById(android.R.id.content),
                         //       R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.e("PantallaLogin", "Error al conectar con el servidor para comprobar el usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nUsuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     *  Método que comprueba si el usuario introducido está confirmado o no
     **********************************************************************************************/
    public void check_isConfirmed(){
        Log.i("PantallaLogin", "Comprobamos confirmación de registro del usuario");
        request = new StringRequest(Request.Method.POST, url_consulta5,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                        try {
                            if (response.equals("1")) { // SÍ está confirmado, comprobamos bloqueo
                                Log.i("PantallaLogin", "Usuario confirmado");
                                // obtenemos los datos del usuario para guardarlos
                                check_isLocked();
                            } else { // NO está confirmado, obligamos a confirmar
                                Log.i("PantallaLogin", "Usuario no confirmado");
                                progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                                // no hace falta comprobar isLogged, porque lógicamente es imposible que esté en 1 si no ha confirmado su registro
                                AlertDialog.Builder builder = new AlertDialog.Builder(PantallaLogin.this);
                                builder.setMessage(R.string.text_dialog_confirm)
                                        .setPositiveButton(R.string.btn_aceptar_confirm, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // Al dar a confirmar se manda a la pantalla de confirmación de registro
                                                Log.i("PantallaLogin", "Redirigimos a pantalla de confirmación de registro");
                                                Intent intent = new Intent(PantallaLogin.this, ConfirmaRegistro.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // Al dar a cancelar la ventana simplemente se cierra.
                                            }
                                        });
                                // Creamos el diálogo y lo mostramos
                                Dialog dialog = builder.create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                          progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                          Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                       // Snackbar.make(findViewById(android.R.id.content),
                         //       R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        Log.e("PantallaLogin", "Error al conectar con el servidor para comprobar la confirmación del usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     * Método que comprueba si el usuario que intenta iniciar sesión está bloqueado o no
     **********************************************************************************************/
    private void check_isLocked(){
        //  Log.i("PantallaLogin", "Comprobamos bloqueo del usuario");
        request = new StringRequest(Request.Method.POST, url_consulta3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("0")) { // usuario NO BLOQUEADO
                            //  Log.i("PantallaLogin", "El usuario no está bloqueado");
                            comprobarClave(); // comprobamos si la clave es correcta para hacer el login
                        } else {
                            progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                            // AL DETECTAR QUE ESTÁ BLOQUEADO, MOSTRAMOS DIRECTAMENTE EL CAMPO DE DESBLOQUEO
                            desbloquearCuenta.setVisibility(View.VISIBLE);
                            // LE PROHIBIMOS ACCEDER
                            //  Log.i("PantallaLogin", "El usuario está bloqueado");
                              Toast.makeText(PantallaLogin.this, R.string.aviso_usuario_bloqueado, Toast.LENGTH_SHORT).show();

                            //Snackbar.make(findViewById(android.R.id.content),
                              //      R.string.aviso_usuario_bloqueado, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                         Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        //Snackbar.make(findViewById(android.R.id.content),
                          //      R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.e("PantallaLogin", "Error al conectar con el servidor para comprobar el bloqueo del usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     *  Método que comprueba la clave introducida por el usuario
     **********************************************************************************************/
    public void comprobarClave() {
        //Log.i("PantallaLogin", "Comprobamos la clave introducida");
        request = new StringRequest(Request.Method.POST, url_consulta6,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("3")) { // ERROR: CONTRASEÑA ERRÓNEA
                            // INTENTO FALLIDO DE INICIO DE SESIÓN
                            //Log.i("PantallaLogin", "Contraseña incorrecta");
                            try {
                                progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                                // restamos intentos restantes de inicio de sesión
                                restaIntentos();
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                            }
                        } else { // Usuario y contraseña correctos
                            if (response.equals("4")) { // Respuesta "4" = login correcto.
                                //Log.i("PantallaLogin", "Contraseña correcta");
                                guardarPreferencias(); // guardamos los datos del usuario en las preferencias, para usarlo en clases futuras para
                                loginCorrecto(); // hacemos login
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                      //  Snackbar.make(findViewById(android.R.id.content),
                        //        R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.e("PantallaLogin", "Error al conectar con el servidor para comprobar la clave del usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                parametros.put("clave", clave);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     * Método que obtiene el número de intentos de inicio de sesión disponibles del usuario
     * y los actualiza
     **********************************************************************************************/
    public void restaIntentos(){
        //Log.i("PantallaLogin", "Restamos un intento de inicio de sesión");
        request = new StringRequest(Request.Method.POST, url_consulta7,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { // la respuesta que se obtiene es el número de intentos restantes del usuario
                        try {
                            int intentos_restantes = Integer.valueOf(response);
                            System.out.println("RESPUESTA: "+ response);
                            if (intentos_restantes > 0) { //  mientras quede algún intento, se siguen restando
                                Toast.makeText(PantallaLogin.this, "Contraseña incorrecta. Te quedan " + intentos_restantes + " intentos de inicios de sesión.", Toast.LENGTH_LONG).show();
                                //Log.d("PantallaLogin", "Intento restado");
                                intentos_restantes--; // restamos un intento
                                intentos_login = String.valueOf(intentos_restantes);
                                actualizaIntentos();
                                System.out.println("INTENTOS RESTANTES: "+intentos_restantes);
                                System.out.println("INTENTOS LOGIN: "+intentos_login);
                                if (intentos_restantes == 1) {
                                    //Log.d("PantallaLogin", "Quedan pocos intentos restantes");
                                    Toast toast = Toast.makeText(PantallaLogin.this, "Atención, solo quedan " + (intentos_restantes+1)+ " intentos de inicio de sesión. Si los agotas se bloqueará tu cuenta.", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.show();
                                }
                            } else { // Cuando los intentos se agoten, es decir, estén en 0, se bloquea al usuario
                                Log.i("PantallaLogin", "Intentos de inicio agotados. Bloqueamos usuario");
                                bloquearUsuario();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                       // Snackbar.make(findViewById(android.R.id.content),
                         //       R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        Log.e("PantallaLogin", "Error al conectar con el servidor para restar intentos de inicio de sesión");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     * Método que actualiza el número de intentos de login restantes del usuario
     **********************************************************************************************/
    public void actualizaIntentos(){
        Log.i("PantallaLogin", "Actualizamos el número de intentos de inicio de sesión del usuario");
        request = new StringRequest(Request.Method.POST, url_consulta8,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Se actualiza el número de intentos
                        Log.d("PantallaLogin", "Intentos actualizados");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                       // Snackbar.make(findViewById(android.R.id.content),
                         //       R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        Log.e("PantallaLogin", "Error al conectar con el servidor para actualizar el número de intentos de login");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                parametros.put("intentos_login", intentos_login);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     * Método que resetea el número de intentos de login restantes del usuario a 5 otra vez
     * cuando el usuario inicia sesión correctamente
     **********************************************************************************************/
    public void reseteaIntentos(){
        Log.i("PantallaLogin", "Reseteamos el número de intentos de inicio de sesión");
        intentos_login = "5";
        request = new StringRequest(Request.Method.POST, url_consulta8,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Se resetea el número de intentos
                        Log.i("PantallaLogin", "Intentos reseteados a 5");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                       // Snackbar.make(findViewById(android.R.id.content),
                         //       R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        Log.e("PantallaLogin", "Error al conectar con el servidor para resetear los intentos de login");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                parametros.put("intentos_login", intentos_login);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     *   Método que bloquea a un usuario cuando ha hecho demasiados intentos de inicio de sesión
     **********************************************************************************************/
    public void bloquearUsuario() {
        Log.d("PantallaLogin", "Bloqueamos usuario por exceso de intentos de inicio de sesión");
        motivo_bloqueo = "Demasiados intentos de inicio de sesión";
        valor_isLocked = "1";
        intentos_login = "0";
        request = new StringRequest(Request.Method.POST, url_consulta4,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("ESTÁ BLOQUEADO? = " + response);
                        if (response.equals("0")) { // el dato que se obtiene como respuesta es el valor de isLocked. Si está a 0, es que no está bloqueado, así que se bloquea
                            // Esta validación se hace porque este método se ejecuta cada vez que se detecte que los intentos de login están a 0, y si se le ha bloqueado tendrá
                            // siempre los intentos a 0, así que siempre se ejecutará este método.
                            // Validamos para que no se cambie la fecha de bloqueo cada vez que se ejecute el método ni se envíe varias veces el correo de aviso.
                            actualizaFechaBloqueo();
                            enviarCorreoAvisoBloqueo();
                            desbloquearCuenta.setVisibility(View.VISIBLE);
                           Log.i("PantallaLogin", "Bloqueo correcto");
                            //Toast toast = Toast.makeText(PantallaLogin.this, R.string.aviso_bloqueo_realizado, Toast.LENGTH_LONG);
                            //toast.show();

                            // En lugar de un Toast, ponemos un diálogo para alargar un poco la espera, ya que si se pulsa muy seguido el botón de
                            // iniciar sesión cuando se acaba de bloquear al usuario no sé por qué no se coge bien que isLocked es igual a 1, y se volvería
                            // a ejecutar t0do esto.
                            AlertDialog.Builder builder = new AlertDialog.Builder(PantallaLogin.this);
                            builder.setMessage(R.string.aviso_bloqueo_realizado)
                                    .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // al pulsar aceptar, no se hace nada, se cierra el diálogo
                                        }
                                    });
                            // Creamos el diálogo y lo mostramos
                            Dialog dialog = builder.create();
                            dialog.show();
                            //Snackbar.make(findViewById(android.R.id.content),
                                //    R.string.aviso_bloqueo_realizado, Snackbar.LENGTH_LONG).show();
                        } else { // El usuario ya estaba bloqueado, no actualizamos fecha ni bloqueamos.
                          //  Log.i("PantallaLogin", "El usuario ya está bloqueado");
                            Toast toast = Toast.makeText(PantallaLogin.this, R.string.aviso_usuario_bloqueado, Toast.LENGTH_LONG);
                            toast.show();
                          //  Snackbar.make(findViewById(android.R.id.content),
                            //        R.string.aviso_usuario_bloqueado, Snackbar.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                      //  Snackbar.make(findViewById(android.R.id.content),
                        //        R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        Log.e("PantallaLogin", "Error al conectar con el servidor para bloquear al usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                parametros.put("valor_isLocked", valor_isLocked);
                parametros.put("motivo_bloqueo", motivo_bloqueo);
                parametros.put("intentos_login", intentos_login);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /*******************************************************************************************************
     * Método que actualiza el campo de fecha en la que se ha bloqueado al usuario y se introduce en la bd
     ******************************************************************************************************/
    public void actualizaFechaBloqueo(){
       Log.i("PantallaLogin", "Guardamos fecha de bloqueo del usuario");
        request = new StringRequest(Request.Method.POST, url_consulta9,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fecha_bloqueo = getFecha();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                      //  Snackbar.make(findViewById(android.R.id.content),
                        //        R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                       Log.e("PantallaLogin", "Error al conectar con el servidor para guardar la fecha de bloqueo");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                parametros.put("fecha_bloqueo", fecha_bloqueo);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     * Método que le envía un correo de aviso al usuario recién bloqueado por superar el número de
     * intentos de inicio de sesión indicándole el motivo del bloqueo y cómo recuperar su cuenta.
     **********************************************************************************************/
    public void enviarCorreoAvisoBloqueo(){
        request = new StringRequest(Request.Method.POST, url_consulta12,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final String clave_gmail = response;
                            Properties props = new Properties();
                            props.put("mail.smtp.host", "smtp.gmail.com");
                            props.put("mail.smtp.socketFactory.port", "465");
                            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                            props.put("mail.smtp.auth", "true");
                            props.put("mail.smtp.port", "465");

                            session = Session.getDefaultInstance(props, new Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication("noreply.miagendafp@gmail.com", clave_gmail);
                                }
                            });

                            RetreiveFeedTask task = new RetreiveFeedTask();
                            task.execute();

                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("ReenviarCodigoConf", "Error al enviar el correo");
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                      //  Snackbar.make(findViewById(android.R.id.content),
                        //        R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        Log.e("ReenviarCodigoConf", "Error al conectar con el servidor para obtener la clave del correo noreply...");
                    }
                });
        AppController.getInstance().addToRequestQueue(request);
    }

    // Clase con el contenido del correo electrónico
    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("noreply.miagendafp@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo_de_usuario));
                message.setSubject("No-reply: Bloqueo de cuenta");
                message.setContent("<p style=\"text-align:justify\"> ¡Hola <b>"+ nombre_usuario+"</b>!</p> <p style=\"text-align:justify\"> Hemos detectado que has superado el número máximo de intentos de inicio de sesión permitido en <b>Mi agenda FP</b> " +
                        "al haber introducido mal tu contraseña más de cinco veces, así que hemos procedido a bloquear tu cuenta por motivos de seguridad.\n</p>" +
                        "<p style=\"text-align:justify\">Queremos recordarte que si has olvidado tu contraseña puedes recuperarla a través de la opción <b>He olvidado mis datos de usuario</b>, que encontrarás en la pantalla de inicio de sesión.</p>"+
                        "<p style=\"text-align:justify\">Podrás recuperar tu cuenta accediendo a la aplicación y pulsando sobre la opción <b>Desbloquear mi cuenta</b> que aparecerá en la pantalla de inicio de sesión.</p> " +
                        "<p style=\"text-align:justify\">Esta opción aparece automáticamente cuando se detecta que el usuario que está intentando iniciar sesión está bloqueado. Si no la ves, intenta iniciar sesión con tu cuenta bloqueada y deberá aparecerte abajo.</p> " +
                        "<p style=\"text-align:justify\">Al pulsar sobre ella aparecerá un cuadro de diálogo. Deberás pulsar sobre el botón <b>Enviar código de desbloqueo</b>. Se te enviará a continuación un correo electrónico con un <b>código de desbloqueo</b>, que después deberás introducir" +
                        " en el campo <b>Código de desbloqueo</b>. Cuando lo hayas escrito, pulsa sobre <b>Validar código</b>. Si el código es correcto, desbloquearás tu cuenta al instante.</p> " +
                        "<p style=\"text-align:justify\">Sentimos lo ocurrido y esperamos que vuelvas lo antes posible.</p>" +
                        "<p style=\"text-align:justify\">Atentamente, Mi agenda FP.</p> "+
                        "<div style=\"background-color:#EEEEEE; border:1px solid #BABABA; box-shadow: 2px 2px 5px #999; font-size:10px; text-align:justify\">" + // el sombreado no se ve en el móvil
                        "<p style=\"margin-left: 10px; margin-right: 11px\">" +
                        "Este mensaje se ha generado automáticamente. Por favor <b>no responda a este correo</b>, no recibirá ninguna respuesta.\n" +
                        "<br/>Si tiene algún problema, duda o sugerencia, contacte con el soporte a través de la dirección de correo <b>soportemiagendafp@gmail.com</b>\n" +
                        "<br/>Si ha recibido este correo por error, por favor, le rogamos que lo elimine y se ponga en contacto con la dirección de correo indicada arriba.\n", "text/html; charset=utf-8");
                Transport.send(message);
            } catch(MessagingException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ReenviarCodigoConf", "Correo enviado");
        }
    }


    /***********************************************************************************************************************
     * Método que obtiene los datos del usuario que ha hecho inicio de sesión (id del usuario, familia del ciclo y correo)
     **********************************************************************************************************************/
    private void obtenerDatosUsuario(){
        Log.d("PantallaLogin", "Obtenemos datos del usuario para guardarlos");
        request = new StringRequest(Request.Method.POST, url_consulta11,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response); // creamos array json para obtener el objeto del correo
                            idUsuario = jsonArray.getJSONObject(0).getString("idUsuario");
                            familiaCiclo = codificaString(jsonArray.getJSONObject(0).getString("familia_ciclo"));
                            correo_de_usuario = jsonArray.getJSONObject(0).getString("correo");
                            horas_fct = jsonArray.getJSONObject(0).getString("horas_fct");
                            provincia = jsonArray.getJSONObject(0).getString("provincia");
                            Log.d("PantallaLogin","ID DEL USUARIO "+ idUsuario);
                            Log.d("PantallaLogin","FAMILIA DEL CICLO DEL USUARIO " + familiaCiclo);
                            Log.d("PantallaLogin","CORREO ELECTRÓNICO "+ correo_de_usuario);
                            Log.d("PantallaLogin", "HORAS FCT " + horas_fct);
                        } catch (Exception e){
                            e.printStackTrace();
                            Log.e("PantallaLogin", "Error al obtener datos del usuario");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                       // Snackbar.make(findViewById(android.R.id.content),
                            //    R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        Log.e("PantallaLogin", "Error al conectar con el servidor para obtener los datos de usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /*********************************************************************************************************************
     * Método que se ejecuta cuando se han verificado todos los datos necesarios para hacer un inicio de sesión correcto
     ********************************************************************************************************************/
    private void loginCorrecto(){
        Log.i("PantallaLogin", "Login correcto");
        reseteaIntentos(); // reseteamos número de intentos de login restantes
        // Creamos ventana de diálogo con circulo de carga para la espera de carga de los datos
        // Cargamos la pantalla principal de la aplicación
        progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
        // A continuación cambiamos el valor de isLogged a 1 para hacer login automático en la pantalla de carga en la próxima apertura de la app
        // y la fecha de último login
        request = new StringRequest(Request.Method.POST, url_consulta2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("PantallaLogin", "Actualizamos valor de isLogged");
                        fecha_ultimo_login = getFecha();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel(); // cerramos el diálogo de cargando para mostrar el error
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                     //   Snackbar.make(findViewById(android.R.id.content),
                       //         R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        Log.e("PantallaLogin", "Error al conectar con el servidor para actualizar valor del login y fecha de inicio de sesión");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                parametros.put("fecha_ultimo_login", fecha_ultimo_login);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
        // Abrimos el menú pcpal de la app y finalizamos la actividad actual
        Intent intent = new Intent(PantallaLogin.this, NavMenu.class);
        startActivity(intent);
        finish();
    }

    /*********************************************************************************************************************************
     * Mediante este método guardamos como preferencias el nombre de usuario que el usuario haya introducido al hacer inicio de sesión
     * para poder utilizarlo después en todas las consultas que utilicen como filtro el nombre del usuario (que serán casi todas)
     ********************************************************************************************************************************/
    private void guardarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nombre_de_usuario", nombre_usuario);
        editor.putString("correo_de_usuario", correo_de_usuario);
        editor.putString("familia_ciclo", familiaCiclo);
        editor.putString("horas_fct", horas_fct);
        editor.putString("provincia", provincia);
        editor.putString("idUsuario", idUsuario);
        editor.commit();
        Log.d("PantallaLogin", "Preferencias guardadas");
    }

    /***********************************************************************************************
     * Menú de la barra de acciones
     **********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu); // la R referencia a la ubicación del archivo
        return true; // .menu es el directorio, y .toolbar el archivo
    }

    /***********************************************************************************************
     * Iconos del menú de la barra de acciones
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_acerca_de:
                Intent intent = new Intent (PantallaLogin.this, AcercaDe.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***********************************************************************************************
     * Método que se ejecuta al pulsar Atrás
     **********************************************************************************************/
 //   @Override
  //  public void onBackPressed() {
        // dejamos en blanco para que no se haga nada
   // }

    /***********************************************************************************************
     * Método que valida la clave introducida por el usuario para desbloquear su cuenta
     **********************************************************************************************/
    public void desbloquearUsuario(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final Button btnEnviarCodigoRC, btnValidarCodigoRC, btnCancelarRecuperaCuenta;
        final EditText txtCodigo;
        final AlertDialog dialog = alert.create();
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.recuperar_cuenta_usuario, (ViewGroup) findViewById(R.id.recuperar_cuenta_usuario));
        btnEnviarCodigoRC = (Button) view.findViewById(R.id.btn_enviar_codigo);
        btnValidarCodigoRC = (Button) view.findViewById(R.id.btn_validar_codigo);
        btnCancelarRecuperaCuenta = (Button) view.findViewById(R.id.btn_recuperar_cuenta_cancelar);
        txtCodigo = (EditText) view.findViewById(R.id.txt_codigo_rc);
        dialog.setView(view);
        dialog.show();

        // enviamos el código al correo obtenido de los datos del usuario que ha intentado iniciar sesión
        btnEnviarCodigoRC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Primero validamos si hay correo en preferencias para enviarle el correo directamente
                // a ese usuario, o si no se le manda al usuario obtenido del intento de inicio de sesión
                // Lo haremos solo si esta variable está en true, porque significará que se ha comprobado
                // que el usuario está bloqueado desde la pantalla carga, lo cual quiere decir que se ha
                // hecho a través de las preferencias, lo que nos indica que tiene que haber un correo
                // guardado en ellas
                txtCodigo.setVisibility(View.VISIBLE); // hacemos visible el campo de validación de código
                btnValidarCodigoRC.setVisibility(View.VISIBLE); // y el botón de validar código
                if (estaBloqueado) {
                    SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                    correo_de_usuario = preferences.getString("correo_de_usuario", "");
                }
                generaCodigoDesbloqueo(); // generamos el código de desbloqueo
                enviarCodigoDesbloqueo(); // lo enviamos al usuario
                Toast.makeText(PantallaLogin.this, R.string.dialog_desbloqueo, Toast.LENGTH_LONG).show();
            }
        });

        // validamos el código introducido y si es igual al generado, desbloqueamos al usuario
        btnValidarCodigoRC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final String codigo = txtCodigo.getText().toString();
                SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                String codigo_de_desbloqueo = preferences.getString("codigo_de_desbloqueo", "");

                // si hay código de desbloqueo en las preferencias, seguimos
                if (!codigo_de_desbloqueo.isEmpty()) {
                    if (codigo.isEmpty()) { // si el campo de código a validar está vacío, no seguimos
                        //Snackbar.make(findViewById(android.R.id.content),
                          //      R.string.error_introducir_codigo, Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(PantallaLogin.this, R.string.error_introducir_codigo, Toast.LENGTH_SHORT).show();
                    } else {
                        if (codigo.equals(codigo_de_desbloqueo)) { // si el campo coincide con el codigo obtenido de las preferencias, desbloqueamos finalmente al usuario
                            motivo_bloqueo = "";
                            valor_isLocked = "0";
                            intentos_login = "5";
                            request = new StringRequest(Request.Method.POST, url_consulta4,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.i("PantallaLogin", "Desbloqueamos usuario");
                                            estaBloqueado = false;
                                          //  Snackbar.make(findViewById(android.R.id.content),
                                            //        R.string.usuario_desbloqueado, Snackbar.LENGTH_SHORT).show();
                                            Toast.makeText(PantallaLogin.this, R.string.usuario_desbloqueado, Toast.LENGTH_LONG).show();
                                            dialog.cancel(); // cerramos el diálogo de validación
                                            txtCodigo.setVisibility(View.GONE); // hacemos invisibles de nuevo los campos una vez que ya hemos desbloqueado al usuario
                                            btnValidarCodigoRC.setVisibility(View.GONE);
                                            // quitamos también la visibilidad del campo de desbloqueo
                                            desbloquearCuenta.setVisibility(View.GONE);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                             Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                                            //Snackbar.make(findViewById(android.R.id.content),
                                              //      R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                                            Log.e("PantallaLogin", "Error al conectar con el servidor para actualizar valor del login y fecha de inicio de sesión");
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> parametros = new HashMap<>();
                                    parametros.put("nUsuario", nombre_usuario);
                                    parametros.put("valor_isLocked", valor_isLocked);
                                    parametros.put("motivo_bloqueo", motivo_bloqueo);
                                    parametros.put("intentos_login", intentos_login);
                                    return parametros;
                                }
                            };
                            AppController.getInstance().addToRequestQueue(request);
                        } else {
                            //Snackbar.make(findViewById(android.R.id.content),
                              //      R.string.error_codigo_incorrecto, Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(PantallaLogin.this, R.string.error_codigo_incorrecto, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    //Snackbar.make(findViewById(android.R.id.content),
                      //      R.string.error_codigo_desbloqueo_expirado, Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(PantallaLogin.this, R.string.error_codigo_desbloqueo_expirado, Toast.LENGTH_SHORT).show();

                }
            }
        });
        btnCancelarRecuperaCuenta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // cierra el diálogo
                dialog.cancel();
            }
        });
    }

    /***********************************************************************************************
     * Método que genera el código de desbloqueo que se le enviará al usuario
     **********************************************************************************************/
    public void generaCodigoDesbloqueo() {
        // generamos un código aleatorio de 6 dígitos
        codigo_desbloqueo = (int) (Math.random() * 999999) + 1;
        sCodigoDesbloqueo = Integer.toString(codigo_desbloqueo); // pasamos el código a String para poder guardarlo como preferencia
        //Log.d("RegistroNuevoUsuario", "Código de confirmación generado");
        guardarCodigoDesbloqueo(); // guardamos el dato
    }

    /***********************************************************************************************
     * Método que guarda el código de desbloqueo en preferencias para compararlo después
     **********************************************************************************************/
    public void guardarCodigoDesbloqueo() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("codigo_de_desbloqueo", sCodigoDesbloqueo);
        editor.commit();
    }

    /***********************************************************************************************
     * Método que le envía un correo de aviso al usuario recién bloqueado por superar el número de
     * intentos de inicio de sesión indicándole el motivo del bloqueo y cómo recuperar su cuenta.
     **********************************************************************************************/
    public void enviarCodigoDesbloqueo(){
        request = new StringRequest(Request.Method.POST, url_consulta12,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final String clave_gmail = response;
                            Properties props = new Properties();
                            props.put("mail.smtp.host", "smtp.gmail.com");
                            props.put("mail.smtp.socketFactory.port", "465");
                            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                            props.put("mail.smtp.auth", "true");
                            props.put("mail.smtp.port", "465");

                            session = Session.getDefaultInstance(props, new Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication("noreply.miagendafp@gmail.com", clave_gmail);
                                }
                            });

                            RetreiveFeedTask2 task = new RetreiveFeedTask2();
                            task.execute();

                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("ReenviarCodigoConf", "Error al enviar el correo");
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        //Snackbar.make(findViewById(android.R.id.content),
                           //     R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        Log.e("ReenviarCodigoConf", "Error al conectar con el servidor para obtener la clave del correo noreply...");
                    }
                });
        AppController.getInstance().addToRequestQueue(request);
    }

    // Clase con el contenido del correo electrónico
    class RetreiveFeedTask2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("noreply.miagendafp@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo_de_usuario));
                message.setSubject("No-reply: Código de desbloqueo de cuenta Mi agenda FP");
                message.setContent("<p style=\"text-align:justify\"> ¡Hola <b>"+ nombre_usuario+"</b>!</p> <p style=\"text-align:justify\"> El código para desbloquear tu cuenta de <b>Mi agenda FP</b> es: <b>" + sCodigoDesbloqueo + "</b></p>"+
                        "<p style=\"text-align:justify\">Introduce este código pulsando la opción <b>Desbloquear mi cuenta</b> que encontrarás en la pantalla de inicio de sesión de <b>Mi agenda FP </b>.</p> " +
                        "<p style=\"text-align:justify\">Esta opción aparece automáticamente cuando se detecta que el usuario que está intentando iniciar sesión está bloqueado. Si no la ves, intenta iniciar sesión con tu cuenta bloqueada y deberá aparecerte abajo.</p> " +
                        "<p style=\"text-align:justify\">Al pulsar sobre ella aparecerá un cuadro de diálogo. Deberás introducir el código" +
                        " en el campo <b>Código de desbloqueo</b> y pulsar sobre <b>Validar código</b>. Si el código es correcto, desbloquearás tu cuenta al instante.</p> " +
                        "<p style=\"text-align:justify\">Atentamente, Mi agenda FP.</p> "+
                        "<div style=\"background-color:#EEEEEE; border:1px solid #BABABA; box-shadow: 2px 2px 5px #999; font-size:10px; text-align:justify\">" + // el sombreado no se ve en el móvil
                        "<p style=\"margin-left: 10px; margin-right: 11px\">" +
                        "Este mensaje se ha generado automáticamente. Por favor <b>no responda a este correo</b>, no recibirá ninguna respuesta.\n" +
                        "<br/>Si tiene algún problema, duda o sugerencia, contacte con el soporte a través de la dirección de correo <b>soportemiagendafp@gmail.com</b>\n" +
                        "<br/>Si ha recibido este correo por error, por favor, le rogamos que lo elimine y se ponga en contacto con la dirección de correo indicada arriba.\n", "text/html; charset=utf-8");
                Transport.send(message);
            } catch(MessagingException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ReenviarCodigoConf", "Correo enviado");
        }
    }
}
