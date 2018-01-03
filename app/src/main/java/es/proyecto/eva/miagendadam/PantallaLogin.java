package es.proyecto.eva.miagendadam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
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

import java.sql.SQLOutput;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.VolleyController.AppController;

public class PantallaLogin extends AppCompatActivity {
    private Button btnIniciarSesion;
    private Button btnRegistroUsuario;
    private Button btnRecuperarClave;
    private EditText txtNombreUsuario;
    private EditText txtClave;
    private String url_consulta = "http://192.168.0.12/MiAgenda/check_usuario_existe.php";
    private String url_consulta2 = "http://192.168.0.12/MiAgenda/update_isLogged.php";
    private String url_consulta3 = "http://192.168.0.12/MiAgenda/check_isLocked.php";
    private String url_consulta4 = "http://192.168.0.12/MiAgenda/update_isLocked.php";
    private String url_consulta5 = "http://192.168.0.12/MiAgenda/check_isConfirmed.php";
    private String url_consulta6 = "http://192.168.0.12/MiAgenda/check_clave.php";
    private String url_consulta7 = "http://192.168.0.12/MiAgenda/check_num_intentos_login.php";
    private String url_consulta8 = "http://192.168.0.12/MiAgenda/update_intentos_login.php";
    private String url_consulta9 = "http://192.168.0.12/MiAgenda/update_fecha_bloqueo.php";
    private String url_consulta10 = "http://192.168.0.12/MiAgenda/consulta_recuperar_correo.php";
    private String url_consulta11 = "http://192.168.0.12/MiAgenda/consulta_recuperar_id_usuario.php";
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
   // private String url_consulta = "http://miagendafp.000webhostapp.com/consulta_datos_usuario3.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
    //private String url_consulta2 = "http://miagendafp.000webhostapp.com/consulta_update_isLogged.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
    //private String url_consulta3 = "http://miagendafp.000webhostapp.com/consulta_isLocked.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";

    static String nombre_usuario = ""; // para guardar el nUsuario cuando confirmamos que es válido
    static String nUsuario=""; // el nombre de usuario que introduce el usuario para logearse (no tiene por qué se válido, hay que comprobarlo)
    static String clave="";
    static String correo_electronico=""; // será el email que le corresponde al usuario, y se obtendrá por consulta
    private String idUsuario = ""; // el identificador de usuario que utilizaremos para realizar consultas posteriores
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
        setTitle("Inicio de sesión");
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

