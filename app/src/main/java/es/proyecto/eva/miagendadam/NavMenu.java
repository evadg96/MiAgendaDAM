package es.proyecto.eva.miagendadam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.Fragments.DiarioFragment;
import es.proyecto.eva.miagendadam.Fragments.HorasFragment;
import es.proyecto.eva.miagendadam.VolleyController.AppController;

/***************************************************************************************************
 *  Menú lateral desplegable con las opciones de la aplicación.
 *  Será a su vez la clase contenedora del layout que contenga los fragments
 *  de las distintas opciones de la app, que se mostrarán en función de la opción
 *  seleccionada en el menú.
 **************************************************************************************************/

public class NavMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView nombreUsuario, correoUsuario;
    static String nombre_de_usuario;
    static String correo_electronico;
    private StringRequest request;
    private String url_consulta = "http://192.168.0.12/MiAgenda/cerrar_sesion.php";
    private String  url_consulta2 = "http://192.168.0.12/MiAgenda/select_dias.php";
//    private String url_consulta = "http://192.168.0.158/MiAgenda/cerrar_sesion.php";
//    private String  url_consulta2 = "http://192.168.0.158/MiAgenda/select_dias.php";
    public static boolean diarioVacio = false;
    public static boolean horasVacio = false;
    public static boolean anotacionesVacio = false;
    public static boolean contenidoRecoVacio = false;
    public static boolean contenidoPersoVacio = false;
    public static JSONArray jsonArrayDiario;
    private android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        // Referenciamos al SharedPreferences que habíamos creado en la clase PantallaLogin
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        // ****************** ¡¡¡¡ UTILIZAR ESTE FRAGMENTO CADA VEZ QUE SE QUIERA REFERENCIAR AL NOMBRE DE USUARIO ALMACENADO POR LA APLICACIÓN !!!! **********************
        nombre_de_usuario = preferences.getString("nombre_de_usuario", ""); // habiendo declarado la variable CON EL MISMO NOMBRE arriba
        // ****************************************************************************************************************************************************************
        correo_electronico = preferences.getString("correo_electronico", "");

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        // añadimos en el menú lateral el nombre y correo del usuario
        nombreUsuario = (TextView) headerView.findViewById(R.id.nombre_usuario_nav);
        nombreUsuario.setText(nombre_de_usuario);
        correoUsuario = (TextView) headerView.findViewById(R.id.correo_nav);
        correoUsuario.setText(correo_electronico);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        // COMENTO T0DO PARA QUE, AL HACER CLICK EN EL BOTÓN DE ATRÁS DESDE ESTA
        // PANTALLA, NO SE PUEDA VOLVER A LA PANTALLA DE LOGIN.
       // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       // if (drawer.isDrawerOpen(GravityCompat.START)) {
       //     drawer.closeDrawer(GravityCompat.START);
       // } else {
       //     super.onBackPressed();
       // }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_diario) {
            obtenDatosDiario();
        } else if (id == R.id.nav_horas) {
            fragmentManager.beginTransaction().replace(R.id.contenedor, new HorasFragment()).commit();
        } else if (id == R.id.nav_c_reco) {

        } else if (id == R.id.nav_c_perso) {

        } else if (id == R.id.nav_festivos) {

        } else if (id == R.id.nav_contacto) {

        } else if (id == R.id.nav_cerrar_sesion) {
            cerrarSesion();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void obtenDatosDiario(){
        request = new StringRequest(Request.Method.POST, url_consulta2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!nombre_de_usuario.isEmpty()) { // aseguramos que las preferencias no están vacías
                            if (response.equals("0")){
                                diarioVacio = true;
                                // meter un item que diga "no hay nada!"
                                System.out.println("NO HAY REGISTROS DE DIARIO PARA ESTE USUARIO.");
                            } else {
                                try {
                                    response = response.replace("][",","); // SUSTITUIMOS LOS CARACTERES QUE SEPARAN CADA RESULTADO DEL ARRAY
                                    // PORQUE SI NO NOS TOMARÍA SOLO EL PRIMER ARRAY. DE ESTA MANERA HACEMOS QUE LOS DETECTE COMO OBJETOS (EN VEZ DE COMO ARRAYS DIFERENTES)
                                    // DENTRO DE UN ÚNICO ARRAY
                                    // YA QUE LOS ARRAYS TIENEN FORMATO [{...}][{...}], ... CON LO QUE, SI OBTIENE ASÍ LOS RESULTADOS, SOLO VA A COGER EL PRIMERO
                                    // Y UN ARRAY DE OBJETOS TENDRÍA ESTE OTRO FORMATO [{...}, {...}, {...}] DONDE LOS CORCHETES DETERMINAN EL ARRAY, Y LAS LLAVES LOS OBJETOS.
                                    jsonArrayDiario = new JSONArray(response);
                                    //System.out.println("Respuesta de servidor: " + response); // debug
                                    //System.out.println("LONGITUD DEL ARRAY: "+ jsonArrayDominios.length()); // debug
                                    fragmentManager.beginTransaction().replace(R.id.contenedor, new DiarioFragment()).commit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else { // si no hay preferencias, notificamos
                            Toast.makeText(NavMenu.this, "No se pudo obtener el nombre de usuario.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(NavMenu.this, "Error de conexión.", Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // AQUI SE ENVIARAN LOS DATOS EMPAQUETADOS EN UN OBJETO MAP<clave, valor>
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_de_usuario); // pasamos el nombre de usuario como parámetro de la consulta para obtener sus registros del diario
                return parametros;
            }

        };
        AppController.getInstance().addToRequestQueue(request);
    }

    public void cerrarSesion(){
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Al cerrar sesión estaremos actualizando el campo isLogged a 0 para que no se detecte como sesión iniciada en la pantalla
                        // de carga al volver a abrir la aplicación
                        System.out.println("SESIÓN DE USUARIO CERRADA.");
                        Intent intent = new Intent(NavMenu.this, PantallaLogin.class);
                        startActivity(intent);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(NavMenu.this, "Error de conexión.", Toast.LENGTH_SHORT).show();

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
