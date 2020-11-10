package com.bqc1990.weather.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.bqc1990.weather.R;
import java.util.concurrent.TimeUnit;

public class WeatherPreference {

    private static final String LASTTIMESAVED = "last_time_saved";

    public static String getPreferredLocation(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_location_key);
        String valueDefault = context.getString(R.string.pref_location_default);
        String preferredLocation = sharedPreferences.getString(key, valueDefault);
        return preferredLocation;
    }

    public static String getPreferredUnit(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.pref_unit_key);
        String valueDefault = context.getString(R.string.pref_unit_imperial_value);
        String preferredUnit= sharedPreferences.getString(key, valueDefault);
        return preferredUnit;
    }

    public static boolean autoRefresh(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_auto_key),context.getResources().getBoolean(R.bool.pref_auto_default));
    }

    public static void saveLastTimeForNotification(Context context, long timestamp){
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LASTTIMESAVED, timestamp);
        editor.apply();
    }

    public static boolean sendNotification(Context context){
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        long timeSaved = sharedPreferences.getLong(LASTTIMESAVED, 0);
        long timeNow = System.currentTimeMillis();
        if(TimeUnit.MILLISECONDS.toHours(timeNow) - TimeUnit.MILLISECONDS.toHours(timeSaved) >= 12) return true;
        return false;
    }

    public static boolean notificationEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_notification_key),context.getResources().getBoolean(R.bool.pref_notification_default));
    }
}
