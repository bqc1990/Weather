package com.bqc1990.weather.Sync;

import android.accounts.NetworkErrorException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.bqc1990.weather.Database.WeatherContract;
import com.bqc1990.weather.Helper.WeatherNetwork;
import com.bqc1990.weather.Helper.WeatherNotification;
import com.bqc1990.weather.Helper.WeatherPreference;
import java.net.URL;

public class WeatherSyncTask {

    public static void sync(Context context) {
        String preferredLocation = WeatherPreference.getPreferredLocation(context);
        try{
            URL url = WeatherNetwork.buildURL(preferredLocation);
            String jsonData = WeatherNetwork.getResponseFromHttp(url);
            ContentValues[] values = WeatherNetwork.covertJsonToContentValues(context, jsonData);
            if(values == null || values.length == 0) return;
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.delete(WeatherContract.WeatherEntry.CONTENT_URI, null,null);
            contentResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, values);

            if(WeatherPreference.sendNotification(context)&&WeatherPreference.notificationEnabled(context)){
                WeatherNotification.sendNotification(context);
                WeatherPreference.saveLastTimeForNotification(context, System.currentTimeMillis());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
