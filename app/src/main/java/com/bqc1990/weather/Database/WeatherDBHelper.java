package com.bqc1990.weather.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class WeatherDBHelper extends SQLiteOpenHelper {
    private static final String NAME = "weather.db";
    private static final int VERSION = 1;
    public WeatherDBHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE_WEATHER = "CREATE TABLE "+ WeatherContract.WeatherEntry.TABLE_NAME+ " ("
                + WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY, "
                + WeatherContract.WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_SUNRISE + " INTEGER NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_SUNSET + " INTEGER NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_TIMEZONE + " INTEGER NOT NULL,"
                + WeatherContract.WeatherEntry.COLUMN_POPULATION + " INTEGER NOT NULL,"
                + WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_TEMP_MAX + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_TEMP_MIN + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_WIND_SPEED+ " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, "
                + "UNIQUE ( "+ WeatherContract.WeatherEntry.COLUMN_DATE + " ) ON CONFLICT REPLACE"
                +" );";
        db.execSQL(SQL_CREATE_TABLE_WEATHER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
