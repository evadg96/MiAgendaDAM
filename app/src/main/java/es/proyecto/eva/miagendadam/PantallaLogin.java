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
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
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

import org.json.JSONArray;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.VolleyController.AppController;

public class PantallaLogin extends AppCompatActivity {
    private Button btnIniciarSesion,  btnRegistroUsuario,  btnRecuperarClave;
    private EditText txtNombreUsuario, txtClave;

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

    static String nombre_usuario = ""; // para guardar el nUsuario cuando confirmamos que es válido
    static String nUsuario=""; // el nombre de usuario que introduce el usuario para logearse (no tiene por qué se válido, hay que comprobarlo)
    static String clave="";
    private String correo_de_usuario = ""; // será el email que le corresponde al usuario, y se obtendrá por consulta
    private String idUsuario = ""; // el identificador de usuario que utilizaremos para realizar consultas posteriores
    private String familiaCiclo = ""; // la familia del ciclo del usuario para después recomendarle contenidos en base
    // a su familia
    private StringRequest request;

    public static String getFecha() {
        Date date = new Date();
        String fecha = date.toString();
        return fecha;
    }
    private String fecha_ultimo_login = "";
    private String fecha_bloqueo = "";
    // Declaramos el número de intentos de inicio de sesión base, para ir restándolo y mostrándoselo al usuario con cada intento fallido que haga
    private String intentos_login = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_login);
        setTitle(R.string.title_activity_login);
        btnIniciarSesion = (Button) findViewById(R.id.btn_iniciar_sesion);
        btnRegistroUsuario = (Button) findViewById(R.id.btn_registrarse);
        btnRecuperarClave = (Button) findViewById(R.id.btn_recuperar_clave);
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

        // Botón He olvidado mi contraseña, abre actividad de RecuperarDatosUsuario
        btnRecuperarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PantallaLogin.this, RecuperarDatosUsuario.class);
                startActivity(intent);
            }
        });

        // Botón Iniciar sesión
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                nUsuario = txtNombreUsuario.getText().toString();
                clave = txtClave.getText().toString();
                // Actualizamos la hora cada vez que se pulse el botón
                fecha_bloqueo = getFecha();
                fecha_ultimo_login = getFecha();
                System.out.println("HORA ACTUAL: "+ getFecha());
                if (nUsuario.isEmpty()) { // validamos que el campo no se haya dejado en blanco
                    Toast.makeText(PantallaLogin.this, R.string.error_introducir_nombre_usuario, Toast.LENGTH_SHORT).show();
                } else {
                    if (clave.isEmpty()) {
                        Toast.makeText(PantallaLogin.this, R.string.error_introducir_clave, Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("DATOS INTRODUCIDOS: " + nUsuario + " " + clave);
                        compruebaDatos();
                    }

                }
            }
        });
    }

    /***********************************************************************************************
     *   Método que bloquea a un usuario cuando ha hecho demasiados intentos de inicio de sesión
     **********************************************************************************************/
    public void bloquearUsuario() {
        request = new StringRequest(Request.Method.POST, url_consulta4,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("0")) { // el dato que se obtiene como respuesta es el valor de isLocked. Si está a 0, es que no está bloqueado, así que se bloquea
                            // Esta validación se hace porque este método se ejecuta cada vez que se detecte que los intentos de login están a 0, y si se le ha bloqueado tendrá
                            // siempre los intentos a 0, así que siempre se ejecutará este método.
                            // Validamos para que no se cambie la fecha de bloqueo cada vez que se ejecute el método.
                            actualizaFechaBloqueo();
                            System.out.println(fecha_bloqueo);
                            System.out.println("BLOQUEO CORRECTO DESDE MÉTODO BLOQUEARUSUARIO :)");
                            Toast toast = Toast.makeText(PantallaLogin.this, R.string.aviso_bloqueo_realizado, Toast.LENGTH_LONG);
                            toast.show();
                        } else { // El usuario ya estaba bloqueado, no actualizamos fecha ni bloqueamos.
                            System.out.println("YA ESTABA BLOQUEADO. NO SE ACTUALIZA LA FECHA.");
                            Toast toast = Toast.makeText(PantallaLogin.this, R.string.aviso_usuario_bloqueado, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        System.out.println("ERROR BLOQUEARUSUARIO()");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }




    /***********************************************************************************************
     * Método que obtiene la fecha en la que se ha bloqueado al usuario y se introduce en la bd
     **********************************************************************************************/
    public void actualizaFechaBloqueo(){
        request = new StringRequest(Request.Method.POST, url_consulta9,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fecha_bloqueo = getFecha();
                        System.out.println("FECHA DE BLOQUEO: "+ fecha_bloqueo);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        System.out.println("ERROR ACTUALIZAFECHABLOQUEO()");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                parametros.put("fecha_bloqueo", fecha_bloqueo);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     *  Método que comprueba si el usuario introducido está confirmado o no
     **********************************************************************************************/
    public void check_isConfirmed(){
        request = new StringRequest(Request.Method.POST, url_consulta5,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                        try {
                            if (response.equals("1")) { // SÍ está confirmado, hacemos login
                                System.out.println("SÍ ESTÁ CONFIRMADO (DESDE MÉTODO CHECK_ISCONFIRMED :) )");
                                System.out.println("Obtenemos datos del usuario");
                                obtenerDatosUsuario(); // obtenemos los datos del usuario para guardarlos
                                System.out.println("Guardamos preferencias");
                                guardarPreferencias(); // guardamos los datos del usuario en las preferencias, para usarlo en clases futuras para
                                System.out.println("Hacemos login");
                                loginCorrecto();
                            } else { // NO está confirmado, obligamos a confirmar
                                System.out.println(" NO ESTÁ CONFIRMADO (DESDE MÉTODO CHECK_ISCONFIRMED :( )");
                                // no hace falta comprobar isLogged, porque lógicamente es imposible que esté en 1 si no ha confirmado su registro
                                AlertDialog.Builder builder = new AlertDialog.Builder(PantallaLogin.this);
                                builder.setMessage(R.string.text_dialog_confirm)
                                        .setPositiveButton(R.string.btn_aceptar_confirm, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // Al dar a confirmar se manda a la pantalla de confirmación de registro
                                                Intent intent = new Intent(PantallaLogin.this, ConfirmaRegistro.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(R.string.btn_cancelar_confirm, new DialogInterface.OnClickListener() {
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
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        System.out.println("ERROR ONCREATE() HAS COMPROBADO SI LA IP ES CORRECTA?");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
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
        request = new StringRequest(Request.Method.POST, url_consulta3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                        if (response.equals("0")) { // usuario NO BLOQUEADO
                            System.out.println("NO ESTÁ BLOQUEADO DESDE CHECKISLOCKED :)");
                            check_isConfirmed(); // comprobamos si está confirmado
                        } else {
                            // LE PROHIBIMOS ACCEDER
                            System.out.println("SÍ ESTÁ BLOQUEADO DESDE CHECKISLOCKED :(");
                            Toast.makeText(PantallaLogin.this, R.string.aviso_usuario_bloqueado, Toast.LENGTH_SHORT).show();
                            System.out.println("USUARIO BLOQUEADO.");
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
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
        request = new StringRequest(Request.Method.POST, url_consulta6,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("3")) { // ERROR: CONTRASEÑA ERRÓNEA
                            // INTENTO FALLIDO DE INICIO DE SESIÓN
                            System.out.println("RESPUESTA 3: ERROR DE CLAVE");
                            try {
                                // restamos intentos restantes de inicio de sesión
                                restaIntentos();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else { // Usuario y contraseña correctos
                            if (response.equals("4")) { // Respuesta "4" = login correcto.
                                System.out.println("RESPUESTA 4, DATOS CORRECTOS.");
                                check_isLocked(); // comprobamos si está bloqueado
                            }
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        System.out.println("ERROR BLOQUEARUSUARIO()");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
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
        request = new StringRequest(Request.Method.POST, url_consulta7,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { // la respuesta que se obtiene es el número de intentos restantes del usuario
                        try {
                            int intentos_restantes = Integer.valueOf(response);
                            System.out.println("RESPUESTA: "+ response);
                            if (intentos_restantes > 0) { //  mientras quede algún intento, se siguen restando
                                Toast.makeText(PantallaLogin.this, R.string.aviso_clave_incorrecta + intentos_restantes, Toast.LENGTH_LONG).show();
                                intentos_restantes--; // restamos un intento
                                intentos_login = String.valueOf(intentos_restantes);
                                actualizaIntentos();
                                System.out.println("INTENTOS RESTANTES: "+intentos_restantes);
                                System.out.println("INTENTOS LOGIN: "+intentos_login);
                                if (intentos_restantes == 1) {
                                    Toast toast = Toast.makeText(PantallaLogin.this, R.string.aviso_pocos_intentos_restantes + (intentos_restantes+1)+ R.string.aviso_pocos_intentos_restantes_2, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.show();
                                }
                            } else { // Cuando los intentos se agoten, es decir, estén en 0, se bloquea al usuario
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
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        System.out.println("ERROR RESTAINTENTOS()");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
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
        request = new StringRequest(Request.Method.POST, url_consulta8,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Se actualiza el número de intentos
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        System.out.println("ERROR ACTUALIZAINTENTOS()");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
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
        intentos_login = "5";
        request = new StringRequest(Request.Method.POST, url_consulta8,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Se resetea el número de intentos
                        System.out.println("INTENTOS RESTANTES RESETEADOS A 5.");
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        System.out.println("ERROR ACTUALIZAINTENTOS()");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                parametros.put("intentos_login", intentos_login);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }
    /***********************************************************************************************
     * Método que inicia el flujo de comprobación de datos:
     * 1. Comprueba si el usuario introducido existe. Si existe, continúa con las comprobaciones (2),
     * si no, muestra alerta de que no existe.
     * 2. Comprueba  la clave introducida. Si es correcta pasa a la siguiente comprobación, si está bloqueado (3).
     * Si no es correcta, resta intentos de inicio, llegando a bloquear al usuario si agota los intentos.
     * permite obviar el mensaje o ir a la pantalla de confirmación.
     * 3. Comprueba si el usuario está bloqueado. Si es así, le muestra un mensaje informándole. Si
     * no, pasa a comprobar la confirmación del usuario (4).
     * 4. Comprueba si el usuario ya confirmó su registro (hace consulta que devuelve 1 o 0, siendo 1
     * que sí y 0 que no). Si está confirmado, pasará a login correcto, abriendo la pantalla principal
     * de la aplicación. (3). Si no está confirmado, le muestra un cuadro de diálogo de alerta que le
     * permite obviar el mensaje o ir a la pantalla de confirmación.
     ***********************************************************************************************/
    private void compruebaDatos(){
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("2")) { // ERROR: usuario no existe
                            try {
                                Toast.makeText(PantallaLogin.this, R.string.error_usuario_no_existe, Toast.LENGTH_SHORT).show();
                                System.out.println("RESPUESTA 2: USUARIO NO EXISTE");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else { // Sí existe el usuario
                            nombre_usuario = nUsuario;
                            System.out.println("EL USUARIO INTRODUCIDO EXISTE... PROCEDEMOS A COMPROBAR CONFIRMACIÓN Y BLOQUEO...");
                            comprobarClave(); // comprobamos si la clave es correcta para hacer el login
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nUsuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************************************
     * Método que obtiene los datos del usuario que ha hecho inicio de sesión (id del usuario, familia del ciclo y correo
     **********************************************************************************************************************/
    private void obtenerDatosUsuario(){
        request = new StringRequest(Request.Method.POST, url_consulta11,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response); // creamos array json para obtener el objeto del correo
                            idUsuario = jsonArray.getJSONObject(0).getString("idUsuario");
                            familiaCiclo = jsonArray.getJSONObject(0).getString("familia_ciclo");
                            correo_de_usuario = jsonArray.getJSONObject(0).getString("correo");
                            Log.d("PantallaLogin","ID DEL USUARIO "+ idUsuario);
                            Log.d("PantallaLogin","FAMILIA DEL CICLO DEL USUARIO " + familiaCiclo);
                            Log.d("PantallaLogin","CORREO ELECTRÓNICO "+ correo_de_usuario);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
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
        // el resto de funcionalidades
        reseteaIntentos(); // reseteamos número de intentos de login restantes
        // Creamos ventana de diálogo con circulo de carga para la espera de carga de los datos
        ProgressDialog progressDialog = new ProgressDialog(PantallaLogin.this);
        progressDialog.setTitle(R.string.dialog_cargando);
        progressDialog.setMessage("Comprobando datos. Por favor, espera un momento.");
        progressDialog.show();
        System.out.println("LOGIN CORRECTO :)");
        // Cargamos la pantalla principal de la aplicación
        Intent intent = new Intent(PantallaLogin.this, NavMenu.class);
        startActivity(intent);
        // A continuación cambiamos el valor de isLogged a 1 para hacer login automático en la pantalla de carga en la próxima apertura de la app.
        request = new StringRequest(Request.Method.POST, url_consulta2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fecha_ultimo_login = getFecha();
                        System.out.println("FECHA ULTIMO LOGIN: " + fecha_ultimo_login);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PantallaLogin.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_usuario);
                parametros.put("fecha_ultimo_login", fecha_ultimo_login);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
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
        System.out.println("CORREO GUARDADO EN PREFERENCIAS");
        editor.putString("idUsuario", idUsuario);
        editor.putString("familiaCiclo", familiaCiclo);
        System.out.println("FAMILIA DE CICLO GUARDADO EN PREFERENCIAS");
        editor.commit();
    }

    /***********************************************************************************************
     * Asociamos el menú de toolbar_login a esta actividad
     * @param menu
     * @return
     **********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_login, menu); // la R referencia a la ubicación del archivo
        return true; // .menu es el directorio, y .toolbar el archivo
    }

    /***********************************************************************************************
     * Iconos de acciones del menú de la barra de acciones
     * @param item
     * @return
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
     * Al pulsar atrás no hacemos nada
     **********************************************************************************************/
    @Override
    public void onBackPressed() {
        // dejamos en blanco para que no se haga nada

    }
}
