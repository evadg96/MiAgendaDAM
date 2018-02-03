package es.proyecto.eva.miagendadam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import es.proyecto.eva.miagendadam.Fragments.Diario.DiarioFragment;
import es.proyecto.eva.miagendadam.Fragments.Horas.HorasFragment;
import es.proyecto.eva.miagendadam.Fragments.Inicio.InicioFragment;
import es.proyecto.eva.miagendadam.Fragments.MiPerfil.MiPerfilFragment;
import es.proyecto.eva.miagendadam.VolleyController.AppController;


/***************************************************************************************************
 *  Menú lateral desplegable con las opciones de la aplicación.                                    *
 *  Será a su vez la clase contenedora del layout que contenga los fragments                       *
 *  de las distintas opciones de la app, que se mostrarán en función de la opción                  *
 *  seleccionada en el menú.                                                                       *
 **************************************************************************************************/

public class NavMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView nombreUsuario, correoUsuario, familiaCiclo;
    private String nombre_de_usuario;
    private String correo_de_usuario;
    private String familia_ciclo;
    private StringRequest request;

    // Datos del usuario que se mostrarán en la opción Mi perfil
    public static String nombre_del_estudiante = "", apellidos_del_usuario = "", provincia_del_usuario = "",
            centro_estudios_usuario = "", familia_ciclo_usuario = "", ciclo_formativo_usuario = "", centro_practicas_usuario = "",
            horas_fct_usuario = "";

    //    private String url_consulta = "http://192.168.0.12/MiAgenda/cerrar_sesion.php";
    private String url_consulta = "http://miagendafp.000webhostapp.com/cerrar_sesion.php";
