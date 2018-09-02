package es.proyecto.eva.miagendafp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendafp.Fragments.Recursos.RecursosFragment;
import es.proyecto.eva.miagendafp.Fragments.Notas.NotasFragment;
import es.proyecto.eva.miagendafp.Fragments.Contactos.ContactosFragment;
import es.proyecto.eva.miagendafp.Fragments.Diario.DiarioFragment;
import es.proyecto.eva.miagendafp.Fragments.Horas.HorasFragment;
import es.proyecto.eva.miagendafp.Fragments.Inicio.InicioFragment;
import es.proyecto.eva.miagendafp.Fragments.MiPerfil.MiPerfilFragment;
import es.proyecto.eva.miagendafp.VolleyController.AppController;


/***************************************************************************************************
 *  Menú lateral desplegable con las opciones de la aplicación.                                    *
 *  Será a su vez la clase contenedora del layout que contenga los fragments                       *
 *  de las distintas opciones de la app, que se mostrarán en función de la opción                  *
 *  seleccionada en el menú.                                                                       *
 **************************************************************************************************/

public class NavMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView nombreUsuario, correoUsuario;
    public static TextView familiaCiclo; // estático para poder actualizar la familia del ciclo automáticamente si se cambia
    // desde el perfil del usuario
    private String nombre_de_usuario;
    private String correo_de_usuario;
    private String familia_ciclo;
    private StringRequest request;
    private String idUsuario = ""; // el identificador de usuario que utilizaremos para realizar consultas posteriores
    //    private String url_consulta = "http://192.168.0.12/MiAgenda/cerrar_sesion.php";
    private String url_consulta = "http://miagendafp.000webhostapp.com/cerrar_sesion.php";
    private String url_consulta2 = "http://miagendafp.000webhostapp.com/eliminar_cuenta.php";
