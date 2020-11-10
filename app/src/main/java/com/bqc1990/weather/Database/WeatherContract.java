package com.bqc1990.weather.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.bqc1990.weather";
    public static final String CONTENT_PATH = "weather";

    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY)
                .buildUpon()
                .appendEncodedPath(CONTENT_PATH)
                .build();

        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_TEMP_MAX = "max";
        public static final String COLUMN_TEMP_MIN = "min";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_SUNRISE = "sunrise";
        public static final String COLUMN_SUNSET = "sunset";
        public static final String COLUMN_WIND_SPEED = "speed";
        public static final String COLUMN_TIMEZONE = "timezone";
        public static final String COLUMN_POPULATION = "population";
    }

}
