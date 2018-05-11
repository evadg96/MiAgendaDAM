package es.proyecto.eva.miagendadam.Fragments.Recursos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Toast;

import es.proyecto.eva.miagendadam.R;

import static es.proyecto.eva.miagendadam.Fragments.Recursos.RecursosFragment.esMiCalendario;
import static es.proyecto.eva.miagendadam.Fragments.Recursos.RecursosFragment.otrosCalendarios;
import static es.proyecto.eva.miagendadam.Fragments.Recursos.ListadoComunidadesCalendario.comunidad_seleccionada;

public class WebView extends AppCompatActivity {
    // la url por defecto está en blanco, la rellenaremos según los datos que debamos mostrar en cada caso
    private String url = "";
    private String provincia = ""; // para el cal. de festivos locales
    private String comunidad_autonoma = ""; // para el cal. escolar
    android.webkit.WebView web;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_calendarios);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtenemos la provincia del usuario para saber qué calendario debemos cargar
        SharedPreferences preferences = this.getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        provincia = preferences.getString("provincia", "");
        System.out.println("PROVINCIA DEL USUARIO: " + provincia);
        web = (android.webkit.WebView) findViewById(R.id.webview_calendario);
        web.setWebViewClient(new MyWebViewClient());
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true); // habilitamos opción de pinch to zoom
        web.getSettings().setDisplayZoomControls(false); // quitamos los botones de zoom visibles en pantalla
        web.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLoadWithOverviewMode(true);

