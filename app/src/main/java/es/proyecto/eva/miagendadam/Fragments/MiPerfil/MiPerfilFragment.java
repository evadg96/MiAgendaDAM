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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import static es.proyecto.eva.miagendadam.NavMenu.familia_ciclo_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.ciclo_formativo_usuario;
import static es.proyecto.eva.miagendadam.NavMenu.provincia_del_usuario;

// TODO: Pendiente de depuración a fondo. En principio parece funcionar correctamente.
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
    boolean editando = false; // para controlar si se ha pulsado el botón de edición de datos del perfil
    private String claveNueva = "", repiteClave = "";
    // Patrón para controlar el formato de la contraseña nueva
    private String pattern_formato = "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z" // minúsculas
            + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z" // mayúsculas
            + "|0|1|2|3|4|5|6|7|8|9" // números
            + "|!|=|-|_|@|:|%|~|#)+";



    // Array de provincias
    private String[] provincias;

    // Array de familias de ciclos formativos
    private String[] familias;

    // Array de ciclos formativos
    private String[] ciclos;

    public MiPerfilFragment() {
        // Required empty public constructor
    }

    /***********************************************************************************************
     * Método que rellena los spinner para que se puedan ver los datos siempre bien actualizados
     **********************************************************************************************/
    public void rellenarSpinners(){
        provincias  = new String [] {provincia_del_usuario, "A Coruña", "Álava", "Albacete","Alicante","Almería","Asturias","Ávila","Badajoz","Islas Baleares",
                "Barcelona","Burgos","Cáceres","Cádiz","Cantabria","Castellón","Ciudad Real","Córdoba","Cuenca","Girona","Granada",
                "Guadalajara","Guipúzcoa","Huelva","Huesca","Jaén","La Rioja","Las Palmas","León","Lleida","Lugo","Madrid","Málaga",
                "Murcia","Navarra","Orense","Palencia","Pontevedra","Salamanca","Segovia","Sevilla","Soria","Tarragona","Santa Cruz de Tenerife",
                "Teruel","Toledo","Valencia","Valladolid","Vizcaya","Zamora","Zaragoza"};
        familias = new String [] {familia_ciclo_usuario, "Actividades físicas y deportivas", "Administración y gestión", "Agraria", "Artes gráficas", "Artes y artesanías",
                "Comercio y marketing", "Edificación y obra civil", "Electricidad y electrónica", "Energía y agua", "Fabricación mecánica", "Hostelería y turismo",
                "Imagen personal", "Imagen y sonido", "Industrias alimentarias", "Industrias extractivas", "Informática y comunicaciones", "Instalación y mantenimiento",
                "Madera, mueble y corcho", "Marítimo-pesquera", "Química", "Sanidad", "Seguridad y medio ambiente", "Servicios socioculturales y a la comunidad",
                "Textil, confección y piel", "Transporte y mantenimiento de vehículos", "Vidrio y cerámica"};
        ciclos = new String [] {ciclo_formativo_usuario,
                "  ACTIVIDADES FÍSICAS Y DEPORTIVAS  ",
                "Actividades ecuestres", "Acondicionamiento físico", "Enseñanza y animación sociodeportiva",
                "  ADMINISTRACIÓN Y GESTIÓN  ",
                "Informática de oficina (básico)", "Servicios administrativos (básico)", "Gestión administrativa", "Administración y finanzas", "Asistencia a la dirección",
                "  AGRARIA  ",
                "Actividades agropecuarias (básico)", "Agro-jardinería y composiciones florales (básico)",
                "Aprovechamientos forestales (básico)", "Actividades ecuestres", "Aprovechamiento y conservación del medio natural", "Jardinería y floristería",
                "Producción agroecológica", "Producción agropecuaria", "Ganadería y asistencia en sanidad animal", "Gestión florestal y del medio natural", "Paisajismo y medio rural",
                "  ARTES GRÁFICAS  ",
                "Artes gráficas (básico)", "Impresión gráfica", "Postimpresión y acabados gráficos", "Preimpresión digital", "Diseño y edición de publicaciones impresas y multimedia",
                "Diseño y gestión de la producción gráfica",
                "  ARTES Y ARTESANÍAS  ",
                "Artista fallero y construcción de escenografías",
                "  COMERCIO Y MARKETING  ",
                "Servicios comerciales (básico)", "Actividades comerciales", "Comercio internacional", "Gestión de ventas y espacios comerciales", "Marketing y publicidad", "Transporte y logística",
                "  EDIFICACIÓN Y OBRA CIVIL  ",
                "Reforma y mantenimiento de edificios (básico)", "Construcción", "Obras de interior, decoración y rehabilitación", "Organización y control de obras y construcción",
                "Proyectos de edificación", "Proyectos de obra civil",
                "  ELECTRICIDAD Y ELECTRÓNICA  ",
                "Electricidad y electrónica (básico)", "Fabricación de elementos metálicos (básico)", "Instalaciones electrotécnicas y mecánica (básico)", "Instalaciones eléctricas y automáticas", "Instalaciones de telecomunicaciones",
                "Automatización y robótica industrial", "Electromedicina clínica", "Mantenimiento electrónico", "Sistemas electrotécnicos y automatizados",
                "Sistemas de telecomunicaciones e informáticos",
                "  ENERGÍA Y AGUA  ",
                "Redes y estaciones de tratamiento de aguas", "Centrales eléctricas",
                "Eficiencia enegrética y energía solar térmica", "Energías renovables", "Gestión del agua",
                "  FABRICACIÓN Y MECÁNICA  ",
                "Fabricación de elementos metálicos (básico)",
                "Fabricación y montaje (básico)", "Instalaciones electrotécnicas y mecánica (básico)", "Conformado por moldeo de metales y polímeros",
                "Mecanizado", "Soldadura y calderería", "Construcciones metálicas", "Diseño en fabricación mecánica", "Programación de la producción en fabricación mecánica",
                "Programación de la producción en moldeo de metales y polímeros",
                "  HOSTELERÍA Y TURISMO  ",
                "Actividades de panadería y pastelería (básico)", "Alojamiento y lavandería (básico)",
                "Cocina y restauración (básico)", "Cocina y gastronomía", "Servicios en restauración", "Agencias de viajes y gestión de eventos",
                "Dirección de cocina", "Dirección de servicios de restauración", "Gestión de alojamientos turísticos", "Guía, información y asistencias turísticas",
                "  IMAGEN PERSONAL  ",
                "Peluquería y estética (básico)", "Estética y belleza", "Peliquería y cosmética capilar", "Asesoría de imagen personal y corporativa",
                "Caracterización y maquillaje profesional", "Estilismo y dirección de peluquería", "Estética integral y bienestar",
                "  IMAGEN Y SONIDO  ",
                "Video disc-jockey y sonido", "Animaciones 3D, juegos y entornos interactivos", "Iluminación, captación y tratamiento de imagen", "Producción de audiovisuales y espectáculos",
                "Realización de audiovisuales y espectáculos", "Sonido para audiovisuales y espectáculos",
                "  INDUSTRIAS ALIMENTARIAS  ",
                "Actividades de panadería y pastelería (básico)", "Industrias alimentarias (básico)", "Aceites de oliva y vinos", "Elaboración de productos alimenticios", "Panadería, repostería y confitería",
                "Procesos y calidad en la industria alimentaria", "Vitivinicultura",
                "  INDUSTRIAS EXTRACTIVAS  ",
                "Excavaciones y sondeos", "Piedra natural",
                "  INFORMÁTICA Y COMUNICACIONES  ",
                "Informática de oficina (básico)",
                "Informática y comunicaciones (básico)", "Sistemas microinformáticos y redes", "Administración de sistemas informáticos en red",
                "Desarrollo de aplicaciones multiplataforma", "Desarrollo de aplicaciones web",
                "  INSTALACIÓN Y MANTENIMIENTO  ",
                "Fabricación y montaje (básico)", "Mantenimiento de viviendas (básico)",
                "Instalaciones frigorísficas y de climatización", "Instalaciones de producción de calor", "Mantenimiento electromecánico",
                "Desarrollo de proyectos de instalaciones térmicas y de fluidos", "Mantenimiento de instalaciones térmicas y de fluidos", "Mecatrónica industrial",
                "  MADERA, MUEBLE Y CORCHO  ",
                "Carpintería y mueble (básico)", "Carpintería y mueble (medio)", "Instalación y amueblamiento", "Diseño y amueblamiento",
                "  MARÍTIMO-PESQUERA  ",
                "Actividades marítimo-pesqueras (básico)",
                "Mantenimiento de embarcaciones deportivas y de recreo (básico)", "Cultivos acuícolas", "Mantenimiento y control de la maquinaria de buques y embarcaciones",
                "Navegación y pesca de litoral", "Operaciones subacuáticas e hiperbáricas", "Acuicultura", "Organización del mantenimiento de maquinaria de buques y embarcaciones",
                "Transporte marítimo y pesca de altura",
                "  QUÍMICA  ",
                "Operaciones de laboratorio", "Planta química", "Fabricación  de productos farmacéuticos, biotecnológicos y afines",
                "Laboratorio de análisis y control de calidad", "Química industrial",
                "  SANIDAD  ",
                "Emergencias sanitarias", "Farmacia y parafarmacia", "Anatomía patológica y citodiagnóstico",
                "Audiología protésica", "Documentación y administración sanitarias", "Higiene bucodental", "Imagen para el diagnóstico y medicina nuclear",
                "Laboratorio clínico y biomédico", "Ortoprótesis y productos de apoyo", "Prótesis dentales", "Radioterapia y dosimetría",
                "  SEGURIDAD Y MEDIO AMBIENTE  ",
                "Emergencias y protección civil",
                "Coordinación de emergencias y protección civil", "Educación y control ambiental",
                "  SERVICIOS SOCIOCULTURALES  ",
                "Actividades domésticas y limpieza de edificios (básico)",
                "Atención a personas en situación de dependencia", "Animación sociocultural y turística", "Educación infantil", "Integración social", "Mediación comunicativa",
                "Promoción de igualdad de género",
                "  TEXTIL, CONFECCIÓN Y PIEL  ",
                "Arreglo y reparación de artículos textiles y de piel (básico)", "Tapicería y cortinaje (básico)", "Calzado y complementos de moda", "Confección y moda", "Fabricación y ennoblecimiento de productos textiles", "Diseño técnico en textil y piel",
                "Diseño y producción de calzado y complementos", "Patronaje y moda", "Vestuario a medida y de espectáculos",
                "  TRANSPORTE Y MANTENIMIENTO DE VEHÍCULOS  ",
                "Mantenimiento de embarcaciones deportivas y de recreo (básico)", "Mantenimiento de vehículos (básico)", "Carrocería", "Conducción de vehículos de transporte por carretera", "Electromecánica de maquinaria", "Electromecánica de vehículos automóviles",
                "Mantenimiento de material rodante ferroviario", "Automoción",
                "  VIDRIO Y CERÁMICA  ",
                "Vidriería y alfarería (básico)", "Fabricación de productos cerámicos",
                "Desarrollo y fabricación de productos cerámicos"};
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
            case R.id.menu_editar_datos_perfil: // Opción de abilitar la edición de datos
                //Log.i("MiPerfilFragment", "Action Actualizar datos de usuario");
                editarDatos();
                return true;
            case R.id.menu_cancelar_edicion_perfil: // Opción de cancelar la edición de datos
                //Log.i("MiPerfilFragment", "Action Actualizar datos de usuario");
                cancelarEdicion();
                getActivity().invalidateOptionsMenu();
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
        cancelarEdicion();
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

    // TODO: DEPURAR
    /******************************************************************************************************
     * Método que actualiza las horas del módulo fct de las preferencias. Solo se ejecuta si se actualizan
     * los datos del perfil del usuario
     ***********************************************************************************************/
    private void guardarPreferencias() {
        SharedPreferences preferences = getActivity().getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("horas_fct", horas_fct_usuario);
        editor.putString("familia_ciclo", familia_ciclo_usuario);
        editor.commit();
    }

    /***********************************************************************************************
     * Método que deshabilita la edición de los campos de cualquier manera
     **********************************************************************************************/
    public void cancelarEdicion(){
        editando = false;
        txtNombre.setEnabled(false);
        txtApellidos.setEnabled(false);
        txtCentroPracticas.setEnabled(false);
        txtCentroEstudios.setEnabled(false);
        txtHorasFCT.setEnabled(false);
        spinnerFamiliaCiclo.setEnabled(false);
        spinnerCiclo.setEnabled(false);
        spinnerProvincia.setEnabled(false);
    }

    /***********************************************************************************************
     * Método que rellena los campos con los datos del usuario obtenidos
     **********************************************************************************************/
    public void rellenarCampos(){
        try {

            // primero validamos que no haya habido errores al obtener los datos del usuario y los campos no estén vacíos:
            if (nombre_del_estudiante.isEmpty() || apellidos_del_usuario.isEmpty() || centro_estudios_usuario.isEmpty() || centro_practicas_usuario.isEmpty() || horas_fct_usuario.isEmpty()){
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        R.string.error_obtener_datos_usuario, Snackbar.LENGTH_LONG).show();
            } else {
                rellenarSpinners();
                // asignamos los adaptadores de los spinner con los datos de cada array
                spinnerFamiliaCiclo.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, familias));
                spinnerProvincia.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, provincias));
                spinnerCiclo.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ciclos));
                // ponemos los textos correspondientes en cada campo
                tvNombreSaludo.setText(" " + nombre_del_estudiante);
                txtNombre.setText(nombre_del_estudiante);
                txtApellidos.setText(apellidos_del_usuario);
                txtCentroEstudios.setText(centro_estudios_usuario);
                txtCentroPracticas.setText(centro_practicas_usuario);
                txtHorasFCT.setText(horas_fct_usuario);
                tvNombreUsuario.setText(nombre_de_usuario);
                tvCorreo.setText(correo_usuario);
                spinnerProvincia.setPrompt(provincia_del_usuario);
                spinnerCiclo.setPrompt(ciclo_formativo_usuario);
                spinnerFamiliaCiclo.setPrompt(familia_ciclo_usuario);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // TODO 2: falta por añadir algunas validaciones de formato más: en registro de nuevo usuario, para los apellidos. En contactos, para el módulo y el correo ...?
    /***********************************************************************************************
     * Método que habilita la edición de los campos de datos del perfil del usuario
     **********************************************************************************************/
    public void editarDatos(){
       editando = true;
       getActivity().invalidateOptionsMenu(); // para llamar de nuevo al onCreateOptionsMenu y ocultar el botón de editar
        // y mostrar el de guardar
        txtNombre.setEnabled(true);
        txtApellidos.setEnabled(true);
        txtCentroPracticas.setEnabled(true);
        txtCentroEstudios.setEnabled(true);
        txtHorasFCT.setEnabled(true);
        spinnerFamiliaCiclo.setEnabled(true);
        spinnerProvincia.setEnabled(true);
        spinnerCiclo.setEnabled(true);
        // controlamos la selección del spinner y lo añadimos al String provincia
        spinnerProvincia.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        provincia_del_usuario = provincias[(position)];
                        // Log.d("RegistroNuevoUsuario", "Provincia seleccionada: "+ provincia);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
        // obtenemos la familia de ciclo seleccionada
        spinnerFamiliaCiclo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        familia_ciclo_usuario = familias[(position)];
                        //  Log.d("RegistroNuevoUsuario", "Familia de ciclo seleccionada: "+ familiaCiclo);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
        // lo mismo con el spinner de familias de ciclo
        spinnerCiclo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        ciclo_formativo_usuario = ciclos[(position)];
                        // Log.d("RegistroNuevoUsuario", "Ciclo formativo seleccionado: "+ ciclo_formativo);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );

    }

    /***********************************************************************************************
     * Método que actualiza los datos del usuario desde el perfil
     **********************************************************************************************/
    public void actualizarDatosUsuario(){
        // Actualizamos el valor de los datos con lo introducido en los campos
        nombre_del_estudiante = txtNombre.getText().toString();
        apellidos_del_usuario = txtApellidos.getText().toString();
        centro_practicas_usuario = txtCentroPracticas.getText().toString();
        centro_estudios_usuario = txtCentroEstudios.getText().toString();
        horas_fct_usuario = txtHorasFCT.getText().toString();

        // validamos si alguno de los campos está vacío, para no dejarle seguir al usuario.
        // si alguno de los campos está vacío, no continuamos
        if (nombre_del_estudiante.isEmpty() || apellidos_del_usuario.isEmpty() || centro_practicas_usuario.isEmpty() || centro_estudios_usuario.isEmpty() || horas_fct_usuario.isEmpty() ||
                // tampoco permitimos la selección de las familias a las que pertenecen los ciclos, puesto que son títulos para organizar los ciclos que contiene cada familia
                ciclo_formativo_usuario.equals("  ACTIVIDADES FÍSICAS Y DEPORTIVAS  ")||
                ciclo_formativo_usuario.equals("  ADMINISTRACIÓN Y GESTIÓN  ") ||
                ciclo_formativo_usuario.equals("  AGRARIA  ") ||
                ciclo_formativo_usuario.equals("  ARTES GRÁFICAS  ") ||
                ciclo_formativo_usuario.equals("  ARTES Y ARTESANÍAS  ") ||
                ciclo_formativo_usuario.equals("  COMERCIO Y MARKETING  ") ||
                ciclo_formativo_usuario.equals("  EDIFICACIÓN Y OBRA CIVIL  ") ||
                ciclo_formativo_usuario.equals("  ELECTRICIDAD Y ELECTRÓNICA  ") ||
                ciclo_formativo_usuario.equals("  ENERGÍA Y AGUA  ") ||
                ciclo_formativo_usuario.equals("  FABRICACIÓN Y MECÁNICA  ") ||
                ciclo_formativo_usuario.equals("  HOSTELERÍA Y TURISMO  ") ||
                ciclo_formativo_usuario.equals("  IMAGEN PERSONAL  ") ||
                ciclo_formativo_usuario.equals("  IMAGEN Y SONIDO  ") ||
                ciclo_formativo_usuario.equals("  INDUSTRIAS ALIMENTARIAS  ") ||
                ciclo_formativo_usuario.equals("  INDUSTRIAS EXTRACTIVAS  ") ||
                ciclo_formativo_usuario.equals("  INFORMÁTICA Y COMUNICACIONES  ") ||
                ciclo_formativo_usuario.equals("  INSTALACIÓN Y MANTENIMIENTO  ") ||
                ciclo_formativo_usuario.equals("  MADERA, MUEBLE Y CORCHO  ") ||
                ciclo_formativo_usuario.equals("  MARÍTIMO-PESQUERA  ") ||
                ciclo_formativo_usuario.equals("  QUÍMICA  ") ||
                ciclo_formativo_usuario.equals("  SANIDAD  ") ||
                ciclo_formativo_usuario.equals("  SEGURIDAD Y MEDIO AMBIENTE  ") ||
                ciclo_formativo_usuario.equals("  SERVICIOS SOCIOCULTURALES  ") ||
                ciclo_formativo_usuario.equals("  TEXTIL, CONFECCIÓN Y PIEL  ") ||
                ciclo_formativo_usuario.equals("  TRANSPORTE Y MANTENIMIENTO DE VEHÍCULOS  ") ||
                ciclo_formativo_usuario.equals("  VIDRIO Y CERÁMICA  ")){
             Toast.makeText(getActivity(), R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            // Con los botones en la barra de acción no saca los snackbar
            //Snackbar.make(getActivity().findViewById(android.R.id.content),
              //      R.string.error_campos_vacios, Snackbar.LENGTH_LONG).show();

        } else {
            // consulta volley para actualizar los datos del usuario
            request = new StringRequest(Request.Method.POST, url_consulta,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("1")) {
                                Toast.makeText(getActivity(), R.string.perfil_actualizado, Toast.LENGTH_SHORT).show();
                                // Snackbar.make(getActivity().findViewById(android.R.id.content),
                                //       R.string.perfil_actualizado, Snackbar.LENGTH_LONG).show();
                                //Log.d("VerYEditarRegistroDiario", "Registro actualizado");
                                System.out.println("DATOS ACTUALIZADOS");
                                guardarPreferencias(); // Actualizamos las horas del módulo fct en preferencias TODO: DEPURAR
                                editando = false; // deshabilitamos la edición de campos de nuevo
                                cancelarEdicion();
                                getActivity().invalidateOptionsMenu(); // llamamos otra vez para quitar el icono de guardado una vez que se ha guardado correctamente
                                // actualizamos los datos a visualizar automáticamente, para evitar que el usuario tenga que hacerlo manualmente
                                rellenarCampos();
                            } else {
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        "Error al actualizar los datos del usuario.", Snackbar.LENGTH_LONG).show();
                            }
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
                    parametros.put("nombre", nombre_del_estudiante);
                    parametros.put("apellidos", apellidos_del_usuario);
                    parametros.put("provincia", provincia_del_usuario);
                    parametros.put("horas_fct", horas_fct_usuario);
                    parametros.put("centro_estudios", centro_estudios_usuario);
                    parametros.put("familia_ciclo", familia_ciclo_usuario);
                    parametros.put("ciclo_formativo", ciclo_formativo_usuario);
                    parametros.put("centro_practicas", centro_practicas_usuario);
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
                                                            Toast.makeText(getActivity(), R.string.clave_actualizada, Toast.LENGTH_SHORT).show();
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
