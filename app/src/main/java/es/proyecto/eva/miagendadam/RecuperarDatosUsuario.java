package es.proyecto.eva.miagendadam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import es.proyecto.eva.miagendadam.VolleyController.AppController;

public class RecuperarDatosUsuario extends AppCompatActivity {
    EditText txtCorreo;
    Button btnEnviarClave;
    Button btnEnviarUsuario;
    static String correo;
    static String nUsuario;
    private Session session;
    private StringRequest request;

//    private String url_consulta = "http://192.168.0.12/MiAgenda/consulta_recuperar_clave.php";
//    private String url_consulta2 = "http://192.168.0.12/MiAgenda/check_correo.php";
//    private String url_consulta3 = "http://192.168.0.12/MiAgenda/consulta_recuperar_usuario.php";
//    private String url_consulta4 = "http://192.168.0.12/MiAgenda/clave_gmail.php";

//    private String url_consulta = "http://192.168.0.159/MiAgenda/consulta_recuperar_clave.php";
//    private String url_consulta2 = "http://192.168.0.159/MiAgenda/check_correo.php";
//    private String url_consulta3 = "http://192.168.0.159/MiAgenda/consulta_recuperar_usuario.php";
//    private String url_consulta4 = "http://192.168.0.159/MiAgenda/clave_gmail.php";

    private String url_consulta = "http://miagendafp.000webhostapp.com/consulta_recuperar_clave.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/check_correo.php";
    private String url_consulta3 = "http://miagendafp.000webhostapp.com/consulta_recuperar_usuario.php";
    private String url_consulta4 = "http://miagendafp.000webhostapp.com/clave_gmail.php";

