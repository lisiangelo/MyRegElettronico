package it.android.j940549.myreg_elettronico.argomenti;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.model.Alunno;


public class Mostra_Compiti_Argoemnti_Frag extends Fragment{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private String annosc,quadrimestre,quadr;
    private Alunno alunno;
    public Mostra_Compiti_Argoemnti_Frag(){

    }
    public static Mostra_Compiti_Argoemnti_Frag newInstance(Alunno alunno, String annosc, String quadrimestre) {
        Mostra_Compiti_Argoemnti_Frag fragment = new Mostra_Compiti_Argoemnti_Frag ();
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
            this.quadr= getArguments().getString("quadrimestre");

        }
        if(quadr.contains("I")){
            quadrimestre="1";
        }
        if(quadr.contains("II")){
            quadrimestre="2";
        }
        Log.i("log_tag_arg mostra voti","arumets "+alunno.getCod_alunno()+", "+annosc+", "+quadrimestre);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_mostra__compiti,container,false);



       /* TextView nomealunno= (TextView) view.findViewById(R.id.nomealunno_compiti);
            nomealunno.setText(alunno.toUpperCase());
*/
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(),alunno.getCod_alunno(),annosc,quadrimestre);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.container_compiti);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs_compiti);
        tabLayout.setupWithViewPager(mViewPager);

return view;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private String uncod_alunno,unannosc,unquadrimestre;
        public SectionsPagerAdapter(FragmentManager fm,String uncod_alunno,String unannosc,String unquadrimestre ) {
            super(fm);
            this.uncod_alunno=uncod_alunno;
            this.unannosc=unannosc;
            this.unquadrimestre=unquadrimestre;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();

            args.putString("cod_alunno", uncod_alunno);
            args.putString("annosc", unannosc);
            args.putString("quadrimestre", unquadrimestre);
            //Fragment fragment = null;
            switch (position) {
                case 0: {
                    Fragment_Elenco_Argomenti fragment_elenco_argomenti = new Fragment_Elenco_Argomenti();
                    fragment_elenco_argomenti.setArguments(args);

                    return fragment_elenco_argomenti;
                    //break;
                }
                case 1: {
                    Fragment_Elenco_Compiti fragment_elenco_compiti = new Fragment_Elenco_Compiti();
                    fragment_elenco_compiti.setArguments(args);

                    return fragment_elenco_compiti;
                    //break;

                }
                default:
                    Fragment fragment=null;
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
                    return "Lista degli Argomenti";
                case 1:
                    return "Lista dei Compiti";

            }
            return null;
        }
    }

}