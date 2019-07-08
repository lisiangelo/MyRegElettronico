package it.android.j940549.myreg_elettronico.orari;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.model.Orario_giornaliero;
import it.android.j940549.myreg_elettronico.navigationDrawer.ScegliDettaglioNav;

public class Crea_Mod_Orario extends AppCompatActivity {
    private GridLayout gridMaterie, gridOrario;
    private Alunno alunno;
    private String TAG_LOG = "Crea_Mod-Orario";
    private ArrayList<DataObject_Materia> materie = new ArrayList<>();
    private ArrayList<Orario_giornaliero> orario = new ArrayList<>();
    private TextView nomealunno, istituto, classe;
    private String annosc, quadr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea__mod__orario);
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
        gridMaterie = findViewById(R.id.grigliaMaterie);
        caricaMaterie();
        gridOrario = findViewById(R.id.grigliaOrario);
        findViewById(R.id.lunedi1).setOnDragListener(new MyDragListener());
        findViewById(R.id.lunedi2).setOnDragListener(new MyDragListener());
        findViewById(R.id.lunedi3).setOnDragListener(new MyDragListener());
        findViewById(R.id.lunedi4).setOnDragListener(new MyDragListener());
        findViewById(R.id.lunedi5).setOnDragListener(new MyDragListener());
        findViewById(R.id.lunedi6).setOnDragListener(new MyDragListener());
        findViewById(R.id.lunedi7).setOnDragListener(new MyDragListener());

        findViewById(R.id.martedì1).setOnDragListener(new MyDragListener());
        findViewById(R.id.martedì2).setOnDragListener(new MyDragListener());
        findViewById(R.id.martedì3).setOnDragListener(new MyDragListener());
        findViewById(R.id.martedì4).setOnDragListener(new MyDragListener());
        findViewById(R.id.martedì5).setOnDragListener(new MyDragListener());
        findViewById(R.id.martedì6).setOnDragListener(new MyDragListener());
        findViewById(R.id.martedì7).setOnDragListener(new MyDragListener());

        findViewById(R.id.mercoledì1).setOnDragListener(new MyDragListener());
        findViewById(R.id.mercoledì2).setOnDragListener(new MyDragListener());
        findViewById(R.id.mercoledì3).setOnDragListener(new MyDragListener());
        findViewById(R.id.mercoledì4).setOnDragListener(new MyDragListener());
        findViewById(R.id.mercoledì5).setOnDragListener(new MyDragListener());
        findViewById(R.id.mercoledì6).setOnDragListener(new MyDragListener());
        findViewById(R.id.mercoledì7).setOnDragListener(new MyDragListener());

        findViewById(R.id.giovedì1).setOnDragListener(new MyDragListener());
        findViewById(R.id.giovedì2).setOnDragListener(new MyDragListener());
        findViewById(R.id.giovedì3).setOnDragListener(new MyDragListener());
        findViewById(R.id.giovedì4).setOnDragListener(new MyDragListener());
        findViewById(R.id.giovedì5).setOnDragListener(new MyDragListener());
        findViewById(R.id.giovedì6).setOnDragListener(new MyDragListener());
        findViewById(R.id.giovedì7).setOnDragListener(new MyDragListener());

        findViewById(R.id.venerdì1).setOnDragListener(new MyDragListener());
        findViewById(R.id.venerdì2).setOnDragListener(new MyDragListener());
        findViewById(R.id.venerdì3).setOnDragListener(new MyDragListener());
        findViewById(R.id.venerdì4).setOnDragListener(new MyDragListener());
        findViewById(R.id.venerdì5).setOnDragListener(new MyDragListener());
        findViewById(R.id.venerdì6).setOnDragListener(new MyDragListener());
        findViewById(R.id.venerdì7).setOnDragListener(new MyDragListener());

        findViewById(R.id.sabato1).setOnDragListener(new MyDragListener());
        findViewById(R.id.sabato2).setOnDragListener(new MyDragListener());
        findViewById(R.id.sabato3).setOnDragListener(new MyDragListener());
        findViewById(R.id.sabato4).setOnDragListener(new MyDragListener());
        findViewById(R.id.sabato5).setOnDragListener(new MyDragListener());
        findViewById(R.id.sabato6).setOnDragListener(new MyDragListener());
        findViewById(R.id.sabato7).setOnDragListener(new MyDragListener());


        Button mod_materie = findViewById(R.id.btn_crea_materie);
        mod_materie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaiaCreaMaterie();
            }
        });
