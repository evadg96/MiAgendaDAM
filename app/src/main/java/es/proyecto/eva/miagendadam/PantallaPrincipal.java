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
}
