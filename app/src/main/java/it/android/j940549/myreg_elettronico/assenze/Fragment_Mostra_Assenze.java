package it.android.j940549.myreg_elettronico.assenze;

import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.model.ConvertiData;


public class Fragment_Mostra_Assenze extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private String annosc,quadr;
    private Alunno alunno;
    ArrayList results = new ArrayList<DataObject_Assenze>();
    android.support.v7.widget.Toolbar tb;
    String TAG_LOG="Fragment_mostra_assenze";

    public Fragment_Mostra_Assenze() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @ param param1 Parameter 1.
     * @ param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Elenco_Voti.
     */
    // TODO: Rename and change types and number of parameters

    public static Fragment_Mostra_Assenze newInstance(Alunno alunno, String annosc, String quadr) {
        Fragment_Mostra_Assenze fragment = new Fragment_Mostra_Assenze();
        Bundle args = new Bundle();
        args.putSerializable("alunno", alunno);
        args.putString("annosc", annosc);
        args.putString("quadrimestre", quadr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.alunno = (Alunno) getArguments().getSerializable("alunno");
            this.annosc = getArguments().getString("annosc");
            this.quadr= getArguments().getString("quadrimestre");
        }
        Log.i("log_tag_arg","arumets "+alunno.getCod_alunno()+", "+annosc+", "+quadr);
        caricaDati(alunno.getCod_alunno(),annosc,quadr);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        rootView = inflater.inflate(R.layout.activity_mostra__assenze, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view_elenco_assenze);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        /*TextView nomealunno= (TextView) rootView.findViewById(R.id.nomealunno_assenze);
        nomealunno.setText(alunno.toUpperCase());*/
        mAdapter = new MyRecyclerViewAdapter_ElencoAssenze(results);
        mRecyclerView.setAdapter(mAdapter);


        return rootView;
    }

    private ArrayList<DataObject_Assenze> getDataSet() {
        return results;
    }

    public void caricaDati(String alunno,String annosc,String quadr) {
        Log.i("log_tag_argcar","arumets "+alunno+", "+annosc+", "+quadr);

        results.removeAll(results);
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getRichiesta_assenze(alunno,annosc);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG,"cursor: "+cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                results.clear();

                do {
//                         "alunno TEXT, annosc TEXT,  data INTEGER, tipoassenza TEXT, giustificazione TEXT);");
                    String a = new ConvertiData().da_Millis_a_String(cursor.getInt(3));
                    String b = cursor.getString(4);
                    String c = cursor.getString(5);
                    DataObject_Assenze obj = new DataObject_Assenze(a, b, c );
                    Log.i("log_tag", "datobject inserito " + obj.getData() + " " + obj.getTipoassenza());

                    results.add(obj);

                }while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();


        Log.i(TAG_LOG, "results... " + results.size());


    }
    }

