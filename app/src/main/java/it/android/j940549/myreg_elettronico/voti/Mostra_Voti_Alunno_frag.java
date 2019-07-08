package it.android.j940549.myreg_elettronico.voti;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

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
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.navigationDrawer.ScegliDettaglioNav;


public class Mostra_Voti_Alunno_frag extends Fragment {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    // private Spinner sel_annosc,  sel_quadr;
    private String annosc, quadr;
    private Alunno alunno;
    private Double valoremediatotale;
    private TextView mediaTotale;
    private TabLayout tabLayout;
    private Fragment_Elenco_Voti fragment_elenco_voti;
    private Fragment_Medie fragment_medie;
    String TAG_LOG = "Mostra_voti_alunno";
    Toolbar tb;

    public Mostra_Voti_Alunno_frag() {

    }


    public static Mostra_Voti_Alunno_frag newInstance(Alunno alunno, String annosc, String quadr) {
        Mostra_Voti_Alunno_frag fragment = new Mostra_Voti_Alunno_frag();
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
            this.quadr = getArguments().getString("quadrimestre");

        }

        Log.i(TAG_LOG, "arumets " + alunno.getCod_alunno() + ", " + annosc + ", " + quadr);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mostra__voti__alunno_frag, container, false);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), alunno, annosc, quadr);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                mViewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable("alunno", alunno);
        savedInstanceState.putString("annosc", annosc);
        savedInstanceState.putString("quadrimestre", quadr);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private String  unannosc, unquadrimestre;
        private Alunno alunnox;
        public SectionsPagerAdapter(FragmentManager fm, Alunno alunno, String unannosc, String unquadrimestre) {
            super(fm);
/*            this.unalunno=unalunno;
            this.unannosc=unannosc;
            this.unquadrimestre=unquadrimestre;*/

            alunnox = alunno;
            annosc = unannosc;
            quadr = unquadrimestre;
            Log.i("log_tag_arg fragpager", "arumets " + alunno.getCod_alunno() + ", " + unannosc + ", " + unquadrimestre);

        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();

            args.putSerializable("alunno", alunno);
            args.putString("annosc", annosc);
            args.putString("quadrimestre", quadr);
            //Fragment fragment = null;
            switch (position) {
                case 0: {
                    fragment_elenco_voti = Fragment_Elenco_Voti.newInstance(alunno, annosc, quadr);
                    //fragment_elenco_voti.setArguments(args);

                    return fragment_elenco_voti;
                    //break;
                }
                case 1: {
                    fragment_medie = new Fragment_Medie();
                    fragment_medie.setArguments(args);
                    return fragment_medie;

                }
                default:
                    Fragment fragment = null;
                    return fragment;
            }


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Lista dei Voti";
                case 1:
                    return "Medie per Materie";

            }
            return null;
        }

    }

}