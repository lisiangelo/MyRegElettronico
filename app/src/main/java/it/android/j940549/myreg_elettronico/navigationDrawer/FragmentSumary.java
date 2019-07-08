package it.android.j940549.myreg_elettronico.navigationDrawer;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.assenze.DataObject_Assenze;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.model.ConvertiData;
import it.android.j940549.myreg_elettronico.model.Credito;
import it.android.j940549.myreg_elettronico.orari.Crea_Mod_Orario;
import it.android.j940549.myreg_elettronico.voti.DataObject_elencoVoti;


public class FragmentSumary extends Fragment {
    private Alunno alunno;
    private String cod_alunno, annosc, quadr, classe_sez, tipo_ist;
    private Double valoremediatotale;
    private TextView mediaTotale, crediti, datavotosom, materiasom, tiposom,
            votosom, dataassenza, tipoassenza, giustificazione, data_comun, testo_comun;
    String TAG_LOG = "FragmentSummary";
    private PieChart pieChat_media_totale, pieChart_ultimoVoto;
    Button btn_mostra_comunicaioni;

    public FragmentSumary() {
        // Required empty public constructor
    }

    public static FragmentSumary newInstance(Alunno alunno, String annosc, String quadrimestre) {
        FragmentSumary fragment = new FragmentSumary();
        Bundle args = new Bundle();
        args.putSerializable("alunno", alunno);
        args.putString("annosc", annosc);
        args.putString("quadrimestre", quadrimestre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.alunno = (Alunno) getArguments().getSerializable("alunno");
            this.annosc = getArguments().getString("annosc");
            this.quadr = getArguments().getString("quadrimestre");
        }
        if (alunno != null) {
            cod_alunno = alunno.getCod_alunno();
            classe_sez = alunno.getClasse_sez();
            tipo_ist = alunno.getTipo();
        }

        Log.i(TAG_LOG, "arumets " + alunno.getCod_alunno() + "..." + cod_alunno + ", " + annosc + ", " + quadr + ", " + classe_sez);
        //   caricaDati(alunno,annosc,quadrimestre);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;

        view = inflater.inflate(R.layout.fragment_fragment_sumary, container, false);


        mediaTotale = (TextView) view.findViewById(R.id.mediatotale_summary);
        crediti = (TextView) view.findViewById(R.id.crediti);


        //   pieChart_ultimoVoto= (PieChart) view.findViewById(R.id.pie_ultimo_voto);
        pieChat_media_totale = (PieChart) view.findViewById(R.id.pie_voto_media);


        datavotosom = (TextView) view.findViewById(R.id.data_voto_sommario);
        materiasom = (TextView) view.findViewById(R.id.materia_voto_sommario);
        tiposom = (TextView) view.findViewById(R.id.tipo_voto_sommario);
        votosom = (TextView) view.findViewById(R.id.voto_sommario);

        dataassenza = (TextView) view.findViewById(R.id.data_assenza_sommario);
        tipoassenza = (TextView) view.findViewById(R.id.tipo_assenza_sommario);
        giustificazione = (TextView) view.findViewById(R.id.giustificazione_sommario);

        data_comun = (TextView) view.findViewById(R.id.data_ultima_comunicazione);
        testo_comun = (TextView) view.findViewById(R.id.testo_ultima_comunicazione);
        btn_mostra_comunicaioni = view.findViewById(R.id.btn_mostra_comun);
        btn_mostra_comunicaioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaiaMostraComunicazioni();
            }
        });
        caricaUltimoVoto(cod_alunno, annosc, quadr);
        caricaUtimaAssenza(cod_alunno, annosc, quadr);
        caricaMedia(cod_alunno, annosc, quadr);
        caricaUltimaComunicazione(cod_alunno);

        return view;
    }

    public void caricaMedia(String alunno, String annosc, String quadrimestre) {
        Log.i(TAG_LOG, "arumets " + alunno + ", " + annosc + ", " + quadrimestre);

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getMedia_totale(alunno, annosc, quadrimestre);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG, "cursor: " + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    valoremediatotale = Math.rint(cursor.getDouble(0) * Math.pow(10, 2)) / 100;

                } while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();

        Log.i(TAG_LOG, "media_totale: " + valoremediatotale);
        mediaTotale.setText("MEDIA TOTALE = " + valoremediatotale);

        if (Float.parseFloat("" + valoremediatotale) < 6) {
            mediaTotale.setTextColor(Color.RED);
        } else if (Float.parseFloat("" + valoremediatotale) >= 6 && Float.parseFloat("" + valoremediatotale) < 7) {
            mediaTotale.setTextColor(Color.rgb(255, 165, 0));
        } else if (Float.parseFloat("" + valoremediatotale) >= 7 && Float.parseFloat("" + valoremediatotale) < 8) {
            mediaTotale.setTextColor(Color.YELLOW);
        } else if (Float.parseFloat("" + valoremediatotale) >= 8) {
            mediaTotale.setTextColor(Color.GREEN);
        }


        creaPie(pieChat_media_totale, valoremediatotale);
        Credito credito = new Credito();
        if (!tipo_ist.toUpperCase().contains("COMPRENSIVO")) {
            if (classe_sez.contains("3")) {
                credito.setAnno(3);
                credito.setAnno_prev(0);
            }
            if (classe_sez.contains("4")) {
                credito.setAnno(4);
                credito.setAnno_prev(0);
            }
            if (classe_sez.contains("5")) {
                credito.setAnno(5);
                credito.setAnno_prev(0);
            }
        }
        if (valoremediatotale == 6) {
            credito.setVoto(1);
        }
        if (valoremediatotale >= 6 && valoremediatotale <= 6.99) {
            credito.setVoto(2);
        }
        if (valoremediatotale >= 7 && valoremediatotale <= 7.99) {
            credito.setVoto(3);
        }
        if (valoremediatotale >= 8 && valoremediatotale <= 8.99) {
            credito.setVoto(4);
        }
        if (valoremediatotale >= 9 && valoremediatotale == 10) {
            credito.setVoto(5);
        }
        crediti.setText("Crediti corrispondenti:\n " + credito.calcolaCredito());
    }


