package es.proyecto.eva.miagendadam.Fragments.Horas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.proyecto.eva.miagendadam.R;

/***************************************************************************************************
 * Fragmento de la opción Horas que permite la visualización de las horas de prácticas que lleva
 * trabajadas el usuario activo, así como de las horas restantes para finalizar el módulo.
 **************************************************************************************************/
public class HorasFragment extends Fragment {
    public HorasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_horas, container, false);
    }
}
