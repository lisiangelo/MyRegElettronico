package it.android.j940549.myreg_elettronico.SQLite;

/**
 * Created by J940549 on 22/04/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import it.android.j940549.myreg_elettronico.model.Assenza;
import it.android.j940549.myreg_elettronico.model.Compito;
import it.android.j940549.myreg_elettronico.model.Comunicazione;
import it.android.j940549.myreg_elettronico.model.DataObject_MediaTotale;
import it.android.j940549.myreg_elettronico.model.Orario_giornaliero;
import it.android.j940549.myreg_elettronico.model.Voto;
import it.android.j940549.myreg_elettronico.orari.DataObject_OrarioLezioni;


/**
 * Created by J940549 on 22/04/2017.
 */

public class DBLayer {

    private static final String DATABASE_NAME = "MyRegElettronico.db";
    private static final int DATABASE_VERSION = 4;
    private String TGA_LOG="DBLayer";

    private DbHelper ourHelper;
    private static Context ourContext;
    private SQLiteDatabase ourDatabase;
    String TAG_LOG = "DBLayer";
    //private static Crypto crypto;

    public DBLayer(Context c) {
        this.ourContext = c;
    }

    private static class DbHelper extends SQLiteOpenHelper {


        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            //  crypto=new Crypto(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS alunni (" +
                        " id INTEGER PRIMARY KEY , " +
                        "cf_istituto INTEGER, tipo TEXT, nome_istituto TEXT, indirizzo TEXT, alunno TEXT, cod_alunno TEXT, classe_sez TEXT, userid TEXT, password TEXT);");

                db.execSQL("CREATE TABLE IF NOT EXISTS voti (" +
                        " id INTEGER PRIMARY KEY , " +
                        "alunno TEXT, annosc TEXT, quadrimestre TEXT, data INTEGER, materia TEXT, tipo TEXT, voto INTEGER, docente TEXT );");

                db.execSQL("CREATE TABLE IF NOT EXISTS compiti_argomenti (" +
                        " id INTEGER PRIMARY KEY , " +
                        "alunno TEXT, annosc TEXT,  data TEXT, argomento TEXT, compiti TEXT, assenze TEXT, noteprof  TEXT,notediscip  TEXT );");

                db.execSQL("CREATE TABLE IF NOT EXISTS assenze (" +
                        " id INTEGER PRIMARY KEY , " +
                        "alunno TEXT, annosc TEXT,  data INTEGER, tipoassenza TEXT, giustificazione TEXT);");

                db.execSQL("CREATE TABLE IF NOT EXISTS orario (" +
                        " id INTEGER PRIMARY KEY , " +
                        "alunno TEXT,  giorno INTEGER, ora integer, materia TEXT, insegnante TEXT);");

                db.execSQL("CREATE TABLE IF NOT EXISTS materie (" +
                        " id INTEGER PRIMARY KEY , " +
                        "alunno TEXT, materia TEXT,insegnante TEXT);");

