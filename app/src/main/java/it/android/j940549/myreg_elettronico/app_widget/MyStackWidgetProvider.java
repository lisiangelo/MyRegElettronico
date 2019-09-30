package it.android.j940549.myreg_elettronico.app_widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import it.android.j940549.myreg_elettronico.Home_Activity;
import it.android.j940549.myreg_elettronico.R;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.model.ConvertiData;

public class MyStackWidgetProvider extends AppWidgetProvider {
    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";
    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";
    private String TAG_LOG = "myStackProvider";


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        //Toast.makeText(context, "intent ricevuto ", Toast.LENGTH_SHORT).show();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
              Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();

            Intent intentx = new Intent(context, Home_Activity.class);
            context.startActivity(intentx);
        }

        if (intent.getAction().equals(ACTION_AUTO_UPDATE)) {

            ComponentName thisWidget = new ComponentName(context.getApplicationContext(), getClass().getName());
            //Toast.makeText(context, "appWidgetIds[i]" + appWidgetManager.getAppWidgetIds(thisWidget).length, Toast.LENGTH_SHORT).show();
            Log.i(TAG_LOG, "appWidgetIds[i]" + appWidgetManager.getAppWidgetIds(thisWidget).length);
            //       appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(thisWidget),appWidgetManager.getAppWidgetIds(thisWidget)[0]);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            for (int i = 0; i < appWidgetIds.length; ++i) {


/*            Log.i(TAG_LOG, "update " + appWidgetIds.length);

            Toast.makeText(context, "ACTION_AUTO_UPDATE" + appWidgetIds[i], Toast.LENGTH_SHORT).show();
*/
                Log.i(TAG_LOG, "update " + appWidgetIds[i]);
                // Here we setup the intent which points to the StackViewService which will
                // provide the views for this collection.
                Intent intentx = new Intent(context, MyStackWidgetService.class);
                intentx.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                // When intents are compared, the extras are ignored, so we need to embed the extras
                // into the data so that the extras will not be ignored.
                intentx.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                intentx.putExtra("data", ( new SimpleDateFormat( "dd-MM-yyyy' 'HH:mm:ss" ) ).format( Calendar.getInstance().getTime() ));

                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.my_widget_layout);
                rv.setRemoteAdapter(R.id.stack_view, intentx);
                // The empty view is displayed when the collection has no items. It should be a sibling
                // of the collection view.
                rv.setEmptyView(R.id.stack_view, R.id.empty_view);
                // Here we setup the a pending intent template. Individuals items of a collection
                // cannot setup their own pending intents, instead, the collection as a whole can
                // setup a pending intent template, and the individual items can set a fillInIntent
                // to create unique before on an item to item basis.
                Intent toastIntent = new Intent(context, MyStackWidgetProvider.class);
                toastIntent.setAction(MyStackWidgetProvider.TOAST_ACTION);
                toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                toastIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);
                appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, appWidgetIds[i]);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // update each of the widgets with the remote adapter

        for (int i = 0; i < appWidgetIds.length; ++i) {
/*            Log.i(TAG_LOG, "update " + appWidgetIds.length);

            Toast.makeText(context, "ACTION_AUTO_UPDATE" + appWidgetIds[i], Toast.LENGTH_SHORT).show();
*/
            Log.i(TAG_LOG, "update " + appWidgetIds[i]);
            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            Intent intent = new Intent(context, MyStackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.my_widget_layout);
            rv.setRemoteAdapter(R.id.stack_view, intent);
            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.stack_view, R.id.empty_view);
            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
            Intent toastIntent = new Intent(context, MyStackWidgetProvider.class);
            toastIntent.setAction(MyStackWidgetProvider.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }


}
