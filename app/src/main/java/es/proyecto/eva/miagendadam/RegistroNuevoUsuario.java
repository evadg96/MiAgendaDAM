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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Log;
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
    Spinner spinnerProvincia, spinnerFamiliasCiclo, spinnerCiclo;
    EditText txtNombre, txtApellidos, txtCentroEstudios, txtHorasFct, txtCentroPracticas, txtCorreo, txtNombreUsuario, txtClave, txtClave2;

//     private String url_consulta = "http://192.168.0.12/MiAgenda/inserta_nuevo_usuario.php";
//     private String url_consulta2 = "http://192.168.0.12/MiAgenda/clave_gmail.php";

//    private String url_consulta = "http://192.168.0.159/MiAgenda/inserta_nuevo_usuario.php";
//    private String url_consulta2 = "http://192.168.0.159/MiAgenda/clave_gmail.php";

    private String url_consulta = "http://miagendafp.000webhostapp.com/inserta_nuevo_usuario.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/clave_gmail.php";

    public String getFecha() {
        Date date = new Date();
        String fecha = date.toString();
        return fecha;
    }
    private String fecha_registro = getFecha();

    public static String correo = "";
    private static int codigoConfirmacion;
    private static String sCodigoConfirmacion;
    private static String nombre = "";
    private static String n_Usuario = "";
    private static String clave = "";
    private static String horas_fct = "";
    private String provincia = "";
    private String familiaCiclo = "";
    private String ciclo_formativo = "";
    private Session session;
    // Array de provincias
    private String[] provincias = {"Selecciona una provincia", "A Coruña", "Álava", "Albacete","Alicante","Almería","Asturias","Ávila","Badajoz","Islas Baleares",
            "Barcelona","Burgos","Cáceres","Cádiz","Cantabria","Castellón","Ciudad Real","Córdoba","Cuenca","Girona","Granada",
            "Guadalajara","Guipúzcoa","Huelva","Huesca","Jaén","La Rioja","Las Palmas","León","Lleida","Lugo","Madrid","Málaga",
            "Murcia","Navarra","Orense","Palencia","Pontevedra","Salamanca","Segovia","Sevilla","Soria","Tarragona","Santa Cruz de Tenerife",
            "Teruel","Toledo","Valencia","Valladolid","Vizcaya","Zamora","Zaragoza"};

    // Array de familias de ciclos formativos
    private String[] familias = {"Selecciona una familia de ciclos formativos", "Actividades físicas y deportivas", "Administración y gestión", "Agraria", "Artes gráficas", "Artes y artesanías",
            "Comercio y marketing", "Edificación y obra civil", "Electricidad y electrónica", "Energía y agua", "Fabricación mecánica", "Hostelería y turismo",
            "Imagen personal", "Imagen y sonido", "Industrias alimentarias", "Industrias extractivas", "Informática y comunicaciones", "Instalación y mantenimiento",
            "Madera, mueble y corcho", "Marítimo-pesquera", "Química", "Sanidad", "Seguridad y medio ambiente", "Servicios socioculturales y a la comunidad",
            "Textil, confección y piel", "Transporte y mantenimiento de vehículos", "Vidrio y cerámica"};

    // Array de ciclos formativos
    private String[] ciclos = {"Selecciona un ciclo formativo",
            "------------ Actividades físicas y deportivas ------------",
            "Actividades ecuestres", "Acondicionamiento físico", "Enseñanza y animación sociodeportiva",
            "---------------- Administración y gestión ----------------",
            "Informática de oficina (básico)", "Servicios administrativos (básico)", "Gestión administrativa", "Administración y finanzas", "Asistencia a la dirección",
            "----------------------------- Agraria ---------------------------------",
            "Actividades agropecuarias (básico)", "Agro-jardinería y composiciones florales (básico)",
            "Aprovechamientos forestales (básico)", "Actividades ecuestres", "Aprovechamiento y conservación del medio natural", "Jardinería y floristería",
            "Producción agroecológica", "Producción agropecuaria", "Ganadería y asistencia en sanidad animal", "Gestión florestal y del medio natural", "Paisajismo y medio rural",
            "------------------------ Artes gráficas ------------------------",
            "Artes gráficas (básico)", "Impresión gráfica", "Postimpresión y acabados gráficos", "Preimpresión digital", "Diseño y edición de publicaciones impresas y multimedia",
            "Diseño y gestión de la producción gráfica",
            "---------------------- Artes y artesanías ----------------------",
            "Artista fallero y construcción de escenografías",
            "------------------ Comercio y marketing ------------------",
            "Servicios comerciales (básico)", "Actividades comerciales", "Comercio internacional", "Gestión de ventas y espacios comerciales", "Marketing y publicidad", "Transporte y logística",
            "------------------ Edificación y obra civil ------------------",
            "Reforma y mantenimiento de edificios (básico)", "Construcción", "Obras de interior, decoración y rehabilitación", "Organización y control de obras y construcción",
            "Proyectos de edificación", "Proyectos de obra civil",
            "----------------- Electricidad y electrónica -----------------",
            "Electricidad y electrónica (básico)", "Fabricación de elementos metálicos (básico)", "Instalaciones electrotécnicas y mecánica (básico)", "Instalaciones eléctricas y automáticas", "Instalaciones de telecomunicaciones",
            "Automatización y robótica industrial", "Electromedicina clínica", "Mantenimiento electrónico", "Sistemas electrotécnicos y automatizados",
            "Sistemas de telecomunicaciones e informáticos",
            "----------------------- Energía y agua ------------------------",
            "Redes y estaciones de tratamiento de aguas", "Centrales eléctricas",
            "Eficiencia enegrética y energía solar térmica", "Energías renovables", "Gestión del agua",
            "----------------- Fabricación y mecánica -----------------",
            "Fabricación de elementos metálicos (básico)",
            "Fabricación y montaje (básico)", "Instalaciones electrotécnicas y mecánica (básico)", "Conformado por moldeo e metales y polímeros",
            "Mecanizado", "Soldadura y calderería", "Construcciones metálicas", "Diseño en fabricación mecánica", "Programación de la producción en fabricación mecánica",
            "Programación de la producción en moldeo de metales y polímeros",
            "------------------ Hostelería y turismo --------------------",
            "Actividades de panadería y pastelería (básico)", "Alojamiento y lavandería (básico)",
            "Cocina y restauración (básico)", "Cocina y gastronomía", "Servicios en restauración", "Agencias de viajes y gestión de eventos",
            "Dirección de cocina", "Dirección de servicios de restauración", "Gestión de alojamientos turísticos", "Guía, información y asistencias turísticas",
            "---------------------- Imagen personal ------------------------",
            "Peluquería y estética (básico)", "Estética y belleza", "Peliquería y cosmética capilar", "Asesoría de imagen personal y corporativa",
            "Caracterización y maquillaje profesional", "Estilismo y dirección de peluquería", "Estética integral y bienestar",
            "----------------------- Imagen y sonido -----------------------",
            "Video disc-jockey y sonido", "Animaciones 3D, juegos y entornos interactivos", "Iluminación, captación y tratamiento de imagen", "Producción de audiovisuales y espectáculos",
            "Realización de audiovisuales y espectáculos", "Sonido para audiovisuales y espectáculos",
            "----------------- Industrias alimentarias ----------------",
            "Actividades de panadería y pastelería (básico)", "Industrias alimentarias (básico)", "Aceites de oliva y vinos", "Elaboración de productos alimenticios", "Panadería, repostería y confitería",
            "Procesos y calidad en la industria alimentaria", "Vitivinicultura",
            "------------------- Industrias extractivas -------------------",
            "Excavaciones y sondeos", "Piedra natural",
            "------------ Informática y comunicaciones ------------",
            "Informática de oficina (básico)",
            "Informática y comunicaciones (básico)", "Sistemas microinformáticos y redes", "Administración de sistemas informáticos en red",
            "Desarrollo de aplicaciones multiplataforma", "Desarrollo de aplicaciones web",
            "--------------- Instalación y mantenimiento --------------",
            "Fabricación y montaje (básico)", "Mantenimiento de viviendas (básico)",
            "Instalaciones frigorísficas y de climatización", "Instalaciones de producción de calor", "Mantenimiento electromecánico",
            "Desarrollo de proyectos de instalaciones térmicas y de fluidos", "Mantenimiento de instalaciones térmicas y de fluidos", "Mecatrónica industrial",
            "----------------- Madera, mueble y corcho ----------------",
            "Carpintería y mueble (básico)", "Carpintería y mueble (medio)", "Instalación y amueblamiento", "Diseño y amueblamiento",
            "------------------- Marítimo-pesquera --------------------",
            "Actividades marítimo-pesqueras (básico)",
            "Mantenimiento de embarcaciones deportivas y de recreo (básico)", "Cultivos acuícolas", "Mantenimiento y control de la maquinaria de buques y embarcaciones",
            "Navegación y pesca de litoral", "Operaciones subacuáticas e hiperbáricas", "Acuicultura", "Organización del mantenimiento de maquinaria de buques y embarcaciones",
            "Transporte marítimo y pesca de altura",
            "----------------------------- Química ------------------------------",
            "Operaciones de laboratorio", "Planta química", "Fabricación  de productos farmacéuticos, biotecnológicos y afines",
            "Laboratorio de análisis y control de calidad", "Química industrial",
            "---------------------------- Sanidad -----------------------------",
            "Emergencias sanitarias", "Farmacia y parafarmacia", "Anatomía patológica y citodiagnóstico",
            "Audiología protésica", "Documentación y administración sanitarias", "Higiene bucodental", "Imagen para el diagnóstico y medicina nuclear",
            "Laboratorio clínico y biomédico", "Ortoprótesis y productos de apoyo", "Prótesis dentales", "Radioterapia y dosimetría",
            "-------------- Seguridad y medio ambiente --------------",
            "Emergencias y protección civil",
            "Coordinación de emergencias y protección civil", "Educación y control ambiental",
            "----------------- Servicios socioculturales ----------------",
            "Actividades domésticas y limpieza de edificios (básico)",
            "Atención a personas en situación de dependencia", "Animación sociocultural y turística", "Educación infantil", "Integración social", "Mediación comunicativa",
            "Promoción de igualdad de género",
            "----------------- Textil, confección y piel ----------------",
            "Arreglo y reparación de artículos textiles y de piel (básico)", "Tapicería y cortinaje (básico)", "Calzado y complementos de moda", "Confección y moda", "Fabricación y ennoblecimiento de productos textiles", "Diseño técnico en textil y piel",
            "Diseño y producción de calzado y complementos", "Patronaje y moda", "Vestuario a medida y de espectáculos",
            "---- Transporte y mantenimiento de vehículos ----",
            "Mantenimiento de embarcaciones deportivas y de recreo (básico)", "Mantenimiento de vehículos (básico)", "Carrocería", "Conducción de vehículos de transporte por carretera", "Electromecánica de maquinaria", "Electromecánica de vehículos automóviles",
            "Mantenimiento de material rodante ferroviario", "Automoción",
            "--------------------- Vidrio y cerámica ------------------------",
            "Vidriería y alfarería (básico)", "Fabricación de productos cerámicos",
            "Desarrollo y fabricación de productos cerámicos"};

    private static final String pattern_email = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"; // declaramos patrón para validar el formato del correo electrónico introducido
    // por el usuario
    private String pattern_formato = "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z" // minúsculas
            + "|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z" // mayúsculas
            + "|0|1|2|3|4|5|6|7|8|9" // números
            + "|!|=|-|_|@|:|%|~|#|&)+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_nuevo_usuario);
        setTitle(R.string.title_activity_registro_usuario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // para VER la flecha atrás en el actionbar
        btnRegistro = (Button) findViewById(R.id.btn_registrarse);
        txtNombre = (EditText) findViewById(R.id.editText_nombre);
        txtApellidos = (EditText) findViewById(R.id.editText_ape_uno);
        spinnerProvincia = (Spinner) findViewById(R.id.spinner_provincia);
        // asociamos el array de provincias al spinner
        spinnerProvincia.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provincias));
        spinnerFamiliasCiclo= (Spinner) findViewById(R.id.spinner_familias_ciclo);
        // asociamos el array de familias de ciclos al spinner
        spinnerFamiliasCiclo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, familias));
        spinnerCiclo = (Spinner) findViewById(R.id.spinner_ciclo);
        spinnerCiclo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ciclos));
        txtCentroEstudios = (EditText) findViewById(R.id.editText_centro_estudios);
        txtHorasFct = (EditText) findViewById(R.id.editText_horas_fct);
        txtCentroPracticas = (EditText) findViewById(R.id.editText_centro_practicas);
        txtCorreo = (EditText) findViewById(R.id.editText_correo);
        txtNombreUsuario = (EditText) findViewById(R.id.editText_nombre_usuario);
        txtClave = (EditText) findViewById(R.id.editText_clave);
        txtClave2 = (EditText) findViewById(R.id.editText_confirma_clave);

        // controlamos la selección del spinner y lo añadimos al String provincia
        spinnerProvincia.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        provincia = provincias[(position)];
                        Log.d("RegistroNuevoUsuario", "Provincia seleccionada: "+ provincia);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
        // lo mismo con el spinner de familias de ciclo
        spinnerFamiliasCiclo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        familiaCiclo = familias[(position)];
                        Log.d("RegistroNuevoUsuario", "Familia de ciclo seleccionada: "+ familiaCiclo);
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
                        ciclo_formativo = ciclos[(position)];
                        Log.d("RegistroNuevoUsuario", "Ciclo formativo seleccionado: "+ ciclo_formativo);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
    }

    // Al hacer click en el botón de registro...
    public void registro(View view) {
        // Parámetros que vamos a pasar a la consulta
        nombre = txtNombre.getText().toString();
        final String apellidos = txtApellidos.getText().toString();
        final String centro_estudios = txtCentroEstudios.getText().toString();
        horas_fct = txtHorasFct.getText().toString();
        final String centro_practicas = txtCentroPracticas.getText().toString();
        correo = txtCorreo.getText().toString();
        n_Usuario = txtNombreUsuario.getText().toString();
        clave = txtClave.getText().toString();
        final String clave2 = txtClave2.getText().toString();
        fecha_registro = getFecha();
        System.out.println("FECHA REGISTRO: " + fecha_registro);

        // validamos si alguno de los campos está vacío, para no dejarle seguir al usuario.
        if (nombre.isEmpty() || apellidos.isEmpty() || provincia.equals("Selecciona una provincia") || centro_estudios.isEmpty() || familiaCiclo.equals("Selecciona una familia de ciclos formativos") ||
                ciclo_formativo.equals("Selecciona un ciclo formativo") || horas_fct.isEmpty() || centro_practicas.isEmpty() || correo.isEmpty() || n_Usuario.isEmpty() || clave.isEmpty() || clave2.isEmpty()) { // validamos que no haya ningún campo en blanco
            Toast.makeText(RegistroNuevoUsuario.this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            Log.i("RegistroNuevoUsuario", "Campos vacíos");
        } else {
            if (Integer.valueOf(horas_fct) > 700) {
                Toast.makeText(RegistroNuevoUsuario.this, R.string.error_horas_practicas, Toast.LENGTH_LONG).show();
                Log.i("RegistroNuevoUsuario", "Horas FCT por encima de lo permitido");
            } else {
                Pattern pattern = Pattern.compile(pattern_email); // creamos el patrón asignándole el formato declarado arriba para el correo electrónico
                Matcher matcher = pattern.matcher(correo); // le indicamos que queremos que aplique el patrón al correo
                if (!matcher.matches()) { // si el correo no cumple con el formato del patrón, salta el mensaje de error
                    Toast.makeText(RegistroNuevoUsuario.this, R.string.error_correo_no_valido, Toast.LENGTH_SHORT).show();
                    Log.i("RegistroNuevoUsuario", "Formato de correo no válido");
                } else {
                    if (n_Usuario.length() < 6) {
                        Toast.makeText(RegistroNuevoUsuario.this, R.string.error_longitud_usuario, Toast.LENGTH_LONG).show();
                        Log.i("RegistroNuevoUsuario", "Longitud de nombre de usuario inferior a la necesaria");
                    } else {
                        if (clave.length() < 8) {
                            Toast.makeText(RegistroNuevoUsuario.this, R.string.error_longitud_clave, Toast.LENGTH_LONG).show();
                            Log.i("RegistroNuevoUsuario", "Longitud de clave inferior a la necesaria");
                        } else { // VALIDAMOS CARACTERES ACEPTADOS PARA LA CLAVE:
                            if (!clave.matches(pattern_formato) || !n_Usuario.matches(pattern_formato)) { // si la clave o el nombre de usuario no cumplen con el formato del patrón
                                Toast.makeText(RegistroNuevoUsuario.this, R.string.error_formato_usuario_clave +
                                        " : % ~ # &", Toast.LENGTH_LONG).show();
                                Log.i("RegistroNuevoUsuario", "Formato de clave o nombre de usuario no válido");
                            } else {
                                if (!clave.equals(clave2)) {
                                    Toast.makeText(RegistroNuevoUsuario.this, R.string.error_claves_no_coinciden, Toast.LENGTH_SHORT).show();
                                    Log.i("RegistroNuevoUsuario", "Las claves no coinciden");
                                    txtClave.setText(""); // Borramos los campos de clave
                                    txtClave2.setText("");
                                } else {
                                    System.out.println("DATOS USUARIO A REGISTRAR: " + "\n" + nombre + "\n" + apellidos + "\n" + provincia + "\n"
                                            + centro_estudios + "\n" + ciclo_formativo + "\n" + horas_fct + "\n" + centro_practicas + "\n" + correo + "\n" + n_Usuario + "\n" + clave + "\n" + clave2);
                                    // INICIAMOS CONEXIÓN CON VOLLEY
                                    StringRequest request = new StringRequest(Request.Method.POST, url_consulta,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    System.out.println("CONEXIÓN INICIADA!");
                                                    if (response.equals("1")) {
                                                        try {
                                                            Log.d("RegistroNuevoUsuario", "Ya existe un usuario con ese correo");
                                                            Toast.makeText(RegistroNuevoUsuario.this, R.string.error_correo_ya_existe, Toast.LENGTH_SHORT).show();
                                                            System.out.println("ERROR: Correo ya registrado.");
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        if (response.equals("2")) {
                                                            try {
                                                                Log.d("RegistroNuevoUsuario", "Ya existe un usuario con ese nombre");
                                                                Toast.makeText(RegistroNuevoUsuario.this, R.string.error_usuario_ya_existe, Toast.LENGTH_LONG).show();
                                                                System.out.println("ERROR: Usuario ya existe.");
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            if (response.equals("0")) { // datos y registro correcto
                                                                try {
                                                                    Log.d("RegistroNuevoUsuario", "Usuario creado correctamente");
                                                                    enviarCorreoConfirmacion();
                                                                    // Creamos alerta de confirmación  para decir que se ha creado correctamente
                                                                    // y mandamos a la pantalla de confirmación de usuario
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistroNuevoUsuario.this);
                                                                    builder.setTitle(R.string.title_dialog_registro_correcto); // titulo del diálogo
                                                                    builder.setMessage(R.string.text_dialog_registro_correcto)
                                                                            .setPositiveButton(R.string.btn_aceptar_dialog, new DialogInterface.OnClickListener() {
                                                                                public void onClick(DialogInterface dialog, int id) {
                                                                                    // mandamos a la pantalla de confirmación de registro
                                                                                    Intent intent = new Intent(RegistroNuevoUsuario.this, ConfirmaRegistro.class);
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
                                                                    Log.e("RegistroNuevoUsuario", "Error al intentar enviar el correo con el código de confirmación");
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toast.makeText(RegistroNuevoUsuario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                                                    Log.e("RegistroNuevoUsuario", "Error al conectar con el servidor para crear el nuevo usuario");
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
                                            parametros.put("centro_estudios", centro_estudios);
                                            parametros.put("familia_ciclo", familiaCiclo);
                                            parametros.put("ciclo_formativo", ciclo_formativo);
                                            parametros.put("horas_fct", horas_fct);
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
    }


    /***********************************************************************************************
     * Método que genera el código de confirmación de registro que se le envía al usuario
     **********************************************************************************************/
    public void generaCodigoConfirmacion() {
        // generamos un código aleatorio de 6 dígitos
        codigoConfirmacion = (int) (Math.random() * 999999) + 1;
        sCodigoConfirmacion = Integer.toString(codigoConfirmacion); // pasamos el código a String para poder guardarlo como preferencia
        Log.d("RegistroNuevoUsuario", "Código de confirmación generado");
        guardarPreferencias(); // guardamos el dato
    }

    // guardamos como preferencia el código de confirmación
    public void guardarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("codigo_de_confirmacion", sCodigoConfirmacion);
        editor.commit();
        Log.d("RegistroNuevoUsuario", "Preferencias guardadas (código de confirmación)");
    }

    /***********************************************************************************************
     * Método que envía el correo con los datos correspondientes
     **********************************************************************************************/
    public void enviarCorreoConfirmacion() {
        Log.d("RegistroNuevoUsuario", "Enviamos el correo de confirmación");
        generaCodigoConfirmacion(); // generamos el código de confirmación que se le envía al usuario
        StringRequest request = new StringRequest(Request.Method.POST, url_consulta2,
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

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("RegistroNuevoUsuario", "Error al intentar enviar el correo");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistroNuevoUsuario.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
                        Log.e("RegistroNuevoUsuario", "Error al conectar con el servidor para obtener la clave del correo noreply...");
                    }
                });
        AppController.getInstance().addToRequestQueue(request);
    }

    // Clase con el contenido del correo electrónico que se enviará
    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("noreply.miagendafp@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
                message.setSubject("No-reply: Confirmación de registro");
                message.setContent("<p style=\"text-align:justify\">¡Hola " + nombre + "! Ya estás un paso más cerca de finalizar tu registro como usuario de <b>Mi agenda FP</b>, tan solo nos queda confirmar" +
                        "    tu cuenta introduciendo el código de confirmación que se indica aquí abajo.</p>" +
                        "       <p style=\"text-align:justify\"> Código de confirmación: <b>" + codigoConfirmacion + "</b></p> " +
                        "        <p style=\"text-align:justify\">Usuario: <b>" + n_Usuario + "</b></p>" +
                        "<div style=\"background-color:#EEEEEE; border:1px solid #BABABA; box-shadow: 2px 2px 5px #999; font-size:10px; text-align:justify\">" + // el sombreado no se ve en el móvil
                        "<p style=\"margin-left: 10px; margin-right: 11px\">" +
                        "Este mensaje se ha generado automáticamente. Por favor <b>no responda a este correo</b>, no recibirá ninguna respuesta.\n" +
                        "    <br/>Si tiene algún problema, duda o sugerencia, contacte con el soporte a través de la dirección de correo <b>soportemiagendafp@gmail.com</b>\n" +
                        "        <br/>Si ha recibido este correo por error, por favor, le rogamos que lo elimine y se ponga en contacto con la dirección de correo indicada arriba.\n" +
                        "        <br/>Atentamente, el equipo de <b>Mi agenda FP</b>.", "text/html; charset=utf-8");
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("RegistroNuevoUsuario", "Correo enviado");
        }
    }

    // Al pulsar hacia atrás volvemos un paso atrás en la aplicación
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i("RegistroNuevoUsuario", "Action Atrás");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
