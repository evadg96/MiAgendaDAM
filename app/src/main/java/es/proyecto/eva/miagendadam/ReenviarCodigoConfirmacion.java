package es.proyecto.eva.miagendadam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
    static Session session;
    static StringRequest request;
    static String url_consulta = "http://192.168.0.10/MiAgenda/consulta_check_correo.php";
    static String url_consulta2 = "http://192.168.0.10/MiAgenda/clave_gmail.php";
//    static String url_consulta = "http://192.168.0.158/MiAgenda/consulta_check_correo.php";
    // ************************************** SERVIDOR REMOTO *****************************************
    //private String url_consulta = "http://miagendafp.000webhostapp.com/consulta_check_correo.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reenviar_codigo_confirmacion);
        setTitle("Reenviar código confirmación");
        btnEnviar = (Button) findViewById(R.id.btn_enviar);
        txtCorreo = (EditText) findViewById(R.id.editText_correo);
        // Referenciamos al SharedPreferences que habíamos creado en la clase PantallaLogin
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        codigo_de_confirmacion = preferences.getString("codigo_de_confirmacion", ""); // obtenemos preferencia del código


        // Botón reenviar código
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo = txtCorreo.getText().toString();
                if (!correo.isEmpty()){
                    request = new StringRequest(Request.Method.POST, url_consulta,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                                    if (response.equals("0")) { // no existe el correo en la bd
                                        Toast.makeText(ReenviarCodigoConfirmacion.this, "No hay ningún usuario registrado con ese correo.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (response.equals("1")) { // existe el correo, así que le enviamos el código
                                            enviarCorreoConfirmacion();
                                            // Creamos alerta de confirmación  para decir que se ha creado correctamente
                                            // y mandamos a la pantalla de confirmación de usuario
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ReenviarCodigoConfirmacion.this);
                                            builder.setTitle("Código enviado"); // titulo del diálogo
                                            builder.setMessage("Se ha enviado un código nuevo de confirmación. Revisa tu bandeja de entrada.")
                                                    .setPositiveButton(R.string.btn_aceptar_dialog, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Intent intent = new Intent(ReenviarCodigoConfirmacion.this, ConfirmaRegistro.class);
                                                            startActivity(intent);
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
                                    // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                    Toast.makeText(ReenviarCodigoConfirmacion.this, "Error de conexión.", Toast.LENGTH_SHORT).show();

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

                } else {
                    Toast.makeText(ReenviarCodigoConfirmacion.this, "Introduce tu correo electrónico.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void generaCodigoConfirmacion(){
        // generamos un código aleatorio de 6 dígitos
        codigo = (int) (Math.random() * 999999) + 1;
        System.out.println("CÓDIGO CONFIRMACIÓN INT!!!: "+codigo);
        nuevoCodigo = Integer.toString(codigo); // pasamos el código a String para poder guardarlo como preferencia
        System.out.println("CÓDIGO CONFIRMACIÓN STRING!!!: "+nuevoCodigo);
        guardarPreferencias(); // guardamos el dato
    }

    // guardamos como preferencia el código de confirmación
    public void guardarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("codigo_de_confirmacion", nuevoCodigo );
        editor.putString("correo_electronico", correo);
        editor.commit();
        System.out.println("NUEVAS PREFERENCIAS GUARDADAS\n: CÓDIGO CONFIRMACIÓN: " + nuevoCodigo + "\nCORREO ELECTRÓNICO: "+ correo );
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
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(ReenviarCodigoConfirmacion.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                    }
                });
        AppController.getInstance().addToRequestQueue(request);
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("noreply.miagendafp@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
                message.setSubject("No-reply: Confirmación de registro");
                message.setContent(" Código de confirmación: <b>"+ nuevoCodigo + "</b><br/><br/> " +
                        "       Este mensaje se ha generado automáticamente. Por favor <b>no responda a este correo</b>, no recibirá ninguna respuesta.\n" +
                        "    <br/> Si tiene algún problema, duda o sugerencia, contacte con el soporte a través de la dirección de correo <b>soportemiagendafp@gmail.com</b>\n" +
                        "        <br/> Si ha recibido este correo por error, por favor, le rogamos que lo elimine y se ponga en contacto con la dirección de correo indicada arriba.\n" +
                        "        <br/> Atentamente, el equipo de <b>Mi agenda FP</b>.", "text/html; charset=utf-8");
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
}
