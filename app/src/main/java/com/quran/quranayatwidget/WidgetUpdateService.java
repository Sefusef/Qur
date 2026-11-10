package com.quran.quranayatwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.quran.quranayatwidget.QuranDataHelper.Ayat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WidgetUpdateService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWidget();
        stopSelf();
        return START_NOT_STICKY;
    }

    private void updateWidget() {
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("QuranAyatPrefs", Context.MODE_PRIVATE);
        int currentAyatIndex = prefs.getInt("current_ayat_index", 1);

        // Load content
        Ayat data = QuranDataHelper.getAyatByIndex(currentAyatIndex);
        
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, QuranAyatWidgetProvider.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quran_ayat);

        // Update Time
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime());
        views.setTextViewText(R.id.widget_time, time);

        // Update Text Content
        if (data != null) {
            views.setTextViewText(R.id.ayat_arabic, data.arabicText);
            views.setTextViewText(R.id.ayat_translation, data.translation);
            views.setTextViewText(R.id.ayat_reference, data.reference);
        }

        appWidgetManager.updateAppWidget(thisWidget, views);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}