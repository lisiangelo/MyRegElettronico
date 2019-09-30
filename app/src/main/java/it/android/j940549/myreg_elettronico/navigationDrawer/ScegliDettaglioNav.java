package it.android.j940549.myreg_elettronico.navigationDrawer;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import it.android.j940549.myreg_elettronico.Home_Activity;
import it.android.j940549.myreg_elettronico.HttpUrlConnection.RegHttpConnection_adatto;
import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.app_widget.MyStackWidgetProvider;
import it.android.j940549.myreg_elettronico.argomenti.Mostra_Compiti_Argoemnti_Frag;
import it.android.j940549.myreg_elettronico.assenze.Fragment_Mostra_Assenze;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.model.Assenza;
import it.android.j940549.myreg_elettronico.model.Compito;
import it.android.j940549.myreg_elettronico.model.Comunicazione;
import it.android.j940549.myreg_elettronico.model.Voto;
import it.android.j940549.myreg_elettronico.orari.OrarioLezioniFragment;
import it.android.j940549.myreg_elettronico.service.AllarmReceiver;
import it.android.j940549.myreg_elettronico.voti.Mostra_Voti_Alunno_frag;

public class ScegliDettaglioNav extends AppCompatActivity {
    private String annosc, quadr;
    private Alunno alunno;
    private Spinner sel_annosc, sel_quadr;
    private ArrayList<Assenza> assenze = new ArrayList<Assenza>();
    private ArrayList<Compito> compiti = new ArrayList<Compito>();
    private ArrayList<Voto> voti = new ArrayList<Voto>();
    private ArrayList<Comunicazione> comunicazioni= new ArrayList<Comunicazione>();

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private Switch aSwitch;
    private NavigationView nvDrawer;
    private BottomNavigationView bottomNavigationView;
    String TAG_LOG = "ScegliDettaglioNav";

    private ActionBarDrawerToggle drawerToggle;
    private int pagina;
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    private NotificationManager notificationManager;
    private ProgressDialog progressDialog;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView nomealunno, istituto, classe;
    private int  voti_alunnno_prec;
    private int ass_alunno_prec, giust_alunno_prec;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scegli_dettaglio_nav);

        if (savedInstanceState != null) {
            alunno = (Alunno) savedInstanceState.getSerializable("alunno");
            annosc = savedInstanceState.getString("annosc");
            quadr = savedInstanceState.getString("quadrimestre");
            pagina = savedInstanceState.getInt("pagina");

        } else {

            alunno = (Alunno) getIntent().getSerializableExtra("alunno");
            annosc = getIntent().getStringExtra("annosc");
            quadr = getIntent().getStringExtra("quadrimestre");
            pagina = getIntent().getIntExtra("pagina", 0);

        }

        collapsingToolbarLayout = findViewById(R.id.collapsingtl);
        collapsingToolbarLayout.setTitleEnabled(false);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //set toolbar per rimpiazzare  ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //trova il tuo drawerview
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //trova il tuo navigationview
        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectDrawerItem(menuItem, menuItem.getItemId());
                return false;
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectBottomItem(menuItem, menuItem.getItemId());
                return false;
            }
        });

        // To set whether switch is on/off use:
        nvDrawer.getMenu().findItem(R.id.checkOnService)
                .setActionView(new Switch(this));

        aSwitch = (Switch)
                nvDrawer.getMenu().findItem(R.id.checkOnService).getActionView();


