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
    private EditText txtNombreUsuario;
    private EditText txtClave;
  //  private String url_consulta = "http://192.168.0.10/MiAgenda/consulta_datos_usuario3.php";
  //  private String url_consulta2 = "http://192.168.0.10/MiAgenda/consulta_update_isLogged.php";
  //  private String url_consulta3 = "http://192.168.0.10/MiAgenda/consulta_isLocked.php";

    private String url_consulta = "http://192.168.0.157/MiAgenda/consulta_datos_usuario3.php";
    private String url_consulta2 = "http://192.168.0.157/MiAgenda/consulta_update_isLogged.php";
    private String url_consulta3 = "http://192.168.0.157/MiAgenda/consulta_isLocked.php";

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
    static String fecha_ultimo_login= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_login);
        setTitle("Inicio de sesión");
        btnIniciarSesion = (Button) findViewById(R.id.btn_iniciar_sesion);
        btnRegistroUsuario = (Button) findViewById(R.id.btn_registrarse);
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
                        System.out.println("DATOS INTRODUCIDOS!!!!!!!!: " + nUsuario + clave);
                        compruebaDatos();
                    }

                }
            }
        });
    }

    /***********************************************************************************************
     * Método para comprobar si los datos que el usuario ha introducido son correctos
     ***********************************************************************************************/
    private void compruebaDatos(){
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                        if (response.equals("2")) { // usuario no existe
                            try {
                                Toast.makeText(PantallaLogin.this, "No existe ningún usuario con ese nombre.", Toast.LENGTH_SHORT).show();
                                System.out.println("USUARIO NO EXISTE");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (response.equals("3")) { // contraseña errónea
                                try {
                                    Toast.makeText(PantallaLogin.this, "La clave introducida no es correcta.", Toast.LENGTH_SHORT).show();
                                    System.out.println("ERROR DE CLAVE");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (response.equals("1")) { // ESTÁ CONFIRMADO
                                    System.out.println("USUARIO CONFIRMADO");
                                    check_isLocked(); // comprobamos si está bloqueado
                                } else {
                                    if (response.equals("0")){ // NO ESTÁ CONFIRMADO, no le dejamos iniciar sesión
                                        // no hace falta comprobar isLogged, porque lógicamente es imposible que esté en 1 si no
                                        // ha confirmado su correo
                                        System.out.println("USUARIO NO CONFIRMADO");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(PantallaLogin.this);
                                        builder.setMessage(R.string.text_dialog_confirm)
                                                .setPositiveButton(R.string.btn_aceptar_confirm, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent intent = new Intent(PantallaLogin.this, ConfirmaRegistro.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton(R.string.btn_cancelar_confirm, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // User cancelled the dialog

                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        Dialog dialog = builder.create();
                                        dialog.show();
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

    /*********************************************************************************************************************
     * Método que se ejecuta cuando se han verificado todos los datos necesarios para hacer un inicio de sesión correcto
     ********************************************************************************************************************/
    private void loginCorrecto(){
        nombre_usuario = nUsuario;
        guardarPreferencias();
        // Pero antes cambiamos el valor de isLogged a 1 para ahorrarnos t0do este proceso
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
                         // si hemos llegado hasta aquí, es que el nombre de usuario
                        // y la clave introducidos por el usuario son válidos, por tanto se guarda el dato que el
                        // usuario ha introducido para mostrar luego los datos que le correspondan
                        // También, en el script php, se cambiará el campo del usuario isLogged a 1, para que al
                        // cargar la PantallaCarga, el programa seleccione su campo, y, si es 1 pase directamente
                        // a la pantalla principal, o si es 0, entre en la pantalla Login.

                        // Ahora obtenemos la fecha en la que ha iniciado sesión para controlar la última vez que entró
                        Date fechaR = new Date();
                        fecha_ultimo_login = fechaR.toString();
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
                            // permitimos el login
                            loginCorrecto();
                        } else {
                            if (response.equals("1")) { // usuario BLOQUEADO
                                // LE PROHIBIMOS ACCEDER
                                Toast.makeText(PantallaLogin.this, "Usuario bloqueado.  No se ha podido iniciar sesión.", Toast.LENGTH_SHORT).show();
                                System.out.println("USUARIO BLOQUEADO.");
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
}
