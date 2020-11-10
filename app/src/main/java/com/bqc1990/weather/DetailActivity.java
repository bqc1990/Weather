package com.bqc1990.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bqc1990.weather.Database.WeatherContract;
import com.bqc1990.weather.Helper.WeatherDate;
import com.bqc1990.weather.Helper.WeatherUnit;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView mDetailWeatherIconImageView;
    private TextView mDetailDateTextView;
    private TextView mDetailDescriptionTextView;
    private TextView mDetailHighTextView;
    private TextView mDetailLowTextView;
    private TextView mDetailHumidityTextView;
    private TextView mDetailPressureTextView;
    private TextView mDetailWindSpeedTextView;
    private TextView mDetailSunriseTextView;
    private TextView mDetailSunsetTextView;
    private TextView mDetailPopulationTextView;
    private Uri mUri;
    private int mPosition;

    private static final int DETAIL_LOADER_ID = 22;

    private static String[] DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_TEMP_MAX,
            WeatherContract.WeatherEntry.COLUMN_TEMP_MIN,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_SUNRISE,
            WeatherContract.WeatherEntry.COLUMN_SUNSET,
            WeatherContract.WeatherEntry.COLUMN_TIMEZONE,
            WeatherContract.WeatherEntry.COLUMN_POPULATION
    };

    private int INDEX_DATE = 0;
    private int INDEX_WEATHER_ID = 1;
    private int INDEX_MAX = 2;
    private int INDEX_MIN = 3;
    private int INDEX_HUMIDITY = 4;
    private int INDEX_PRESSURE = 5;
    private int INDEX_WIND_SPEED = 6;
    private int INDEX_SUNRISE = 7;
    private int INDEX_SUNSET = 8;
    private int INDEX_TIMEZONE = 9;
    private int INDEX_POPULATION = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        mDetailDateTextView = (TextView) findViewById(R.id.detail_tv_date);
        mDetailWeatherIconImageView = (ImageView) findViewById(R.id.detail_iv_weather_condition);
        mDetailDescriptionTextView = (TextView) findViewById(R.id.detail_tv_description);
        mDetailHighTextView = (TextView) findViewById(R.id.detail_tv_high);
        mDetailLowTextView = (TextView) findViewById(R.id.detail_tv_low);
        mDetailHumidityTextView = (TextView) findViewById(R.id.detail_tv_humidity);
        mDetailPressureTextView = (TextView) findViewById(R.id.detail_tv_pressure);
        mDetailWindSpeedTextView = (TextView) findViewById(R.id.detail_tv_wind_speed);
        mDetailSunriseTextView = (TextView) findViewById(R.id.detail_tv_sunrise);
        mDetailSunsetTextView = (TextView) findViewById(R.id.detail_tv_sunset);
        mDetailPopulationTextView = (TextView) findViewById(R.id.detail_tv_population);

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        if(intent != null){
           mUri = intent.getData();
           mPosition = intent.getIntExtra("cursor_position", -1);
        }

        getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, mUri, DETAIL_PROJECTION,null,null, WeatherContract.WeatherEntry.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        long date = data.getLong(INDEX_DATE);
        int weather_id = data.getInt(INDEX_WEATHER_ID);
        long sunrise = data.getLong(INDEX_SUNRISE);
        long sunset = data.getLong(INDEX_SUNSET);
        long timezone = data.getLong(INDEX_TIMEZONE);
        long population = data.getLong(INDEX_POPULATION);
        double max = data.getDouble(INDEX_MAX);
        double min = data.getDouble(INDEX_MIN);
        double humidity = data.getDouble(INDEX_HUMIDITY);
        double pressure = data.getDouble(INDEX_PRESSURE);
        double wind_speed = data.getDouble(INDEX_WIND_SPEED);
        String description = WeatherUnit.getStringForWeatherCondition(this, weather_id);
        int image_id = WeatherUnit.getLargeArtResourceIdForWeatherCondition(weather_id);
        String dateString = WeatherDate.dateString(date, mPosition, getApplicationContext());
        mDetailDateTextView.setText(dateString);
        mDetailWeatherIconImageView.setImageResource(image_id);
        mDetailDescriptionTextView.setText(description);
        mDetailHighTextView.setText(String.valueOf((int) max));
        mDetailLowTextView.setText(String.valueOf((int) min));
        mDetailHumidityTextView.setText(WeatherUnit.getFormattedHumidity(humidity));
        mDetailPressureTextView.setText(WeatherUnit.getFormattedPressure(pressure));
        String unit = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.pref_unit_key),this.getString(R.string.pref_unit_imperial_value));
        mDetailWindSpeedTextView.setText(WeatherUnit.getFormattedSpeed(wind_speed, unit));
        mDetailSunriseTextView.setText(WeatherDate.displaySunriseSunsetInLocalTime(sunrise, timezone));
        mDetailSunsetTextView.setText(WeatherDate.displaySunriseSunsetInLocalTime(sunset, timezone));
        mDetailPopulationTextView.setText(String.valueOf(population));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}