//        aSwitch.setChecked(false);
        leggiPreferenze();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {

                if (ischecked) {

                    salvaPreferenze(ischecked);

                    attivaNotifiche();
                    Log.i("switch", "on");
                    Toast.makeText(getBaseContext(), "Notifiche abilitate", Toast.LENGTH_LONG).show();
                    //  startService(i);
                } else {

                    salvaPreferenze(ischecked);
                    disattivaNotifiche();
                    Log.i("switch", "off");
                    Toast.makeText(getBaseContext(), "Notifiche disabilitate", Toast.LENGTH_LONG).show();

                }

            }
        });

        drawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        Log.i(TAG_LOG, "prima dei frag " + alunno.getNome_alunno() + ", " + annosc + ", " + quadr + "," + alunno.getCod_alunno());

        if(pagina<4) {
            caricaDati(alunno.getCod_alunno(), annosc, quadr);
        }else if (pagina==4) {
            Fragment fragment = OrarioLezioniFragment.newInstance(alunno, annosc, quadr);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }else if (pagina==5){
                Fragment fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        }

        nomealunno = (TextView) findViewById(R.id.nomealunno_summary);
        istituto = (TextView) findViewById(R.id.istituto_summary);
        classe = (TextView) findViewById(R.id.classe_summary);

        nomealunno.setText(alunno.getNome_alunno().toUpperCase());
        istituto.setText(alunno.getTipo() + " - " + alunno.getNome_istituto());
        classe.setText(alunno.getClasse_sez());
        sel_annosc = (Spinner) findViewById(R.id.select_annosc_summary);
        sel_quadr = (Spinner) findViewById(R.id.select_quadr_summary);


        String[] anni = carica_2anniScolasticiAlunno();
        String[] quadrim = {"I quad/trim", "II quad/pent"};
        ArrayAdapter<String> adapterAnnsc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, anni);
        ArrayAdapter<String> adapterQuadr = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, quadrim);
        sel_annosc.setAdapter(adapterAnnsc);
        sel_quadr.setAdapter(adapterQuadr);

        sel_annosc.setSelection(1);

       /* sel_annosc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                annosc = (String) sel_annosc.getSelectedItem();
                String quadrSpin = (String) sel_quadr.getSelectedItem();
                if (quadrSpin.contains("I")) {
                    quadr = "1";
                }
                if (quadrSpin.contains("II")) {
                    quadr = "2";
                }

                caricaDati(alunno.getCod_alunno(), annosc, quadr);

            }

        });

*/
        if (quadr.equals("1")) {
            sel_quadr.setSelection(0);
        } else {
            sel_quadr.setSelection(1);

        }

        sel_quadr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                annosc = (String) sel_annosc.getSelectedItem();
                String quadrSpin = (String) sel_quadr.getSelectedItem();
                if (quadrSpin.contains("I")) {
                    quadr = "1";
                }
                if (quadrSpin.contains("II")) {
                    quadr = "2";
                }
                Fragment fragment = null;
                int menuItem_id = bottomNavigationView.getSelectedItemId();

                switch (menuItem_id) {

                    case R.id.menu_item_voti:
                        fragment = Mostra_Voti_Alunno_frag.newInstance(alunno, annosc, quadr);
                        pagina=1;
                        break;

                    case R.id.menu_item_compiti:
                        fragment = Mostra_Compiti_Argoemnti_Frag.newInstance(alunno, annosc, quadr);
                        pagina=2;
                        break;

                    case R.id.menu_item_assenza:
                        fragment = Fragment_Mostra_Assenze.newInstance(alunno, annosc, quadr);
                        pagina=3;
                        break;

                    case R.id.menu_item_orario_lezioni:
                        fragment = OrarioLezioniFragment.newInstance(alunno, annosc, quadr);
                        pagina=4;
                        break;

                    case R.id.menu_item_sommario:
                        fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                        pagina=0;
                        break;

                    default:
                        fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                        pagina=0;
                        bottomNavigationView.getMenu().findItem(R.id.menu_item_sommario).setChecked(true);
                        break;
                }

                //inserisci il fragment rimpiazzando i frgment esitente
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageButton btn = (ImageButton) findViewById(R.id.btnRefresh_summary);
        btn.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       annosc = (String) sel_annosc.getSelectedItem();
                                       String quadrSpin = (String) sel_quadr.getSelectedItem();
                                       if (quadrSpin.contains("I")) {
                                           quadr = "1";
                                       }
                                       if (quadrSpin.contains("II")) {
                                           quadr = "2";
                                       }

                                       caricaDati(alunno.getCod_alunno(), annosc, quadr);
                                   }
                               });
