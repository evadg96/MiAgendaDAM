package es.proyecto.eva.miagendadam.Fragments.MiPerfil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.R;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

import static es.proyecto.eva.miagendadam.NavMenu.nombre_del_estudiante;
import static es.proyecto.eva.miagendadam.NavMenu.apellidos_del_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.centro_estudios_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.centro_practicas_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.horas_fct_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.familia_ciclo_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.ciclo_formativo_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.provincia_del_usuario;

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
    private String url_consulta3 = "http://miagendafp.000webhostapp.com/consulta_recuperar_datos_perfil_usuario.php";
    private JSONArray jsonArray;
    private String correo_usuario = "", nombre_de_usuario = "";
    boolean editando = false; // para controlar si se ha pulsado el botón de edición de datos del perfil
    private String claveNueva = "", repiteClave = "";
    // Patrón para controlar el formato de la contraseña nueva
    private String pattern_formato = "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z" // minúsculas
            + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z" // mayúsculas
            + "|0|1|2|3|4|5|6|7|8|9" // números
            + "|!|=|-|_|@|:|%|~|#)+";

    /***********************************************************************************************
     * Método que codifica un dato que se le pase por parámetro para visualizar sus tildes y otros
     * caracteres especiales
     * @param dato
     * @return
     **********************************************************************************************/
    private String codificaString(String dato){
        String datoCodificado = "";
        try {
            byte[] arrByteNombre = dato.getBytes("ISO-8859-1");
            datoCodificado = new String(arrByteNombre);
        } catch (Exception e){
            e.printStackTrace();
        }
        return datoCodificado;
    }

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
        if (editando) { // si estamos en modo edición, habilitamos el icono de guardado y ocultamos los de editar y refrescar
            menu.findItem(R.id.menu_actualizar_perfil).setVisible(true);
            menu.findItem(R.id.menu_cancelar_edicion_perfil).setVisible(true);
            menu.findItem(R.id.menu_editar_datos_perfil).setVisible(false);
            menu.findItem(R.id.menu_refrescar_datos).setVisible(false);
        }
    }

    // Selección de opciones del menú del action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_actualizar_perfil: // Opción de guardar los datos de usuario actualizados
                //Log.i("MiPerfilFragment", "Action Actualizar datos de usuario");
                actualizarDatosUsuario();
                return true;
            case R.id.menu_refrescar_datos: // Opción de refrescar los datos de usuario obtenidos de la bd y visibles en los campos
                //Log.i("MiPerfilFragment", "Action Actualizar datos de usuario");
                obtenerDatosUsuario();
                return true;
            case R.id.menu_editar_datos_perfil: // Opción de abilitar la edición de datos
                //Log.i("MiPerfilFragment", "Action Actualizar datos de usuario");
                editarDatos();
                return true;
            case R.id.menu_cancelar_edicion_perfil: // Opción de cancelar la edición de datos
                //Log.i("MiPerfilFragment", "Action Actualizar datos de usuario");
                cancelarEdicion();
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

    public void cancelarEdicion(){
        editando = false;
        txtNombre.setFocusableInTouchMode(false);
        txtApellidos.setFocusableInTouchMode(false);
        txtCentroPracticas.setFocusableInTouchMode(false);
        txtCentroEstudios.setFocusableInTouchMode(false);
        txtHorasFCT.setFocusableInTouchMode(false);
        getActivity().invalidateOptionsMenu();
    }

    /***********************************************************************************************
     * Método que obtiene de nuevo los datos del usuario para mostrarlos
     **********************************************************************************************/
    public void obtenerDatosUsuario(){
        request = new StringRequest(Request.Method.POST, url_consulta3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Log.d("MiPerfilFragment", "Obtenemos datos del usuario");
                            jsonArray = new JSONArray(response); // guardamos los registros en el array
                            nombre_del_estudiante = jsonArray.getJSONObject(0).getString("nombre");
                            apellidos_del_usuario = jsonArray.getJSONObject(0).getString("apellidos");
                          // TODO: Rellenar spinners con los datos correspondientes
                            provincia_del_usuario = jsonArray.getJSONObject(0).getString("provincia");
                            horas_fct_usuario = jsonArray.getJSONObject(0).getString("horas_fct");
                            centro_estudios_usuario = jsonArray.getJSONObject(0).getString("centro_estudios");
                            familia_ciclo_usuario = jsonArray.getJSONObject(0).getString("familia_ciclo");
                            ciclo_formativo_usuario = jsonArray.getJSONObject(0).getString("ciclo_formativo");
                            centro_practicas_usuario = jsonArray.getJSONObject(0).getString("centro_practicas");
                            rellenarCampos();
                             Toast.makeText(getActivity(), R.string.datos_refrescados, Toast.LENGTH_SHORT).show();
                           // Snackbar.make(getActivity().findViewById(android.R.id.content),
                             //       R.string.datos_refrescados, Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Log.e("MiPerfilFragment", "Error al obtener datos del usuario");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(NavMenu.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //Log.d("MiPerfilFragment", "Error al conectar con el servidor para obtener datos del usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_de_usuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }

    /***********************************************************************************************
     * Método que rellena los campos con los datos del usuario obtenidos
     **********************************************************************************************/
    public void rellenarCampos(){
        // Codificamos primero los campos que puedan llevar tildes
        try {
           // byte[] arrByteProvincia = descripcion_seleccionada.getBytes("ISO-8859-1");
            String nombreCodificado = codificaString(nombre_del_estudiante);
            String apellidosCodificados = codificaString(apellidos_del_usuario);
            String centroEstudiosCodificado = codificaString(centro_estudios_usuario);
            String centroPracticasCodificado = codificaString(centro_practicas_usuario);
            tvNombreSaludo.setText(" " + nombreCodificado);
            txtNombre.setText(nombreCodificado);
            txtApellidos.setText(apellidosCodificados);
            txtCentroEstudios.setText(centroEstudiosCodificado);
            txtCentroPracticas.setText(centroPracticasCodificado);
            txtHorasFCT.setText(horas_fct_usuario);
            tvNombreUsuario.setText(nombre_de_usuario);
            tvCorreo.setText(correo_usuario);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /***********************************************************************************************
     * Método que habilita la edición de los campos de datos del perfil del usuario
     **********************************************************************************************/
    public void editarDatos(){
       editando = true;
       getActivity().invalidateOptionsMenu(); // para llamar de nuevo al onCreateOptionsMenu y ocultar el botón de editar
        // y mostrar el de guardar
        txtNombre.setFocusableInTouchMode(true);
        txtApellidos.setFocusableInTouchMode(true);
        txtCentroPracticas.setFocusableInTouchMode(true);
        txtCentroEstudios.setFocusableInTouchMode(true);
        txtHorasFCT.setFocusableInTouchMode(true);
    }

    /***********************************************************************************************
     * Método que actualiza los datos del usuario desde el perfil
     **********************************************************************************************/
    public void actualizarDatosUsuario(){
        // Validamos campos:
        final String nombre_nuevo = txtNombre.getText().toString();
        final String apellidos_nuevos = txtApellidos.getText().toString();
        final String centro_practicas_nuevo = txtCentroPracticas.getText().toString();
        final String centro_estudios_nuevo = txtCentroEstudios.getText().toString();
        final String horas_fct_nuevas = txtHorasFCT.getText().toString();
        // si alguno de los campos está vacío, no continuamos
        if (nombre_nuevo.isEmpty() || apellidos_nuevos.isEmpty() || centro_practicas_nuevo.isEmpty() || centro_estudios_nuevo.isEmpty() || horas_fct_nuevas.isEmpty()){
             Toast.makeText(getActivity(), R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            // Con los botones en la barra de acción no saca los snackbar
            //Snackbar.make(getActivity().findViewById(android.R.id.content),
              //      R.string.error_campos_vacios, Snackbar.LENGTH_LONG).show();

        } else {
            editando = false; // deshabilitamos la edición de campos de nuevo
            txtNombre.setFocusableInTouchMode(false);
            txtApellidos.setFocusableInTouchMode(false);
            txtCentroPracticas.setFocusableInTouchMode(false);
            txtCentroEstudios.setFocusableInTouchMode(false);
            txtHorasFCT.setFocusableInTouchMode(false);
            // consulta volley para actualizar los datos del usuario
            request = new StringRequest(Request.Method.POST, url_consulta,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                             Toast.makeText(getActivity(), R.string.perfil_actualizado, Toast.LENGTH_SHORT).show();
                            // Snackbar.make(getActivity().findViewById(android.R.id.content),
                                 //       R.string.perfil_actualizado, Snackbar.LENGTH_LONG).show();
                                //Log.d("VerYEditarRegistroDiario", "Registro actualizado");
                                getActivity().invalidateOptionsMenu(); // llamamos otra vez para quitar el icono de guardado una vez que se ha guardado correctamente
                            System.out.println("DATOS ACTUALIZADOS");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                            //Log.e("VerYEditarRegistroDiario", "Error al conectar con el servidor para actualizar el registro");
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("nombre", nombre_nuevo);
                    parametros.put("apellidos", apellidos_nuevos);
                    parametros.put("provincia", provincia_del_usuario);
                    parametros.put("horas_fct", horas_fct_nuevas);
                    parametros.put("centro_estudios", centro_estudios_nuevo);
                    parametros.put("familia_ciclo", familia_ciclo_usuario);
                    parametros.put("ciclo_formativo", ciclo_formativo_usuario);
                    parametros.put("centro_practicas", centro_practicas_nuevo);
                    parametros.put("nUsuario", nombre_de_usuario);
                    return parametros;
                }
            };
            AppController.getInstance().addToRequestQueue(request);
        }
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
           // Toast.makeText(getActivity(), R.string.error_introduce_clave, Toast.LENGTH_SHORT).show();
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    R.string.error_introduce_clave, Snackbar.LENGTH_LONG).show();
        } else { // Los campos no están vacíos. Continuamos validando...
            
            // Si la clave introducida es menor a 8 caracteres, no permitimos continuar
            // (no validamos la longitud máxima porque ya hemos definido el campo para que solo acepte
            // hasta 20 caracteres)
            if (claveNueva.length() < 8) {
                //Log.d("MiPerfilFragment", "Longitud de clave inferior a la permitida");
                //Toast.makeText(getActivity(), R.string.error_longitud_clave, Toast.LENGTH_LONG).show();
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        R.string.error_longitud_clave, Snackbar.LENGTH_LONG).show();
            } else { // La longitud de clave es correcta. Continuamos validando...
                // Si los caracteres no son los aceptados por el patrón, no permitimos continuar
                if (!claveNueva.matches(pattern_formato)) {
                    //Log.d("MiPerfilFragment", "Formato de clave no válido");
                   // Toast.makeText(getActivity(), R.string.error_formato_clave, Toast.LENGTH_LONG).show();
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.error_formato_clave, Snackbar.LENGTH_LONG).show();
                } else { // La clave tiene un formato correcto. Continuamos validando...
                    // Si la clave no repite con la repetida no permitimos continuar
                    if (!claveNueva.equals(repiteClave)) {
                        //Log.d("MiPerfilFragment", "Las claves no coinciden");
                       // Toast.makeText(getActivity(), R.string.error_claves_no_coinciden, Toast.LENGTH_SHORT).show();
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.error_claves_no_coinciden, Snackbar.LENGTH_LONG).show();
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
                                                        //Toast.makeText(getActivity(), R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
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
