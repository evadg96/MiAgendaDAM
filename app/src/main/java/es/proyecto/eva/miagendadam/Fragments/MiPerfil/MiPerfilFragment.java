package es.proyecto.eva.miagendadam.Fragments.MiPerfil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

import static es.proyecto.eva.miagendadam.NavMenu.nombre_del_estudiante;
import static es.proyecto.eva.miagendadam.NavMenu.apellidos_del_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.centro_estudios_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.centro_practicas_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.horas_fct_usuario;

/***************************************************************************************************
 * Fragmento de la opción Mi perfil, que permite la visualización de los datos del usuario, así como
 * la modificación de algunos de estos datos.
 **************************************************************************************************/
public class MiPerfilFragment extends Fragment {

    EditText txtNombre, txtApellidos, txtHorasFCT, txtCentroEstudios, txtCentroPracticas, txtClave, txtRepiteClave;
    Spinner spinnerProvincia, spinnerFamiliaCiclo, spinnerCiclo;
    TextView tvNombreSaludo, tvNombreUsuario, tvCorreo;
    Button btnActualizaClave;
    private StringRequest request;
    private String url_consulta = "http://miagendafp.000webhostapp.com/update_datos_perfil_usuario.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/update_clave_perfil_usuario.php";

    private String correo_usuario = "", nombre_de_usuario = "";

    private String claveNueva = "", repiteClave = "";
    // Patrón para controlar el formato de la contraseña nueva
    private String pattern_formato = "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z" // minúsculas
            + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z" // mayúsculas
            + "|0|1|2|3|4|5|6|7|8|9" // números
            + "|!|=|-|_|@|:|%|~|#)+";

