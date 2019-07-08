package it.android.j940549.myreg_elettronico.voti;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.model.ConvertiData;

public class Voti_per_Materia extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private String annosc, quadr, materia, media;
    private Alunno alunno;
    private LineChart lineChart;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    ArrayList results = new ArrayList<DataObject_Voti_per_Materia>();
    String TAG_LOG = "Voti_per_materie";
    Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voti_per__materia);

        tb = (Toolbar) findViewById(R.id.toolbarvotimateria);
        collapsingToolbarLayout=findViewById(R.id.collapsingtl);
        collapsingToolbarLayout.setTitleEnabled(false);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            alunno = (Alunno) savedInstanceState.getSerializable("alunno");
            annosc = savedInstanceState.getString("annosc");
            quadr = savedInstanceState.getString("quadrimestre");
            materia = savedInstanceState.getString("materia");
            media = savedInstanceState.getString("media");

        } else {

            alunno = (Alunno) getIntent().getSerializableExtra("alunno");
            annosc = getIntent().getStringExtra("annosc");
            quadr = getIntent().getStringExtra("quadrimestre");
            materia = getIntent().getStringExtra("materia");
            media = getIntent().getStringExtra("media");

        }


        TextView nomealunno = (TextView) findViewById(R.id.alunno);
        nomealunno.setText(alunno.getNome_alunno().toUpperCase());

        TextView titolo = (TextView) findViewById(R.id.titolo_materia);
        titolo.setText(materia);
        TextView textmedia = (TextView) findViewById(R.id.media_materia);
        textmedia.setText("MEDIA: " + media);
        lineChart = (LineChart) findViewById(R.id.linechart);


        caricaDati(alunno.getCod_alunno(), annosc, quadr, materia);
        Log.i("log_tag", "results... prima setadapter" + results.size());

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_voti_per_materie);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter_Voti_Materia(results);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setFocusable(false);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable("alunno", alunno);
        savedInstanceState.putString("annosc", annosc);
        savedInstanceState.putString("quadrimestre", quadr);
        savedInstanceState.putString("materia", materia);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private ArrayList<DataObject_Voti_per_Materia> getDataSet() {
        return results;
    }

    public void caricaDati(String alunno, String annosc, String quadr, String materia) {
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();
            Cursor cursor = dbLayer.getDettaglio_materia(alunno, annosc, quadr, materia);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG, "cursor: " + cursor.getCount());
            if (cursor.getCount() > 0) {
                results.clear();
                cursor.moveToPosition(0);
                do {

//               "alunno TEXT, annosc TEXT, quadrimestre TEXT, data INTEGER, materia TEXT, tipo TEXT, voto INTEGER, docente TEXT );");
//                materia, docente, avg(voto) as media

                    String a = new ConvertiData().da_Millis_a_String(cursor.getInt(4));
                    String b = cursor.getString(6);
                    Double c = cursor.getDouble(7);
                    DataObject_Voti_per_Materia obj = new DataObject_Voti_per_Materia(a, b, c);
                    Log.i("log_tag", "datobject inserito " + obj.getData() + " " + obj.getVoto());
                    results.add(obj);

                } while (cursor.moveToNext());
                Log.i(TAG_LOG, "results... " + results.size());

                creaPie(lineChart, results);

            } else {
                Log.i(TAG_LOG, "results... " + results.size());
                results.clear();

            }
        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

    }

    private void creaPie(LineChart lineChart, ArrayList<DataObject_Voti_per_Materia> voti) {


        ArrayList<Entry> entries_voti = new ArrayList<>();
        ArrayList<Entry> entries_6 = new ArrayList<>();
        ArrayList<Entry> entries_10 = new ArrayList<>();
        int index = 0;
        for (int i = voti.size(); i >= 1; i--) {

            Log.i(TAG_LOG, "" + voti.get(i - 1));
            entries_voti.add(new Entry(Float.parseFloat("" + voti.get(i - 1).getVoto()), index));
            entries_6.add(new Entry(6.0f, index));
            entries_10.add(new Entry(10.0f, index));
            index++;
        }

        LineDataSet dataset = new LineDataSet(entries_voti, " ");
        dataset.setColor(Color.GREEN);
        dataset.setLineWidth(2);
        LineDataSet dataset2 = new LineDataSet(entries_6, "");
        dataset2.setColor(Color.RED);
        dataset2.setLineWidth(2);
        LineDataSet dataset3 = new LineDataSet(entries_10, "");
        dataset3.setColor(Color.TRANSPARENT);
        dataset3.setLineWidth(0);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataset); // add the datasets
        dataSets.add(dataset2);
        dataSets.add(dataset3);

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 1; i <= voti.size(); i++) {

            Log.i(TAG_LOG, "" + voti.get(i - 1));
            labels.add("");//voti.get(i-1).getData());
        }

        LineData data = new LineData(labels, dataSets);
        data.setValueTextColor(Color.YELLOW);

        lineChart.setData(data);
        lineChart.setDescriptionColor(Color.YELLOW);
        lineChart.setMaxVisibleValueCount(10);
        lineChart.animateX(3000);

/*        pieChart.setData(data);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterText(""+voto_f);
        pieChart.setDescription("");
        pieChart.setRotationAngle(270f);
        pieChart.setDrawSliceText(false);
        pieChart.setPadding(0,0,0,0);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.animateX(1500);*/

    }
}

