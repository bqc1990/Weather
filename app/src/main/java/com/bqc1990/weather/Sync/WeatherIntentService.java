package com.bqc1990.weather.Sync;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class WeatherIntentService extends IntentService {

    private static final String NAME = "weather_intent_service";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name Used to name the worker thread, important only for debugging.
     */
    public WeatherIntentService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        WeatherSyncTask.sync(this);
    }
}
