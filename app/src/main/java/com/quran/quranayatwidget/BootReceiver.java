package com.quran.quranayatwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("QuranWidget", "Boot detected. Re-scheduling widget updates.");
            
            // This initializes the 1-minute loop again
            QuranAyatWidgetProvider.startOneMinuteUpdater(context);
        }
    }
}