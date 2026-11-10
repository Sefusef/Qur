package com.quran.quranayatwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

public class QuranAyatWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_TICK_ACTION = "com.quran.quranayatwidget.WIDGET_TICK";
    private static final int REQ_CODE = 101;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        startOneMinuteUpdater(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        
        if (WIDGET_TICK_ACTION.equals(intent.getAction())) {
            // Load data to get the correct TOTAL_AYATS_COUNT
            QuranDataHelper.loadJsonData(context);

            SharedPreferences prefs = context.getSharedPreferences("QuranAyatPrefs", Context.MODE_PRIVATE);
            int currentIndex = prefs.getInt("current_ayat_index", 1);
            
            // Move to the next Ayat
            int nextIndex = QuranDataHelper.getNextAyatIndex(currentIndex);
            prefs.edit().putInt("current_ayat_index", nextIndex).apply();

            // Refresh the Widget UI
            context.startService(new Intent(context, WidgetUpdateService.class));
            
            // Schedule the next minute
            startOneMinuteUpdater(context);
        }
    }

    public static void startOneMinuteUpdater(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, QuranAyatWidgetProvider.class).setAction(WIDGET_TICK_ACTION);
        
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        PendingIntent pi = PendingIntent.getBroadcast(context, REQ_CODE, intent, flags);

        long nextTick = (System.currentTimeMillis() / 60000 + 1) * 60000;

        if (am != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTick, pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, nextTick, pi);
            }
        }
    }
}