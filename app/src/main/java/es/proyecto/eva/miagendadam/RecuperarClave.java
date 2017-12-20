package es.proyecto.eva.miagendadam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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

public class RecuperarClave extends AppCompatActivity {
    EditText txtCorreo;
    Button btnEnviarClave;
    Button btnEnviarUsuario;
    static String correo;
    static String nUsuario;
    static Session session;
    static StringRequest request;

    static String url_consulta = "http://192.168.0.10/MiAgenda/consulta_recuperar_clave.php";
    static String url_consulta2 = "http://192.168.0.10/MiAgenda/consulta_check_correo.php";
    static String url_consulta3 = "http://192.168.0.10/MiAgenda/consulta_recuperar_usuario.php";
    static String url_consulta4 = "http://192.168.0.10/MiAgenda/clave_gmail.php";
//    static String url_consulta = "http://192.168.0.158/MiAgenda/consulta_recuperar_clave.php";
//    static String url_consulta2 = "http://192.168.0.158/MiAgenda/consulta_check_usuario_existe.php";

    // VARIABLES PARA GENERAR LA CLAVE NUEVA
    private static final String dCase = "abcdefghijklmnopqrstuvwxyz";
    private static final String uCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String sChar = "!=-_@:%&~#";
    private static final String intChar = "0123456789";
    private static Random r = new Random();
    // ------- Usamos los booleanos para determinar qué botón ha pulsado el usuario y qué método queremos ejecutar --------
    private static boolean enviarClave = false;
    private static boolean enviarUsuario = false;
    // --------------------------------------------------------------------------------------------------------------------
    // Generamos la clave directamente en el string
    private static String generaClave(){
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
        return pass;
    }
    // metemos la clave generada en otro string
    private String claveNueva = generaClave();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_clave);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // para VER la flecha atrás en el actionbar
        setTitle("Recuperar datos");
        btnEnviarClave = (Button) findViewById(R.id.btn_enviar_nueva_clave);
        btnEnviarUsuario = (Button) findViewById(R.id.btn_enviar_nombre_usuario);
        txtCorreo = (EditText) findViewById(R.id.txt_nombre_usuario);
        // Botón He olvidado mi contraseña, abre actividad de RecuperarClave
        btnEnviarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarClave = true;
                check_correo();
            }
        });

        btnEnviarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarUsuario = true;
                check_correo();
            }
        });
    }


    /***********************************************************************************************
     * Método que comprueba si existe algún usuario con el correo introducido
     **********************************************************************************************/
    public void check_correo(){
        correo = txtCorreo.getText().toString();
        if (!correo.isEmpty()){ // si se ha introducido un dato en el campo de correo...
            request = new StringRequest(Request.Method.POST, url_consulta2,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                            if (response.equals("0")) { // no existe el usuario en la bd
                                Toast.makeText(RecuperarClave.this, "No hay ningún usuario registrado con ese correo.", Toast.LENGTH_SHORT).show();
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
                            // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                            Toast.makeText(RecuperarClave.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
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
        } else { // si el campo de nombre de usuario está vacío
            Toast.makeText(RecuperarClave.this, "Debes introducir tu correo electrónico.", Toast.LENGTH_SHORT).show();
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
                                RecuperarClave.RetreiveFeedTask task = new RetreiveFeedTask();
                                task.execute();
                                enviarClave = false;
                            } else if (enviarUsuario){
                                // Establecemos el contenido del correo con los datos del usuario
                                RecuperarClave.RetreiveFeedTask2 task2 = new RetreiveFeedTask2();
                                task2.execute();
                                enviarUsuario = false;
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(RecuperarClave.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
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
                message.setFrom(new InternetAddress("noreply.miagendafp@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
                message.setSubject("No-reply: Solicitud de contraseña nueva");
                message.setContent("Hemos recibido una solicitud de cambio de contraseña para su cuenta de <b>Mi agenda FP</b>." +
                        " <br/>Su contraseña nueva es: <b>"+ claveNueva + "</b><br/><br/>" +
                        " Este mensaje se ha generado automáticamente porque se ha recibido una solicitud de cambio de contraseña para el usuario registrado con este correo. "+
                        " Puede volver a cambiar su contraseña desde su perfil de usuario." +
                        " <br/>Por favor <b>no responda a este correo</b>, no recibirá ninguna respuesta.\n" +
                        " <br/><br/> Si tiene algún problema, duda o sugerencia, contacte con el soporte a través de la dirección de correo <b>soportemiagendafp@gmail.com</b>\n" +
                        " <br/><br/> Si ha recibido este correo por error, por favor, le rogamos que lo elimine y se ponga en contacto con la dirección de correo indicada arriba.\n" +
                        " <br/><br/> Atentamente, el equipo de <b>Mi agenda FP</b>.", "text/html; charset=utf-8");
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
            System.out.println("CORREO ENVIADO CORRECTAMENTE");
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
                message.setContent("Hemos recibido una solicitud de recuperación de usuario para su cuenta de <b>Mi agenda FP</b>." +
                        " <br/>Su nombre de usuario es: <b>"+ nUsuario + "</b><br/><br/>" +
                        " Este mensaje se ha generado automáticamente porque se ha recibido una solicitud de recuperación de nombre de usuario para el usuario registrado con este correo. "+
                        " Puede volver a cambiar su contraseña desde su perfil de usuario." +
                        " <br/>Por favor <b>no responda a este correo</b>, no recibirá ninguna respuesta.\n" +
                        " <br/><br/> Si tiene algún problema, duda o sugerencia, contacte con el soporte a través de la dirección de correo <b>soportemiagendafp@gmail.com</b>\n" +
                        " <br/><br/> Si ha recibido este correo por error, por favor, le rogamos que lo elimine y se ponga en contacto con la dirección de correo indicada arriba.\n" +
                        " <br/><br/> Atentamente, el equipo de <b>Mi agenda FP</b>.", "text/html; charset=utf-8");
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
            System.out.println("CORREO ENVIADO CORRECTAMENTE");
        }
    }

    /***********************************************************************************************
     *  Método que recupera el usuario asociado con el correo indicado por el usuario
     **********************************************************************************************/
    public void recuperaUsuario(){
        request = new StringRequest(Request.Method.POST, url_consulta3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                        try {
                            JSONArray jsonArray = new JSONArray(response); // creamos array json para obtener el objeto del correo
                            nUsuario = jsonArray.getJSONObject(0).getString("nUsuario");
                            System.out.println("NOMBRE DE USUARIO OBTENIDO DEL CORREO INTRODUCIDO: "+ nUsuario);
                            enviaCorreo();
                            // Creamos alerta de confirmación  para decir que se ha creado correctamente
                            // y mandamos a la pantalla de confirmación de usuario
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecuperarClave.this);
                            builder.setTitle("Nombre de usuario enviado"); // titulo del diálogo
                            builder.setMessage("Se ha enviado el nombre de usuario del correo indicado. Revisa tu bandeja de entrada.")
                                    .setPositiveButton(R.string.btn_aceptar_dialog, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(RecuperarClave.this, PantallaLogin.class);
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
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(RecuperarClave.this, "Error de conexión.", Toast.LENGTH_SHORT).show();

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
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                        try {
                            System.out.println("CLAVE NUEVA DEL USUARIO: "+ claveNueva);
                        enviaCorreo();
                        // Creamos alerta de confirmación  para decir que se ha creado correctamente
                        // y mandamos a la pantalla de confirmación de usuario
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecuperarClave.this);
                        builder.setTitle("Contraseña enviada"); // titulo del diálogo
                        builder.setMessage("Se ha enviado una contraseña nueva. Revisa tu bandeja de entrada.")
                                .setPositiveButton(R.string.btn_aceptar_dialog, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(RecuperarClave.this, PantallaLogin.class);
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
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(RecuperarClave.this, "Error de conexión.", Toast.LENGTH_SHORT).show();

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

    // PARA DAR FUNCIONALIDAD AL BOTÓN DE ATRÁS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent (RecuperarClave.this, PantallaLogin.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
