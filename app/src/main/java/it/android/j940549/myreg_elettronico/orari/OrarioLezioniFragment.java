package it.android.j940549.myreg_elettronico.orari;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.model.Alunno;

public class OrarioLezioniFragment extends Fragment {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private String annosc, quadrimestre, quadr;
    private Alunno alunno;
    private FloatingActionButton mod_orario;


    public OrarioLezioniFragment() {

    }

    public static OrarioLezioniFragment newInstance(Alunno alunno, String annosc, String quadrimestre) {
        OrarioLezioniFragment fragment = new OrarioLezioniFragment();
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
        if (quadr.contains("I")) {
            quadrimestre = "1";
        }
        if (quadr.contains("II")) {
            quadrimestre = "2";
        }
        Log.i("log_tag_arg mostra voti", "arumets " + alunno + ", " + annosc + ", " + quadrimestre);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_orario_lezioni, container, false);

        mod_orario = view.findViewById(R.id.btn_modifica_orario);
        mod_orario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vai_Crea_mod_orario();
            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), alunno, annosc, quadrimestre);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable("alunno", alunno);
        savedInstanceState.putString("annosc", annosc);
        savedInstanceState.putString("quadrimestre", quadrimestre);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private String unannosc, unquadrimestre;
        private Alunno unalunno;

        public SectionsPagerAdapter(FragmentManager fm, Alunno unalunno, String unannosc, String unquadrimestre) {
            super(fm);
            this.unalunno = unalunno;
            this.unannosc = unannosc;
            this.unquadrimestre = unquadrimestre;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();

            args.putSerializable("alunno", unalunno);
            //Fragment fragment = null;
            switch (position) {
                case 0: {
                    args.putString("giornosettimana", "Lunedì");
                    Fragment_OrarioLezioni fragment_orarioLezioni = new Fragment_OrarioLezioni();
                    fragment_orarioLezioni.setArguments(args);
                    return fragment_orarioLezioni;
                    //break;
                }
                case 1: {
                    args.putString("giornosettimana", "Martedì");
                    Fragment_OrarioLezioni fragment_orarioLezioni = new Fragment_OrarioLezioni();
                    fragment_orarioLezioni.setArguments(args);
                    return fragment_orarioLezioni;
                    //break;
                }
                case 2: {
                    args.putString("giornosettimana", "Mercoledì");
                    Fragment_OrarioLezioni fragment_orarioLezioni = new Fragment_OrarioLezioni();
                    fragment_orarioLezioni.setArguments(args);
                    return fragment_orarioLezioni;
                    //break;
                }
                case 3: {
                    args.putString("giornosettimana", "Giovedì");
                    Fragment_OrarioLezioni fragment_orarioLezioni = new Fragment_OrarioLezioni();
                    fragment_orarioLezioni.setArguments(args);
                    return fragment_orarioLezioni;
                    //break;
                }
                case 4: {
                    args.putString("giornosettimana", "Venerdì");
                    Fragment_OrarioLezioni fragment_orarioLezioni = new Fragment_OrarioLezioni();
                    fragment_orarioLezioni.setArguments(args);
                    return fragment_orarioLezioni;
                    //break;
                }
                case 5: {
                    args.putString("giornosettimana", "Sabato");
                    Fragment_OrarioLezioni fragment_orarioLezioni = new Fragment_OrarioLezioni();
                    fragment_orarioLezioni.setArguments(args);
                    return fragment_orarioLezioni;
                    //break;
                }
                default:
                    Fragment fragment = null;
                    return fragment;
            }


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Lunedì";
                case 1:
                    return "Martedì";
                case 2:
                    return "Mercoledì";
                case 3:
                    return "Giovedì";
                case 4:
                    return "Venerdì";
                case 5:
                    return "Sabato";

            }
            return null;
        }
    }

    private void vai_Crea_mod_orario() {


        Intent intent = new Intent(getActivity(), Crea_Mod_Orario.class);//CreaElencoMaterie.class);
        intent.putExtra("alunno", (Serializable)alunno);
        intent.putExtra("annosc", annosc);
        intent.putExtra("quadrimestre",quadr);

        startActivity(intent);
        getActivity().finish();


    }
}
