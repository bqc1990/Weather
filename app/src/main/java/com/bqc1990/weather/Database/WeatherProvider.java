package com.bqc1990.weather.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bqc1990.weather.Helper.WeatherDate;

public class WeatherProvider extends ContentProvider {

    private WeatherDBHelper mDBHelper;
    private UriMatcher mUriMatcher = buildUriMatcher();
    private static final int CODE_WEATHER = 100;
    private static final int CODE_WEATHER_DATE = 101;

    private UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.CONTENT_PATH, CODE_WEATHER);
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.CONTENT_PATH+"/#", CODE_WEATHER_DATE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new WeatherDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDBHelper.getReadableDatabase();
        int match = mUriMatcher.match(uri);
        Cursor cursor = null;
        switch (match){
            case CODE_WEATHER:
                cursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME,projection,selection,selectionArgs,null, null, sortOrder);
                break;
            case CODE_WEATHER_DATE:
                String date = uri.getLastPathSegment();
                selection = WeatherContract.WeatherEntry.COLUMN_DATE+ "=?";
                selectionArgs = new String[]{date};
                cursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        switch (match){
            case CODE_WEATHER:
                int rowInserted = 0;
                db.beginTransaction();
                try{
                    for (ContentValues val : values){
                        long date = val.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
                        if(!WeatherDate.isNormalized(date)) throw new IllegalArgumentException("Date must be normalized to insert.");
                        long id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, val);
                        if(id != -1) rowInserted++;
                    }
                     db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                if(rowInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowInserted;
            default:
                return super.bulkInsert(uri, values);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
            final SQLiteDatabase db = mDBHelper.getWritableDatabase();
            int match = mUriMatcher.match(uri);
            if (null == selection) selection = "1";
            int rowDeleted = 0;
            switch (match){
                case CODE_WEATHER:
                    rowDeleted = db.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown Uri: "+uri);
            }

        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
