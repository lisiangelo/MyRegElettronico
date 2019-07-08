package it.android.j940549.myreg_elettronico;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import it.android.j940549.myreg_elettronico.HttpUrlConnection.RegHttpgetCod_alunno;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.service.RegHttpConnection_Voti_assenze;

public class Aggiungi_Alunno extends AppCompatActivity {

    TextView cf_istituto, tipo_istituto, nome_istituto, indirizzo, nome_alunno, txclasse, txsez, userid, password;
    String cod_alunno, cf_ist, tipo, nome_ist, indir, nome_al, clas_sz, user, pw;

    CardView cerca_ist;
    Button inserisci;
    String TAG_LOG = "Aggiungi_Alunno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi__alunno);

        cf_istituto = findViewById(R.id.cf_istituto);
        tipo_istituto = findViewById(R.id.tipo_istituto);
        nome_istituto = findViewById(R.id.nome_istituto);
        indirizzo = findViewById(R.id.indirizzo);

        nome_alunno = findViewById(R.id.nome_alunno);
        txclasse = findViewById(R.id.classe_);
        txsez = findViewById(R.id.sez_);
        userid = findViewById(R.id.userid);
        password = findViewById(R.id.password);

        cerca_ist = findViewById(R.id.click_cerca_scuola);
        cerca_ist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vai_a_cerca_istituto(v);

            }
        });
        inserisci = findViewById(R.id.btn_inserisci);
        inserisci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cf_ist = cf_istituto.getText().toString();
                tipo = tipo_istituto.getText().toString();
                nome_ist = nome_istituto.getText().toString();
                indir = indirizzo.getText().toString();
                nome_al = nome_alunno.getText().toString();
                clas_sz = txclasse.getText().toString() + " " + txsez.getText().toString();
                user = userid.getText().toString();
                pw = password.getText().toString();
                cod_alunno = "";
                if (!cf_ist.equals("") || !nome_ist.equals("")) {
                    try {
                        HttpGetTask httpGetTask = new HttpGetTask();
                        httpGetTask.execute(cf_ist, nome_al, user, pw);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Aggiungi_Alunno.this, "Mancano i dati dell'istituto", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG_LOG, "alunno e cod:alunno" + nome_al + "--" + cod_alunno);


            }
        });

    }

    public void inserisci(String cf, String tipo, String nome_ist, String indir, String nome_al, String cod_alunno,
                          String classe_sz, String user, String pw) {

        String result = "";
        DBLayer dbLayer = new DBLayer(this);
        dbLayer.open();

        boolean inserito = dbLayer.inserisciNewAlunno(cf, tipo, nome_ist, indir, nome_al, cod_alunno, classe_sz, user, pw);
        if (inserito) {
            Toast.makeText(this, "Alunno inserito", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Home_Activity.class);
            startActivity(intent);
            finish();


        } else {
            Toast.makeText(this, "Nessun nuovo dato inserito", Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();

    }

    public void vai_a_cerca_istituto(View v) {
        Intent intent = new Intent(this, Cerca_istituto.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            //  if(resultCode==RESULT_OK){
            String cf = data.getStringExtra("cf_istituto");
            String tipo = data.getStringExtra("tipo_istituto");
            String nome_ist = data.getStringExtra("nome_istituto");
            String indir = data.getStringExtra("indirizzo");
            cf_istituto.setText(cf);
            tipo_istituto.setText(tipo);
            nome_istituto.setText(nome_ist);
            indirizzo.setText(indir);


            //  }
        }
    }

    private class HttpGetTask extends AsyncTask<String, String, String> {
        //ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {

           /* progressDialog = new ProgressDialog(getParent());
            progressDialog.setMessage("caricamento dati in corso");
            progressDialog.setCancelable(false);
            progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();*/
            super.onPreExecute();

         /*   if (isNetworkConnected() == false) {
                // Cancel request.
                Toast.makeText(getParent(), "non sei connesso ad Internet", Toast.LENGTH_SHORT).show();
                cancel(true);

                return;
            }*/
        }

        @Override
        protected String doInBackground(String... params) {
            // Toast.makeText(getBaseContext(), "run Service avviato", Toast.LENGTH_SHORT).show();
            String result = "";
            if (isNetworkConnected() == true) {
                String cf_ist = params[0];
                String nome_al = params[1];
                String user = params[2];
                String pw = params[3];


                try {
                    RegHttpgetCod_alunno regHttpgetCod_alunno = new RegHttpgetCod_alunno(cf_ist, nome_al, user, pw);
                    regHttpgetCod_alunno.doInBackground();
                    result = regHttpgetCod_alunno.getCod_alunno();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getParent(), "non sei connesso ad Internet", Toast.LENGTH_SHORT).show();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (!result.equals(""))
                    cod_alunno = result;
                if (!cf_ist.equals("") && !nome_ist.equals("") && !nome_al.equals("") && !cod_alunno.equals("") && !user.equals("") && !pw.equals("")) {

                    inserisci(cf_ist, tipo, nome_ist, indir, nome_al, cod_alunno, clas_sz, user, pw);
                } else {
                    Toast.makeText(Aggiungi_Alunno.this, "completa  i dati ", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(Aggiungi_Alunno.this, "Ops, qualcosa non va, alunno non registrato a R.e.", Toast.LENGTH_SHORT).show();
            }
//        progressDialog.dismiss();
        }

    }

    protected boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = null;
        // Instantiate mConnectivityManager if necessary
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        // Is device connected to the Internet?
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