                db.execSQL("CREATE TABLE IF NOT EXISTS comunicazioni (" +
                        " id INTEGER PRIMARY KEY , " +
                        "alunno TEXT, data INTEGER, testo TEXT);");

            } catch (SQLException ex) {
                Toast.makeText(ourContext, "" + ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS alunni;");
            db.execSQL("DROP TABLE IF EXISTS voti;");
            db.execSQL("DROP TABLE IF EXISTS compiti_argomenti;");
            db.execSQL("DROP TABLE IF EXISTS assenze;");
            db.execSQL("DROP TABLE IF EXISTS orario;");
            db.execSQL("DROP TABLE IF EXISTS materie;");
            db.execSQL("DROP TABLE IF EXISTS comunicazioni;");
            onCreate(db);
        }
    }


    public DBLayer open() throws SQLException {
        this.ourHelper = new DbHelper(ourContext);
        this.ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.ourHelper.close();
    }

    public Cursor getAllAlunni() {

        Cursor c = ourDatabase.rawQuery("select * from alunni", null);
        return c;

    }

    public Cursor getAlunno(String id_alunno) {
        id_alunno = "\"" + id_alunno + "\"";
        Cursor c = ourDatabase.rawQuery("select * from alunni where id=" + id_alunno, null);
        return c;

    }

    public boolean inserisciNewAlunno(String cf_istituto, String tipo, String nome_istituto, String indirizzo,
                                      String nome_alunno, String cod_alunno, String classe_sez, String userid, String password) {

        long cf = Long.parseLong(cf_istituto.replaceAll("\"", ""));
        tipo = "\"" + tipo + "\"";
        if (nome_istituto.contains("\"")) {
            nome_istituto = nome_istituto.replaceAll("\"", "");
        }
        nome_istituto = "\"" + nome_istituto + "\"";
        indirizzo = "\"" + indirizzo + "\"";
        nome_alunno = "\"" + nome_alunno + "\"";
        cod_alunno = "\"" + cod_alunno + "\"";
        classe_sez = "\"" + classe_sez + "\"";
        userid = "\"" + userid + "\"";
        password = "\"" + password + "\"";

        Log.i("query", tipo + "." + nome_istituto + "." + indirizzo + "." + nome_alunno + "." + cod_alunno + "." + classe_sez + "." + userid + "." + password);
        boolean c = false;
        String Query = "insert into alunni (cf_istituto, tipo,  nome_istituto,  indirizzo, alunno, cod_alunno,classe_sez, userid,  password)" +
                "values (" + cf + "," + tipo + "," + nome_istituto + "," + indirizzo + "," + nome_alunno + "," + cod_alunno + "," + classe_sez + "," + userid + "," + password + ");";
        Log.i("query", Query);
        try {
            ourDatabase.execSQL(Query);
            c = true;
            Log.i("query succesfully", "" + c);
        } catch (Exception e) {
            c = false;
            Log.i("query errore", e.toString());
        }

        return c;
    }


    public Cursor getDettaglio_materia(String alunno, String annosc, String quadrimestre, String materia) {
        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";
        quadrimestre = "\"" + quadrimestre + "\"";
        materia = "\"" + materia + "\"";


        Cursor c = ourDatabase.rawQuery("select * from voti where alunno=" + alunno + " and annosc=" + annosc + " " +
                "and quadrimestre=" + quadrimestre + " and materia=" + materia, null);
        Log.i("getDet_materia..", alunno);
        return c;
    }

    public Cursor getUltima_assenza(String alunno, String annosc) {

        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";

        Cursor c = ourDatabase.rawQuery("select * from assenze where alunno=" + alunno + " and annosc=" + annosc + " limit 1", null);
        Log.i("getultima_Assenza..", alunno);
        return c;
    }

    public Cursor getRichiesta_assenze(String alunno, String annosc) {

        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";


        Cursor c = ourDatabase.rawQuery("select * from assenze where alunno=" + alunno + " and annosc=" + annosc, null);
        Log.i("getDet_assenza..", alunno);
        return c;
    }

    public Cursor getRichiesta_media_materie(String alunno, String annosc, String quadrimestre) {

        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";
        quadrimestre = "\"" + quadrimestre + "\"";

        Cursor c = ourDatabase.rawQuery("select materia, docente, avg(voto) as media from voti where alunno=" + alunno +
                " and annosc=" + annosc + " and quadrimestre=" + quadrimestre + " group by materia", null);
        Log.i("getmedia_mater..", alunno);
        return c;
    }

    public Cursor getDettaglio(String alunno, String annosc, String quadrimestre, String materia) {

        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";
        quadrimestre = "\"" + quadrimestre + "\"";
        materia = "\"" + materia + "\"";


        Cursor c = ourDatabase.rawQuery("select * from voti where alunno=" + alunno + " and annosc=" + annosc +
                " and quadrimestre=" + quadrimestre + " and materia=" + materia, null);
        Log.i("getDettaglio..", alunno);
        return c;
    }


    public Cursor getRichesta_compiti(String alunno, String annosc) {

        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";


        Cursor c = ourDatabase.rawQuery("select data,compiti from compiti_argomenti where alunno=" + alunno + " and annosc=" + annosc, null);
        Log.i("getcompiti..", alunno);
        return c;
    }


    public Cursor getMedia_totale(String alunno, String annosc, String quadrimestre) {

        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";
        quadrimestre = "\"" + quadrimestre + "\"";

        Cursor c = ourDatabase.rawQuery("select avg(voto) as media_totale from voti where alunno=" + alunno +
                " and annosc=" + annosc + " and quadrimestre=" + quadrimestre, null);
        // Log.i("getmedia_totale..",alunno+"--"+c.getString(0));
        return c;
    }


    public Cursor getRichesta_argomenti(String alunno, String annosc) {

        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";


        Cursor c = ourDatabase.rawQuery("select data,argomento,noteprof,notediscip from compiti_argomenti where alunno=" + alunno +
                " and annosc=" + annosc, null);
        Log.i("getargomenti..", alunno);
        return c;
    }


    public Cursor getElenco_voti(String alunno, String annosc, String quadrimestre) {

        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";
        quadrimestre = "\"" + quadrimestre + "\"";

        Cursor c = ourDatabase.rawQuery("select * from voti where alunno =" + alunno + " and annosc=" + annosc +
                " and quadrimestre=" + quadrimestre, null);
        Log.i("getelenco_voti..", alunno);
        return c;
    }

    public Cursor getUltimo_voto(String alunno, String annosc, String quadrimestre) {

        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";
        quadrimestre = "\"" + quadrimestre + "\"";

        Cursor c = ourDatabase.rawQuery("select * from voti where alunno=" + alunno + " and annosc=" + annosc +
                " and quadrimestre=" + quadrimestre + " limit 1", null);
        Log.i("getelenco_voti..", alunno);
        return c;
    }


    public int deleteAlunno(String idAlunno) {
        idAlunno = "\"" + idAlunno + "\"";
        int res = ourDatabase.delete("alunni", "id=" + idAlunno, null);
        //db.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
        return res;
    }


    public int cancellaVoti(String annosc, String alunno) {
        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";

        int res = ourDatabase.delete("voti", "annosc=" + annosc + " and `alunno`=" + alunno, null);
        //db.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
        return res;
    }

    public boolean inserisciVoti(ArrayList<Voto> voti, String annosc) {
        boolean c = false;
        for (Voto voto : voti) {
            if (!voto.getVoto().equals("0")) {

                Log.i(TAG_LOG, voto.getVoto());
                ContentValues contentValues = new ContentValues();
                contentValues.put("alunno", voto.getAlunno());
                contentValues.put("annosc", annosc);
                contentValues.put("quadrimestre", voto.getQuadrim());
                contentValues.put("data", voto.getData());
                contentValues.put("materia", voto.getMateria());
                contentValues.put("tipo", voto.getTipo());
                contentValues.put("voto", voto.getVoto());
                contentValues.put("docente", voto.getDocente());


                try {
                    long result = ourDatabase.insert("voti", null, contentValues);
                    if (result == -1) {
                        c = true;
                    } else {
                        c = true;
                    }
                    Log.i("query succesfully", "" + c);
                } catch (Exception e) {
                    c = false;
                    Log.i("query errore", e.toString());
                }
            }
        }

        return c;
    }


    public int cancellaCompiti_Argomenti(String annosc, String alunno) {
        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";

        int res = ourDatabase.delete("compiti_argomenti", "annosc=" + annosc + " and `alunno`=" + alunno, null);
        //db.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
        return res;
    }


    public boolean inserisciCompiti_argomenti(ArrayList<Compito> compiti, String annosc) {
        boolean c = false;
        for (Compito compito : compiti) {
            if (!compito.getAlunno().equals("")) {


                ContentValues contentValues = new ContentValues();
                contentValues.put("alunno", compito.getAlunno());
                contentValues.put("annosc", annosc);
                contentValues.put("data", compito.getData());
                contentValues.put("argomento", compito.getArgoemnto());
                contentValues.put("compiti", compito.getCompiti());
                contentValues.put("assenze", compito.getAssenze());
                contentValues.put("noteprof", compito.getNoteProf());
                contentValues.put("notediscip", compito.getNoteDiscip());


                try {
                    long result = ourDatabase.insert("compiti_argomenti", null, contentValues);
                    if (result == -1) {
                        c = true;
                    } else {
                        c = true;
                    }
                    Log.i("query succesfully", "" + c);
                } catch (Exception e) {
                    c = false;
                    Log.i("query errore", e.toString());
                }
            }
        }

        return c;
    }

    public int cancellaAssenze(String annosc, String alunno) {
        alunno = "\"" + alunno + "\"";
        annosc = "\"" + annosc + "\"";

        int res = ourDatabase.delete("assenze", "annosc=" + annosc + " and `alunno`=" + alunno, null);
        //db.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
        return res;
    }


    public boolean inserisciAssenze(ArrayList<Assenza> assenze, String annosc) {
        boolean c = false;
        for (Assenza assenza : assenze) {
            if (!assenza.getAlunno().equals("")) {


                ContentValues contentValues = new ContentValues();
                contentValues.put("alunno", assenza.getAlunno());
                contentValues.put("annosc", annosc);
                contentValues.put("data", assenza.getData());
                contentValues.put("tipoassenza", assenza.getTipoAssenza());
                contentValues.put("giustificazione", assenza.getGiustificazione());


                try {
                    long result = ourDatabase.insert("assenze", null, contentValues);
                    Log.i(TAG_LOG, "query resultinesertt" +result);
                    if (result == -1) {
                        c = true;
                    } else {
                        c = true;
                    }
                    Log.i(TAG_LOG, "" + c);
                } catch (Exception e) {
                    c = false;
                    Log.i("query errore", e.toString());
                }
            }
        }

        return c;
    }

