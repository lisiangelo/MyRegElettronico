package it.android.j940549.myreg_elettronico.voti;

import android.content.Context;
import android.content.Intent;
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
import java.io.Serializable;
import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_Medie.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Medie#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Medie extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private String annosc,quadr;
    private Alunno alunno;
    ArrayList results = new ArrayList<DataObject_Medie>();
    String TAG_LOG="Fragment_medie";
    private OnFragmentInteractionListener mListener;

    public Fragment_Medie() {
        // Required empty public constructor
    }


    public static Fragment_Medie newInstance(Alunno alunno,String annosc,String quadr) {
        Fragment_Medie fragment = new Fragment_Medie ();
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

        Log.i("log_tag_arg","arumets "+alunno+", "+annosc+", "+quadr);

        caricaDati(alunno.getCod_alunno(),annosc,quadr);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        rootView = inflater.inflate(R.layout.fragment_fragment__medie, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(results);
        mRecyclerView.setAdapter(mAdapter);

        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                TextView texviev_materia = (TextView) v.findViewById(R.id.materia);
                String materia= texviev_materia.getText().toString();
                TextView texviev_media_materia = (TextView) v.findViewById(R.id.mediavoti);
                String media= texviev_media_materia.getText().toString();

                Log.i("log_tag", " Clicked on Item " + position);
                Intent intent = new Intent(getContext(),Voti_per_Materia.class);
                intent.putExtra("alunno", (Serializable)alunno);
                intent.putExtra("annosc", annosc);
                intent.putExtra("quadrimestre", quadr);
                intent.putExtra("materia", materia);
                intent.putExtra("media", media);
                startActivity(intent);

            }
        });

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
    private ArrayList<DataObject_Medie> getDataSet() {
        return results;
    }

    public void caricaDati(final String alunno, final String annosc, final String quadr) {
        Log.i("log_tag_argcar","arumets "+alunno+", "+annosc+", "+quadr);
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getRichiesta_media_materie(alunno,annosc,quadr);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG,"cursor: "+cursor.getCount());
            if (cursor.getCount() > 0) {
                results.clear();
                cursor.moveToPosition(0);
                do {

//                    materia, docente, avg(voto) as media

                    String a = cursor.getString(0);
                    String b = cursor.getString(1);
                    Double c = Math.rint(cursor.getDouble(2) * Math.pow(10, 2)) / 100;
                    //Double c = json_data.getDouble("media");
                    DataObject_Medie obj = new DataObject_Medie(a, b, c);
                    Log.i(TAG_LOG, "datobject inserito " + obj.getMateria() + " " + obj.getMediaVoti());
                    results.add(obj);

                } while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();


        Log.i(TAG_LOG, "results... " + results.size());


    }


}