//    private String url_consulta = "http://192.168.0.159/MiAgenda/cerrar_sesion.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/consulta_recuperar_datos_perfil_usuario.php";
    private JSONArray jsonArray;
    private android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Referenciamos al SharedPreferences que habíamos creado en la clase PantallaLogin
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        nombre_de_usuario = preferences.getString("nombre_de_usuario", "");
        correo_de_usuario = preferences.getString("correo_de_usuario", "");
        familia_ciclo = preferences.getString("familia_ciclo", "");
       // Log.d("NavMenu", "Nombre de usuario: "+ nombre_de_usuario);
        //Log.d("NavMenu", "Correo electrónico: "+ correo_de_usuario);
        //Log.d("NavMenu", "Familia ciclo: "+ familia_ciclo);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // Obtenemos los datos de usuario que se verán en el fragmento mi perfil para tenerlos nada
        // más abrirse la aplicación. Si no se pone aquí, y se pone, por ejemplo, al pulsar sobre la
        // opción Mi perfil, no se obtendrán los datos en la primera pulsación. Saldrán después siempre,
        // pero la primera vez nunca.
        obtenerDatosUsuario();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        // añadimos en el menú lateral el nombre y correo del usuario
        nombreUsuario = (TextView) headerView.findViewById(R.id.nombre_usuario_nav);
        nombreUsuario.setText(nombre_de_usuario);
        correoUsuario = (TextView) headerView.findViewById(R.id.correo_nav);
        correoUsuario.setText(correo_de_usuario);
        familiaCiclo = (TextView) headerView.findViewById(R.id.familia_ciclo_nav);
        familiaCiclo.setText(familia_ciclo);
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager.beginTransaction().replace(R.id.contenedor, new InicioFragment()).commit(); // abrimos por defecto el fragmento Diario
        setTitle(R.string.opc_inicio);
    }


    // Al pulsar el botón de Atrás
    @Override
    public void onBackPressed() {
        // Dejamos en blanco para que no se haga nada
    }

    /***********************************************************************************************
     *  Acciones a realizar con la selección de cada opción del menú lateral                       *
     **********************************************************************************************/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        // OPCIONES DEL MENÚ LATERAL:
        if (id == R.id.nav_inicio) {
            // Log.i("NavMenu", "Opción menú: Inicio");
            setTitle(R.string.opc_inicio);
            fragmentManager.beginTransaction().replace(R.id.contenedor, new InicioFragment()).commit();
        } else if (id == R.id.nav_diario) {
           // Log.i("NavMenu", "Opción menú: Diario");
            setTitle(R.string.opc_diario);
            fragmentManager.beginTransaction().replace(R.id.contenedor, new DiarioFragment()).commit();
        } else if (id == R.id.nav_horas) {
           // Log.i("NavMenu", "Opción menú: Horas");
            setTitle(R.string.opc_horas);
            fragmentManager.beginTransaction().replace(R.id.contenedor, new HorasFragment()).commit();
        } else if (id == R.id.nav_c_reco) {
          //  Log.i("NavMenu", "Opción menú: Contenido recomendado");
            setTitle(R.string.opc_mat_reco);
        } else if (id == R.id.nav_c_perso) {
           // Log.i("NavMenu", "Opción menú: Contenido personalizado");
            setTitle(R.string.opc_mat_perso);
        } else if (id == R.id.nav_festivos) {
           // Log.i("NavMenu", "Opción menú: Festivos/No lectivos");
            setTitle(R.string.opc_festivos_no_lectivos);
        } else if (id == R.id.nav_ayuda) {
           // Log.i("NavMenu", "Opción menú: Acerca de");
            Intent intent = new Intent (this, AcercaDe.class);
            startActivity(intent);
        } else if (id == R.id.nav_cerrar_sesion) {
           // Log.i("NavMenu", "Opción menú: Cerrar sesión");
            cerrarSesion();
        } else if (id == R.id.nav_anotaciones) {
           // Log.i("NavMenu", "Opción menú: Anotaciones");
            setTitle(R.string.opc_anotaciones);
        } else if (id == R.id.nav_tutores) {
           // Log.i("NavMenu", "Opción menú: Tutores");
            setTitle(R.string.opc_tutores);
        } else if (id == R.id.nav_proyecto) {
          //  Log.i("NavMenu", "Opción menú: Anteproyecto");
        } else if (id == R.id.nav_perfil) {
          //  Log.i("NavMenu", "Opción menú: Mi perfil");
            setTitle(R.string.opc_perfil);
            fragmentManager.beginTransaction().replace(R.id.contenedor, new MiPerfilFragment()).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /***********************************************************************************************
     * Método que obtiene los datos del usuario para mostrarlos
     **********************************************************************************************/
    public void obtenerDatosUsuario(){
        request = new StringRequest(Request.Method.POST, url_consulta2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Log.d("MiPerfilFragment", "Obtenemos datos del usuario");
                            jsonArray = new JSONArray(response); // guardamos los registros en el array
                            nombre_del_estudiante = jsonArray.getJSONObject(0).getString("nombre");
                            apellidos_del_usuario = jsonArray.getJSONObject(0).getString("apellidos");
                            provincia_del_usuario = jsonArray.getJSONObject(0).getString("provincia");
                            horas_fct_usuario = jsonArray.getJSONObject(0).getString("horas_fct");
                            centro_estudios_usuario = jsonArray.getJSONObject(0).getString("centro_estudios");
                            familia_ciclo_usuario = jsonArray.getJSONObject(0).getString("familia_ciclo");
                            ciclo_formativo_usuario = jsonArray.getJSONObject(0).getString("ciclo_formativo");
                            centro_practicas_usuario = jsonArray.getJSONObject(0).getString("centro_practicas");
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Log.e("MiPerfilFragment", "Error al obtener datos del usuario");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NavMenu.this, R.string.error_servidor, Toast.LENGTH_SHORT).show();
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

    /*************************************************************************************************************
     * Método que cierra la sesión del usuario activo (actualiza isLogged a 0 y vuelva a la pantalla de login)   *
     ************************************************************************************************************/
    public void cerrarSesion(){
        Intent intent = new Intent(NavMenu.this, PantallaLogin.class);
        startActivity(intent);
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Al cerrar sesión estaremos actualizando el campo isLogged a 0 para que no se detecte como sesión iniciada en la pantalla
                        // de carga al volver a abrir la aplicación
                      //  Log.d("NavMenu", "Sesión de usuario cerrada");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NavMenu.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                      //  Log.e("NavMenu", "Error al conectar con el servidor para cerrar la sesión del usuario");
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
}
