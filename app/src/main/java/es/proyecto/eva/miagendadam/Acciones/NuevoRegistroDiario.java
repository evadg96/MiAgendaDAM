package es.proyecto.eva.miagendadam.Acciones;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

/***************************************************************************************************
 *  Pantalla que se abre con la pulsación del botón "+" del diario (nuevo registro de diario)      *
 *  y que sirve para crear un nuevo registro en el diario del usuario.                             *
 *  Contiene los campos de fecha, horas, descripción y valoración.                                 *
 **************************************************************************************************/
public class NuevoRegistroDiario extends AppCompatActivity {
    ImageButton btnBueno, btnRegular, btnMalo;
    EditText fecha, horas, descripcion;
    private String valoracionDia = "";
    private boolean guardado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_registro_diario);
        setTitle("Nuevo registro");
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

    /***********************************************************************************************
     *     Opciones del menú de la barra de acciones
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.menu_guardar: // Opción de guardar registro
                Log.i("ActionBar", "Guardar!");
                guardarRegistro(); // guardamos el registro en la base de datos
                guardado = true;
                return true;
            case android.R.id.home: // Opción de volver hacia atrás
                if (!guardado){
                    AlertDialog.Builder builder = new AlertDialog.Builder(NuevoRegistroDiario.this);
                    builder.setTitle(R.string.titulo_dialog_salir_sin_guardar); // titulo del diálogo
                    builder.setMessage(R.string.contenido_dialog_salir_sin_guardar)
                            .setPositiveButton(R.string.respuesta_dialog_volver, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    onBackPressed(); // volvemos atrás
                                }
                            })
                            .setNegativeButton(R.string.respuesta_dialog_no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    //no hacemos nada, y al pulsar el botón simplemente se cerrará el diálogo
                                }
                            });
                    // Create the AlertDialog object and return it
                    Dialog dialog = builder.create();
                    dialog.show();

                } else if (guardado){
                    onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void guardarRegistro(){
        System.out.println("Ejecutamos consulta de guardado de registro.");
    }


}