// orario

    public Cursor getOrario_alunno(String alunno, int giorno) {

        alunno = "\"" + alunno + "\"";


        Cursor c = ourDatabase.rawQuery("select * from orario where alunno =" + alunno+" and giorno="+giorno+" order by ora" , null);
        Log.i("getorario..", alunno);
        return c;
    }

    public int cancellaOrario(String alunno ) {
        alunno = "\"" + alunno + "\"";


        int res = ourDatabase.delete("orario", " `alunno`=" + alunno, null);
        //db.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
        return res;
    }


    public boolean inserisciOrario(String alunno, ArrayList<Orario_giornaliero> orario ) {
        boolean c = false;
        for (Orario_giornaliero orario_gg : orario) {
            if (!alunno.equals("")) {


                ContentValues contentValues = new ContentValues();
                contentValues.put("alunno", alunno);
                contentValues.put("giorno", orario_gg.getGiorno());
                contentValues.put("ora", orario_gg.getOra());
                contentValues.put("materia", orario_gg.getMateria());
                contentValues.put("insegnante", orario_gg.getInsegnante());


                try {
                    long result = ourDatabase.insert("orario", null, contentValues);
                    if (result == -1) {
                        c = true;
                    } else {
                        c = true;
                    }
                    Log.i("query succesfully", "" + c);
                } catch (Exception e) {
                    c = false;
                    Log.i("query errore", e.toString());
                }
            }
        }

        return c;
    }

