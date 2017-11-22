package es.proyecto.eva.miagendadam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PantallaPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        setTitle("Mi agenda FP");
    }
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