    public MiPerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // para visualizar el menú en el action bar
    }

    // Creamos el menú en el action bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mi_perfil, menu);
    }

    // Selección de opciones del menú del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_actualizar_perfil: // Opción de guardar los datos de usuario actualizados
                //Log.i("MiPerfilFragment", "Action Actualizar datos de usuario");
                actualizarDatosUsuario();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mi_perfil, container, false);
        // Obtenemos preferencias:
        SharedPreferences preferences = this.getActivity().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        nombre_de_usuario = preferences.getString("nombre_de_usuario", "");
        correo_usuario = preferences.getString("correo_de_usuario", "");
        //Log.d("MiPerfilFragment", "Nombre de usuario obtenido: " + nombre_de_usuario);
        //Log.d("MiPerfilFragment", "Correo de usuario obtenido: " + correo_usuario);
        txtNombre = (EditText) view.findViewById(R.id.txt_nombre_mi_perfil);
        txtApellidos = (EditText) view.findViewById(R.id.txt_apellidos_mi_perfil);
        txtHorasFCT = (EditText) view.findViewById(R.id.txt_horas_fct_mi_perfil);
        txtCentroEstudios = (EditText) view.findViewById(R.id.txt_centro_estudios_mi_perfil);
        txtCentroPracticas = (EditText) view.findViewById(R.id.txt_centro_practicas_mi_perfil);
        txtClave = (EditText) view.findViewById(R.id.txt_clave_mi_perfil);
        txtRepiteClave = (EditText) view.findViewById(R.id.txt_repite_clave_mi_perfil);
        spinnerProvincia = (Spinner) view.findViewById(R.id.spinner_provincias_mi_perfil);
        spinnerFamiliaCiclo = (Spinner) view.findViewById(R.id.spinner_familias_ciclo_mi_perfil);
        spinnerCiclo = (Spinner) view.findViewById(R.id.spinner_ciclo_mi_perfil);
        tvNombreSaludo = (TextView) view.findViewById(R.id.nombre_saludo_mi_perfil);
        tvNombreUsuario = (TextView) view.findViewById(R.id.tv_nombre_usuario_mi_perfil);
        tvCorreo = (TextView) view.findViewById(R.id.tv_correo_mi_perfil);
        btnActualizaClave = (Button) view.findViewById(R.id.btn_cambiar_clave);
        // colocamos los datos obtenidos en NavMenu en los campos correspondientes
        rellenarCampos();
        // Al pulsar el botón de actualizar clave...
        btnActualizaClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("MiPerfilFragment", "Actualizar clave de usuario");
                actualizarClave();
            }
        });
        return view;
    }

    /***********************************************************************************************
     * Método que rellena los campos con los datos del usuario obtenidos
     **********************************************************************************************/
    public void rellenarCampos(){
        tvNombreSaludo.setText(" " + nombre_del_estudiante);
        txtNombre.setText(nombre_del_estudiante);
        txtApellidos.setText(apellidos_del_usuario);
        txtCentroEstudios.setText(centro_estudios_usuario);
        txtCentroPracticas.setText(centro_practicas_usuario);
        txtHorasFCT.setText(horas_fct_usuario);
        tvNombreUsuario.setText(nombre_de_usuario);
        tvCorreo.setText(correo_usuario);
    }

    /***********************************************************************************************
     * Método que actualiza los datos del usuario desde el perfil
     **********************************************************************************************/
    public void actualizarDatosUsuario(){
        // TODO: Implementar método de actualización de datos de usuario
        // Validamos campos:

    }

    /***********************************************************************************************
     * Método que actualiza la contraseña del usuario
     **********************************************************************************************/
    public void actualizarClave() {
        // Validamos primero:
        claveNueva = txtClave.getText().toString();
        repiteClave = txtRepiteClave.getText().toString();
        // Si alguno de los campos de la contraseña está vacío, no permitimos continuar
        if (claveNueva.isEmpty() || repiteClave.isEmpty()) {
            //Log.d("MiPerfilFragment", "Campos de clave vacíos");
            Toast.makeText(getActivity(), R.string.error_introduce_clave, Toast.LENGTH_SHORT).show();
        } else { // Los campos no están vacíos. Continuamos validando...
            
            // Si la clave introducida es menor a 8 caracteres, no permitimos continuar
            // (no validamos la longitud máxima porque ya hemos definido el campo para que solo acepte
            // hasta 20 caracteres)
            if (claveNueva.length() < 8) {
                //Log.d("MiPerfilFragment", "Longitud de clave inferior a la permitida");
                Toast.makeText(getActivity(), R.string.error_longitud_clave, Toast.LENGTH_LONG).show();
            } else { // La longitud de clave es correcta. Continuamos validando...

                // Si los caracteres no son los aceptados por el patrón, no permitimos continuar
                if (!claveNueva.matches(pattern_formato)) {
                    //TODO: Quitar este mensaje de error de string y ponerlo directamente aquí, porque no saca el mensaje
                    //Log.d("MiPerfilFragment", "Formato de clave no válido");
                    Toast.makeText(getActivity(), R.string.error_formato_clave, Toast.LENGTH_LONG).show();
                } else { // La clave tiene un formato correcto. Continuamos validando...
                    // Si la clave no repite con la repetida no permitimos continuar
                    if (!claveNueva.equals(repiteClave)) {
                        //Log.d("MiPerfilFragment", "Las claves no coinciden");
                        Toast.makeText(getActivity(), R.string.error_claves_no_coinciden, Toast.LENGTH_SHORT).show();
                        System.out.println("CLAVES: " + claveNueva + repiteClave);
                        txtClave.setText(""); // Borramos los campos de clave
                        txtRepiteClave.setText("");
                    } else { // Las dos claves introducidas coindicen. Terminamos las validaciones.

                        // Preguntamos al usuario antes de hacer nada si está seguro de efectuar los cambios
                        // mediante un cuadro de diálogo
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.title_dialog_actualizar_clave_mi_perfil); // titulo del diálogo
                        builder.setMessage(R.string.txt_dialog_actualizar_clave_mi_perfil)
                                // Si pulsa el botón de cambiar, se procede con la actualización
                                .setPositiveButton(R.string.btn_cambiar, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        // Todas las validaciones se han pasado correctamente, ejecutamos la consulta
                                        // para actualizar la clave
                                        //Log.i("MiPerfilFragment", "Confirmar cambio de clave");
                                        request = new StringRequest(Request.Method.POST, url_consulta2,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        //SE EJECUTA CUANDO LA CONSULTA SALE BIEN
                                                        try {
                                                            System.out.println("CLAVE NUEVA DEL USUARIO: " + claveNueva);
                                                            //Log.d("MiPerfilFragment", "Clave actualizada correctamente.");
                                                            // todo: Mostrar algún mensaje de "Contraseña actualizada con éxito"
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            //Log.e("MiPerfilFragment", "Error al actualizar la clave");
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                                                        Toast.makeText(getActivity(), R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                                        //Log.d("MiPerfilFragment", "Error al conectar con el servidor para actualizar la clave de usuario");
                                                    }
                                                }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                                                Map<String, String> parametros = new HashMap<>();
                                                parametros.put("nUsuario", nombre_de_usuario);
                                                parametros.put("clave", claveNueva);
                                                return parametros;
                                            }

                                        };
                                        AppController.getInstance().addToRequestQueue(request);
                                    }
                                    // Si pulsa el botón cancelar, no se hace nada
                                })
                                .setNegativeButton(R.string.respuesta_dialog_no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                        //no hacemos nada, y al pulsar el botón simplemente se cerrará el diálogo
                                        //Log.i("MiPerfilFragment", "Cancelar cambio de clave");
                                    }
                                });
                        // Creamos diálogo y lo mostramos
                        Dialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        }
    }
}
