package es.proyecto.eva.miagendadam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import es.proyecto.eva.miagendadam.VolleyController.AppController;

public class RegistroNuevoUsuario extends AppCompatActivity {

    Button btnRegistro;
    EditText txtNombre;
    EditText txtApellidos;
    //EditText txtApellidoDos;
    EditText txtProvincia;
    EditText txtLocalidad;
    EditText txtCentroEstudios;
    EditText txtCiclo;
    EditText txtCentroPracticas;
    EditText txtCorreo;
    EditText txtNombreUsuario;
    EditText txtClave;
    EditText txtClave2;
    private String url_consulta = "http://192.168.0.10/MiAgenda/consulta_inserta_nuevo_usuario.php";
//    private String url_consulta = "http://192.168.0.158/MiAgenda/consulta_inserta_nuevo_usuario.php";
    // **************************************** SERVIDOR REMOTO ************************************************************
    //private String url_consulta = "http://miagendafp.000webhostapp.com/consulta_inserta_nuevo_usuario.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
    public static String correo="";
    private static int codigoConfirmacion;
    private static String sCodigoConfirmacion;
    private static String nombre="";
    private static String n_Usuario ="";
    private static String clave="";
    static boolean isConfirmed = false;
    static Session session;
    private static final String pattern_email = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"; // declaramos patrón para validar el formato del correo electrónico introducido
    // por el usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_nuevo_usuario);
        setTitle("Registro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // para VER la flecha atrás en el actionbar
        btnRegistro = (Button) findViewById(R.id.btn_registrarse);
        txtNombre = (EditText) findViewById(R.id.editText_nombre);
        txtApellidos = (EditText) findViewById(R.id.editText_ape_uno);
        //txtApellidoDos = (EditText) findViewById(R.id.editText_ape_dos);
        txtProvincia = (EditText) findViewById(R.id.editText_provincia);
        txtLocalidad = (EditText) findViewById(R.id.editText_localidad);
        txtCentroEstudios = (EditText) findViewById(R.id.editText_centro_estudios);
        txtCiclo = (EditText) findViewById(R.id.editText_ciclo);
        txtCentroPracticas = (EditText) findViewById(R.id.editText_centro_practicas);
        txtCorreo = (EditText) findViewById(R.id.editText_correo);
        txtNombreUsuario = (EditText) findViewById(R.id.editText_nombre_usuario);
        txtClave = (EditText) findViewById(R.id.editText_clave);
        txtClave2 = (EditText) findViewById(R.id.editText_confirma_clave);
    }

