package es.proyecto.eva.miagendafp.Fragments.Contactos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static es.proyecto.eva.miagendafp.Fragments.Contactos.ContactosFragment.id_contacto_seleccionado;
import static es.proyecto.eva.miagendafp.Fragments.Contactos.ContactosFragment.nombre_seleccionado_codificado;
import static es.proyecto.eva.miagendafp.Fragments.Contactos.ContactosFragment.modulo_seleccionado_codificado;
import static es.proyecto.eva.miagendafp.Fragments.Contactos.ContactosFragment.correo_seleccionado;
import static es.proyecto.eva.miagendafp.Fragments.Contactos.ContactosFragment.telefono_seleccionado;

import es.proyecto.eva.miagendafp.R;
import es.proyecto.eva.miagendafp.VolleyController.AppController;
// ****************** PUBLICIDAD ************************
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
public class VerContacto extends AppCompatActivity {
    TextView txtNombre, txtModulo, txtCorreo, txtTelefono;
    ImageButton btnLlamar, btnEnviarCorreo;
    FloatingActionButton btnEditar;
    private StringRequest request;
    private String url_consulta = "http://miagendafp.000webhostapp.com/delete_contacto.php";
    // ******* PUBLICIDAD *******
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_contacto);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtNombre = (TextView) findViewById(R.id.txt_nombre);
        txtModulo = (TextView) findViewById(R.id.txt_modulo);
        txtCorreo = (TextView) findViewById(R.id.txt_correo);
        txtTelefono = (TextView) findViewById(R.id.txt_telefono);
        btnLlamar = (ImageButton) findViewById(R.id.btn_llamar);
        btnEnviarCorreo = (ImageButton) findViewById(R.id.btn_correo);
        btnEditar = (FloatingActionButton) findViewById(R.id.btn_editar_contacto);
        System.out.println("VISTA DETALLE DE CONTACTO.");
        System.out.println("NOMBRE SELECCIONADO: " + nombre_seleccionado_codificado + "\n CORREO SELECCIONADO: "+ correo_seleccionado
                + "\n MODULO SELECCIONADO: "+ modulo_seleccionado_codificado + "\n TELEFONO: "+ telefono_seleccionado);

        btnEnviarCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escribirCorreo();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerContacto.this, EditarContacto.class);
                startActivity(intent);
            }
        });


    // **************************** PUBLICIDAD *****************************************
    // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public void onResume(){
       super.onResume();
       System.out.println("ON RESUME VER CONTACTO");
        // Ponemos los datos obtenidos del contacto seleccionado
        txtNombre.setText(nombre_seleccionado_codificado);
        txtModulo.setText(modulo_seleccionado_codificado);
        txtCorreo.setText(correo_seleccionado);
        // si el contacto seleccionado no tiene teléfono asociado, escondemos el TextView del teléfono y el icono de llamar
        if (telefono_seleccionado.equals("")) {
            btnLlamar.setVisibility(View.GONE);
            txtTelefono.setVisibility(View.GONE);
        } else {
            txtTelefono.setText(telefono_seleccionado);
            // asignamos la función de llamar al botón
            btnLlamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Llamar contacto");
                    llamada();
                }
            });
        }
    }

    /***********************************************************************************************
     * Crea el menú de opciones de la barra de acciones
     * @param menu
     * @return
     **********************************************************************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_borrar, menu); // la R referencia a la ubicación del archivo
        return true;
    }

    /***********************************************************************************************
     *     Opciones del menú de la barra de acciones
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_borrar: //Opción de borrar el registro
                //Log.i("VerYEditarRegistroD", "Action Borrar registro");
                borrarContacto();
                return true;
            case android.R.id.home: // Opción de volver hacia atrás
                //Log.i("VerYEditarRegistroD", "Action Atrás");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************************************
     * Método que realiza una llamada de teléfono al número de teléfono del contacto
     **********************************************************************************************/
    public void llamada(){
        Intent i = new Intent(android.content.Intent.ACTION_CALL, Uri.parse("tel:" + telefono_seleccionado));
        // comprobamos si el permiso de llamada está concedido o no
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) { // No está concedido
            System.out.println("Permiso no concedido, solicitar permiso");
            AlertDialog.Builder builder = new AlertDialog.Builder(VerContacto.this);
            builder.setMessage(R.string.texto_permiso_llamada)
                    .setPositiveButton(R.string.opc_permiso, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            gestionaPermisos();
                        }
                    })
                    .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Se cancela el diálogo, dejamos
                            // en blanco para que no se haga nada,
                            // solo cerrar el diálogo
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show(); // mostramos el diálogo
            return;
        } else { // Sí está concedido
            startActivity(i);
            System.out.println("Permiso concedido, llamando...");
        }
    }

    public void gestionaPermisos(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        this.startActivity(intent);
    }

    /*******************************************************************************************************
     * Método que abre una app cliente de correo electrónico para enviarle un email al contacto seleccionado
     ******************************************************************************************************/
    public void escribirCorreo(){
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", correo_seleccionado , null));
        // i.putExtra(Intent.EXTRA_SUBJECT, "Asunto"); <-- si quisiéramos poner un asunto por defecto
        try {
            startActivity(Intent.createChooser(i, "Selecciona un gestor de correo electrónico: "));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
        }
    }

    /***********************************************************************************************
     * Método que elimina el registro seleccionado, preguntando previamente si se desea realizar la
     * operación
     **********************************************************************************************/
    public void borrarContacto(){
        // Preguntamos antes de borrar definitivamente
        AlertDialog.Builder builder = new AlertDialog.Builder(VerContacto.this);
        builder.setTitle(R.string.dialog_borrar_contacto); // titulo del diálogo
        builder.setMessage(R.string.dialog_texto_borrar_contacto)
                .setPositiveButton(R.string.dialog_opcion_borrar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        request = new StringRequest(Request.Method.POST, url_consulta,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(VerContacto.this, R.string.toast_contacto_eliminado, Toast.LENGTH_LONG).show();
                                        finish(); // cerramos la actividad para volver al fragmento con el listado de registros
                                        //Log.d("VerYEditarRegistroDiario", "Registro borrado");

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Toast.makeText(VerYEditarRegistroDiario.this, R.string.error_servidor, Toast.LENGTH_LONG).show();
                                        Snackbar.make(findViewById(android.R.id.content),
                                                R.string.error_servidor, Snackbar.LENGTH_LONG).show();
                                        //Log.e("VerYEditarRegistroDiario", "Error al conectar con el servidor para borrar el registro seleccionado");
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> parametros = new HashMap<>();
                                parametros.put("idContacto", id_contacto_seleccionado);
                                return parametros;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(request);
                    }
                })
                .setNegativeButton(R.string.respuesta_dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //no hacemos nada, y al pulsar el botón simplemente se cerrará el diálogo
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }
}
