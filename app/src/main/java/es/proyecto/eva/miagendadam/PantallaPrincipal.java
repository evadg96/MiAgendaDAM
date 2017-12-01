package es.proyecto.eva.miagendadam;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.proyecto.eva.miagendadam.VolleyController.AppController;

public class PantallaPrincipal extends AppCompatActivity {

    Button btn;
    TextView texto;
    static String nombre_de_usuario;
    static StringRequest request;
    static String url_consulta = "http://192.168.0.10/MiAgenda/consulta_cerrar_sesion.php";
    //static String url_consulta = "http://192.168.0.156/MiAgenda/consulta_cerrar_sesion.php";
    // ************************************ SERVIDOR REMOTO *************************************************************
   // private String url_consulta = "http://miagendafp.000webhostapp.com/consulta_cerrar_sesion.php?host=localhost&user=id3714609_miagendafp_admin&bd=id3714609_1_miagenda";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        setTitle("Mi agenda FP");
        btn = (Button)findViewById(R.id.btn_cerrar);
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        nombre_de_usuario = preferences.getString("nombre_de_usuario", ""); // habiendo declarado la variable CON EL MISMO NOMBRE arriba
        texto = (TextView) findViewById(R.id.txt);
        texto.setText("Hola, " + nombre_de_usuario);
    }

    public void cerrarSesion(View view){
        request = new StringRequest(Request.Method.POST, url_consulta,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Al cerrar sesión estaremos actualizando el campo isLogged a 0 para que no se detecte como sesión iniciada en la pantalla
                        // de carga al volver a abrir la aplicación
                        System.out.println("SESIÓN DE USUARIO CERRADA.");
                        Intent intent = new Intent(PantallaPrincipal.this, PantallaLogin.class);
                        startActivity(intent);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // SE EJECUTA CUANDO ALGO SALE MAL AL INTENTAR HACER LA CONEXION
                        Toast.makeText(PantallaPrincipal.this, "Error de conexión.", Toast.LENGTH_SHORT).show();

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


    // NO BORRAR
    @Override
    public void onBackPressed (){
        /**
         *  Generamos este método para que la app no haga nada en esta actividad si se da el botón de Atrás.
         *  De esta manera evitamos que pueda volver a la pantalla de login cuando haga inicio de sesión por primera vez
         *  Por dos motivos:
         *  1) No queremos que pueda volver a la pantalla de inicio de sesión si ya ha iniciado sesión
         *  2) Al volver desde esta pantalla a la de login, el ProgressDialog se queda cargando de manera infinita, así
         *  impedimos que se vea este error.
          */
    }
}
