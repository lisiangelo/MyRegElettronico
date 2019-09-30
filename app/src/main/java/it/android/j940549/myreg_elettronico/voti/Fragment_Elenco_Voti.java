package it.android.j940549.myreg_elettronico.voti;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class Fragment_Elenco_Voti extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private String  annosc,quadr;
    private Alunno alunno;
    ArrayList results = new ArrayList<DataObject_elencoVoti>();
    String TAG_LOG="Fragment_Elenco_Voti";

    private OnFragmentInteractionListener mListener;

    public Fragment_Elenco_Voti() {
        // Required empty public constructor
    }


    public static Fragment_Elenco_Voti newInstance(Alunno alunno, String annosc,String quadr) {
        Fragment_Elenco_Voti fragment = new Fragment_Elenco_Voti();
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

        Log.i(TAG_LOG,"arumets "+alunno.getCod_alunno()+", "+annosc+", "+quadr);

        caricaDati(alunno.getCod_alunno(),annosc,quadr);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        rootView = inflater.inflate(R.layout.fragment_fragment__elenco__voti, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view_elenco_voti);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter_ElencoVoti(results);
        mRecyclerView.setAdapter(mAdapter);



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


    private ArrayList<DataObject_elencoVoti> getDataSet() {
        return results;
    }

    public void caricaDati(String alunno,String annosc,String quadrimestre) {
        Log.i(TAG_LOG,"arumets "+alunno+", "+annosc+", "+quadrimestre);

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getElenco_voti(alunno,annosc,quadrimestre);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG,"cursor: "+cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                results.clear();

                do {
//                    "alunno TEXT, annosc TEXT, quadrimestre TEXT, data INTEGER, materia TEXT, tipo TEXT, voto INTEGER, docente TEXT );");

                    String a = cursor.getString(5);
                    String b = new ConvertiData().da_Millis_a_String(cursor.getInt(4));
                    String c = cursor.getString(6);
                    Double d = cursor.getDouble(7);
                    DataObject_elencoVoti obj = new DataObject_elencoVoti(a, b, c,d );
                    Log.i(TAG_LOG, "datobject inserito " + obj.getData() + " " + obj.getVoto());

                    results.add(obj);

                }while (cursor.moveToNext());
                Log.i(TAG_LOG, "results... " + results.size());


            }else {
                Log.i(TAG_LOG, "results... " + results.size());
                results.clear();
                //            mAdapter = new MyRecyclerViewAdapter_ElencoVoti(results);
                //          mRecyclerView.setAdapter(mAdapter);

            }
        } catch (SQLException ex) {
            Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();


    }



}
