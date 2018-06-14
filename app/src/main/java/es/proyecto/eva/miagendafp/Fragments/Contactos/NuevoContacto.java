package es.proyecto.eva.miagendafp.Fragments.Contactos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.proyecto.eva.miagendafp.R;
import es.proyecto.eva.miagendafp.VolleyController.AppController;
// ****************** PUBLICIDAD ************************
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
public class NuevoContacto extends AppCompatActivity {
    private String url_consulta = "http://miagendafp.000webhostapp.com/inserta_nuevo_contacto.php";
    EditText txtNombre, txtCorreo, txtModulo, txtTelefono;
    private String nombreContacto = "", correoContacto = "", modulo = "", telefono = "", idUsuario = "";
    private StringRequest request;
    private ProgressDialog progressDialog;

    // ******* PUBLICIDAD *******
    private AdView mAdView;

    private String pattern_formato_nombre = "( |a|b|c|d|e|f|g|h|i|j|k|l|m|n|ñ|o|p|q|r|s|t|u|v|w|x|y|z" // minúsculas
            + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|Ñ|O|P|Q|R|S|T|U|V|W|X|Y|Z" // mayúsculas
            + "|á|é|í|ó|ú|Á|É|Í|Ó|Ú|ç|Ç|à|è|ì|ò|ù|À|È|Ì|Ò|Ù|ä|ë|ï|ö|ü|Ä|Ë|Ï|Ö|Ü|â|ê|î|ô|û|Â|Ê|Î|Ô|Û|ã|õ|Ã|Õ)+"; // letras con tildes u otros caracteres

    // Patrón de caracteres que queremos que acepte el módulo (rol que desempeña el contacto)
    private String pattern_formato_modulo = "( |a|b|c|d|e|f|g|h|i|j|k|l|m|n|ñ|o|p|q|r|s|t|u|v|w|x|y|z" // minúsculas
            + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|Ñ|O|P|Q|R|S|T|U|V|W|X|Y|Z" // mayúsculas
            + "|á|é|í|ó|ú|Á|É|Í|Ó|Ú|ç|Ç|à|è|ì|ò|ù|À|È|Ì|Ò|Ù|ä|ë|ï|ö|ü|Ä|Ë|Ï|Ö|Ü|â|ê|î|ô|û|Â|Ê|Î|Ô|Û|ã|õ|Ã|Õ"
            + "|0|1|2|3|4|5|6|7|8|9)+";

    // Patrón de caracteres para validar un formato de email correcto
    private static final String pattern_email = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_contacto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_nuevo_contacto);
        txtNombre = (EditText) findViewById(R.id.txt_nombre_contacto);
        txtCorreo = (EditText) findViewById(R.id.txt_correo);
        txtModulo = (EditText) findViewById(R.id.txt_modulo);
        txtTelefono = (EditText) findViewById(R.id.txt_telefono);
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario al que vamos a introducir el registro.
        // **************************** PUBLICIDAD *****************************************
// Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    // Añade los iconos a la barra de acciones (en este caso, el de guardar)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo, menu);
        return true; // .menu es el directorio, y .menu_nuevo el archivo
    }

    /***********************************************************************************************
     *     Opciones del menú de la barra de acciones
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.menu_guardar: // Opción de guardar registro
                // Log.i("NuevoRegistroDiario", "Action Guardar registro");
                nombreContacto = txtNombre.getText().toString();
                correoContacto = txtCorreo.getText().toString();
                modulo = txtModulo.getText().toString();
                telefono = txtTelefono.getText().toString();

                txtNombre.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                txtModulo.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                txtCorreo.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                txtTelefono.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

                // Todos los campos son obligatorios salvo el del teléfono, que no se tiene por qué conocer
                if (nombreContacto.isEmpty() || correoContacto.isEmpty() || modulo.isEmpty()){
                    // Toast.makeText(NuevoRegistroDiario.this, "Debes completar todos los datos.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(NuevoContacto.this, R.string.error_campos_vacios, Toast.LENGTH_LONG).show();
                } else { // pasamos a validación de nombre
                    Pattern pattern = Pattern.compile(pattern_formato_nombre); // creamos el patrón asignándole los caracteres que no queremos que tenga
                    Matcher matcher = pattern.matcher(nombreContacto); // le indicamos que queremos que aplique el patrón al correo
                    if (!matcher.matches()) { // si el nombre del contacto a guardar no contiene exclusivamente caracteres del patrón, no dejamos guardar
                        txtNombre.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        Toast.makeText(NuevoContacto.this, R.string.error_nombre_invalido, Toast.LENGTH_LONG).show();
                    } else {
                        // Después validamos el rol introducido...
                        if (!modulo.matches(pattern_formato_modulo)){
                            txtModulo.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                            System.out.println("FORMATO MÓDULO INVÁLIDO");
                            Toast.makeText(NuevoContacto.this, R.string.error_modulo_invalido, Toast.LENGTH_LONG).show();
                        } else {
                            // A continuación validamos el correo electrónico...
                            if (!correoContacto.matches(pattern_email)) {
                                txtCorreo.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                System.out.println("FORMATO CORREO INVÁLIDO");
                                Toast.makeText(NuevoContacto.this, R.string.error_correo_no_valido, Toast.LENGTH_LONG).show();
                            } else {
                                // Se han validado los campos correctamente, actualizamos finalmente el contacto:
                                guardarContacto();
                            }
                        }
                    }
                }
                return true;
            case android.R.id.home: // Opción de volver hacia atrás
                // Log.i("NuevoRegistroDiario", "Action Atrás");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Método que guarda el nuevo contacto introducido
     **********************************************************************************************/
    public void guardarContacto(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.dialog_cargando);
        progressDialog.setMessage("Guardando contacto...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("1")){
                            progressDialog.dismiss();
                            Toast.makeText(NuevoContacto.this, R.string.contacto_guardado, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(NuevoContacto.this, R.string.error_guardar_contacto, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NuevoContacto.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombreContacto", nombreContacto);
                parametros.put("correoContacto", correoContacto);
                parametros.put("telefono", telefono);
                parametros.put("modulo", modulo);
                parametros.put("idUsuario", idUsuario); // pasamos el nombre de usuario como parámetro de la consulta para obtener sus registros del diario
                return parametros;
            }

        };
        AppController.getInstance().addToRequestQueue(request);
    }

    public void onBackPressed(){
        nombreContacto = txtNombre.getText().toString();
        correoContacto = txtCorreo.getText().toString();
        modulo = txtModulo.getText().toString();
        telefono = txtTelefono.getText().toString();
        // comprobamos si los campos están totalmente vacíos, ya que de esa manera volveremos atrás sin preguntar nada, puesto que el usuario no perderá cambios significativos
        if (nombreContacto.isEmpty() && correoContacto.isEmpty() && modulo.isEmpty() && telefono.isEmpty()){
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(NuevoContacto.this);
            builder.setTitle(R.string.titulo_dialog_salir_sin_guardar); // titulo del diálogo
            builder.setMessage(R.string.contenido_dialog_salir_sin_guardar)
                    .setPositiveButton(R.string.respuesta_dialog_volver, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish(); // volvemos atrás
                        }
                    })
                    .setNegativeButton(R.string.respuesta_dialog_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            //no hacemos nada, y al pulsar el botón simplemente se cerrará el diálogo
                        }
                    });
            // Create the AlertDialog object and return it
            Dialog dialog = builder.create();
            dialog.show();
        }
    }

}
