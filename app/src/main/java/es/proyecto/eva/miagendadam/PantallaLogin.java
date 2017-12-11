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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import static es.proyecto.eva.miagendadam.RegistroNuevoUsuario.isConfirmed;


public class PantallaLogin extends AppCompatActivity {
    private Button btnIniciarSesion;
    private Button btnRegistroUsuario;
    private Button btnRecuperarClave;
    private EditText txtNombreUsuario;
    private EditText txtClave;
//    private String url_consulta = "http://192.168.0.10/MiAgenda/consulta_check_usuario_existe.php";
//    private String url_consulta2 = "http://192.168.0.10/MiAgenda/consulta_update_isLogged.php";
//    private String url_consulta3 = "http://192.168.0.10/MiAgenda/consulta_isLocked.php";
//    private String url_consulta4 = "http://192.168.0.10/MiAgenda/consulta_update_isLocked.php";
//    private String url_consulta5 = "http://192.168.0.10/MiAgenda/consulta_isConfirmed.php";
//    private String url_consulta6 = "http://192.168.0.10/MiAgenda/consulta_check_clave.php";

    private String url_consulta = "http://192.168.0.158/MiAgenda/consulta_check_usuario_existe.php";
    private String url_consulta2 = "http://192.168.0.158/MiAgenda/consulta_update_isLogged.php";
    private String url_consulta3 = "http://192.168.0.158/MiAgenda/consulta_isLocked.php";
    private String url_consulta4 = "http://192.168.0.158/MiAgenda/consulta_update_isLocked.php";
    private String url_consulta5 = "http://192.168.0.158/MiAgenda/consulta_isConfirmed.php";
    private String url_consulta6 = "http://192.168.0.158/MiAgenda/consulta_check_clave.php";
    /*****************************************************************************************
     *                              SERVIDOR REMOTO
     ****************************************************************************************/
   // private String url_consulta = "http://miagendafp.000webhostapp.com/consulta_datos_usuario3.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
    //private String url_consulta2 = "http://miagendafp.000webhostapp.com/consulta_update_isLogged.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
    //private String url_consulta3 = "http://miagendafp.000webhostapp.com/consulta_isLocked.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";