        // Botón He olvidado mi contraseña, abre actividad de RecuperarClave
        btnRecuperarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PantallaLogin.this, RecuperarClave.class);
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
                    Toast.makeText(PantallaLogin.this, "Debes introducir un nombre de usuario.", Toast.LENGTH_SHORT).show();
                } else {
                    if (clave.isEmpty()) {
                        Toast.makeText(PantallaLogin.this, "Debes introducir una clave.", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("DATOS INTRODUCIDOS!!!!!!!!: " + nUsuario + " " + clave);
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
                            System.out.println("NO ESTABA BLOQUEADO (RESPUESTA = "+response+")");
                            System.out.println(fecha_bloqueo);
                            System.out.println("BLOQUEO CORRECTO DESDE MÉTODO BLOQUEARUSUARIO :)");
                            Toast toast = Toast.makeText(PantallaLogin.this, "Usuario bloqueado. Contacte con soporte.", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            System.out.println("YA ESTABA BLOQUEADO. NO SE ACTUALIZA LA FECHA.");
                            Toast toast = Toast.makeText(PantallaLogin.this, "Usuario bloqueado. Contacte con soporte.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PantallaLogin.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
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
                                loginCorrecto();
                            } else { // NO está confirmado, obligamos a confirmar
                                System.out.println(" NOOOOO ESTÁ CONFIRMADO (DESDE MÉTODO CHECK_ISCONFIRMED :( )");
                                // no hace falta comprobar isLogged, porque lógicamente es imposible que esté en 1 si no ha confirmado su registro
                                System.out.println("USUARIO NO CONFIRMADO DESDE MÉTODO COMPRUEBADATOS");
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
                                // Create the AlertDialog object and return it
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
                        Toast.makeText(PantallaLogin.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(PantallaLogin.this, "El usuario está bloqueado. Contacte con soporte.", Toast.LENGTH_SHORT).show();
                            System.out.println("USUARIO BLOQUEADO.");
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
                        Toast.makeText(PantallaLogin.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PantallaLogin.this, "La clave introducida no es correcta. Intentos restantes: " + intentos_restantes, Toast.LENGTH_LONG).show();
                                intentos_restantes--; // restamos un intento
                                intentos_login = String.valueOf(intentos_restantes);
                                actualizaIntentos();
                                System.out.println("INTENTOS RESTANTES: "+intentos_restantes);
                                System.out.println("INTENTOS LOGIN: "+intentos_login);
                                if (intentos_restantes == 1) {
                                    Toast toast = Toast.makeText(PantallaLogin.this, "Atención, solo te quedan "+ (intentos_restantes+1)+ " intentos restantes. Si los agotas se bloqueará tu cuenta.", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.show();
                                }
                            } else { // cuando estén en 0, se bloquea
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
                        Toast.makeText(PantallaLogin.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PantallaLogin.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PantallaLogin.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PantallaLogin.this, "No existe ningún usuario con ese nombre.", Toast.LENGTH_SHORT).show();
                                System.out.println("RESPUESTA 2: USUARIO NO EXISTE");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else { // Sí existe el usuario, lo guardamos para utilizarlo como parámetro en las consultas
                            // posteriores
                            nombre_usuario = nUsuario;
                            obtenerCorreo(); // obtenemos el correo electrónico
                            obtenerIDUsuario(); // y el identificador del usuario
                            System.out.println("EL USUARIO INTRODUCIDO EXISTE... PROCEDEMOS A COMPROBAR CONFIRMACIÓN Y BLOQUEO...");
                            // Comprobamos si el usuario está confirmado
                            // (desde este método empieza el flujo de comprobaciones)
                           // check_isConfirmed();
                            comprobarClave();
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
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /*******************************************************************************************************************
     * Método que obtiene el correo que le corresponde al usuario que hace login una vez verificado que existe
     ******************************************************************************************************************/
    private void obtenerCorreo(){
        request = new StringRequest(Request.Method.POST, url_consulta10,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response); // creamos array json para obtener el objeto del correo
                            correo_electronico = jsonArray.getJSONObject(0).getString("correo");
                            System.out.println("CORREO DEL USUARIO "+ nombre_usuario + ":" + correo_electronico );
                        } catch (Exception e){
                            e.printStackTrace();
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
                parametros.put("nUsuario", nombre_usuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /*******************************************************************************************************************
     * Método que obtiene el ID que le corresponde al usuario que hace login una vez verificado que existe
     ******************************************************************************************************************/
    private void obtenerIDUsuario(){
        request = new StringRequest(Request.Method.POST, url_consulta11,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response); // creamos array json para obtener el objeto del correo
                            idUsuario = jsonArray.getJSONObject(0).getString("idUsuario");
                            System.out.println("ID DEL USUARIO "+ idUsuario);
                        } catch (Exception e){
                            e.printStackTrace();
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
       // nombre_usuario = nUsuario; (comentamos línea, ya habíamos guardado este dato en la comprobación
        // del usuario en el método de comprobación de datos)
        guardarPreferencias(); // guardamos el dato de nombre_usuario y correo_electronico en las preferencias, para usarlo en clases futuras para
        // el resto de funcionalidades
        // reseteamos número de intentos de login restantes
        reseteaIntentos();
        // Creamos ventana de diálogo con circulo de carga para la espera de carga de los datos
        ProgressDialog progressDialog = new ProgressDialog(PantallaLogin.this);
        progressDialog.setTitle("Cargando");
        progressDialog.setMessage("Comprobando datos. Por favor, espere un momento.");
        progressDialog.show();
        System.out.println("LOGIN CORRECTO :)");
        Intent intent = new Intent(PantallaLogin.this, NavMenu.class);
        startActivity(intent);
        // A continuación cambiamos el valor de isLogged a 1 para hacer login automático en la pantalla de carga.
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
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaLogin.this, "Error de conexión.", Toast.LENGTH_SHORT).show();

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
        editor.putString("correo_de_usuario", correo_electronico);
        editor.putString("idUsuario", idUsuario);
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
     * Programamos acciones con las pulsaciones de las opciones del menú
     * @param item
     * @return
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_acerca_de:
                Log.i("ActionBar", "Acerca de!");
                Intent intent = new Intent (PantallaLogin.this, AcercaDe.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // NO BORRAR!!
    @Override
    public void onBackPressed() {
        // DEJO EN BLANCO PARA QUE, AL HACER CLICK EN EL BOTÓN DE ATRÁS DESDE ESTA
        // PANTALLA, NO SE PUEDA VOLVER A LA PANTALLA PRINCIPAL HABIENDO CERRADO YA SESIÓN.

    }
}
