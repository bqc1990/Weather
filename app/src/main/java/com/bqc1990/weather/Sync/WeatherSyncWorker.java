package com.bqc1990.weather.Sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WeatherSyncWorker extends Worker {
    public WeatherSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        WeatherSyncTask.sync(getApplicationContext());
        Log.v("Jason", "Worker worked");
        return Result.success();
    }
}