    static String nombre_usuario = ""; // para guardar el nUsuario cuando confirmamos que es válido
    static String nUsuario=""; // el nombre de usuario que introduce el usuario para logearse (no tiene por qué se válido, hay que comprobarlo)
    static String clave="";
    static String correo_electronico=""; // el email que el usuario introdujo en el registro para registrarse como nuevo usuario
    static StringRequest request;
    // OBTENEMOS LA FECHA DESDE AQUÍ DIRECTAMENTE, SI NO NO LA GUARDA BIEN EN LA BASE DE DATOS
    static Date fecha = new Date();
    static String fecha_ultimo_login = fecha.toString();
    // Declaramos el número de intentos de inicio de sesión base, para ir restándolo y mostrándoselo al usuario con cada intento fallido que haga
    static int intentosRestantes = 5;
    // EL PROBLEMA CON ESTO ES QUE EN EL PRIMER INTENTO ME COGE EL VALOR QUE ESTÉ AQUÍ, ES DECIR, QUE SI AQUÍ TIENE UN 2, LO PRIMERO QUE VA A COGER
    // POR MUCHO QUE EJECUTE UNA LLAMADA AL MÉTODO, VA A SER ESTE VALOR. ASÍ PASA, QUE NO SE CUMPLE LA CONDICIÓN LA PRIMERA VEZ PORQUE NO ES EL VALOR
    // ESPERADO... SOLO FUNCIONA A PARTIR DE LA SEGUNDA EJECUCION
     // LO QUE HAY QUE HACER ES METERLE AQUÍ DIRECTAMENTE EL VALOR QUE SE OBTENGA
    // DE LOS MÉTODOS DE COMPROBACIÓN


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
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        correo_electronico = preferences.getString("correo_electronico", "");
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
        intentosRestantes = 5;
        request = new StringRequest(Request.Method.POST, url_consulta4,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("BLOQUEO CORRECTO DESDE MÉTODO BLOQUEARUSUARIO :)");
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
     *  Método que comprueba si el usuario introducido está confirmado o no
     **********************************************************************************************/
    public void check_isConfirmed(){
        request = new StringRequest(Request.Method.POST, url_consulta5,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                        try {
                            if (response.equals("1")) { // SÍ está confirmado, comprobamos si está bloqueado
                                System.out.println("SÍ ESTÁ CONFIRMADO (DESDE MÉTODO CHECK_ISCONFIRMED :) )");
                                check_isLocked();
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
                            comprobarClave();
                        } else {
                            // LE PROHIBIMOS ACCEDER
                            System.out.println("SÍ ESTÁ BLOQUEADO DESDE CHECKISLOCKED :(");
                            Toast.makeText(PantallaLogin.this, "Usuario bloqueado.  No se ha podido iniciar sesión.", Toast.LENGTH_SHORT).show();
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
                parametros.put("nUsuario", nUsuario);
                parametros.put("clave", clave);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     *  Método que comprueba la clave introducida por el usuario una vez que se ha comprobado
     *  que el usuario en cuestión está confirmado
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
                                if (intentosRestantes > 1) { // si todavía le quedan intentos...
                                    // ESTO SE EJECUTA HASTA QUE EL NÚMERO DE INTENTOS RESTANTES SEA 0
                                    intentosRestantes--; // restamos un intento restante por cada intento fallido
                                    System.out.println("NÚMERO DE INTENTOS DE INICIO DE SESIÓN RESTANTES: " + intentosRestantes);
                                    Toast.makeText(PantallaLogin.this, "La clave introducida no es correcta. Intentos restantes: " + intentosRestantes, Toast.LENGTH_LONG).show();
                                    if (intentosRestantes == 2) { // cuando sólo le queden dos intentos, le avisaremos que tenga cuidado, que se le va a bloquear...
                                        Toast toast = Toast.makeText(PantallaLogin.this, "Atención, solo te quedan " + intentosRestantes + " intentos restantes. Si los agotas se bloqueará tu cuenta.", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                        toast.show();
                                    }
                                } else { // ya no le quedan intentos
                                    // EJECUTAMOS SCRIPT AQUÍ PARA BLOQUEAR AL USUARIO
                                    bloquearUsuario();
                                    Toast.makeText(PantallaLogin.this, "Usuario bloqueado.", Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else { // Usuario y contraseña correctos, llamamos a método que hace el login
                            if (response.equals("4")) { // Respuesta "4" = login correcto.
                                System.out.println("RESPUESTA 4, DATOS CORRECTOS.");
                                loginCorrecto();
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
     * Método que inicia el flujo de comprobación de datos:
     * 1. Comprueba si el usuario introducido existe. Si existe, continúa con las comprobaciones (2),
     * si no, muestra alerta de que no existe.
     * 2. Comprueba si el usuario ya confirmó su registro (hace consulta que devuelve 1 o 0, siendo 1
     * que sí y 0 que no). Si está confirmado, continúa con la siguiente comprobación, si el usuario
     * está bloqueado (3). Si no está confirmado, le muestra un cuadro de diálogo de alerta que le
     * permite obviar el mensaje o ir a la pantalla de confirmación.
     * 3. Comprueba si el usuario está bloqueado. Si es así, le muestra un mensaje informándole. Si
     * no, pasa a comprobar la contraseña del usuario (4).
     * 4. Comprueba la clave introducida. Si es correcta, se habrá validado t0do y se hará un login
     * correcto mediante el método loginCorrecto(). Si no es correcta, le restará el número de
     * intentos restantes de inicio de sesión (tendrá 5). Si se llega a 2 intentos restantes, se le
     * notificará de que tenga cuidado, porque si se queda sin intentos se le va a bloquear.
     * Si el número de intentos llega a 0, se bloqueará al usuario.
     * Si consigue hacer el login correcto antes de llegar a 0, se reseteará el número de intentos y
     * se hará el login correcto.
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
                            System.out.println("EL USUARIO INTRODUCIDO EXISTE... PROCEDEMOS A COMPROBAR CONFIRMACIÓN Y BLOQUEO...");
                            // Comprobamos si el usuario está confirmado
                            // (desde este método empieza el flujo de comprobaciones)
                            check_isConfirmed();
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

    /*********************************************************************************************************************
     * Método que se ejecuta cuando se han verificado todos los datos necesarios para hacer un inicio de sesión correcto
     ********************************************************************************************************************/
    private void loginCorrecto(){
       // nombre_usuario = nUsuario; (comentamos línea, ya habíamos guardado este dato en la comprobación
        // del usuario en el método de comprobación de datos)
        guardarPreferencias(); // guardamos el dato de nombre_usuario en las preferencias, para usarlo en clases futuras para
        // el resto de funcionalidades
        intentosRestantes = 5; // reseteamos nuevamente el número de intentos restantes
        // A continuación cambiamos el valor de isLogged a 1 para hacer login automático en la pantalla de carga.
        request = new StringRequest(Request.Method.POST, url_consulta2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Creamos ventana de diálogo con circulo de carga para la espera de carga de los datos
                        ProgressDialog progressDialog = new ProgressDialog(PantallaLogin.this);
                        progressDialog.setTitle("Cargando");
                        progressDialog.setMessage("Comprobando datos. Por favor, espere un momento.");
                        progressDialog.show();
                        System.out.println("LOGIN CORRECTO :)");

                        // Ahora obtenemos la fecha en la que ha iniciado sesión para controlar la última vez que entró
                        System.out.println("FECHA ULTIMO LOGIN: " + fecha_ultimo_login);
                        Intent intent = new Intent(PantallaLogin.this, NavMenu.class);
                        startActivity(intent);
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
        editor.commit();
    }

    // NO BORRAR!!
    @Override
    public void onBackPressed() {
        // DEJO EN BLANCO PARA QUE, AL HACER CLICK EN EL BOTÓN DE ATRÁS DESDE ESTA
        // PANTALLA, NO SE PUEDA VOLVER A LA PANTALLA PRINCIPAL HABIENDO CERRADO YA SESIÓN.
    }
}