//    private String url_consulta = "http://192.168.0.159/MiAgenda/cerrar_sesion.php";

    private android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    SharedPreferences preferences;
    NavigationView navigationView;
    public static View headerView; // estático para poder actualizar la familia del ciclo automáticamente si se cambia
    // desde el perfil del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_menu);
        // Obtenemos de las preferencias el nombre del usuario
        SharedPreferences preferences = this.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        idUsuario = preferences.getString("idUsuario", "");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        // Obtenemos preferencias
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
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);

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
        } else if (id == R.id.nav_recursos) {
           // Log.i("NavMenu", "Opción menú: Festivos/No lectivos");
            setTitle(R.string.opc_recursos);
            fragmentManager.beginTransaction().replace(R.id.contenedor, new RecursosFragment()).commit();
        } else if (id == R.id.nav_ayuda) {
           // Log.i("NavMenu", "Opción menú: Acerca de");
            Intent intent = new Intent (this, AcercaDe.class);
            startActivity(intent);
        } else if (id == R.id.nav_cerrar_sesion) {
           // Log.i("NavMenu", "Opción menú: Cerrar sesión");
            AlertDialog.Builder builder = new AlertDialog.Builder(NavMenu.this);
            builder.setTitle(R.string.opc_cerrar_sesion); // titulo del diálogo
            builder.setMessage(R.string.confirma_cerrar_sesion)
                    .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            cerrarSesion();
                        }
                    })
            .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
             // User cancelled the dialog
             }
             });
             // Create the AlertDialog object and return it
             Dialog dialog = builder.create();
             dialog.show();

        } else if (id == R.id.nav_anotaciones) {
           // Log.i("NavMenu", "Opción menú: Anotaciones");
            setTitle(R.string.opc_anotaciones);
            fragmentManager.beginTransaction().replace(R.id.contenedor, new NotasFragment()).commit();
        } else if (id == R.id.nav_contactos) {
           // Log.i("NavMenu", "Opción menú: Tutores");
            setTitle(R.string.opc_contactos);
            fragmentManager.beginTransaction().replace(R.id.contenedor, new ContactosFragment()).commit();
        } else if (id == R.id.nav_perfil) {
          //  Log.i("NavMenu", "Opción menú: Mi perfil");
            setTitle(R.string.opc_perfil);
            fragmentManager.beginTransaction().replace(R.id.contenedor, new MiPerfilFragment()).commit();
        } else if (id == R.id.nav_borrar_cuenta) {
            AlertDialog.Builder builder = new AlertDialog.Builder(NavMenu.this);
            builder.setMessage(R.string.msj_eliminar_cuenta)
                    .setPositiveButton(R.string.msj_si_borrar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Ejecutamos método de borrado de datos
                            eliminarCuentaUsuario();
                        }
                    })
                    .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Al dar a cancelar la ventana simplemente se cierra.
                        }
                    });
            // Creamos el diálogo y lo mostramos
            Dialog dialog = builder.create();
            dialog.show();

        } /**else if (id == R.id.nav_pro) {
         AlertDialog.Builder builder = new AlertDialog.Builder(NavMenu.this);
         builder.setMessage(R.string.dialog_pro_Version)
         .setPositiveButton(R.string.abrir_tienda, new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int id) {
         // Al dar a confirmar se manda a la pantalla de confirmación de registro

         Uri uri = Uri.parse("http://www.andreaardions.com/");
         Intent intent = new Intent(Intent.ACTION_VIEW, uri);
         startActivity(intent);
         }
         })
         .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int id) {
         // Al dar a cancelar la ventana simplemente se cierra.
         }
         });
         // Creamos el diálogo y lo mostramos
         Dialog dialog = builder.create();
         dialog.show();
         }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**************************************************************************************************************************
     * Método que elimina los datos de un usuario y cambia su estado a usuario no activo, para que "no exista"
     * (se borrarán todos sus registros, contactos, notas y demás, y su cuenta de usuario dejará de tener validez
     * para utilizar la aplicación, es decir, no podrá iniciar sesión nunca más porque será como si no existiera ese usuario,
     * pero sus datos de cuenta seguirán almacenados en la base de datos.)
     ***************************************************************************************************************************/
    public void eliminarCuentaUsuario(){
        final ProgressDialog progressDialog = new ProgressDialog(NavMenu.this);
        progressDialog.setTitle(R.string.dialog_cargando);
        progressDialog.setMessage("Borrando datos. Por favor, espera un momento.");
        progressDialog.show();
        request = new StringRequest(Request.Method.POST, url_consulta2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       if (response.equals("1")){
                           progressDialog.dismiss();
                           Toast.makeText(NavMenu.this, R.string.msj_datos_borrados, Toast.LENGTH_LONG).show();
                           // vamos a pantalla login
                           Intent intent = new Intent(NavMenu.this, PantallaLogin.class);
                           startActivity(intent);
                       } else {
                           Toast.makeText(NavMenu.this, R.string.error_borrar_datos, Toast.LENGTH_LONG).show();
                       }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(NavMenu.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        Snackbar.make(findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                        //  Log.e("NavMenu", "Error al conectar con el servidor para cerrar la sesión del usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("idUsuario", idUsuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }


    /*************************************************************************************************************
     * Método que cierra la sesión del usuario activo (actualiza isLogged a 0 y vuelva a la pantalla de login)   *
     ************************************************************************************************************/
    public void cerrarSesion(){
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
                       // Toast.makeText(NavMenu.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                        Snackbar.make(findViewById(android.R.id.content),
                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                      //  Log.e("NavMenu", "Error al conectar con el servidor para cerrar la sesión del usuario");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nUsuario", nombre_de_usuario);
                return parametros;
            }
        };
        AppController.getInstance().addToRequestQueue(request);

        // vamos a pantalla login
        Intent intent = new Intent(NavMenu.this, PantallaLogin.class);
        startActivity(intent);
        finish(); // cerramos para imposibilitar vuelta
    }

}
