package es.proyecto.eva.miagendafp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
// ****************** PUBLICIDAD ************************
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
/**********************************************************************************************************
 * Clase que muestra información de la aplicación tal como el autor de la misma, el motivo de desarrollo,
 * la versión de la aplicación y el correo de contacto para dar soporte al usuario
 * Se accede a ella a través de la pantalla Login y del menú lateral desplegable de la aplicación,
 * en la opción Acerca de.
 *********************************************************************************************************/
public class AcercaDe extends AppCompatActivity {
    // ******* PUBLICIDAD *******
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);
        setTitle(R.string.title_activity_acerca_de);
        // **************************** PUBLICIDAD *****************************************
// Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // para ocultar el teclado si se ha quedado abierto de alguna pantalla
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Al pulsar el botón Atrás que aparece en la barra de acciones (no el del dispositivo),
    // volvemos un paso atrás
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
