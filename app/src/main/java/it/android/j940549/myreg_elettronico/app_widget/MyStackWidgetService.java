package it.android.j940549.myreg_elettronico.app_widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.model.ConvertiData;

public class MyStackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private String TAG_LOG = "MyStacKWidgetFactory";
    private ArrayList<WidgetItem> mWidgetItems = new ArrayList<>();
    private ArrayList<Alunno> alunni = new ArrayList<>();
    private Context mContext;
    private int mAppWidgetId;
    private String annosc, quadrimestre;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        setAnnosc_quadr();

        caricaDatiAlunni();
        Log.i(TAG_LOG,"alunni.size()->"+alunni.size());
        for (int i = 0; i < alunni.size() ; i++) {
            WidgetItem widgetItem = new WidgetItem();
            widgetItem.setNomealunno(alunni.get(i).getNome_alunno());
            caricaUltimoVoto(widgetItem, alunni.get(i).getCod_alunno(), annosc, quadrimestre);
            caricaUtimaAssenza(widgetItem, alunni.get(i).getCod_alunno(), annosc, quadrimestre);
            mWidgetItems.add(widgetItem);
        }
        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the StackWidgetProvider and should be a sibling of the
        // collection view.
        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mWidgetItems.clear();
    }

    public int getCount() {
        return mWidgetItems.size();
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews rviews = new RemoteViews(mContext.getPackageName(), R.layout.my_widget_item);
        rviews.setTextViewText(R.id.alunno_widget,mWidgetItems.get(position).getNomealunno());
        rviews.setTextViewText(R.id.voto_widget, mWidgetItems.get(position).getVoto());
        rviews.setTextViewText(R.id.data_voto_widget, mWidgetItems.get(position).getDatavoto());
        rviews.setTextViewText(R.id.materia_widget, mWidgetItems.get(position).getMateria());
        String voto=mWidgetItems.get(position).getVoto();
        Log.i(TAG_LOG,"voto--"+voto);
        if(!voto.equals("0 voti")) {
            if (Float.parseFloat(voto) < 6) {
                rviews.setTextColor(R.id.voto_widget, Color.RED);
                rviews.setTextColor(R.id.data_voto_widget, Color.RED);
                rviews.setTextColor(R.id.materia_widget, Color.RED);

            } else if (Float.parseFloat(voto) >= 6 && Float.parseFloat(voto) < 7) {
                rviews.setTextColor(R.id.voto_widget, Color.rgb(255, 165, 0));
                rviews.setTextColor(R.id.data_voto_widget, Color.rgb(255, 165, 0));
                rviews.setTextColor(R.id.materia_widget, Color.rgb(255, 165, 0));

            } else if (Float.parseFloat(voto) >= 7 && Float.parseFloat(voto) < 8) {
                rviews.setTextColor(R.id.voto_widget, Color.YELLOW);
                rviews.setTextColor(R.id.data_voto_widget, Color.YELLOW);
                rviews.setTextColor(R.id.materia_widget, Color.YELLOW);

            } else if (Float.parseFloat(voto) >= 8) {
                rviews.setTextColor(R.id.voto_widget, Color.GREEN);
                rviews.setTextColor(R.id.data_voto_widget, Color.GREEN);
                rviews.setTextColor(R.id.materia_widget, Color.GREEN);

            }

        }
        rviews.setTextViewText(R.id.data_assenza_widget, mWidgetItems.get(position).getDataAss());
        rviews.setTextViewText(R.id.tipo_assenza_widget, mWidgetItems.get(position).getTipoAssenza());
       String giust=mWidgetItems.get(position).getGiustifica();
         if(!mWidgetItems.get(position).getDataAss().equals("0 assenze")) {
             if (giust.contains("Si")) {
                 rviews.setTextColor(R.id.data_assenza_widget, Color.GREEN);
                 rviews.setTextColor(R.id.tipo_assenza_widget, Color.GREEN);
                 rviews.setTextViewText(R.id.da_giustificare,"giustificata");
                 rviews.setTextColor(R.id.da_giustificare,Color.GREEN);

             } else {
                 rviews.setTextColor(R.id.data_assenza_widget, Color.RED);
                 rviews.setTextColor(R.id.tipo_assenza_widget, Color.RED);
                 rviews.setTextViewText(R.id.da_giustificare,"da giustificare");
                 rviews.setTextColor(R.id.da_giustificare,Color.RED);

             }
         }
        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(MyStackWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent= new Intent();
        fillInIntent.putExtras(extras);
        rviews.setOnClickFillInIntent(R.id.widget_card, fillInIntent);
        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.
        try {
            System.out.println("Loading view " + position);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Return the remote views object.
        return rviews;
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
    }

    public void caricaUltimoVoto(WidgetItem widgetItem, String alunno, String annosc, String quadrimestre) {
        Log.i(TAG_LOG, "arumets " + alunno + ", " + annosc + ", " + quadrimestre);

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(mContext);
            dbLayer.open();
            Cursor cursor = dbLayer.getUltimo_voto(alunno, annosc, quadrimestre);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG, "cursor: " + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {

                    widgetItem.setMateria(cursor.getString(5));//+"   "+ cursor.getDouble(7)+ "   "+cursor.getString(6));
                    widgetItem.setVoto("" + cursor.getDouble(7));
                    widgetItem.setDatavoto(new ConvertiData().da_Millis_a_String(cursor.getInt(4)));

                } while (cursor.moveToNext());
            }else{
                widgetItem.setVoto("0 voti" );

            }
        } catch (SQLException ex) {
            //Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();
    }

//carica ultima assenza

    public void caricaUtimaAssenza(WidgetItem widgetItem, String alunno, String annosc, String quadrimestre) {
        Log.i(TAG_LOG, "arumets " + alunno + ", " + annosc + ", " + quadrimestre);

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(mContext);
            dbLayer.open();
            Cursor cursor = dbLayer.getUltima_assenza(alunno, annosc);
            //             Cursor cursor = dbLayer.getAllVoti();
            Log.i(TAG_LOG, "cursor: " + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {

                    String data = new ConvertiData().da_Millis_a_String(cursor.getInt(3));
                    widgetItem.setDataAss(data);
                    String tipoassenza = cursor.getString(4);
                    widgetItem.setTipoAssenza(tipoassenza);

                    widgetItem.setGiustifica(cursor.getString(5));

                } while (cursor.moveToNext());
            }else{
                widgetItem.setDataAss("0 assenze" );

            }
        } catch (SQLException ex) {
            //Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();
    }

    public void caricaDatiAlunni() {

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(mContext);
            dbLayer.open();
            Cursor cursor = dbLayer.getAllAlunni();

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    Alunno dataObject_alunno = new Alunno();
                    dataObject_alunno.setId_alunno(cursor.getString(0));
                    dataObject_alunno.setCfIstituto(cursor.getString(1));
                    dataObject_alunno.setTipo(cursor.getString(2));
                    dataObject_alunno.setNome_istituto(cursor.getString(3));
                    dataObject_alunno.setIndirizzo(cursor.getString(4));
                    dataObject_alunno.setNome_alunno(cursor.getString(5));
                    dataObject_alunno.setCod_alunno(cursor.getString(6));
                    dataObject_alunno.setClasse_sez(cursor.getString(7));
                    dataObject_alunno.setUserid(cursor.getString(8));
                    dataObject_alunno.setPassword(cursor.getString(9));
                    Log.i(TAG_LOG, dataObject_alunno.getNome_alunno() + "--" + dataObject_alunno.getCod_alunno() + "..." +
                            dataObject_alunno.getNome_istituto() + "\n"
                            + dataObject_alunno.getPassword() + "---" + dataObject_alunno.getUserid());
                    alunni.add(dataObject_alunno);
                } while (cursor.moveToNext());
            } else {
                //vai_agg_alunno();
            }
        } catch (SQLException ex) {
            //Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();


    }

    public void setAnnosc_quadr() {
        GregorianCalendar ddate = new GregorianCalendar();

        int mese = ddate.get(GregorianCalendar.MONTH) + 1;
        int anno = ddate.get((GregorianCalendar.YEAR));
        Log.i(TAG_LOG, "" + mese + "---" + anno);
        if (mese > 1 && mese < 8) {
            quadrimestre = "2";

        } else {
            quadrimestre = "1";
        }
        if (mese > 8) {
            annosc = "" + anno + "/" + (anno + 1);
        } else {
            annosc = "" + (anno - 1) + "/" + anno;
        }

    }
}