// materie

    public Cursor getMaterie(String alunno) {

        alunno = "\"" + alunno + "\"";

        Cursor c = ourDatabase.rawQuery("select * from materie where alunno =" + alunno , null);
        Log.i("getorario..", alunno);
        return c;
    }

    public int deleteMateria(int id) {

        int res = ourDatabase.delete("materie", "id=" + id, null);
        //db.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
        return res;
    }


    public boolean inserisciMaterie(String alunno, String materia, String insegnante ) {
        boolean c = false;
            if (!alunno.equals("")) {


                ContentValues contentValues = new ContentValues();
                contentValues.put("alunno", alunno);
                contentValues.put("materia", materia);
                contentValues.put("insegnante", insegnante);


                try {
                    long result = ourDatabase.insert("materie", null, contentValues);
                    if (result == -1) {
                        c = true;
                    } else {
                        c = true;
                    }
                    Log.i(TAG_LOG,"query succesfully"+ c);
                } catch (Exception e) {
                    c = false;
                    Log.i( TAG_LOG,"query errore.."+ e.toString());
                }

        }

        return c;
    }

    // comunicazioni

    public Cursor getComunicazioni(String alunno) {

        alunno = "\"" + alunno + "\"";

        Cursor c = ourDatabase.rawQuery("select * from comunicazioni where alunno =" + alunno , null);
        Log.i("getcomunicazioni..", alunno);
        return c;
    }

    public Cursor get_Ultima_Comunicazioni(String alunno) {

        alunno = "\"" + alunno + "\"";

        Cursor c = ourDatabase.rawQuery("select * from comunicazioni where alunno =" + alunno+" limit 1" , null);
        Log.i("getcomunicazioni..", alunno);
        return c;
    }

    public int deleteComunicazioni(String alunno) {

        alunno = "\"" + alunno + "\"";

        int res = ourDatabase.delete("comunicazioni", "alunno=" + alunno, null);
        //db.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
        return res;
    }


    public boolean inserisciComunicazioni(ArrayList<Comunicazione> comunicazioni) {
            boolean c = false;
            for (Comunicazione comunicazione : comunicazioni) {
                if (!comunicazione.getAlunno().equals("")) {

                    Log.i(TAG_LOG, ""+comunicazione.getData());
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("alunno", comunicazione.getAlunno());
                    contentValues.put("data", comunicazione.getData());
                    contentValues.put("testo", comunicazione.getTesto());


                    try {
                        long result = ourDatabase.insert("comunicazioni", null, contentValues);
                        if (result == -1) {
                            c = true;
                        } else {
                            c = true;
                        }
                        Log.i("query succesfully", "" + c);
                    } catch (Exception e) {
                        c = false;
                        Log.i("query errore", e.toString());
                    }
                }
            }

            return c;
        }


    }