    // VARIABLES PARA GENERAR LA CLAVE NUEVA
    private static final String dCase = "abcdefghijklmnopqrstuvwxyz";
    private static final String uCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String sChar = "!=-_@:%&~#";
    private static final String intChar = "0123456789";
    private static Random r = new Random();
    // ------- Usamos los booleanos para determinar qué botón ha pulsado el usuario y qué método queremos ejecutar --------
    private boolean enviarClave = false;
    private boolean enviarUsuario = false;
    // --------------------------------------------------------------------------------------------------------------------
    // Generamos la clave directamente en el string
    private String generaClave(){
        String pass = "";
        while (pass.length () != 10) { // establecemos longitud de la clave
            int rPick = r.nextInt(4);
            if (rPick == 0) {
                int spot = r.nextInt(25);
                pass += dCase.charAt(spot);
            } else if (rPick == 1) {
                int spot = r.nextInt(25);
                pass += uCase.charAt(spot);
            } else if (rPick == 2) {
                int spot = r.nextInt(7);
                pass += sChar.charAt(spot);
            } else if (rPick == 3) {
                int spot = r.nextInt(9);
                pass += intChar.charAt(spot);
            }
        }
        //Log.i("RecuperarDatosUsuario", "Clave nueva generada");
        return pass;
    }
    // metemos la clave generada en otro string
    private String claveNueva = generaClave();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_datos_usuario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // para VER la flecha atrás en el actionbar
        setTitle(R.string.title_activity_recuperar_datos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnEnviarClave = (Button) findViewById(R.id.btn_enviar_nueva_clave);
        btnEnviarUsuario = (Button) findViewById(R.id.btn_enviar_nombre_usuario);
        txtCorreo = (EditText) findViewById(R.id.txt_nombre_usuario);
        // Botón He olvidado mi contraseña, abre actividad de RecuperarDatosUsuario
        btnEnviarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarClave = true;
                //Log.i("RecuperarDatosUsuario", "Enviar clave nueva");
                check_correo();
            }
        });

        btnEnviarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarUsuario = true;
                //Log.i("RecuperarDatosUsuario", "Enviar nombre de usuario");
                check_correo();
            }
        });
    }


    /***********************************************************************************************
     * Método que comprueba si existe algún usuario con el correo introducido
     **********************************************************************************************/
    public void check_correo(){
        ////Log.d("RecuperarDatosUsuario", "Comprobamos el correo introducido");
        correo = txtCorreo.getText().toString();
        if (!correo.isEmpty()){ // si se ha introducido un dato en el campo de correo...
            request = new StringRequest(Request.Method.POST, url_consulta2,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("0")) { // no existe el usuario en la bd
                                //Log.i("RecuperarDatosUsuario", "No hay ningún usuario con ese correo");
                            //   Toast.makeText(RecuperarDatosUsuario.this, "No hay ningún usuario registrado con ese correo.", Toast.LENGTH_SHORT).show();
                                Snackbar.make(findViewById(android.R.id.content),
                                        R.string.error_correo_no_existe, Snackbar.LENGTH_LONG).show();
                            } else if(response.equals("1")){
                                // Comprobamos qué botón había pulsado el usuario:
                                if (enviarClave){ // Si es el de solicitar una clave nueva...
                                    // Generamos una clave nueva
                                    actualizaClave();
                                } else if (enviarUsuario){ // Si es el de recuperar el nombre de usuario...
                                    // Obtenemos el nombre del usuario asociado al correo indicado
                                    recuperaUsuario();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                           // Toast.makeText(RecuperarDatosUsuario.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(android.R.id.content),
                                    R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                            //Log.e("RecuperarDatosUsuario", "Error al conectar con el servidor para comprobar el correo introducido");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("correo", correo);
                    return parametros;
                }

            };
            AppController.getInstance().addToRequestQueue(request);
        } else { // si el campo de correo electrónico está vacío
           // Toast.makeText(RecuperarDatosUsuario.this, "Debes introducir tu correo electrónico.", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.error_introducir_correo, Snackbar.LENGTH_SHORT).show();
        }

    }

    /***********************************************************************************************
     * Método que envía el correo con el contenido adaptado en función del botón pulsado
     **********************************************************************************************/
    private void enviaCorreo(){
        request = new StringRequest(Request.Method.POST, url_consulta4,
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

                            if (enviarClave){
                                // Añadimos los datos del correo junto con la nueva clave
                                //Log.i("RecuperarDatosUsuario", "Enviamos correo con nueva clave");
                                RecuperarDatosUsuario.RetreiveFeedTask task = new RetreiveFeedTask();
                                task.execute();
                                enviarClave = false;
                            } else if (enviarUsuario){
                                // Establecemos el contenido del correo con los datos del usuario
                                //Log.i("RecuperarDatosUsuario", "Enviamos correo con nombre de usuario");
                                RecuperarDatosUsuario.RetreiveFeedTask2 task2 = new RetreiveFeedTask2();
                                task2.execute();
                                enviarUsuario = false;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            //Log.e("RecuperarDatosUsuario", "Error al enviar el correo electrónico");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                     //   Toast.makeText(RecuperarDatosUsuario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        Snackbar.make(findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.e("RecuperarDatosUsuario", "Error al obtener la clave del correo de noreply...");
                    }
                });
        AppController.getInstance().addToRequestQueue(request);
    }


    /***********************************************************************************************
     * Contenido del correo de solicitud de clave nueva:                                           *
     **********************************************************************************************/
    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("noreply.miagendafp@gmail.com")); // quién lo envía
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo)); // a quién lo envía
                message.setSubject("No-reply: Solicitud de contraseña nueva"); // asunto del correo
                message.setContent("<p style=\"text-align:justify\">¡Hola! Hemos recibido una solicitud de cambio de contraseña para tu cuenta de <b>Mi agenda FP</b>.</p>" +
                        " <p style=\"text-align:justify\">Su contraseña nueva es: <b>"+ claveNueva + "</b></p>" +
                        " Puedes volver a cambiarla cuando desee desde tu perfil de usuario." +
                        " <br/>Atentamente, <b>Mi agenda FP</b>. </p></div>" +
                        "<div style=\"background-color:#EEEEEE; border:1px solid #BABABA; box-shadow: 2px 2px 5px #999; font-size:10px; text-align:justify\">" + // el sombreado no se ve
                        "<p style=\"margin-left: 10px; margin-right: 11px\">" +
                        "Este mensaje se ha generado automáticamente porque se ha recibido una solicitud de cambio de contraseña para el usuario registrado con este correo. "+
                        " <br/>Por favor <b>no responda a este correo</b>, no recibirá ninguna respuesta." +
                        " <br/>Si tiene algún problema, duda o sugerencia, contacte con el soporte a través de la dirección de correo <b>soportemiagendafp@gmail.com</b>\n" +
                        " <br/>Si ha recibido este correo por error, por favor, le rogamos que lo elimine y se ponga en contacto con la dirección de correo indicada arriba.\n"
                        , "text/html; charset=utf-8");
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
            //Log.d("RecuperarDatosUsuario", "Correo de recuperación de clave enviado");
        }
    }

    /***********************************************************************************************
     * Contenido del correo de recuperación de usuario                                             *
     **********************************************************************************************/
    class RetreiveFeedTask2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("noreply.miagendafp@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
                message.setSubject("No-reply: Solicitud de recordatorio de nombre de usuario");
                message.setContent("<p style=\"text-align:justify\">¡Hola! Hemos recibido una solicitud de recuperación de usuario para tu cuenta de <b>Mi agenda FP</b>.</p>" +
                        " <p style=\"text-align:justify\">Tu nombre de usuario es: <b>"+ nUsuario + "</b></p><br/>" +
                        "<div style=\"background-color:#EEEEEE; border:1px solid #BABABA; box-shadow: 2px 2px 5px #999; font-size:10px; text-align:justify\">" + // el sombreado no se ve
                        "<p style=\"margin-left: 10px; margin-right: 11px\">" +
                        " Este mensaje se ha generado automáticamente porque se ha recibido una solicitud de recuperación de nombre de usuario para el usuario registrado con este correo. "+
                        " <br/>Por favor <b>no responda a este correo</b>, no recibirá ninguna respuesta.\n" +
                        " <br/> Si tiene algún problema, duda o sugerencia, contacte con el soporte a través de la dirección de correo <b>soportemiagendafp@gmail.com</b>\n" +
                        " <br/> Si ha recibido este correo por error, por favor, le rogamos que lo elimine y se ponga en contacto con la dirección de correo indicada arriba.\n" +
                        " <br/> Atentamente, el equipo de <b>Mi agenda FP</b>.", "text/html; charset=utf-8");
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
            //Log.d("RecuperarDatosUsuario", "Correo de recuperación de nombre de usuario enviado");
        }
    }

    /***********************************************************************************************
     *  Método que recupera el usuario asociado con el correo indicado por el usuario
     **********************************************************************************************/
    public void recuperaUsuario(){
        //Log.d("RecuperarDatosUsuario", "Recuperamos el nombre de usuario asociado al correo");
        request = new StringRequest(Request.Method.POST, url_consulta3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                        try {
                            JSONArray jsonArray = new JSONArray(response); // creamos array json para obtener el objeto del correo
                            nUsuario = jsonArray.getJSONObject(0).getString("nUsuario");
                            //Log.d("RecuperarDatosUsuario", "Nombre de usuario obtenido: " + nUsuario);
                            enviaCorreo();
                            // Creamos alerta de confirmación  para decir que se ha creado correctamente
                            // y mandamos a la pantalla de confirmación de usuario
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecuperarDatosUsuario.this);
                            builder.setTitle(R.string.title_dialog_recuperar_usuario); // titulo del diálogo
                            builder.setMessage(R.string.txt_dialog_recuperar_usuario)
                                    .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(RecuperarDatosUsuario.this, PantallaLogin.class);
                                            startActivity(intent);
                                        }
                                    });
                            // Create the AlertDialog object and return it
                            Dialog dialog = builder.create();
                            dialog.show();
                        } catch (Exception e){
                            e.printStackTrace();
                            //Log.e("RecuperarDatosUsuario", "Error al obtener el nombre de usuario");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Toast.makeText(RecuperarDatosUsuario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        Snackbar.make(findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.d("RecuperarDatosUsuario", "Error al conectar con el servidor para obtener el nombre de usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("correo", correo);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     * Método que genera y envía la clave nueva que se le enviará al usuario
     **********************************************************************************************/
    public void actualizaClave(){
        //Log.i("RecuperarDatosUsuario", "Generamos la clave nueva");
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Log.d("RecuperarDatosUsuario", "Clave generada: " + claveNueva);
                            enviaCorreo();
                            // Creamos alerta de confirmación  para decir que se ha creado correctamente
                            // y mandamos a la pantalla de confirmación de usuario
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecuperarDatosUsuario.this);
                            builder.setTitle(R.string.title_dialog_recuperar_clave); // titulo del diálogo
                            builder.setMessage(R.string.txt_dialog_recuperar_clave)
                                    .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(RecuperarDatosUsuario.this, PantallaLogin.class);
                                            startActivity(intent);
                                        }
                                    });
                            // Create the AlertDialog object and return it
                            Dialog dialog = builder.create();
                            dialog.show();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(RecuperarDatosUsuario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        Snackbar.make(findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.d("RecuperarDatosUsuario", "Error al conectar con el servidor para actualizar la clave de usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("correo", correo);
                parametros.put("clave", claveNueva);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    // Acciones a realizar por acción seleccionada del menú del action bar: 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Log.i("RecuperarDatosUsuario", "Action Atrás");
                Intent intent = new Intent (RecuperarDatosUsuario.this, PantallaLogin.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
