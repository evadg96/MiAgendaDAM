package es.proyecto.eva.miagendafp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import es.proyecto.eva.miagendafp.VolleyController.AppController;

/***************************************************************************************************
 * Clase que se llama con la pulsación del botón de reenviar un código de confirmación al usuario.
 * Se solicita un correo electrónico y se le envía un código nuevo que deberá introducir en la
 * pantalla de confirmación de registro.
 **************************************************************************************************/
public class ReenviarCodigoConfirmacion extends AppCompatActivity {
    Button btnEnviar;
    EditText txtCorreo;
    static String nuevoCodigo="";
    static int codigo;
    static String correo="";
    static String codigo_de_confirmacion;
    private Session session;
    private StringRequest request;

    // Patrón para validar el formato del correo electrónico introducido
    private static final String pattern_email = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

//    private String url_consulta = "http://192.168.0.12/MiAgenda/check_correo.php";
//    private String url_consulta2 = "http://192.168.0.12/MiAgenda/clave_gmail.php";

//    private String url_consulta = "http://192.168.0.159/MiAgenda/check_correo.php";
//    private String url_consulta2 = "http://192.168.0.159/MiAgenda/clave_gmail.php";

    private String url_consulta = "http://miagendafp.000webhostapp.com/check_correo.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/clave_gmail.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reenviar_codigo_confirmacion);
        setTitle(R.string.title_activity_reenvia_codigo);
        btnEnviar = (Button) findViewById(R.id.btn_enviar);
        txtCorreo = (EditText) findViewById(R.id.editText_correo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Referenciamos al SharedPreferences que habíamos creado en la clase PantallaLogin
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        codigo_de_confirmacion = preferences.getString("codigo_de_confirmacion", ""); // obtenemos preferencia del código
        //Log.d("ReenviarCodigoConf", "Código actual: " + codigo_de_confirmacion );
        // Botón reenviar código
        btnEnviar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Log.d("ReenviarCodigoConf", "Reenviar código de confirmación");
                correo = txtCorreo.getText().toString();
                if (!correo.isEmpty()){
                    if (!correo.matches(pattern_email)){ // validamos el formato del email
                        Toast.makeText(ReenviarCodigoConfirmacion.this, R.string.error_correo_no_valido, Toast.LENGTH_SHORT).show();
                    } else {
                        request = new StringRequest(Request.Method.POST, url_consulta,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.equals("0")) { // no existe el correo en la bd
                                              Toast.makeText(ReenviarCodigoConfirmacion.this, R.string.error_correo_no_existe, Toast.LENGTH_SHORT).show();
                                            //Snackbar.make(findViewById(android.R.id.content),
                                              //      R.string.error_correo_no_existe, Snackbar.LENGTH_LONG).show();
                                            //Log.d("ReenviarCodigoConfirmacion", "No existe ningún usuario con ese correo");
                                        } else {
                                            if (response.equals("1")) { // existe el correo, así que le enviamos el código
                                                enviarCorreoConfirmacion();
                                                // Creamos alerta de confirmación  para decir que se ha creado correctamente
                                                // y mandamos a la pantalla de confirmación de usuario
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ReenviarCodigoConfirmacion.this);
                                                builder.setTitle(R.string.dialog_codigo_reenviado); // titulo del diálogo
                                                builder.setMessage(R.string.dialog_mensaje_codigo_reenviado)
                                                        .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                finish(); // cerramos la actividad para volver a la de confirmación
                                                            }
                                                        });
                                                // Create the AlertDialog object and return it
                                                Dialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(ReenviarCodigoConfirmacion.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                                        //Snackbar.make(findViewById(android.R.id.content),
                                          //      R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                                        //Log.e("ReenviarCodigoConfirmacion", "Error al conectar con el servidor para comprobar el correo electrónico introducido");
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> parametros = new HashMap<>();
                                parametros.put("correo", correo);
                                return parametros;
                            }

                        };
                        AppController.getInstance().addToRequestQueue(request);
                    }
                } else {
                   Toast.makeText(ReenviarCodigoConfirmacion.this, R.string.error_introducir_correo, Toast.LENGTH_SHORT).show();
                   // Snackbar.make(findViewById(android.R.id.content),
                     //       R.string.error_introducir_correo, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    /***********************************************************************************************
     * Método que genera un nuevo código de confirmación para enviar al usuario
     **********************************************************************************************/
    public void generaCodigoConfirmacion(){
        // generamos un código aleatorio de 6 dígitos
        codigo = (int) (Math.random() * 999999) + 1;
        nuevoCodigo = Integer.toString(codigo); // pasamos el código a String para poder guardarlo como preferencia
        //Log.d("ReenviarCodigoConf", "Código nuevo generado");
        guardarPreferencias(); // guardamos el dato
    }

    // guardamos como preferencia el código de confirmación
    public void guardarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("codigo_de_confirmacion", nuevoCodigo );
        editor.commit();
        //Log.d("ReenviarCodigoConf", "Preferencias guardadas. Código almacenado actualizado");
    }

    /***********************************************************************************************
     * Método que envía el correo con la nueva clave a la dirección de correo del usuario inroducido
     **********************************************************************************************/
    public void enviarCorreoConfirmacion(){
        generaCodigoConfirmacion();
        request = new StringRequest(Request.Method.POST, url_consulta2,
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
                            //Log.e("ReenviarCodigoConf", "Error al enviar el correo");
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       Toast.makeText(ReenviarCodigoConfirmacion.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                       // Snackbar.make(findViewById(android.R.id.content),
                         //       R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.e("ReenviarCodigoConf", "Error al conectar con el servidor para obtener la clave del correo noreply...");
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
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
                message.setSubject("No-reply: Confirmación de registro");
                message.setContent("<p style=\"text-align:justify\"> ¡Hola! Hemos recibido una solicitud de reenvío de código de confirmación de registro para la app <b>Mi agenda FP.</b></p>" +
                        "<p style=\"text-align:justify\">El código de confirmación es: <b>"+ nuevoCodigo + "</b></p> " +
                        "<div style=\"background-color:#EEEEEE; border:1px solid #BABABA; box-shadow: 2px 2px 5px #999; font-size:10px; text-align:justify\">" + // el sombreado no se ve en el móvil
                        "<p style=\"margin-left: 10px; margin-right: 11px\">" +
                        "Este mensaje se ha generado automáticamente. Por favor <b>no responda a este correo</b>, no recibirá ninguna respuesta.\n" +
                        "<br/>Si tiene algún problema, duda o sugerencia, contacte con el soporte a través de la dirección de correo <b>soportemiagendafp@gmail.com</b>\n" +
                        "<br/>Si ha recibido este correo por error, por favor, le rogamos que lo elimine y se ponga en contacto con la dirección de correo indicada arriba.\n"
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
            //Log.d("ReenviarCodigoConf", "Correo enviado");
        }
    }

    /***********************************************************************************************
     * Al pulsar el botón de atrás se vuelve un paso atrás en la aplicación
     * @param item
     * @return
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Log.i("ReenviarCodigoConf", "Action Atrás");
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
