package it.android.j940549.myreg_elettronico.orari;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_OrarioLezioni.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_OrarioLezioni#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_OrarioLezioni extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String TAG_LOG="Fragment_OrarioLezioni";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private String giornoSettimana;
    private Alunno alunno;
    private String orarioMelanie[][]={
            {"Inglese-Pappalardo","Inglese-Pappalardo", "Geostoria-D'Innocenzo","Geostoria-D'Innocenzo" },
            {"Latino-Pernaferri","Spagnolo-Mosticone","Scienze Motorie-x","Cinese-Monteduro"},
            {"Matematica-Cavallini", "Inglese-Pappalardo","Scienze-Brunetti","Geostoria-D'Innocenzo", "Antologia-Pernaferri"},
            {"Latino-Pernaferri","Italiano-Pernaferri","Inglese-Pappalardo","Spagnolo-Mosticone","Spagnolo-Mosticone"},
            {"Italiano-Pernaferri","Italiano-Pernaferri","Matematica-Cavallini","Matematica-Cavallini", "Scienze Motorie-x"},
            {"Religione-Anelli", "Cinese-Monteduro", "Cinese-Monteduro", "Scienze-Brunetti"}
    };
    private String orarioManuel[][]={
            {"Inglese-Presti", "Tecnolog.Proget.ne-Lamberti","Tecnolog.Proget.ne-Lamberti","Sistemi-Fantaccione","Italiano/Storia-Nilo","Italiano/Storia-Nilo"},
            {"Elettronica-Ferrari/Zecch.","Elettronica-Ferrari/Zecch.","Sistemi-Fantaccione","Sistemi-Fantaccione", "Inglese-Presti", "Italiano/Storia-Nilo","Italiano/Storia-Nilo"},
            {"Tecnolog.Proget.ne-Lamberti", "Elettronica-Ferrari", "Italiano/Storia-Nilo","Elettronica-Ferrari","Matematica-Manca","Educazione Fisica-Tagliavento"},
            {"Sistemi-Fantaccione","Sistemi-Fantaccione","Inglese-Presti","Matematica-Manca","Educazione Fisica-Tagliavento","Religione-Toselli","Elettronica-Ferrari"},
            {"Tecnolog.Proget.ne-Lamberti","Tecnolog.Proget.ne-Lamberti","Elettronica-Ferrari","Matematica-Manca","Matematica-Manca","Italiano/Storia-Nilo"}
    };

    ArrayList results = new ArrayList<DataObject_OrarioLezioni>();

    private OnFragmentInteractionListener mListener;

    public Fragment_OrarioLezioni() {
        // Required empty public constructor
    }

    public static Fragment_OrarioLezioni newInstance(Alunno alunno,  String giornoSettimana) {
        Fragment_OrarioLezioni fragment = new Fragment_OrarioLezioni();
        Bundle args = new Bundle();
        args.putSerializable("alunno", alunno);
        args.putString("giornosettimana", giornoSettimana);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.alunno = (Alunno) getArguments().getSerializable("alunno");
           this.giornoSettimana = getArguments().getString("giornosettimana");
        }
        Log.i("log_tag_arg","arumets "+alunno.getCod_alunno()+" -- "+giornoSettimana);

        caricaDati(alunno.getCod_alunno(), giornoSettimana);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        rootView = inflater.inflate(R.layout.fragment_fragment__orariolezioni, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view_orario);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter_OrarioLezioni(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

//        TextView nomealunno = (TextView) rootView.findViewById(R.id.nomealunno_orarioLezioni);
  //      nomealunno.setText(alunno.toUpperCase());


        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private ArrayList<DataObject_OrarioLezioni> getDataSet() {
        return results;
    }

    public void caricaDati(String cod_alunno, String giornoSettimana) {
        Log.i("log_tag_argcar","arumets "+alunno+ "----"+ giornoSettimana);

        int gg=0;
        switch (giornoSettimana) {
            case "Lunedì":
                gg=1;
                break;
            case "Martedì":
                gg=2;
                break;
            case "Mercoledì":
                gg=3;
                break;
            case "Giovedì":
                gg=4;
                break;
            case "Venerdì":
                gg=5;
                break;
            case "Sabato":
                gg=6;
                break;
        }

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getOrario_alunno(cod_alunno,gg);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG,"cursor: "+cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                results.clear();
                do {
//                    "alunno TEXT, annosc TEXT,  data INTEGER, tipoassenza TEXT, giustificazione TEXT);");
                    DataObject_OrarioLezioni orario=new DataObject_OrarioLezioni();
                    orario.setMateria(cursor.getString(4));
                    orario.setDocente(cursor.getString(5));
                    results.add(orario);

                }while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            // Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
            Log.i(TAG_LOG,"error: "+ex.toString());
        }
        dbLayer.close();



        Log.i("result"," result"+results.size()+"---"+getDataSet().size());

    }



}
