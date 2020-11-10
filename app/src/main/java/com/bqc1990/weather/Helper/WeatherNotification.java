package com.bqc1990.weather.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.bqc1990.weather.Database.WeatherContract;
import com.bqc1990.weather.MainActivity;
import com.bqc1990.weather.R;

public class WeatherNotification {

    private static final int NOTIFICATION_PENDING_INTENT_ID = 24;
    private static final String NOTIFICATION_CHANNEL_ID = "notification_channel_id";
    private static final String NOTIFICATION_CHANNEL_NAME = "notification_channel_name";
    private static final int NOTIFICATION_ID = 23;
    private static String[] NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_TEMP_MAX,
            WeatherContract.WeatherEntry.COLUMN_TEMP_MIN
    };

    private static final int INDEX_DATE = 0;
    private static final int INDEX_WEATHER_ID = 1;
    private static final int INDEX_MAX = 2;
    private static final int INDEX_MIN = 3;

    public static void sendNotification(Context context){
        long dateForToday = WeatherDate.getNormalizedUTCDateForToday();
        Cursor cursor = context.getContentResolver().query(buildUri(dateForToday),NOTIFICATION_PROJECTION,null,null, WeatherContract.WeatherEntry.COLUMN_DATE);
        cursor.moveToFirst();
        long date = cursor.getLong(INDEX_DATE);
        int weather_id = cursor.getInt(INDEX_WEATHER_ID);
        String description = WeatherUnit.getStringForWeatherCondition(context, weather_id);
        double max = cursor.getDouble(INDEX_MAX);
        double min = cursor.getDouble(INDEX_MIN);
        String text = buildString(date, description, max, min);
        String title = context.getString(R.string.forecast);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(largeIcon(context))
                .setSmallIcon(R.drawable.ic_weather_white_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(contentIntent(context))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());

    }

    private static String buildString(long date, String description, double max, double min){
        String localTimeString = WeatherDate.getLocalTime(date);
        return localTimeString + "-"+description+"-"+(int)max+"/"+(int)min;
    }

    private static Uri buildUri(long date){
        return WeatherContract.WeatherEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(date)).build();
    }

    private static PendingIntent contentIntent(Context context){
        Intent intent_startMainActivity = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, NOTIFICATION_PENDING_INTENT_ID,intent_startMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_weather_white_foreground);
        return largeIcon;
    }

}