// Creamos la ventana de diálogo con círculo de carga para la espera de carga de los datos
        progressDialog = new ProgressDialog(WebView.this);
        progressDialog.setMessage("Cargando contenido...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        // obtenemos la referencia de donde se viene para adecuar el contenido a mostrar
        rellenarWebView();
    }

    public void rellenarWebView(){
        // Obtenemos las comunidades y rellenamos las url con los calendarios escolares correspondientes, solo si se pretende visualizar el calendario escolar
        // ya que si no tendríamos problemas para rellenar correctamente la url (se solaparían)
            if (esMiCalendario) {
                // obtenemos la comunidad autónoma en base a la provincia del usuario
                if (provincia.equals("A Coruña") || provincia.equals("Lugo") || provincia.equals("Pontevedra") || provincia.equals("Orense")) {
                    comunidad_autonoma = "Galicia";
                    url = "http://www.edu.xunta.gal/portal/calendarioescolar";
                } else if (provincia.equals("Asturias")) {
                    comunidad_autonoma = "Principado de Asturias";
                    url = "http://mas.lne.es/calendario/escolar/";
                } else if (provincia.equals("Cantabria")) {
                    comunidad_autonoma = "Cantabria";
                    url = "http://www.cantabriafesmcugt.es/calendario-escolar-curso-2017-2018/";
                } else if (provincia.equals("Álava") || provincia.equals("Guipúzcoa") || provincia.equals("Vizcaya")) {
                    comunidad_autonoma = "País Vasco";
                    url = "http://www.elcorreo.com/sociedad/educacion/calendario-escolar-euskadi-pais-vasco-2017-2018-20170906140611-nt.html";
                } else if (provincia.equals("Madrid")) {
                    comunidad_autonoma = "Comunidad de Madrid";
                    url = "https://www.educa2.madrid.org/web/calendario-escolar-de-la-comunidad-de-madrid/2017-18";
                } else if (provincia.equals("Valladolid") || provincia.equals("Soria") || provincia.equals("Segovia") || provincia.equals("Salamanca") || provincia.equals("Burgos")
                        || provincia.equals("Ávila") || provincia.equals("Palencia") || provincia.equals("Zamora") || provincia.equals("León")) {
                    comunidad_autonoma = "Castilla y León";
                    url = "http://www.educa.jcyl.es/es/calendario-escolar";
                } else if (provincia.equals("Murcia")) {
                    comunidad_autonoma = "Región de Murcia";
                    url = "http://www.laopiniondemurcia.es/especiales/calendario-escolar-murcia/";
                } else if (provincia.equals("Albacete") || provincia.equals("Ciudad Real") || provincia.equals("Cuenca") || provincia.equals("Guadalajara")
                        || provincia.equals("Toledo")) {
                    comunidad_autonoma = "Castilla-La Mancha";
                    url = "https://www.csif.es/contenido/castilla-la-mancha/educacion/235104";
                } else if (provincia.equals("Las Palmas") || provincia.equals("Santa Cruz de Tenerife")) {
                    comunidad_autonoma = "Canarias";
                    url = "http://www.gobiernodecanarias.org/educacion/web/centros/calendario_escolar/";
                } else if (provincia.equals("Almería") || provincia.equals("Cádiz") || provincia.equals("Córdoba") || provincia.equals("Granada") || provincia.equals("Huelva")
                        || provincia.equals("Jaén") || provincia.equals("Málaga") || provincia.equals("Sevilla")) {
                    comunidad_autonoma = "Andalucía";
                    url = "http://www.juntadeandalucia.es/educacion/portals/web/ced/calendario-escolar";
                } else if (provincia.equals("Huesca") || provincia.equals("Teruel") || provincia.equals("Zaragoza")) {
                    comunidad_autonoma = "Aragón";
                    url = "http://www.educaragon.org/calendario/calendario_escolar.asp";
                } else if (provincia.equals("Baleares")) {
                    comunidad_autonoma = "Islas Baleares";
                    url = "http://www.diariodemallorca.es/especiales/calendario-escolar-laboral-baleares/curso-2017-2018/";
                } else if (provincia.equals("Barcelona") || provincia.equals("Girona") || provincia.equals("Lleida") || provincia.equals("Tarragona")) {
                    comunidad_autonoma = "Cataluña";
                    url = "http://ensenyament.gencat.cat/ca/arees-actuacio/centres-serveis-educatius/centres/calendari-escolar/curs-2017-2018/";
                } else if (provincia.equals("Alicante") || provincia.equals("Valencia") || provincia.equals("Castellón")) {
                    comunidad_autonoma = "Comunidad Valenciana";
                    url = "http://www.levante-emv.com/especiales/calendario-laboral/calendario-escolar-2017-2018/";
                } else if (provincia.equals("Badajoz") || provincia.equals("Cáceres")) {
                    comunidad_autonoma = "Extremadura";
                    url = "https://www.educarex.es/calendario-escolar.html";
                } else if (provincia.equals("La Rioja")) {
                    comunidad_autonoma = "La Rioja";
                    url = "https://www.elbalcondemateo.es/calendario-escolar-2017-18-la-rioja/";
                } else if (provincia.equals("Navarra")) {
                    comunidad_autonoma = "Comunidad Foral de Navarra";
                    url = "https://www.educacion.navarra.es/web/dpto/calendario-escolar";
                }
                setTitle(comunidad_autonoma);
                System.out.println("COMUNIDAD AUTÓNOMA DEL USUARIO: " + comunidad_autonoma);
                // Cargamos la url correspondiente en el WebView
                web.loadUrl(url);
            } else if (otrosCalendarios) {
                if (comunidad_seleccionada.equals("Andalucía")) {
                    url = "http://www.juntadeandalucia.es/educacion/portals/web/ced/calendario-escolar";
                } else if (comunidad_seleccionada.equals("Aragón")) {
                    url = "http://www.educaragon.org/calendario/calendario_escolar.asp";
                } else if (comunidad_seleccionada.equals("Principado de Asturias")) {
                    url = "http://mas.lne.es/calendario/escolar/";
                } else if (comunidad_seleccionada.equals("Islas Baleares")) {
                    url = "http://www.diariodemallorca.es/especiales/calendario-escolar-laboral-baleares/curso-2017-2018/";
                } else if (comunidad_seleccionada.equals("Canarias")) {
                    url = "http://www.gobiernodecanarias.org/educacion/web/centros/calendario_escolar/";
                } else if (comunidad_seleccionada.equals("Cantabria")) {
                    url = "http://www.cantabriafesmcugt.es/calendario-escolar-curso-2017-2018/";
                } else if (comunidad_seleccionada.equals("Castilla-La Mancha")) {
                    url = "https://www.csif.es/contenido/castilla-la-mancha/educacion/235104";
                } else if (comunidad_seleccionada.equals("Castilla y León")) {
                    url = "http://www.educa.jcyl.es/es/calendario-escolar";
                } else if (comunidad_seleccionada.equals("Cataluña")) {
                    url = "http://ensenyament.gencat.cat/ca/arees-actuacio/centres-serveis-educatius/centres/calendari-escolar/curs-2017-2018/";
                } else if (comunidad_seleccionada.equals("Comunidad Valenciana")) {
                    url = "http://www.levante-emv.com/especiales/calendario-laboral/calendario-escolar-2017-2018/";
                } else if (comunidad_seleccionada.equals("Extremadura")) {
                    url = "https://www.educarex.es/calendario-escolar.html";
                } else if (comunidad_seleccionada.equals("Galicia")) {
                    url = "http://www.edu.xunta.gal/portal/calendarioescolar";
                } else if (comunidad_seleccionada.equals("La Rioja")) {
                    url = "https://www.elbalcondemateo.es/calendario-escolar-2017-18-la-rioja/";
                } else if (comunidad_seleccionada.equals("Comunidad de Madrid")) {
                    url = "https://www.educa2.madrid.org/web/calendario-escolar-de-la-comunidad-de-madrid/2017-18";
                } else if (comunidad_seleccionada.equals("Región de Murcia")) {
                    url = "http://www.laopiniondemurcia.es/especiales/calendario-escolar-murcia/";
                } else if (comunidad_seleccionada.equals("Comunidad Foral de Navarra")) {
                    url = "https://www.educacion.navarra.es/web/dpto/calendario-escolar";
                } else if (comunidad_seleccionada.equals("País Vasco")) {
                    url = "http://www.elcorreo.com/sociedad/educacion/calendario-escolar-euskadi-pais-vasco-2017-2018-20170906140611-nt.html";
                }
                setTitle(comunidad_seleccionada);
                web.loadUrl(url);
            }
    }


    /***********************************************************************************************
     * Crea el menú de opciones de la barra de acciones
     * @param menu
     * @return
     **********************************************************************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_webview, menu); // la R referencia a la ubicación del archivo
        return true;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url){
            view.loadUrl(url);
            return true;
        }

        // cuando la página termina de cargar...
        @Override
        public void onPageFinished(android.webkit.WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss(); // cerramos el diálogo
            }
        }

        @Override
        public void onReceivedError(android.webkit.WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(WebView.this, "Error: " + description, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Al pulsar el icono de la flecha atrás de la barra de acciones
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_info:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.info_webview)
                        .setPositiveButton(R.string.btn_entendido, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            // dejamos en blanco para que solo se cierre el diálogo
                            }
                        });
                Dialog dialog = builder.create();
                dialog.show(); // mostramos el diálogo
                return true;
            case R.id.menu_copiar:
                // copiamos al portapapeles la dirección de enlace del calendario escolar que se esté mostrando
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("enlace calendario escolar", url);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, R.string.copiado, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
