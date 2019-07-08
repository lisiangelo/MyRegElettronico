package it.android.j940549.myreg_elettronico.orari;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class CreaElencoMaterie extends AppCompatActivity {
    private Alunno alunno;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String TAG_LOG = "CercaElencoMaterie";
    ArrayList dataSet = new ArrayList<DataObject_Materia>();
    private EditText txtmateria, txtisnegnante;
    private Button inserisci;
    private TextView nomealunno, istituto, classe;
    private String annosc,quadr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_elenco_materie);
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

        txtmateria=findViewById(R.id.materia);
        txtisnegnante=findViewById(R.id.insegnante);
        inserisci=findViewById(R.id.button);


        mRecyclerView = (RecyclerView) findViewById(R.id.elenco_materie);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        caricaMaterie();
        inserisci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.button).getWindowToken(),0);
                String materia=txtmateria.getText().toString();
                String insegnante=txtisnegnante.getText().toString();
                String codAlunno=alunno.getCod_alunno();
                boolean res=inserisciMateria(codAlunno,materia,insegnante);

                if(res){
                    caricaMaterie();
                    mAdapter = new MyRecyclerViewAdapter_Elenco_Materie(dataSet, CreaElencoMaterie.this);
                    mRecyclerView.setAdapter(mAdapter);
                    txtmateria.setText("");
                    txtisnegnante.setText("");
                }
            }
        });
        mAdapter = new MyRecyclerViewAdapter_Elenco_Materie(dataSet, this);
        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment
        InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.button).getWindowToken(),0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.action_crea_elenco_materie,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.fatto){
        vaiaCreaOrario();
        }
        if(item.getItemId()==android.R.id.home){
            vaiaCreaOrario();
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable("alunno", alunno);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void vaiaCreaOrario(){
        Intent intent= new Intent(this, Crea_Mod_Orario.class);
        intent.putExtra("alunno",(Serializable) alunno);
        intent.putExtra("annosc", annosc);
        intent.putExtra("quadrimestre", quadr);
        startActivity(intent);
        finish();
    }
    private void caricaMaterie() {
        Log.i(TAG_LOG,"arumets "+alunno.getCod_alunno());

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();
            Cursor cursor = dbLayer.getMaterie(alunno.getCod_alunno());
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG,"cursor: "+cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                dataSet.clear();
                do {
//                    "alunno TEXT, annosc TEXT,  data INTEGER, tipoassenza TEXT, giustificazione TEXT);");
                    DataObject_Materia materia=new DataObject_Materia();
                    materia.setId_materia(cursor.getInt(0));
                    materia.setMateria(cursor.getString(2));
                    materia.setDocente(cursor.getString(3));
                    dataSet.add(materia);

                }while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            // Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
            Log.i(TAG_LOG,"error: "+ex.toString());
        }
        dbLayer.close();
    }

    private boolean inserisciMateria(String cod_alunno,String materia, String insegnante) {
        Log.i(TAG_LOG,"arumets inseriscimateria"+alunno.getCod_alunno());
        boolean esito=false;
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();
            esito= dbLayer.inserisciMaterie(cod_alunno,materia,insegnante);
            //             Cursor cursor = dbLayer.getAllVoti();

            Log.i(TAG_LOG,"eseguito insermateria: "+esito);
        } catch (SQLException ex) {
            Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();
        return esito;
    }

}

