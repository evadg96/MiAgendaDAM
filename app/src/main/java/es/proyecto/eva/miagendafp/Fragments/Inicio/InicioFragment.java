package es.proyecto.eva.miagendafp.Fragments.Inicio;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import es.proyecto.eva.miagendafp.R;


public class InicioFragment extends Fragment {
    int dia, mes, anyo, diaSemana;
    String sDia, sMes, sAnyo, sDiaSemana;
    TextView txtDia, txtMesAnyo, txtDiaSemana;
    public InicioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Date date = new Date(); // your date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        diaSemana = cal.get(Calendar.DAY_OF_WEEK);
        anyo = cal.get(Calendar.YEAR);
        mes = (cal.get(Calendar.MONTH)) + 1; // sumamos uno porque el primer mes lo toma como un 0
        dia = cal.get(Calendar.DAY_OF_MONTH);
        sDia = String.valueOf(dia);
        sDiaSemana =  String.valueOf(diaSemana);
        sMes =  String.valueOf(mes);
        sAnyo = String.valueOf(anyo);
        if (sDiaSemana.equals("1")){
            sDiaSemana = "Domingo";
        } else if (sDiaSemana.equals("2")){
            sDiaSemana = "Lunes";
        } else if (sDiaSemana.equals("3")){
            sDiaSemana = "Martes";
        } else if (sDiaSemana.equals("4")){
            sDiaSemana = "Miércoles";
        } else if (sDiaSemana.equals("5")){
            sDiaSemana = "Jueves";
        } else if (sDiaSemana.equals("6")){
            sDiaSemana = "Viernes";
        } else if (sDiaSemana.equals("7")){
            sDiaSemana = "Sábado";
        }

        if (sMes.equals("1")){
            sMes = "enero";
        } else if (sMes.equals("2")){
            sMes = "febrero";
        } else if (sMes.equals("3")){
            sMes = "marzo";
        } else if (sMes.equals("4")){
            sMes = "abril";
        } else if (sMes.equals("5")){
            sMes = "mayo";
        } else if (sMes.equals("6")){
            sMes = "junio";
        } else if (sMes.equals("7")){
            sMes = "julio";
        } else if (sMes.equals("8")){
            sMes = "agosto";
        } else if (sMes.equals("9")){
            sMes = "septiembre";
        } else if (sMes.equals("10")){
            sMes = "octubre";
        } else if (sMes.equals("11")){
            sMes = "noviembre";
        } else if (sMes.equals("12")){
            sMes = "diciembre";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        txtDia = (TextView) view.findViewById(R.id.txt_dia_actual);
        txtMesAnyo = (TextView) view.findViewById(R.id.txt_mes_anyo);
        txtDiaSemana = (TextView) view.findViewById(R.id.txt_dia_semana);
        txtDia.setText(sDia);
        txtDiaSemana.setText(sDiaSemana);
        txtMesAnyo.setText("de " + sMes + " de " + sAnyo);
        return view;
    }
}
