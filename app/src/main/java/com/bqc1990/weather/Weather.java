package com.bqc1990.weather;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.bqc1990.weather.Database.WeatherContract;
import com.bqc1990.weather.Helper.WeatherPreference;
import com.bqc1990.weather.Sync.WeatherIntentService;
import com.bqc1990.weather.Sync.WeatherSyncWorker;

import java.util.concurrent.TimeUnit;

public class Weather {
    private static boolean sInitialized;
    private static final int SYNC_WEATHER_MINUTES = 180;


    public static void startImmediately(Context context){
        final Intent intent = new Intent(context, WeatherIntentService.class);
        context.startService(intent);
    }

    private static void startWorker(Context context){
        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(WeatherSyncWorker.class, SYNC_WEATHER_MINUTES, TimeUnit.MINUTES)
                .build();
        WorkManager
                .getInstance(context)
                .enqueue(saveRequest);
    }

    synchronized public static void initialize(@NonNull final Context context) {

        if(!WeatherPreference.autoRefresh(context) || sInitialized) return;

        sInitialized = true;


        startWorker(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;

                String[] projectionColumns = {WeatherContract.WeatherEntry._ID};


                Cursor cursor = context.getContentResolver().query(
                        forecastQueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                if (null == cursor || cursor.getCount() == 0) {
                    startImmediately(context);
                }

                cursor.close();
            }
        });

        checkForEmpty.start();
    }

}
