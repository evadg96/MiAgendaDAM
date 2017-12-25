package es.proyecto.eva.miagendadam.Acciones;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import es.proyecto.eva.miagendadam.R;

public class NuevoRegistroDiario extends AppCompatActivity {
    ImageButton btnBueno, btnRegular, btnMalo;
    EditText fecha, horas, descripcion;
    private String valoracionDia = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_registro_diario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnBueno = (ImageButton) findViewById(R.id.btn_bueno);
        btnRegular = (ImageButton) findViewById(R.id.btn_regular);
        btnMalo = (ImageButton) findViewById(R.id.btn_malo);
        fecha = (EditText) findViewById(R.id.editText_fecha);
        horas = (EditText) findViewById(R.id.editText_horas);
        descripcion = (EditText) findViewById(R.id.editText_descripcion);

        btnBueno.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnBueno.setAlpha(1f);
                btnRegular.setAlpha(0.5f);
                btnMalo.setAlpha(0.5f);
                valoracionDia = "Bueno";
            }
        });
        btnRegular.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnRegular.setAlpha(1f);
                btnBueno.setAlpha(0.5f);
                btnMalo.setAlpha(0.5f);
                valoracionDia = "Regular";
            }
        });
        btnMalo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnMalo.setAlpha(1f);
                btnBueno.setAlpha(0.5f);
                btnRegular.setAlpha(0.5f);
                valoracionDia = "Malo";
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nuevo, menu); // la R referencia a la ubicación del archivo
        return true; // .menu es el directorio, y .toolbar el archivo
    }

    // Opciones del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.menu_guardar:
                Log.i("ActionBar", "Guardar!"); // en este caso solo hacemos un log
                return true;
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void guardarRegistro(){
        System.out.println("Ejecutamos consulta de guardado de registro.");
    }


}
