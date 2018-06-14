package es.proyecto.eva.miagendafp.Fragments.Recursos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import es.proyecto.eva.miagendafp.R;

// ****************** PUBLICIDAD ************************
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
public class ListadoPaginasRecomendadas extends AppCompatActivity {
    private ArrayList<String> arrayTitulos;
    private ArrayList<String> arrayCategorias;
    ListView listaResultado;
    AdaptadorListaPaginasRecomendadas adaptador; // objeto de la clase AdaptadorListaDiario que utilizaremos como adaptador personalizado para

    // Array de enlaces a las páginas web recomendadas
    private String[] paginas = {
            "https://www.mecd.gob.es/educacion-mecd/areas-educacion/estudiantes/formacion-profesional/becas-ayudas-premios/becas-y-ayudas-para-estudiar.html", // 0
            "http://www.fplandia.es/",                                              // 1
            "https://apuntes.rincondelvago.com/apuntes_fp/",                        // 2
            "https://forofp.es/",                                                   // 3
           // "http://www.formacion-profesional.org/",        NO FUNCIONA                        // 4
            "http://www.descubrelafp.org/",                                         // 4
            "http://todofp.es/que-como-y-donde-estudiar/que-estudiar/ciclos.html",  // 5
            "http://todofp.es/que-como-y-donde-estudiar/que-estudiar/familia.html", // 6
            "http://todofp.es/pruebas-convalidaciones.html",                        // 7
            "http://todofp.es/sobre-fp/modulo-profesional-proyecto.html",           // 8
            "http://todofp.es/sobre-fp/formacion-en-centros-de-trabajo.html",       // 9
            "http://todofp.es/que-como-y-donde-estudiar/donde-estudiar/comunidades.html", // 10
            "http://todofp.es/que-como-y-donde-estudiar/cuando-inscribirse.html"   // 11
    };

    // Array de títulos a las páginas
    private String[] titulos = {
            "Becas y ayudas para fp 17-18", // 0
            "Información general",          // 1
            "Apuntes, exámenes y más",      // 2
            "Forofp.es",                    // 3
           // "Más info sobre la fp",  NO FUNCIONA       // 4
            "Descubre la fp",               // 4
            "Ciclos por niveles",           // 5
            "Ciclos por familias",          // 6
            "Pruebas y convalidaciones",    // 7
            "El proyecto final en fp superior", // 8
            "La formación en centros de trabajo", // 9
            "Centros por comunidad autónoma",  // 10
            "Fechas de inscripciones y pruebas" // 11
    };

    // Array de categorías de las páginas
    private String[] categorias = {
            "Becas, ayudas y premios", // 0
            "Apuntes",                 // 1
            "Info.",                   // 2
            "Misc.",                   // 3
            "Foros"                    // 4
    };

    // ******* PUBLICIDAD *******
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paginas_recomendadas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listaResultado = (ListView) findViewById(R.id.lista_paginas);

        // **************************** PUBLICIDAD *****************************************
    // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        arrayTitulos = new ArrayList<String>();
        arrayCategorias = new ArrayList<String>();

        // Rellenamos arrays de títulos y categorías de las páginas con cada dato manualmente:
        // 0ª página:
        arrayTitulos.add(titulos[0]);
        arrayCategorias.add(categorias[0]);
        // 1ª página:
        arrayTitulos.add(titulos[1]);
        arrayCategorias.add(categorias[2]);
        // 2ª página:
        arrayTitulos.add(titulos[2]);
        arrayCategorias.add(categorias[1]);
        // 3ª página:
        arrayTitulos.add(titulos[3]);
        arrayCategorias.add(categorias[3]);
        // 4ª página:
        arrayTitulos.add(titulos[4]);
        arrayCategorias.add(categorias[2]);
        // 5ª página:
        arrayTitulos.add(titulos[5]);
        arrayCategorias.add(categorias[3]);
        // 6ª página:
        arrayTitulos.add(titulos[6]);
        arrayCategorias.add(categorias[2]);
        // 7ª página:
        arrayTitulos.add(titulos[7]);
        arrayCategorias.add(categorias[2]);
        // 8ª página:
        arrayTitulos.add(titulos[8]);
        arrayCategorias.add(categorias[2]);
        // 9ª página:
        arrayTitulos.add(titulos[9]);
        arrayCategorias.add(categorias[2]);
        // 10ª página:
        arrayTitulos.add(titulos[10]);
        arrayCategorias.add(categorias[2]);
        // 11ª página:
        arrayTitulos.add(titulos[11]);
        arrayCategorias.add(categorias[2]);
        // 12ª página:
        arrayTitulos.add(titulos[12]);
        arrayCategorias.add(categorias[2]);

        for (int i = 0; i < arrayTitulos.size(); i++){
            System.out.println("Título: " + arrayTitulos.get(i));
            System.out.println("Categoría: " + arrayCategorias.get(i));
        }

        // *******************************************************************
        adaptador = new AdaptadorListaPaginasRecomendadas(this, arrayTitulos, arrayCategorias);
        listaResultado.setAdapter(adaptador); // asociamos el adaptador a la lista

        // Al pulsar sobre algún item de la lista (sobre alguna página recomendada) la mostramos en el webview correspondiente
        listaResultado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> customerAdapter, View footer, int selectedInt, long selectedLong) {
                int id = (int) listaResultado.getItemIdAtPosition(selectedInt); // obtenemos el id del elemento del listado seleccionado
                // para saber qué id debemos obtener...
                try {
                    String url = paginas[id];
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))); // abrimos la url de la página seleccionada del listado en el navegador
                    // hacemos esto en vez de utilizar webviews ya que muchas de estas páginas tienen recursos pdf o archivos multimedia que con un webview no podríamos utilizar

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Al pulsar el icono de la flecha atrás de la barra de acciones
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