    // Al hacer click en el botón de registro...
    //
    public void registro(View view) {
        // Parámetros que vamos a pasar a la consulta
        nombre = txtNombre.getText().toString();
        final String apellidos = txtApellidos.getText().toString();
        //final String apellido_dos = txtApellidoDos.getText().toString();
        final String provincia = txtProvincia.getText().toString();
        final String localidad = txtLocalidad.getText().toString();
        final String centro_estudios = txtCentroEstudios.getText().toString();
        final String ciclo_formativo = txtCiclo.getText().toString();
        final String centro_practicas = txtCentroPracticas.getText().toString();
        correo = txtCorreo.getText().toString();
        n_Usuario = txtNombreUsuario.getText().toString();
        clave = txtClave.getText().toString();
        final String clave2 = txtClave2.getText().toString();

        // OBTENEMOS EL DATO fecha_registro, que guarda en la base de datos la fecha en el momento de pulsar
        // el botón de registrarse
        Date fechaR = new Date();
        final String fecha_registro = fechaR.toString();
        System.out.println("FECHA REGISTRO: " + fecha_registro);

        // validamos si alguno de los campos está vacío, para no dejarle seguir al usuario.
        if (nombre.isEmpty() || apellidos.isEmpty() || provincia.isEmpty() || localidad.isEmpty() || centro_estudios.isEmpty() ||
                ciclo_formativo.isEmpty() || centro_practicas.isEmpty() || correo.isEmpty() || n_Usuario.isEmpty() || clave.isEmpty() || clave2.isEmpty()) { // validamos que no haya ningún campo en blanco
            Toast.makeText(RegistroNuevoUsuario.this, "Debes rellenar todos los campos.", Toast.LENGTH_SHORT).show();
        } else {
            Pattern pattern = Pattern.compile(pattern_email); // creamos el patrón asignándole el formato declarado arriba para el correo electrónico
            Matcher matcher = pattern.matcher(correo); // le indicamos que queremos que aplique el patrón al correo
            if (!matcher.matches()){ // si el correo no cumple con el formato del patrón, salta el mensaje de error
                Toast.makeText(RegistroNuevoUsuario.this, "El correo electrónico introducido no es válido.", Toast.LENGTH_SHORT).show();
            } else {
                if (n_Usuario.length() < 6) {
                    Toast.makeText(RegistroNuevoUsuario.this, "Debes introducir un nombre de usuario que contenga entre 6 y 20 caracteres.", Toast.LENGTH_LONG).show();
                } else {
                    if (clave.length() < 8) {
                        Toast.makeText(RegistroNuevoUsuario.this, "Debes introducir una clave que contenga entre 8 y 20 caracteres.", Toast.LENGTH_LONG).show();
                    } else { // VALIDAMOS CARACTERES ACEPTADOS PARA LA CLAVE:
                        if (!clave.matches("(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z" // minúsculas
                                + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z" // mayúsculas
                                + "|0|1|2|3|4|5|6|7|8|9" // números
                                + "|!|=|-|_|@|:|%|~|#|&)+")) { // símbolos
                            Toast.makeText(RegistroNuevoUsuario.this, "No se pueden introducir espacios, tildes ni caracteres que no sean letras, números ó ! = - _ @" +
                                    " : % ~ # &", Toast.LENGTH_LONG).show();

                        } else {

                            if (!clave.equals(clave2)) {
                                Toast.makeText(RegistroNuevoUsuario.this, "Las claves introducidas no coinciden", Toast.LENGTH_SHORT).show();
                                System.out.println("CLAVES!!!" + clave + clave2);
                                txtClave.setText(""); // Borramos los campos de clave
                                txtClave2.setText("");
                            } else {
                                System.out.println("DATOS USUARIO A REGISTRAR: " + "\n" + nombre + "\n" + apellidos + "\n" + provincia + "\n" + localidad + "\n"
                                        + centro_estudios + "\n" + ciclo_formativo + "\n" + centro_practicas + "\n" + correo + "\n" + n_Usuario + "\n" + clave + "\n" + clave2);
                                // INICIAMOS CONEXIÓN CON VOLLEY
                                System.out.println("INICIAMOS CONEXIÓN");
                                StringRequest request = new StringRequest(Request.Method.POST, url_consulta,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                                                System.out.println("CONEXIÓN INICIADA!");
                                                if (response.equals("1")) {
                                                    try {

                                                        Toast.makeText(RegistroNuevoUsuario.this, "Ya hay un usuario registrado con ese email.", Toast.LENGTH_SHORT).show();
                                                        System.out.println("ERROR: Correo ya registrado.");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    if (response.equals("2")) {
                                                        try {
                                                            Toast.makeText(RegistroNuevoUsuario.this, "Ya existe un usuario con ese nombre.", Toast.LENGTH_SHORT).show();
                                                            System.out.println("ERROR: Usuario ya existe.");
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        if (response.equals("0")) { // datos y registro correcto
                                                            try {
                                                                // Creamos ventana de diálogo con circulo de carga para la espera de carga de los datos
                                                                /**
                                                                 ProgressDialog progressDialog = new ProgressDialog(RegistroNuevoUsuario.this);
                                                                 progressDialog.setTitle("Carga");
                                                                 progressDialog.setMessage("Creando usuario. Por favor, espere un momento.");
                                                                 progressDialog.show();*/
                                                                System.out.println("USUARIO CREADO CORRECTAMENTE :)");

                                                                enviarCorreoConfirmacion();
                                                                // Creamos alerta de confirmación  para decir que se ha creado correctamente
                                                                // y mandamos a la pantalla de confirmación de usuario
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistroNuevoUsuario.this);
                                                                builder.setTitle(R.string.title_dialog_registro_correcto); // titulo del diálogo
                                                                builder.setMessage(R.string.text_dialog_registro_correcto)
                                                                        .setPositiveButton(R.string.btn_aceptar_dialog, new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int id) {
                                                                                // mandamos a la pantalla de confirmación de registro
                                                                                Intent intent = new Intent (RegistroNuevoUsuario.this, ConfirmaRegistro.class);
                                                                                startActivity(intent);
                                                                            }
                                                                        });
                                                                /**.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                                 public void onClick(DialogInterface dialog, int id) {
                                                                 // User cancelled the dialog
                                                                 }
                                                                 });*/
                                                                // Create the AlertDialog object and return it
                                                                Dialog dialog = builder.create();
                                                                dialog.show();

                                                            } catch (Exception e) {
                                                                e.printStackTrace();
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
                                                Toast.makeText(RegistroNuevoUsuario.this, "Error de conexión.", Toast.LENGTH_SHORT).show();

                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        // AQUI SE ENVIARÁN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                                        Map<String, String> parametros = new HashMap<>();
                                        parametros.put("nombre", nombre);
                                        parametros.put("apellidos", apellidos);
                                       // parametros.put("apellido_dos", apellido_dos);
                                        parametros.put("provincia", provincia);
                                        parametros.put("localidad", localidad);
                                        parametros.put("centro_estudios", centro_estudios);
                                        parametros.put("ciclo_formativo", ciclo_formativo);
                                        parametros.put("centro_practicas", centro_practicas);
                                        parametros.put("correo", correo);
                                        parametros.put("nUsuario", n_Usuario);
                                        parametros.put("clave", clave);
                                        parametros.put("fecha_registro", fecha_registro);
                                        return parametros;
                                    }
                                };
                                AppController.getInstance().addToRequestQueue(request);
                            }
                        }
                    }

                }
            }
        }
    }
    //  }
    // });

    public void generaCodigoConfirmacion(){
        // generamos un código aleatorio de 6 dígitos
        codigoConfirmacion = (int) (Math.random() * 999999) + 1;
        System.out.println("CÓDIGO CONFIRMACIÓN INT!!!: "+codigoConfirmacion);
        sCodigoConfirmacion = Integer.toString(codigoConfirmacion); // pasamos el código a String para poder guardarlo como preferencia
        System.out.println("CÓDIGO CONFIRMACIÓN STRING!!!: "+sCodigoConfirmacion);
        guardarPreferencias(); // guardamos el dato
    }

    // guardamos como preferencia el código de confirmación
    public void guardarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("codigo_de_confirmacion", sCodigoConfirmacion );
        editor.putString("correo_electronico", correo);
        editor.commit();

    }

    public void enviarCorreoConfirmacion(){
        // enviamos correo de confirmación al usuario

        generaCodigoConfirmacion();
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("noreply.miagendafp@gmail.com", "fY6TKB#yvwSnX@ZvJkCu");
            }
        });

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("noreply.miagendafp@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
                message.setSubject("No-reply: Confirmación de registro");
                message.setContent("¡Hola " + nombre + "! Ya estás un paso más cerca de finalizar tu registro como usuario de <b>Mi agenda FP</b>, tan solo nos queda confirmar\n" +
                        "    tu cuenta introduciendo el código de confirmación que se indica aquí abajo. <br/><br/>\n" +
                        "        Código de confirmación: <b>"+ codigoConfirmacion + "</b><br/><br/> " +
                        "        Usuario: " + n_Usuario + "<br/><br/>"+
                        "Este mensaje se ha generado automáticamente. Por favor <b>no responda a este correo</b>, no recibirá ninguna respuesta.\n" +
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




    // PARA DAR FUNCIONALIDAD AL BOTÓN DE ATRÁS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
