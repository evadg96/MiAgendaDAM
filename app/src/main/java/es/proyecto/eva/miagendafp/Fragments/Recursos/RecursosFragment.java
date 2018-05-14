package es.proyecto.eva.miagendafp.Fragments.Recursos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import es.proyecto.eva.miagendafp.R;

public class RecursosFragment extends Fragment {
    ImageButton btnCalEscolar, btnOtrosCalendarios, btnEnviarSugerencias, btnPaginasReco;
    public static boolean esMiCalendario = false, otrosCalendarios = false;// para indicar al webView qué página mostrar
    public RecursosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recursos, container, false);
        btnCalEscolar = (ImageButton) view.findViewById(R.id.btn_mi_calendario);
        btnOtrosCalendarios = (ImageButton) view.findViewById(R.id.btn_otros_calendarios);
        btnEnviarSugerencias = (ImageButton) view.findViewById(R.id.btn_fp);
        btnPaginasReco = (ImageButton) view.findViewById(R.id.btn_paginas);
        btnCalEscolar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otrosCalendarios = false;
                esMiCalendario = true;
                Intent intent = new Intent(getActivity(), WebView.class);
                startActivity(intent);
            }
        });
        btnOtrosCalendarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otrosCalendarios = true;
                esMiCalendario = false;
                Intent intent = new Intent(getActivity(), ListadoComunidadesCalendario.class);
                startActivity(intent);
            }
        });
        btnEnviarSugerencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esMiCalendario = false;
                otrosCalendarios = false;
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","soportemiagendafp@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sugerencia páginas recomendadas");
                intent.putExtra(Intent.EXTRA_TEXT, "Ejemplo: Me gustaría sugerir la página [página sugerida] para añadirla al listado de páginas recomendadas porque creo que es muy útil.");
                try {
                    startActivity(Intent.createChooser(intent, "Selecciona un gestor de correo electrónico: "));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnPaginasReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esMiCalendario = false;
                otrosCalendarios = false;
                Intent intent = new Intent(getActivity(), ListadoPaginasRecomendadas.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
