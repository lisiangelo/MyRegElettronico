package it.android.j940549.myreg_elettronico.navigationDrawer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.model.Comunicazione;
import it.android.j940549.myreg_elettronico.model.ConvertiData;
import it.android.j940549.myreg_elettronico.orari.CreaElencoMaterie;
import it.android.j940549.myreg_elettronico.orari.Crea_Mod_Orario;
import it.android.j940549.myreg_elettronico.orari.DataObject_Materia;
import it.android.j940549.myreg_elettronico.orari.MyRecyclerViewAdapter_Elenco_Materie;

public class Mostra_Comunicazioni extends AppCompatActivity {

    private Alunno alunno;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String TAG_LOG = "MostraComunicazioni";
    ArrayList dataSet = new ArrayList<Comunicazione>();
    private Button inserisci;
    private TextView nomealunno, istituto, classe;
    private String annosc,quadr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra__comunicazioni);
        if (savedInstanceState != null) {
            alunno = (Alunno) savedInstanceState.getSerializable("alunno");
            annosc = savedInstanceState.getString("annosc");
            quadr = savedInstanceState.getString("quadrimestre");
        } else {
            alunno = (Alunno) getIntent().getSerializableExtra("alunno");
            annosc = getIntent().getStringExtra("annosc");
            quadr = getIntent().getStringExtra("quadrimestre");

        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nomealunno = (TextView) findViewById(R.id.nome_alunno);
        istituto = (TextView) findViewById(R.id.nome_istituto);
        classe = (TextView) findViewById(R.id.classe);

        nomealunno.setText(alunno.getNome_alunno().toUpperCase());
        istituto.setText(alunno.getTipo() + " - " + alunno.getNome_istituto());
        classe.setText(alunno.getClasse_sez());


        mRecyclerView = (RecyclerView) findViewById(R.id.elenco_comunicazioni);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        caricaComunicazioni();

        mAdapter = new MyRecyclerViewAdapter_ElencoComunicazioni(dataSet);
        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //getMenuInflater().inflate(R.menu.action_crea_elenco_materie,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==android.R.id.home){
            Intent intent= new Intent(this, ScegliDettaglioNav.class);
            intent.putExtra("alunno",(Serializable) alunno);
            intent.putExtra("annosc", annosc);
            intent.putExtra("quadrimestre", quadr);
            intent.putExtra("pagina",5);
            startActivity(intent);
            finish();
        }
        return true;
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

    private void caricaComunicazioni() {
        Log.i(TAG_LOG,"arumets "+alunno.getCod_alunno());

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();
            Cursor cursor = dbLayer.getComunicazioni(alunno.getCod_alunno());
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG,"cursor: "+cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                dataSet.clear();
                do {
//                    "alunno TEXT, annosc TEXT,  data INTEGER, tipoassenza TEXT, giustificazione TEXT);");
                    Comunicazione comunicazione=new Comunicazione();
                    comunicazione.setData(cursor.getInt(2));
                    comunicazione.setTesto(cursor.getString(3));
                    dataSet.add(comunicazione);

                }while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            // Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
            Log.i(TAG_LOG,"error: "+ex.toString());
        }
        dbLayer.close();
    }


}