// carica ultimo voto

    public void caricaUltimoVoto(String alunno, String annosc, String quadrimestre) {
        Log.i(TAG_LOG, "arumets " + alunno + ", " + annosc + ", " + quadrimestre);

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getUltimo_voto(alunno, annosc, quadrimestre);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG, "cursor: " + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    //    "alunno TEXT, annosc TEXT, quadrimestre TEXT, data INTEGER, materia TEXT, tipo TEXT, voto INTEGER, docente TEXT );");

                    datavotosom.setText(new ConvertiData().da_Millis_a_String(cursor.getInt(4)));
                    materiasom.setText(cursor.getString(5));//+"   "+ cursor.getDouble(7)+ "   "+cursor.getString(6));
                    tiposom.setText(cursor.getString(6));
                    String voto = "" + cursor.getDouble(7);

//                creaPie(pieChart_ultimoVoto,cursor.getDouble(7));

                    votosom.setText("" + voto);
                    if (Float.parseFloat(voto) < 6) {
                        materiasom.setTextColor(Color.RED);
                        votosom.setTextColor(Color.RED);
                    } else if (Float.parseFloat(voto) >= 6 && Float.parseFloat(voto) < 7) {
                        materiasom.setTextColor(Color.rgb(255, 165, 0));
                        votosom.setTextColor(Color.rgb(255, 165, 0));
                    } else if (Float.parseFloat(voto) >= 7 && Float.parseFloat(voto) < 8) {
                        materiasom.setTextColor(Color.YELLOW);
                        votosom.setTextColor(Color.YELLOW);
                    } else if (Float.parseFloat(voto) >= 8) {
                        materiasom.setTextColor(Color.GREEN);
                        votosom.setTextColor(Color.GREEN);
                    }
                } while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();
    }

