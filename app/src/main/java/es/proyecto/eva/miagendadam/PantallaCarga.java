package es.proyecto.eva.miagendadam;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class PantallaCarga extends AppCompatActivity {
    private final int DURACION_SPLASH = 3000; // los segundos que se verá la pantalla (3)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);
        getSupportActionBar().hide(); // para ocultar la barra de titulo de la pantalla
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, // para poner en pantalla completa la actividad, así no se verá la barra
                WindowManager.LayoutParams.FLAG_FULLSCREEN);        // de notificaciones con la hora etc. (luego vuelve a aparecer)

        // Para dejar la actividad visible durante 3 segundos
        // Después se pasa a la pantalla principal
        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(PantallaCarga.this, LoginActivity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);
    }
}