//        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable("alunno", alunno);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
            salva_dati();
        }
        if(item.getItemId()==android.R.id.home){
            vaiaScegliDettaglioNav();

        }
        return true;
    }

    private void vaiaCreaMaterie() {

        Intent intent = new Intent(this, CreaElencoMaterie.class);
        intent.putExtra("alunno",(Serializable) alunno);
        intent.putExtra("annosc", annosc);
        intent.putExtra("quadrimestre", quadr);
        startActivity(intent);
        finish();
    }

    private void vaiaScegliDettaglioNav() {

        Intent intent= new Intent(this, ScegliDettaglioNav.class);
        intent.putExtra("alunno",(Serializable) alunno);
        intent.putExtra("annosc", annosc);
        intent.putExtra("quadrimestre", quadr);
        intent.putExtra("pagina", 4);
        startActivity(intent);
        finish();
    }

    private void caricaMaterie() {

        Log.i(TAG_LOG, "arumets " + alunno.getCod_alunno());
        materie.removeAll(materie);
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();
            Cursor cursor = dbLayer.getMaterie(alunno.getCod_alunno());
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG, "cursor: " + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);

                do {
//                    "alunno TEXT, annosc TEXT,  data INTEGER, tipoassenza TEXT, giustificazione TEXT);");
                    DataObject_Materia materia = new DataObject_Materia();
                    materia.setMateria(cursor.getString(2));
                    materia.setDocente(cursor.getString(3));
                    materie.add(materia);

                } while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            // Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
            Log.i(TAG_LOG, "error: " + ex.toString());
        }
        dbLayer.close();


        if (materie.size() > 0) {
            for (DataObject_Materia materia : materie) {


                // LinearLayout cardView = (LinearLayout) getLayoutInflater().inflate(R.layout.card_materie_x_grid,null);
                LinearLayout cardView= new LinearLayout(this);
                // cardView.setLayoutParams(new CardView.LayoutParams(getResources().getIdentifier("width_column", "dimens", getPackageName()), getResources().getIdentifier("height_row", "dimens", getPackageName())));
                cardView.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.width_column), (int) getResources().getDimension(R.dimen.height_row)));
                cardView.setGravity(Gravity.CENTER);

                cardView.setPadding(10,10,10,10);
                cardView.setOrientation(LinearLayout.VERTICAL);
                //cardView.setBackgroundColor(Color.TRANSPARENT);
                cardView.setBackgroundResource(R.drawable.bordo);

                // cardView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                //cardView.setCardElevation(5);
              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    cardView.setBackgroundResource(getResources().getIdentifier("shape", "drawable", getPackageName()));
                }*/
                // cardView.setPadding(2, 2, 2, 2);
                TextView textView =new TextView(this);//cardView.findViewById(R.id.txtmat);//
                TextView txtMateria =new TextView(this);//cardView.findViewById(R.id.materia);
                TextView txtinsegnante = new TextView(this);//cardView.findViewById(R.id.insegnante);//
                textView.setTextSize(12);
                textView.setGravity(Gravity.CENTER);// findViewById(R.id.card_x_grid_materie_txtmateria);
                String mat="";
                try {
                   mat= materia.getMateria().substring(0, 3).toUpperCase();
                   Log.i(TAG_LOG, "MAT: " + mat);
               }catch (Exception e){

               }
                textView.setText(mat);
                textView.setWidth((int) getResources().getDimension(R.dimen.width_column));
                textView.setWidth((int) getResources().getDimension(R.dimen.height_row));
                txtMateria.setText(materia.getMateria());
                txtMateria.setVisibility(View.GONE);
                txtinsegnante.setText(materia.getDocente());
                txtinsegnante.setVisibility(View.GONE);
                cardView.addView(textView);
                cardView.addView(txtMateria);
                cardView.addView(txtinsegnante);
                cardView.setOnTouchListener(new MyTouchListener());
                gridMaterie.addView(cardView);

            }
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Crea Elenco Materie")
                    .setMessage("L'elenco delle materie è vuoto.\n Ne vuoi creare uno?")
                    .setPositiveButton (android.R.string.yes, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
            vaiaCreaMaterie();

                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void salva_dati() {

        Log.i(TAG_LOG, "arumets " + alunno.getCod_alunno());
        String una_materia, un_insegnante;

        for (int i = 0; i <= 47; i++) {
            //for (int x = 1; x <= gridOrario.getRowCount(); x++) {
            LinearLayout linearLayout = (LinearLayout) gridOrario.getChildAt(i);
            LinearLayout  cardView = null;
            int quanti=0;
            try {
                cardView = (LinearLayout ) linearLayout.getChildAt(0);
            } catch (Exception e) {
                Log.i(TAG_LOG, e.toString());
            }
            if (cardView != null) {
                TextView txtMateria = (TextView) cardView.getChildAt(1);
                TextView txtInsegnante = (TextView) cardView.getChildAt(2);
                una_materia = txtMateria.getText().toString();
                un_insegnante = txtInsegnante.getText().toString();
                Log.i(TAG_LOG, "i..." + i + "...." + una_materia + ".." + un_insegnante);
                crea_orario(i, una_materia, un_insegnante);

            }

        }
        inserisci_orario();
    }
    private void  crea_orario(int index, String materia,String insegnante) {

        int gg = 0;
        int ora = 0;
        if (index >= 1 && index <= 7) {
            gg = 1;
            ora = index;
        } else if (index >= 9 && index <= 15) {
            gg = 2;
            ora = index - 8;
        } else if (index >= 17 && index <= 23) {
            gg = 3;
            ora = index - 16;
        } else if (index >= 25 && index <= 31) {
            gg = 4;
            ora = index - 24;
        } else if (index >= 33 && index <= 39) {
            gg = 5;
            ora = index - 32;
        } else if (index >= 41 && index <= 47) {
            gg = 6;
            ora = index - 40;
        }

        if (ora != 0 || gg != 0) {
            Orario_giornaliero orario_giornaliero = new Orario_giornaliero();
            orario_giornaliero.setGiorno(gg);
            orario_giornaliero.setOra(ora);
            orario_giornaliero.setMateria(materia);
            orario_giornaliero.setInsegnante(insegnante);
            orario.add(orario_giornaliero);
        }

    }
    private void inserisci_orario(){
        if (orario.size() > 0) {

            DBLayer dbLayer = null;
            boolean res=false;
            try {
                dbLayer = new DBLayer(this);
                dbLayer.open();
                dbLayer.cancellaOrario(alunno.getCod_alunno());
                res     = dbLayer.inserisciOrario(alunno.getCod_alunno(), orario);
                //             Cursor cursor = dbLayer.getAllVoti();
                Log.i(TAG_LOG, "result: " + res);
            } catch (SQLException ex) {
                // Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
                Log.i(TAG_LOG, "error: " + ex.toString());
            }
            dbLayer.close();
            if(res){
            }else{
                Toast.makeText(this, "Ops! si è verificato un errore", Toast.LENGTH_SHORT).show();
            }

        }else {// oraio vuoto
        }
        Intent intent= new Intent(this, ScegliDettaglioNav.class);
        intent.putExtra("alunno",(Serializable) alunno);
        intent.putExtra("annosc", annosc);
        intent.putExtra("quadrimestre", quadr);
        intent.putExtra("pagina", 4);
        startActivity(intent);
        finish();



    }
    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                // view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(
                R.drawable.shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundDrawable(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundDrawable(normalShape);
                    break;
                case DragEvent.ACTION_DROP:

                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    LinearLayout container = (LinearLayout) v;
                    // GridLayout container = (GridLayout) v;
                    Log.i(TAG_LOG, "v.." + v.toString());
                    container.removeAllViews();
                    container.addView(view);
                    Log.i(TAG_LOG, "v.. aggiunto" + container.toString());
                    view.setVisibility(View.VISIBLE);
                    gridMaterie.removeAllViews();
                    caricaMaterie();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundDrawable(normalShape);
                default:
                    break;
            }
            return true;
        }
    }
}


