package it.android.j940549.myreg_elettronico.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import it.android.j940549.myreg_elettronico.Home_Activity;
import it.android.j940549.myreg_elettronico.HttpUrlConnection.RegHttpgetCod_alunno;
import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.assenze.DataObject_Assenze;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.model.Assenza;
import it.android.j940549.myreg_elettronico.model.ConvertiData;
import it.android.j940549.myreg_elettronico.model.Voto;
import it.android.j940549.myreg_elettronico.voti.DataObject_elencoVoti;

public class AllarmReceiver extends BroadcastReceiver {

    private int NOTIF_ID_VOTI = 100;
    private int NOTIF_ID_ASS = 300;
    private static final String PRIMARY_CHANEL_ID = "primary_notification_chanel";
    private String annosc = "2018/2019";
    private String quadrimestre = "1";
    private int  voti_alunnno_prec;
    private ArrayList<Voto> voti_alunno_attuali;
    private int ass_alunno_prec;
    private ArrayList<Assenza> ass_alunno_attuali;
    private Context context;
    private NotificationManager notificationManager;
    private ArrayList<Alunno> alunni = new ArrayList<>();
    private String TAG_LOG = "AllarmReceiver";
    private RegHttpConnection_Voti_assenze regHttpConnection_voti_assenze;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("brodcast", "ricevuto");
        this.context = context;
        getAlunni();

        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        for (Alunno alunno : alunni) {

            NOTIF_ID_ASS = NOTIF_ID_ASS+100;
            NOTIF_ID_VOTI = NOTIF_ID_ASS+100;

            setDatiIiziali(alunno.getCod_alunno());

            HttpGetTaskVoti_Ass taskVoti_Ass = new HttpGetTaskVoti_Ass(alunno, voti_alunnno_prec,ass_alunno_prec);
            taskVoti_Ass .execute( annosc, quadrimestre);

            /*HttpGetTaskAssenze taskAss = new HttpGetTaskAssenze();
            taskAss.execute(alunno.getCod_alunno(), annosc, quadrimestre);
*/
        }