//carica ultima assenza

    public void caricaUtimaAssenza(String alunno, String annosc, String quadrimestre) {
        Log.i("log_tag_argcar", "arumets " + alunno + ", " + annosc + ", " + quadrimestre);

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getUltima_assenza(alunno, annosc);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG, "cursor: " + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
//                    "alunno TEXT, annosc TEXT,  data INTEGER, tipoassenza TEXT, giustificazione TEXT);");

                    dataassenza.setText(new ConvertiData().da_Millis_a_String(cursor.getInt(3)));
                    tipoassenza.setText(cursor.getString(4));
                    String giust = cursor.getString(5);
                    Log.i("giust", giust);
                    if (giust.contains("Si")) {
                        // giust="SI";
                        giustificazione.setTextColor(Color.GREEN);
                    } else {
                        giust = "NO";
                        giustificazione.setTextColor(Color.RED);
                    }
                    giustificazione.setText(giust);


                } while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();
    }

    public void caricaUltimaComunicazione(String alunno) {
        Log.i("log_tag_argcar", "arumets " + alunno);

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.get_Ultima_Comunicazioni(alunno);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG, "cursor: " + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
//                    "alunno TEXT, annosc TEXT,  data INTEGER, tipoassenza TEXT, giustificazione TEXT);");

                    data_comun.setText(new ConvertiData().da_Millis_a_String(cursor.getInt(2)));
                    testo_comun.setText(cursor.getString(3));
                    btn_mostra_comunicaioni.setVisibility(View.VISIBLE);
                } while (cursor.moveToNext());
            }else{
                testo_comun.setText("NESSUNA COMUNICAZIONE");testo_comun.setTextSize(18);
                btn_mostra_comunicaioni.setVisibility(View.INVISIBLE);
            }
        } catch (SQLException ex) {
            Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();
    }


    private void creaPie(PieChart pieChart, double voto) {

        float voto_f = (float) voto;
        ArrayList<Entry> Yvals = new ArrayList<>();
        Yvals.add(new Entry(10 - voto_f, 0));
        Yvals.add(new Entry(voto_f, 1));
        PieDataSet dataset = new PieDataSet(Yvals, " ");
        dataset.setDrawValues(false);

        ArrayList year = new ArrayList<>();
        year.add("");
        year.add("media");
        PieData data = new PieData(year, dataset);
        data.setDrawValues(false);

        pieChart.setData(data);
        data.setValueTextSize(10);//Formatter(new PercentFormatter());
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.TRANSPARENT);
        if (voto_f < 6) {
            colors.add(Color.RED);
            pieChart.setCenterTextColor(Color.RED);
        } else if (voto_f >= 6 && voto_f < 7) {
            colors.add(Color.rgb(255, 165, 0));
            pieChart.setCenterTextColor(Color.rgb(255, 165, 0));
        } else if (voto_f >= 7 && voto_f < 8) {
            colors.add(Color.YELLOW);
            pieChart.setCenterTextColor(Color.YELLOW);
        } else if (voto_f >= 8) {
            colors.add(Color.GREEN);
            pieChart.setCenterTextColor(Color.GREEN);
        }

        dataset.setColors(colors);
        pieChart.setData(data);
        pieChart.setCenterText("" + voto_f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setDescription("");
        pieChart.setRotationAngle(270f);
        pieChart.setDrawSliceText(false);
        pieChart.setPadding(0, 0, 0, 0);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.animateX(1500);

    }

    private void vaiaMostraComunicazioni() {

        Intent intent = new Intent(getActivity(), Mostra_Comunicazioni.class);//CreaElencoMaterie.class);
        intent.putExtra("alunno", (Serializable) alunno);
        intent.putExtra("annosc", annosc);
        intent.putExtra("quadrimestre", quadr);

        startActivity(intent);

    }
}





