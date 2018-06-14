package es.proyecto.eva.miagendafp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
// ****************** PUBLICIDAD ************************
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
/***************************************************************************************************
 *  Clase que confirma el registro de un usuario. Al momento del registro se le envía un código
 *  de confirmación al correo que haya introducido en el formulario de registro, y en esta pantalla
 *  deberá introducir el código. Esta clase se encargará de comprobar que el código introducido
 *  coincide con el código que se generó y se le mandó por correo, y le confirma en la base de datos.
 ***************************************************************************************************/

public class ConfirmaRegistro extends AppCompatActivity {
    /**
     * SE VALIDARÁ AL USUARIO POR SU CORREO, NO POR NOMBRE. ES DECIR, SE CONFIRMARÁ AL USUARIO CON EL CORREO QUE SE HAYA INTRODUCIDO
     * EN EL CAMPO DE CORREO, BIEN DURANTE EL REGISTRO, BIEN EN LA PETICIÓN DE REENVÍO DE CÓDIGO DE CONFIRMACIÓN
     */
    Button btnConfirmar, btnReenviar;
    EditText txtCodigo;
    TextView txtCorreo;
    private String codigo_de_confirmacion;
    private String correo_de_usuario = "";
    private StringRequest request;
    private int codigo;
    private String nuevoCodigo = "";
    private Session session;
    private String sCodigo = "";

    // ******************************* SERVIDORES Y CONSULTAS **************************************

//    private String url_consulta = "http://192.168.0.12/MiAgenda/update_isConfirmed.php";
//    private String url_consulta2 = "http://192.168.0.12/MiAgenda/check_correo.php";

//    private String url_consulta="http://192.168.0.159/MiAgenda/update_isConfirmed.php";
//    private String url_consulta2 = "http://192.168.0.159/MiAgenda/check_correo.php";

    private String url_consulta = "http://miagendafp.000webhostapp.com/update_isConfirmed.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/clave_gmail.php";

    // ******* PUBLICIDAD *******
    private AdView mAdView;