        createNotificationChannel();

    }

    private void setDatiIiziali(String nome_alunno) {
        voti_alunnno_prec = 0;
        ass_alunno_prec = 0;
        GregorianCalendar ddate = new GregorianCalendar();

        int mese = ddate.get(GregorianCalendar.MONTH) + 1;
        int anno = ddate.get((GregorianCalendar.YEAR));
        Log.i(TAG_LOG,"log_mese_anno"+ "..." + mese + "---" + anno);
        if (mese > 1 && mese < 8) {
            quadrimestre = "2";

        } else {
            quadrimestre = "1";
        }
        voti_alunnno_prec = getVoti(nome_alunno, annosc, "1")+getVoti(nome_alunno, annosc, "2");
        ass_alunno_prec = getAssenze(nome_alunno, annosc, quadrimestre);
    }

    private void createNotificationChannel() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANEL_ID, "Notifiche nuovi Voti e Assenze", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.CYAN);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifiche nuovi Voti e Assenze");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


    private class HttpGetTaskVoti_Ass extends AsyncTask<String, String, String> {

        private Alunno alunno;
        private int voti_prec, ass_prec;

        HttpGetTaskVoti_Ass(Alunno unalunno, int voti_prec, int ass_prec) {
            alunno = unalunno;
            this.voti_prec=voti_prec;
            this.ass_prec=ass_prec;

        }

        @Override
        protected String doInBackground(String... params) {
            // Toast.makeText(getBaseContext(), "run Service avviato", Toast.LENGTH_SHORT).show();
            String annosc = params[0];
            String quadr = params[1];
            String result = "";
            try {
                regHttpConnection_voti_assenze =
                        new RegHttpConnection_Voti_assenze(alunno.getCfIstituto(), alunno.getCod_alunno(), alunno.getUserid(),
                                alunno.getPassword(), annosc, quadr);
                result = regHttpConnection_voti_assenze.doInBackground();

            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;

        }

        @Override
        protected void onPostExecute(String result) {

            voti_alunno_attuali = regHttpConnection_voti_assenze.getVoti();
            ass_alunno_attuali = regHttpConnection_voti_assenze.getAssenze();

            if (voti_alunno_attuali.size() > 0) {
                Log.i(TAG_LOG, "dati_att_"+voti_alunno_attuali.size()+"dati_prec"+voti_prec);

                if (voti_alunno_attuali.size() > voti_prec) {
                    Log.i(TAG_LOG, "dati_att_mag...lancio notifica");

                    Intent notificationIntent = new Intent(context, Home_Activity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIF_ID_VOTI,
                            notificationIntent, PendingIntent
                                    .FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context, PRIMARY_CHANEL_ID)
                            .setSmallIcon(R.drawable.icona_re)
                            .setContentTitle("Notifica da Registro Elettronico")
                            .setContentText("ci sono nuovi voti di" + alunno.getNome_alunno())
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    notificationManager.notify(NOTIF_ID_VOTI, notification.build());


                }else {
                    Log.i(TAG_LOG, "voti_att_ugale non lancio notifica");

                }

            }

            if (ass_alunno_attuali.size() > 0) {

                Log.i(TAG_LOG, "conf...ass_att_"+ass_alunno_attuali.size()+"ass_prec_"+ass_prec);

                if (ass_alunno_attuali.size() > ass_prec) {
                    Log.i(TAG_LOG, "ass_att_maggiore lancio notifica");

                    Intent notificationIntent = new Intent(context, Home_Activity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIF_ID_ASS,
                            notificationIntent, 0);

                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.icona_re)
                            .setContentTitle("Notifica da Registro Elettronico")
                            .setContentText("ci sono nuove Assenze di " + alunno.getNome_alunno())
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    notificationManager.notify(NOTIF_ID_ASS, notification.build());

                }else {
                    Log.i(TAG_LOG, "ass_att_ugale non lancio notifica");

                }

            }
        }
    }

    public void getAlunni() {

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(context);
            dbLayer.open();
            Cursor cursor = dbLayer.getAllAlunni();

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    Alunno alunno = new Alunno();
                    alunno .setId_alunno(cursor.getString(0));
                    alunno .setCfIstituto(cursor.getString(1));
                    alunno .setTipo(cursor.getString(2));
                    alunno .setNome_istituto(cursor.getString(3));
                    alunno .setIndirizzo(cursor.getString(4));
                    alunno .setNome_alunno(cursor.getString(5));
                    alunno .setCod_alunno(cursor.getString(6));
                    alunno .setClasse_sez(cursor.getString(7));
                    alunno .setUserid(cursor.getString(8));
                    alunno .setPassword(cursor.getString(9));

                    alunni.add(alunno);
                } while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            Toast.makeText(context, "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();


    }

    public int getVoti(String alunno, String annosc, String quadrimestre) {
        Log.i(TAG_LOG, "arumets " + alunno + ", " + annosc + ", " + quadrimestre);
        int voti_iniz = 0;
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(context);
            dbLayer.open();
            Cursor cursor = dbLayer.getElenco_voti(alunno, annosc, quadrimestre);
            //             Cursor cursor = dbLayer.getAllVoti();
            if (cursor.getCount() > 0) {

                voti_iniz = cursor.getCount();
                Log.i(TAG_LOG, "voti_alunnno_prec: " + voti_iniz);

            }
        } catch (SQLException ex) {
            Toast.makeText(context, "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();

        return voti_iniz;
    }

    public int getAssenze(String alunno, String annosc, String quadr) {
        Log.i("log_tag_argcar", "arumets " + alunno + ", " + annosc + ", " + quadr);

        int assenze_inz = 0;
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(context);
            dbLayer.open();
            Cursor cursor = dbLayer.getRichiesta_assenze(alunno, annosc);
            //             Cursor cursor = dbLayer.getAllVoti();

            if (cursor.getCount() > 0) {
                assenze_inz = cursor.getCount();
                Log.i(TAG_LOG, "ass_alunno_prec: " + assenze_inz);
            }
        } catch (SQLException ex) {
            Toast.makeText(context, "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();


        return assenze_inz;

    }


}
