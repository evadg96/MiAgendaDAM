package es.proyecto.eva.miagendadam.Fragments.Contactos;

import android.app.Dialog;
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

import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

import static es.proyecto.eva.miagendadam.Fragments.Contactos.ContactosFragment.correo_seleccionado;
import static es.proyecto.eva.miagendadam.Fragments.Contactos.ContactosFragment.id_contacto_seleccionado;
import static es.proyecto.eva.miagendadam.Fragments.Contactos.ContactosFragment.modulo_seleccionado_codificado;
import static es.proyecto.eva.miagendadam.Fragments.Contactos.ContactosFragment.nombre_seleccionado_codificado;
import static es.proyecto.eva.miagendadam.Fragments.Contactos.ContactosFragment.telefono_seleccionado;

/**********************************************************************************************************************
 * Mediante esta clase se lleva a cabo el proceso de actualización de un contacto para el que se ha editado alguno de
 * los datos personales que lo componen
 *********************************************************************************************************************/
public class EditarContacto extends AppCompatActivity {
    private String url_consulta = "http://miagendafp.000webhostapp.com/update_contactos.php";
    EditText txtNombre, txtModulo, txtCorreo, txtTelefono;
    private String nombreContacto = "", correoContacto = "", modulo = "", telefono = "", idUsuario = "";
    private StringRequest request;
    // Patrón de caracteres que queremos que ACEPTE el nombre del contacto
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

    // todo añadir validación de teléfono aquí también

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        // Obtenemos de las preferencias el nombre del usuario
        SharedPreferences preferences = this.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", ""); // obtenemos el id del usuario al que vamos a introducir el registro.
        txtNombre = (EditText) findViewById(R.id.txt_nombre_contacto_2);
        txtModulo = (EditText) findViewById(R.id.txt_modulo_2);
        txtCorreo = (EditText) findViewById(R.id.txt_correo_2);
        txtTelefono = (EditText) findViewById(R.id.txt_telefono_2);
        txtNombre.setText(nombre_seleccionado_codificado);
        txtModulo.setText(modulo_seleccionado_codificado);
        txtCorreo.setText(correo_seleccionado);
        txtTelefono.setText(telefono_seleccionado);
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
            case R.id.menu_guardar: // al pulsar el botón de guardar...
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
                    Toast.makeText(EditarContacto.this, R.string.error_campos_vacios, Toast.LENGTH_LONG).show();
                } else {
                    Pattern pattern = Pattern.compile(pattern_formato_nombre); // creamos el patrón asignándole los caracteres que no queremos que tenga
                    Matcher matcher = pattern.matcher(nombreContacto); // le indicamos que queremos que aplique el patrón al correo
                    // Primero validamos el nombre del contacto introducido...
                    if (!matcher.matches()) { // si el nombre del contacto no cumple con los caracteres aceptados por el patrón, no dejamos guardar
                        txtNombre.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        System.out.println("FORMATO NOMBRE INVÁLIDO");
                        Toast.makeText(EditarContacto.this, R.string.error_nombre_invalido, Toast.LENGTH_LONG).show();
                    } else {
                        // Después validamos el rol introducido...
                        if (!modulo.matches(pattern_formato_modulo)){
                            txtModulo.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                            System.out.println("FORMATO MÓDULO INVÁLIDO");
                            Toast.makeText(EditarContacto.this, R.string.error_modulo_invalido, Toast.LENGTH_LONG).show();
                        } else {
                            // A continuación validamos el correo electrónico...
                            if (!correoContacto.matches(pattern_email)){
                                txtCorreo.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                System.out.println("FORMATO CORREO INVÁLIDO");
                                Toast.makeText(EditarContacto.this, R.string.error_correo_no_valido, Toast.LENGTH_LONG).show();
                            } else {
                                // Se han validado los campos correctamente, actualizamos finalmente el contacto:
                                actualizarContacto();
                            }
                        }
                    }
                    // actualizamos los datos del contacto seleccionado y actualizado para que aparezcan actualizados
                    // en la pantalla de visualización en detalle del contacto
                    System.out.println("DATOS ACTUALIZADOS.");
                    System.out.println("NOMBRE SELECCIONADO: " + nombre_seleccionado_codificado + "\n CORREO SELECCIONADO: "+ correo_seleccionado
                    + "\n MODULO SELECCIONADO: "+ modulo_seleccionado_codificado + "\n TELEFONO: "+ telefono_seleccionado);
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
     * Método que ejecuta la consulta que actualiza el contacto
     **********************************************************************************************/
    public void actualizarContacto(){
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        nombre_seleccionado_codificado = nombreContacto;
                        correo_seleccionado = correoContacto;
                        modulo_seleccionado_codificado = modulo;
                        telefono_seleccionado = telefono;
                        if(response.equals("1")){
                            Toast.makeText(EditarContacto.this, R.string.editar_contacto, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(EditarContacto.this, R.string.error_editar_contacto, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditarContacto.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombreContacto", nombreContacto);
                parametros.put("correoContacto", correoContacto);
                parametros.put("telefono", telefono);
                parametros.put("modulo", modulo);
                parametros.put("idContacto", id_contacto_seleccionado);
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
        // primero validamos si se ha hecho alguna modificación en los datos del contacto al momento de pulsar atrás
      if (nombreContacto.equals(nombre_seleccionado_codificado) && correoContacto.equals(correo_seleccionado) && modulo.equals(modulo_seleccionado_codificado) &&
              telefono.equals(telefono_seleccionado)) {
            finish(); // al no haber cambiado nada, volvemos atrás sin preguntar nada
      } else { // si en cambio algún dato es diferente, deberemos preguntar ya que se perderían los cambios realizados
          android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditarContacto.this);
          builder.setTitle(R.string.titulo_dialog_salir_sin_guardar); // titulo del diálogo
          builder.setMessage(R.string.contenido_dialog_salir_editar_sin_guardar)
                  .setPositiveButton(R.string.respuesta_dialog_volver, new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          finish();// cerramos la actividad actual
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