/*                Fragment fragment = null;
                int menuItem_id = bottomNavigationView.getSelectedItemId();

                switch (menuItem_id) {

                    case R.id.menu_item_voti:
                        fragment = Mostra_Voti_Alunno_frag.newInstance(alunno, annosc, quadr);
                        pagina=1;
                        break;

                    case R.id.menu_item_compiti:
                        fragment = Mostra_Compiti_Argoemnti_Frag.newInstance(alunno, annosc, quadr);
                        pagina=2;
                        break;

                    case R.id.menu_item_assenza:
                        fragment = Fragment_Mostra_Assenze.newInstance(alunno, annosc, quadr);
                        pagina=3;
                        break;

                    case R.id.menu_item_orario_lezioni:
                        fragment = OrarioLezioniFragment.newInstance(alunno, annosc, quadr);
                        pagina=4;
                        break;

                    case R.id.menu_item_sommario:
                        fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                        pagina=0;
                        break;

                    default:
                        fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                        pagina=0;
                        bottomNavigationView.getMenu().findItem(R.id.menu_item_sommario).setChecked(true);
                        break;
                }

                //inserisci il fragment rimpiazzando i frgment esitente
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            }
        });*/

        createNotificationChannel();
        setTitle("MyRegistroElettronico");
    }

    private String [] carica_2anniScolasticiAlunno(){

        String annosc1="";
        String annosc2="";
        GregorianCalendar ddate = new GregorianCalendar();
        int mese = ddate.get(GregorianCalendar.MONTH) + 1;
        int anno = ddate.get((GregorianCalendar.YEAR));
        if (mese > 8) {
            annosc1= ""+(anno-1)+"/"+anno;
            annosc2= ""+anno+"/"+(anno+1);
        } else {
            annosc1= ""+(anno-2)+"/"+(anno-1);
            annosc2= ""+(anno-1)+"/"+anno;
        }
        String dato[]={annosc1,annosc2};
        return dato;
    }

    private void leggiPreferenze() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean siNotifiche = sharedPref.getBoolean("switch", false);

        if (!siNotifiche) {
            Toast.makeText(this, "ATTIVA LE NOTIFICHE PER NUOVI VOTI E ASSENZE", Toast.LENGTH_SHORT).show();
            aSwitch.setChecked(false);

            disattivaNotifiche();
        } else {
            aSwitch.setChecked(true);

            attivaNotifiche();
        }

    }

    private void salvaPreferenze(boolean si_no_Notifiche) {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("switch", si_no_Notifiche);
        editor.commit();

    }

    private void createNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Notifiche nuovi Voti e Assenze", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.CYAN);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifiche nuovi Voti e Assenze");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void attivaNotifiche() {

        Intent intent = new Intent(ScegliDettaglioNav.this, AllarmReceiver.class);
        PendingIntent pendingalarmIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm to start and repet evert 1 hours
        long repeatInterval = 1 * (60*60* 1000);

        long triggerTime = SystemClock.elapsedRealtime();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // If the Toggle is turned on, set the repeating alarm with
        // a 2 hours interval.
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime, repeatInterval,
                    pendingalarmIntent);
        }

    }

    private void disattivaNotifiche() {

        Intent intent = new Intent(ScegliDettaglioNav.this, AlarmManager.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(alarmIntent);


    }


    public void selectDrawerItem(MenuItem item, int menuItem_id) {

        Fragment fragment = null;

        if (menuItem_id == R.id.menu_item_cambia_alunno) {
            Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
            startActivity(intent);
            finish();
        } else if (menuItem_id == R.id.nav_esci) {

            finish();

        } else {

            switch (menuItem_id) {

                case R.id.attivaService:

                    ((Switch) item.getActionView()).toggle();
                    fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                    pagina=0;
                    break;

                default:
                    fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                    pagina=0;

                    break;
            }

            //inserisci il fragment rimpiazzando i frgment esitente
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }
        //evidenzio  l'item che è stato selezionato nel Navigationview
        item.setChecked(true);

        //imposto il titolo dellìaction bar
        setTitle(item.getTitle());
        //chiudo il navigationdrawer
        mDrawer.closeDrawers();

    }

    public void selectBottomItem(MenuItem item, int menuItem_id) {

        Fragment fragment = null;
//        Fragment fragment= BlankFragment.newInstance(alunno,annosc);
        Class fragmentClass;


        switch (menuItem_id) {


            case R.id.menu_item_voti:
                fragment = Mostra_Voti_Alunno_frag.newInstance(alunno, annosc, quadr);
                pagina=1;
                break;


            case R.id.menu_item_compiti:
                fragment = Mostra_Compiti_Argoemnti_Frag.newInstance(alunno, annosc, quadr);
                pagina=2;
                break;

            case R.id.menu_item_assenza:
                fragment = Fragment_Mostra_Assenze.newInstance(alunno, annosc, quadr);
                pagina=3;
                break;

            case R.id.menu_item_orario_lezioni:
                fragment = OrarioLezioniFragment.newInstance(alunno, annosc, quadr);
                pagina=4;
                break;

            case R.id.menu_item_sommario:
                fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                pagina=0;
                break;

            default:
                fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                pagina=0;
                bottomNavigationView.getMenu().findItem(R.id.menu_item_sommario).setChecked(true);
                break;
        }
        //inserisci il fragment rimpiazzando i frgment esitente
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        //evidenzio  l'item che è stato selezionato nel Navigationview
        item.setChecked(true);

        //imposto il titolo dellìaction bar e disabilito quello di collapsingtoolban
        collapsingToolbarLayout.setTitleEnabled(false);//setTitle(item.getTitle());
        setTitle(item.getTitle());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable("alunno", alunno);
        savedInstanceState.putString("annosc", annosc);
        savedInstanceState.putString("quadrimestre", quadr);
        savedInstanceState.putInt("pagina", pagina);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

//            super.onBackPressed();
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("sei sicuro di voler uscire?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ScegliDettaglioNav.this.onSuperBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    public void onSuperBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.scegli_dettaglio_nav, menu);
        return true;
    }

    public void caricaDati(String cod_alunno, String annosc, String quadrimestre) {

        ScegliDettaglioNav.HttpGetTask task = new ScegliDettaglioNav.HttpGetTask(this);
        task.execute(cod_alunno, annosc, quadrimestre);
        Log.i(TAG_LOG, "arumets " + alunno.getNome_alunno() + ", " + annosc + ", " + quadrimestre + "," + cod_alunno);

    }


    private class HttpGetTask extends AsyncTask<String, String, String> {

        RegHttpConnection_adatto regHttpConnection_adatto;
        private Activity myActivity;

        public HttpGetTask(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(myActivity);
            progressDialog.setMessage("caricamento dati in corso");
            progressDialog.setCancelable(false);
            progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            super.onPreExecute();

           if (isNetworkConnected() == false) {
                // Cancel request.
                Toast.makeText(myActivity, "non sei connesso ad Internet", Toast.LENGTH_SHORT).show();
                cancel(true);
                progressDialog.dismiss();

            }
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            if (isNetworkConnected() == true) {
                try {
                    //progressBar.setProgress(10);
                    regHttpConnection_adatto = new RegHttpConnection_adatto(alunno.getCfIstituto(), alunno.getCod_alunno(), alunno.getUserid(), alunno.getPassword(), params[1]);
                    result = regHttpConnection_adatto.doInBackground();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return result;

        }

        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);


        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG_LOG, "result on postex.." + result);
            progressDialog.dismiss();

            if (!result.equals("")) {
                voti = regHttpConnection_adatto.getVoti();
                compiti = regHttpConnection_adatto.getCompiti();
                assenze = regHttpConnection_adatto.getAssenze();
                comunicazioni=regHttpConnection_adatto.getComunicazioni();
                voti_alunnno_prec=getVoti(alunno.getCod_alunno(),annosc,"1")+getVoti(alunno.getCod_alunno(),annosc,"2");
                ass_alunno_prec=getAssenze(alunno.getCod_alunno(),annosc,quadr);
                Log.i(TAG_LOG, "voti.size()" + voti.size());
                Log.i(TAG_LOG, "compiti.size()" + compiti.size());
                Log.i(TAG_LOG, "assenze.size()" + assenze.size());

                if (voti.size() != 0&& voti.size()!=voti_alunnno_prec) {
                    inserisciVoti(voti, annosc);
                }else{
                    Log.i(TAG_LOG, "voti non inseriti");
                }
                if (compiti.size() != 0) {
                    inserisciCompiti_argomenti(compiti, annosc);
                }
                if (assenze.size() != 0){//&& assenze.size()!=ass_alunno_prec) {
                    inserisciAssenze(assenze, annosc);
                }else{
                    Log.i(TAG_LOG, "assenze non inserite");
                }
                if (comunicazioni.size() != 0) {
                    inserisciComunicazioni(comunicazioni);
                }else{
                    Log.i(TAG_LOG, "assenze non inserite");
                }

            } else {
                Toast.makeText(myActivity, "non sei connesso ad Internet", Toast.LENGTH_SHORT).show();
                Toast.makeText(myActivity, "Ops! non ho ricevuto dati dal sito \n questi sono gli ultimi dati salvati", Toast.LENGTH_SHORT).show();
            }
            Fragment fragment = null;
            switch (pagina) {
                case 0:
                    fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                    break;
                case 1:
                    fragment = Mostra_Voti_Alunno_frag.newInstance(alunno, annosc, quadr);
                    break;
                case 2:
                    fragment = Mostra_Compiti_Argoemnti_Frag.newInstance(alunno, annosc, quadr);
                    break;
                case 3:
                    fragment = Fragment_Mostra_Assenze.newInstance(alunno, annosc, quadr);
                    break;
                case  4:
                    fragment = OrarioLezioniFragment.newInstance(alunno, annosc, quadr);
                    break;
                    default:
                        fragment = FragmentSumary.newInstance(alunno, annosc, quadr);
                        break;
                }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

//            progressDialog.dismiss();
            Intent intentUpdateWidget= new Intent();
            intentUpdateWidget.setAction(MyStackWidgetProvider.ACTION_AUTO_UPDATE);
            myActivity.sendBroadcast(intentUpdateWidget);
            Log.i(TAG_LOG,"intentUpdatewidget");

        }
    }

    public void inserisciVoti(ArrayList<Voto> voti, String annosc) {

        String result = "";
        if (!annosc.equals("")) {
            DBLayer dbLayer = new DBLayer(this);
            dbLayer.open();
            dbLayer.cancellaVoti(annosc, alunno.getCod_alunno());
            boolean inserito = dbLayer.inserisciVoti(voti, annosc);
            if (inserito) {
                Log.i(TAG_LOG, "voti inseriti");


            } else {
                Toast.makeText(this, "Nessun nuovo dato inserito", Toast.LENGTH_SHORT).show();
            }
            dbLayer.close();
        } else {
            Toast.makeText(this, "completa  i dati essenziali", Toast.LENGTH_SHORT).show();
        }

    }

    public void inserisciComunicazioni(ArrayList<Comunicazione> comunicazioni) {

        String result = "";
        if (!comunicazioni.get(0).getAlunno().equals("")) {
            DBLayer dbLayer = new DBLayer(this);
            dbLayer.open();
            dbLayer.deleteComunicazioni(alunno.getCod_alunno());
            boolean inserito = dbLayer.inserisciComunicazioni(comunicazioni);
            if (inserito) {
                Log.i(TAG_LOG, "comunicazioni inseriti");


            } else {
                Toast.makeText(this, "Nessun nuovo dato inserito", Toast.LENGTH_SHORT).show();
            }
            dbLayer.close();
        } else {
            Toast.makeText(this, "completa  i dati essenziali", Toast.LENGTH_SHORT).show();
        }

    }

    public void inserisciCompiti_argomenti(ArrayList<Compito> compiti, String annosc) {

        String result = "";
        if (!annosc.equals("") || !quadr.equals("")) {
            DBLayer dbLayer = new DBLayer(this);
            dbLayer.open();
            dbLayer.cancellaCompiti_Argomenti(annosc, alunno.getCod_alunno());
            boolean inserito = dbLayer.inserisciCompiti_argomenti(compiti, annosc);
            if (inserito) {
                Log.i(TAG_LOG, "compiti inseriti");


            } else {
                Toast.makeText(this, "Nessun nuovo dato inserito", Toast.LENGTH_SHORT).show();
            }
            dbLayer.close();
        } else {
            Toast.makeText(this, "completa  i dati essenziali", Toast.LENGTH_SHORT).show();
        }

    }

    public void inserisciAssenze(ArrayList<Assenza> assenze, String annosc) {

        String result = "";
        if (!annosc.equals("") || !quadr.equals("")) {
            DBLayer dbLayer = new DBLayer(this);
            dbLayer.open();
            dbLayer.cancellaAssenze(annosc, alunno.getCod_alunno());
            boolean inserito = dbLayer.inserisciAssenze(assenze, annosc);
            if (inserito) {
                Log.i(TAG_LOG, "assenze inserite");


            } else {
                Toast.makeText(this, "Nessun nuovo dato inserito", Toast.LENGTH_SHORT).show();
            }
            dbLayer.close();
        } else {
            Toast.makeText(this, "completa  i dati essenziali", Toast.LENGTH_SHORT).show();
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

    public int getVoti(String alunno, String annosc, String quadrimestre) {
        Log.i(TAG_LOG, "arumets " + alunno + ", " + annosc + ", " + quadrimestre);
        int voti_iniz = 0;
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();
            Cursor cursor = dbLayer.getElenco_voti(alunno, annosc, quadrimestre);
            //             Cursor cursor = dbLayer.getAllVoti();
            if (cursor.getCount() > 0) {

                voti_iniz = cursor.getCount();
                Log.i(TAG_LOG, "voti_alunnno_prec: " + voti_iniz);

            }
        } catch (SQLException ex) {
            Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();

        return voti_iniz;
    }

    public int getAssenze(String alunno, String annosc, String quadr) {
        Log.i("log_tag_argcar", "arumets " + alunno + ", " + annosc + ", " + quadr);

        int assenze_inz = 0;
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();
            Cursor cursor = dbLayer.getRichiesta_assenze(alunno, annosc);
            //             Cursor cursor = dbLayer.getAllVoti();

            if (cursor.getCount() > 0) {
                assenze_inz = cursor.getCount();
                Log.i(TAG_LOG, "ass_alunno_prec: " + assenze_inz);
            }
        } catch (SQLException ex) {
            Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();


        return assenze_inz;

    }

}