    // *********************************************************************************************
    @Override
    public void onResume(){
        super.onResume();
        // Referenciamos al SharedPreferences que habíamos creado en la clase PantallaLogin
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        codigo_de_confirmacion = preferences.getString("codigo_de_confirmacion", ""); // obtenemos preferencia del código
        correo_de_usuario = preferences.getString("correo_de_usuario", ""); // obtenemos preferencia del código
        System.out.println("CODIGO: " + codigo_de_confirmacion + " CORREO: " + correo_de_usuario);
        txtCorreo.setText(correo_de_usuario);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_registro);
        setTitle(R.string.title_activity_confirma_registro);
        btnConfirmar = (Button) findViewById(R.id.btn_confirmar);
        btnReenviar = (Button) findViewById(R.id.btn_reenviar_codigo);
        txtCorreo = (TextView) findViewById(R.id.tv_correo);
        txtCodigo = (EditText) findViewById(R.id.editText_codigo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // **************************** PUBLICIDAD *****************************************
// Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // comprobamos si el correo existe en la base de datos
        //Log.i("ConfirmaRegistro", "Código de confirmación actual: " + codigo_de_confirmacion);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("ConfirmaRegistro", "Confirmar registro");
                check_correo();
            }
        });

        // Botón reenviar código, abrimos pantalla de reenvío de código
        btnReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("ConfirmaRegistro", "Reenviar código de confirmación");
                reenviaCodigo();
            }
        });
    }

    /***********************************************************************************************
     * Método que comprueba que el correo introducido para confirmar el registro de usuario existe
     * en la base de datos
     **********************************************************************************************/
    public void check_correo(){
        //Log.i("ConfirmaRegistro", "Comprobamos existencia de correo introducido");
        if (!correo_de_usuario.isEmpty()){ // si se ha introducido un dato en el campo de correo...
            if (!codigo_de_confirmacion.isEmpty()) { // aseguramos que tengamos el dato del código almacenado
                // Obtenemos el dato introducido por el usuario en el campo de texto del código
                sCodigo = txtCodigo.getText().toString();
                if (sCodigo.equals(codigo_de_confirmacion)) { // EL CÓDIGO ES CORRECTO
                    // CONFIRMAMOS AL USUARIO
                        //Log.i("ConfirmaRegistro", "Comprobamos código de confirmación");
                        request = new StringRequest(Request.Method.POST, url_consulta,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Creamos diálogo alerta de aviso que lleva a pantalla login
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmaRegistro.this);
                                        builder.setMessage(R.string.dialog_confirmacion_correcta)
                                                .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        finish(); // cerramos la actividad para volver a pantalla de inicio de sesión
                                                    }
                                                })
                                                .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // Se cancela el diálogo, dejamos
                                                        // en blanco para que no se haga nada,
                                                        // solo cerrar el diálogo
                                                    }
                                                });
                                        Dialog dialog = builder.create();
                                        dialog.show(); // mostramos el diálogo
                                    }
                                },
                                // Error al confirmar el registro
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(ConfirmaRegistro.this, R.string.error_activacion_cuenta, Toast.LENGTH_LONG).show();
                                        // Snackbar.make(findViewById(android.R.id.content),
                                        //     R.string.error_confirmar_registro, Snackbar.LENGTH_LONG).show();
                                        //Log.e("ConfirmaRegistro", "Error al conectar con el servidor para confirmar el registro del usuario");
                                    }
                                }) {
                            // Enviamos los datos necesarios al script php para que pueda realizar la consulta
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                // Enviamos los datos en un objeto Map<clave, valor> (el nombre del dato que se pide en el script y el nombre de la variable
                                // que se enviará como ese dato)
                                Map<String, String> parametros = new HashMap<>();
                                parametros.put("correo", correo_de_usuario); // en este caso enviamos el correo
                                // para confirmar el registro del usuario asociado a dicho correo
                                return parametros;
                            }

                        };
                        AppController.getInstance().addToRequestQueue(request); // añadimos a la cola de peticiones la petición actual

                } else { // El código NO es correcto
                    Toast.makeText(ConfirmaRegistro.this, R.string.error_codigo_incorrecto, Toast.LENGTH_SHORT).show();
                    //Snackbar.make(findViewById(android.R.id.content),
                    //      R.string.error_codigo_incorrecto, Snackbar.LENGTH_SHORT).show();
                    //Log.d("ConfirmaRegistro", "Código incorrecto");
                }
            } else { // Si no hay codigo de confirmación en las preferencias, le decimos que ha expirado, para que solicite uno nuevo
                Toast.makeText(ConfirmaRegistro.this, R.string.error_codigo_expirado, Toast.LENGTH_LONG).show();
                // Snackbar.make(findViewById(android.R.id.content),
                //   R.string.error_codigo_expirado, Snackbar.LENGTH_LONG).show();
                //Log.d("ConfirmaRegistro", "No hay ningún código almacenado");

            }
        } else { // si el campo de nombre de usuario está vacío
            Toast.makeText(ConfirmaRegistro.this, R.string.error_introducir_correo, Toast.LENGTH_SHORT).show();
          //  Snackbar.make(findViewById(android.R.id.content),
            //        R.string.error_introducir_correo, Snackbar.LENGTH_SHORT).show();
            //Log.e("ConfirmaRegistro", "No hay nombre de usuario almacenado");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Al pulsar el icono de la flecha atrás de la barra de acciones
            case android.R.id.home:
                //Log.i("ConfirmaRegistro", "Action Atrás");
                // Para que no nos mande de vuelta al registro recién hecho, porque esta pantalla se abre
                // por primera vez al terminar el registro de nuevo usuario
               onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Al pulsar hacia atrás se cierra la actividad
     **********************************************************************************************/
    public void onBackPressed(){
        finish();
    }

    /**
     * Método que muestra diálogo preguntando si se desea reenviar el código al correo obtenido del usuario
     */
    public void reenviaCodigo(){
        // Creamos diálogo alerta de aviso que lleva a pantalla login
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmaRegistro.this);
        builder.setMessage("¿Deseas reenviar un código de confirmación al correo " + correo_de_usuario + "?")
                .setPositiveButton(R.string.btn_enviar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        enviarCorreoConfirmacion();
                    }
                })
                .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Se cancela el diálogo, dejamos
                        // en blanco para que no se haga nada,
                        // solo cerrar el diálogo
                    }
                });
        Dialog dialog = builder.create();
        dialog.show(); // mostramos el diálogo
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
        sCodigo = nuevoCodigo;
        codigo_de_confirmacion = nuevoCodigo;
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
                            Toast.makeText(ConfirmaRegistro.this, R.string.codigo_reenviado, Toast.LENGTH_LONG).show();

                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(ConfirmaRegistro.this, R.string.error_reenvio_correo_activacion, Toast.LENGTH_LONG).show();
                            //Log.e("ReenviarCodigoConf", "Error al enviar el correo");
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ConfirmaRegistro.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
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
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo_de_usuario));
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

}
