package com.bqc1990.weather.Helper;

import android.content.Context;

import com.bqc1990.weather.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WeatherDate {

    public static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);
    private static final int POSITION_TODAY = 0;
    private static final int POSITION_TOMORROW = 1;

    public static long getNormalizedUTCDateForToday() {
        long utcNowTimeMillis = System.currentTimeMillis();
        TimeZone currentTimeZone = TimeZone.getDefault();
        long gmtOffsetMillis = currentTimeZone.getOffset(utcNowTimeMillis);
        long localTimeNowMills = utcNowTimeMillis + gmtOffsetMillis;
        long days = TimeUnit.MILLISECONDS.toDays(localTimeNowMills);
        long normalizedUtcMidnightMillis = TimeUnit.DAYS.toMillis(days);
        return normalizedUtcMidnightMillis;
    }

    public static boolean isNormalized(long date) {
        return date % DAY_IN_MILLIS == 0;
    }

    /**
     * covert UTC date in million seconds to date string display in the UI
     *
     * @param utcMidnightTimeMillis UTC date stored in database
     * @return local time as String, for example Tuesday, June 9
     */

    public static String dateString(long utcMidnightTimeMillis, int position, Context context) {
        TimeZone currentTimeZone = TimeZone.getDefault();
        long gmtOffsetMillis = currentTimeZone.getOffset(utcMidnightTimeMillis);
        long localTimeNowMills = utcMidnightTimeMillis - gmtOffsetMillis;
        String dateString = null;
        try {


            if (position == POSITION_TODAY) {
                Date date = new Date(localTimeNowMills);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
                dateString = context.getString(R.string.today) + "," + dateFormat.format(date);
            } else if (position == POSITION_TOMORROW) {
                Date date = new Date(localTimeNowMills);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
                dateString = context.getString(R.string.tomorrow) + "," + dateFormat.format(date);

            } else {

                Date date = new Date(localTimeNowMills);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE,MM/dd");
                dateString = dateFormat.format(date);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String displaySunriseSunsetInLocalTime(long timeInSeconds, long timeZoneInSecondsFromUtc) {
        long timeZoneInMillis = TimeUnit.SECONDS.toMillis(timeZoneInSecondsFromUtc);
        Date date = new Date(TimeUnit.SECONDS.toMillis(timeInSeconds));
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String[] timezoneIds = TimeZone.getAvailableIDs((int) timeZoneInMillis);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezoneIds[0]));
        String dateString = dateFormat.format(date);
        return dateString;

    }

    public static String getLocalTime(long timeInMillis) {
        Date date = new Date(timeInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");

        dateFormat.setTimeZone(TimeZone.getDefault());
        String dateString = dateFormat.format(date);
        return dateString;
    }
}
