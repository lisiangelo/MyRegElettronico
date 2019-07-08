package it.android.j940549.myreg_elettronico.argomenti;

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
import it.android.j940549.myreg_elettronico.model.ConvertiData;
import it.android.j940549.myreg_elettronico.voti.MyRecyclerViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_Elenco_Compiti.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Elenco_Compiti#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Elenco_Compiti extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private String cod_alunno,annosc,quadr;
    ArrayList results = new ArrayList<DataObject_Compiti>();
    String TAG_LOG="Fragment_elenco_compiti";

//    private OnFragmentInteractionListener mListener;

    public Fragment_Elenco_Compiti() {
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

    public static Fragment_Elenco_Compiti newInstance(String cod_alunno,String annosc, String quadr) {
        Fragment_Elenco_Compiti fragment = new Fragment_Elenco_Compiti();
        Bundle args = new Bundle();
        args.putString("cod_alunno", cod_alunno);
        args.putString("annosc", annosc);
        args.putString("quadrimestre", quadr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.cod_alunno = getArguments().getString("cod_alunno");
            this.annosc = getArguments().getString("annosc");
            this.quadr= getArguments().getString("quadrimestre");
        }
        Log.i("log_tag_arg","arumets "+cod_alunno+", "+annosc+", "+quadr);
        caricaDati(cod_alunno,annosc,quadr);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        rootView = inflater.inflate(R.layout.fragment_fragment__elenco__compiti, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view_elenco_compiti);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyRecyclerViewAdapter_ElencoCompiti(results);
        mRecyclerView.setAdapter(mAdapter);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
/*        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
  */  }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
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


    private ArrayList<DataObject_Compiti> getDataSet() {
        return results;
    }

    public void caricaDati(String alunno,String annosc,String quadrimestre) {
        Log.i("log_tag_argcar","arumets "+alunno+", "+annosc+", "+quadrimestre);

        results.removeAll(results);
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getRichesta_compiti(alunno,annosc);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG,"cursor: "+cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                results.clear();

                do {
//                    "alunno TEXT, annosc TEXT,  data INTEGER, argomento TEXT, compiti TEXT, assenze TEXT, noteprof  TEXT,notediscip  TEXT );");

                    String a = cursor.getString(0);
                    String b = cursor.getString(1);
                    DataObject_Compiti obj = new DataObject_Compiti(a,b );
                    Log.i("log_tag", "datobject inserito " + obj.getData() );

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